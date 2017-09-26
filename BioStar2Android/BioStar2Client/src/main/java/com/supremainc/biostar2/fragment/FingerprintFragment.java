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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.FingerPrintAdapter;
import com.supremainc.biostar2.datatype.BioMiniTemplate;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.provider.BioMiniDataProvider;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.common.VersionData;
import com.supremainc.biostar2.sdk.models.v2.device.Device;
import com.supremainc.biostar2.sdk.models.v2.device.FingerprintVerify;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.FingerPrints;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.fingerprint.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SeekBarPopup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.supremainc.biostar2.provider.BioMiniDataProvider.UFA_OK;

public class FingerprintFragment extends BaseFragment {
    private static final int MODE_DELETE = 1;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;
    private FingerPrintAdapter mItemAdapter;

    private String mDeviceId;
    private ListFingerprintTemplate mFingerprintTemplate;
    private boolean mIsDisableModify;
    private boolean mIsTwiceInput;
    private int mQuality = 80;
    private int mScanQuality1st = 0;
    private int mScanQuality2nd = 0;
    private int mReplacePosition = -1;
    private SeekBarPopup mSeekBarPopup;
    private BioMiniDataProvider mBioMiniDataProvider;
    private BioMiniTemplate mBioMiniTemplate0;
    private BioMiniTemplate mBioMiniTemplate1;
    private boolean mIsBioMiniRescan;
    private BroadcastReceiver mUsbReceiver;
    private Callback<FingerPrints> mFingerPrintListener = new Callback<FingerPrints>() {
        @Override
        public void onFailure(Call<FingerPrints> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), true);
        }

        @Override
        public void onResponse(Call<FingerPrints> call, Response<FingerPrints> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, true, true)) {
                return;
            }
            mUserInfo.fingerprint_templates = response.body().records;
            mUserInfo.card_count = mUserInfo.fingerprint_templates.size();
            refreshValue();
        }
    };
    private Callback<ResponseStatus> mModifyFingerPrintListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showErrorPopup(t.getMessage(), true);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call, response, false)) {
                return;
            }
            if (isInvalidResponse(response, true, true)) {
                return;
            }
            request(mUserDataProvider.getFingerPrints(mUserInfo.user_id, mFingerPrintListener));
        }
    };
    private Callback<FingerprintVerify> mScanConfirmListener = new Callback<FingerprintVerify>() {
        @Override
        public void onFailure(Call<FingerprintVerify> call, Throwable t) {
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    scanVerify();
                }

                @Override
                public void OnNegative() {
                    clearValue();
                }
            });
        }

        @Override
        public void onResponse(Call<FingerprintVerify> call, Response<FingerprintVerify> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (isInvalidResponse(response, false, false) || !response.body().verify_result) {
                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.fail_verify_finger), new OnPopupClickListener() {
                    @Override
                    public void OnPositive() {
                        clearValue();
                    }

                    @Override
                    public void OnNegative() {
                        clearValue();
                    }
                }, getString(R.string.ok), null, false);
                return;
            }
            sucess();
        }
    };
    
    private SubToolbar.SubToolBarListener mSubToolBarEvent = new SubToolbar.SubToolBarListener() {
        @Override
        public void onClickSelectAll() {
            if (mSubToolbar.showReverseSelectAll()) {
                if (mItemAdapter != null) {
                    mItemAdapter.selectChoices();
                    mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
                }
            } else {
                if (mItemAdapter != null) {
                    mItemAdapter.clearChoices();
                    mSubToolbar.setSelectedCount(0);
                }
            }
        }
    };
    
    private SeekBarPopup.OnResult mOnChangeQualityResult = new SeekBarPopup.OnResult() {
        @Override
        public void OnResult(String data) {
            if (data != null && !data.isEmpty()) {
                mQuality = Integer.valueOf(data);
                scanFinger();
            } else {
                reScan();
            }
        }
    };
    private Callback<ScanFingerprintTemplate> mScanListener = new Callback<ScanFingerprintTemplate>() {
        @Override
        public void onFailure(Call<ScanFingerprintTemplate> call, Throwable t) {
            if (isIgnoreCallback(call, true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    reScan();
                    //scanFinger();
                }

                @Override
                public void OnNegative() {
                    clearValue();
                }
            });
        }

        @Override
        public void onResponse(Call<ScanFingerprintTemplate> call, Response<ScanFingerprintTemplate> response) {
            if (isIgnoreCallback(call, response, true)) {
                return;
            }
            if (!response.isSuccessful() || response.body() == null) {
//                if (response.errorBody() != null) {
//                    try {
//                        ResponseStatus responseClass = (ResponseStatus) mGson.fromJson(response.errorBody().string(), ResponseStatus.class);
//                        if (Device.SCAN_QUALITY_IS_LOW.equals(responseClass.status_code)) {
//                            showRetryPopup(getString(R.string.low_quality), new OnPopupClickListener() {
//                                @Override
//                                public void OnPositive() {
//                                    reScan();
//                                }
//
//                                @Override
//                                public void OnNegative() {
//                                    clearValue();
//                                }
//                            });
//
//                            return;
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
                String error = getResponseErrorMessage(response);
                if (error.contains(Device.SCAN_QUALITY_IS_LOW)) {
                    error = getString(R.string.low_quality);
                }
                onFailure(call, new Throwable(error));
                return;
            }
            int scanQuality = 0;
            if (response.body().enroll_quality != null) {
                scanQuality = Integer.valueOf(response.body().enroll_quality);
            }

            if (mFingerprintTemplate == null) {
                mFingerprintTemplate = new ListFingerprintTemplate();
                mFingerprintTemplate.template0 = response.body().template0;
                mFingerprintTemplate.template1 = response.body().template0;
            }

            if (mIsTwiceInput) {
                mScanQuality2nd = scanQuality;
            } else {
                mScanQuality1st = scanQuality;
            }

            if (mQuality > scanQuality) {
                reScan();
                return;
            }

            if (mIsTwiceInput) {
                mIsTwiceInput = false;
                mFingerprintTemplate.template1 = response.body().template0;
                scanVerify();
            } else {
                mIsTwiceInput = true;
                scanFinger();
            }
        }
    };

    public FingerprintFragment() {
        super();
        setType(ScreenType.FINGERPRINT_REGISTER);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void scanVerify() {
        mHandler.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     mPopup.show(PopupType.FINGERPRINT_CONFIRM, mItemAdapter.getName(mReplacePosition), "1" + getString(R.string.st) + " " + getString(R.string.quality) + " " + mScanQuality1st + "\n" + "2" + getString(R
                                                     .string.nd) + " " + getString(R.string.quality) + " " + mScanQuality2nd + "\n" + getString(R.string
                                                     .verify_finger),
                                             null,
                                             null, null,
                                             false);
                                     request(mDeviceDataProvider.verifyFingerprint(mDeviceId, mFingerprintTemplate, mScanConfirmListener));
                                 }
                             }
                , 1000);
    }

    private void showGuideScanFinger() {
        PopupType type = PopupType.FINGERPRINT;
        String body = "\n" + getString(R.string.finger_on_device);

        if (mIsTwiceInput) {
            type = PopupType.FINGERPRINT_AGAGIN;
            body = "1" + getString(R.string.st) + " " + getString(R.string.quality) + " " + mScanQuality1st + "\n" + getString(R.string.finger_on_device_same);
        }
        mPopup.show(type, mItemAdapter.getName(mReplacePosition), body, null,
                null, null, false);
    }

    private void scanFinger() {
        if (isInValidCheck()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mIsBioMiniRescan) {
                    int result = mBioMiniDataProvider.findDevice();
                    if (result == UFA_OK) {
                        scanBioMini();
                        return;
                    }
                    mIsBioMiniRescan = false;
                    mToastPopup.show(ToastPopup.TYPE_DEFAULT, getString(R.string.portable), BioMiniDataProvider.getMessage(result));
                    return;
                }
                showGuideScanFinger();
                request(mDeviceDataProvider.scanFingerprint(mDeviceId, mQuality, false, mScanListener));
            }
        });
    }

    private void sucess() {
        mPopup.show(PopupType.FINGERPRINT_CONFIRM, mItemAdapter.getName(mReplacePosition), getString(R.string.scan_success), new OnPopupClickListener() {
            @Override
            public void OnPositive() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (BuildConfig.DEBUG) {
                            Log.i(TAG, "mReplacePosition:" + mReplacePosition + " size:" + mUserInfo.fingerprint_templates.size());
                        }
                        if (mReplacePosition == -1 || mReplacePosition >= mUserInfo.fingerprint_templates.size()) {
                            try {
                                mUserInfo.fingerprint_templates.add(mFingerprintTemplate.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                mUserInfo.fingerprint_templates.set(mReplacePosition, mFingerprintTemplate.clone());
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }
                        }
                        refreshValue();
                        if (VersionData.getCloudVersion(mActivity) > 1) {
                            mPopup.showWait(mCancelExitListener);
                            request(mUserDataProvider.modifyFingerPrints(mUserInfo.user_id, mUserInfo.fingerprint_templates, mModifyFingerPrintListener));
                        }
                    }
                });
            }

            @Override
            public void OnNegative() {
                clearValue();
            }
        }, getString(R.string.ok), null, false);
    }

    private void clearValue() {
        mReplacePosition = -1;
        mFingerprintTemplate = null;
        mIsTwiceInput = false;
        mIsBioMiniRescan = false;
        mBioMiniTemplate0 = null;
        mBioMiniTemplate1 = null;
    }

    private void deleteConfirm(int selectedCount) {
        mPopup.show(PopupType.ALERT, getString(R.string.delete_confirm_question), getString(R.string.selected_count) + " " + selectedCount, new OnPopupClickListener() {
            @Override
            public void OnNegative() {
            }

            @Override
            public void OnPositive() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (VersionData.getCloudVersion(mActivity) > 1) {
                            int i = mItemAdapter.getCount() - 1;
                            ArrayList<ListFingerprintTemplate> fingerprintTemplates = (ArrayList<ListFingerprintTemplate>) mUserInfo.fingerprint_templates.clone();
                            for (; i >= 0; i--) {
                                boolean isCheck = mItemAdapter.isItemChecked(i);
                                if (isCheck) {
                                    fingerprintTemplates.remove(i);
                                }
                            }
                            mPopup.showWait(mCancelExitListener);
                            request(mUserDataProvider.modifyFingerPrints(mUserInfo.user_id, fingerprintTemplates, mModifyFingerPrintListener));
                        } else {
                            int i = mItemAdapter.getCount() - 1;
                            for (; i >= 0; i--) {
                                boolean isCheck = mItemAdapter.isItemChecked(i);
                                if (isCheck) {
                                    mUserInfo.fingerprint_templates.remove(i);
                                }
                            }
                            refreshValue();
                        }
                    }
                });
            }
        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        if (mBioMiniDataProvider == null) {
            mBioMiniDataProvider = BioMiniDataProvider.getInstance(mActivity);
        }
        Boolean disable = getExtraData(Setting.DISABLE_MODIFY, savedInstanceState);
        if (disable != null) {
            mIsDisableModify = disable;
        }

        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(mSubToolBarEvent, getActivity());
            mSubToolbar.setVisibleSearch(false, null);
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }
        if (mUserInfo.fingerprint_templates == null) {
            mUserInfo.fingerprint_templates = new ArrayList<ListFingerprintTemplate>();
        }
        if (mSeekBarPopup == null) {
            mSeekBarPopup = new SeekBarPopup(getActivity());
            mSeekBarPopup.setRange(20, 100, 20);
        }
        if (mItemAdapter == null) {
            mItemAdapter = new FingerPrintAdapter(mActivity, mUserInfo.fingerprint_templates, getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        int count = mItemAdapter.getCheckedItemCount();
                        mSubToolbar.setSelectedCount(count);
                        if (count == mItemAdapter.getCount()) {
                            if (!mSubToolbar.getSelectAll()) {
                                mSubToolbar.showReverseSelectAll();
                            }
                        }
                    } else {
                        mReplacePosition = position;
                        showSelectDevice();
                    }
                }
            }, mPopup, null, mIsDisableModify);
        }
        refreshValue();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        if (mUserInfo.fingerprint_templates != null) {
            try {
                mUserInfo.fingerprint_count = mUserInfo.fingerprint_templates.size();
                mUserInfo.fingerprint_template_count = mUserInfo.fingerprint_templates.size();
                sendLocalBroadcast(Setting.BROADCAST_UPDATE_FINGER, (Serializable) mUserInfo.clone());
            } catch (Exception e) {

            }
        }
        if (mItemAdapter != null) {
            mItemAdapter.clearItems();
        }
        if (mUsbReceiver != null) {
            getActivity().unregisterReceiver(mUsbReceiver);
            mUsbReceiver = null;
        }
        int unint = mBioMiniDataProvider.unInitDevice();
        //      Log.e(TAG,"unInit:"+mBioMiniDataProvider.getMessage(unint));
        super.onDestroy();
    }

    @Override
    protected void registerBroadcast() {
        if (mUsbReceiver == null) {
            mUsbReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    mIsBioMiniRescan = false;
                    mBioMiniDataProvider.unInitDevice();
                }
            };
        }
        getActivity().registerReceiver(mUsbReceiver, new IntentFilter("android.hardware.usb.action.USB_DEVICE_DETACHED"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_delete_confirm:
                int selectedCount = mItemAdapter.getCheckedItemCount();
                if (selectedCount < 1) {
                    mToastPopup.show(getString(R.string.selected_none), null);
                    return true;
                }
                deleteConfirm(selectedCount);
                break;
            case R.id.action_add:
                if (mUserInfo.fingerprint_templates.size() >= 10) {
                    mToastPopup.show(getString(R.string.max_size), null);
                    return true;
                }
                mReplacePosition = mUserInfo.fingerprint_templates.size();
                showSelectDevice();
                break;
            case R.id.action_delete:
                setSubMode(MODE_DELETE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onSearch(String query) {
        if (super.onSearch(query)) {
            return true;
        }
        if (mSelectDevicePopup != null && mSelectDevicePopup.isExpand()) {
            mSelectDevicePopup.onSearch(query);
        }
        return true;
    }

    @Override
    protected void setSubMode(int mode) {
        mSubMode = mode;
        switch (mode) {
            case MODE_NORMAL:
                mItemAdapter.setChoiceMode(ListView.CHOICE_MODE_NONE);
                mSubToolbar.showMultipleSelectInfo(false, 0);
                break;
            case MODE_DELETE:
                mItemAdapter.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                mSubToolbar.showMultipleSelectInfo(true, mItemAdapter.getCheckedItemCount());
                break;
        }
        mActivity.invalidateOptionsMenu();
    }

    private void onCreateMenu(Menu menu, MenuInflater inflater) {
        switch (mSubMode) {
            default:
            case MODE_NORMAL:
                initActionbar(getString(R.string.fingerprint));
                inflater.inflate(R.menu.add_delete, menu);
                break;
            case MODE_DELETE:
                initActionbar(getString(R.string.delete_fingerprint));
                inflater.inflate(R.menu.delete_confirm, menu);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_list);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.fingerprint));
            mRootView.invalidate();
        }

        if (mUserInfo == null) {
            //         Log.e(TAG, "data is null");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mToastPopup.show(getString(R.string.none_data), null);
                    mScreenControl.backScreen();
                }
            }, 1000);
            return null;
        }
        if (VersionData.getCloudVersion(mActivity) > 1) {
            if (!mIsDisableModify) {
                mPopup.showWait(mCancelExitListener);
                request(mUserDataProvider.getFingerPrints(mUserInfo.user_id, mFingerPrintListener));
            }
        }
        return mRootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        User bundleItem = null;
        try {
            bundleItem = (User) mUserInfo.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return;
        }
        outState.putSerializable(User.TAG, bundleItem);
        outState.putSerializable(Setting.DISABLE_MODIFY, mIsDisableModify);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mIsDisableModify) {
            return;
        }
        onCreateMenu(menu, mActivity.getMenuInflater());
        super.onPrepareOptionsMenu(menu);
    }

    private void refreshValue() {
        clearValue();
        if (mUserInfo.fingerprint_templates == null) {
            mUserInfo.fingerprint_templates = new ArrayList<ListFingerprintTemplate>();
        }
        if (mItemAdapter != null) {
            mItemAdapter.setData(mUserInfo.fingerprint_templates);
            mItemAdapter.clearChoices();
        }
        if (mSelectDevicePopup == null) {
            mSelectDevicePopup = new SelectPopup<ListDevice>(mActivity, mPopup);
        }
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
            if (mItemAdapter != null) {
                mSubToolbar.setTotal(mItemAdapter.getCount());
            }
        }
    }

    private void scanBioMini() {
        if (isInValidCheck()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                int result = mBioMiniDataProvider.initDevice(BioMiniDataProvider.FingerTemplateType.UFA_TEMPLATE_TYPE_SUPREMA);
                if (result == UFA_OK || result == BioMiniDataProvider.UFA_ERR_ALREADY_INITIALIZED) {
                    if (mBioMiniTemplate0 == null) {
                        mBioMiniTemplate0 = new BioMiniTemplate();
                    }
                    if (mBioMiniTemplate1 == null) {
                        mBioMiniTemplate1 = new BioMiniTemplate();
                    }
                    showGuideScanFinger();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isInValidCheck()) {
                                return;
                            }
                            int result;
                            if (mIsTwiceInput) {
                                result = mBioMiniDataProvider.scanFingerPrint(mBioMiniTemplate1);
                            } else {
                                result = mBioMiniDataProvider.scanFingerPrint(mBioMiniTemplate0);
                            }
                            mPopup.dismiss();
                            if (result == UFA_OK) {
                                int quality;
                                if (mFingerprintTemplate == null) {
                                    mFingerprintTemplate = new ListFingerprintTemplate();
                                }
                                if (mIsTwiceInput) {
                                    quality = mBioMiniTemplate1.getQuality()[0];
                                    mScanQuality2nd = quality;
                                    mFingerprintTemplate.template1 = mBioMiniTemplate1.getTemplateString();
                                } else {
                                    quality = mBioMiniTemplate0.getQuality()[0];
                                    mScanQuality1st = quality;
                                    mFingerprintTemplate.template0 = mBioMiniTemplate0.getTemplateString();
                                }
                                if (quality < mQuality) {
                                    showRetryPopup(getString(R.string.low_quality), new OnPopupClickListener() {
                                        @Override
                                        public void OnPositive() {
                                            reScan();
                                        }

                                        @Override
                                        public void OnNegative() {
                                            clearValue();
                                        }
                                    });
                                    return;
                                }

                                if (mIsTwiceInput == true) {
                                    mIsTwiceInput = false;
                                    //scanVerify();
                                    mPopup.show(PopupType.FINGERPRINT_CONFIRM, mItemAdapter.getName(mReplacePosition), "1" + getString(R.string.st) + " " + getString(R.string.quality) + " " + mScanQuality1st + "\n" + "2" + getString(R
                                                    .string.nd) + " " + getString(R.string.quality) + " " + mScanQuality2nd + "\n" + getString(R.string
                                                    .verify_finger),
                                            null,
                                            null, null,
                                            false);
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mBioMiniDataProvider.verify(mBioMiniTemplate0,mBioMiniTemplate1) == UFA_OK) {
                                                sucess();
                                            } else {
                                                mPopup.show(PopupType.ALERT, getString(R.string.info), getString(R.string.fail_verify_finger), new OnPopupClickListener() {
                                                    @Override
                                                    public void OnPositive() {
                                                        clearValue();
                                                    }

                                                    @Override
                                                    public void OnNegative() {
                                                        clearValue();
                                                    }
                                                }, getString(R.string.ok), null, false);
                                            }
                                        }
                                    },1000);
                                } else {
                                    mIsTwiceInput = true;
                                    scanBioMini();
                                }
                            } else {
                                mPopup.show(PopupType.ALERT, getString(R.string.portable), BioMiniDataProvider.getMessage(result), null, null, null);
                            }
                        }
                    }, 1000);

                } else {
                    clearValue();
                    if (isInValidCheck()) {
                        return;
                    }
                    mToastPopup.show(ToastPopup.TYPE_DEFAULT, getString(R.string.portable), BioMiniDataProvider.getMessage(result));
                    return;
                }
            }
        });
    }

    private void showSelectDevice() {
        if (mIsDisableModify) {
            return;
        }
        mIsBioMiniRescan = false;
        mSelectDevicePopup.show(SelectType.DEVICE_FINGERPRINT_BIOMINI, new OnSelectResultListener<ListDevice>() {
            @Override
            public void OnResult(ArrayList<ListDevice> selectedItem, boolean isPositive) {
                if (isInValidCheck()) {
                    return;
                }
                if (selectedItem == null) {
                    clearValue();
                    return;
                }
                mDeviceId = selectedItem.get(0).id;
                if (mDeviceId == null) {
                    return;
                }
                if (mDeviceId.equals("-10")) {
                    int result = mBioMiniDataProvider.findDevice();
                    if (result == UFA_OK) {
                        mIsBioMiniRescan = true;
                        scanBioMini();
                        return;
                    } else {
                        mToastPopup.show(ToastPopup.TYPE_DEFAULT, getString(R.string.portable), BioMiniDataProvider.getMessage(result));
                    }
                    return;
                }
                scanFinger();
            }
        }, null, getString(R.string.select_device_orginal), false, true);

    }

    private void reScan() {
        if (isInValidCheck()) {
            return;
        }
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
                ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
                linkType.add(new SelectCustomData(getString(R.string.rescan_default), 0, false));
                linkType.add(new SelectCustomData(getString(R.string.rescan_change), 1, false));
                selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
                    @Override
                    public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                        if (isInValidCheck() || selectedItem == null) {
                            mIsTwiceInput = false;
                            return;
                        }
                        int select = selectedItem.get(0).getIntId();
                        if (select == 0) {
                            mQuality = 80;
                            scanFinger();
                            return;
                        } else {
                            mSeekBarPopup.show(getString(R.string.rescan_change), mOnChangeQualityResult, mQuality);
                        }
                    }
                }, linkType, getString(R.string.rescan), false);
            }
        });
    }
}
