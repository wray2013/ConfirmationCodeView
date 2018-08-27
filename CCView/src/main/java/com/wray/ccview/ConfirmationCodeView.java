package com.wray.ccview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ray on 2018/8/21.
 */
public class ConfirmationCodeView extends LinearLayout {

    private static final int SHAPE_LINE = 0;
    private static final int SHAPE_SQUARE = 1;
    private static final int SHAPE_SQUARE_LINE = 2;
    private static final int SHAPE_RECTANGLE = 3;
    private static final int SHAPE_CIRCLE = 4;

    public static final int TYPE_MIXTURE = 0;
    public static final int TYPE_LETTER = 1;
    public static final int TYPE_DIGIT = 2;

    public static final int DIRECTION_LTR = 0;
    public static final int DIRECTION_RTL = 1;

    //****** Major Attr ******
    private int mCount;
    private int mThick;
    private int mSize;
    private int mPadding;
    private String mText;

    //****** Border Attr ******
    private int mBorderShape;
    private int mBorderColor;
    private int mBorderSelectColor;
    private int mBorderRadius;

    //****** Content Attr ******
    private boolean mPasswordMode = false;
    private int mInputType;
    private int mTextColor;
    private int mTextSelectColor;
    private int mPasswordColor;
    private int mPasswordSelectColor;
    private int mContentColor;
    private int mContentSelectColor;

    //****** Default Values ******
    private int defaultWidth = 80;
    private int defaultPadding = 5;
    private int defaultThick = 2;
    private int defaultRadius = 6;

    //****** Internal Vars ******
    private int mDirection = 0;
    private boolean bAutoNext = true;
    private int mFocusIndex = -1;
    private boolean bRefreshing = false;

    //****** Internal View ******
    private Drawable normalGD;
    private Drawable selectGD;
    private ImageView[] mPassArray;
    private RelativeLayout[] mRLArray;
    private CCEditText[] mETArray;

    private char[] mContent;
    private ArrayList<OnInputCompletionListener> mListeners;

    public ConfirmationCodeView(Context context) {
        this(context, null);
    }

    public ConfirmationCodeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmationCodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ConfirmationCodeView);
        mCount = ta.getInteger(R.styleable.ConfirmationCodeView_ccvCount, 6);
        mThick = (int) ta.getDimension(R.styleable.ConfirmationCodeView_ccvLineWidth, defaultThick);
        mSize = (int) ta.getDimension(R.styleable.ConfirmationCodeView_ccvSize, defaultWidth);
        mText = ta.getString(R.styleable.ConfirmationCodeView_ccvText);
        mDirection = ta.getInteger(R.styleable.ConfirmationCodeView_ccvDirection, DIRECTION_LTR);

        mBorderShape = ta.getInteger(R.styleable.ConfirmationCodeView_ccvBorderShape, SHAPE_SQUARE_LINE);
        mBorderColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvBorderColor, Color.LTGRAY);
        mBorderSelectColor = ta.getInteger(R.styleable.ConfirmationCodeView_ccvBorderSelectColor, Color.BLACK);
        mBorderRadius = (int) ta.getDimension(R.styleable.ConfirmationCodeView_ccvBorderRadius, defaultRadius);

        mPasswordMode = ta.getBoolean(R.styleable.ConfirmationCodeView_ccvPasswordMode, false);
        mInputType = ta.getInteger(R.styleable.ConfirmationCodeView_ccvContentType, TYPE_DIGIT);
        mTextColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvTextColor, Color.TRANSPARENT);
        if (mTextColor == Color.TRANSPARENT) {
            mTextColor = mBorderColor;
            mTextSelectColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvTextSelectColor, mBorderSelectColor);
        } else {
            mTextSelectColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvTextSelectColor, mTextColor);
        }
        mPasswordColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvPasswordColor, Color.BLACK);
        mPasswordSelectColor = ta.getColor(R.styleable.ConfirmationCodeView_ccvPasswordSelectColor, mPasswordColor);
        mContentColor = ta.getInteger(R.styleable.ConfirmationCodeView_ccvContentColor, Color.WHITE);
        mContentSelectColor = ta.getInteger(R.styleable.ConfirmationCodeView_ccvContentSelectColor, Color.LTGRAY);

        mPadding = (int) ta.getDimension(R.styleable.ConfirmationCodeView_ccvPadding, defaultPadding);
        ta.recycle();

        if (mBorderShape == SHAPE_CIRCLE) {
            mBorderRadius = mSize / 2;
        }

        initView();
    }

    private void initView() {
        initEditTextArray();
        if (mCount > 0) {
            setText(mText);
        }
    }

    private void initEditTextArray() {
        if (mCount > 0) {
            removeAllViews();
            mRLArray = new RelativeLayout[mCount];
            mETArray = new CCEditText[mCount];
            mPassArray = new ImageView[mCount];
            mContent = new char[mCount];
            for (int n = 0; n < mCount; n++) {
                mContent[n] = (char)-1;
            }
            getETDrawable();
            for (int i = 0; i < mCount; i++) {
                final int finalI = i;

                //******************************************
                //    PASSWORD MODE IMAGE
                //******************************************
                final ImageView ivItem = new ImageView(getContext());
                setImageBG(ivItem);
                mPassArray[i] = ivItem;

                //******************************************
                //    EDIT TEXT
                //******************************************
                final CCEditText etItem = new CCEditText(getContext());
                etItem.setLayoutParams(getETLayoutParams(mSize));
                etItem.setGravity(Gravity.CENTER);
                etItem.setCursorVisible(false);
                etItem.setContentType(mInputType);
                if (mBorderShape == SHAPE_SQUARE || mBorderShape == SHAPE_CIRCLE || mBorderShape == SHAPE_SQUARE_LINE) {
                    etItem.setBackground(normalGD);
                } else {
                    etItem.setBackground(null);
                }
                etItem.setInputChangedListener(new CCEditText.onInputChangedListener() {
                    @Override
                    public void onFinish(CharSequence e) {
                        refreshPassword();
                        mContent[finalI] = etItem.getTextStr().charAt(0);
                        // the last edit text input finished.
                        String wholeContent = getTrueString(mContent, mDirection);
                        if (wholeContent.length() == mCount) {
                            if (mListeners != null && mListeners.size() > 0) {
                                for (OnInputCompletionListener l : mListeners) {
                                    if (l != null)
                                        l.onCompletion(ConfirmationCodeView.this, wholeContent);
                                }
                            }
                        }
                        if (bAutoNext) {
                            switch (mDirection) {
                                case DIRECTION_LTR:
                                    if (finalI < mCount - 1) {
                                        mETArray[finalI + 1].requestFocus();
                                    }
                                    break;
                                case DIRECTION_RTL:
                                    if (finalI > 0) {
                                        mETArray[finalI - 1].requestFocus();
                                    }
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onClear() {
                        refreshPassword();
                        switch (mDirection) {
                            case DIRECTION_LTR:
                                if (finalI > 0) {
                                    mETArray[finalI - 1].requestFocus();
                                }
                                break;
                            case DIRECTION_RTL:
                                if (finalI < mCount - 1) {
                                    mETArray[finalI + 1].requestFocus();
                                }
                                break;
                        }
                    }
                });
                etItem.setFocusableInTouchMode(true);
                etItem.setOnFocusChangeListener(new OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        if (hasFocus) {
                            mFocusIndex = finalI;
                            if (mBorderShape == SHAPE_SQUARE || mBorderShape == SHAPE_CIRCLE || mBorderShape == SHAPE_SQUARE_LINE) {
                                etItem.setBackground(selectGD);
                            } else if (mRLArray[finalI] != null) {
                                mRLArray[finalI].setBackground(selectGD);
                            }
                            ((GradientDrawable) mPassArray[finalI].getDrawable()).setColor(mPasswordSelectColor);
                            if (!mPasswordMode)
                                etItem.setTextColor(mTextSelectColor);
                        } else {
                            if (mFocusIndex == finalI) mFocusIndex = -1;
                            if (mBorderShape == SHAPE_SQUARE || mBorderShape == SHAPE_CIRCLE || mBorderShape == SHAPE_SQUARE_LINE) {
                                etItem.setBackground(normalGD);
                            } else if (mRLArray[finalI] != null) {
                                mRLArray[finalI].setBackground(normalGD);
                            }
                            ((GradientDrawable) mPassArray[finalI].getDrawable()).setColor(mPasswordColor);
                            if (!mPasswordMode)
                                etItem.setTextColor(mTextColor);
                        }
                    }
                });
                etItem.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

                etItem.setTextColor(mTextColor);
                etItem.setHighlightColor(mTextColor);

                mETArray[i] = etItem;

                //******************************************
                //    RELATIVE LAYOUT WRAPPER
                //******************************************
                RelativeLayout rlItem = new RelativeLayout(getContext());
                rlItem.setLayoutParams(getRLLayoutParams(mSize));
                if (mBorderShape == SHAPE_SQUARE || mBorderShape == SHAPE_CIRCLE || mBorderShape == SHAPE_SQUARE_LINE) {
                    rlItem.setBackground(null);
                } else {
                    rlItem.setBackground(normalGD);
                }
                rlItem.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mETArray[finalI].requestFocus();
                    }
                });
                rlItem.addView(mETArray[i]);
                rlItem.addView(mPassArray[i]);
                mRLArray[i] = rlItem;

                addView(mRLArray[i]);
            }
            refreshPassword();
        }
    }

    /****************************************************
     *               Layout Function
     ****************************************************/
    private void setImageBG(ImageView view) {
        GradientDrawable gd = (GradientDrawable) getResources().getDrawable(R.drawable.shape_circle_bg);
        gd.setSize(mSize / 4, mSize / 4);
        gd.setColor(mPasswordColor);
        view.setImageDrawable(gd);
        view.setLayoutParams(getIVLayoutParams(mSize / 4));
    }
    private void getETDrawable() {
        switch (mBorderShape) {
            case SHAPE_LINE:
            case SHAPE_SQUARE_LINE:
                ShapeDrawable shapeDrawableBg = new ShapeDrawable();
                shapeDrawableBg.setPadding(0, 0, 0, 0);
                shapeDrawableBg.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawableBg.getPaint().setColor(mBorderColor);
                shapeDrawableBg.setIntrinsicWidth(mSize);
                shapeDrawableBg.setIntrinsicHeight(mSize);

                ShapeDrawable shapeDrawableFg = new ShapeDrawable();
                shapeDrawableFg.setPadding(0, 0, 0, 0);
                shapeDrawableFg.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawableFg.getPaint().setColor(mContentColor);
                shapeDrawableFg.setIntrinsicWidth(mSize);
                shapeDrawableFg.setIntrinsicHeight(mSize);

                normalGD = new LayerDrawable(new ShapeDrawable[]{shapeDrawableBg, shapeDrawableFg});
                ((LayerDrawable)normalGD).setLayerInset(1, 0, 0, 0, mThick);

                ShapeDrawable shapeDrawableSelectBg = new ShapeDrawable();
                shapeDrawableSelectBg.setPadding(0, 0, 0, 0);
                shapeDrawableSelectBg.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawableSelectBg.getPaint().setColor(mBorderSelectColor);
                shapeDrawableSelectBg.setIntrinsicWidth(mSize);
                shapeDrawableSelectBg.setIntrinsicHeight(mSize);

                ShapeDrawable shapeDrawableSelectFg = new ShapeDrawable();
                shapeDrawableSelectFg.setPadding(0, 0, 0, 0);
                shapeDrawableSelectFg.getPaint().setStyle(Paint.Style.FILL);
                shapeDrawableSelectFg.getPaint().setColor(mContentSelectColor);
                shapeDrawableSelectFg.setIntrinsicWidth(mSize);
                shapeDrawableSelectFg.setIntrinsicHeight(mSize);

                selectGD = new LayerDrawable(new ShapeDrawable[]{shapeDrawableSelectBg, shapeDrawableSelectFg});
                ((LayerDrawable)selectGD).setLayerInset(1, 0, 0, 0, mThick);
                break;
            case SHAPE_SQUARE:
            case SHAPE_CIRCLE:
                normalGD = new GradientDrawable();
                ((GradientDrawable)normalGD).setShape(GradientDrawable.RECTANGLE);
                ((GradientDrawable)normalGD).setColor(mContentColor);
                ((GradientDrawable)normalGD).setCornerRadius(mBorderRadius);
                ((GradientDrawable)normalGD).setStroke(mThick, mBorderColor);
                ((GradientDrawable)normalGD).setSize(mSize, mSize);

                selectGD = new GradientDrawable();
                ((GradientDrawable)selectGD).setShape(GradientDrawable.RECTANGLE);
                ((GradientDrawable)selectGD).setColor(mContentSelectColor);
                ((GradientDrawable)selectGD).setCornerRadius(mBorderRadius);
                ((GradientDrawable)selectGD).setStroke(mThick, mBorderSelectColor);
                ((GradientDrawable)selectGD).setSize(mSize, mSize);
                break;
            case SHAPE_RECTANGLE:
                normalGD = new GradientDrawable();
                ((GradientDrawable)normalGD).setShape(GradientDrawable.RECTANGLE);
                ((GradientDrawable)normalGD).setColor(mContentColor);
                ((GradientDrawable)normalGD).setCornerRadius(mBorderRadius);
                ((GradientDrawable)normalGD).setStroke(mThick, mBorderColor);

                selectGD = new GradientDrawable();
                ((GradientDrawable)selectGD).setShape(GradientDrawable.RECTANGLE);
                ((GradientDrawable)selectGD).setColor(mContentSelectColor);
                ((GradientDrawable)selectGD).setCornerRadius(mBorderRadius);
                ((GradientDrawable)selectGD).setStroke(mThick, mBorderSelectColor);
                break;
        }
    }

    private LayoutParams getRLLayoutParams(int size) {
        LayoutParams lp = new LayoutParams(size,
                (mBorderShape == SHAPE_SQUARE || mBorderShape == SHAPE_SQUARE_LINE || mBorderShape == SHAPE_CIRCLE) ? size : LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        lp.setMargins(mPadding, mPadding, mPadding, mPadding);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    private RelativeLayout.LayoutParams getIVLayoutParams(int size) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(size, size);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        return lp;
    }

    private RelativeLayout.LayoutParams getETLayoutParams(int size) {
        RelativeLayout.LayoutParams lp;
        if (size > 0) {
            lp = new RelativeLayout.LayoutParams(size, LayoutParams.WRAP_CONTENT);
        } else {
            lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        return lp;
    }

    private void refreshPassword() {
        if (mPasswordMode) {
            for (int i = 0; i < mCount; i ++) {
                mETArray[i].hideInput();
                mPassArray[i].setVisibility(mETArray[i].hasInput() ? View.VISIBLE : View.GONE);
            }
        } else {
            for (int i = 0; i < mCount; i ++) {
                mETArray[i].showInput(mTextColor, mTextSelectColor);
                mPassArray[i].setVisibility(View.GONE);
            }
        }
    }

    private String getTrueString(char[] content, int direction) {
        String trueString = "";
        for (char c : content) {
            if (c != (char)-1) {
                trueString += String.valueOf(c);
            }
        }
        return direction == DIRECTION_RTL ? new StringBuilder(trueString).reverse().toString() : trueString;
    }

    private void setOnInputCompletionListener(List<OnInputCompletionListener> l) {
        if (l == null) return;
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.addAll(l);
    }

    /**
     * Interface definition for a callback to be invoked when the input is completed.
     */
    public interface OnInputCompletionListener {
        void onCompletion(ConfirmationCodeView ccv, String content);
    }

    /****************************************************
     *               Public Function
     ****************************************************/
    public void refreshView() {
        ArrayList<OnInputCompletionListener> lastListeners =
                mListeners == null ? null : (ArrayList<OnInputCompletionListener>) mListeners.clone();
        removeOnInputCompletionListener(null);
        char[] lastContent = getText();
        int lastFocusIndex = getFocusIndex();
        initEditTextArray();
        setText(lastContent);
        setFocus(lastFocusIndex);
        setOnInputCompletionListener(lastListeners);
    }

    /**
     * Requires focus of the indicated edit text.
     *
     * @param index the index of edit text.
     */
    public void setFocus(int index) {
        index = ((index != -1 && mDirection == DIRECTION_RTL) ? mCount - index - 1 : index);
        if (index < 0) {
            for (CCEditText e: mETArray) {
                e.clearFocus();
            }
        } else if (index < mCount) {
            mETArray[index].requestFocus();
        }
    }

    /**
     * Gets the current focus index.
     * @return The index of current focus EditText, -1 if not in focus.
     */
    public int getFocusIndex() {
        if (mFocusIndex == -1) return mFocusIndex;
        return mDirection == DIRECTION_RTL ? mCount - mFocusIndex - 1 : mFocusIndex;
    }

    /**
     * Register a callback to be invoked when the input is completed.
     * It contains the content of the complete input.
     *
     * @param l The callback that will run
     */
    public void setOnInputCompletionListener(OnInputCompletionListener l) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(l);
    }

    public void removeOnInputCompletionListener(OnInputCompletionListener l) {
        if (mListeners != null) {
            if (l == null) {
                mListeners.clear();
                return;
            }
            int i = mListeners.indexOf(l);

            if (i >= 0) {
                mListeners.remove(i);
            }
        }
    }

    /**
     * Removes any input from the confirmation code view.
     */
    public void clearInput() {
        for (CCEditText e : mETArray) {
            if (e != null) {
                e.setText("");
            }
        }
        mText = "";
    }

    /**
     * Sets the string value of the ConfirmationCodeView.
     *
     * @param text the string value of the ConfirmationCodeView.
     */
    public void setText(String text) {
        if (!TextUtils.isEmpty(text)) {
            mText = "";
            int len = text.trim().length();
            int minLen = Math.min(mETArray.length, len);
            for (int i = 0; i < minLen; i++) {
                mText += text.trim().charAt(i);
                mETArray[i].setText(text.trim().charAt(i) + "");
            }
        } else {
            mText = text;
        }
    }

    public void setText(char[] text) {
        if (text == null) return;
        int len = text.length;
        for (int i = 0; i < len; i++) {
            CCEditText e = mETArray[i];
            if (e == null) continue;
            if (text[i] != (char) -1) {
                e.setText(String.valueOf(text[i]));
            } else {
                e.setText("");
            }
        }
    }

    /**
     * Return the text the ConfirmationCodeView is displaying.
     */
    public char[] getText() {
        return mContent;
    }

    /**
     * Sets the mode of the input fields to password or not.
     *
     * @param isPasswordMode Will not show the characters if it set TRUE.
     */
    public void setPasswordMode(boolean isPasswordMode) {
        this.mPasswordMode = isPasswordMode;
        refreshPassword();
    }

    /**
     * Returns a <code>boolean</code> indicating whether the current
     * mode is password (<code>true</code>) or not (<code>false</code>)
     *
     * @return
     */
    public boolean isPasswordMode() {
        return this.mPasswordMode;
    }

    /**
     * Sets the border color.
     *
     * @param color
     */
    public void setBorderColor(@ColorRes int color) {
        this.mBorderColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current border color.</p>
     *
     * @return Returns the current border color.
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * Sets the border selection color.
     *
     * @param color
     */
    public void setBorderSelectColor(@ColorRes int color) {
        this.mBorderSelectColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current border selection color.</p>
     *
     * @return Returns the current border selection color.
     */
    public int getBorderSelectColor() {
        return mBorderSelectColor;
    }

    /**
     * Sets the content color.
     *
     * @param color
     */
    public void setContentColor(@ColorRes int color) {
        this.mContentColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current content color.</p>
     *
     * @return Returns the current content color.
     */
    public int getContentColor() {
        return mContentColor;
    }

    /**
     * Sets the content selection color.
     *
     * @param color
     */
    public void setContentSelectColor(@ColorRes int color) {
        this.mContentSelectColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current content selection color.</p>
     *
     * @return Returns the current content selection color.
     */
    public int getContentSelectColor() {
        return mContentSelectColor;
    }

    /**
     * Sets the text color.
     *
     * @param color
     */
    public void setTextColor(@ColorRes int color) {
        this.mTextColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current text color.</p>
     *
     * @return Returns the current text color.
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Sets the text selection color.
     *
     * @param color
     */
    public void setTextSelectColor(@ColorRes int color) {
        this.mTextSelectColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current text selection color.</p>
     *
     * @return Returns the current text selection color.
     */
    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    /**
     * Sets the password color.
     *
     * @param color
     */
    public void setPasswordColor(@ColorRes int color) {
        this.mPasswordColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current password color.</p>
     *
     * @return Returns the current password color.
     */
    public int getPasswordColor() {
        return mPasswordColor;
    }

    /**
     * Sets the password selection color.
     *
     * @param color
     */
    public void setPasswordSelectColor(@ColorRes int color) {
        this.mPasswordSelectColor = getResources().getColor(color);
        refreshView();
    }

    /**
     * <p>Return the current password selection color.</p>
     *
     * @return Returns the current password selection color.
     */
    public int getPasswordSelectColor() {
        return mPasswordSelectColor;
    }

}
