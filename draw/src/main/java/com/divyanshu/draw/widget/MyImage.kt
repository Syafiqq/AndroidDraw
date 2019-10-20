package com.divyanshu.draw.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import timber.log.Timber
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

interface OnRequestStreamImageListener {
    fun onRequestStreamImage(uri: Uri): InputStream?
}

interface Operation {
    fun setImage(uri: Uri)
    fun placeTo(x: Float, y: Float)
    fun placeTo(x: Float, y: Float, pointerId: Int)
    fun setTouchDiffer(x: Float, y: Float)
    fun isInside(x: Float, y: Float) : Boolean
    fun scaledUp()
    fun scaledDown()
    fun setPointer(pointerId: Int)
}

class MyImage(private val listener: OnRequestStreamImageListener): Operation {
    override fun setPointer(pointerId: Int) {
        ptx = pointerId
    }

    override fun setTouchDiffer(x: Float, y: Float) {
        val r = rectScaled ?: return
        dX = (x - r.left).roundToInt()
        dY = (y - r.top).roundToInt()
    }

    override fun isInside(x: Float, y: Float): Boolean {
        val r = rectScaled ?: return false
        Timber.d("[${r.left} ${r.top} ${r.right} ${r.bottom}] [$x $y]")

        return x > r.left && x < r.right && y > r.top && y < r.bottom
    }

    override fun setImage(uri: Uri) {
        bitmap = decodeSampledBitmapFromUri(uri, 1024, 1024)
        if(bitmap == null) {
            throw RuntimeException("cannot retrieve bitmap")
        }
    }

    override fun placeTo(x: Float, y: Float) {
        this.x = x.roundToInt()
        this.y = y.roundToInt()
    }

    override fun placeTo(x: Float, y: Float, pointerId: Int) {
        if(ptx != pointerId) return
        placeTo(x, y)
    }

    override fun scaledUp() {
        val scaleTest = scale + 1
        when {
            abs(scaleTest) > scaledMax -> return
            else -> ++scale
        }
    }

    override fun scaledDown() {
        val scaleTest = scale - 1
        when {
            abs(scaleTest) > scaledMax -> return
            min(bitmap?.width ?: 0, bitmap?.height ?: 0) + (scaleTest * scaleSize) < 0 -> return
            else -> --scale
        }
    }

    private var scale:Int = 0
    private val scaledMax = 10
    private val scaleSize = 64
    var bitmap: Bitmap? = null
    private var rectScaled: Rect? = null
    private var x: Int = 0
    private var y: Int = 0
    private var dX: Int = 0
    private var dY: Int = 0
    private var ptx = -1

    init {
        rectScaled = Rect(0, 0, 0, 0)
    }

    fun getRectScaled() : Rect? {
        val w = bitmap?.width ?: 0
        val h = bitmap?.height ?: 0
        val scaledX = scaleSize * scale
        val scaledY = (scaleSize * h/w) * scale
        val x = this.x - dX
        val y = this.y - dY
        return rectScaled?.apply {
            left = x
            top = y
            right = x + w + scaledX
            bottom = y + h + scaledY
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (height / inSampleSize > reqHeight && width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {

        // First decode with inJustDecodeBounds=true to check dimensions
        val stream = listener.onRequestStreamImage(uri) ?: return null
        stream.mark(1)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        stream.reset()
        val bitmap = BitmapFactory.decodeStream(stream, null, options)
        stream.close()
        return bitmap
    }

    fun onDestroy() {
        bitmap?.recycle()
    }
}