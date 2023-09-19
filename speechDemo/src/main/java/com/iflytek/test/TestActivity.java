package com.iflytek.test;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.speech.util.JsonParser;
import com.iflytek.voicedemo.IatDemo;
import com.iflytek.voicedemo.MainActivity;
import com.iflytek.voicedemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText;
    TextView state;
    Button button;
    RecognizerDialog recognizerDialog;
    // 语音听写对象
    private SpeechRecognizer mIat;
    private static final String TAG = AppCompatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
        setTitle("测试语音");
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button_start);
        state = findViewById(R.id.state);
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(TestActivity.this, mInitListener);
        if (mIat == null) {
            state.setText("mIat==null");
        } else {
            state.setText(mIat.toString());
        }
        recognizerDialog = new RecognizerDialog(
                TestActivity.this, mInitListener);// 这里应该写从科大讯飞申请到的appid
        button.setOnClickListener(this);
        findViewById(R.id.button_start2).setOnClickListener(this);
        findViewById(R.id.button_stop).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                editText.setText("");
                recognizerDialog.setListener(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        printResult(recognizerResult);
                    }

                    @Override
                    public void onError(SpeechError speechError) {

                    }
                });
                recognizerDialog.show();
                break;
            case R.id.button_start2:
                int ret = 0;
                ret = mIat.startListening(recognizerListener);
                state.setText("被点击了:" + ret);
                if (ret != ErrorCode.SUCCESS) {
                    state.setText("听写失败,错误码：" + ret + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
                } else {
                    state.setText(getString(R.string.text_begin));
                }
                break;
            case R.id.button_stop:
                mIat.stopListening();
                break;
            case R.id.button_cancel:
                mIat.cancel();
                break;
            default:
                break;
        }
    }

    RecognizerListener recognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            state.setText("当前正在说话，音量大小 = " + i + " 返回音频数据 = " + bytes.length);
        }

        @Override
        public void onBeginOfSpeech() {
            state.setText("开始说话");
        }

        @Override
        public void onEndOfSpeech() {
            state.setText("结束说话");
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            state.setText("错误代码:" + speechError.getErrorDescription());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
    InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            if (i != ErrorCode.SUCCESS) {
                state.setText("初始化失败，错误码：" + i + ",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<>();

    /**
     * 显示结果
     */
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        editText.setText(resultBuffer.toString());
        // editText.setSelection(mResultText.length());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIat != null) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }
}
