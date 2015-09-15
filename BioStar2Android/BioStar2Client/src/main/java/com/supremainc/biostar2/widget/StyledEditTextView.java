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
package com.supremainc.biostar2.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.supremainc.biostar2.R;

import java.lang.reflect.Field;

public class StyledEditTextView extends EditText {
    private static final String ROBOTO_BLACK = "roboto_black.ttf";
    private static final String ROBOTO_BOLD = "roboto_bold.ttf";
    private static final String ROBOTO_LIGHT = "roboto_light.ttf";
    private static final String ROBOTO_MEDIUM = "roboto_medium.ttf";
    private static final String ROBOTO_REGULAR = "roboto_regular.ttf";
    private static Typeface robotoBlack;
    private static Typeface robotoBold;
    private static Typeface robotoLight;
    private static Typeface robotoMedium;
    private static Typeface robotoRegular;


    public StyledEditTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyTypeface(context, attrs);
    }

    public StyledEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyTypeface(context, attrs);
    }

    public StyledEditTextView(Context context) {
        super(context);
        loadFont(context);
        setTypeface(robotoRegular);
    }

    private void applyTypeface(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.StyledTextView);
        String typefaceName = arr.getString(R.styleable.StyledTextView_typeface);
        Typeface typeface = null;
        loadFont(context);
        if (typefaceName.equals(ROBOTO_MEDIUM)) {
            typeface = robotoMedium;
        } else if (typefaceName.equals(ROBOTO_LIGHT)) {
            typeface = robotoLight;
        } else if (typefaceName.equals(ROBOTO_BLACK)) {
            typeface = robotoBlack;
        } else if (typefaceName.equals(ROBOTO_BOLD)) {
            typeface = robotoBold;
        } else {
            typeface = robotoRegular;
        }

        try {
            if (typeface != null) {
                setTypeface(typeface);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, R.drawable.shape_cursor_black);
        } catch (Exception ignored) {
        }
    }

    private void loadFont(Context context) {
        if (robotoMedium == null) {
            robotoMedium = Typeface.createFromAsset(context.getAssets(), ROBOTO_MEDIUM);
        }
        if (robotoLight == null) {
            robotoLight = Typeface.createFromAsset(context.getAssets(), ROBOTO_LIGHT);
        }
        if (robotoRegular == null) {
            robotoRegular = Typeface.createFromAsset(context.getAssets(), ROBOTO_REGULAR);
        }
        if (robotoBold == null) {
            robotoBold = Typeface.createFromAsset(context.getAssets(), ROBOTO_BOLD);
        }
        if (robotoBlack == null) {
            robotoBlack = Typeface.createFromAsset(context.getAssets(), ROBOTO_BLACK);
        }
    }

    public String toString2() {
        if (getText() != null && getText().toString() != null) {
            return getText().toString();
        }
        return "";
    }

    public String toValue() {
        if (getText() != null && getText().toString() != null) {
            return getText().toString();
        }
        return null;
    }
}