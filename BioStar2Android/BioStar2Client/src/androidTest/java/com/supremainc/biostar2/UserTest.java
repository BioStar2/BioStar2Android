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
package com.supremainc.biostar2;


import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.supremainc.biostar2.activity.LoginActivity;
import com.supremainc.biostar2.widget.popup.Popup;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UserTest {
    public static final String TAG = "AutoTest";
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);

    public void w(int time) {
        onView(isRoot()).perform(ExtTest.waitTime(time));
    }

    /**
     * User / User 등록
     */
    @Test
    public void user() {
        ExtTest.checkLogin();
        w(1000);
        Log.e(TAG, "case: User/User 등록/UserID입력/1/1. User를 선택한다.");
//        onView(withId(R.id.main_user)).perform(ViewActions.click());
        //inputTest();

        w(1000);
        onView(withId(R.id.listview)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        ExtTest.waitSwipyRefresh();
        UserTestUtil.checkUserList(R.id.listview);
        Log.e(TAG, "result: 1) All Users List로 이동한다.");
//        UserTestUtil.deleteUserID();

//        inputTelephone();
//        inputEmail();
//        inputUserID();
//        inputUserName();
//        inputOperator();


//        Log.e(TAG, "result: User 추가 화면으로 이동한다.");
//
//        Log.e(TAG, "User/User 등록/UserID입력/3. User ID를 선택한다.");
//        w(1000);
//        onView(withId(R.id.user_id)).perform(ViewActions.click());
//
//
//        w(1000);
//        onView(withId(R.id.user_id)).perform(ViewActions.click());
//        w(1000);
//
//
//        onView(withId(R.id.input)).perform(ViewActions.typeText("alpha")); // subdomain
//        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
//        onView(isRoot()).perform(ExtTest.waitTime(1100));
//        onView(withId(R.id.login_id)).perform(ViewActions.typeText("isbaek")); //id
//        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
//        onView(isRoot()).perform(ExtTest.waitTime(1100));
//        onView(withId(R.id.password)).perform(ViewActions.typeText("1234Qwer+")); //password
//        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
//        onView(isRoot()).perform(ExtTest.waitTime(1100));
//        onView(withId(R.id.login)).perform(ViewActions.click());
//        onView(isRoot()).perform(ExtTest.waitId(R.id.main_menu, 10000));
//        onView(withId(R.id.main_menu)).check(ViewAssertions.matches(isDisplayed()));
    }

    private void inputTest() {
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        UserTestUtil.selectOperation(0);
        w(1000);
        //   onView(withId(R.id.detail_scroll)).perform(ViewActions.swipeUp());
        w(5000);
        onView(withId(R.id.password_edit)).perform(ViewActions.click());
        ExtTest.waitId(R.id.main_container, 60000);
    }

    private void inputUserName() {
        Log.e(TAG, "start: inputUserName");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        UserTestUtil.inputEdit(R.id.name, 48, R.id.name);
        UserTestUtil.saveName();
        UserTestUtil.saveUserID();
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 1) Apply 시 정상적으로 저장됨을 확인한다.");

        Log.e(TAG, "case: User/User 등록/Name입력/1. 입력한 Name이 정상적으로 등록됨을 확인한다.");
        UserTestUtil.searchDeleteUserID("4294967294", false);
        Log.e(TAG, "result:User/User 등록/Name입력/1.  User 정보, User 검색 시 정상 동작됨을 확인한다.");
        Log.e(TAG, "end: inputUserName");
    }


    private void inputUserID() {
        Log.e(TAG, "start: inputUserID");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        UserTestUtil.saveUserID();
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 1) Apply 시 정상적으로 저장됨을 확인한다.");


        ExtTest.waitSwipyRefresh();
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));

        Log.e(TAG, "case: User/User 등록/UserID입력/2/2. 최대 입력값 이상 입력한다. 4294967295");
        onView(withId(R.id.user_id)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.user_id)).perform(ViewActions.typeText("4294967295")); //id
        w(1000);
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 2) Invalid User ID 팝업이 발생한다.");

        Log.e(TAG, "case: User/User 등록/UserID입력/3/1. 이미 등록 된 사용자와 동일한 ID를 입력한다. 4294967294");
        onView(withId(R.id.user_id)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.user_id)).perform(ViewActions.typeText("4294967294")); //id
        w(1000);
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 1) Duplicate User 팝업이 발생한다.");

        Log.e(TAG, "case: User/User 등록/UserID입력/4/1.  ID(필수항목)를 입력하지 않는다. 2. Apply를 누른다.");
        onView(withId(R.id.user_id)).perform(ViewActions.click());
        w(1000);
        ExtTest.delEditText(R.id.user_id);
        w(1000);
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 1) You must enter ID  팝업이 발생한다.");

        pressBack();
        Log.e(TAG, "case: User/User 등록/UserID입력/5/1. 입력한 ID가 정상적으로 등록됨을 확인한다.");
        UserTestUtil.searchDeleteUserID("4294967294", false);
        Log.e(TAG, "result: User/User 등록/UserID입력/5/1. 입력한 ID가 정상적으로 등록됨을 확인한다.");
        Log.e(TAG, "end: inputUserID");
    }

    private void inputEmail() {
        Log.e(TAG, "start: inputEmail");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        UserTestUtil.saveUserID();
        UserTestUtil.saveName();
        UserTestUtil.saveTelephone();

        Log.e(TAG, "case: User/User 등록/email 입력/ 유효하지 않은 Email 주소를 입력한다.");
        UserTestUtil.saveEmail("123456!@#$^&*()_abcdefghijklmopqrst7810");
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        Log.e(TAG, "result: Email Address Invalid 팝업이 발생한다.");

        Log.e(TAG, "case: User/User 등록/email 입력/ 입력한 Email이 정상적으로 등록됨을 확인한다.");
        UserTestUtil.saveEmail("test@suprema.co.kr");
        w(1000);
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        Log.e(TAG, "result: User/User 등록/email 입력/ 입력한 Email이 정상적으로 등록됨을 확인한다.");
        w(1000);
        UserTestUtil.searchDeleteUserID("test@suprema.co.kr", true);
        Log.e(TAG, "end: inputEmail");
    }

    private void inputTelephone() {
        Log.e(TAG, "start: inputTelephone");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        UserTestUtil.saveUserID();
        UserTestUtil.saveEmail("test@suprema.co.kr");
        UserTestUtil.saveTelephone();
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        UserTestUtil.searchDeleteUserID("4294967294", true);
        Log.e(TAG, "end: inputTelephone");
    }

    private void inputOperator() {
        Log.e(TAG, "start: inputOperator");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));
        Log.e(TAG, "case: User/User 등록/operator/BioStar Operator 초기값을 확인한다.");
        onView(withId(R.id.operator)).check(ViewAssertions.matches(ViewMatchers.withText(mActivityRule.getActivity().getResources().getString(R.string.none))));
//        onView(isRoot()).perform(ExtTest.waitInvisibleGoneId(R.id.operator_expand, 3000));
        Log.e(TAG, "result: 초기값은 None으로 출력된다.");

        UserTestUtil.selectOperation(0);
        UserTestUtil.selectOperation(-1);
        UserTestUtil.selectOperation(0);

        UserTestUtil.saveUserID();
//        UserTestUtil.inputEdit(R.id.login_id, 32, R.id.login_id_edit);
        UserTestUtil.inputPassword();
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        w(1000);
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 입력한 특수기호 표시,입력한 Password로 설정이 가능해야한다.");
        UserTestUtil.searchDeleteUserID("4294967294", false);
        Log.e(TAG, "end: inputOperator");
    }

    private void inputGroup() {
        Log.e(TAG, "start: inputGroup");
        UserTestUtil.addUserCheck(mActivityRule.getActivity().getResources().getString(R.string.new_user));


        Log.e(TAG, "case: User/User 등록/operator/BioStar Operator 초기값을 확인한다.");
        onView(withId(R.id.operator)).check(ViewAssertions.matches(ViewMatchers.withText(mActivityRule.getActivity().getResources().getString(R.string.none))));
//        onView(isRoot()).perform(ExtTest.waitInvisibleGoneId(R.id.operator_expand, 3000));
        Log.e(TAG, "result: 초기값은 None으로 출력된다.");

        UserTestUtil.selectOperation(0);
        UserTestUtil.selectOperation(-1);
        UserTestUtil.selectOperation(0);

        UserTestUtil.saveUserID();
//        UserTestUtil.inputEdit(R.id.login_id, 32, R.id.login_id_edit);
        UserTestUtil.inputPassword();
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        w(1000);
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 입력한 특수기호 표시,입력한 Password로 설정이 가능해야한다.");
        UserTestUtil.searchDeleteUserID("4294967294", false);
        Log.e(TAG, "end: inputGroup");
    }


}