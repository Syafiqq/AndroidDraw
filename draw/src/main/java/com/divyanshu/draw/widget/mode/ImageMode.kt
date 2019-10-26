package com.divyanshu.draw.widget.mode

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.divyanshu.draw.util.ImageUtil
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.IMode
import java.io.InputStream
import kotlin.math.roundToInt

class ImageMode(override val mode: DrawingMode) : IMode {
    private val sizeThreshold = 1024
    var bitmap: Bitmap? = null
    private var rectScaled: Rect = Rect()

    private var scale:Int = 0
    private val scaledMax = 10
    private val scaleSize = 64

    private var isInBound = false

    private var curX = 0F
    private var curY = 0F
    private var difX = 0F
    private var difY = 0F

    fun onFingerDown(x: Float, y: Float) {
    }

    fun onFingerMove(x: Float, y: Float) {
    }

    fun onFingerUp(x: Float, y: Float) {
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
        return false
    }

    fun updateBitmapFromStream(image: InputStream) {
        bitmap = ImageUtil.decodeSampledBitmapFromStream(image, sizeThreshold, sizeThreshold)
    }

    private fun getRectScaled() : Rect {
        val w = bitmap?.width ?: 0
        val h = bitmap?.height ?: 0
        val scaledX = scaleSize * scale
        val scaledY = (scaleSize * h/w) * scale
        val x = this.curX - difX
        val y = this.curY - difY
        return rectScaled.apply {
            left = x.roundToInt()
            top = y.roundToInt()
            right = (x + w + scaledX).roundToInt()
            bottom = (y + h + scaledY).roundToInt()
        }
    }

    fun scaledUp(paint: Paint) {
    }

    fun scaledDown(paint: Paint) {
    }

    fun onDraw(canvas: Canvas, paint: Paint) {
        bitmap?.let {
            canvas.drawBitmap(it, null, getRectScaled(), paint)
        }
    }
}