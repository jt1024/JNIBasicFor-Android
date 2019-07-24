package com.tao.complielame;

import android.util.Log;


/**
 * 作者： 麦典威
 * 修改时间：2018/3/18 11:02
 * 版权声明：www.ekwing.com
 * 功能： ${TODO}
 */


public class ConvertUtil {
    private onConvertListener listener;

    static {
        System.loadLibrary("lamemp3");
    }

    public native String getLameVersion();

    /**
     * wav转码为MP3
     *
     * @param wavPath    wav音频路径
     * @param mp3Path    mp3音频路径
     * @param samplerate 采样率（实测可以设值为44100）
     * @param channels   声道数（实测可以设值为2）
     */
    public native void convertmp3(String wavPath, String mp3Path, int samplerate, int channels);

    /**
     * 音频转码成功后，C触发此方法
     */
    public void convertCompleted() {
        Log.e("ConvertUtil", "convertCompleted——>no implement ");
        if (listener != null) {
            listener.showMsg();
        }
    }

    /**
     * 音频转码成功后的回调接口
     */
    interface onConvertListener {
        void showMsg();
    }

    public void setListener(onConvertListener listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }

}
