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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
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

import com.supremainc.biostar2.R;
import com.supremainc.biostar2.adapter.FaceAdapter;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.sdk.models.v2.common.ResponseStatus;
import com.supremainc.biostar2.sdk.models.v2.device.ListDevice;
import com.supremainc.biostar2.sdk.models.v2.face.Face;
import com.supremainc.biostar2.sdk.models.v2.face.Faces;
import com.supremainc.biostar2.sdk.models.v2.user.User;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FaceFragment extends BaseFragment {
    private static final int MODE_DELETE = 1;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;
    private Faces mFaces;
    private Face mFace;
    private FaceAdapter mItemAdapter;
    private String mDeviceId;
    private boolean mIsDisableModify;
    private int mReplacePosition = -1;
    private Bitmap rBmp;
    private int mQuality = 4;
    private SeekBarPopup mSeekBarPopup;

    private Callback<ResponseStatus> mModifyUserPhotoListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    updatePhoto();
                }

                @Override
                public void OnNegative() {

                }
            });
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                showRetryPopup(getResponseErrorMessage(response), new OnPopupClickListener() {
                    @Override
                    public void OnPositive() {
                        updatePhoto();
                    }

                    @Override
                    public void OnNegative() {

                    }
                });
                return;
            }
            mUserInfo.photo = mFace.raw_image;
            mUserInfo.photo_exist = true;
            mUserInfo.last_modify = String.valueOf(System.currentTimeMillis());
            if (mUserDataProvider.getLoginUserInfo().user_id.equals(mUserInfo.user_id)) {
                try {
                     mUserDataProvider.setLoginUserInfo(mUserInfo.clone());
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
                } catch (Exception e) {
                    Log.e(TAG, " " + e.getMessage());
                }
            }
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), null, null, null);
        }
    };

    private Callback<ResponseStatus> mModifyUserListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(mCancelExitListener);
                             request(mUserDataProvider.modifyUser(mUserInfo, mModifyUserListener));
                        }
                    });
                }

                @Override
                public void OnNegative() {

                }
            });
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            if (mUserDataProvider.getLoginUserInfo().user_id.equals(mUserInfo.user_id)) {
                try {
                     mUserDataProvider.setLoginUserInfo(mUserInfo.clone());
                    LocalBroadcastManager.getInstance(mActivity).sendBroadcast(new Intent(Setting.BROADCAST_REROGIN));
                } catch (Exception e) {
                    Log.e(TAG, " " + e.getMessage());
                }
            }
            mPopup.show(PopupType.CONFIRM, getString(R.string.info), getString(R.string.user_modify_success), null, null, null);
        }
    };

    private Callback<Faces> mFacesListener = new Callback<Faces>() {
        @Override
        public void onFailure(Call<Faces> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.showWait(mCancelExitListener);
                             request(mUserDataProvider.getFace(mUserInfo.user_id, mFacesListener));
                        }
                    });
                }

                @Override
                public void OnNegative() {
                    if (mScreenControl != null) {
                        mScreenControl.backScreen();
                    }
                }
            });
        }

        @Override
        public void onResponse(Call<Faces> call, Response<Faces> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            mFaces = response.body();
            mUserInfo.face_template_count = mFaces.records.size();
            refreshValue();
            if (mFace != null && mFace.raw_image != null) {
                if (rBmp != null) {
                    rBmp.recycle();
                    rBmp = null;
                }
                byte[] photoByte = Base64.decode(mFace.raw_image, 0);
                final Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);
                rBmp = ImageUtil.getRoundedBitmap(bmp, true);
                mPopup.show(PopupType.FACE_CONFIRM, rBmp, mItemAdapter.getName(mReplacePosition), getString(R.string.scan_success), new OnPopupClickListener() {
                    @Override
                    public void OnPositive() {
                        if (mPopup.getValue() != 0) {
                            updatePhoto();
                        }
                    }

                    @Override
                    public void OnNegative() {
                        clearValue();
                    }
                }, getString(R.string.ok), null, false);
            }
        }
    };

    private Callback<ResponseStatus> mModifyFacesListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    modifyFaces();
                }

                @Override
                public void OnNegative() {
                    if (mScreenControl != null) {
                        mScreenControl.backScreen();
                    }
                }
            });
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            mPopup.showWait(mCancelExitListener);
             request(mUserDataProvider.getFace(mUserInfo.user_id, mFacesListener));
        }
    };

    private Callback<ResponseStatus> mDeleteFacesListener = new Callback<ResponseStatus>() {
        @Override
        public void onFailure(Call<ResponseStatus> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showErrorPopup(t.getMessage(),true);
        }

        @Override
        public void onResponse(Call<ResponseStatus> call, Response<ResponseStatus> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            mPopup.showWait(mCancelExitListener);
             request(mUserDataProvider.getFace(mUserInfo.user_id, mFacesListener));
        }
    };

    private Callback<Face> mScanListener = new Callback<Face>() {
        @Override
        public void onFailure(Call<Face> call, Throwable t) {
            if (isIgnoreCallback(call,true)) {
                return;
            }
            showRetryPopup(t.getMessage(), new OnPopupClickListener() {
                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (isInValidCheck()) {
                                return;
                            }
                            showChangeQualityPopup();
                        }
                    });
                }

                @Override
                public void OnNegative() {
                    clearValue();
                }
            });
        }

        @Override
        public void onResponse(Call<Face> call, Response<Face> response) {
            if (isIgnoreCallback(call,response,true)) {
                return;
            }
            if (isInvalidResponse( response,false,false)) {
                onFailure(call,new Throwable(getResponseErrorMessage(response)));
                return;
            }
            mFace = response.body();
            mFace.id = null;
            modifyFaces();
        }
    };


    private void modifyFaces() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Faces faces = mFaces.clone();
                    if (mReplacePosition == -1 || mReplacePosition >= mFaces.records.size()) {
                        faces.records.add(mFace.clone());
                    } else {
                        faces.records.set(mReplacePosition, mFace.clone());
                    }
                    mPopup.showWait(mCancelExitListener);
                     request(mUserDataProvider.modifyFaces(mUserInfo.user_id, faces, mModifyFacesListener));
                } catch (CloneNotSupportedException e) {
                    mPopup.dismiss();
                }
            }
        });
    }
    private SeekBarPopup.OnResult mOnResult = new SeekBarPopup.OnResult() {
        @Override
        public void OnResult(String data) {
            if (data != null && !data.isEmpty()) {
                mQuality = Integer.valueOf(data);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPopup.show(PopupType.FACE, mItemAdapter.getName(mReplacePosition), getString(R.string.face_on_device), null, null, null, false);
                         request(mDeviceDataProvider.scanFace(mDeviceId, mQuality, mScanListener));
                    }
                }, 500);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showChangeQualityPopup();
                    }
                }, 500);
            }
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
    public FaceFragment() {
        super();
        setType(ScreenType.FACE);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void showChangeQualityPopup() {
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mActivity, mPopup);
        ArrayList<SelectCustomData> linkType = new ArrayList<SelectCustomData>();
        String rescan_default = getString(R.string.rescan_default);
        rescan_default = rescan_default.replace("80", "4");
        linkType.add(new SelectCustomData(rescan_default, 0, false));
        linkType.add(new SelectCustomData(getString(R.string.rescan_change), 1, false));
        selectPopup.show(SelectPopup.SelectType.CUSTOM, new SelectPopup.OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem, boolean isPositive) {
                if (isInValidCheck() || selectedItem == null) {
                    return;
                }
                int select = selectedItem.get(0).getIntId();
                if (select == 0) {
                    mQuality = 4;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.show(PopupType.FACE, mItemAdapter.getName(mReplacePosition), getString(R.string.face_on_device), null, null, null, false);
                             request(mDeviceDataProvider.scanFace(mDeviceId, mQuality, mScanListener));
                        }
                    }, 500);
                    return;
                } else {
                    mSeekBarPopup.show(getString(R.string.rescan_change), mOnResult, mQuality);
                }
            }
        }, linkType, getString(R.string.rescan), false);
    }

    private void clearValue() {

    }
    private void updatePhoto() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPopup.showWait(mCancelExitListener);
                if (mFace.raw_image != null) {
                    try {
                        User userInfo = mUserInfo.clone();
                        userInfo.photo = mFace.raw_image;
                        userInfo.photo_exist = true;
                         request(mUserDataProvider.modifyUser(userInfo, mModifyUserPhotoListener));
                    } catch (Exception e) {
                        mPopup.dismissWiat();
                    }
                }
            }
        }, 500);
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
                        try {
                            Faces faces = mFaces.clone();
                            int i = mItemAdapter.getCount() - 1;
                            for (; i >= 0; i--) {
                                boolean isCheck = mItemAdapter.isItemChecked(i);
                                if (isCheck) {
                                    faces.records.remove(i);
                                }
                            }
                            mPopup.showWait(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mScreenControl.backScreen();
                                }
                            });
                            mFace = null;
                             request(mUserDataProvider.modifyFaces(mUserInfo.user_id, faces, mDeleteFacesListener));
                        } catch (Exception e) {
                            Log.e(TAG, "e:" + e.getMessage());
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
        Boolean disable = getExtraData(Setting.DISABLE_MODIFY, savedInstanceState);

        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(mSubToolBarEvent, getActivity());
            mSubToolbar.setVisibleSearch(false, null);
            mSubToolbar.showMultipleSelectInfo(false, 0);
        }
        if (mFaces == null) {
            mFaces = new Faces();
        }
        if (mFaces.records == null) {
            mFaces.records = new ArrayList<Face>();
        }
        if (disable != null) {
            mIsDisableModify = disable;
            if (mUserInfo.face_templates != null) {
                mFaces.records = (ArrayList<Face>) mUserInfo.face_templates.clone();
            }
        }
        if (mSeekBarPopup == null) {
            mSeekBarPopup = new SeekBarPopup(getActivity());
            mSeekBarPopup.setRange(0, 9, 1);
        }
        if (mItemAdapter == null) {
            mItemAdapter = new FaceAdapter(mActivity, mFaces.records, getListView(), new OnItemClickListener() {
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
        if (mItemAdapter != null) {
            mItemAdapter.clearItems();
        }
        if (rBmp != null) {
            rBmp.recycle();
            rBmp = null;
        }

        //      Log.e(TAG,"unInit:"+mBioMiniDataProvider.getMessage(unint));
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mFaces != null && mFaces.records != null && !mIsDisableModify) {
            try {
                sendLocalBroadcast(Setting.BROADCAST_UPDATE_FACE, (Serializable) mUserInfo.clone());
            } catch (Exception e) {

            }
        }
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
                if (mFaces.records.size() >= 5) {
                    mToastPopup.show(getString(R.string.max_size), null);
                    return true;
                }
                mReplacePosition = mFaces.records.size();
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
                initActionbar(getString(R.string.face));
                inflater.inflate(R.menu.add_delete, menu);
                break;
            case MODE_DELETE:
                initActionbar(getString(R.string.delete));
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
            initActionbar(getString(R.string.face));
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
        if (!mIsDisableModify) {
            mPopup.showWait(mCancelExitListener);
             request(mUserDataProvider.getFace(mUserInfo.user_id, mFacesListener));
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (mFaces == null) {
            mFaces = new Faces();
        }
        if (mFaces.records == null) {
            mFaces.records = new ArrayList<Face>();
        }
        if (mItemAdapter != null) {
            mItemAdapter.setData(mFaces.records);
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

    private void showSelectDevice() {
        if (mIsDisableModify) {
            return;
        }
        mSelectDevicePopup.show(SelectType.DEVICE_FACE, new OnSelectResultListener<ListDevice>() {
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPopup.show(PopupType.FACE, mItemAdapter.getName(mReplacePosition), getString(R.string.face_on_device), null, null, null, false);
                         request(mDeviceDataProvider.scanFace(mDeviceId, mQuality, mScanListener));
                    }
                });
            }
        }, null, getString(R.string.select_device_orginal), false, true);

    }
}
