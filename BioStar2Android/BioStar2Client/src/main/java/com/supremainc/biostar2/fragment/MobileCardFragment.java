/*
 * Copyright 2015 Suprema(biostar2@suprema.co.kr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.supremainc.biostar2.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.AppDataProvider;
import com.supremainc.biostar2.provider.MobileCardDataProvider;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCard;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCardRaw;
import com.supremainc.biostar2.sdk.models.v2.card.MobileCards;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.service.ble.BluetoothLeServiceManager;
import com.supremainc.biostar2.view.MobileCardView;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.supremainc.biostar2.R.string.invalid_card;
import static com.supremainc.biostar2.meta.Setting.BROADCAST_BLE_ERROR_RESULT;

public class MobileCardFragment extends BaseFragment {
    private MobileCardView mMobileCardView;
    private User mUser;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mBleBroadcastReceiver;
    private View mBackground;
    private StyledTextView mGuideText;
    private MobileCard mServerCard;
    private boolean mIsExistCard;
    private LottieAnimationView mAnimationView;
    private ImageView mMobileCardImageView;
    private ImageView mResultView;
    private FrameLayout mBackGroundView;

    private Callback<MobileCardRaw> mRegisterListener = new Callback<MobileCardRaw>() {
        @Override
        public void onFailure(Call<MobileCardRaw> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), true);
        }

        @Override
        public void onResponse(Call<MobileCardRaw> call, Response<MobileCardRaw> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, true)) {
                return;
            }
            setExist(false);
            boolean result = mMobileCardDataProvider.setCard(response.body().smart_card_layout_primary_key, response.body().raw, mActivity);
            if (!result) {
                showErrorPopup(getString(R.string.fail) + "\n" + getString(R.string.invalid_card), true);
                return;
            }
            mServerCard.is_registered = true;
            mBackground.setOnClickListener(null);
            mBackground.setBackgroundColor(getResources().getColor(R.color.transparent));
            mGuideText.setVisibility(View.GONE);
            MobileCardDataProvider.CARD_VERIFY verify = mMobileCardDataProvider.Verify(mActivity);

            if (verify != MobileCardDataProvider.CARD_VERIFY.VALID) {
                mBackground.setBackgroundColor(getResources().getColor(R.color.transparent80));
                mGuideText.setVisibility(View.VISIBLE);
                mGuideText.setText(getString(invalid_card));
                showErrorPopup(getString(R.string.invalid_card), true);
                return;
            }
            setExist(true);
            animBleScan();
            mPopup.show(Popup.PopupType.INFO, getString(R.string.info), getString(R.string.register_mobile_card), null, getString(R.string.ok), null, false);
        }
    };

    private OnSingleClickListener mOnSingleClickListener = new OnSingleClickListener() {
        @Override
        public void onSingleClick(View v) {
            if (mServerCard == null) {
                return;
            }
            if (mServerCard.is_registered) {
                return;
            }
            mPopup.showWait(false);
            mMobileCardDataProvider.registerMobileCard(mActivity, mServerCard.id, mRegisterListener);
        }
    };

    private Callback<MobileCards> mCardsListener = new Callback<MobileCards>() {
        @Override
        public void onFailure(Call<MobileCards> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), true);
        }

        @Override
        public void onResponse(Call<MobileCards> call, Response<MobileCards> response) {
            setExist(false);
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, true)) {
                return;
            }
            if (response.body().records == null || response.body().records.size() < 1) {
                mToastPopup.show(getString(R.string.none_data), null);
                return;
            }

            mServerCard = response.body().records.get(0);
            mMobileCardView.setCard(mServerCard, mUser, mOnSingleClickListener, mMobileCardImageView);

            if (mServerCard.is_registered) {
                mBackground.setOnClickListener(null);
                mBackground.setBackgroundColor(getResources().getColor(R.color.transparent));
                mGuideText.setVisibility(View.GONE);
                MobileCardDataProvider.CARD_VERIFY verify = mMobileCardDataProvider.Verify(mActivity);
                if (verify == MobileCardDataProvider.CARD_VERIFY.NONE || verify == MobileCardDataProvider.CARD_VERIFY.INVALID) {
                    mBackground.setBackgroundColor(getResources().getColor(R.color.transparent80));
                    mGuideText.setVisibility(View.VISIBLE);
                    mGuideText.setText(getString(invalid_card));
                } else {
                    setExist(true);
                    animBleScan();
                }
            } else {
                mBackground.setOnClickListener(mOnSingleClickListener);
                mBackground.setBackgroundColor(getResources().getColor(R.color.transparent80));
                mGuideText.setVisibility(View.VISIBLE);
                mGuideText.setText(getString(R.string.guide_register_mobile_card3));
            }
            mIsDataReceived = true;
        }
    };

    private Runnable mRunAllow = new Runnable() {
        @Override
        public void run() {
            if (mBluetoothAdapter == null) {
                try {
                    BluetoothManager bm = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
                    if (bm != null) {
                        mBluetoothAdapter = bm.getAdapter();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG,""+e.getMessage());
                    }
                }
            }
            setBleOnOff(true);
            if (!mIsStoped) {
                if (BluetoothLeServiceManager.scan(true) == false) {
                    mHandler.removeCallbacks(mRunAllow);
                    mHandler.postDelayed(mRunAllow, 1000);
                } else {
                    animBleScan();
                }
            }
        }
    };
    private Runnable mRunRestart = new Runnable() {
        @Override
        public void run() {
            if (mIsDestroy || mRootView == null) {
                return;
            }
            BluetoothLeServiceManager.start(mActivity);
            mHandler.removeCallbacks(mRunAllow);
            mHandler.post(mRunAllow);
        }
    };
    private Runnable mRunDeny = new Runnable() {
        @Override
        public void run() {
            if (Build.VERSION.SDK_INT >= 23) {
                String permissionLabel = "";
                try {
                    PackageManager pm = mActivity.getPackageManager();
                    PermissionGroupInfo pg = pm.getPermissionGroupInfo(Manifest.permission_group.LOCATION, PackageManager.GET_META_DATA);
                    permissionLabel = pg.loadLabel(pm).toString();
                } catch (Exception e) {

                }
                if (!permissionLabel.isEmpty()) {
                    permissionLabel = "(" + permissionLabel + ")";
                }
                permissionLabel = getString(R.string.guide_feature_permission) + " " + getString(R.string.allow_permission) + permissionLabel;
                Snackbar snackbar = Snackbar
                        .make(mRootView, permissionLabel, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.permission_setting), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                mActivity.startActivity(intent);
                            }
                        });
                //snackbar.setActionTextColor(Color.MAGENTA);
                View snackbarView = snackbar.getView();
                TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                snackbar.show();
            }
        }
    };

    private Runnable mRunnableReScanAni = new Runnable() {
        @Override
        public void run() {
            if (mIsDestroy || mRootView == null) {
                return;
            }
            mResultView.setVisibility(View.INVISIBLE);
            mBackGroundView.setBackgroundColor(getResources().getColor(R.color.main_bg));
            animBleScan();
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsDestroy || mRootView == null) {
                return;
            }
            animScan();
        }
    };
    private Runnable mRunnableStop = new Runnable() {
        @Override
        public void run() {
            if (mIsDestroy || mRootView == null) {
                return;
            }
            animStop();
        }
    };
    public MobileCardFragment() {
        super();
        setType(ScreenType.MOBILE_CARD_LIST);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void setExist(boolean exist) {
        if (isInValidCheck()) {
            return;
        }
        mIsExistCard = exist;
        mActivity.invalidateOptionsMenu();
        BluetoothLeServiceManager.setExist(exist);
    }

    private void initValue() {
        mUser = mUserDataProvider.getLoginUserInfo();
        if (mAnimationView == null) {
            mAnimationView = (LottieAnimationView) mRootView.findViewById(R.id.animation_view);
        }
        if (mBackGroundView == null) {
            mBackGroundView = (FrameLayout)mRootView.findViewById(R.id.main_bg);
        }
        if (mResultView == null) {
            mResultView = (ImageView) mRootView.findViewById(R.id.result);
        }
        if (mMobileCardView == null) {
            mMobileCardView = (MobileCardView) mRootView.findViewById(R.id.mobile_card);
        }
        if (mMobileCardImageView == null) {
            mMobileCardImageView = (ImageView) mRootView.findViewById(R.id.mobile_card_image);
        }
        if (mBackground == null) {
            mBackground = mRootView.findViewById(R.id.background);
        }
        if (mGuideText == null) {
            mGuideText = (StyledTextView) mRootView.findViewById(R.id.guide);
        }


        if (mBluetoothAdapter == null) {
            try {
                BluetoothManager bm = (BluetoothManager) mActivity.getSystemService(Context.BLUETOOTH_SERVICE);
                if (bm != null) {
                    mBluetoothAdapter = bm.getAdapter();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_mobilecard2);
        super.onCreateView(inflater, container, savedInstanceState);

        if (!mIsReUsed) {
            initValue();
            initActionbar(getString(R.string.mobile_card));
            mPopup.showWait(mCancelExitListener);
            mMobileCardDataProvider.getMobileCards(mActivity, mCardsListener);
            mRootView.invalidate();
        }
        if (mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC)) {
            if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
                mPopup.show(Popup.PopupType.CARD, getString(R.string.nfc_not_support), null, getString(R.string.ok), null);
            }
        }
        return mRootView;
    }

    @Override
    public boolean onBack() {
        if (super.onBack()) {
            return true;
        }
        return false;
    }

    private void animBleScan() {
        if (!mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC) && mIsExistCard) {
            if (!BluetoothLeServiceManager.isIdle()) {
                animScan();
            }
        }
    }

    private void setBleOnOff(boolean on) {
        if (mBluetoothAdapter == null) {
            return;
        }
        if (mIsStoped && on) {
            return;
        }
        if (mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC) && on) {
            return;
        }
        int state = mBluetoothAdapter.getState();
        switch (state) {
            case BluetoothAdapter.STATE_ON:
            case BluetoothAdapter.STATE_TURNING_ON:
                if (!on) {
                    mBluetoothAdapter.disable();
                }
                break;
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                if (on) {
                    mBluetoothAdapter.enable();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAllow(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_LOCATION) {
            return;
        }
        mHandler.removeCallbacks(mRunAllow);
        mHandler.postDelayed(mRunAllow, 500);
    }

    @Override
    public void onDeny(int requestCode) {
        if (mHandler == null || requestCode != Setting.REQUEST_LOCATION) {
            return;
        }
        mHandler.removeCallbacks(mRunDeny);
        mHandler.postDelayed(mRunDeny, 500);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "onStart");
        }
        BluetoothLeServiceManager.setRange(mAppDataProvider.getBleRange());
        mHandler.removeCallbacks(mRunRestart);
        if (!mAppDataProvider.getBoolean(AppDataProvider.BooleanType.MOBILE_CARD_NFC)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if ((ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            Setting.REQUEST_LOCATION);
                    BluetoothLeServiceManager.start(mActivity);
                    return;
                }
            }
            setBleOnOff(true);
            mHandler.post(mRunRestart);
        }
    }

    @Override
    public void onStop() {
        mHandler.removeCallbacks(mRunAllow);
        mHandler.removeCallbacks(mRunRestart);
        BluetoothLeServiceManager.stop(mActivity);
        animStop();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unRegisterBroadcast();
        if (mMobileCardView != null) {
            mMobileCardView.relese();
        }
        super.onDestroy();
    }

    @Override
    protected void unRegisterBroadcast() {
        super.unRegisterBroadcast();
        if (mBleBroadcastReceiver != null) {
            mActivity.unregisterReceiver(mBleBroadcastReceiver);
            mBleBroadcastReceiver = null;
        }
    }

    private void animScan() {
        mAnimationView.setVisibility(View.VISIBLE);
        if (!mAnimationView.isAnimating()) {
            mAnimationView.cancelAnimation();
            mAnimationView.setAnimation("scan.json");
            mAnimationView.loop(true);
            mAnimationView.setProgress(0f);
            mAnimationView.playAnimation();
        }
    }

    private void animStop() {
        mAnimationView.cancelAnimation();
        mAnimationView.setVisibility(View.INVISIBLE);
    }

    private void animResult(int resID) {
        Log.e(TAG,"animResult:"+resID);
        animStop();
        mResultView.setImageResource(resID);
        mResultView.setVisibility(View.VISIBLE);
        if (R.drawable.ic_access_success == resID) {
            mBackGroundView.setBackgroundResource(R.drawable.shape_backgorund_sucess);
        } else {
            mBackGroundView.setBackgroundResource(R.drawable.shape_backgorund_fail);
        }
        mHandler.removeCallbacks(mRunnableReScanAni);
        mHandler.postDelayed(mRunnableReScanAni, 3000);
    }

    protected void registerBroadcast() {
        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (isInValidCheck()) {
                        return;
                    }
                    String action = intent.getAction();
                    if (action.equals(Setting.BROADCAST_REROGIN)) {
                        mMobileCardDataProvider.getMobileCards(mActivity, mCardsListener);
                        return;
                    }
                    mHandler.removeCallbacks(mRunnableStop);
                    mHandler.removeCallbacks(mRunnable);
                    if (action.equals(Setting.BROADCAST_BLE_ERROR)) {
                        if (BuildConfig.DEBUG) {
                            Log.e(TAG, "BROADCAST_BLE_ERROR");
                        }
                        mHandler.removeCallbacks(mRunAllow);
                        BluetoothLeServiceManager.stop(mActivity);
                        animStop();
                        mHandler.removeCallbacks(mRunRestart);
                        mHandler.postDelayed(mRunRestart, 2000);
                        return;
                    }

                    if (action.equals(Setting.BROADCAST_BLE_SUCESS)) {
                        animResult(R.drawable.ic_access_success);
                    } else if (action.equals(Setting.BROADCAST_BLE_ERROR_DEVICE)) {
                        animResult(R.drawable.ic_access_fail2);
                    } else if (action.equals(BROADCAST_BLE_ERROR_RESULT)) {
                        animResult(R.drawable.ic_access_fail1);
                    } else if (action.equals(Setting.BROADCAST_BLE_CONNECT) || action.equals(Setting.BROADCAST_BLE_ERROR_CONNECT)) {
                        mHandler.removeCallbacks(mRunnableReScanAni);
                        mHandler.post(mRunnableReScanAni);
                    } else if (action.equals(Setting.BROADCAST_NFC_CONNECT)) {
                        mHandler.postDelayed(mRunnableStop, 2000);
                        animScan();
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Setting.BROADCAST_REROGIN);
            intentFilter.addAction(Setting.BROADCAST_BLE_ERROR);
            intentFilter.addAction(Setting.BROADCAST_BLE_CONNECT);
            intentFilter.addAction(Setting.BROADCAST_BLE_ERROR_DEVICE);
            intentFilter.addAction(Setting.BROADCAST_BLE_ERROR_RESULT);
            intentFilter.addAction(Setting.BROADCAST_BLE_ERROR_CONNECT);
            intentFilter.addAction(Setting.BROADCAST_BLE_SUCESS);
            intentFilter.addAction(Setting.BROADCAST_NFC_CONNECT);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        if (mBleBroadcastReceiver == null) {
            mBleBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    final String action = intent.getAction();
                    if (mIsDestroy || action == null || mIsStoped) {
                        return;
                    }
                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                        switch (state) {
                            case BluetoothAdapter.STATE_CONNECTED:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_CONNECTED:");
                                }
                                break;
                            case BluetoothAdapter.STATE_CONNECTING:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_CONNECTING:");
                                }
                                break;
                            case BluetoothAdapter.STATE_ON:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_ON:");
                                }
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_TURNING_ON:");
                                }
                                break;
                            case BluetoothAdapter.STATE_DISCONNECTED:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_DISCONNECTED:");
                                }
                                break;
                            case BluetoothAdapter.STATE_DISCONNECTING:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_DISCONNECTING:");
                                }
                                break;
                            case BluetoothAdapter.STATE_OFF:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_OFF:");
                                }
                                setBleOnOff(true);
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                if (BuildConfig.DEBUG) {
                                    Log.e(TAG, "STATE_TURNING_OFF:");
                                }
                                break;
                        }
                    } else if (action.equals(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)) {
                        final int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE,
                                NfcAdapter.STATE_OFF);
                        switch (state) {
                            case NfcAdapter.STATE_TURNING_OFF:
                                break;
                            case NfcAdapter.STATE_OFF:
                                break;
                            case NfcAdapter.STATE_ON:
                            case NfcAdapter.STATE_TURNING_ON:
                                break;
                        }
                    }
                }
            };
            mActivity.registerReceiver(mBleBroadcastReceiver, filter);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = mActivity.getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_setting:
                mScreenControl.addScreen(ScreenType.PREFERENCE, null);
                return true;
        }
        return false;
    }
}
