#include <jni.h>

// contact to ts team
extern "C"
jbyteArray
Java_com_supremainc_biostar2_provider_MobileCardDataProvider_nProcessCommandApduNFC(JNIEnv *env,
                                                                                    jobject thiz, jbyteArray javaBytes, jobject context, jobject privateKey) {
    char ret_error[2] = {(char)0x6F, (char)0x00};
    jbyteArray retArrError = env->NewByteArray(2);
    env->SetByteArrayRegion(retArrError, 0, 2, reinterpret_cast<jbyte *>(ret_error));
    return retArrError;
}

