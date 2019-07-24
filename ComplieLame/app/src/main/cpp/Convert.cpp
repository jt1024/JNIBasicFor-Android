#include <jni.h>
#include <malloc.h>
#include <string.h>
#include "lame.h"

#include <android/log.h>

#define LOG_TAG    "jiat"
#define LOGD(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)


char *Jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = env->FindClass("java/lang/String");
    jstring strencode = env->NewStringUTF("GB2312");
    jmethodID mid = env->GetMethodID(clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) env->CallObjectMethod(jstr, mid, strencode);
    jsize alen = env->GetArrayLength(barr);
    jbyte *ba = env->GetByteArrayElements(barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1);
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    env->ReleaseByteArrayElements(barr, ba, 0);
    return rtn;
}


extern "C" JNIEXPORT void JNICALL
Java_com_tao_complielame_ConvertUtil_convertmp3 (JNIEnv *env, jobject instance, jstring jwav, jstring jmp3, jint samplerate, jint channels) {
    char *cwav = Jstring2CStr(env, jwav);
    char *cmp3 = Jstring2CStr(env, jmp3);

    LOGD("wav = %s", cwav);
    LOGD("mp3 = %s", cmp3);

    //打开 wav,MP3文件
    FILE *fwav = fopen(cwav, "rb");
    FILE *fmp3 = fopen(cmp3, "wb");

    short int wav_buffer[8192 * 2];
    unsigned char mp3_buffer[8192];

    //1.初始化lame的编码器
    lame_t lame = lame_init();
    //2. 设置lame mp3编码的采样率
    lame_set_in_samplerate(lame, samplerate);//samplerate=44100
    lame_set_num_channels(lame, channels);//channels=2
    // 3. 设置MP3的编码方式
    lame_set_VBR(lame, vbr_default);

    lame_init_params(lame);
    LOGD("lame init finish");

    int read;
    int write; //代表读了多少个次 和写了多少次
    int total = 0; // 当前读的wav文件的byte数目
    do {

        read = fread(wav_buffer, sizeof(short int) * 2, 8192, fwav);
        LOGD("converting ....%d", total);
        total += read * sizeof(short int) * 2;

        if (read != 0) {
            write = lame_encode_buffer_interleaved(lame, wav_buffer, read, mp3_buffer, 8192);
            //把转化后的mp3数据写到文件里
            fwrite(mp3_buffer, sizeof(unsigned char), write, fmp3);
        }
        if (read == 0) {
            lame_encode_flush(lame, mp3_buffer, 8192);
        }
    } while (read > 0);
    LOGD("convert  finish");
    lame_close(lame);
    fclose(fwav);
    fclose(fmp3);


    // 调用java代码，通知转码结束
    jclass jclazz = env->FindClass("com/tao/complielame/ConvertUtil");
    jmethodID jmethod = env->GetMethodID(jclazz, "convertCompleted", "()V");
    env->CallVoidMethod(instance, jmethod);

}