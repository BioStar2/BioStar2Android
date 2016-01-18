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

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    public static final String TAG = "AutoTest";
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);
    public void w(int time) {
        onView(isRoot()).perform(ExtTest.waitTime(time));
    }
    @Test
    public void login() {
        w(3000);
        if (ExtTest.isExist(R.id.subdomain)) {

            onView(withId(R.id.subdomain)).perform(ViewActions.click());

            w(1100);
            ExtTest.delEditText(R.id.input);

            w(1100);
            onView(withId(R.id.input)).perform(ViewActions.typeText("yoursubdomain")); // subdomain
            onView(withId(R.id.input)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.login_id)).perform(ViewActions.click());

            w(1100);
            ExtTest.delEditText(R.id.login_id);

            w(1100);
            onView(withId(R.id.login_id)).perform(ViewActions.typeText("yourid")); //id
            onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.password)).perform(ViewActions.typeText("yourpassword1#")); //password
            onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.login)).perform(ViewActions.click());
            //  onView(isRoot()).perform(ExtTest.waitId(R.id.main_menu, 10000));

            ExtTest.checkPopup(false);
            onView(withId(R.id.display_time)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            Log.e(TAG, "Login Success");
            w(1100);
            ExtTest.checkPopup(false);
        }
    }
}