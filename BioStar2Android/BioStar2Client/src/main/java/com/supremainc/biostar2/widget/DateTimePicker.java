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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;

import com.supremainc.biostar2.sdk.provider.CommonDataProvider;
import com.supremainc.biostar2.sdk.provider.CommonDataProvider.DATE_TYPE;

import java.util.Calendar;

public class DateTimePicker {
    private final String TAG = getClass().getSimpleName();
    protected CommonDataProvider mCommonDataProvider;
    Dialog dlgDate;
    Dialog dlgTime;
    private Activity mActivity;

    public DateTimePicker(Activity activity) {
        mActivity = activity;
        mCommonDataProvider = CommonDataProvider.getInstance(activity);
    }

    private void close() {
        if (dlgTime != null) {
            if (dlgTime.isShowing()) {
                dlgTime.dismiss();
            }
        }
        if (dlgDate != null) {
            if (dlgDate.isShowing()) {
                dlgDate.dismiss();
            }
        }
    }

    public String getDateString(int y, int m, int d) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m);
        cal.set(Calendar.DAY_OF_MONTH, d);
        return mCommonDataProvider.getClientTimeFormat(mActivity, DATE_TYPE.FORMAT_DATE).format(cal.getTime());
    }

    public boolean isErrorSetDate(int sYear, int sMonth, int sDay, int eYear, int eMonth, int eDay) {
        boolean isError = false;
        if (sYear > eYear) {
            isError = true;
        } else if (sYear == eYear) {
            if (sMonth > eMonth) {
                isError = true;
            } else if (sMonth == eMonth) {
                if (sDay > eDay) {
                    isError = true;
                }
            }
        }
        return isError;
    }


    public void showDatePicker(OnDateSetListener dateSetListener, int year, int month, int day) {
        if (mActivity.isFinishing()) {
            return;
        }
        close();
        dlgDate = new DatePickerDialog(mActivity, dateSetListener, year, month, day);
        dlgDate.show();
//		DialogFragment datePicker = new DatePickerFragment(dateSetListener, year, month, day);
//		try {
//			datePicker.show(mActivity.getSupportFragmentManager(), "datePicker");
//		} catch (Exception e) {
//			Log.e(TAG, "showDatePicker " + e.getMessage());
//		}
    }

    public void showTimePicker(OnTimeSetListener timeSetListener, int hourOfDay, int minute) {
        if (mActivity.isFinishing()) {
            return;
        }
        close();
        dlgTime = new TimePickerDialog(mActivity, timeSetListener, hourOfDay, minute, true);
        dlgTime.show();
//		try {
//			DialogFragment timePicker = new TimePickerFragment(timeSetListener, hourOfDay, minute);
//			timePicker.show(mActivity.getSupportFragmentManager(), "timePicker");
//		} catch (Exception e) {
//			Log.e(TAG, "showTimePicker " + e.getMessage());
//		}
    }

//    public static class DatePickerFragment extends DialogFragment implements OnDateSetListener {
//        private OnDateSetListener mDateSetListener;
//        private int mYear;
//        private int mMonth;
//        private int mDay;
//
//        public DatePickerFragment(OnDateSetListener dateSetListener, int year, int month, int day) {
//            mDateSetListener = dateSetListener;
//            mYear = year;
//            mMonth = month;
//            mDay = day;
//            ;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            return new DatePickerDialog(getActivity(), this, mYear, mMonth, mDay);
//        }
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//            if (mDateSetListener != null) {
//                mDateSetListener.onDateSet(view, year, monthOfYear, dayOfMonth);
//            }
//        }
//    }
//
//    public static class TimePickerFragment extends DialogFragment implements OnTimeSetListener {
//        private OnTimeSetListener mTimeSetListener;
//        private int mHourOfDay;
//        private int mMinute;
//
//        public TimePickerFragment(OnTimeSetListener timeSetListener, int hourOfDay, int minute) {
//            mTimeSetListener = timeSetListener;
//            mHourOfDay = hourOfDay;
//            mMinute = minute;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            return new TimePickerDialog(getActivity(), this, mHourOfDay, mMinute, true);
//        }
//
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            if (mTimeSetListener != null) {
//                mTimeSetListener.onTimeSet(view, hourOfDay, minute);
//            }
//        }
//    }

}
