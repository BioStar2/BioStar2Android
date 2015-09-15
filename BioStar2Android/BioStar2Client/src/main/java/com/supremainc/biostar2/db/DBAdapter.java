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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class DBAdapter extends SqlDB {
    public static final int COLUMN_ALARM_CODE = 11;
    public static final int COLUMN_ALARM_DB_ID = 0;
    public static final int COLUMN_ALARM_DEVICE = 6;
    public static final int COLUMN_ALARM_DEVICE_ID = 5;
    public static final int COLUMN_ALARM_DOOR = 9;
    public static final int COLUMN_ALARM_DOOR_ID = 10;
    public static final int COLUMN_ALARM_MSG = 7;
    public static final int COLUMN_ALARM_TELEPHONE = 4;
    public static final int COLUMN_ALARM_TIME = 1;
    public static final int COLUMN_ALARM_TITLE = 8;
    public static final int COLUMN_ALARM_UNREAD = 12;
    public static final int COLUMN_ALARM_USER = 3;
    public static final int COLUMN_ALARM_USER_ID = 2;
    public static final String DB_ID = "_id";
    public static final String TABLE_ALARM = "alarm";
    public static final String TABLE_ALARM_CODE = "code";
    public static final String TABLE_ALARM_DEVICE = "device";
    public static final String TABLE_ALARM_DEVICE_ID = "device_id";
    public static final String TABLE_ALARM_DOOR = "door";
    public static final String TABLE_ALARM_DOOR_ID = "doorid";
    public static final String TABLE_ALARM_MSG = "msg";
    public static final String TABLE_ALARM_TELEPHONE = "telephone";
    public static final String TABLE_ALARM_TIME = "time";
    public static final String TABLE_ALARM_TITLE = "title";
    public static final String TABLE_ALARM_UNREAD = "unread";
    public static final String TABLE_ALARM_USER = "user";
    public static final String TABLE_ALARM_USER_ID = "user_id";
    static final String AUTHORITY = "com.supremainc.biostar2.db.DBAdapter";
    public static final Uri CONTENT_ALARM_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_ALARM);
    private static final String CREATE_ALARM_TABLE = TABLE_ALARM + " (" + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TABLE_ALARM_TIME + " TEXT, " + TABLE_ALARM_USER_ID + " TEXT, "
            + TABLE_ALARM_USER + " TEXT, " + TABLE_ALARM_TELEPHONE + " TEXT, " + TABLE_ALARM_DEVICE_ID + " TEXT, " + TABLE_ALARM_DEVICE + " TEXT, " + TABLE_ALARM_MSG + " TEXT, " + TABLE_ALARM_TITLE
            + " TEXT, " + TABLE_ALARM_DOOR + " TEXT, " + TABLE_ALARM_DOOR_ID + " TEXT, " + TABLE_ALARM_CODE + " TEXT, " + TABLE_ALARM_UNREAD + " INTEGER DEFAULT 1" + ");";
    private static final String DATABASE_NAME = "suprema.db";
    private static int DATABASE_VERSION = 1;

    // ==================================================================================================================================================

    @Override
    public boolean onCreate() {
        mSQLiteOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    private class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        private void createTables(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + CREATE_ALARM_TABLE);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                if (oldVersion < newVersion) {
//					try {
//						db.beginTransaction();
//						db.execSQL("ALTER TABLE_ALARM " + TABLE_ALARM + " ADD COLUMN " + TABLE_ALARM_ADD + " String");
//						db.setTransactionSuccessful();
//					} catch (IllegalStateException e) {
//						Log.e(TAG, " "+e.getMessage());
//					} finally {
//						db.endTransaction();
//					};
                }
            }
        }
    }
}
