package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode

class TextMode(override val mode: DrawingMode) : IMode {
    var color = 0
    var textSize = 0F

    var text: String? = null

    private var curX = 0F
    private var curY = 0F
    private var initX = 0F
    private var initY = 0F

    fun onFingerDown(x: Float, y: Float) {
        initialPos(x, y)
        currentPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        currentPos(x, y)
    }

    fun onFingerUp(x: Float, y: Float) {
        currentPos(x, y)
    }

    private fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun initialPos(x: Float, y: Float) {
        initX = x
        initY = y
    }

    private fun decorate(paint: Paint) {
        paint.textSize = textSize
        paint.color = color
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        text?.let {
            decorate(paint)
            canvas.drawText(it, initX, initY, paint)
        }
    }
}