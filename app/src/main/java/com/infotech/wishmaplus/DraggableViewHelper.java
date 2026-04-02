package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import androidx.annotation.NonNull;

public class DraggableViewHelper {

    public static void makeDraggable(View view) {

        ScaleGestureDetector scaleDetector =
                new ScaleGestureDetector(view.getContext(),
                        new ScaleGestureDetector.SimpleOnScaleGestureListener() {

                            float scaleFactor = 1f;

                            @Override
                            public boolean onScale(@NonNull ScaleGestureDetector detector) {

                                scaleFactor *= detector.getScaleFactor();

                                scaleFactor = Math.max(0.5f,
                                        Math.min(scaleFactor, 3f));

                                view.setScaleX(scaleFactor);
                                view.setScaleY(scaleFactor);

                                return true;
                            }
                        });

        final float[] dX = new float[1];
        final float[] dY = new float[1];

        @SuppressLint("ClickableViewAccessibility")
        View.OnTouchListener touchListener =
                (v, event) -> {

                    scaleDetector.onTouchEvent(event);

                    switch (event.getAction()) {

                        case MotionEvent.ACTION_DOWN:

                            dX[0] = v.getX() - event.getRawX();
                            dY[0] = v.getY() - event.getRawY();

                            break;

                        case MotionEvent.ACTION_MOVE:

                            v.animate()
                                    .x(event.getRawX() + dX[0])
                                    .y(event.getRawY() + dY[0])
                                    .setDuration(0)
                                    .start();

                            break;

                        case MotionEvent.ACTION_UP:

                            v.performClick();
                            break;
                    }

                    return true;
                };

        view.setOnTouchListener(touchListener);
    }
}
