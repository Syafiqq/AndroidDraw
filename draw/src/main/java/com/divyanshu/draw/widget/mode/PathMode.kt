package com.divyanshu.draw.widget.mode

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import com.divyanshu.draw.widget.contract.CanvasContract
import com.divyanshu.draw.widget.contract.DrawingHolderContract

class PathMode(override val canvas: CanvasContract) : Path(), DrawingHolderContract {
    private var _color = 0
    private var _strokeWidth = 0F
    private var _alpha = 0

    private var curX = 0F
    private var curY = 0F
    private var initX = 0F
    private var initY = 0F

    override var color: Int
        get() = _color
        set(value) {
            _color = value
        }
    override var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
        }
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = value
        }

    override fun onDraw(canvas: Canvas, paint: Paint) {
        paint.color = color
        paint.strokeWidth = strokeWidth
        canvas.drawPath(this, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    override fun onFingerDown(x: Float, y: Float) {
        reset()
        moveTo(x, y)
        initialPos(x, y)
        currentPos(x, y)
        canvas.requestInvalidate()
    }

    override fun onFingerMove(x: Float, y: Float) {
        quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2)
        currentPos(x, y)
        canvas.requestInvalidate()
    }

    override fun onFingerUp(x: Float, y: Float) {
        lineTo(curX, curY)

        if (initX == curX && initY == curY) {
            lineTo(curX, curY + 2)
            lineTo(curX + 1, curY + 2)
            lineTo(curX + 1, curY)
        }
        canvas.requestInvalidate()
        canvas.attachToCanvas()
    }

    private fun currentPos(x: Float, y: Float) {
        curX = x
        curY = y
    }

    private fun initialPos(x: Float, y: Float) {
        initX = x
        initY = y
    }
}