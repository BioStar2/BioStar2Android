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
package com.supremainc.biostar2.sdk.provider;

import android.content.Context;
import android.util.Log;

import com.supremainc.biostar2.sdk.models.v1.permission.CloudRoles;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionItem;
import com.supremainc.biostar2.sdk.models.v2.permission.PermissionModule;
import com.supremainc.biostar2.sdk.models.v2.permission.UserPermissions;
import com.supremainc.biostar2.sdk.models.v2.user.ListUser;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

import static com.supremainc.biostar2.sdk.models.v2.common.VersionData.getCloudVersionString;

public class PermissionDataProvider extends BaseDataProvider {
    private static PermissionDataProvider mSelf = null;
    @SuppressWarnings("unused")
    private final String TAG = getClass().getSimpleName();

    private PermissionDataProvider(Context context) {
        super(context);
    }

    public static PermissionDataProvider getInstance(Context context) {
        if (mSelf == null) {
            mSelf = new PermissionDataProvider(context);
        }
        return mSelf;
    }

    public static PermissionDataProvider getInstance() {
        if (mSelf != null) {
            return mSelf;
        }
        if (mContext != null) {
            mSelf = new PermissionDataProvider(mContext);
            return mSelf;
        }
        return null;
    }

    public boolean getPermission(PermissionModule module, boolean isWriteAllow) {
        if (mPermissionMap == null) {
            Log.e(TAG, "map is null");
            return false;
        }
        String key;
        if (isWriteAllow) {
            key = module.mName + PermissionItem.ROLE_WRITE;
        } else {
            key = module.mName + PermissionItem.ROLE_READ;
        }
        Boolean result = mPermissionMap.get(key);
        if (ConfigDataProvider.DEBUG) {
            Log.i(TAG, key + ":" + String.valueOf(result));
        }
        if (result == null || result == false) {
            return false;
        }
        return true;
    }

    public Call<CloudRoles> getCloudRoles(Callback<CloudRoles> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<CloudRoles> call = mApiInterface.get_reference_role_codes(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }
    public Call<UserPermissions> getPermissions(Callback<UserPermissions> callback) {
        if (!checkAPI(callback)) {
            return null;
        }
        Call<UserPermissions> call = mApiInterface.get_setting_permission_list(getCloudVersionString(mContext));
        call.enqueue(callback);
        return call;
    }

    public boolean isEnableModifyUser(ListUser user) {
        if (user != null && user.user_id.equals("1")) {
            return false;
        }
        if (mUserInfo != null && mUserInfo.permission != null && mUserInfo.permission.id.equals("1")) {
            return true;
        }
        if (user == null) {
            return false;
        }
        if (!getPermission(PermissionModule.USER, true)) {
            return false;
        }
        if (user.permission == null || user.permission.id == null) {
            return true;
        }
        if (user.permission.id.equals("255")) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getDefaultAllowUserGroupSize() {
        if (mUserInfo == null || mUserInfo.permission == null || mUserInfo.permission.permissions == null) {
            return null;
        }
        for (PermissionItem item:mUserInfo.permission.permissions) {
            if (PermissionModule.USER.mName.equals(item.module)) {
                if (item.allowed_group_id_list == null) {
                    return null;
                }
                return item.allowed_group_id_list;
            }
        }
        return null;
    }

}
