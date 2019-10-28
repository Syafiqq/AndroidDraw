package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.ImageMode
import java.io.InputStream

class ImageContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer, IImageDrawCallback {
    private var draw: ImageMode? = null

    private val listener: InteractionListener

    private val paint = Paint()

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is ImageMode) return
        draw.onDraw(canvas, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float, event: MotionEvent) {
        if (draw != null || event.pointerCount > 1) return

        listener.attachComponent(this)
        draw = ImageMode(DrawingMode.IMAGE).apply {
            currentPos(x, y)
        }
        listener.requestImage()
    }

    override fun destroyDrawingObject() {
        draw = null
        listener.detachComponent()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draw?.bitmap == null) return true
        if (event.pointerCount > 1) {
            return false
        }

        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> draw?.onFingerDown(x, y, event.getPointerId(0))
            MotionEvent.ACTION_MOVE -> draw?.onFingerMove(x, y, event.getPointerId(0))
            MotionEvent.ACTION_UP ->  draw?.onFingerUp(x, y, event.getPointerId(0))
        }

        drawing.requestInvalidate()
        return true
    }

    override fun onImageRetreived(image: InputStream) {
        draw?.run {
            updateBitmapFromStream(image)
            drawing.requestInvalidate()
        }
    }

    override fun onApply() {
        draw?.run {
            drawing.attachToCanvas(this)
            drawing.requestInvalidate()
        }
    }

    override fun onCancel() {
        destroyDrawingObject()
        drawing.requestInvalidate()
    }

    override fun onScaleUp() {
        draw?.scaledUp()
        drawing.requestInvalidate()
    }

    override fun onScaleDown() {
        draw?.scaledDown()
        drawing.requestInvalidate()
    }

    interface InteractionListener {
        fun attachComponent(callback: IImageDrawCallback)
        fun requestImage()
        fun detachComponent()
    }
}