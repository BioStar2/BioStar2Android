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

import android.graphics.Rect;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.espresso.util.TreeIterables;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.supremainc.biostar2.popup.Popup;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


public class ExtTest {
    public static final String TAG = "AutoTest";
    public static final int WAIT_POPUP_TIME = 10000;

    public static void w(int time) {
        onView(isRoot()).perform(ExtTest.waitTime(time));
    }

    public static void delEditText(final int viewId) {
        int length = ExtTest.getTextFromEditTextView(viewId).length();
        for (int i = 0; i < length; i++) {
            onView(withId(viewId)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_DEL));
            onView(isRoot()).perform(ExtTest.waitTime(200));
        }
    }

    public static void checkPopupType(boolean positive,boolean reverse,Popup.PopupType type) {
        waitId(R.id.main_container, 60000);
        if (ExtTest.isExist(R.id.main_container)) {
            if (reverse && (Popup.PopupType)getTag(R.id.main_container) == type) {
                onView(withId(R.id.main_container))
                        .check((ViewAssertions.matches(setResult(false))));
            } else if (!reverse && (Popup.PopupType)getTag(R.id.main_container) != type) {
                onView(withId(R.id.main_container))
                        .check((ViewAssertions.matches(setResult(false))));
            }
            checkPopup(positive);
        }
    }
    public static void checkPopup(boolean positive) {
        w(500);
        if (ExtTest.isExist(R.id.waitpopup_container)) {
            onView(isRoot()).perform(ExtTest.waitInvisibleGoneId(R.id.waitpopup_container, ExtTest.WAIT_POPUP_TIME));
            w(1000);
        }

        if (ExtTest.isExist(R.id.main_container)) {
            if (positive) {
                if (ExtTest.isExist(R.id.positive)) {
                    onView(withId(R.id.positive)).perform(ViewActions.click());
                } else {
                    onView(withId(R.id.negative)).perform(ViewActions.click());
                }
            } else {
                if (ExtTest.isExist(R.id.negative)) {
                    onView(withId(R.id.negative)).perform(ViewActions.click());
                } else {
                    onView(withId(R.id.positive)).perform(ViewActions.click());
                }
            }
        }
    }

    public static void waitSwipyRefresh() {
        w(3000);
        onView(withId(R.id.swipe_refresh_layout)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(SwipyRefreshLayout.class);
            }

            @Override
            public String getDescription() {
                return "get text from edit text view:";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                SwipyRefreshLayout swipy = (SwipyRefreshLayout) view;
                while (swipy.isRefreshing()) {
                    uiController.loopMainThreadUntilIdle();
                    uiController.loopMainThreadForAtLeast(1000);
                }
            }
        });
    }

    public static void waitIdleBack() {
        waitIdle();
        pressBack();
    }
    public static void waitIdle() {
        w(1000);
        onView(isRoot()).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "get text from edit text view:";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                uiController.loopMainThreadForAtLeast(500);
            }
        });
    }
    public static boolean isExist(final int viewId) {
        final Boolean[] result = {false};
        onView(isRoot()).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "get text from edit text view:" + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                final Matcher<View> viewMatcher = withId(viewId);
                uiController.loopMainThreadForAtLeast(500);

                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child) && child.getVisibility() == View.VISIBLE) {
                        result[0] = true;
                        break;
                    }
                }
            }
        });
        return result[0];
    }

    public static Object getTag(final int viewId) {
        final Object[] result = {null};
        onView(isRoot()).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "get text from edit text view:" + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadUntilIdle();
                final Matcher<View> viewMatcher = withId(viewId);
                uiController.loopMainThreadForAtLeast(500);

                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) {
                        result[0] = child.getTag();
                    }
                }
            }
        });
        return result[0];
    }

    public static String getTextFromEditTextView(final int viewId) {
        final String[] result = {null};
        onView(withId(viewId)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(EditText.class);
            }

            @Override
            public String getDescription() {
                return "get text from edit text view:" + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                EditText et = (EditText) view;
                if (et.getText() == null) {
                    result[0] = "";
                    return;
                }
                String text = et.getText().toString();
                if (text == null) {
                    result[0] = "";
                    return;
                }
                result[0] = text;
                et.setSelection(text.length());
            }
        });
        return result[0];
    }

    public static int checkListCount(final int viewId) {
        final Integer[] result = {0};
        onView(withId(viewId)).check(ViewAssertions.matches(new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                ListView listView = (ListView) view;
                result[0] = listView.getCount();
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        }));
        return result[0];
    }

    public static String getTextFromTextView(final int viewId) {
        final String[] result = {null};
        onView(withId(viewId)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "get text from text view:" + viewId;
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view;
                result[0] = tv.getText().toString();
            }
        });
        return result[0];
    }

    public static ViewAction waitInvisibleGoneId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait view id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);
//                Log.e("test", "waitInvisibleGoneId view id:" + viewId);
                uiController.loopMainThreadForAtLeast(200);
                do {
                    View find = null;
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
//                        if (viewMatcher.matches(child) && child.getVisibility() == View.VISIBLE) {
//                          Log.e("test","original view id:"+viewId+" child view id:"+child.getId());
                        if (viewMatcher.matches(child)) {
                            find = child;
                            Log.e("test","macthch  visible: "+child.getVisibility());
                        }

                    }
                    if (find != null && find.getVisibility() == View.VISIBLE) {
//                            Log.e("test", "find startTime:" + startTime);
//                            Log.e("test","find endTime:"+endTime);
//                            Log.e("test","find System.currentTimeMillis():"+System.currentTimeMillis());
                    } else {
                        Log.e("test", "waitInvisibleGoneId wait time :" + (System.currentTimeMillis() - startTime) / 1000 + " id:" + viewId);
                        return;
                    }
//                    Log.e("test","startTime:"+startTime);
//                    Log.e("test","endTime:"+endTime);
                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                Log.e("test", "waitInvisibleGoneId time out :" + (System.currentTimeMillis() - startTime) / 1000 + " id:" + viewId);
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static ViewAction waitId(final int viewId, final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait view id <" + viewId + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                final Matcher<View> viewMatcher = withId(viewId);
                Log.e("test", "waitId original view id:" + viewId);
                do {
                    for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
//                        if (viewMatcher.matches(child) && child.getVisibility() == View.VISIBLE) {
                        //  Log.e("test","original view id:"+viewId+" child view id:"+child.getId());
                        if (viewMatcher.matches(child) && child.getVisibility() == View.VISIBLE) {
//                            Log.e("test", "find startTime:" + startTime);
//                            Log.e("test","find endTime:"+endTime);
//                            Log.e("test","find System.currentTimeMillis():"+System.currentTimeMillis());
                            Log.e("test", "waitId wait time :" + (System.currentTimeMillis() - startTime) / 1000 + " id:" + viewId);
                            return;
                        }
                    }
//                    Log.e("test","startTime:"+startTime);
//                    Log.e("test","endTime:"+endTime);
                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);

                Log.e("test", "waitId time out :" + (System.currentTimeMillis() - startTime) / 1000 + " id:" + viewId);
                throw new PerformException.Builder()
                        .withActionDescription(this.getDescription())
                        .withViewDescription(HumanReadables.describe(view))
                        .withCause(new TimeoutException())
                        .build();
            }
        };
    }

    public static ViewAction waitTime(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait view id <" + "> during " + millis + " millis.";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                uiController.loopMainThreadUntilIdle();
                final long startTime = System.currentTimeMillis();
                final long endTime = startTime + millis;
                do {
                    uiController.loopMainThreadForAtLeast(50);
                }
                while (System.currentTimeMillis() < endTime);
            }
        };
    }

    public static Matcher<View> isReverseDisplayed() {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("is displayed on the screen to the user");
            }

            @Override
            public boolean matchesSafely(View view) {
                return !(view.getGlobalVisibleRect(new Rect())
                        && ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE).matches(view));
            }
        };
    }

    public static Matcher<View> hasImeInputType(final int inputType) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has ime action: ");
            }

            @Override
            public boolean matchesSafely(View view) {
                EditorInfo editorInfo = new EditorInfo();
                InputConnection inputConnection = view.onCreateInputConnection(editorInfo);
                if (inputConnection == null) {
                    return false;
                }
                int result =  editorInfo.inputType & inputType;
                if (result != inputType) {
                    return false;
                }
                return true;
            }
        };
    }

    public static Matcher<View> setResult(final boolean result) {
        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("has ime action: ");
            }

            @Override
            public boolean matchesSafely(View view) {
                return result;
            }
        };
    }

    public static void checkLogin() {
        w(5000);
        ExtTest.checkPopup(false);
        if (ExtTest.isExist(R.id.subdomain)) {

            onView(withId(R.id.subdomain)).perform(ViewActions.click());

            w(1100);
            ExtTest.delEditText(R.id.input);

            w(1100);
            onView(withId(R.id.input)).perform(ViewActions.typeText("alpha")); // subdomain
            onView(withId(R.id.input)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.login_id)).perform(ViewActions.click());

            w(1100);
            ExtTest.delEditText(R.id.login_id);

            w(1100);
            onView(withId(R.id.login_id)).perform(ViewActions.typeText("isbaek1")); //id
            onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.password)).perform(ViewActions.typeText("1234qwer+")); //password
            onView(withId(R.id.login_id)).perform(ViewActions.closeSoftKeyboard());

            w(1100);
            onView(withId(R.id.login)).perform(ViewActions.click());
            //  onView(isRoot()).perform(ExtTest.waitId(R.id.main_menu, 10000));

            ExtTest.checkPopup(false);
            onView(withId(R.id.display_time)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            Log.e(TAG, "Login Success");
            w(1100);
            ExtTest.checkPopup(false);
        } else {
            ExtTest.checkPopup(false);
        }
        w(1100);
    }

    public static boolean search(String content) {
        Log.e(TAG, "case: User/" + content+"를 찾는다.");
                onView(withId(R.id.searchbar)).perform(ViewActions.click());
        onView(withId(R.id.search_src_text)).perform(ViewActions.typeText("4294967294"));
        onView(withId(R.id.searchbar)).perform(ViewActions.pressKey(KeyEvent.KEYCODE_ENTER));
        ExtTest.waitSwipyRefresh();
        if (ExtTest.checkListCount(R.id.listview) > 0) {
            Log.e(TAG, "result: "+content+"를 찾았다");
            return true;
        } else {
            Log.e(TAG, "result: "+content+"를 찾지못했다");
            return false;
        }
    }
}
