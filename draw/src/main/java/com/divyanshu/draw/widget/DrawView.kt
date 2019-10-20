package com.divyanshu.draw.widget

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class DrawView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mPaths = LinkedList<MyImage>()
    private var mPathsR = LinkedList<MyImage>()

    private var mLastPaths = LinkedHashMap<MyImage, PaintOptions>()
    private var mUndonePaths = LinkedHashMap<MyImage, PaintOptions>()

    private var mPaint = Paint()
    private var mPath : MyImage? = null
    private var selPath : MyImage? = null
    private var mPaintOptions = PaintOptions()

    private val disposable= CompositeDisposable()
    private val subject= PublishSubject.create<Int>()
    private var gate = AtomicBoolean(true)

    private var mCurX = 0f
    private var mCurY = 0f
    private var mStartX = 0f
    private var mStartY = 0f
    private var mIsSaving = false
    private var mIsStrokeWidthBarEnabled = false

    var imageOperation : ImageOperation? = null

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            Timber.d("onScale")
            if(selPath != null) {
                if(detector.scaleFactor < 1.0)
                    selPath?.scaledDown()
                else
                    selPath?.scaledUp()
                invalidate()
                return true
            }
            return false
        }
    }
    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)

    var isEraserOn = false
        private set

    init {
        mPaint.apply {
            color = mPaintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = mPaintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    fun undo() {
/*        if (mPaths.isEmpty() && mLastPaths.isNotEmpty()) {
            mPaths = mLastPaths.clone() as LinkedHashMap<MyImage, PaintOptions>
            mLastPaths.clear()
            invalidate()
            return
        }
        if (mPaths.isEmpty()) {
            return
        }
        val lastPath = mPaths.values.lastOrNull()
        val lastKey = mPaths.keys.lastOrNull()

        mPaths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            mUndonePaths[lastKey] = lastPath
        }
        invalidate()*/
    }

    fun redo() {
/*        if (mUndonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = mUndonePaths.keys.last()
        addPath(lastKey, mUndonePaths.values.last())
        mUndonePaths.remove(lastKey)
        invalidate()*/
    }

    fun setColor(newColor: Int) {
/*        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, mPaintOptions.alpha)
        mPaintOptions.color = alphaColor
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }*/
    }

    fun setAlpha(newAlpha: Int) {
/*        val alpha = (newAlpha*255)/100
        mPaintOptions.alpha = alpha
        setColor(mPaintOptions.color)*/
    }

    fun setStrokeWidth(newStrokeWidth: Float) {
/*        mPaintOptions.strokeWidth = newStrokeWidth
        if (mIsStrokeWidthBarEnabled) {
            invalidate()
        }*/
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

    fun addPath(path: MyImage, options: PaintOptions) {
        /*mPaths[path] = options*/
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (key in mPaths) {
            if(key.bitmap != null && key.getRectScaled() != null)
                canvas.drawBitmap(key.bitmap!!, null, key.getRectScaled()!!, null)
        }

        val path = mPath
        if(path?.bitmap != null && path.getRectScaled() != null)
            canvas.drawBitmap(path.bitmap!!, null, path.getRectScaled()!!, null)
    }

    private fun changePaint(paintOptions: PaintOptions) {
/*        mPaint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        mPaint.strokeWidth = paintOptions.strokeWidth*/
    }

    fun clearCanvas() {
/*        mLastPaths = mPaths.clone() as LinkedHashMap<MyImage, PaintOptions>
        mPath.reset()
        mPaths.clear()
        invalidate()*/
    }

    private fun actionDown(event: MotionEvent, x: Float, y: Float) {
        checkSelection(x, y, event.getPointerId(0))
        buildObservable()
    }

    private fun actionMove(event: MotionEvent, x: Float, y: Float) {
        if(selPath == null)
            return
        else {
            if(event.pointerCount <= 1) {
                selPath?.placeTo(x, y, event.getPointerId(0))
                invalidate()
            }
        }
    }

    private fun actionUp(event: MotionEvent) {
        if(selPath != null) {
            selPath = null
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(mScaleDetector.onTouchEvent(event)) {
            val x = event.x
            val y = event.y
            mCurX = x
            mCurY = y

            when (event.action) {
                MotionEvent.ACTION_DOWN -> actionDown(event, x, y)
                MotionEvent.ACTION_MOVE -> actionMove(event, x, y)
                MotionEvent.ACTION_UP -> actionUp(event)
            }
            subject.onNext(event.action)

            return true
        }
        return false
    }

    private fun checkSelection(x: Float, y: Float, pointerId: Int) {
        Timber.d("Check selection of ${mPathsR.size}")
        for (p in mPathsR) {
            if(p.isInside(x, y)) {
                Timber.d("Is Inside")
                selPath = p
                p.setTouchDiffer(x, y)
                p.setPointer(pointerId)
                return
            }
        }
    }

    fun toggleEraser() {
        isEraserOn = !isEraserOn
        mPaintOptions.isEraserOn = isEraserOn
        invalidate()
    }

    fun retrieveImageUri(uri: Uri) {
        mPath?.run {
            setImage(uri)
            invalidate()
        }

        val path = mPath
        if(path != null)
        {
            mPaths.addLast(path)
            mPathsR.addFirst(path)
            mPath = null
            mPaintOptions = PaintOptions(mPaintOptions.color, mPaintOptions.strokeWidth, mPaintOptions.alpha, mPaintOptions.isEraserOn)
        }
    }

    private fun buildObservable() {
        if(gate.get()) {
            gate.set(false)
            subject
                    .takeUntil(Observable.timer(150L, TimeUnit.MILLISECONDS))
                    .defaultIfEmpty(MotionEvent.ACTION_MOVE)
                    .toList()
                    .observeOn(Schedulers.computation())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        gate.set(true)
                        createObjectIfSufficient(it)
                    },{})
                    .addTo(disposable)
        }
    }

    private fun createObjectIfSufficient(seq: List<Int>) {
        if((seq.last() == MotionEvent.ACTION_UP && seq.size < 3) || (seq.last() == MotionEvent.ACTION_MOVE && selPath == null)) {
            imageOperation?.run {
                mPath = MyImage(this).apply {
                    placeTo(mCurX, mCurY)
                }
                onRequestImage()
            }
        }
    }

    fun onDestroy() {
        disposable.dispose()
        mPaths.forEach(MyImage::onDestroy)
        mPath?.onDestroy()
    }
}

interface ImageOperation : OnRequestStreamImageListener {
    fun onRequestImage()
}