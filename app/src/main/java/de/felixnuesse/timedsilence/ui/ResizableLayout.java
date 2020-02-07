package de.felixnuesse.timedsilence.ui;

/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 06.02.20 - 11:56
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.core.view.MotionEventCompat;

import de.felixnuesse.timedsilence.R;

public class ResizableLayout extends RelativeLayout implements View.OnTouchListener {

    private static final int INVALID_POINTER_ID = 45;
    public static int top_margine;
    private View dragHandle;
    float downRawY;
    float dY;
    float height;


    public ResizableLayout(Context context) {
        super(context);
        init();
    }
    public ResizableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        //addView(inflate(getContext(), R.layout.activity_main,null));
        setOnTouchListener(this);
        dragHandle = this.findViewById(R.id.textView5);
        Log.e("reslay","init");
    }


    float oldX=0;
    float oldY=0;

/*
    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        int action = ev.getAction();
        Log.e("reslay","act:"+action);



        if(action == MotionEvent.ACTION_DOWN){
            downRawY = ev.getRawY();
            height = this.getMeasuredHeight();
            Log.e("reslay","ACTION_DOWN");


            oldX = ev.getX();
            oldY = ev.getY();
            //start timer

        } else if (action == MotionEvent.ACTION_MOVE){
            Log.e("reslay","ACTION_MOVE");
            Log.e("reslay",": "+ev.getRawY());
            //Log.e("reslay",": "+String.valueOf(ev.getX()));
           // Log.e("reslay",": "+ev.getRawY());

            int x = (int) ev.getX();
            int y = (int) ev.getY();

            Log.e("reslay",": "+y);
            //long timerTime = getTime between two event down to Up
            float newX = ev.getX();
            float newY = ev.getY();

            double t = Math.sqrt((newX-oldX) * (newX-oldX) + (newY-oldY) * (newY-oldY));
            int distance = (int)t;

            RelativeLayout rt = findViewById(R.id.testlayout);

            ViewGroup.LayoutParams params = rt.getLayoutParams();

            int newHeight = params.height+distance;
            Log.e("reslay","nh: "+newHeight);

            if(newHeight<2500){
                params.height = newHeight;
            }
            rt.setLayoutParams(params);

        } else {
            Log.e("reslay","else");
            Log.e("reslay","else");
            // change layout margin inside switch

          /*  View parent = (View) this.getParent();
            if(downRawY<parent.getHeight() - height + 2*dragHandle.getHeight()) {
                float rawY = ev.getRawY()>20*dragHandle.getHeight()?ev.getRawY():20*dragHandle.getHeight();
                MarginLayoutParams p = (MarginLayoutParams) this.getLayoutParams();
                p.topMargin = (int)rawY;
                if(p.topMargin!=0)
                    this.top_margine = p.topMargin;
                this.setLayoutParams(p);
            }*/

/*
        }
        return true;
    }
 */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent){

        Log.e("reslay","onInterceptTouchEvent");
        onTouch(this, motionEvent);
       // super.onInterceptTouchEvent(motionEvent);
        return false;
    }



    // The ‘active pointer’ is the one currently moving our object.
    private int mActivePointerId = INVALID_POINTER_ID;

    float mLastTouchX=0;
    float mLastTouchY=0;

    float mPosX=0;
    float mPosY=0;


    @Override
    public boolean onTouch(View view, MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        //mScaleDetector.onTouchEvent(ev);

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.e("reslay","down");
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                Log.e("reslay","move");
                // Find the index of the active pointer and fetch its position
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);

                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;
                final float dy2 = y - mLastTouchY;

                mPosX += dx;
                mPosY += dy;

              //  invalidate();

                // Remember this touch position for the next move event
                mLastTouchX = x;
                mLastTouchY = y;



                Log.e("reslay","else "+mLastTouchY);
                Log.e("reslay","else "+dy2);
                Log.e("reslay","else "+pointerIndex);


                RelativeLayout rt = findViewById(R.id.testlayout);

                ViewGroup.LayoutParams params = rt.getLayoutParams();

                int newHeight = (int) ((int) 100F-mLastTouchY);
                Log.e("reslay","nh: "+newHeight);

                if(newHeight<2500 && newHeight>150){
                    params.height = newHeight;
                    rt.setLayoutParams(params);
                }

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

}
