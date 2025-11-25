package com.infotech.wishmaplus.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.infotech.wishmaplus.R;

public class PinEntryEditTextBox extends EditText {
    public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";
    int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_selected}, // selected
            new int[]{android.R.attr.state_focused}, // focused
            new int[]{-android.R.attr.state_focused}, // unfocused
    };
    int[] mColors = new int[]{
            Color.BLACK,
            Color.WHITE,
            Color.WHITE
    };
    ColorStateList mColorStates = new ColorStateList(mStates, mColors);
    private float mSpace = 15; //15 dp by default, space between the lines
    private float mCharSize;
    private float mNumChars = 4;
    private float mLineSpacing = 8; //8dp by default, height of the text from our lines
    private int mMaxLength = 4;
    private OnClickListener mClickListener;
    private float mLineStroke = 1; //1dp by default
    private float mLineStrokeSelected = 2; //1dp by default
    private Paint mLinesPaint, mFillPaint, textPaint;


    public PinEntryEditTextBox(Context context) {
        super(context);
    }

    public PinEntryEditTextBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinEntryEditTextBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinEntryEditTextBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = /*multi **/  context.getResources().getDimension(com.intuit.sdp.R.dimen._1sdp);
        mLineStrokeSelected = /*multi **/ context.getResources().getDimension(com.intuit.sdp.R.dimen._1sdp);

        textPaint = new Paint(getPaint());
        textPaint.setColor(ContextCompat.getColor(context,android.R.color.black));

        mFillPaint = new Paint(getPaint());
        mFillPaint.setColor(ContextCompat.getColor(context, android.R.color.white));
        mFillPaint.setStyle(Paint.Style.FILL);
        //mFillPaint.setShadowLayer(10,0.2f,0.2f,ContextCompat.getColor(context, R.color.grey_2));

        //-- warning, Honeycomb and above only
        //-- this will reduce draw performance of view
        //-- but is required to support drawing filters, like shadow, blur etc
        setLayerType(LAYER_TYPE_SOFTWARE,mFillPaint);

        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeWidth(mLineStroke);
        if (!isInEditMode()) {
           /* TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorAccent, outValue, true);
            final int colorActivated = outValue.data;*/
            mColors[0] = context.getColor(R.color.colorAccent);
            mColors[1] = context.getColor(R.color.grey_1);
            mColors[2] = context.getColor(R.color.grey_1);

            /*context.getTheme().resolveAttribute(R.attr.colorOnPrimary, outValue, true);
            final int colorDark = outValue.data;
            mColors[1] = colorDark;

            context.getTheme().resolveAttribute(R.attr.colorOnPrimary, outValue, true);
            final int colorHighlight = outValue.data;
            mColors[2] = colorHighlight;*/
        }
        setBackgroundResource(0);

        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels for our density
        mMaxLength = attrs.getAttributeIntValue(XML_NAMESPACE_ANDROID, "maxLength", 4);
        mNumChars = mMaxLength;

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // When tapped, move cursor to end of text.
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });

    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        if (mSpace < 0) {
            mCharSize = (availableWidth / (mNumChars * 2 - 1));
        } else {
            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;
        }

        int startX = getPaddingLeft();
        int bottom = getHeight() - getPaddingBottom();
        int top = getPaddingBottom();
        //Text Width
        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);

        for (int i = 0; i < mNumChars; i++) {
            updateColorForLines(i == textLength);
            // canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mLinesPaint);

            canvas.drawRoundRect(new RectF(startX-5, top - mLineSpacing, startX + mCharSize+10, bottom + mLineSpacing), 15, 15, mFillPaint);
            canvas.drawRoundRect(new RectF(startX-5, top - mLineSpacing, startX + mCharSize+10, bottom + mLineSpacing), 15, 15, mLinesPaint);

            if (getText().length() > i) {
                float middleX = startX + mCharSize / 2;
                canvas.drawText(text, i, i + 1, middleX - textWidths[0] / 2, (bottom-6) -mLineSpacing, textPaint);
            }

            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }


    private int getColorForState(int... states) {
        return mColorStates.getColorForState(states, Color.DKGRAY);
    }

    /**
     * @param next Is the current char the next character to be input?
     */
    private void updateColorForLines(boolean next) {
        if (isFocused()) {

            mLinesPaint.setStrokeWidth(mLineStrokeSelected);
            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
            if (next) {
                mLinesPaint.setColor(getColorForState(android.R.attr.state_selected));
            }
        } else {
            mLinesPaint.setStrokeWidth(mLineStroke);
            mLinesPaint.setColor(getColorForState(-android.R.attr.state_focused));
        }
    }
}
