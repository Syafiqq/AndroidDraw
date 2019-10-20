package com.divyanshu.draw.widget.contract

import android.view.MotionEvent

interface TouchContract {
    fun onTouchEvent(event: MotionEvent): Boolean
    fun onFingerDown(x: Float, y: Float)
    fun onFingerMove(x: Float, y: Float)
    fun onFingerUp(x: Float, y: Float)
}