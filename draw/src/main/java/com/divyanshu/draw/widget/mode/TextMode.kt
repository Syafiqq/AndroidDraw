package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode

class TextMode(override val mode: DrawingMode) : IMode {
    private val selThreshold = 32

    var color = 0
    var textSize = 0F
        private set

    var text: String? = null
        private set
    private var dim = Rect()

    var isInBound = false

    private var curX = 0F
    private var curY = 0F
    private var difX = 0F
    private var difY = 0F

    fun onFingerDown(x: Float, y: Float) {
        isInBound = isInBound(x, y)
        if(isInBound)
            diffPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        if(isInBound)
            currentPos(x + difX, y + difY)
    }

    fun onFingerUp(x: Float, y: Float) {
        isInBound = false
    }

    fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun diffPos(x: Float, y: Float) {
        difX = curX - x
        difY = curY - y
    }

    private fun isInBound(x: Float, y: Float): Boolean {
        return x > (curX - selThreshold) && x < (curX + dim.width() + selThreshold) &&
                y > (curY - selThreshold) && y < (curY + dim.height() + selThreshold)
    }

    private fun decorate(paint: Paint) {
        paint.textSize = textSize
        paint.color = color
    }

    fun updateText(text: String, paint: Paint) {
        this.text = text
        updateTextDimension(paint)
    }

    fun updateTextSize(textSize: Float, paint: Paint) {
        this.textSize = textSize
        updateTextDimension(paint)
    }

    fun updateTextAndSize(text: String, textSize: Float, paint: Paint) {
        this.text = text
        this.textSize = textSize
        updateTextDimension(paint)
    }

    private fun updateTextDimension(paint: Paint) {
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