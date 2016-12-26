/*
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
package com.supremainc.biostar2.view;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.os.Parcel;

public class BaseView extends LinearLayout {
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public BaseView(Context context) {
        super(context);
        initView(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    protected void setTextView(int resID, String content) {
        if (content != null) {
            StyledTextView view = ((StyledTextView) findViewById(resID));
            if (view != null) {
                view.setText(content);
            }
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable originalState = super.onSaveInstanceState();
        CustomSavedState state = new CustomSavedState(originalState);
        state.childStates = new SparseArray();
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).saveHierarchyState(state.childStates);
        }
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable originalState) {
        CustomSavedState state = (CustomSavedState) originalState;
        super.onRestoreInstanceState(state.getSuperState());
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).restoreHierarchyState(state.childStates);
        }
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    static class CustomSavedState extends BaseSavedState {
        public SparseArray childStates;

        CustomSavedState(Parcelable superState) {
            super(superState);
        }

        private CustomSavedState(Parcel read, ClassLoader classLoader) {
            super(read);
            childStates = read.readSparseArray(classLoader);
        }

        @Override
        public void writeToParcel(Parcel write, int flags) {
            super.writeToParcel(write, flags);
            write.writeSparseArray(childStates);
        }

        public static final ClassLoaderCreator<CustomSavedState> CREATOR = new ClassLoaderCreator<CustomSavedState>() {
            @Override
            public CustomSavedState createFromParcel(Parcel source, ClassLoader loader) {
                return new CustomSavedState(source, loader);
            }

            @Override
            public CustomSavedState createFromParcel(Parcel source) {
                return createFromParcel(null);
            }

            public CustomSavedState[] newArray(int count) {
                return new CustomSavedState[count];
            }
        };
    }
}
