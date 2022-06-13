package com.android.radiosegmentedprogressview.stateprogress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.collections.ArrayList

/**
 * @Intro
 * This is a static view-component which can be used to show the progress of anykind of
 * data or process or state or work or anykind of representation that is required to
 * provide information to the end user about the start, current and last stages of the process
 *
 * Subway Progress Bar, is usually used to describe/represent the various check-points in the
 * process which indicates where the process is at that given point of time.
 *
 * Subway Progress Bar is custom component that is developed to use across the application
 * for similar(above described) process
 *
 * @Features
 *
 * This is dynamic component that can be used across the application and all attributes of this
 * component like
 *  - Subway Points
 *  - Thickness of the line
 *  - Data in first and second row
 *  Can be changed dynamically, Developer can use his/her choice of color, data, and adjust the
 *  thickness of the component
 * */
class SurveyProgressBar : View {

    constructor(context: Context) : super(context) {
        initParams()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initParams(attrs)
    }

    private fun initParams(attrs: AttributeSet? = null) {
        spbUtil = SurveyProgressBarUtil(context, attrs)
        initializePainters()
    }

    /**
     * Init the default values of the component
     * */
    private fun initializePainters() {
        spbUtil.mBackgroundPaint = setPaintAttributes(spbUtil.mStateLineThickness, spbUtil.mBackgroundColor)
        spbUtil.mForegroundPaint = setPaintAttributes(spbUtil.mStateLineThickness, spbUtil.mForegroundColor)

        spbUtil.mStateTextValuePaint = setPaintAttributes(
            spbUtil.mStateTextValueSize, spbUtil.mStateTextValueColor, spbUtil.mDefaultTypefaceBold!!
        )
        spbUtil.mStateSubtextPaint = setPaintAttributes(
            spbUtil.mStateSubtextSize, spbUtil.mStateSubtextColor, spbUtil.mDefaultTypefaceNormal!!
        )
    }

    /**
     * Sets the un-progress color of the component
     * */
    override fun setBackgroundColor(backgroundColor: Int) {
        spbUtil.mBackgroundColor = backgroundColor
        spbUtil.mBackgroundPaint!!.color = spbUtil.mBackgroundColor
    }

    /**
     * Sets the in-progress color of the component
     * */
    fun setForegroundColor(foregroundColor: Int) {
        spbUtil.mForegroundColor = foregroundColor
        spbUtil.mForegroundPaint!!.color = spbUtil.mForegroundColor
    }

    /**
     * Sets the thickness of the line
     * */
    fun setStateLineThickness(stateLineThickness: Float) {
        spbUtil.mStateLineThickness = convertDpToPixel(stateLineThickness)
        resolveStateLineThickness()
    }

    /**
     * Resolve/reset the thickness of the line on re-draw
     * */
    private fun resolveStateLineThickness() {
        spbUtil.validateLineThickness(spbUtil.mStateLineThickness)
        spbUtil.mBackgroundPaint!!.strokeWidth = spbUtil.mStateLineThickness
        spbUtil.mForegroundPaint!!.strokeWidth = spbUtil.mStateLineThickness
        invalidate()
    }

    /**
     * Sets the text color of data in first row
     * */
    fun setStateTextValueColor(stateTextValueColor: Int) {
        spbUtil.mStateTextValueColor = stateTextValueColor
        spbUtil.mStateTextValuePaint!!.color = spbUtil.mStateTextValueColor
        invalidate()
    }

    /**
     * Sets the text color of data in second row
     * */
    fun setStateSubtextColor(color: Int) {
        spbUtil.mStateSubtextColor = color
        spbUtil.mStateSubtextPaint!!.color = spbUtil.mStateSubtextColor
        invalidate()
    }


    /**
     * Sets the current completed sub-way points
     * */
    fun setCurrentStateNumber(currentStateNumber: Int) {
        if (currentStateNumber < 0 || currentStateNumber > spbUtil.mMaxStateNumber) {
            spbUtil.mCurrentStateNumber = 0
            return
        }
        spbUtil.mCurrentStateNumber = currentStateNumber
        resetAllStateValues(spbUtil.mEnableAllStatesCompleted)
        invalidate()
    }

    /**
     * Sets the max number of subway points
     * */
    fun setMaxStateNumber(maximumState: Int) {
        spbUtil.mMaxStateNumber = maximumState
        resolveMaxStateNumber()
    }

    /**
     * Redraws the state subway points on refresh of canvas
     * */
    private fun resolveMaxStateNumber() {
        validateStateNumber(spbUtil.mCurrentStateNumber)
        resetAllStateValues(spbUtil.mEnableAllStatesCompleted)
        invalidate()
    }

    /**
     * Size of the subway view point
     * */
    fun setStateSize(stateSize: Float) {
        spbUtil.mStateSize = convertDpToPixel(stateSize)
        resetStateSizeValues()
    }

    /**
     * Resets the size of line thickness for both in-progress and un-progress line views
     * */
    private fun resetStateSizeValues() {
        spbUtil.validateLineThickness(spbUtil.mStateLineThickness)
        spbUtil.mBackgroundPaint!!.strokeWidth = spbUtil.mStateLineThickness
        spbUtil.mForegroundPaint!!.strokeWidth = spbUtil.mStateLineThickness
    }

    /**
     * Set the font size of the first row of data in subway point
     * */
    fun setStateTextValueSize(stateDescriptionSize: Float) {
        spbUtil.mStateTextValueSize = convertSpToPixel(stateDescriptionSize)
        resolveStateDescriptionSize()
    }

    /**
     * Set the font size of the second row of data in subway point
     * */
    fun setStateSubtextSize(stateValueSize: Float) {
        spbUtil.mStateSubtextSize = convertSpToPixel(stateValueSize)
        resolveSubtextSize()
    }

    private fun resolveStateDescriptionSize() {
        spbUtil.mStateTextValuePaint!!.textSize = spbUtil.mStateTextValueSize
        requestLayout()
    }

    private fun resolveSubtextSize() {
        spbUtil.mStateSubtextPaint!!.textSize = spbUtil.mStateSubtextSize
        requestLayout()
    }

    private fun resetAllStateValues(enableAllStatesCompleted: Boolean) {
        if (enableAllStatesCompleted) {
            spbUtil.mCheckStateCompleted = true
            spbUtil.mCurrentStateNumber = spbUtil.mMaxStateNumber
        }
        invalidate()
    }

    private fun validateStateNumber(stateNumber: Int) {
        check(stateNumber <= spbUtil.mCurrentStateNumber) { "State number ($stateNumber) cannot be greater than total number of states ${spbUtil.mMaxStateNumber}" }
    }

    private fun updateDescriptionMultilineStatus(multiline: Boolean) {
        spbUtil.mIsDescriptionMultiline = multiline
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

    private fun drawCircles(canvas: Canvas, paint: Paint, startIndex: Int, endIndex: Int) {
        for (i in startIndex until endIndex) {
            canvas.drawCircle(
                mCellWidth * (i + 1) - mCellWidth / 2,
                mCellHeight / 2,
                spbUtil.mStateRadius,
                paint
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (spbUtil.mMaxStateNumber == 0) spbUtil.mMaxStateNumber = 4
        mCellWidth = (width / spbUtil.mMaxStateNumber).toFloat()
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

    /**
     * Return height of the view
     * */
    private fun getDesiredHeight(): Int {
        return if (mStateDescriptionData.isEmpty()) {
            getCellHeight()
        } else {
            getHeightCalculatedVal()
        }
    }

    /**
     * Calculates the total height of the view
     * */
    private fun getHeightCalculatedVal(): Int {
        val radius = (2 * spbUtil.mStateRadius).toInt()
        val descCalculation = spbUtil.mDescTopSpaceIncrementer.toInt()
            .minus(spbUtil.mDescTopSpaceDecrementer.toInt())
        return if (checkForDescriptionMultiLine(mStateDescriptionData)) {
            val descHeight =
                (selectMaxDescriptionLine(spbUtil.mMaxDescriptionLine) * (1.3 * spbUtil.mStateTextValueSize)).toInt()
            radius + descHeight + spbUtil.mSpacing.toInt() + descCalculation + spbUtil.mDescriptionLinesSpacing.toInt()
        } else {
            radius + (1.3 * spbUtil.mStateTextValueSize).toInt() + spbUtil.mSpacing.toInt() + descCalculation
        }
    }

    /**
     * Calculate height of the subway bar view
     * */
    private fun getCellHeight(): Int {
        return (2 * spbUtil.mStateRadius).toInt() + spbUtil.mSpacing.toInt()
    }

    /**
     * Check for multiple line visibility for the text in the view
     * */
    private fun checkForDescriptionMultiLine(stateDescriptionData: List<String>): Boolean {
        var isMultiLine = false
        for (stateDescription in stateDescriptionData) {
            isMultiLine =
                stateDescription.contains(spbUtil.STATE_DESCRIPTION_LINE_SEPARATOR)
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
                stateDescription.split(spbUtil.STATE_DESCRIPTION_LINE_SEPARATOR)
                    .toTypedArray().size
            maxLine = if (lineSize > maxLine) lineSize else maxLine
        }
        spbUtil.mMaxDescriptionLine = maxLine
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
        spbUtil.mBackgroundPaint?.style = Paint.Style.STROKE
        drawCircles(canvas, spbUtil.mBackgroundPaint!!, spbUtil.mCurrentStateNumber, spbUtil.mMaxStateNumber)
    }

    /**
     * Draw selected subway-points on subway progress bar
     * */
    private fun drawForegroundCircles(canvas: Canvas) {
        drawCircles(canvas, spbUtil.mForegroundPaint!!, 0, spbUtil.mCurrentStateNumber)
    }

    /**
     * Draw lines from one point to another when there is no progress
     * with remaining un-selected states in iterations
     * */
    private fun drawBackgroundLines(canvas: Canvas) {
        val iterations = if (spbUtil.mCurrentStateNumber != 0) {
            (spbUtil.mMaxStateNumber - spbUtil.mCurrentStateNumber) + 1
        } else {
            spbUtil.mMaxStateNumber - spbUtil.mCurrentStateNumber
        }

        var si = if (spbUtil.mCurrentStateNumber - 1 < 0) 0 else spbUtil.mCurrentStateNumber - 1
        var ei = if (spbUtil.mCurrentStateNumber == 0) spbUtil.mCurrentStateNumber + 2 else spbUtil.mCurrentStateNumber + 1
        for (i in 0 until iterations - 1) {
            si = if (i != 0) si + 1 else si
            ei = if (i != 0 && ei + 1 <= spbUtil.mMaxStateNumber) ei + 1 else ei
            drawLines(canvas, spbUtil.mBackgroundPaint!!, si, ei)
        }
    }

    /**
     * draw lines for selected progress states
     * */
    private fun drawForegroundLines(canvas: Canvas) {
        drawLines(canvas, spbUtil.mForegroundPaint!!, 0, spbUtil.mCurrentStateNumber)
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
            startX = startCenterX + spbUtil.mStateRadius * 0.75f
            stopX = endCenterX - spbUtil.mStateRadius * 0.75f
            canvas.drawLine(startX, mCellHeight / 2, stopX, mCellHeight / 2, paint)
        }
    }

    private fun drawCurrentStateJoiningLine(canvas: Canvas) {
        drawLineToCurrentState(canvas)
    }

    private fun drawLineToCurrentState(canvas: Canvas) {
        canvas.drawLine(
            mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2,
            spbUtil.mForegroundPaint!!
        )
        mNextCellWidth = mCellWidth
        invalidate()
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
                    spbUtil.mStateTextValuePaint!!,
                    mStateDescriptionData[i],
                    0f,
                    spbUtil.mStateTextValueSize
                )
            }
        }
        mNextCellWidth = mCellWidth
        invalidate()
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
                    spbUtil.mStateSubtextPaint!!,
                    mStateTextAData[i],
                    60f,
                    spbUtil.mStateSubtextSize
                )
            }
        }
        mNextCellWidth = mCellWidth
        invalidate()
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
            (mCellHeight + textSize - spbUtil.mSpacing - mDescTopSpaceDecrementer + spbUtil.mDescTopSpaceIncrementer).toInt()

        if (i < spbUtil.mMaxStateNumber) {
            xPos = (mNextCellWidth - mCellWidth / 2).toInt()

            if (spbUtil.mIsDescriptionMultiline && spbUtil.mMaxDescriptionLine > 1) {

                var nextLineCounter = 0
                val stateDescriptionLines: Array<String> =
                    text.split(spbUtil.STATE_DESCRIPTION_LINE_SEPARATOR)
                        .toTypedArray()

                for (line in stateDescriptionLines) {
                    nextLineCounter += 1

                    val newXPos = getXPosition(
                        nextLineCounter, stateDescriptionLines[0],
                        line,
                        textColor,
                        xPos
                    )

                    if (nextLineCounter <= spbUtil.mMaxDescriptionLine) {
                        var rNumberVal = 0.0f
                        if (nextLineCounter > 1) {
                            rNumberVal = spbUtil.mDescriptionLinesSpacing * (nextLineCounter - 1) * 2
                        }
                        yPos = ((mCellHeight
                                + nextLineCounter
                                * textSize
                                - spbUtil.mSpacing
                                - mDescTopSpaceDecrementer
                                + spbUtil.mDescTopSpaceIncrementer
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
        if (spbUtil.mJustifyMultilineDescription && counter > 1) {
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
        invalidate()
    }

    /**
     * List of data that has to shown on subway progress as Sub-Text
     * */
    fun setStateDescriptionData(stateDescriptionData: List<String>) {
        mStateDescriptionData = stateDescriptionData as ArrayList<String>
        requestLayout()
        invalidate()
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
        private lateinit var spbUtil: SurveyProgressBarUtil

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
    }
}