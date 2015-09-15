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
package com.supremainc.biostar2.user;

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
import com.supremainc.biostar2.Setting;
import com.supremainc.biostar2.base.BaseFragment;
import com.supremainc.biostar2.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.popup.Popup.PopupType;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.DeviceData.FingerprintVerify;
import com.supremainc.biostar2.sdk.datatype.DeviceData.ListDevice;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.FingerPrintData.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.io.Serializable;
import java.util.ArrayList;

public class FingerprintFragment extends BaseFragment {
    private static final int MODE_DELETE = 1;
    private UserSubDepthFragmentLayout mLayout;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;
    private FingerPrintAdapter mItemAdapter;

    private String mDeviceId;
    private ListFingerprintTemplate mFingerprintTemplate;
    private boolean mIsDisableModify;
    private boolean mIsTwiceInput;
    private int mQuality = 40;
    private int mReplacePosition = -1;

    private Response.ErrorListener mScanConfirmErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismiss();
            mPopup.show(PopupType.ALERT, getString(R.string.info), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnNegative() {
                    clearValue();
                }

                @Override
                public void OnPositive() {
                    clearValue();
                }


            }, getString(R.string.ok), null, false);
        }
    };
    private Listener<FingerprintVerify> mScanConfirmListener = new Response.Listener<FingerprintVerify>() {
        @Override
        public void onResponse(FingerprintVerify response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismiss();
            if (response == null) {
                Log.e(TAG, "error");
                mScanErrorListener.onErrorResponse(new VolleyError(getString(R.string.server_null)), deliverParam);
                return;
            }
            if (!response.verify_result) {
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

            mPopup.show(PopupType.FINGERPRINT_CONFIRM, mItemAdapter.getName(mReplacePosition), getString(R.string.scan_success), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
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
                }

                @Override
                public void OnNegative() {
                    clearValue();
                }
            }, getString(R.string.ok), null, false);
        }
    };
    private Listener<ScanFingerprintTemplate> mScanListener = new Response.Listener<ScanFingerprintTemplate>() {
        @Override
        public void onResponse(ScanFingerprintTemplate response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismiss();
            if (response == null) {
                Log.e(TAG, "error");
                mScanErrorListener.onErrorResponse(new VolleyError(getString(R.string.server_null)), deliverParam);
                return;
            }

            if (mFingerprintTemplate == null) {
                mFingerprintTemplate = new ListFingerprintTemplate();
                mFingerprintTemplate.template0 = response.template0;
                mFingerprintTemplate.template1 = response.template0;
            }
            String quality = response.enroll_quality;
            if (quality != null) {
                if (40 > Integer.valueOf(quality)) {
                    mPopup.show(PopupType.FINGERPRINT_AGAGIN, mItemAdapter.getName(mReplacePosition), getString(R.string.low_quality) + "\n" + getString(R.string.finger_on_device_same), null, null,
                            null, false);
                    mDeviceDataProvider.scanFingerprint(TAG, mDeviceId, mQuality, false, mScanListener, mScanErrorListener, null);
                    return;
                }
            }

            if (mIsTwiceInput == true) {
                mIsTwiceInput = false;
                mFingerprintTemplate.template1 = response.template0;
                mPopup.show(PopupType.FINGERPRINT_CONFIRM, mItemAdapter.getName(mReplacePosition), getString(R.string.quality) + " " + quality + "\n" + getString(R.string.verify_finger), null, null, null, false);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceDataProvider.verifyFingerprint(TAG, mDeviceId, 0, mFingerprintTemplate, mScanConfirmListener, mScanConfirmErrorListener, null);
                    }
                }, 1000);

            } else {
                mIsTwiceInput = true;
                mPopup.show(PopupType.FINGERPRINT_AGAGIN, mItemAdapter.getName(mReplacePosition), getString(R.string.quality) + " " + quality + "\n" + getString(R.string.finger_on_device_same), null,
                        null, null, false);
                mDeviceDataProvider.scanFingerprint(TAG, mDeviceId, mQuality, false, mScanListener, mScanErrorListener, null);
            }

        }
    };
    private Response.ErrorListener mScanErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error, Object deliverParam) {
            if (isInValidCheck(error)) {
                return;
            }
            mPopup.dismiss();
            mPopup.show(PopupType.ALERT, getString(R.string.fail_retry), Setting.getErrorMessage(error, mContext), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIsTwiceInput) {
                                mPopup.show(PopupType.FINGERPRINT_AGAGIN, mItemAdapter.getName(mReplacePosition), getString(R.string.finger_on_device_same), null, null, null, false);
                            } else {
                                mPopup.show(PopupType.FINGERPRINT, mItemAdapter.getName(mReplacePosition), getString(R.string.finger_on_device), null, null, null, false);
                            }
                            mDeviceDataProvider.scanFingerprint(TAG, mDeviceId, mQuality, false, mScanListener, mScanErrorListener, null);
                        }
                    });
                }

                @Override
                public void OnNegative() {
                    clearValue();
                }
            }, getString(R.string.ok), getString(R.string.cancel), false);
        }
    };
    private SubToolbar.SubToolBarEvent mSubToolBarEvent = new SubToolbar.SubToolBarEvent() {
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

    public FingerprintFragment() {
        super();
        setType(ScreenType.FINGERPRINT_REGISTER);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void clearValue() {
        mReplacePosition = -1;
        mFingerprintTemplate = null;
        mIsTwiceInput = false;
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
                        int i = mItemAdapter.getCount() - 1;
                        for (; i >= 0; i--) {
                            boolean isCheck = mItemAdapter.isItemChecked(i);
                            if (isCheck) {
                                mUserInfo.fingerprint_templates.remove(i);
                            }
                        }
                        refreshValue();
                        ;
                    }
                });
            }


        }, getString(R.string.ok), getString(R.string.cancel));
    }

    private void initValue(Bundle savedInstanceState) {
        if (mUserInfo == null) {
            mUserInfo = getExtraData(User.TAG, savedInstanceState);
        }
        Boolean disable = getExtraData(Setting.DISABLE_MODIFY, savedInstanceState);
        if (disable != null) {
            mIsDisableModify = disable;
        }

        if (mSubToolbar == null) {
            mSubToolbar = mLayout.getSubToolbar(mSubToolBarEvent);
        }
        if (mUserInfo.fingerprint_templates == null) {
            mUserInfo.fingerprint_templates = new ArrayList<ListFingerprintTemplate>();
        }
        if (mItemAdapter == null) {
            mItemAdapter = new FingerPrintAdapter(mContext, mUserInfo.fingerprint_templates, mLayout.getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
                        ;
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
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_FINGER, (Serializable) mUserInfo.fingerprint_templates.clone());
        }
        if (mItemAdapter != null) {
            mItemAdapter.clearItems();
        }
        super.onDestroy();
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
        mContext.invalidateOptionsMenu();
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
        if (mLayout == null) {
            mLayout = new UserSubDepthFragmentLayout(this, null);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.fingerprint));
        }

        if (mUserInfo == null) {
            Log.e(TAG, "data is null");
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mToastPopup.show(getString(R.string.none_data), null);
                    mScreenControl.backScreen();
                }
            }, 1000);
            return null;
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "onSaveInstanceState");
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
        onCreateMenu(menu, mContext.getMenuInflater());
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
            mSelectDevicePopup = new SelectPopup<ListDevice>(mContext, mPopup);
        }
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
            if (mItemAdapter != null) {
                mSubToolbar.setTotal(mItemAdapter.getCount());
            }
        }
    }

    private void showSelectDevice() {
        mSelectDevicePopup.show(SelectType.DEVICE_FINGERPRINT, new OnSelectResultListener<ListDevice>() {
            @Override
            public void OnResult(ArrayList<ListDevice> selectedItem) {
                if (isInValidCheck(null)) {
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPopup.show(PopupType.FINGERPRINT, mItemAdapter.getName(mReplacePosition), getString(R.string.finger_on_device), null, null, null, false);
                        mDeviceDataProvider.scanFingerprint(TAG, mDeviceId, mQuality, false, mScanListener, mScanErrorListener, null);
                    }
                });
            }
        }, null, getString(R.string.select_device_orginal), false, true);
    }
}
