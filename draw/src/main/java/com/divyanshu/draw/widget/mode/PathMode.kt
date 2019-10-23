package com.divyanshu.draw.widget.mode

import android.graphics.Path

class PathMode : Path() {
    var color = 0
    var strokeWidth = 0F

    private var curX = 0F
    private var curY = 0F
    private var initX = 0F
    private var initY = 0F

    fun onFingerDown(x: Float, y: Float) {
        reset()
        moveTo(x, y)
        initialPos(x, y)
        currentPos(x, y)
    }

    fun onFingerMove(x: Float, y: Float) {
        quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2)
        currentPos(x, y)
    }

    fun onFingerUp(x: Float, y: Float) {
        lineTo(curX, curY)

        if (initX == curX && initY == curY) {
            lineTo(curX, curY + 2)
            lineTo(curX + 1, curY + 2)
            lineTo(curX + 1, curY)
        }
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