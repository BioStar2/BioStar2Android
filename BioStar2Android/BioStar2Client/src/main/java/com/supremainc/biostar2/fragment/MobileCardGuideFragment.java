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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.datatype.MobileCardData;
import com.supremainc.biostar2.impl.OnSingleClickListener;
import com.supremainc.biostar2.provider.MobileCardDataProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Card.MobileCard;
import com.supremainc.biostar2.sdk.datatype.v2.User.User;
import com.supremainc.biostar2.sdk.provider.TimeConvertProvider;
import com.supremainc.biostar2.sdk.utils.ImageUtil;
import com.supremainc.biostar2.view.StyledTextView;
import com.supremainc.biostar2.view.SubToolbar;
import com.supremainc.biostar2.view.SwitchView;
import com.supremainc.biostar2.widget.ScreenControl.ScreenType;

public class MobileCardGuideFragment extends BaseFragment {
    private SubToolbar mSubToolbar;
    private int mTotal = -1;
    private User mUser;
    private MobileCardDataProvider mMobileCardDataProvider;
    private MobileCard mCard;

    private void setAccessOnCardData(View v, User user, MobileCard card) {
        StyledTextView cardType = (StyledTextView) v.findViewById(R.id.card_type);
        cardType.setText(mContext.getString(R.string.access_on_card));
        ImageView photo = (ImageView) v.findViewById(R.id.user_photo);
        setPhoto(photo,user.photo);
        StyledTextView cardID = (StyledTextView) v.findViewById(R.id.card_id);
        cardID.setText(card.card_id);
        StyledTextView fingerCount = (StyledTextView) v.findViewById(R.id.fingerprint_count);
        if (card.fingerprint_index_list != null) {
            fingerCount.setText(String.valueOf(card.fingerprint_index_list.size()));
        } else {
            fingerCount.setText("2");
        }
        StyledTextView period = (StyledTextView) v.findViewById(R.id.period);
        String startDateTime = card.getTimeFormmat(TimeConvertProvider.getInstance(), MobileCard.TimeType.start_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        if (startDateTime == null) {
            startDateTime = "2000.12.12";
        }
        String endDateTime = card.getTimeFormmat(TimeConvertProvider.getInstance(), MobileCard.TimeType.expiry_datetime, TimeConvertProvider.DATE_TYPE.FORMAT_DATE_HOUR_MIN);
        if (endDateTime == null) {
            endDateTime = "2030.12.12";
        }
        period.setText(startDateTime + " - " + endDateTime);
        StyledTextView name = (StyledTextView) v.findViewById(R.id.user_name);
        name.setText(user.getName());
        StyledTextView accessGroup = (StyledTextView) v.findViewById(R.id.access_group);
        if (card.access_groups != null && card.access_groups.size() > 0) {
            if (card.access_groups.size() > 1) {
                accessGroup.setText(card.access_groups.get(0).name + " + " + (card.access_groups.size() - 1));
            } else {
                accessGroup.setText(card.access_groups.get(0).name);
            }
        } else {
            accessGroup.setText("All Groups");
        }
    }
    private void setPhoto(ImageView view,String photo) {
        if(photo!=null&&!photo.isEmpty())   {
            byte[] photoByte = Base64.decode(photo, 0);
            Bitmap bmp = ImageUtil.byteArrayToBitmap(photoByte);
            if (bmp != null) {
                view.setImageBitmap(bmp);
            }
        }
    }
//    private void setAccessOnCardData(View v, User user, MobileCardData.MobileCard card) {
//        StyledTextView cardType = (StyledTextView) v.findViewById(R.id.card_type);
//        cardType.setText(mContext.getString(R.string.access_on_card));
//        ImageView photo = (ImageView) v.findViewById(R.id.user_photo);
//        StyledTextView cardID = (StyledTextView) v.findViewById(R.id.card_id);
//        cardID.setText(card.cardID);
//        StyledTextView fingerCount = (StyledTextView) v.findViewById(R.id.fingerprint_count);
//        fingerCount.setText(String.valueOf(card.templateCount));
//        StyledTextView period = (StyledTextView) v.findViewById(R.id.period);
//        period.setText(card.startDateTime + "- " + card.endDateTime);
//        StyledTextView name = (StyledTextView) v.findViewById(R.id.user_name);
//        name.setText(user.getName());
//        StyledTextView accessGroup = (StyledTextView) v.findViewById(R.id.access_group);
//        if (card.accessGroups != null && card.accessGroups.size() > 0) {
//            if (card.accessGroups.size() > 1) {
//                accessGroup.setText(card.accessGroups.get(0) + " + " + (card.accessGroups.size() - 1));
//            } else {
//                accessGroup.setText(card.accessGroups.get(0));
//            }
//        }
//    }

    public MobileCardGuideFragment() {
        super();
        setType(ScreenType.MOBILE_CARD_GUIDE);
        TAG = getClass().getSimpleName() + String.valueOf(System.currentTimeMillis());
    }
  /*  private Runnable mRunnableGuide = new Runnable() {
        @Override
        public void run() {
            if (isInValidCheck(null)) {
                return;
            }
            View aoc2 = mRootView.findViewById(R.id.aoc2);
            setAccessOnCardData(aoc2,mUser,mCard);
            View aoc = mRootView.findViewById(R.id.aoc);
            setAccessOnCardData(aoc,mUser,mCard);

//                aoc2.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
            ViewGroup container = (ViewGroup)aoc2.findViewById(R.id.container);
            View card_switch = container.findViewById(R.id.card_switch);
            card_switch.setVisibility(View.INVISIBLE);

            int w = card_switch.getWidth();
            int h = card_switch.getHeight();
            if (w==0 || h ==0) {
                mHandler.postDelayed(mRunnableGuide,1000);
                return;
            }
            int[] position = {0, 0};
            card_switch.getLocationOnScreen(position);
            LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(card_switch.getWidth() + 60, card_switch.getHeight() + 60);
//                LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams itemParam = new FrameLayout.LayoutParams(w, h, Gravity.CENTER);

            FrameLayout containerView = new FrameLayout(mContext);
            containerView.setBackgroundResource(R.drawable.dash);
            SwitchView itemView = new SwitchView(mContext);
            itemView.setSwitch(true);
            containerView.addView(itemView, itemParam);
//            mRootView.addView(containerView, containerParam);
            LinearLayout targetView = (LinearLayout)mRootView.findViewById(R.id.content_container);
            targetView.addView(containerView, containerParam);
            containerView.setX(position[0]);
            containerView.setY(position[1]-60);
        }
    };*/
  private Runnable mRunnableGuide = new Runnable() {
      @Override
      public void run() {
          Log.e(TAG,"mRunnableGuide");
          if (isInValidCheck(null)) {
              return;
          }
          if (mUser == null) {
              mUser = mUserDataProvider.getLoginUserInfo();
          }
          View aoc2 = mRootView.findViewById(R.id.aoc2);
          setAccessOnCardData(aoc2,mUser,mCard);
          View aoc = mRootView.findViewById(R.id.aoc);
          setAccessOnCardData(aoc,mUser,mCard);

//                aoc2.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
          ViewGroup container = (ViewGroup)aoc2.findViewById(R.id.container);
          View card_switch = container.findViewById(R.id.card_switch);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "x:" + card_switch.getX() + " y:" + card_switch.getY());
            View v = (View) card_switch.getParent();
            Log.i(TAG, "p1 x:" + v.getX() + " y:" + v.getY());
            v = (View) v.getParent();
            Log.i(TAG, "p2 x:" + v.getX() + " y:" + v.getY());
            v = (View) v.getParent();
            Log.i(TAG, "p3 x:" + v.getX() + " y:" + v.getY());
            v = (View) v.getParent();
            Log.i(TAG, "p4 x:" + v.getX() + " y:" + v.getY());
        }

          int w = card_switch.getWidth();
          int h = card_switch.getHeight();
          if (w==0 || h ==0) {
              mHandler.postDelayed(mRunnableGuide,1000);
              return;
          }
//          int[] position = {0, 0};
//          card_switch.getLocationOnScreen(position);
//          Log.e(TAG,"position  x:"+ position[0]+" y:"+position[1]);
          LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams(card_switch.getWidth() + 60, card_switch.getHeight() + 60);
//                LinearLayout.LayoutParams containerParam = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
          FrameLayout.LayoutParams itemParam = new FrameLayout.LayoutParams(w, h, Gravity.CENTER);

          FrameLayout containerView = new FrameLayout(mContext);
          containerView.setBackgroundResource(R.drawable.dash);
          SwitchView itemView = new SwitchView(mContext);
          itemView.setSwitch(true);
          containerView.addView(itemView, itemParam);
          FrameLayout targetView = (FrameLayout)mRootView.findViewById(R.id.aoc2_container);
          targetView.addView(containerView, containerParam);

          float x = card_switch.getX();
          View v = (View) card_switch.getParent();
          x = x + v.getX();
          v = (View) v.getParent();
          x = x + v.getX();

          containerView.setX(x);
          containerView.setY(0);
          card_switch.setVisibility(View.INVISIBLE);
      }
  };
    private void initValue() {
        mUser = mUserDataProvider.getLoginUserInfo();
        if (mMobileCardDataProvider == null) {
            mMobileCardDataProvider = MobileCardDataProvider.getInstance(mContext);
        }
        if (mSubToolbar == null) {
            mSubToolbar = (SubToolbar) mRootView.findViewById(R.id.subtoolbar);
            mSubToolbar.init(getActivity());
        }
        mRootView.findViewById(R.id.close_guide).setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mScreenControl.backScreen();
            }
        });
        mHandler.removeCallbacks(mRunnableGuide);
        mHandler.postDelayed(mRunnableGuide,500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setResID(R.layout.fragment_mobilecard_guide);
        super.onCreateView(inflater, container, savedInstanceState);
        if (!mIsReUsed) {
            Bundle bundle = getArguments();
            if (bundle != null) {
                mTotal = bundle.getInt(Setting.TOTAL_COUNT);
                mCard = getExtraData(MobileCard.TAG, savedInstanceState);
            } else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScreenControl.backScreen();
                    }
                }, 1000);
                return null;
            }
            initValue();
            initActionbar(getString(R.string.mobile_card));
//            MobileCardData.MobileCard local = mMobileCardDataProvider.getLocalMobileCard();
            mSubToolbar.setTotal(mTotal);
            mRootView.invalidate();
        }
        return mRootView;
    }


    @Override
    public boolean onBack() {
        if (mSubToolbar != null) {
            if (mSubToolbar.isExpandSearch()) {
                mSubToolbar.setSearchIconfied();
                return true;
            }
        }
        if (super.onBack()) {
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
