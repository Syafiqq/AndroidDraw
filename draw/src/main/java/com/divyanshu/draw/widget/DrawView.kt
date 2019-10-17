package com.divyanshu.draw.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.LinkedHashMap
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.divyanshu.draw.R
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.hasActionButtons
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.callbacks.onPreShow
import com.afollestad.materialdialogs.callbacks.onShow
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.*
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.utils.MDUtil.maybeSetTextColor
import com.afollestad.materialdialogs.utils.MDUtil.textChanged


class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaths = LinkedHashMap<TextHolder, PaintOptions>()

    private var mLastPaths = LinkedHashMap<TextHolder, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<TextHolder, PaintOptions>()

    private var mPaint = Paint()
    private var mPath = TextHolder()
    private var mPaintOptions = PaintOptions()

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            color = mPaintOptions.color
            textSize = spToPx(12f, context)
        }
    }

    fun undo() {
    }

    fun redo() {
    }

    fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha*255)/100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        mIsSaving = true
        draw(canvas)
        mIsSaving = false
        return bitmap
    }

    fun addPath(path: TextHolder, options: PaintOptions) {
        mPaths[path] = options
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((key, value) in mPaths) {
            changePaint(value)
            mPaint.textSize = key.textSize
            canvas.drawText(key.text, key.x, key.y, mPaint)
        }

        changePaint(mPaintOptions)
        if(mPath is TextHolder) {
            mPaint.textSize = mPath.textSize
            canvas.drawText(mPath.text, mPath.x, mPath.y, mPaint)
        }
    }

    private fun changePaint(paintOptions: PaintOptions) {
        mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth
        setEraser(paintOptions.isEraserOn)
    }

    private fun setEraser(isEraser: Boolean) {
        if (isEraser) {
            mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        } else {
            mPaint.xfermode = null
        }
    }

    fun clearCanvas() {
    }

    private fun actionDown(x: Float, y: Float, t: String, s: Float) {
        mPath.x = x
        mPath.y = y
        mPath.text = t
        mPath.textSize = s
        /*mPath.reset()
        mPath.moveTo(x, y)
        mCurX = x
        mCurY = y*/
    }

    private fun actionMove(x: Float, y: Float) {
        /*mPath.quadTo(mCurX, mCurY, (x + mCurX) / 2, (y + mCurY) / 2)
        mCurX = x
        mCurY = y*/
    }

    private fun actionUp() {
        mPaths[mPath] = mPaintOptions
        mPath = TextHolder()
        mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                MaterialDialog(context).show {
                    resizeableInput { dialog, text ->
                        actionDown(x, y, text.toString(), dialog.getInputField().textSize)
                        invalidate()
                    }
                    positiveButton(R.string.app_name)
                }
                mUndonePaths.clear()
            }
            MotionEvent.ACTION_MOVE -> {}
            MotionEvent.ACTION_UP -> actionUp()
        }

        return true
    }

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()
    }


    fun spToPx(sp: Float, context: Context): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toFloat()
    }
}


/**
 * Shows an input field as the content of the dialog. Can be used with a message and checkbox
 * prompt, but cannot be used with a list.
 *
 * @param hint The literal string to display as the input field hint.
 * @param hintRes The string resource to display as the input field hint.
 * @param prefill The literal string to pre-fill the input field with.
 * @param prefillRes The string resource to pre-fill the input field with.
 * @param inputType The input type for the input field, e.g. phone or email. Defaults to plain text.
 * @param maxLength The max length for the input field, shows a counter and disables the positive
 *    action button if the input length surpasses it.
 * @param waitForPositiveButton When true, the [callback] isn't invoked until the positive button
 *    is clicked. Otherwise, it's invoked every time the input text changes. Defaults to true if
 *    the dialog has buttons.
 * @param allowEmpty Defaults to false. When false, the positive action button is disabled unless
 *    the input field is not empty.
 * @param callback A listener to invoke for input text notifications.
 */
@SuppressLint("CheckResult")
@CheckResult
fun MaterialDialog.resizeableInput(
        hint: String? = null,
        @StringRes hintRes: Int? = null,
        prefill: CharSequence? = null,
        @StringRes prefillRes: Int? = null,
        inputType: Int = InputType.TYPE_CLASS_TEXT,
        maxLength: Int? = null,
        waitForPositiveButton: Boolean = true,
        allowEmpty: Boolean = false,
        callback: InputCallback = null
): MaterialDialog {
    customView(R.layout.resizeable_input)
    onPreShow { showKeyboardIfApplicable() }
    if (!hasActionButtons()) {
        positiveButton(android.R.string.ok)
    }

    if (callback != null && waitForPositiveButton) {
        // Add an additional callback to invoke the input listener after the positive AB is pressed
        positiveButton { callback.invoke(this@resizeableInput, getInputField().text ?: "") }
    }

    prefillInput(prefill = prefill, prefillRes = prefillRes, allowEmpty = allowEmpty)
    styleInput(hint = hint, hintRes = hintRes, inputType = inputType)

    if (maxLength != null) {
        getInputLayout().run {
            isCounterEnabled = true
            counterMaxLength = maxLength
        }
        invalidateInputMaxLength(allowEmpty)
    }

    getInputField().textChanged {
        if (!allowEmpty) {
            setActionButtonEnabled(WhichButton.POSITIVE, it.isNotEmpty())
        }
        maxLength?.let { invalidateInputMaxLength(allowEmpty) }

        if (!waitForPositiveButton && callback != null) {
            // We aren't waiting for positive, so invoke every time the text changes
            callback.invoke(this, it)
        }
    }
    getCustomView().findViewById<SeekBar>(R.id.seekBar3).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            if(p1 < 1) return
            else {
                getInputField().textSize = p1.toFloat()
            }
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }
    })

    return this
}

private fun MaterialDialog.prefillInput(
        prefill: CharSequence?,
        prefillRes: Int?,
        allowEmpty: Boolean
) {
    val resources = windowContext.resources
    val editText = getInputField()

    val prefillText = prefill ?: if (prefillRes != null) resources.getString(prefillRes) else ""
    if (prefillText.isNotEmpty()) {
        editText.setText(prefillText)
        onShow { editText.setSelection(prefillText.length) }
    }
    setActionButtonEnabled(
            WhichButton.POSITIVE,
            allowEmpty || prefillText.isNotEmpty()
    )
}

private fun MaterialDialog.styleInput(
        hint: String?,
        hintRes: Int?,
        inputType: Int
) {
    val resources = windowContext.resources
    val editText = getInputField()

    editText.hint = hint ?: if (hintRes != null) resources.getString(hintRes) else null
    editText.inputType = inputType
    editText.maybeSetTextColor(
            windowContext,
            attrRes = com.afollestad.materialdialogs.input.R.attr.md_color_content,
            hintAttrRes = com.afollestad.materialdialogs.input.R.attr.md_color_hint
    )
    bodyFont?.let(editText::setTypeface)
}

internal fun MaterialDialog.invalidateInputMaxLength(allowEmpty: Boolean) {
    val currentLength = getInputField().text?.length ?: 0
    if (!allowEmpty && currentLength == 0) {
        return
    }
    val maxLength = getInputLayout().counterMaxLength
    if (maxLength > 0) {
        setActionButtonEnabled(WhichButton.POSITIVE, currentLength <= maxLength)
    }
}

internal fun MaterialDialog.showKeyboardIfApplicable() {
    getInputField().postRun {
        requestFocus()
        val imm =
                windowContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }
}

internal inline fun <T : View> T.postRun(crossinline exec: T.() -> Unit) = this.post {
    this.exec()
}
