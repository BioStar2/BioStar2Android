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
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule =
            new ActivityTestRule<>(LoginActivity.class);
    @Test
    public void login() {
        onView(isRoot()).perform(ExtTest.waitTime(3000));
        onView(withId(R.id.subdomain)).perform(ViewActions.click());
        onView(isRoot()).perform(ExtTest.waitTime(1100));
        onView(withId(R.id.input)).perform(ViewActions.typeText("input your subdomain")); // subdomain
        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
        onView(isRoot()).perform(ExtTest.waitTime(1100));
        onView(withId(R.id.login_id)).perform(ViewActions.typeText("input your id")); //id
        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
        onView(isRoot()).perform(ExtTest.waitTime(1100));
        onView(withId(R.id.password)).perform(ViewActions.typeText("input your password")); //password
        onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());
        onView(isRoot()).perform(ExtTest.waitTime(1100));
        onView(withId(R.id.login)).perform(ViewActions.click());
        onView(isRoot()).perform(ExtTest.waitId(R.id.main_menu, 10000));
        onView(withId(R.id.main_menu)).check(ViewAssertions.matches(isDisplayed()));
    }
}