package com.tao.complielame;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity /*implements onConvertListener*/ {

    private static final String TAG = "MainActivity";

    private EditText et_wav;
    private EditText et_mp3;
    private Button button2, button1;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_wav = (EditText) this.findViewById(R.id.editText1);
        et_mp3 = (EditText) this.findViewById(R.id.editText2);
        button1 = (Button) this.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convert();
            }
        });
        button2 = (Button) this.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getversion();
            }
        });
        pd = new ProgressDialog(this);
    }

    public void getversion() {
        if (convertUtil == null) {
            convertUtil = new ConvertUtil();
        }
        String version = convertUtil.getLameVersion();
        Toast.makeText(this, version, Toast.LENGTH_LONG).show();
    }

    private ConvertUtil convertUtil;

    public void convert() {
        if (convertUtil == null) {
            convertUtil = new ConvertUtil();
        }

        //暂时写死音频路径，实际可以通过EditText输入
        final String wavPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/encode.wav";
        final String mp3path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cuba.mp3";


        File file = new File(wavPath);
        int size = (int) file.length();
        System.out.println("文件大小 " + size);
        if ("".equals(mp3path) || "".equals(wavPath)) {
            Toast.makeText(this, "路径不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        convertUtil.setListener(convertListener);
        new Thread() {
            @Override
            public void run() {
                convertUtil.convertmp3(wavPath, mp3path, 44100, 2);
            }
        }.start();
    }


    ConvertUtil.onConvertListener convertListener = new ConvertUtil.onConvertListener() {
        @Override
        public void showMsg() {
            Log.e(TAG, "jt——>C调用了java的 showResult——>has implement ");
        }
    };
}
