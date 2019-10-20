package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.contract.DrawingHolderContract
import java.util.*

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaths = LinkedList<DrawingHolderContract>()
    private var mPathsR = LinkedList<DrawingHolderContract>()

    private var mLastPaths = LinkedList<DrawingHolderContract>()
    private var mUndonePaths = LinkedList<DrawingHolderContract>()

    private var mPaint = Paint()
    private var mPath :DrawingHolderContract? = null

    private var mIsSaving = false

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
    }

    fun undo() {
        /*if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedList<DrawingHolderContract>
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
        /*if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        addPath(lastKey, mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()*/
    }

    fun setColor(newColor: Int, alpha: Int = 255) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, alpha)
        mPath?.color = alphaColor
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha*255)/100
        mPath?.let {
            setColor(it.color, alpha)
        }
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        mPath?.strokeWidth = newStrokeWidth
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

    fun addPath(path: MyPath, options: PaintOptions) {
        //mPaths[path] = options
    }

    override fun onDraw(canvas: Canvas) {
        /*super.onDraw(canvas)

        for ((key, value) in mPaths) {
            changePaint(value)
            canvas.drawPath(key, mPaint)
        }

        changePaint(mPaintOptions)
        canvas.drawPath(mPath, mPaint)*/
    }

    private fun changePaint(paintOptions: PaintOptions) {
        /*mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth*/
    }

    fun clearCanvas() {
        /*mLastPaths = mPaths.clone() as LinkedList<DrawingHolderContract>
        mPath.reset()
        mPaths.clear()
        invalidate()*/
    }

    private fun actionDown(x: Float, y: Float) {
        /*mPath.reset()
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y*/
    }

    private fun actionMove(x: Float, y: Float) {
        /*mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y*/
    }

    private fun actionUp() {
        /*mPath.lineTo(mCurX, mCurY)

        // draw a dot on click
        if (mStartX == mCurX && mStartY == mCurY) {
            mPath.lineTo(mCurX, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY + 2)
            mPath.lineTo(mCurX + 1, mCurY)
        }

        mPaths[mPath] = mPaintOptions
        mPath = MyPath()
        mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        /*val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                actionDown(x, y)
                mUndonePaths.clear()
            }
            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }

        invalidate()*/
        return true
    }

    fun toggleEraser() {
        /*isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()*/
    }

}