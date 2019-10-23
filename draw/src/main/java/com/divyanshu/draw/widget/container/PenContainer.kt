package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.contract.IDrawingContainer
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.IPaint
import com.divyanshu.draw.widget.mode.PathMode

class PenContainer(override val context: Context, override val drawing: ICanvas) : IDrawingContainer, IPaint {
    var draw: PathMode? = null

    private val listener: InteractionListener

    private var _color = 0
    private var _strokeWidth = 0F
    private var _alpha = 0

    private val paint = Paint()


    override var color: Int
        get() = _color
        set(value) {
            @ColorInt
            val alphaColor = ColorUtils.setAlphaComponent(value, alpha)
            _color = alphaColor
            draw?.color = alphaColor
        }
    override var strokeWidth: Float
        get() = _strokeWidth
        set(value) {
            _strokeWidth = value
            draw?.strokeWidth = value
        }
    override var alpha: Int
        get() = _alpha
        set(value) {
            _alpha = (value * 255) / 100
            color = color
        }

    init {
        if (context !is InteractionListener) {
            throw ClassCastException("context must implement InteractionListener")
        }

        listener = context
        with(paint) {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
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

        listener.attachPaint(this)
        draw = PathMode().apply {
            color = this@PenContainer.color
            strokeWidth = this@PenContainer.strokeWidth
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

    interface InteractionListener {
        fun attachPaint(paint: IPaint)
        fun detachComponent()
    }
}