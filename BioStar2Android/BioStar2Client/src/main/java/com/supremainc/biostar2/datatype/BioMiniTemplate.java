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
package com.supremainc.biostar2.datatype;

import android.util.Base64;

public class BioMiniTemplate {
    public final String TAG = getClass().getSimpleName();
    private byte[] mTemplate = new byte[1024];
    // Enroll Template Size
    private int[] mTemplateSize = new int[4];
    private byte[] mImage = new byte[320 * 480];
    private int[] mQuality = new int[4];


    public String getTemplateString() {
        try {
            byte[] temp = new byte[mTemplateSize[0]];
            System.arraycopy(mTemplate, 0, temp, 0, mTemplateSize[0]);
            String result = Base64.encodeToString(temp, 0);
            result = result.replaceAll("\n", "");
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public byte[] getmImage() {
        return mImage;
    }
    public void setmImage(byte[] mImage) {
        this.mImage = mImage;
    }
    public byte[] getTemplate() {
        return mTemplate;
    }
    public int[] getTemplateSize() {
        return mTemplateSize;
    }
    public void setTemplateSize(int[] templateSize) {
        this.mTemplateSize = templateSize;
    }
    public void setTemplate(byte[] template) {
        this.mTemplate = template;
    }
    public int[] getQuality() {
        return mQuality;
    }
}
