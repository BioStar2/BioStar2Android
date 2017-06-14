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
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.supremainc.biostar2.sdk.models.v1.permission.CloudRole;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;
import com.supremainc.biostar2.widget.popup.Popup;

import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class UserTestUtil {
    public static final String TAG = "AutoTest";
    public static final int WAIT_POPUP_TIME = 10000;

    public static void w(int time) {
        onView(isRoot()).perform(ExtTest.waitTime(time));
    }

    public static void checkUserList(final int viewId) {
        onView(withId(viewId)).check(ViewAssertions.matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                try {
                    ListUser user = (ListUser) listView.getItemAtPosition(0);
                } catch (Exception e) {
                    Log.e(TAG, "fail Not match UserList");
                    return false;
                }
                //  ListUser user = (ListUser) listView.getItemAtPosition(0);
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
    }

    public static void userDelete() {
        onView(withId(R.id.action_delete)).perform(ViewActions.click());
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(ListUser.class))))
                .atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.action_delete_confirm)).perform(ViewActions.click());
        ExtTest.checkPopup(true);
        ExtTest.checkPopupType(false, true, Popup.PopupType.ALERT);
    }

    public static void saveTelephone() {
        w(1000);
//        onView(withId(R.id.telephone_edit)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.user_id))
                .check((ViewAssertions.matches(ExtTest.hasImeInputType(InputType.TYPE_CLASS_PHONE))));
        ExtTest.delEditText(R.id.telephone);
        onView(withId(R.id.telephone)).perform(ViewActions.typeText("!@#$^&*()+8201012345678"));
        onView(withId(R.id.telephone)).perform(ViewActions.closeSoftKeyboard());
        w(1000);
    }

    public static void searchDeleteUserID(String content, boolean checkEmailTelephone) {
        ExtTest.waitSwipyRefresh();
        w(1000);
        if (content == null) {
            content = "4294967294";
        }
        Log.e(TAG, "case: User/" + content + "를 가진 유져를 찾는다.");
        if (ExtTest.search(content)) {
            onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(ListUser.class))))
                    .atPosition(0)
                    .perform(ViewActions.click());
            if (checkEmailTelephone) {
//                ExtTest.waitId(R.id.email_go, 60000);
                String emailContent = ExtTest.getTextFromTextView(R.id.email);
                String phoneContent = ExtTest.getTextFromTextView(R.id.telephone);
                if (emailContent == null || emailContent.isEmpty() || phoneContent == null || phoneContent.isEmpty()) {
//                    onView(withId(R.id.email_go))
//                            .check((ViewAssertions.matches(ExtTest.setResult(false))));
                }
//                if (telephoneContent != null) {
//                    onView(withId(R.id.telephone_go)).perform(ViewActions.click());
//                }
            }
            w(2000);
            pressBack();
            Log.e(TAG, "result: User List와 User 정보, User 검색 시 정상 동작됨을 확인한다.");

            Log.e(TAG, "case: User/4294967294 id를 가진 유져를 삭제한다.");
            UserTestUtil.userDelete();
            Log.e(TAG, "result: User/4294967294 id를 가진 유져를 삭제한다.");
            Log.e(TAG, "case: User/Search창을 닫는다.");
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            w(1000);
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            ExtTest.waitSwipyRefresh();
            Log.e(TAG, "result: Search창을 닫았다.");
            Log.e(TAG, "case: User/삭제모드에서 나온다");
            pressBack();
            ExtTest.waitId(R.id.action_add, 3000);
            Log.e(TAG, "result: 삭제모드에서 나왔다");
        } else {
            onView(withId(R.id.listview))
                    .check((ViewAssertions.matches(ExtTest.setResult(false))));
        }
    }

    public static void deleteUserID() {
        ExtTest.waitSwipyRefresh();
        w(1000);
        if (ExtTest.search("4294967294")) {
            Log.e(TAG, "case: User/4294967294 id를 가진 유져를 삭제한다.");
            UserTestUtil.userDelete();
            Log.e(TAG, "result: User/4294967294 id를 가진 유져를 삭제한다.");
            Log.e(TAG, "case: User/Search창을 닫는다.");
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            w(1000);
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            ExtTest.waitSwipyRefresh();
            Log.e(TAG, "result: Search창을 닫았다.");
            Log.e(TAG, "case: User/삭제모드에서 나온다");
            pressBack();
            ExtTest.waitId(R.id.action_add, 3000);
            Log.e(TAG, "result: 삭제모드에서 나왔다");
        } else {
            w(1000);
            Log.e(TAG, "case: User/Search창을 닫는다.");
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            w(1000);
            onView(withId(R.id.search_close_btn)).perform(ViewActions.click());
            Log.e(TAG, "result: Search창을 닫았다.");
            ExtTest.waitSwipyRefresh();
        }
    }

    public static void saveUserID() {
        Log.e(TAG, "case: User/User 등록/UserID입력/1/3. User ID를 선택한다.");
        w(1000);
        onView(withId(R.id.user_id)).perform(ViewActions.click());
        w(1000);
//        onView(withId(R.id.user_id))
//                .check(ViewAssertions.matches(ViewMatchers.hasImeAction(EditorInfo.IME_ACTION_DONE)));
        onView(withId(R.id.user_id))
                .check((ViewAssertions.matches(ExtTest.hasImeInputType(InputType.TYPE_CLASS_NUMBER))));
        Log.e(TAG, "result: 3) 숫자 키패드가 표시되며 숫자만 입력 가능한지 확인한다.");

        Log.e(TAG, "case: User/User 등록/UserID입력/2/1. 최대 입력값을 입력한다. 4294967294");
        w(1000);
        onView(withId(R.id.user_id)).perform(ViewActions.typeText("4294967294")); //id
        w(1000);
        onView(withId(R.id.user_id)).perform(ViewActions.closeSoftKeyboard());
    }

    public static void saveName() {
        Log.e(TAG, "case: User/User 등록/Name입력/1~4 해당 데이터를 입력한다./영문+국문+숫자+특수문자 혼용 9부터 49자");
        w(1000);
        onView(withId(R.id.name)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.name);
        onView(withId(R.id.name)).perform(ViewActions.typeText("123456!@#$^&*()_abcdefghijklmopqrst7810"));
        onView(withId(R.id.name)).perform(ViewActions.typeText("UVWXYX7899999999999999999"));
        int length = ExtTest.getTextFromEditTextView(R.id.name).length();
        if (length > 48) {
            onView(withId(R.id.name))
                    .check((ViewAssertions.matches(ExtTest.setResult(false))));
        } else {
            Log.e(TAG, "result: User/User 등록/Name입력/1~4 해당 데이터를 입력한다./1. 최대 48자 초과 시 입력되지 않음을 확인한다.");
        }
        onView(withId(R.id.name)).perform(ViewActions.closeSoftKeyboard());
    }

    public static void saveEmail(String content) {
        w(1000);
//        onView(withId(R.id.email_edit)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.email);
        onView(withId(R.id.email)).perform(ViewActions.typeText(content));
        onView(withId(R.id.email)).perform(ViewActions.closeSoftKeyboard());
    }

    public static void addUserCheck(String title) {
        Log.e(TAG, "case: User/User 등록/UserID입력/1/2. 오른쪽 상단의 +버튼을 클릭한다.");
        w(1000);
        onView(withId(R.id.action_add)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.title_text)).check(ViewAssertions.matches(ViewMatchers.withText(title)));
        Log.e(TAG, "result: 2) User 추가 화면으로 이동한다.");
    }

    public static void selectOperation(int position) {
        if (position == -1) {
            Log.e(TAG, "case: User/User 등록/operator/BioStar Operator none선택.");
//            onView(withId(R.id.operator_edit)).perform(ViewActions.click());
            w(1000);
            onView(withId(R.id.action_delete)).perform(ViewActions.click());
            onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(CloudRole.class))))
                    .atPosition(0)
                    .perform(ViewActions.click());
            onView(withId(R.id.action_delete_confirm)).perform(ViewActions.click());
            ExtTest.checkPopup(true);
            w(1000);
            pressBack();
            w(1000);
            pressBack();
            w(1000);
            ExtTest.waitInvisibleGoneId(R.id.login_id, 3000);
            ExtTest.waitInvisibleGoneId(R.id.password, 3000);
            Log.e(TAG, "result: login_id와 password가 나오는걸 확인한다.");
            return;
        }
        Log.e(TAG, "case: User/User 등록/operator/BioStar Operator 권한선택.");
//        onView(withId(R.id.operator_edit)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.action_add)).perform(ViewActions.click());
        //TODO checking? receive data? or onData is process?
        onData(Matchers.allOf(Matchers.is(Matchers.instanceOf(CloudRole.class))))
                .atPosition(position)
                .perform(ViewActions.click());
        ExtTest.checkPopup(true);
        w(1000);
        pressBack();
        ExtTest.waitId(R.id.login_id, 3000);
        ExtTest.waitId(R.id.password, 3000);
        Log.e(TAG, "result: login_id와 password가 나오는걸 확인한다.");
    }

    public static void checkMaxLenght(int resID, int maxCount) {
        int length = ExtTest.getTextFromEditTextView(resID).length();
        if (length > maxCount || length < 1) {
            onView(withId(resID))
                    .check((ViewAssertions.matches(ExtTest.setResult(false))));
        } else {
            Log.e(TAG, "result: 최대" + maxCount + " 초과 시 입력되지 않음을 확인한다. 글자가 입력되었음을 확인한다.");
        }
    }

    public static void inputPassword() {
        w(1000);
//        onView(withId(R.id.detail_scroll)).perform(ViewActions.swipeUp());
        w(1000);
        onView(withId(R.id.password_edit)).perform(ViewActions.click());
        ExtTest.waitId(R.id.main_container, 60000);

        Log.e(TAG, "case: User/User 등록/password입력/패스워드 틀리게입력");
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKL"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("ABCDEFGHIJKLMOPQRST"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("ABCDEFGHIJKLMOPQRST"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKL"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
        w(1000);
        Log.e(TAG, "result: 입력한 Password로 설정이 불가능해야한다.");

        Log.e(TAG, "case: User/User 등록/password입력/영문 대문자");
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKL"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("ABCDEFGHIJKLMOPQRST"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKL"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("ABCDEFGHIJKLMOPQRST"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
        w(1000);
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        Log.e(TAG, "result: 입력한 영문 대문자가 표시,입력한 Password로 설정이 불가능해야한다.");

        Log.e(TAG, "case: User/User 등록/password입력/영문 소문자");
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjkl"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("abcdefghijklmopqrst"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjkl"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("abcdefghijklmopqrst"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        w(1000);
        Log.e(TAG, "result: 입력한 숫자 표시,입력한 Password로 설정이 불가능해야한다.");

        Log.e(TAG, "case: User/User 등록/password입력/숫자");
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("1234567810123456781012345678101234567810"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("1234567899999999999999999"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("1234567810123456781012345678101234567810"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("1234567899999999999999999"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        w(1000);
        Log.e(TAG, "result: 입력한 숫자 표시,입력한 Password로 설정이 불가능해야한다.");

        Log.e(TAG, "case: User/User 등록/password입력/특수기호");
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("!@#$^&*()_!@#$^&*()_!@#$^&*()_!@#$^&*()_"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("!@#$^&*(+++++++++++"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("!@#$^&*()_!@#$^&*()_!@#$^&*()_!@#$^&*()_"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("!@#$^&*(+++++++++++"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
        onView(withId(R.id.action_save)).perform(ViewActions.click());
        ExtTest.checkPopupType(false, false, Popup.PopupType.ALERT);
        w(1000);
        Log.e(TAG, "result: 입력한 특수기호 표시,입력한 Password로 설정이 불가능해야한다.");

        savePassword();
        w(1000);
    }

    public static void saveLoginID() {
        Log.e(TAG, "case: User/User 등록/loginID/1~4 해당 데이터를 입력한다.혼합");
        w(1000);
        onView(withId(R.id.login_id)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.login_id);
        onView(withId(R.id.login_id)).perform(ViewActions.typeText("Eabcde1234556667"));
        onView(withId(R.id.login_id)).perform(ViewActions.typeText("FGHIJKLMNOPQRSTUWXYZfghijklmnopqrstuwxyz"));
        checkMaxLenght(R.id.login_id, 32);
        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
    }

    public static void savePassword() {
        Log.e(TAG, "case: User/User 등록/password/1~4 해당 데이터를 입력한다.혼합");
        w(1000);
        onView(withId(R.id.password)).perform(ViewActions.click());
        ExtTest.delEditText(R.id.password);
        onView(withId(R.id.password)).perform(ViewActions.typeText("!@#$^&*()_+1234567890ABCDEabcde"));
        onView(withId(R.id.password)).perform(ViewActions.typeText("FGHIJKLMNOPQRSTUWXYZfghijklmnopqrstuwxyz"));
        checkMaxLenght(R.id.password, 32);
        onView(withId(R.id.password)).perform(ViewActions.closeSoftKeyboard());
        onView(withId(R.id.password)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.delEditText(R.id.password_confirm);
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("!@#$^&*()_!@#$^&*()_!@#$^&*()_!@#$^&*()_"));
        onView(withId(R.id.password_confirm)).perform(ViewActions.typeText("!@#$^&*(+++++++++++"));
        checkMaxLenght(R.id.password_confirm, 32);
        onView(withId(R.id.positive)).perform(ViewActions.click());
    }


    public static void inputEdit(int resID, int maxCount, int clickResID) {
        Log.e(TAG, "case: User/User 등록//1~4 해당 데이터를 입력한다.대문자");
        w(1000);

        onView(withId(clickResID)).perform(ViewActions.click());
        ExtTest.delEditText(resID);
        onView(withId(resID)).perform(ViewActions.typeText("QWERTYUIOPASDFGHJKLZXCVBNMQWERTYUIOPASDFGHJKL"));
        onView(withId(resID)).perform(ViewActions.typeText("ABCDEFGHIJKLMOPQRST"));
        checkMaxLenght(resID, maxCount);
        onView(withId(resID)).perform(ViewActions.closeSoftKeyboard());


        Log.e(TAG, "case: User/User 등록//1~4 해당 데이터를 입력한다.소문자");
        w(1000);

        onView(withId(clickResID)).perform(ViewActions.click());
        ExtTest.delEditText(resID);
        onView(withId(resID)).perform(ViewActions.typeText("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjkl"));
        onView(withId(resID)).perform(ViewActions.typeText("abcdefghijklmopqrst"));
        checkMaxLenght(resID, maxCount);
        onView(withId(resID)).perform(ViewActions.closeSoftKeyboard());


        Log.e(TAG, "case: User/User 등록//1~4 해당 데이터를 입력한다.숫자");
        w(1000);
        onView(withId(clickResID)).perform(ViewActions.click());
        ExtTest.delEditText(resID);
        onView(withId(resID)).perform(ViewActions.typeText("1234567810123456781012345678101234567810"));
        onView(withId(resID)).perform(ViewActions.typeText("1234567899999999999999999"));
        checkMaxLenght(resID, maxCount);
        onView(withId(resID)).perform(ViewActions.closeSoftKeyboard());


        Log.e(TAG, "case: User/User 등록//1~4 해당 데이터를 입력한다.특수문자");
        w(1000);
        onView(withId(clickResID)).perform(ViewActions.click());
        ExtTest.delEditText(resID);
        onView(withId(resID)).perform(ViewActions.typeText("!@#$^&*()_!@#$^&*()_!@#$^&*()_!@#$^&*()_"));
        onView(withId(resID)).perform(ViewActions.typeText("!@#$^&*(+++++++++++"));
        checkMaxLenght(resID, maxCount);
        onView(withId(resID)).perform(ViewActions.closeSoftKeyboard());


        Log.e(TAG, "case: User/User 등록//1~4 해당 데이터를 입력한다.혼합");
        w(1000);
        onView(withId(clickResID)).perform(ViewActions.click());
        ExtTest.delEditText(resID);
        onView(withId(resID)).perform(ViewActions.typeText("!@#$^&*()_+1234567890ABCDEabcde"));
        onView(withId(resID)).perform(ViewActions.typeText("FGHIJKLMNOPQRSTUWXYZfghijklmnopqrstuwxyz"));
        checkMaxLenght(resID, maxCount);
        onView(withId(resID)).perform(ViewActions.closeSoftKeyboard());

        saveLoginID();

        //        Log.e(TAG, "case: User/User 등록/Name입력/1~4 해당 데이터를 입력한다./1. 국문 가부터 49자");
//        w(1000);
//        onView(withId(R.id.name)).perform(ViewActions.click());
//        ExtTest.delEditText(R.id.name);
//        onView(withId(R.id.name)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_LANGUAGE_SWITCH));
//        onView(withId(R.id.name)).perform(ViewActions.typeText("ㄱ나다라마마마마마마바바바바바사사사사사자자자자자자자자자자다다다다다다다다다하"));
//        onView(withId(R.id.name)).perform(ViewActions.typeText("카타차파마바자다가가가가가가가"));
//        length = ExtTest.getTextFromEditTextView(R.id.name).length();
//        if (length > 48) {
//            onView(withId(R.id.name))
//                    .check((ViewAssertions.matches(ExtTest.setResult(false))));
//        } else {
//            Log.e(TAG, "result: User/User 등록/Name입력/1~4 해당 데이터를 입력한다./1. 최대 48자 초과 시 입력되지 않음을 확인한다.");
//        }
//        onView(withId(R.id.name)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_LANGUAGE_SWITCH));
//        onView(withId(R.id.name)).perform(ViewActions.closeSoftKeyboard());
    }
}
