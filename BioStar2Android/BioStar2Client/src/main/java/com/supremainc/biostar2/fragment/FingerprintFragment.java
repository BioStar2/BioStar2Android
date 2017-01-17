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

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.Toast;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.datatype.BioMiniTemplate;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.adapter.FingerPrintAdapter;
import com.supremainc.biostar2.provider.BioMiniDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Common.ResponseStatus;
import com.supremainc.biostar2.sdk.datatype.v2.Common.VersionData;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Device;
import com.supremainc.biostar2.sdk.datatype.v2.Device.Devices;
import com.supremainc.biostar2.sdk.datatype.v2.Device.FingerprintVerify;
import com.supremainc.biostar2.sdk.datatype.v2.Device.ListDevice;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.FingerPrints;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.ListFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.v2.FingerPrint.ScanFingerprintTemplate;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;
import com.supremainc.biostar2.widget.popup.Popup.OnPopupClickListener;
import com.supremainc.biostar2.widget.popup.Popup.PopupType;
import com.supremainc.biostar2.widget.popup.SeekBarPopup;
import com.supremainc.biostar2.widget.popup.SelectCustomData;
import com.supremainc.biostar2.widget.popup.SelectPopup;
import com.supremainc.biostar2.widget.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.widget.popup.SelectPopup.SelectType;

import java.io.Serializable;
import java.util.ArrayList;

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
    private int mReplacePosition = -1;
    private SeekBarPopup mSeekBarPopup;
    private BioMiniDataProvider mBioMiniDataProvider;
    private BioMiniTemplate mBioMiniTemplate1;
    private BioMiniTemplate mBioMiniTemplate2;
    private boolean mIsBioMiniRescan;

    private SeekBarPopup.OnResult mOnResult = new SeekBarPopup.OnResult() {
        @Override
        public void OnResult(String data) {
            if (data != null && !data.isEmpty()) {
                mQuality = Integer.valueOf(data);
                mHandler.removeCallbacks(mRunReScan);
                mHandler.postDelayed(mRunReScan,500);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reScan();
                    }
                },500);
            }
        }
    };

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
    private Response.ErrorListener mScanErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(final VolleyError error, Object deliverParam) {
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
                            if (error != null) {
                                if (error.getCode().equals(Device.SCAN_QUALITY_IS_LOW)) {
                                    reScan();
                                    return;
                                }
                            }
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
    private Listener<FingerprintVerify> mScanConfirmListener = new Response.Listener<FingerprintVerify>() {
        @Override
        public void onResponse(FingerprintVerify response, Object deliverParam) {
            if (isInValidCheck(null)) {
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
                            if (VersionData.getCloudVersion(mContext) > 1) {
                                mPopup.showWait(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        mScreenControl.backScreen();
                                    }
                                });
                                mUserDataProvider.modifyFingerPrints(TAG, mUserInfo.user_id, mUserInfo.fingerprint_templates, mModifyFingerPrintListener, mErrorBackListener, null);
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
    };
    private Listener<ScanFingerprintTemplate> mScanListener = new Response.Listener<ScanFingerprintTemplate>() {
        @Override
        public void onResponse(ScanFingerprintTemplate response, Object deliverParam) {
            if (isInValidCheck(null)) {
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
                if (mQuality > Integer.valueOf(quality)) {
                    reScan();
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


    private Listener<FingerPrints> mFingerPrintListener = new Response.Listener<FingerPrints>() {
        @Override
        public void onResponse(final FingerPrints response, Object deliverParam) {
            if (isInValidCheck(null)) {
                return;
            }
            mPopup.dismiss();
            if (mUserInfo == null || response == null || response.records == null) {
                mErrorBackListener.onErrorResponse(new VolleyError(getString(R.string.server_null)),deliverParam);
                return;
            }
            mUserInfo.fingerprint_templates = response.records;
            mUserInfo.card_count = mUserInfo.fingerprint_templates.size();
            refreshValue();
        }
    };

    private Response.Listener<ResponseStatus> mModifyFingerPrintListener = new Response.Listener<ResponseStatus>() {
        @Override
        public void onResponse(ResponseStatus response, Object param) {
            if (isInValidCheck(null)) {
                return;
            }
            if (response == null) {
                mErrorBackListener.onErrorResponse(new VolleyError(null,"Server response is null"),null);
                return;
            }
            mUserDataProvider.getFingerPrints(TAG, mUserInfo.user_id,mFingerPrintListener,mErrorBackListener,null);
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
                        if (VersionData.getCloudVersion(mContext) > 1 ) {
                            int i = mItemAdapter.getCount() - 1;
                            ArrayList<ListFingerprintTemplate> fingerprintTemplates = (ArrayList<ListFingerprintTemplate>) mUserInfo.fingerprint_templates.clone();
                            for (; i >= 0; i--) {
                                boolean isCheck = mItemAdapter.isItemChecked(i);
                                if (isCheck) {
                                    fingerprintTemplates.remove(i);
                                }
                            }
                            mPopup.showWait(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mScreenControl.backScreen();
                                }
                            });
                            mUserDataProvider.modifyFingerPrints(TAG,mUserInfo.user_id,fingerprintTemplates,mModifyFingerPrintListener,mErrorStayListener,null);
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
            mBioMiniDataProvider = BioMiniDataProvider.getInstance(mContext);
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
        }
        if (mItemAdapter == null) {
            mItemAdapter = new FingerPrintAdapter(mContext, mUserInfo.fingerprint_templates, getListView(), new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mSubToolbar == null) {
                        return;
                    }
                    if (mSubMode == MODE_DELETE) {
                        mSubToolbar.setSelectAllViewOff();
                        mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
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
        setResID(R.layout.fragment_list);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.fingerprint));
            mRootView.invalidate();
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
        if (VersionData.getCloudVersion(mContext) > 1) {
            if (!mIsDisableModify) {
                mPopup.showWait(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mScreenControl.backScreen();
                    }
                });
                mUserDataProvider.getFingerPrints(TAG, mUserInfo.user_id, mFingerPrintListener, mErrorBackListener, null);
            }
        }
        return mRootView;
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

    private void scanBioMini() {
        int result = mBioMiniDataProvider.initDevice(BioMiniDataProvider.FingerTemplateType.UFA_TEMPLATE_TYPE_SUPREMA);
        if (result == BioMiniDataProvider.UFA_OK || result == BioMiniDataProvider.UFA_ERR_ALREADY_INITIALIZED) {
            if (mBioMiniTemplate1 == null) {
                mBioMiniTemplate1 = new BioMiniTemplate();
            }
            mPopup.show(PopupType.FINGERPRINT, mItemAdapter.getName(mReplacePosition), getString(R.string.finger_on_device), null, null, null, false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int result = mBioMiniDataProvider.scanFingerPrint(mBioMiniTemplate1);
                    mPopup.dismiss();
                    if (result == BioMiniDataProvider.UFA_OK) {
                        if (mBioMiniTemplate1.getQuality()[0] < mQuality) {
                            reScan();
                            return;
                        }
                        if (mFingerprintTemplate == null) {
                            mFingerprintTemplate = new ListFingerprintTemplate();
                        }
                        mFingerprintTemplate.template0 = mBioMiniTemplate1.getTemplateString();
                        mFingerprintTemplate.template1 = mFingerprintTemplate.template0;
                        Toast.makeText(mContext,"size:"+mBioMiniTemplate1.getTemplateSize()[0]+" q:"+mBioMiniTemplate1.getQuality()[0],Toast.LENGTH_LONG).show();

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
                                        if (VersionData.getCloudVersion(mContext) > 1) {
                                            mPopup.showWait(new DialogInterface.OnCancelListener() {
                                                @Override
                                                public void onCancel(DialogInterface dialog) {
                                                    mScreenControl.backScreen();
                                                }
                                            });
                                            mUserDataProvider.modifyFingerPrints(TAG, mUserInfo.user_id, mUserInfo.fingerprint_templates, mModifyFingerPrintListener, mErrorBackListener, null);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void OnNegative() {
                                clearValue();
                            }
                        }, getString(R.string.ok), null, false);
                    } else {
                        Toast.makeText(mContext,"err:"+BioMiniDataProvider.getMessage(result)+" code:"+result,Toast.LENGTH_LONG).show();
                    }
                }
            },500);

        } else {
            mPopup.show(PopupType.ALERT,"BioMini",BioMiniDataProvider.getMessage(result),null,null,null);
            return;
        }
    }

    private void showSelectDevice() {
//        int result = mBioMiniDataProvider.findDevice();
//        if (result == BioMiniDataProvider.UFA_OK) {
//            mIsBioMiniRescan = true;
//            scanBioMini();
//            return;
//        }
//        Toast.makeText(mContext,"err:"+BioMiniDataProvider.getMessage(result)+" code:"+result,Toast.LENGTH_LONG).show();
        if (mIsDisableModify) {
            return;
        }
        mIsBioMiniRescan = false;
        mSelectDevicePopup.show(SelectType.DEVICE_FINGERPRINT, new OnSelectResultListener<ListDevice>() {
            @Override
            public void OnResult(ArrayList<ListDevice> selectedItem, boolean isPositive) {
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
    private Runnable mRunReScan = new  Runnable() {
        @Override
        public void run() {
            if (isInValidCheck(null)) {
                return;
            }

            int result = mBioMiniDataProvider.findDevice();
            if (result == BioMiniDataProvider.UFA_OK) {
                scanBioMini();
                return;
            }
            if (mIsBioMiniRescan) {
                mIsBioMiniRescan = false;
                mPopup.show(PopupType.ALERT, "BioMini", BioMiniDataProvider.getMessage(result), null, null,
                        null, false);
                return;
            }
            mPopup.show(PopupType.FINGERPRINT_AGAGIN, mItemAdapter.getName(mReplacePosition), getString(R.string.low_quality) + "\n" + getString(R.string.finger_on_device_same), null, null,
                    null, false);
            mDeviceDataProvider.scanFingerprint(TAG, mDeviceId, mQuality, false, mScanListener, mScanErrorListener, null);
        }
    };
    private void reScan() {
        if (isInValidCheck(null)) {
            return;
        }
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        linkType.add(new SelectCustomData(getString(R.string.rescan_default), 0, false));
        linkType.add(new SelectCustomData(getString(R.string.rescan_change), 1, false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem,boolean isPositive) {
                if (isInValidCheck(null) || selectedItem == null) {
                    mIsTwiceInput = false;
                    return;
                }
                int select = selectedItem.get(0).getIntId();
                if (select == 0) {
                    mQuality = 80;
                    mHandler.removeCallbacks(mRunReScan);
                    mHandler.postDelayed(mRunReScan,500);
                    return;
                } else {
                    mSeekBarPopup.show(getString(R.string.rescan_change),mOnResult,mQuality);
                }
            }
        }, linkType, getString(R.string.rescan), false);
    }
}
