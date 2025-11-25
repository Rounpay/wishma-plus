package com.infotech.wishmaplus.Utils.VideoEdit.Trimmer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.infotech.wishmaplus.R;

/**
 * Created by Vishnu Agarwal on 15-10-2024.
 */

public class TrimView extends View {

    public TrimView(Context context){
        super(context);
    }

    public TrimView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    //Paint bgPaint= new Paint();

    private Paint bgPaint = new Paint();
    {
        bgPaint.setColor(Color.TRANSPARENT);
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
    }



    private Paint sliderPaint = new Paint();
    {
        sliderPaint.setColor(Color.parseColor("#7A000000"));
        sliderPaint.setAntiAlias(true);
    }

    private Paint thumbPaint = new Paint();
    {
        thumbPaint.setColor(Color.parseColor("#F6A400"));
        thumbPaint.setAntiAlias(true);
        thumbPaint.setStrokeCap(Paint.Cap.ROUND);
    }
    private Paint greenPaint = new Paint();
    {
        greenPaint.setColor(Color.GREEN);
        greenPaint.setAntiAlias(true);
        greenPaint.setStrokeCap(Paint.Cap.ROUND);
    }
   /* private val bracketPaint = Paint().apply {
        color = Color.BLACK
        textSize = dpToPx(14).toFloat()
        isAntiAlias = true
        textAlignment = View.TEXT_ALIGNMENT_CENTER
    }*/

    private Paint bracketPaint = new Paint();
    {
        bracketPaint.setColor(Color.BLACK);
        bracketPaint.setAntiAlias(true);
        bracketPaint.setTextSize((float)dpToPx(14));
        //bracketPaint.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private RectF bgLine = new RectF();
    private RectF mainLine = new RectF();
    private RectF mainLineTop = new RectF();
    private RectF mainLineBottom = new RectF();
    private RectF progressLine = new RectF();
    private RectF leftAnchor = new RectF();
    private RectF rightAnchor = new RectF();
    private int mainLineHeight = dpToPx(50);
    private float anchorWidth = dpToPx(24);
    private float sliderHeight = dpToPx(50);
    private float radius = dpToPx(5);

    public TrimChangeListener onTrimChangeListener = null;

    public long max = 100;

   public long progress = 0;

    public void setProgress(long value) {
        this.progress = value;
        calculateProgress(true);
        invalidate();
    }

    public long trim = max / 3;
    public void setTrim(long value) {
        this.trim = value;
        calculateLeftandRight(true);
    }

    public long trimStart = 0;
    public  long minTrim = 0;
    public long maxTrim = max;

    private long maxPx = 0;
    @Override
    public void onMeasure(int widthMeasureSpec , int heightMeasureSpec ) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), (int) sliderHeight);
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            maxPx = (getMeasuredWidth() - (int)(2 * anchorWidth));
            bgLine.set(anchorWidth, (getMeasuredHeight() / 2f) - (mainLineHeight / 2), anchorWidth + maxPx, (getMeasuredHeight() / 2f) + (mainLineHeight / 2));
            mainLine.set(anchorWidth, (getMeasuredHeight() / 2f) - (mainLineHeight / 2), anchorWidth + (float)(trim * maxPx / max), (getMeasuredHeight() / 2f) + (mainLineHeight / 2));
            mainLineTop.set(anchorWidth-dpToPx(5), 0, anchorWidth + (float) (trim * maxPx / max)+dpToPx(5), dpToPx(4));
            mainLineBottom.set(anchorWidth-dpToPx(5), getMeasuredHeight()-dpToPx(4), anchorWidth + (float) (trim * maxPx / max)+dpToPx(5), getMeasuredHeight());
            progressLine.set(anchorWidth, (getMeasuredHeight() / 2f) - (mainLineHeight / 2), anchorWidth + (trim * progress / 100f), (getMeasuredHeight() / 2f) + (mainLineHeight / 2));
            leftAnchor.set(0f, 0f, anchorWidth, getMeasuredHeight());
            rightAnchor.set(mainLine.right, 0f, mainLine.right + anchorWidth, getMeasuredHeight()
            );
        }
    }

    @Override
    public void onDraw(Canvas canvas ) {
        canvas.drawRoundRect(bgLine, radius, radius, bgPaint);
        canvas.drawRect(mainLine, sliderPaint);
        canvas.drawRect(mainLineTop, thumbPaint);
        canvas.drawRect(mainLineBottom, thumbPaint);
        // top line
        /*canvas.drawRect(lThumbWidth + lThumbOffset,
                0,
                rThumbOffset,
                lineTop, mLinePaint);

        // bottom line
        canvas.drawRect(lThumbWidth + lThumbOffset,
                lineBottom,
                rThumbOffset,
                height, mLinePaint);*/

        canvas.drawRoundRect(leftAnchor, radius, radius, thumbPaint);
        canvas.drawRoundRect(rightAnchor, radius, radius, thumbPaint);
        canvas.drawRect(progressLine, greenPaint);
        canvas.drawText("[", leftAnchor.left + anchorWidth * 2 / 5, (float) (leftAnchor.bottom - sliderHeight / 2.5), bracketPaint);
        canvas.drawText("]", rightAnchor.left + anchorWidth * 2 / 5, (float) (rightAnchor.bottom - sliderHeight / 2.5), bracketPaint);
    }

    private Captured captured = Captured.WHOLE;

    private long initTrim = 0;
    private long initTrimStart = 0;
    private float initx = 0;
    private float initrx = 0;
    private float initlx = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event )  {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            initTrim = trim;
            initTrimStart = trimStart;
            initx = event.getX();
            if(rightAnchor.contains(event.getX(), event.getY()) ){
                initrx = rightAnchor.left;
                if(minTrim==maxTrim){
                    initlx = leftAnchor.left;
                    captured= Captured.WHOLE;
                }else {
                    captured= Captured.RIGHT;
                }

            }else if(leftAnchor.contains(event.getX(), event.getY())){
                initlx = leftAnchor.left;
                if(minTrim==maxTrim){
                    initrx = rightAnchor.left;
                    captured= Captured.WHOLE;
                }else {
                    captured= Captured.LEFT;
                }
            }else {
                initrx = rightAnchor.left;
                initlx = leftAnchor.left;
                captured = Captured.WHOLE;
            }
            onTrimChangeListener.onDragStarted(trimStart, trim);
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            float dx = event.getX() - initx;
            if(captured ==Captured.LEFT){
                float newx = initlx + dx;
                long newTrimStart = initTrimStart + (int)(dx * max / (maxPx));
                long newTrim = initTrim - newTrimStart + initTrimStart;
                if (newTrim >= minTrim && newTrim <= maxTrim && newx >= 0) {
                    trimStart = newTrimStart;
                    trim = newTrim;
                    calculateLeftandRight(true);
                    onTrimChangeListener.onLeftEdgeChanged(trimStart, trim);
                }
            }else if(captured == Captured.RIGHT){
                float newx = initrx + dx;
                long newTrim =  (initTrim + (int)(dx * max / (maxPx)));
                if (newTrim >= minTrim && newTrim <= maxTrim && newx + anchorWidth <= getMeasuredWidth()) {
                    trim = newTrim;
                    if (progress > trim) {
                        progress = trim;
                    }
                    calculateLeftandRight(true);
                    onTrimChangeListener.onRightEdgeChanged(trimStart, trim);
                }
            }else if(captured == Captured.WHOLE){
                if (initrx + dx + anchorWidth <= getMeasuredWidth() && initlx + dx >= 0) {
                    trimStart = initTrimStart + (int)(dx * max / (getMeasuredWidth() - 2 * anchorWidth));
                    calculateLeftandRight(true);
                    onTrimChangeListener.onRangeChanged(trimStart, trim);
                }
            }

        }else if(event.getAction() == MotionEvent.ACTION_UP){
            onTrimChangeListener.onDragStopped(trimStart, trim);
        }


        return true;
    }

    private void calculateLeftandRight(boolean invalidate) {
        long trimStartPx = trimStart * maxPx / max;
        long trimPx = trim * maxPx / max;

        leftAnchor.left = trimStartPx;
        leftAnchor.right = leftAnchor.left + anchorWidth;
        mainLine.left = leftAnchor.right;
        mainLineTop.left=leftAnchor.right-dpToPx(5);
        mainLineBottom.left=leftAnchor.right-dpToPx(5);

        rightAnchor.left = (trimStartPx + trimPx) + anchorWidth;
        rightAnchor.right = rightAnchor.left + anchorWidth;
        mainLine.right = rightAnchor.left;
        mainLineTop.right = rightAnchor.left+dpToPx(5);
        mainLineBottom.right = rightAnchor.left+dpToPx(5);

        calculateProgress(false);


        if(invalidate)
            invalidate();
    }

    private void calculateProgress(boolean invalidate) {
        long progressPx = progress * maxPx / max;
        progressLine.left = mainLine.left;
        progressLine.right = progressLine.left + progressPx;

        if (invalidate)
            invalidate();
    }

    enum  Captured {
        LEFT, RIGHT, WHOLE
    }

   public static interface   TrimChangeListener {
       public void onDragStarted(long trimStart, long trim) ;
        public  void  onLeftEdgeChanged(long trimStart, long trim) ;
        public   void  onRightEdgeChanged(long trimStart, long trim) ;
        public   void  onRangeChanged(long trimStart, long trim) ;
        public   void  onDragStopped(long trimStart, long trim) ;
    }

    private int dpToPx(int dp ) {
       return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}

