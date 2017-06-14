package com.supremainc.biostar2.activity;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.supremainc.biostar2.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() {
        ViewInteraction styledTextView = onView(
                allOf(withId(R.id.subdomain), isDisplayed()));
        styledTextView.perform(click());

        ViewInteraction styledEditTextView = onView(
                allOf(withId(R.id.input),
                        withParent(withId(R.id.container_input)),
                        isDisplayed()));
        styledEditTextView.perform(replaceText("alphatest2"), closeSoftKeyboard());

        ViewInteraction styledTextView2 = onView(
                allOf(withId(R.id.address), withText("biostar2.com"), isDisplayed()));
        styledTextView2.perform(click());

        ViewInteraction styledEditTextView2 = onView(
                allOf(withId(R.id.input), withText("https://api.biostar2.com/"),
                        withParent(withId(R.id.container_input)),
                        isDisplayed()));
        styledEditTextView2.perform(click());

        ViewInteraction styledEditTextView3 = onView(
                allOf(withId(R.id.input), withText("https://api.biostar2.com/"),
                        withParent(withId(R.id.container_input)),
                        isDisplayed()));
        styledEditTextView3.perform(replaceText("https://apitest.biostar2.com/"), closeSoftKeyboard());

        ViewInteraction styledEditTextView4 = onView(
                allOf(withId(R.id.input), withText("https://apitest.biostar2.com/"),
                        withParent(withId(R.id.container_input)),
                        isDisplayed()));
        styledEditTextView4.perform(pressImeActionButton());

        ViewInteraction styledEditTextView5 = onView(
                allOf(withId(R.id.login_id), isDisplayed()));
        styledEditTextView5.perform(replaceText("test1234"), closeSoftKeyboard());

        ViewInteraction styledEditTextView6 = onView(
                allOf(withId(R.id.login_id), withText("test1234"), isDisplayed()));
        styledEditTextView6.perform(pressImeActionButton());

        ViewInteraction styledEditTextView7 = onView(
                allOf(withId(R.id.password), isDisplayed()));
        styledEditTextView7.perform(replaceText("test1234"), closeSoftKeyboard());

        ViewInteraction styledEditTextView8 = onView(
                allOf(withId(R.id.password), withText("test1234"), isDisplayed()));
        styledEditTextView8.perform(pressImeActionButton());

        ViewInteraction menuItemView = onView(
                allOf(withClassName(is("com.supremainc.biostar2.view.MenuItemView")),
                        withParent(withId(R.id.main_menu_first_line)),
                        isDisplayed()));
        menuItemView.perform(click());

        ViewInteraction linearLayout = onView(
                allOf(withId(R.id.user_container),
                        childAtPosition(
                                allOf(withId(R.id.listview),
                                        withParent(withId(R.id.swipe_refresh_layout))),
                                1),
                        isDisplayed()));
        linearLayout.perform(click());

        pressBack();

        ViewInteraction linearLayout2 = onView(
                allOf(withId(R.id.user_container),
                        childAtPosition(
                                allOf(withId(R.id.listview),
                                        withParent(withId(R.id.swipe_refresh_layout))),
                                6),
                        isDisplayed()));
        linearLayout2.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
