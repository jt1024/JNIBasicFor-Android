#include <jni.h>
#include "lame.h"

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_tao_complielame_ConvertUtil_getLameVersion(JNIEnv *env, jobject obj) {
    return env->NewStringUTF(get_lame_version());
}