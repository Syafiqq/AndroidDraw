package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.*
import com.divyanshu.draw.widget.mode.TextMode

class TextContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer, IPaint, ITextDrawCallback {
    private var draw: TextMode? = null

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
            drawing.requestInvalidate()
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
            draw?.updateTextSize(_textSize, paint)
        }

    init {
        val ctx = this.context
        if (ctx !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = ctx
    }

    override fun onDraw(canvas: Canvas, draw: IMode) {
        if (draw !is TextMode) return
        draw.onDraw(canvas, paint)
    }

    override fun onDraw(canvas: Canvas) {
        draw?.let { onDraw(canvas, it) }
    }

    override fun createDrawingObject(x: Float, y: Float) {
        if (draw != null) return

        listener.attachComponent(this, this)
        draw = TextMode(DrawingMode.TEXT).apply {
            color = this@TextContainer.color
            updateTextSize(this@TextContainer.textSize, paint)
            currentPos(x, y)
        }
        listener.requestText()
    }

    override fun destroyDrawingObject() {
        draw = null
        listener.detachComponent()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draw?.text == null) return true

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

    override fun onTextRetrieved(text: String, textSize: Float?) {
        draw?.run {
            if(textSize == null)
                updateText(text, paint)
            else
                updateTextAndSize(text, textSize, paint)
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
        draw?.scaledUp(paint)
        drawing.requestInvalidate()
    }

    override fun onScaleDown() {
        draw?.scaledDown(paint)
        drawing.requestInvalidate()
    }

    interface InteractionListener {
        fun attachComponent(paint: IPaint, callback: ITextDrawCallback)
        fun requestText()
        fun detachComponent()
    }
}