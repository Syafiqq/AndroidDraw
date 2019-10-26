package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.PathMode

class TextContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer, IPaint, TextDrawCallback {
    private var draw: PathMode? = null

    private val listener: InteractionListener

    private var _color = 0
    private var _alpha = 0
    private var _textSize = 0F

    private val paint = Paint()

    override var color: Int
        get() = _color
        set(value) {
            @ColorInt
            val alphaColor = ColorUtils.setAlphaComponent(value, alpha)
            _color = alphaColor
            draw?.color = alphaColor
        }
    override var strokeWidth = 0F
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = (value * 255) / 100
            color = color
        }
    override var textSize: Float
        get() = _textSize
        set(value) {
            _textSize = value
        }

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is PathMode) return
        draw.decorate(paint)
        canvas.drawPath(draw, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float) {
        if (draw != null) return

        listener.attachComponent(this, this)
        draw = PathMode(DrawingMode.LINE).apply {
            color = this@TextContainer.color
            strokeWidth = this@TextContainer.strokeWidth
            onFingerDown(x, y)
        }
        drawing.requestInvalidate()
    }

    override fun destroyDrawingObject() {
        draw = null
        listener.detachComponent()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_MOVE -> draw?.onFingerMove(x, y)
            MotionEvent.ACTION_UP -> {
                draw?.onFingerUp(x, y)
                draw?.let(drawing::attachToCanvas)
            }
        }

        drawing.requestInvalidate()
        return true
    }

    override fun onTextRetrieved(text: String, textSize: Float?) {
        textSize?.let { this@TextContainer.textSize = textSize }
    }

    interface InteractionListener {
        fun attachComponent(paint: IPaint, callback: TextDrawCallback)
        fun detachComponent()
    }
}