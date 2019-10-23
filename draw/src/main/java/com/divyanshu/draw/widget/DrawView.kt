package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.divyanshu.draw.widget.container.PenContainer
import com.divyanshu.draw.widget.contract.DrawingMode
import com.divyanshu.draw.widget.contract.ICanvas
import com.divyanshu.draw.widget.contract.IDrawingContainer
import com.divyanshu.draw.widget.contract.IMode
import com.divyanshu.draw.widget.contract.design.command.ICommand
import com.divyanshu.draw.widget.contract.design.command.ICommandManager
import com.divyanshu.draw.widget.impl.command.ClearCommand
import com.divyanshu.draw.widget.impl.command.DrawCommand
import com.divyanshu.draw.widget.mode.PathMode
import java.util.*
import kotlin.collections.ArrayList

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs), ICanvas, ICommandManager {
    override val recordF = Stack<ICommand>()
    override val recordB = Stack<ICommand>()
    private val holder = ArrayList<IMode>()

    private val toolPath = PenContainer(context, this)

    private var _drawingMode: DrawingMode = DrawingMode.LINE
    private var _drawingTool: IDrawingContainer? = toolPath

    init {
        val paint = Paint().apply {
            alpha = 0xFF
            color = Color.WHITE
            maskFilter = null
            xfermode = null
        }
        setLayerType(LAYER_TYPE_HARDWARE, paint)
    }

    override fun attachToCanvas(draw: IMode) {
        _drawingTool?.destroyDrawingObject()

        val command = DrawCommand(holder, draw)
        command.up()

        recordF.push(command)
        recordB.clear()

        requestInvalidate()
    }

    override fun requestInvalidate() {
        invalidate()
    }

    fun clearCanvas() {
        if (holder.isEmpty()) return

        val command = ClearCommand(holder)
        command.up()

        recordF.push(command)
        recordB.clear()

        requestInvalidate()
    }

    fun undo() {
        if (recordF.isEmpty()) return

        val command = recordF.pop()
        command.down()
        recordB.push(command)

        requestInvalidate()
    }

    fun redo() {
        if (recordB.isEmpty()) return

        val command = recordB.pop()
        command.up()
        recordF.push(command)

        requestInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        holder.forEach {
            when (it) {
                is PathMode -> toolPath.onDraw(canvas, it)
            }
        }

        _drawingTool?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        if (_drawingTool == null) return true

        _drawingTool?.createDrawingObject(event.x, event.y)

        return _drawingTool?.onTouchEvent(event) ?: true
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