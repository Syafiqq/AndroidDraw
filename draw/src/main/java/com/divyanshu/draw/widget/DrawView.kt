package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.component.PathContainer
import com.divyanshu.draw.widget.contract.CanvasContract
import com.divyanshu.draw.widget.contract.DrawingMode

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), CanvasContract {
    private var _drawingMode: DrawingMode = DrawingMode.LINE
    val drawingTools: Map<DrawingMode, PathContainer> = mapOf(
            DrawingMode.LINE to PathContainer(context, this)
    )

    override fun attachToCanvas() {

    }

    override fun requestInvalidate() {
        invalidate()
    }

    fun clearCanvas() {
    }

    fun undo() {
    }

    fun redo() {
    }

    override fun onDraw(canvas: Canvas) {
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        draw(canvas)
        return bitmap
    }
}