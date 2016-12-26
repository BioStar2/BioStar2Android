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
package com.supremainc.biostar2.service.push;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.InboxStyle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.supremainc.biostar2.BuildConfig;
import com.supremainc.biostar2.R;
import com.supremainc.biostar2.meta.Setting;
import com.supremainc.biostar2.activity.DummyActivity;
import com.supremainc.biostar2.db.NotificationDBProvider;
import com.supremainc.biostar2.sdk.datatype.v2.Login.NotificationType;
import com.supremainc.biostar2.sdk.datatype.v2.Login.PushNotification;
import com.supremainc.biostar2.sdk.provider.PushDataProvider;
import com.supremainc.biostar2.sdk.utils.PreferenceUtil;
import com.supremainc.biostar2.widget.popup.ToastPopup;

import java.util.ArrayList;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public static final String REGISTER_RESPONSE_ACTION = "com.google.android.c2dm.intent.REGISTRATION";
    public static final String TAG = GcmBroadcastReceiver.class.getSimpleName();

    private void Notification(PushNotification noti, Context context) {
        NotificationDBProvider dbProvider = NotificationDBProvider.getInstance(context);
        PendingIntent pendingIntent = getPendingIntent(context, DummyActivity.class);

        ArrayList<String> messages = dbProvider.getUnReadMessageTitle6();
        int count = dbProvider.getUnReadMessageCount();
        Builder builder = new NotificationCompat.Builder(context);
        if (count > 1) {
            if (count > 9999) {
                builder.setContentTitle("9999+" + context.getString(R.string.new_notification));
            } else {
                builder.setContentTitle(String.valueOf(count) + context.getString(R.string.new_notification));
            }
            InboxStyle style = new InboxStyle(builder);
            if (count > messages.size()) {
                count = messages.size();
            }
            for (int i = 0; i < count; i++) {
                style.addLine(messages.get(i));
            }
//			style.setSummaryText("+ more");
            builder.setStyle(style);
        } else {
            builder.setContentTitle(noti.title);
            builder.setContentText(noti.message);
        }
        commonBuilder(builder, pendingIntent, noti.message);
        NotificationManager notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.notify(0, builder.build());

        Intent intentBoradCast = new Intent(Setting.BROADCAST_ALARM_UPDATE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentBoradCast);
    }

    private String getTitle(String title, Context context) {
        if (title.equals("notificationType.title.deviceReboot")) {
            return context.getString(R.string.title_deviceReboot);
        }
        if (title.equals("notificationType.title.deviceRs485Disconnect")) {
            return context.getString(R.string.title_deviceRs485Disconnect);
        }
        if (title.equals("notificationType.title.deviceTampering")) {
            return context.getString(R.string.title_deviceTampering);
        }
        if (title.equals("notificationType.title.doorApb")) {
            return context.getString(R.string.title_doorApb);
        }
        if (title.equals("notificationType.title.doorForcedOpen")) {
            return context.getString(R.string.title_doorForcedOpen);
        }
        if (title.equals("notificationType.title.doorHeldOpen")) {
            return context.getString(R.string.title_doorHeldOpen);
        }
        if (title.equals("notificationType.title.doorOpenRequest")) {
            return context.getString(R.string.title_doorOpenRequest);
        }
        if (title.equals("notificationType.title.zoneApb")) {
            return context.getString(R.string.title_zoneApb);
        }
        if (title.equals("notificationType.title.zoneFire")) {
            return context.getString(R.string.title_zoneFire);
        }
        return null;
    }

    private String getMessage(String message, ArrayList<String> arg, Context context) {
        String base = null;
        if (message.equals("notificationType.message.deviceReboot")) {
            base = context.getString(R.string.message_deviceReboot);
        }
        if (message.equals("notificationType.title.deviceRs485Disconnect")) {
            return context.getString(R.string.title_deviceRs485Disconnect);
        }
        if (message.equals("notificationType.title.deviceTampering")) {
            return context.getString(R.string.title_deviceTampering);
        }
        if (message.equals("notificationType.title.doorApb")) {
            return context.getString(R.string.title_doorApb);
        }
        if (message.equals("notificationType.title.doorForcedOpen")) {
            return context.getString(R.string.title_doorForcedOpen);
        }
        if (message.equals("notificationType.title.doorHeldOpen")) {
            return context.getString(R.string.title_doorHeldOpen);
        }
        if (message.equals("notificationType.title.doorOpenRequest")) {
            return context.getString(R.string.title_doorOpenRequest);
        }
        if (message.equals("notificationType.title.zoneApb")) {
            return context.getString(R.string.title_zoneApb);
        }
        if (message.equals("notificationType.title.zoneFire")) {
            return context.getString(R.string.title_zoneFire);
        }
        if (arg == null) {
            return base;
        }
        if (arg.size() == 1) {
            return String.format(base, arg.get(0));
        } else if (arg.size() == 2) {
            return String.format(base, arg.get(0), arg.get(1));
        } else if (arg.size() == 3) {
            return String.format(base, arg.get(0), arg.get(1), arg.get(2));
        } else {
            return base;
        }
    }

    private PushNotification NotificationBuild(String data, String name, Context context) {
        if (data == null) {
            return null;
        }
        PushNotification noti = null;
        try {
            Gson gson = new Gson();
            NotificationDBProvider dbProvider = NotificationDBProvider.getInstance(context);
            noti = gson.fromJson(data, PushNotification.class);
            Log.e("push fromJson", "push fromJson: ");
            noti.code = name;
            noti.unread = 1;
            if (dbProvider.isDuplicate(noti)) {
                Log.e("push onReceive", "push duplicate: ");
                return null;
            }
            if (!TextUtils.isEmpty(noti.title_loc_key)) {
                String title = getTitle(noti.title_loc_key, context);
                if (!TextUtils.isEmpty(title)) {
                    noti.title = title;
                }
            }
            if (!TextUtils.isEmpty(noti.loc_key)) {
                String message = getMessage(noti.loc_key, noti.loc_args, context);
                if (!TextUtils.isEmpty(message)) {
                    noti.message = message;
                }
            }
            ToastPopup toastPopup = new ToastPopup(context);
            toastPopup.show(ToastPopup.TYPE_ALARM, noti.title, null);
            if (!dbProvider.insert(noti)) {
                noti = null;
                Log.e(TAG, "insert fail");
            }
        } catch (Exception e) {
            Log.e("push exception", "push fromJson: ");
            Log.e(TAG, " " + e.getMessage());
            return null;
        }
        return noti;
    }

    private void commonBuilder(Builder builder, PendingIntent intent, String ticker) {
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(intent);
        builder.setSound(alarmSound);
        builder.setLights(Color.BLUE, 500, 500);
        builder.setVibrate(pattern);
//        builder.setTicker(ticker);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
    }

    private PendingIntent getPendingIntent(Context context, Class<?> cls) {
        Intent notificationIntent = null;
        notificationIntent = new Intent(context, cls);
        notificationIntent.setAction(Setting.ACTION_NOTIFICATION_START + String.valueOf(System.currentTimeMillis()));
        if (!Setting.IS_NOTIFICATION_NONE_RESTART) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "========push receiver start =========");
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            setResultCode(Activity.RESULT_OK);
            return;
        }

        if (REGISTER_RESPONSE_ACTION.equals(intent.getAction())) {
            String registrationId = intent.getStringExtra("registration_id");
            Log.e(TAG, "registered:" + registrationId);
            if ((registrationId != null) && (registrationId.length() > 0)) {
                PreferenceUtil.putSharedPreference(context, "registrationId", registrationId);
            }
            PackageInfo packageInfo;
            try {
                packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                PreferenceUtil.putSharedPreference(context, "version", packageInfo.versionCode);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }

            PushDataProvider pushDataProvider = PushDataProvider.getInstance(context);
            pushDataProvider.setNeedUpdateNotificationToken(registrationId);
            ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
            startWakefulService(context, intent.setComponent(comp));
            setResultCode(Activity.RESULT_OK);
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "========push receiver end =========");
            }
            return;
        }
        PushNotification noti = null;
        for (String key : bundle.keySet()) {
            String value;
            try {
                value = (String) bundle.get(key);
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
                continue;
            }
            if (value == null) {
                continue;
            }
            if (key.equals("CMD") && value.equals("RST_FULL")) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "RST_FULL");
                }
                int registeredVersion = PreferenceUtil.getIntSharedPreference(context, "version");
                if (registeredVersion > 0) {
                    registeredVersion--;
                }
                PreferenceUtil.putSharedPreference(context, "version", registeredVersion);
                Intent intentBoradCast = new Intent(Setting.BROADCAST_PUSH_TOKEN_UPDATE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intentBoradCast);
                return;
            }
            String code = null;
            if (key.equals(NotificationType.DEVICE_REBOOT.mName)) {
                code = NotificationType.DEVICE_REBOOT.mName;
            } else if (key.equals(NotificationType.DEVICE_RS485_DISCONNECT.mName)) {
                code = NotificationType.DEVICE_RS485_DISCONNECT.mName;
            } else if (key.equals(NotificationType.DEVICE_TAMPERING.mName)) {
                code = NotificationType.DEVICE_TAMPERING.mName;
            } else if (key.equals(NotificationType.DOOR_FORCED_OPEN.mName)) {
                code = NotificationType.DOOR_FORCED_OPEN.mName;
            } else if (key.equals(NotificationType.DOOR_HELD_OPEN.mName)) {
                code = NotificationType.DOOR_HELD_OPEN.mName;
            } else if (key.equals(NotificationType.DOOR_OPEN_REQUEST.mName)) {
                code = NotificationType.DOOR_OPEN_REQUEST.mName;
            } else if (key.equals(NotificationType.ZONE_APB.mName)) {
                code = NotificationType.ZONE_APB.mName;
            } else if (key.equals(NotificationType.ZONE_FIRE.mName)) {
                code = NotificationType.ZONE_FIRE.mName;
            }
            if (code != null) {
                PushNotification tempNoti = NotificationBuild(value, code, context);
                if (tempNoti != null) {
                    noti = tempNoti;
                }
            }

            if (BuildConfig.DEBUG) {
                Log.i(TAG, "|" + String.format("%s : %s (%s)", key, value.toString(), value.getClass().getName()) + "|");
            }
        }

        if (noti != null) {
            Notification(noti, context);
        }

        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        startWakefulService(context, intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "========push receiver end =========");
        }
    }

}