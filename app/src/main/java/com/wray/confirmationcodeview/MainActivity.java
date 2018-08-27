package com.wray.confirmationcodeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wray.ccview.ConfirmationCodeView;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    private ConfirmationCodeView ccViewDefault, ccViewLine, ccViewSquare, ccViewRectangle, ccViewCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvResult = findViewById(R.id.tv_result);
        ccViewDefault = findViewById(R.id.ccv_default);
        ccViewLine = findViewById(R.id.ccv_line);
        ccViewSquare = findViewById(R.id.ccv_square);
        ccViewRectangle = findViewById(R.id.ccv_rectangle);
        ccViewCircle = findViewById(R.id.ccv_circle);

        ccViewDefault.setOnInputCompletionListener(l);
        ccViewLine.setOnInputCompletionListener(l);
        ccViewSquare.setOnInputCompletionListener(l);
        ccViewRectangle.setOnInputCompletionListener(l);
        ccViewCircle.setOnInputCompletionListener(l);
    }

    ConfirmationCodeView.OnInputCompletionListener l = new ConfirmationCodeView.OnInputCompletionListener() {
        @Override
        public void onCompletion(ConfirmationCodeView ccv, String content) {
            if (tvResult != null) {
                tvResult.setText("Your Input String : " + content);
            }
        }
    };

    private void setStyle(ConfirmationCodeView ccv, int color) {
        if (ccv != null && ccv.getFocusIndex() != -1) {
            ccv.setBorderColor(color);
            ccv.setBorderSelectColor(color);
            ccv.setContentColor(android.R.color.white);
            ccv.setTextColor(color);
            ccv.setTextSelectColor(android.R.color.white);
            ccv.setContentSelectColor(color);
            ccv.setPasswordColor(color);
            ccv.setPasswordSelectColor(android.R.color.white);
        }
    }

    public void changeMode(View view) {
        ccViewLine.setPasswordMode(!ccViewLine.isPasswordMode());
    }

    public void colorChangeFirst(View view) {
        setStyle(ccViewLine, R.color.colorPrimary);
        setStyle(ccViewSquare, R.color.colorPrimary);
        setStyle(ccViewRectangle, R.color.colorPrimary);
        setStyle(ccViewCircle, R.color.colorPrimary);
    }

    public void colorChangeSecond(View view) {
        setStyle(ccViewLine, R.color.colorAccent);
        setStyle(ccViewSquare, R.color.colorAccent);
        setStyle(ccViewRectangle, R.color.colorAccent);
        setStyle(ccViewCircle, R.color.colorAccent);
    }

    public void colorChangeThird(View view) {
        setStyle(ccViewLine, R.color.colorOrange);
        setStyle(ccViewSquare, R.color.colorOrange);
        setStyle(ccViewRectangle, R.color.colorOrange);
        setStyle(ccViewCircle, R.color.colorOrange);
    }
}
