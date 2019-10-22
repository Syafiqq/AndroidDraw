package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.contract.*

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var _drawingMode: DrawingMode = DrawingMode.LINE

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        draw(canvas)
        return bitmap
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}