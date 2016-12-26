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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.sql.SQLException;

public abstract class SqlDB extends ContentProvider {
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    }

    public SQLiteOpenHelper mSQLiteOpenHelper;

    private void Notify(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String table = null;
        table = uri.getPathSegments().get(0);
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(table);
        SQLiteDatabase database = mSQLiteOpenHelper.getWritableDatabase();
        Cursor cursor = queryBuilder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        long rowId = -1;
        switch (mUriMatcher.match(uri)) {
            default:
                String table = uri.getPathSegments().get(0);
                rowId = db.insert(table, null, values);
                break;
        }
        if (rowId > 0) {
            Uri resultUri = ContentUris.withAppendedId(uri, rowId);
            getContext().getContentResolver().notifyChange(resultUri, null);
            return resultUri;
        }
        try {
            throw new SQLException("Uri insert file: " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        String table = null;
        table = uri.getPathSegments().get(0);
        if (db == null) {
            return -1;
        } else {
            db.beginTransaction();
            try {
                final int numValues = values.length;
                for (int i = 0; i < numValues; i++) {
                    if (db.insert(table, null, values[i]) < 0) {
                        return 0;
                    }
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            Notify(uri);
        }
        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        String table = null;
        int count = 0;
        switch (mUriMatcher.match(uri)) {
            default:
                table = uri.getPathSegments().get(0);
                count = db.delete(table, selection, selectionArgs);
                break;
        }
        if (count > 0) {
            Notify(uri);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String table = null;
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        switch (mUriMatcher.match(uri)) {
            default:
                table = uri.getPathSegments().get(0);
                count = db.update(table, values, selection, selectionArgs);
                break;
        }
        if (count > 0) {
            Notify(uri);
        }
        return count;
    }
}
