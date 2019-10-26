package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode

class TextMode(override val mode: DrawingMode) : IMode {
    var color = 0
    var textSize = 0F

    var text: String? = null
    var dim = Rect()

    var isInBound = false

    private var curX = 0F
    private var curY = 0F

    fun onFingerDown(x: Float, y: Float) {
        currentPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        currentPos(x, y)
    }

    fun onFingerUp(x: Float, y: Float) {
        currentPos(x, y)
    }

    fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun decorate(paint: Paint) {
        paint.textSize = textSize
        paint.color = color
    }

    fun initializeText(text: String, paint: Paint) {
        this.text = text
        updateTextDimension(paint)
    }

    fun updateTextDimension(paint: Paint) {
        text?.let {
            decorate(paint)
            paint.getTextBounds(it, 0, it.length, dim)
        }
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        text?.let {
            decorate(paint)
            canvas.drawText(it, curX, curY, paint)
        }
    }
}