/**
 * Copyright (c) 2016 Motorola Mobility, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.motorola.samples.mdkdisplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * A class to represent clock view.
 */
public class ClockView extends View {
    private Bitmap mBmpDial;
    private Bitmap mBmpHour;
    private Bitmap mBmpMinute;
    private Bitmap mBmpSecond;

    private BitmapDrawable bmdHour;
    private BitmapDrawable bmdMinute;
    private BitmapDrawable bmdSecond;
    private BitmapDrawable bmdDial;

    private int mWidth;
    private int mHeight;
    private int mTempWidth;
    private int mTempHeight;
    private int centerX;
    private int centerY;

    private Handler tickHandler;

    /**
     * Draw the second hand on clock face every 1 second
     */
    private Runnable tickRunnable = new Runnable() {
        public void run() {
            postInvalidate();
            tickHandler.postDelayed(tickRunnable, 1000);
        }
    };

    public ClockView(Context context, AttributeSet attr) {
        super(context, attr);

        /** Load clock face and hands images */
        mBmpDial = BitmapFactory.decodeResource(getResources(),
                R.drawable.clock_face);
        bmdDial = new BitmapDrawable(getResources(), mBmpDial);

        mBmpHour = BitmapFactory.decodeResource(getResources(),
                R.drawable.hour_pointer);
        bmdHour = new BitmapDrawable(getResources(), mBmpHour);

        mBmpMinute = BitmapFactory.decodeResource(getResources(),
                R.drawable.minute_pointer);
        bmdMinute = new BitmapDrawable(getResources(), mBmpMinute);

        mBmpSecond = BitmapFactory.decodeResource(getResources(),
                R.drawable.second_pointer);
        bmdSecond = new BitmapDrawable(getResources(), mBmpSecond);

        mWidth = 360;
        mHeight = 360;
        centerX = mWidth / 2;
        centerY = mHeight / 2;

        run();
    }

    /** Start draw clock hand */
    public void run() {
        tickHandler = new Handler();
        tickHandler.post(tickRunnable);
    }

    /** Draw clock face and hands */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /** Get currently time */
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        float hourRotate = hour * 30.0f + minute / 60.0f * 30.0f;
        float minuteRotate = minute * 6.0f;
        float secondRotate = second * 6.0f;

        /** Draw clock face */
        bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeight / 2),
                centerX + (mWidth / 2), centerY + (mHeight / 2));
        bmdDial.draw(canvas);

        /** Draw hour hand */
        mTempWidth = bmdHour.getIntrinsicWidth();
        mTempHeight = bmdHour.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(hourRotate, centerX, centerY);
        bmdHour.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeight / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeight / 2));
        bmdHour.draw(canvas);
        canvas.restore();

        /** Draw minute hand */
        mTempWidth = bmdMinute.getIntrinsicWidth();
        mTempHeight = bmdMinute.getIntrinsicHeight();
        canvas.save();
        canvas.rotate(minuteRotate, centerX, centerY);
        bmdMinute.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeight / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeight / 2));
        bmdMinute.draw(canvas);
        canvas.restore();

        /** Draw second hand */
        mTempWidth = bmdSecond.getIntrinsicWidth();
        mTempHeight = bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, centerX, centerY);
        bmdSecond.setBounds(centerX - (mTempWidth / 2), centerY
                - (mTempHeight / 2), centerX + (mTempWidth / 2), centerY
                + (mTempHeight / 2));
        bmdSecond.draw(canvas);
    }
}
