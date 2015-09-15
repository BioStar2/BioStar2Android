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
package com.supremainc.biostar2.push;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.supremainc.biostar2.Setting;

public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (Setting.IS_GOOGPLAY_SERVICE) {
//			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
//			// The getMessageType() intent parameter must be the intent you
//			// received
//			// in your BroadcastReceiver.
//			String messageType = gcm.getMessageType(intent);
//
//			if (!extras.isEmpty()) { // has effect of unparcelling Bundle
//				if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {

//				} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

//				} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

//				}
//			}
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}