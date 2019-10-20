package com.divyanshu.draw.widget.contract

import android.graphics.Canvas
import android.graphics.Paint

interface DrawingContract {
    fun onDraw(canvas: Canvas, paint: Paint)
}