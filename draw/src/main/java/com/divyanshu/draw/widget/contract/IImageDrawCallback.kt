package com.divyanshu.draw.widget.contract

import java.io.InputStream

interface IImageDrawCallback {
    fun onImageRetreived(image: InputStream)
    fun onApply()
    fun onCancel()
    fun onScaleUp()
    fun onScaleDown()
}