package com.wray.ccview;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

public class CCEditText extends android.support.v7.widget.AppCompatEditText {

    private onInputChangedListener listener = null;

    private int mInputType = ConfirmationCodeView.TYPE_DIGIT;

    public CCEditText(Context context) {
        this(context, null);
    }

    public CCEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (listener != null && hasInput()) {
                    listener.onFinish(editable.toString());
                }
            }
        });
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onKeyDelete();
                    return true;
                }
                return false;
            }
        });
    }

    private void onKeyDelete() {
        if (!TextUtils.isEmpty(getTextStr())) {
            setText("");
        }
        if (listener != null) {
            listener.onClear();
        }
    }

    public void setContentType(int type) {
        mInputType = type;
        switch (type) {
            case ConfirmationCodeView.TYPE_MIXTURE:
            case ConfirmationCodeView.TYPE_LETTER:
                setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case ConfirmationCodeView.TYPE_DIGIT:
                setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }
    }

    public void setInputChangedListener(onInputChangedListener l) {
        listener = l;
    }

    public interface onInputChangedListener {

        void onFinish(CharSequence c);

        void onClear();
    }

    public void hideInput() {
        setTextColor(Color.TRANSPARENT);
    }

    public void showInput(int color, int selectColor) {
        if (color != Color.TRANSPARENT || selectColor != Color.TRANSPARENT) {
            if (this.isFocused()) {
                setTextColor(selectColor);
            } else {
                setTextColor(color);
            }
        }
    }

    public boolean hasInput() {
        return !TextUtils.isEmpty(getText().toString());
    }

    public String getTextStr() {
        if (hasInput()) {
            return getText().toString().trim();
        }
        return "";
    }

    /**
     * 输入法
     *
     * @param outAttrs
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new InnerInputConnection(super.onCreateInputConnection(outAttrs), false);
    }

    class InnerInputConnection extends InputConnectionWrapper implements InputConnection {

        public InnerInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        /**
         * 对输入的内容进行拦截
         *
         * @param text
         * @param newCursorPosition
         * @return
         */
        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            // 只能输入字母或者数字
            switch (mInputType) {
                case ConfirmationCodeView.TYPE_MIXTURE:
                    if (!Character.isLetterOrDigit(text.charAt(0))) {
                        return false;
                    }
                    break;
                case ConfirmationCodeView.TYPE_LETTER:
                    if (!Character.isLetter(text.charAt(0))) {
                        return false;
                    }
                    break;
                case ConfirmationCodeView.TYPE_DIGIT:
                    if (!Character.isDigit(text.charAt(0))) {
                        return false;
                    }
                    break;
            }
            setText(text);
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean setSelection(int start, int end) {
            return super.setSelection(start, end);
        }
    }
}
