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
package com.supremainc.biostar2.provider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.biomini.BioMiniAndroid;
import com.android.biomini.IBioMiniCallback;
import com.supremainc.biostar2.datatype.BioMiniTemplate;

import java.nio.ByteBuffer;

public class BioMiniDataProvider {
    protected static Activity mActivity;
    private static BioMiniDataProvider mSelf = null;
    private static BioMiniAndroid mBioMiniHandle;

    private final String TAG = getClass().getSimpleName();
//    private IOnBioBitmap mIOnBioBitmap;
//    private BioMiniTemplate mBioMiniTemplate;

    public static final int UFA_OK = 0;
    public static final int UFA_ERROR =-1;
    public static final int UFA_ERR_LICENSE_NOT_MATCH =-102;
    public static final int UFA_ERR_NOT_SUPPORTED= -111;
    public static final int UFA_ERR_INVALID_PARAMETERS =-112;
    public static final int UFA_ERR_ALREADY_INITIALIZED= -201;
    public static final int UFA_ERR_NOT_INITIALIZED =-202;
    public static final int UFA_ERR_NO_DEVICE= -205;
    public static final int UFA_ERR_PERMISSION_DENIED= -206;
    public static final int UFA_ERR_CAPTURE_RUNNING= -211;
    public static final int UFA_ERR_CAPTURE_FAILED= -212;
    public static final int UFA_ERR_NOT_CAPTURED =-213;
    public static final int UFA_ERR_EXTRACTION_FAILED= -302;
    public static final int UFA_ERR_TEMPLATE_TYPE= -411;


    public enum FingerTemplateType {UFA_TEMPLATE_TYPE_SUPREMA,UFA_TEMPLATE_TYPE_ISO19794_2,UFA_TEMPLATE_TYPE_ANSI378};
    private BioMiniDataProvider(Activity a) {
        mActivity = a;
    }

    public static BioMiniDataProvider getInstance(Activity a) {
        if (mSelf == null) {
            mSelf = new BioMiniDataProvider(a);
            mBioMiniHandle = new BioMiniAndroid(a);
        }
        return mSelf;
    }

    public int findDevice() {
        if (mBioMiniHandle == null) {
            Log.e(">==< Main Activity >==<", String.valueOf("BioMini SDK Handler with NULL!"));
            return UFA_ERR_NOT_INITIALIZED;
        } else {
            return mBioMiniHandle.UFA_FindDevice();
        }
    }

    private final IBioMiniCallback mBioMiniCallbackHandler = new IBioMiniCallback() {

        @Override
        public void onCaptureCallback(final byte[] capturedimage, int width, int height, int resolution, boolean bfingeron) {

        }

        @Override
        public void onErrorOccurred(String msg) {

        }
    };

    public int initDevice(FingerTemplateType type) {
        int ufa_res = mBioMiniHandle.UFA_Init();
        if (ufa_res == UFA_OK) {
            int sensitivity = 7;
            int timeout = 10;
            int securitylevel = 4;
            int fastmode = 1;
            if (mBioMiniHandle.getProductId() == 0x409) {
                int pnCropMode[] = new int[1];
                ufa_res = mBioMiniHandle.UFA_GetParameter(mBioMiniHandle.UFA_PARAM_SCANNING_MODE , pnCropMode);
                if( ufa_res != UFA_OK ){
                    return ufa_res ;
                }
                ufa_res =mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_SCANNING_MODE ,  mBioMiniHandle.PLUS2_SCANNING_MODE_CROP);
//                if(pnCropMode[0] == mBioMiniHandle.PLUS2_SCANNING_MODE_CROP){
//                    cbox.setChecked(true);
//                }else if( pnCropMode[0] == mBioMiniHandle.PLUS2_SCANNING_MODE_FULL ){
//                    cbox.setChecked(false);
//                }
            }

            mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_SENSITIVITY, sensitivity);
            mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_TIMEOUT, timeout * 1000);
            mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_SECURITY_LEVEL, securitylevel);
            mBioMiniHandle.UFA_SetParameter(mBioMiniHandle.UFA_PARAM_FAST_MODE, fastmode);
            switch (type) {
                case UFA_TEMPLATE_TYPE_ANSI378:
                    ufa_res = mBioMiniHandle.UFA_SetTemplateType(mBioMiniHandle.UFA_TEMPLATE_TYPE_ANSI378);
                    break;
                case UFA_TEMPLATE_TYPE_SUPREMA:
                    ufa_res = mBioMiniHandle.UFA_SetTemplateType(mBioMiniHandle.UFA_TEMPLATE_TYPE_SUPREMA);
                    break;
                case UFA_TEMPLATE_TYPE_ISO19794_2:
                    ufa_res = mBioMiniHandle.UFA_SetTemplateType(mBioMiniHandle.UFA_TEMPLATE_TYPE_ISO19794_2);
                    break;
            }
            mBioMiniHandle.UFA_SetCallback(mBioMiniCallbackHandler);
        }
        return ufa_res;
    }

    public int scanFingerPrint(BioMiniTemplate template) {
        int ufa_res = mBioMiniHandle.UFA_CaptureSingle(template.getmImage());
        if (ufa_res != UFA_OK) {
            return ufa_res;
        }
//        ufa_res = mBioMiniHandle.UFA_ExtractTemplate(template.getTemplate(), template.getTemplateSize(), template.getQuality(), 1024);
        ufa_res = mBioMiniHandle.UFA_ExtractTemplate(template.getTemplate(), template.getTemplateSize(), template.getQuality(), 384);
        return ufa_res;
    }

    public int verify(BioMiniTemplate template1,BioMiniTemplate template2) {
        int ufa_res = 0;
        int[] nVerificationResult = new int[4];
        nVerificationResult[0] = 0;
        ufa_res = mBioMiniHandle.UFA_Verify(template1.getTemplate(), template1.getTemplateSize()[0], template2.getTemplate(), template2.getTemplateSize()[0], nVerificationResult);
        if (nVerificationResult[0] == 1) {
            // match
            return UFA_OK;
        }
        return ufa_res;
    }

    public static String getMessage(int code) {
        return mBioMiniHandle.UFA_GetErrorString(code);
//        switch (code ){
//            case UFA_OK:
//                return "Success";
//            case UFA_ERROR:
//                return "General error";
//            case UFA_ERR_LICENSE_NOT_MATCH:
//                return "License does not match";
//            case UFA_ERR_NOT_SUPPORTED:
//                return "This function is not supported";
//            case UFA_ERR_INVALID_PARAMETERS:
//                return "Input parameters are invalid";
//            case UFA_ERR_ALREADY_INITIALIZED:
//                return "Module is already initialized";
//            case UFA_ERR_NOT_INITIALIZED:
//                return "Module is not initialized";
//            case UFA_ERR_NO_DEVICE:
//                return "Device is not connected";
//            case UFA_ERR_PERMISSION_DENIED:
//                return "Device permission is canceled";
//            case UFA_ERR_CAPTURE_RUNNING:
//                return "Capturing is started using UFA_CaptureSingleImage or UFA_StartCapturing";
//            case UFA_ERR_CAPTURE_FAILED:
//                return "Capturing is timeout or aborted";
//            case UFA_ERR_NOT_CAPTURED:
//                return "There is no captured image for extraction";
//            case UFA_ERR_EXTRACTION_FAILED:
//                return "Extraction is failed";
//            case UFA_ERR_TEMPLATE_TYPE:
//                return "Template type is not matched";
//            default :
//                return "Unkonow Error";
//        }
    }

}
