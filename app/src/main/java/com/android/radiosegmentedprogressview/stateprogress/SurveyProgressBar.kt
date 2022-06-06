package com.android.radiosegmentedprogressview.stateprogress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.android.radiosegmentedprogressview.R
import kotlin.collections.ArrayList

class SurveyProgressBar : View {

    constructor(context: Context) : super(context) {
        initClass(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {
        initClass(context, attrs)
        initializePainters()
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    private fun initClass(context: Context, attrs: AttributeSet? = null) {
        initParams()

        mStateTextValueSize = convertSpToPixel(mStateTextValueSize)
        mStateSubtextSize = convertSpToPixel(mStateSubtextSize)
        mStateLineThickness = convertDpToPixel(mStateLineThickness)
        mSpacing = convertDpToPixel(mSpacing)
        mDefaultTypefaceBold = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        mDefaultTypefaceNormal = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar, 0, 0)

            mBackgroundColor =
                a.getColor(R.styleable.StateProgressBar_spb_stateBackgroundColor, mBackgroundColor)
            mForegroundColor =
                a.getColor(R.styleable.StateProgressBar_spb_stateForegroundColor, mForegroundColor)

            mStateSize = a.getDimension(R.styleable.StateProgressBar_spb_stateSize, mStateSize)

            mStateTextValueSize = a.getDimension(
                R.styleable.StateProgressBar_spb_stateDescriptionSize,
                mStateTextValueSize
            )

            mStateSubtextSize = a.getDimension(
                R.styleable.StateProgressBar_spb_stateDescriptionSize,
                mStateSubtextSize
            )

            mStateLineThickness = a.getDimension(
                R.styleable.StateProgressBar_spb_stateLineThickness,
                mStateLineThickness
            )

            mCheckStateCompleted = a.getBoolean(
                R.styleable.StateProgressBar_spb_checkStateCompleted,
                mCheckStateCompleted
            )

            mEnableAllStatesCompleted = a.getBoolean(
                R.styleable.StateProgressBar_spb_enableAllStatesCompleted,
                mEnableAllStatesCompleted
            )

            mMaxDescriptionLine = a.getInteger(
                R.styleable.StateProgressBar_spb_maxDescriptionLines,
                mMaxDescriptionLine
            )

            mDescriptionLinesSpacing = a.getDimension(
                R.styleable.StateProgressBar_spb_descriptionLinesSpacing,
                mDescriptionLinesSpacing
            )

            validateLineThickness(mStateLineThickness)
            validateStateNumber(mCurrentStateNumber)

            a.recycle()
        }
    }

    private fun initParams() {
        mBackgroundColor = ContextCompat.getColor(context, R.color.background_color)
        mForegroundColor = ContextCompat.getColor(context, R.color.foreground_color)

        mStateTextValueColor = ContextCompat.getColor(context, R.color.background_text_color)
        mStateSubtextColor = ContextCompat.getColor(context, R.color.black)

        mStateSize = 0.0f
        mStateLineThickness = 4.0f
        mStateTextValueSize = 12f
        mStateSubtextSize = 15f
        mMaxStateNumber = 5
        mCurrentStateNumber = 0
        mSpacing = 4.0f
        mDescTopSpaceDecrementer = 0.0f
        mDescTopSpaceIncrementer = 0.0f
        mDescriptionLinesSpacing = 0.0f
        mCheckStateCompleted = false
        mEnableAllStatesCompleted = false
        mJustifyMultilineDescription = false
    }

    private fun initializePainters() {
        mBackgroundPaint = setPaintAttributes(mStateLineThickness, mBackgroundColor)
        mForegroundPaint = setPaintAttributes(mStateLineThickness, mForegroundColor)

        mStateTextValuePaint = setPaintAttributes(
            mStateTextValueSize, mStateTextValueColor, mDefaultTypefaceBold!!
        )
        mStateSubtextPaint = setPaintAttributes(
            mStateSubtextSize, mStateSubtextColor, mDefaultTypefaceNormal!!
        )
    }

    private fun validateLineThickness(lineThickness: Float) {
        val halvedStateSize = mStateSize / 2
        if (lineThickness > halvedStateSize) {
            mStateLineThickness = halvedStateSize
        }
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        mBackgroundPaint!!.color = mBackgroundColor
        invalidate()
    }

    fun setForegroundColor(foregroundColor: Int) {
        mForegroundColor = foregroundColor
        mForegroundPaint!!.color = mForegroundColor
        invalidate()
    }

    fun setStateLineThickness(stateLineThickness: Float) {
        mStateLineThickness = convertDpToPixel(stateLineThickness)
        resolveStateLineThickness()
    }

    private fun resolveStateLineThickness() {
        validateLineThickness(mStateLineThickness)
        mBackgroundPaint!!.strokeWidth = mStateLineThickness
        mForegroundPaint!!.strokeWidth = mStateLineThickness
        invalidate()
    }

    fun setStateTextValueColor(stateTextValueColor: Int) {
        mStateTextValueColor = stateTextValueColor
        mStateTextValuePaint!!.color = mStateTextValueColor
        invalidate()
    }


    fun setStateSubtextColor(color: Int) {
        mStateSubtextColor = color
        mStateSubtextPaint!!.color = mStateSubtextColor
        invalidate()
    }


    fun setCurrentStateNumber(currentStateNumber: Int) {
        if (currentStateNumber < 0 || currentStateNumber > mMaxStateNumber) {
            mCurrentStateNumber = 0
            return
        }
        mCurrentStateNumber = currentStateNumber
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
        invalidate()
    }

    fun setMaxStateNumber(maximumState: Int) {
        mMaxStateNumber = maximumState
        resolveMaxStateNumber()
    }

    private fun resolveMaxStateNumber() {
        validateStateNumber(mCurrentStateNumber)
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
        invalidate()
    }

    fun setStateSize(stateSize: Float) {
        mStateSize = convertDpToPixel(stateSize)
        resetStateSizeValues()
    }

    private fun resetStateSizeValues() {
        validateLineThickness(mStateLineThickness)
        mBackgroundPaint!!.strokeWidth = mStateLineThickness
        mForegroundPaint!!.strokeWidth = mStateLineThickness
        requestLayout()
    }

    fun setStateTextValueSize(stateDescriptionSize: Float) {
        mStateTextValueSize = convertSpToPixel(stateDescriptionSize)
        resolveStateDescriptionSize()
    }

    fun setStateSubtextSize(stateValueSize: Float) {
        mStateSubtextSize = convertSpToPixel(stateValueSize)
        resolveSubtextSize()
    }

    private fun resolveStateDescriptionSize() {
        mStateTextValuePaint!!.textSize = mStateTextValueSize
        requestLayout()
    }

    private fun resolveSubtextSize() {
        mStateSubtextPaint!!.textSize = mStateSubtextSize
        requestLayout()
    }

    fun checkStateCompleted(checkStateCompleted: Boolean) {
        mCheckStateCompleted = checkStateCompleted
        invalidate()
    }

    fun setAllStatesCompleted(enableAllStatesCompleted: Boolean) {
        mEnableAllStatesCompleted = enableAllStatesCompleted
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
        invalidate()
    }

    private fun updateCheckAllStatesValues(enableAllStatesCompleted: Boolean) {
        if (enableAllStatesCompleted) {
            mCheckStateCompleted = true
            mCurrentStateNumber = mMaxStateNumber
        }
    }

    private fun validateStateNumber(stateNumber: Int) {
        check(stateNumber <= mMaxStateNumber) { "State number ($stateNumber) cannot be greater than total number of states $mMaxStateNumber" }
    }

    fun setDescriptionTopSpaceIncrementer(spaceIncrementer: Float) {
        mDescTopSpaceIncrementer = spaceIncrementer
        requestLayout()
    }

    fun setDescriptionTopSpaceDecrementer(spaceDecrementer: Float) {
        mDescTopSpaceDecrementer = spaceDecrementer
        requestLayout()
    }

    fun setDescriptionLinesSpacing(descriptionLinesSpacing: Float) {
        mDescriptionLinesSpacing = descriptionLinesSpacing
        requestLayout()
    }

    fun isDescriptionMultiline(): Boolean {
        return mIsDescriptionMultiline
    }

    private fun updateDescriptionMultilineStatus(multiline: Boolean) {
        mIsDescriptionMultiline = multiline
    }

    fun setMaxDescriptionLine(maxDescriptionLine: Int) {
        mMaxDescriptionLine = maxDescriptionLine
        requestLayout()
    }

    fun setJustifyMultilineDescription(justifyMultilineDescription: Boolean) {
        mJustifyMultilineDescription = justifyMultilineDescription
        invalidate()
    }

    private fun setPaintAttributes(strokeWidth: Float, color: Int): Paint? {
        val paint = setPaintAttributes(color)
        paint.strokeWidth = strokeWidth
        return paint
    }

    private fun setPaintAttributes(textSize: Float, color: Int, typeface: Typeface): Paint? {
        val paint = setPaintAttributes(color)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        paint.typeface = typeface
        return paint
    }

    private fun setPaintAttributes(color: Int): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = color
        return paint
    }

    private fun resolveStateSize() {
        if (mStateSize != 0f) {
            mStateSize = convertDpToPixel(DEFAULT_STATE_SIZE)
        }
    }

    private fun drawCircles(canvas: Canvas, paint: Paint, startIndex: Int, endIndex: Int) {
        for (i in startIndex until endIndex) {
            canvas.drawCircle(
                mCellWidth * (i + 1) - mCellWidth / 2,
                mCellHeight / 2,
                mStateRadius,
                paint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (mMaxStateNumber == 0) mMaxStateNumber = 5

        mCellWidth = (width / mMaxStateNumber).toFloat()
        mNextCellWidth = mCellWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawState(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val height = getDesiredHeight()
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
        mCellHeight = getCellHeight().toFloat()
    }

    private fun getDesiredHeight(): Int {
        return if (mStateDescriptionData.isEmpty()) {
            getCellHeight()
        } else {
            getHeightCalculatedVal()
        }
    }

    private fun getHeightCalculatedVal(): Int {
        val radius = (2 * mStateRadius).toInt()
        val descCalculation = mDescTopSpaceIncrementer.toInt() - mDescTopSpaceDecrementer.toInt()
        return if (checkForDescriptionMultiLine(mStateDescriptionData)) {
            val descHeight =
                (selectMaxDescriptionLine(mMaxDescriptionLine) * (1.3 * mStateTextValueSize)).toInt()
            radius + descHeight + mSpacing.toInt() + descCalculation + mDescriptionLinesSpacing.toInt()
        } else {
            radius + (1.3 * mStateTextValueSize).toInt() + mSpacing.toInt() + descCalculation
        }
    }

    private fun getCellHeight(): Int {
        return (2 * mStateRadius).toInt() + mSpacing.toInt()
    }

    private fun checkForDescriptionMultiLine(stateDescriptionData: List<String>): Boolean {
        var isMultiLine = false
        for (stateDescription in stateDescriptionData) {
            isMultiLine =
                stateDescription.contains(STATE_DESCRIPTION_LINE_SEPARATOR)
            if (isMultiLine) {
                updateDescriptionMultilineStatus(isMultiLine)
                return isMultiLine
            }
        }
        return isMultiLine
    }

    private fun getMaxDescriptionLine(stateDescriptionData: List<String>): Int {
        var maxLine = 1
        for (stateDescription in stateDescriptionData) {
            val lineSize: Int =
                stateDescription.split(STATE_DESCRIPTION_LINE_SEPARATOR)
                    .toTypedArray().size
            maxLine = if (lineSize > maxLine) lineSize else maxLine
        }
        mMaxDescriptionLine = maxLine
        return maxLine
    }


    private fun selectMaxDescriptionLine(maxLine: Int): Int {
        return if (maxLine > 1) maxLine else getMaxDescriptionLine(mStateDescriptionData)
    }

    /**
     * Draw subway progress bar
     * */
    private fun drawState(canvas: Canvas) {
        drawCurrentStateJoiningLine(canvas)
        drawBackgroundLines(canvas)
        drawBackgroundCircles(canvas)
        drawForegroundCircles(canvas)
        drawForegroundLines(canvas)
        drawStateTextAmt(canvas)
        drawStateDescriptionText(canvas)
    }

    /**
     * Draw subway progress bar subway-points
     * */
    private fun drawBackgroundCircles(canvas: Canvas) {
        mBackgroundPaint?.style = Paint.Style.STROKE
        drawCircles(canvas, mBackgroundPaint!!, mCurrentStateNumber, mMaxStateNumber)
    }

    /**
     * Draw selected subway-points on subway progress bar
     * */
    private fun drawForegroundCircles(canvas: Canvas) {
        drawCircles(canvas, mForegroundPaint!!, 0, mCurrentStateNumber)
    }

    /**
     * Draw lines from one point to another when there is no progress
     * with remaining un-selected states in iterations
     * */
    private fun drawBackgroundLines(canvas: Canvas) {
        val iterations = if (mCurrentStateNumber != 0) {
            (mMaxStateNumber - mCurrentStateNumber) + 1
        } else {
            mMaxStateNumber - mCurrentStateNumber
        }

        var si = if (mCurrentStateNumber - 1 < 0) 0 else mCurrentStateNumber - 1
        var ei = if (mCurrentStateNumber == 0) mCurrentStateNumber + 2 else mCurrentStateNumber + 1
        for (i in 0 until iterations - 1) {
            si = if (i != 0) si + 1 else si
            ei = if (i != 0 && ei + 1 <= mMaxStateNumber) ei + 1 else ei
            drawLines(canvas, mBackgroundPaint!!, si, ei)
        }
    }

    /**
     * draw lines for selected progress states
     * */
    private fun drawForegroundLines(canvas: Canvas) {
        drawLines(canvas, mForegroundPaint!!, 0, mCurrentStateNumber)
    }

    /**
     * Drawing lines from point A to Point B in subway depending on the inputs
     * */
    private fun drawLines(canvas: Canvas, paint: Paint, startIndex: Int, endIndex: Int) {
        val startCenterX: Float
        val endCenterX: Float
        val startX: Float
        val stopX: Float
        if (endIndex > startIndex) {
            startCenterX = mCellWidth / 2 + mCellWidth * startIndex
            endCenterX = mCellWidth * endIndex - mCellWidth / 2
            startX = startCenterX + mStateRadius * 0.75f
            stopX = endCenterX - mStateRadius * 0.75f
            canvas.drawLine(startX, mCellHeight / 2, stopX, mCellHeight / 2, paint)
        }
    }

    private fun drawCurrentStateJoiningLine(canvas: Canvas) {
        drawLineToCurrentState(canvas)
    }

    private fun drawLineToCurrentState(canvas: Canvas) {
        canvas.drawLine(
            mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2,
            mForegroundPaint!!
        )
        mNextCellWidth = mCellWidth
    }

    /**
     * Shows/Draws sub-text on canvas
     * */
    private fun drawStateDescriptionText(canvas: Canvas) {
        if (!mStateDescriptionData.isEmpty()) {
            for (i in mStateDescriptionData.indices) {
                drawTextViewOnCanvas(
                    canvas,
                    i,
                    mStateTextValuePaint!!,
                    mStateDescriptionData[i],
                    60f,
                    mStateTextValueSize
                )
            }
        }
        mNextCellWidth = mCellWidth
    }

    /**
     * Shows/Draws main text on canvas
     * */
    private fun drawStateTextAmt(canvas: Canvas) {
        if (!mStateTextAData.isEmpty()) {
            for (i in mStateTextAData.indices) {
                drawTextViewOnCanvas(
                    canvas,
                    i,
                    mStateSubtextPaint!!,
                    mStateTextAData[i],
                    0f,
                    mStateSubtextSize
                )
            }
        }
        mNextCellWidth = mCellWidth
    }

    /**
     * This common method to draw text on canvas for main text and subtext
     * All calculations related to Positions X & Y on screens is calculated
     * */
    private fun drawTextViewOnCanvas(
        canvas: Canvas,
        i: Int,
        textColor: Paint,
        text: String,
        mDescTopSpaceDecrementer: Float,
        textSize: Float
    ) {
        val xPos: Int
        var yPos: Int
        val relativePosition =
            (mCellHeight + textSize - mSpacing - mDescTopSpaceDecrementer + mDescTopSpaceIncrementer).toInt()

        if (i < mMaxStateNumber) {
            xPos = (mNextCellWidth - mCellWidth / 2).toInt()

            if (mIsDescriptionMultiline && mMaxDescriptionLine > 1) {

                var nextLineCounter = 0
                val stateDescriptionLines: Array<String> =
                    text.split(STATE_DESCRIPTION_LINE_SEPARATOR)
                        .toTypedArray()

                for (line in stateDescriptionLines) {
                    nextLineCounter += 1

                    val newXPos = getXPosition(
                        nextLineCounter, stateDescriptionLines[0],
                        line,
                        textColor,
                        xPos
                    )

                    if (nextLineCounter <= mMaxDescriptionLine) {
                        var rNumberVal = 0.0f
                        if (nextLineCounter > 1) {
                            rNumberVal = mDescriptionLinesSpacing * (nextLineCounter - 1) * 2
                        }
                        yPos = ((mCellHeight
                                + nextLineCounter
                                * textSize
                                - mSpacing
                                - mDescTopSpaceDecrementer
                                + mDescTopSpaceIncrementer
                                + rNumberVal
                                ).toInt())


                        val xPosition = (if (newXPos == 0) xPos else newXPos.toFloat()) as Float
                        drawTextOnCanvas(
                            canvas,
                            line,
                            xPosition,
                            yPos.toFloat(),
                            textColor
                        )
                    }
                }
            } else {
                drawTextOnCanvas(
                    canvas, text,
                    xPos.toFloat(),
                    relativePosition.toFloat(),
                    textColor
                )
            }
            mNextCellWidth += mCellWidth
        }
    }

    /**
     * Plain simple call to draw text on screen
     * */
    private fun drawTextOnCanvas(
        canvas: Canvas,
        text: String,
        xPos: Float,
        yPos: Float,
        textColor: Paint
    ) {
        canvas.drawText(
            text,
            xPos,
            yPos,
            textColor
        )
    }

    /**
     * This generic method for this class to :
     * Get x-axis position where text has to be drawn
     * This is calculated based on the input for main text or sub-text
     * */
    private fun getXPosition(
        counter: Int,
        descLine: String,
        line: String,
        textColor: Paint,
        pos: Int
    ): Int {
        if (mJustifyMultilineDescription && counter > 1) {
            return getNewXPosForDescriptionMultilineJustification(
                descLine,
                line,
                textColor,
                pos
            )
        }
        return 0
    }

    /**
     * If description here such as main-text or sub-text has multiple lines
     * then we can calculate here
     * */
    private fun getNewXPosForDescriptionMultilineJustification(
        firstLine: String,
        nextLine: String,
        paint: Paint,
        xPos: Int
    ): Int {
        val firstLineWidth = paint.measureText(firstLine)
        val nextLineWidth = paint.measureText(nextLine)
        val newXPos: Float
        val widthDiff: Float
        if (firstLineWidth > nextLineWidth) {
            widthDiff = firstLineWidth - nextLineWidth
            newXPos = xPos - widthDiff / 2
        } else if (firstLineWidth < nextLineWidth) {
            widthDiff = nextLineWidth - firstLineWidth
            newXPos = xPos + widthDiff / 2
        } else {
            newXPos = xPos.toFloat()
        }
        return Math.round(newXPos)
    }

    /**
     * List of data that has to shown on subway progress as Main-Text
     * */
    fun setStateTextRowOneData(stateTextOneData: List<String?>) {
        mStateTextAData = stateTextOneData as List<String>
        requestLayout()
    }

    /**
     * List of data that has to shown on subway progress as Sub-Text
     * */
    fun setStateDescriptionData(stateDescriptionData: List<String>) {
        mStateDescriptionData = stateDescriptionData as ArrayList<String>
        requestLayout()
    }

    /**
     * Convert dp to pixels - For Subway Progress
     * */
    private fun convertDpToPixel(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale
    }

    /**
     * Convert sp to pixels - For text
     * */
    private fun convertSpToPixel(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }

    private companion object {
        private var mStateDescriptionData: List<String> = arrayListOf()
        private var mStateTextAData: List<String> = arrayListOf()

        /**
         * Subway point(circle) size defined as default value
         * */
        private var mStateRadius = 26.0f

        /**
         * Subway point(circle) size
         * */
        private var mStateSize = 0.0f

        /**
         * Define each line thickness from Point-Point
         * */
        private var mStateLineThickness = 3.3f

        /**
         * Defines Main-Text text size
         * */
        private var mStateSubtextSize = 0.0f

        /**
         * Defines Sub-Text text size
         * */
        private var mStateTextValueSize = 0.0f

        /**
         * Cell width
         */
        private var mCellWidth = 0.0f

        /**
         * Cell height
         */
        private var mCellHeight = 0.0f

        /**
         * next cell(state) from previous cell
         */
        private var mNextCellWidth = 0.0f

        /**
         * center of first cell(state)
         */
        private var mStartCenterX = 0.0f

        /**
         * center of last cell(state)
         */
        private var mEndCenterX = 0.0f

        /**
         * Maximum number of subway points
         * */
        private var mMaxStateNumber = 0

        /**
         * Current state-selected subway point
         * */
        private var mCurrentStateNumber = 0


        private var mSpacing = 0.0f

        private var mDescTopSpaceDecrementer = 0f
        private var mDescTopSpaceIncrementer = 0f

        private const val DEFAULT_TEXT_SIZE = 15f
        private const val DEFAULT_STATE_SIZE = 25f

        /**
         * Paints for drawing
         */
        private var mBackgroundPaint: Paint? = null
        private var mForegroundPaint: Paint? = null
        private var mStateTextValuePaint: Paint? = null
        private var mStateSubtextPaint: Paint? = null

        private var mBackgroundColor = 0
        private var mForegroundColor = 0
        private var mStateTextValueColor = 0
        private var mStateSubtextColor = 0

        private var mDefaultTypefaceBold: Typeface? = null
        private var mDefaultTypefaceNormal: Typeface? = null
        private var mIsDescriptionMultiline = false
        private var mMaxDescriptionLine = 0
        private var mDescriptionLinesSpacing = 0.0f
        private var STATE_DESCRIPTION_LINE_SEPARATOR = "\n"
        private var mJustifyMultilineDescription = false
        private var mEnableAllStatesCompleted = false
        private var mCheckStateCompleted = false
    }
}