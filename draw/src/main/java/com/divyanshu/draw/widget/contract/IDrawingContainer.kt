package com.divyanshu.draw.widget.contract

import android.content.Context
import android.graphics.Canvas
import android.view.MotionEvent


interface IDrawingContainer {
    fun createDrawingObject(x: Float, y: Float)
    fun destroyDrawingObject()
    fun onDraw(canvas: Canvas, draw: Any)
    fun onTouchEvent(event: MotionEvent): Boolean
    val context: Context
    val drawing: ICanvas
}