package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.LinkedHashMap

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaths = LinkedHashMap<MyImage, PaintOptions>()

    private var mLastPaths = LinkedHashMap<MyImage, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<MyImage, PaintOptions>()

    private var mPaint = Paint()
    private var mPath : MyImage? = null
    private var selPath : MyImage? = null
    private var mPaintOptions = PaintOptions()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    var imageOperation : ImageOperation? = null

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    fun undo() {
/*        if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedHashMap<MyImage, PaintOptions>
            mLastPaths.clear()
            invalidate()
            return
        }
        if (mPaths.isEmpty()) {
            return
        }
        val lastPath = mPaths.values.lastOrNull()
        val lastKey = mPaths.keys.lastOrNull()

        mPaths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            mUndonePaths[lastKey] = lastPath
        }
        invalidate()*/
    }

    fun redo() {
/*        if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        addPath(lastKey, mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()*/
    }

    fun setColor(newColor: Int) {
/*        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }*/
    }

    fun setAlpha(newAlpha: Int) {
/*        val alpha = (newAlpha*255)/100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)*/
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
/*        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }*/
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    fun addPath(path: MyImage, options: PaintOptions) {
        /*mPaths[path] = options*/
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((key, value) in mPaths) {
            changePaint(value)
            if(key.bitmap != null && key.getRectScaled() != null)
                canvas.drawBitmap(key.bitmap!!, null, key.getRectScaled()!!, null)
        }

        changePaint(mPaintOptions)
        val path = mPath
        if(path?.bitmap != null && path.getRectScaled() != null)
            canvas.drawBitmap(path.bitmap!!, null, path.getRectScaled()!!, null)
    }

    private fun changePaint(paintOptions: PaintOptions) {
/*        mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth*/
    }

    fun clearCanvas() {
/*        mLastPaths = mPaths.clone() as LinkedHashMap<MyImage, PaintOptions>
        mPath.reset()
        mPaths.clear()
        invalidate()*/
    }

    private fun actionDown(x: Float, y: Float) {
        if(selPath != null)
        else {
            imageOperation?.run {
                mPath = MyImage(this).apply {
                    placeTo(x, y)
                }
                onRequestImage()
            }
        }
    }

    private fun actionMove(x: Float, y: Float) {
        if(selPath == null)
            return
        else {
            selPath?.placeTo(x, y)
            invalidate()
        }
    }

    private fun actionUp() {
        val path = mPath
        if(path != null)
        {
            mPaths[path] = mPaintOptions
            selPath = path
            mPath = null
            mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)
        } else if(selPath != null) {
            selPath = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> actionDown(x, y)
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }

        return true
    }

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()
    }

    fun retrieveImageUri(uri: Uri) {
        mPath?.run {
            setImage(uri)
            invalidate()
        }
    }
}

interface ImageOperation : OnRequestStreamImageListener {
    fun onRequestImage()
}