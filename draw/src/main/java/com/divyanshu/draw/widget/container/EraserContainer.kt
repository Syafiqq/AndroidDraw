package com.divyanshu.draw.widget.container

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.divyanshu.draw.widget.contract.ICanvas

class EraserContainer(override val context: Context, override val drawing: ICanvas) : PenContainer(context, drawing) {
    private val paint = Paint()

    override var color = Color.TRANSPARENT
    override var strokeWidth = super.strokeWidth
    override var alpha = 0xF

    init {
        with(paint) {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            maskFilter = null
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    }
}