package com.android.testtinker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.taobao.sophix.SophixManager;
import com.tencent.bugly.crashreport.CrashReport;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SophixManager.getInstance().queryAndLoadNewPatch();

        TextView textView1 = findViewById(R.id.tv1);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.testJavaCrash();
            }
        });
        TextView textView2 = findViewById(R.id.tv2);
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SophixManager.getInstance().queryAndLoadNewPatch();
            }
        });
        TextView textView = findViewById(R.id.test_tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        TextView textView3 = findViewById(R.id.tv3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aaa="pppppp";
                Loge.e(aaa);
                textView.setText("版本2:"+aaa);
                Loge.e("这是原始版本2");
            }
        });
    }
}