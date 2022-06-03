package com.android.radiosegmentedprogressview.progressview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.android.radiosegmentedprogressview.R

class ProgressViewIndicator : View {

    constructor(context: Context) : super(context, null) {
        // Do nothing
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        // Do nothing
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ProgressViewIndicator)
        mNumOfStep = a.getInt(R.styleable.ProgressViewIndicator_numOfSteps, 0)
    }

    init {
        mLineHeight = 0.2f * THUMB_SIZE
        mThumbRadius = 0.4f * THUMB_SIZE
        mCircleRadius = 0.7f * mThumbRadius
        mPadding = 0.5f * THUMB_SIZE
    }
    fun setStepSize(size: Int) {
        mNumOfStep = size
        invalidate()
    }

    fun setDrawListener(drawListener: OnDrawListener) {
        mDrawListener = drawListener
    }

    fun getThumbContainerXPosition(): List<Float> {
        return mThumbContainerXPosition
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCenterY = 0.5f * height
        mLeftX = mPadding
        mLeftY = mCenterY - (mLineHeight / 2)
        mRightX = width - mPadding
        mRightY = 0.5f * (height + mLineHeight)
        mDelta = (mRightX - mLeftX) / (mNumOfStep - 1)

        mThumbContainerXPosition.add(mLeftX)

        for (i in 1 until mNumOfStep - 1) {
            mThumbContainerXPosition.add(mLeftX + (i * mDelta))
        }

        mThumbContainerXPosition.add(mRightX)
        mDrawListener.onReady()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var width = 200
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }

        var height = THUMB_SIZE + 20
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec)
        }

        setMeasuredDimension(width, height)
    }

    fun setCompletedPosition(position: Int) {
        mCompletedPosition = position
    }

    fun reset() {
        setCompletedPosition(0)
    }

    fun setProgressColor(progressColor: Int) {
        mProgressColor = progressColor
    }

    fun setBarColor(barColor: Int) {
        mBarColor = barColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mDrawListener.onReady()

        // draw rectangle bounds
        paint.isAntiAlias = true
        paint.color = mBarColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2F

        selectedPaint.isAntiAlias = true
        selectedPaint.color = mProgressColor
        selectedPaint.style = Paint.Style.STROKE
        selectedPaint.strokeWidth = 2F

        // Draw circle bounds
        for (i in 0 until mThumbContainerXPosition.size - 1) {
            canvas?.drawCircle(
                mThumbContainerXPosition[i], mCenterY, mCircleRadius,
                getPaintType(i)
            )
        }

        // Changing style for completed circle
        paint.style = Paint.Style.FILL
        selectedPaint.style = Paint.Style.FILL

        for (i in 0 until mThumbContainerXPosition.size - 1) {
            val pos = mThumbContainerXPosition[i]
            try {
                val pos2 = mThumbContainerXPosition[i + 1]
                canvas?.drawRect(pos, mLeftY, pos2, mRightY, getPaintType(i))
            } catch (e: ArrayIndexOutOfBoundsException) {
                // nothing to log/handle
            }
        }

        // Draw remaining circles
        for (i in 0 until  mThumbContainerXPosition.size - 1) {
            canvas?.drawCircle(
                mThumbContainerXPosition[i],
                mCenterY,
                mCircleRadius,
                getPaintType(i)
            )

            if (i == mCompletedPosition) {
                selectedPaint.color = getColorWithAlpha(mProgressColor)
                canvas?.drawCircle(
                    mThumbContainerXPosition[i],
                    mCenterY,
                    mCircleRadius * 1.8f,
                    selectedPaint
                )
            }
        }
    }

    private fun getColorWithAlpha(color: Int): Int {
        val alpha = Math.round(Color.alpha(color) * 0.2f)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.argb(alpha, r, g, b)
    }

    private fun getPaintType(status: Int): Paint {
        if (status <= mCompletedPosition) {
            return selectedPaint
        }
        return paint
    }

    interface OnDrawListener {
        fun onReady()
    }

    private companion object {
        const val THUMB_SIZE = 100
        val paint: Paint = Paint()
        val selectedPaint: Paint = Paint()

        var mNumOfStep: Int = 2
        var mLineHeight = 0.0f
        var mThumbRadius = 0.0f
        var mCircleRadius = 0.0f
        var mPadding = 0.0f

        var mProgressColor = Color.BLUE
        var mBarColor = Color.GRAY

        var mCenterY = 0.0f
        var mLeftX = 0.0f
        var mLeftY = 0.0f
        var mRightX = 0.0f
        var mRightY = 0.0f
        var mDelta = 0.0f

        val mThumbContainerXPosition: MutableList<Float> = mutableListOf()
        lateinit var mDrawListener: OnDrawListener

        private var mCompletedPosition = 0
    }
}