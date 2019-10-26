package com.divyanshu.draw.widget.contract

interface TextDrawCallback {
    fun onTextRetrieved(text: String, textSize: Float?)
    fun onApply()
}