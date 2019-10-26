package com.divyanshu.draw.util

import android.content.Context
import android.util.TypedValue

object UnitConverter {
    fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }
}