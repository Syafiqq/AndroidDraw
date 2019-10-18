package com.divyanshu.draw.widget

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

interface OnRequestStreamImageListener {
    fun onRequestStreamImage(uri: Uri): InputStream
}

interface Operation {
    fun placeTo(x: Float, y: Float)
    fun scaledUp()
    fun scaledDown()
}

class MyImage(uri: Uri, private val listener: OnRequestStreamImageListener): Operation {
    override fun placeTo(x: Float, y: Float) {
        this.x = x.roundToInt()
        this.y = y.roundToInt()
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
    private val scaledMax = 5
    private val scaleSize = 50
    private var bitmap: Bitmap? = null
    private var rectOri: Rect? = null
    private var rectScaled: Rect? = null
    private var x: Int = 0
    private var y: Int = 0

    init {
        bitmap = decodeSampledBitmapFromUri(uri, 768, 768)
        if(bitmap == null) {
            throw RuntimeException("cannot retrieve bitmap")
        }
        rectOri = Rect(0, 0, 0, 0)
        rectScaled = Rect(0, 0, 0, 0)
    }

    fun getRectOri() : Rect? {
        val scaled = scaleSize * scale
        return rectOri?.apply {
            left = x
            top = y
            right = x + ((bitmap?.width ?: 0) + scaled)
            bottom = y + ((bitmap?.height ?: 0) + scaled)
        }
    }

    fun getRectScaled() : Rect? {
        val scaled = scaleSize * scale
        return rectScaled?.apply {
            left = x
            top = y
            right = x + ((bitmap?.width ?: 0) + scaled)
            bottom = y + ((bitmap?.height ?: 0) + scaled)
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = reqHeight
            val halfWidth = reqWidth

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromUri(uri: Uri, reqWidth: Int, reqHeight: Int): Bitmap? {

        // First decode with inJustDecodeBounds=true to check dimensions
        val stream = listener.onRequestStreamImage(uri)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(stream, null, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeStream(stream, null, options)?.apply {
            stream.close()
        }
    }
}