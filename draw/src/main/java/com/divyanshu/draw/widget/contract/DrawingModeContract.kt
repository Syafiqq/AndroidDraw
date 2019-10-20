package com.divyanshu.draw.widget.contract


interface DrawingModeContract {
    var drawingMode: DrawingMode
    fun createDrawingObject()
    fun destroyDrawingObject()
}