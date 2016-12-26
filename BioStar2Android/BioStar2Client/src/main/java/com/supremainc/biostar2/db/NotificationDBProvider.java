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
package com.supremainc.biostar2.db;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.sdk.datatype.v2.Device.BaseDevice;
import com.supremainc.biostar2.sdk.datatype.v2.Door.BaseDoor;
import com.supremainc.biostar2.sdk.datatype.v2.Login.PushNotification;
import com.supremainc.biostar2.sdk.datatype.v2.User.BaseUser;

import java.util.ArrayList;

public class NotificationDBProvider {
    public static final int DATA_RESULT_FAIL = -1;
    private static int mUnreadCount = -1;
    private static NotificationDBProvider self = null;
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    private ContentResolver mResolver;

    private NotificationDBProvider(Context context) {
        this.mContext = context;
        this.mResolver = mContext.getContentResolver();
    }

    public static NotificationDBProvider getInstance(Context context) {
        if (self == null || self.mResolver == null || self.mContext == null) {
            self = new NotificationDBProvider(context);
        }
        return self;
    }

    public static NotificationDBProvider getInstance() {
        if (self == null) {
            return null;
        }
        return self;
    }

    private ContentValues createAlarmValues(PushNotification noti) {
        ContentValues values = new ContentValues();
        values.clear();
        if (noti.request_timestamp != null) {
            values.put(DBAdapter.TABLE_ALARM_TIME, noti.request_timestamp);
        }
        if (noti.user != null && noti.user.user_id != null) {
            values.put(DBAdapter.TABLE_ALARM_USER_ID, noti.user.user_id);
        }
        if (noti.user != null && noti.user.name != null) {
            values.put(DBAdapter.TABLE_ALARM_USER, noti.user.name);
        }
        if (noti.contact_phone_number != null) {
            values.put(DBAdapter.TABLE_ALARM_TELEPHONE, noti.contact_phone_number);
        }
        if (noti.device != null && noti.device.id != null) {
            values.put(DBAdapter.TABLE_ALARM_DEVICE_ID, noti.device.id);
        }
        if (noti.device != null && noti.device.name != null) {
            values.put(DBAdapter.TABLE_ALARM_DEVICE, noti.device.name);
        }
        if (noti.message != null) {
            values.put(DBAdapter.TABLE_ALARM_MSG, noti.message);
        }
        if (noti.title != null) {
            values.put(DBAdapter.TABLE_ALARM_TITLE, noti.title);
        }
        if (noti.door != null && noti.door.id != null) {
            values.put(DBAdapter.TABLE_ALARM_DOOR_ID, noti.door.id);
        }
        if (noti.door != null && noti.door.name != null) {
            values.put(DBAdapter.TABLE_ALARM_DOOR, noti.door.name);
        }
        if (noti.code != null) {
            values.put(DBAdapter.TABLE_ALARM_CODE, noti.code);
        }
        values.put(DBAdapter.TABLE_ALARM_UNREAD, noti.unread);
        return values;
    }

    public void delete(final ArrayList<Integer> deleteList, final OnTaskFinish OnTaskFinishListener, final Activity activity) {
        mUnreadCount = -1;
        if (OnTaskFinishListener == null || activity == null || deleteList == null) {
            return;
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                long start = System.currentTimeMillis();
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "start delete:" + deleteList.size());
                }
                for (int i = 0; i < deleteList.size(); i++) {
                    mResolver.delete(DBAdapter.CONTENT_ALARM_URI, DBAdapter.DB_ID + "=" + deleteList.get(i), null);
                }
                if (BuildConfig.DEBUG) {
                    Log.i(TAG, "end getCheckedItemIds:" + ((System.currentTimeMillis() - start) / 1000));
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        OnTaskFinishListener.onDeleteFinish(deleteList.size());
                    }
                });
                return null;
            }
        }.execute(null, null, null);

    }

    public int deleteAll() {
        mUnreadCount = -1;
        return mResolver.delete(DBAdapter.CONTENT_ALARM_URI, null, null);
    }

    public PushNotification get(Cursor cursor) {
        PushNotification noti = new PushNotification();
        noti.user = new BaseUser();
        noti.device = new BaseDevice();
        noti.door = new BaseDoor();
        noti.request_timestamp = cursor.getString(DBAdapter.COLUMN_ALARM_TIME);
        noti.user.user_id = cursor.getString(DBAdapter.COLUMN_ALARM_USER_ID);
        noti.user.name = cursor.getString(DBAdapter.COLUMN_ALARM_USER);
        noti.contact_phone_number = cursor.getString(DBAdapter.COLUMN_ALARM_TELEPHONE);
        noti.device.id = cursor.getString(DBAdapter.COLUMN_ALARM_DEVICE_ID);
        noti.device.name = cursor.getString(DBAdapter.COLUMN_ALARM_DEVICE);
        noti.message = cursor.getString(DBAdapter.COLUMN_ALARM_MSG);
        noti.title = cursor.getString(DBAdapter.COLUMN_ALARM_TITLE);
        noti.door.id = cursor.getString(DBAdapter.COLUMN_ALARM_DOOR_ID);
        noti.door.name = cursor.getString(DBAdapter.COLUMN_ALARM_DOOR);
        noti.code = cursor.getString(DBAdapter.COLUMN_ALARM_CODE);
        noti.unread = cursor.getInt(DBAdapter.COLUMN_ALARM_UNREAD);
        noti.dbID = cursor.getInt(DBAdapter.COLUMN_ALARM_DB_ID);
        return noti;
    }

    public Cursor getPushAlarmCursor() {
        Cursor cursor = mResolver.query(DBAdapter.CONTENT_ALARM_URI, null, null, null, DBAdapter.TABLE_ALARM_TIME + " DESC");
        // +" DESC" +" ASC"
        return cursor;
    }

    public int getUnReadMessageCount() {
        if (mUnreadCount == -1) {
            getUnReadMessageTitle6();
        }
        return mUnreadCount;
    }

    public ArrayList<String> getUnReadMessageTitle6() {

        String selection = DBAdapter.TABLE_ALARM_UNREAD + "=?";
        String selectionArgs[] = {"1"};
        String projection[] = {DBAdapter.TABLE_ALARM_TITLE, DBAdapter.TABLE_ALARM_TIME};
        Cursor cursor = null;
        try {
            cursor = mResolver.query(DBAdapter.CONTENT_ALARM_URI, projection, selection, selectionArgs, DBAdapter.TABLE_ALARM_TIME + " DESC");
            if (null == cursor || cursor.getCount() < 1) {
                mUnreadCount = 0;
                if (cursor != null) {
                    cursor.close();
                }
                return null;
            }
            mUnreadCount = cursor.getCount();
            int count = 0;
            ArrayList<String> unReads = new ArrayList<String>();
            while (cursor.moveToNext()) {
                count++;
                if (count > 6) {
                    break;
                }
                String title = cursor.getString(0);
                unReads.add(title);
            }
            if (cursor != null) {
                cursor.close();
            }
            return unReads;
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public boolean insert(PushNotification noti) {
        ContentValues vaules = createAlarmValues(noti);
        Uri uri = mResolver.insert(DBAdapter.CONTENT_ALARM_URI, vaules);
        if (uri != null) {
            mUnreadCount = -1;
            return true;
        } else {
            return false;
        }
    }

    public boolean isDuplicate(PushNotification data) {
        String selection = DBAdapter.TABLE_ALARM_TIME + "=?" + " and " + DBAdapter.TABLE_ALARM_CODE + "=?";
        String selectionArgs[] = {data.request_timestamp, data.code};
        String projection[] = {DBAdapter.TABLE_ALARM_TIME, DBAdapter.TABLE_ALARM_CODE};
        Cursor cursor = mResolver.query(DBAdapter.CONTENT_ALARM_URI, projection, selection, selectionArgs, DBAdapter.TABLE_ALARM_TIME + " DESC");
        if (null == cursor || cursor.getCount() < 1) {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }
        if (cursor != null) {
            cursor.close();
        }
        return true;
    }

    public void modify(PushNotification item) {
        if (item.dbID < 0) {
            return;
        }
        mUnreadCount = -1;
        String selection = DBAdapter.DB_ID + "=?";
        String selectionArgs[] = {String.valueOf(item.dbID)};
        ContentValues vaules = createAlarmValues(item);
        Cursor cursor = mResolver.query(DBAdapter.CONTENT_ALARM_URI, null, selection, selectionArgs, null);
        if (null == cursor || cursor.getCount() < 1) {
            mResolver.insert(DBAdapter.CONTENT_ALARM_URI, vaules);
        } else {
            mResolver.update(DBAdapter.CONTENT_ALARM_URI, vaules, selection, selectionArgs);
        }
        cursor.close();

    }

    public interface OnTaskFinish {
        public void onDeleteFinish(int count);
    }
}
