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
import com.supremainc.biostar2.popup.SelectCustomData;
import com.supremainc.biostar2.popup.SelectPopup;
import com.supremainc.biostar2.popup.SelectPopup.OnSelectResultListener;
import com.supremainc.biostar2.popup.SelectPopup.SelectType;
import com.supremainc.biostar2.sdk.datatype.CardData.Card;
import com.supremainc.biostar2.sdk.datatype.CardData.ListCard;
import com.supremainc.biostar2.sdk.datatype.DeviceData.ListDevice;
import com.supremainc.biostar2.sdk.datatype.UserData.User;
import com.supremainc.biostar2.sdk.volley.Response;
import com.supremainc.biostar2.sdk.volley.Response.Listener;
import com.supremainc.biostar2.sdk.volley.VolleyError;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

import java.io.Serializable;
import java.util.ArrayList;

public class CardFragment extends BaseFragment {
    private static final int ASSGIGN_CARD = 1;
    private static final int CARD_READER = 0;
    private static final int MODE_DELETE = 1;
    private UserSubDepthFragmentLayout mLayout;
    private CardAdapter mItemAdapter;
    private SelectPopup<ListCard> mSelectCardPopup;
    private SelectPopup<ListDevice> mSelectDevicePopup;
    private SubToolbar mSubToolbar;
    private User mUserInfo;

    private String mDeviceId;
    private boolean mIsDisableModify;
    private int mReplacePosition = -1;

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
    private Listener<Card> mScanListener = new Response.Listener<Card>() {
        @Override
        public void onResponse(final Card response, Object deliverParam) {
            if (mIsDestroy || !isAdded()) {
                return;
            }
            mPopup.dismiss();
            if (response == null) {
                if (mScanErrorListener != null) {
                    mScanErrorListener.onErrorResponse(new VolleyError(getString(R.string.server_null)), deliverParam);
                }
                return;
            }
            if (!response.unassigned) {
                mToastPopup.show(getString(R.string.already_assigned), null);
                return;
            }

            mPopup.show(PopupType.CARD_CONFIRM, mItemAdapter.getName(mReplacePosition), response.card_id, new OnPopupClickListener() {
                @Override
                public void OnNegative() {

                }

                @Override
                public void OnPositive() {
                    setCard(response);
                }


            }, getString(R.string.ok), null, false);
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
                public void OnNegative() {
                    clearValue();
                }

                @Override
                public void OnPositive() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mPopup.show(PopupType.CARD, mItemAdapter.getName(mReplacePosition), getString(R.string.card_on_device), null, null, null, false);
                            mDeviceDataProvider.scanCard(TAG, mDeviceId, mScanListener, mScanErrorListener, null);
                        }
                    });
                }


            }, getString(R.string.ok), getString(R.string.cancel), false);
        }
    };

    public CardFragment() {
        super();
        setType(ScreenType.CARD_RIGISTER);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }

    private void clearValue() {
        mReplacePosition = -1;
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
                                mUserInfo.cards.remove(i);
                            }
                        }
                        refreshValue();
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
        if (mUserInfo.cards == null) {
            mUserInfo.cards = new ArrayList<ListCard>();
        }
        if (mItemAdapter == null) {
            mItemAdapter = new CardAdapter(mContext, mUserInfo.cards, mLayout.getListView(), new OnItemClickListener() {
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
                        showSelectItem();
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
        if (mUserInfo.cards != null) {
            sendLocalBroadcast(Setting.BROADCAST_UPDATE_CARD, (Serializable) mUserInfo.cards.clone());
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
                if (mUserInfo.cards.size() >= 8) {
                    mToastPopup.show(getString(R.string.max_size), null);
                    return true;
                }
                selectAddOption();
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
        if (mSelectCardPopup != null && mSelectCardPopup.isExpand()) {
            mSelectCardPopup.onSearch(query);
            return true;
        }
        if (mSelectDevicePopup != null && mSelectDevicePopup.isExpand()) {
            mSelectDevicePopup.onSearch(query);
            return true;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mLayout == null) {
            mLayout = new UserSubDepthFragmentLayout(this, null);
        }
        View view = mLayout.initView(this, inflater, container, savedInstanceState);
        initBaseValue(mLayout);
        if (!mLayout.isReUsedView()) {
            initValue(savedInstanceState);
            initActionbar(getString(R.string.card));
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
        MenuInflater inflater = mContext.getMenuInflater();
        if (mIsDisableModify) {
            return;
        }
        switch (mSubMode) {
            default:
            case MODE_NORMAL:
                initActionbar(getString(R.string.card));
                inflater.inflate(R.menu.add_delete, menu);
                break;
            case MODE_DELETE:
                initActionbar(getString(R.string.delete_card));
                inflater.inflate(R.menu.delete_confirm, menu);
                break;
        }
        super.onPrepareOptionsMenu(menu);
    }

    private void refreshValue() {
        clearValue();
        if (mSelectCardPopup == null) {
            mSelectCardPopup = new SelectPopup<ListCard>(mContext, mPopup);
        }
        if (mSelectDevicePopup == null) {
            mSelectDevicePopup = new SelectPopup<ListDevice>(mContext, mPopup);
        }
        if (mUserInfo.cards == null) {
            mUserInfo.cards = new ArrayList<ListCard>();
        }
        if (mItemAdapter != null) {
            mItemAdapter.setData(mUserInfo.cards);
            mItemAdapter.clearChoices();
        }
        if (mSubToolbar != null) {
            mSubToolbar.setSelectedCount(mItemAdapter.getCheckedItemCount());
            if (mItemAdapter != null) {
                mSubToolbar.setTotal(mItemAdapter.getCount());
            }
        }
    }

    private void selectAddOption() {
        mReplacePosition = mUserInfo.cards.size();
        SelectPopup<SelectCustomData> selectPopup = new SelectPopup<SelectCustomData>(mContext, mPopup);
        ArrayList<SelectCustomData> registerationOption = new ArrayList<SelectCustomData>();
        registerationOption.add(new SelectCustomData(getString(R.string.registeration_option_card_reader), CARD_READER, false));
        registerationOption.add(new SelectCustomData(getString(R.string.registeration_option_assign_card), ASSGIGN_CARD, false));

        selectPopup.show(SelectType.CUSTOM, new OnSelectResultListener<SelectCustomData>() {
            @Override
            public void OnResult(ArrayList<SelectCustomData> selectedItem) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (selectedItem == null) {
                    clearValue();
                    return;
                }
                int registrationOption = selectedItem.get(0).getIntId();
                switch (registrationOption) {
                    case CARD_READER: {
                        showSelectDevice();
                        break;
                    }
                    case ASSGIGN_CARD: {
                        showSelectItem();
                        break;
                    }
                }
            }
        }, registerationOption, getString(R.string.registeration_option), false, false);
    }

    private void setCard(ListCard card) {
        for (ListCard item : mUserInfo.cards) {
            // TODO card_id 인지.type까지 같이 비교해야할지..id로 비교해야할지..
            if (item.card_id.equals(card.card_id) && item.type.equals(card.type)) {
                mToastPopup.show(getString(R.string.already_assigned), null);
                return;
            }
        }
        try {
            if (mReplacePosition == -1 || mReplacePosition >= mUserInfo.cards.size()) {
                mUserInfo.cards.add(card.clone());
            } else {
                mUserInfo.cards.set(mReplacePosition, card.clone());
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
        initValue(null);
    }

    private void showSelectDevice() {
        mSelectDevicePopup.show(SelectType.DEVICE_CARD, new OnSelectResultListener<ListDevice>() {
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
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mPopup.show(PopupType.CARD, mItemAdapter.getName(mReplacePosition), getString(R.string.card_on_device), null, null, null, false);
                        mDeviceDataProvider.scanCard(TAG, mDeviceId, mScanListener, mScanErrorListener, null);
                    }
                });
            }
        }, null, getString(R.string.registeration_option_card_reader), false, true);
    }

    private void showSelectItem() {
        mSelectCardPopup.show(SelectType.CARD, new OnSelectResultListener<ListCard>() {
            @Override
            public void OnResult(ArrayList<ListCard> selectedItem) {
                if (isInValidCheck(null)) {
                    return;
                }
                if (selectedItem == null) {
                    clearValue();
                    return;
                }
                ListCard card = null;
                try {
                    card = selectedItem.get(0).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return;
                }
                setCard(card);
            }
        }, null, getString(R.string.registeration_option_assign_card), false, true);
    }
}
