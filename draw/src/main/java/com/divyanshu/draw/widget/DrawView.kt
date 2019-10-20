package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.PathMode
import java.util.*

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), CanvasContract, DrawingModeContract, PaintContract {
    private var _drawingMode: DrawingMode = DrawingMode.LINE
    private var _color = 0
    private var _strokeWidth = 0F
    private var _alpha = 0

    override var drawingMode: DrawingMode
        get() = _drawingMode
        set(value) {
            _drawingMode = value
            destroyDrawingObject()
        }

    override var color: Int
        get() = _color
        set(value) {
            @ColorInt
            val alphaColor = ColorUtils.setAlphaComponent(value, alpha)
            _color = alphaColor
            mPath?.color = alphaColor
        }
    override var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            mPath?.strokeWidth = value
        }
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = (value*255)/100
            color = color
        }

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

/*    fun setColor(newColor: Int, alpha: Int = 255) {

    }*/

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
        super.onDraw(canvas)

        mPaths.forEach {
            it.onDraw(canvas, mPaint)
        }

        mPath?.onDraw(canvas, mPaint)
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
        mPath?.onFingerDown(x, y)
    }

    private fun actionMove(x: Float, y: Float) {
        mPath?.onFingerMove(x, y)
    }

    private fun actionUp(x: Float, y: Float) {
        mPath?.onFingerUp(x, y)
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

    override fun attachToCanvas() {
        val path = mPath
        if(path != null)
        {
            mPaths.addLast(path)
            mPathsR.addFirst(path)
            destroyDrawingObject()
        }
    }

    override fun requestInvalidate() {
        invalidate()
    }

    override fun createDrawingObject() {
        mPath = when(drawingMode) {
            DrawingMode.LINE -> PathMode(this).apply {
                alpha = alpha
                color = color
                strokeWidth = strokeWidth
            }
            else -> null
        }
    }

    override fun destroyDrawingObject() {
        mPath = null
    }


    fun toggleEraser() {
        /*isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()*/
    }

}