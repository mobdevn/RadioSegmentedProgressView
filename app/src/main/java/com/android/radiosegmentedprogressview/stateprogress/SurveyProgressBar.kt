package com.android.radiosegmentedprogressview.stateprogress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.android.radiosegmentedprogressview.R
import com.android.radiosegmentedprogressview.stateprogress.components.FontManager
import com.android.radiosegmentedprogressview.stateprogress.components.StateItem
import kotlin.collections.ArrayList

class SurveyProgressBar(context: Context, attrs: AttributeSet, defStyle: Int) : View(
    context,
    attrs,
    defStyle
) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        initClass(context, attrs, defStyle)
        initializePainters()
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
    }

    private fun initClass(context: Context, attrs: AttributeSet, defStyle: Int) {
        initParams()

        mStateDescriptionSize = convertSpToPixel(mStateDescriptionSize)
        mStateLineThickness = convertDpToPixel(mStateLineThickness)
        mSpacing = convertDpToPixel(mSpacing)
        mCheckFont = FontManager().getTypeface(context)
        mDefaultTypefaceBold = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar, defStyle, 0)

            mBackgroundColor =
                a.getColor(R.styleable.StateProgressBar_spb_stateBackgroundColor, mBackgroundColor)
            mForegroundColor =
                a.getColor(R.styleable.StateProgressBar_spb_stateForegroundColor, mForegroundColor)

            mStateDescriptionColor = a.getColor(
                R.styleable.StateProgressBar_spb_stateDescriptionColor,
                mStateDescriptionColor
            )

            mCurrentStateNumber = a.getInteger(
                R.styleable.StateProgressBar_spb_currentStateNumber,
                mCurrentStateNumber
            )
            mMaxStateNumber =
                a.getInteger(R.styleable.StateProgressBar_spb_maxStateNumber, mMaxStateNumber)

            mStateSize = a.getDimension(R.styleable.StateProgressBar_spb_stateSize, mStateSize)
            mStateNumberTextSize =
                a.getDimension(R.styleable.StateProgressBar_spb_stateTextSize, mStateNumberTextSize)
            mStateDescriptionSize = a.getDimension(
                R.styleable.StateProgressBar_spb_stateDescriptionSize,
                mStateDescriptionSize
            )
            mStateLineThickness = a.getDimension(
                R.styleable.StateProgressBar_spb_stateLineThickness,
                mStateLineThickness
            )

            mCheckStateCompleted = a.getBoolean(
                R.styleable.StateProgressBar_spb_checkStateCompleted,
                mCheckStateCompleted
            )
            mAnimateToCurrentProgressState = a.getBoolean(
                R.styleable.StateProgressBar_spb_animateToCurrentProgressState,
                mAnimateToCurrentProgressState
            )
            mEnableAllStatesCompleted = a.getBoolean(
                R.styleable.StateProgressBar_spb_enableAllStatesCompleted,
                mEnableAllStatesCompleted
            )

            mDescTopSpaceDecrementer = a.getDimension(
                R.styleable.StateProgressBar_spb_descriptionTopSpaceDecrementer,
                mDescTopSpaceDecrementer
            )
            mDescTopSpaceIncrementer = a.getDimension(
                R.styleable.StateProgressBar_spb_descriptionTopSpaceIncrementer,
                mDescTopSpaceIncrementer
            )

            mAnimDuration =
                a.getInteger(R.styleable.StateProgressBar_spb_animationDuration, mAnimDuration)
            mAnimStartDelay =
                a.getInteger(R.styleable.StateProgressBar_spb_animationStartDelay, mAnimStartDelay)

            mIsStateNumberDescending = a.getBoolean(
                R.styleable.StateProgressBar_spb_stateNumberIsDescending,
                mIsStateNumberDescending
            )

            mMaxDescriptionLine = a.getInteger(
                R.styleable.StateProgressBar_spb_maxDescriptionLines,
                mMaxDescriptionLine
            )

            mDescriptionLinesSpacing = a.getDimension(
                R.styleable.StateProgressBar_spb_descriptionLinesSpacing,
                mDescriptionLinesSpacing
            )

            mJustifyMultilineDescription = a.getBoolean(
                R.styleable.StateProgressBar_spb_justifyMultilineDescription,
                mJustifyMultilineDescription
            )

            resolveStateSize()
            validateLineThickness(mStateLineThickness)
            validateStateNumber(mCurrentStateNumber)

            mStateRadius = mStateSize / 2

            a.recycle()
        }
    }

    private fun initParams() {
        mBackgroundColor = ContextCompat.getColor(context, R.color.background_color)
        mForegroundColor = ContextCompat.getColor(context, R.color.foreground_color)

        mStateDescriptionColor = ContextCompat.getColor(context, R.color.background_text_color)

        mStateSize = 0.0f
        mStateSize = 0.0f
        mStateLineThickness = 4.0f
        mStateNumberTextSize = 0.0f
        mStateDescriptionSize = 15f

        mMaxStateNumber = 5

        mCurrentStateNumber = 1

        mSpacing = 4.0f

        mDescTopSpaceDecrementer = 0.0f
        mDescTopSpaceIncrementer = 0.0f

        mDescriptionLinesSpacing = 0.0f

        mCheckStateCompleted = false
        mAnimateToCurrentProgressState = false
        mEnableAllStatesCompleted = false

        mAnimStartDelay = 100
        mAnimDuration = 4000

        mIsStateNumberDescending = false

        mJustifyMultilineDescription = false
    }

    private fun initializePainters() {
        mBackgroundPaint = setPaintAttributes(mStateLineThickness, mBackgroundColor)
        mForegroundPaint = setPaintAttributes(mStateLineThickness, mForegroundColor)

        mStateDescriptionPaint = (mCustomStateDescriptionTypeface ?: mDefaultTypefaceBold)?.let {
            setPaintAttributes(
                mStateDescriptionSize, mStateDescriptionColor,
                it
            )
        }
    }

    fun setStateNumberTypeface(pathToFont: String) {
        mCustomStateNumberTypeface = FontManager().getTypeface(context, pathToFont)
        invalidate()
    }

    fun getStateNumberTypeface(): Typeface? {
        return mCustomStateNumberTypeface
    }

    fun setStateDescriptionTypeface(pathToFont: String) {
        mCustomStateDescriptionTypeface = FontManager().getTypeface(context, pathToFont)
        mStateDescriptionPaint!!.typeface =
            if (mCustomStateDescriptionTypeface != null) mCustomStateDescriptionTypeface else mDefaultTypefaceBold
        /*mCurrentStateDescriptionPaint!!.typeface =
            if (mCustomStateDescriptionTypeface != null) mCustomStateDescriptionTypeface else mDefaultTypefaceBold*/
        invalidate()
    }

    fun getStateDescriptionTypeface(pathToFont: String?): Typeface? {
        return mCustomStateDescriptionTypeface
    }

    private fun validateLineThickness(lineThickness: Float) {
        val halvedStateSize = mStateSize / 2
        if (lineThickness > halvedStateSize) {
            mStateLineThickness = halvedStateSize
        }
    }

    private fun validateStateSize() {
        if (mStateSize <= mStateNumberTextSize) {
            mStateSize = mStateNumberTextSize + mStateNumberTextSize / 2
        }
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        mBackgroundPaint!!.color = mBackgroundColor
        invalidate()
    }

    fun getBackgroundColor(): Int {
        return mBackgroundColor
    }

    fun setForegroundColor(foregroundColor: Int) {
        mForegroundColor = foregroundColor
        mForegroundPaint!!.color = mForegroundColor
        invalidate()
    }

    fun getForegroundColor(): Int {
        return mForegroundColor
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

    fun getStateLineThickness(): Float {
        return mStateLineThickness
    }

    fun setStateNumberBackgroundColor(stateNumberBackgroundColor: Int) {
        /*mStateNumberBackgroundColor = stateNumberBackgroundColor
        mStateNumberBackgroundPaint!!.color = mStateNumberBackgroundColor*/
        invalidate()
    }

    fun setStateDescriptionColor(stateDescriptionColor: Int) {
        mStateDescriptionColor = stateDescriptionColor
        mStateDescriptionPaint!!.color = mStateDescriptionColor
        invalidate()
    }

    fun getStateDescriptionColor(): Int {
        return mStateDescriptionColor
    }

    fun setCurrentStateNumber(currentStateNumber: Int) {
        if (currentStateNumber < 1 || currentStateNumber > mMaxStateNumber) return
        mCurrentStateNumber = currentStateNumber
        updateCheckAllStatesValues(mEnableAllStatesCompleted)
        invalidate()
    }

    fun getCurrentStateNumber(): Int {
        return mCurrentStateNumber
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

    fun getMaxStateNumber(): Int {
        return mMaxStateNumber
    }

    fun setStateSize(stateSize: Float) {
        mStateSize = convertDpToPixel(stateSize)
        resetStateSizeValues()
    }

    private fun resetStateSizeValues() {
        resolveStateSize()
        mStateRadius = mStateSize / 2
        validateLineThickness(mStateLineThickness)
        mBackgroundPaint!!.strokeWidth = mStateLineThickness
        mForegroundPaint!!.strokeWidth = mStateLineThickness
        requestLayout()
    }

    fun setStateDescriptionSize(stateDescriptionSize: Float) {
        mStateDescriptionSize = convertSpToPixel(stateDescriptionSize)
        resolveStateDescriptionSize()
    }

    private fun resolveStateDescriptionSize() {
        mStateDescriptionPaint!!.textSize = mStateDescriptionSize
        requestLayout()
    }

    fun getStateDescriptionSize(): Float {
        return mStateDescriptionSize
    }


    fun getStateNumberTextSize(): Float {
        return mStateNumberTextSize
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
        } else {
            mStateDescriptionPaint!!.color = mStateDescriptionPaint!!.color
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

    fun getDescriptionTopSpaceDecrementer(): Float {
        return mDescTopSpaceDecrementer
    }

    fun getDescriptionTopSpaceIncrementer(): Float {
        return mDescTopSpaceIncrementer
    }

    fun getDescriptionLinesSpacing(): Float {
        return mDescriptionLinesSpacing
    }

    fun setDescriptionLinesSpacing(descriptionLinesSpacing: Float) {
        mDescriptionLinesSpacing = descriptionLinesSpacing
        requestLayout()
    }

    fun setStateNumberIsDescending(stateNumberIsDescending: Boolean) {
        mIsStateNumberDescending = stateNumberIsDescending
        invalidate()
    }

    fun getStateNumberIsDescending(): Boolean {
        return mIsStateNumberDescending
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
        resolveStateSize(mStateSize != 0f, mStateNumberTextSize != 0f)
    }

    private fun resolveStateSize(isStateSizeSet: Boolean, isStateTextSizeSet: Boolean) {
        if (!isStateSizeSet && !isStateTextSizeSet) {
            mStateSize =
                convertDpToPixel(DEFAULT_STATE_SIZE)
            mStateNumberTextSize =
                convertSpToPixel(DEFAULT_TEXT_SIZE)
        } else if (isStateSizeSet && isStateTextSizeSet) {
            validateStateSize()
        } else if (!isStateSizeSet) {
            mStateSize = mStateNumberTextSize + mStateNumberTextSize / 2
        } else {
            mStateNumberTextSize = mStateSize - mStateSize * 0.375f
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
            (2 * mStateRadius).toInt() + mSpacing.toInt()
        } else {
            if (checkForDescriptionMultiLine(mStateDescriptionData)) {
                (2 * mStateRadius).toInt() + (selectMaxDescriptionLine(mMaxDescriptionLine) * (1.3 * mStateDescriptionSize)).toInt() + mSpacing.toInt() - mDescTopSpaceDecrementer.toInt() + mDescTopSpaceIncrementer.toInt() + mDescriptionLinesSpacing.toInt()
            } else {
                (2 * mStateRadius).toInt() + (1.3 * mStateDescriptionSize).toInt() + mSpacing.toInt() - mDescTopSpaceDecrementer.toInt() + mDescTopSpaceIncrementer.toInt()
            }
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

    private fun drawState(canvas: Canvas) {
        drawCurrentStateJoiningLine(canvas)
        drawBackgroundLines(canvas)
        drawBackgroundCircles(canvas)
        drawForegroundCircles(canvas)
        drawForegroundLines(canvas)
        drawStateTextAmt(canvas)
        drawStateDescriptionText(canvas)
    }

    private fun drawBackgroundCircles(canvas: Canvas) {
        val startIndex = if (mIsStateNumberDescending) 0 else mCurrentStateNumber
        val endIndex =
            if (mIsStateNumberDescending) mMaxStateNumber - mCurrentStateNumber else mMaxStateNumber
        drawCircles(canvas, mBackgroundPaint!!, startIndex, endIndex)
    }

    private fun drawForegroundCircles(canvas: Canvas) {
        val startIndex = if (mIsStateNumberDescending) mMaxStateNumber - mCurrentStateNumber else 0
        val endIndex = if (mIsStateNumberDescending) mMaxStateNumber else mCurrentStateNumber
        drawCircles(canvas, mForegroundPaint!!, startIndex, endIndex)
    }

    private fun drawBackgroundLines(canvas: Canvas) {
        val startIndex = if (mIsStateNumberDescending) 0 else mCurrentStateNumber - 1
        val endIndex =
            if (mIsStateNumberDescending) mMaxStateNumber - mCurrentStateNumber + 1 else mMaxStateNumber
        drawLines(canvas, mBackgroundPaint!!, startIndex, endIndex)
    }

    private fun drawForegroundLines(canvas: Canvas) {
        val startIndex =
            if (mIsStateNumberDescending) mMaxStateNumber - mCurrentStateNumber + 1 else 0
        val endIndex = if (mIsStateNumberDescending) mMaxStateNumber else mCurrentStateNumber
        drawLines(canvas, mForegroundPaint!!, startIndex, endIndex)
    }

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

    private fun drawStateDescriptionText(canvas: Canvas) {
        var xPos: Int
        var yPos: Int
        var innerPaintType: Paint
        if (!mStateDescriptionData.isEmpty()) {
            for (i in mStateDescriptionData.indices) {
                if (i < mMaxStateNumber) {
                    innerPaintType = selectDescriptionPaint(mCurrentStateNumber, i)
                    xPos = (mNextCellWidth - mCellWidth / 2).toInt()
                    if (mIsDescriptionMultiline && mMaxDescriptionLine > 1) {
                        val stateDescription =
                            if (mIsStateNumberDescending) mStateDescriptionData[mStateDescriptionData.size - 1 - i] else mStateDescriptionData[i]
                        var nextLineCounter = 0
                        var newXPos = 0
                        val stateDescriptionLines: Array<String> =
                            stateDescription.split(STATE_DESCRIPTION_LINE_SEPARATOR)
                                .toTypedArray()
                        for (line in stateDescriptionLines) {
                            nextLineCounter = nextLineCounter + 1
                            if (mJustifyMultilineDescription && nextLineCounter > 1) {
                                newXPos = getNewXPosForDescriptionMultilineJustification(
                                    stateDescriptionLines[0], line, innerPaintType, xPos
                                )
                            }
                            if (nextLineCounter <= mMaxDescriptionLine) {
                                var rNumberVal = 0.0f
                                if (nextLineCounter > 1) {
                                    rNumberVal = mDescriptionLinesSpacing * (nextLineCounter - 1)
                                }
                                yPos =
                                    ((mCellHeight + nextLineCounter * mStateDescriptionSize - mSpacing - mDescTopSpaceDecrementer + 16f + rNumberVal).toInt())//mSpacing = mStateNumberForegroundPaint.getTextSize()
                                canvas.drawText(
                                    line,
                                    (if (newXPos == 0) xPos else newXPos.toFloat()) as Float,
                                    yPos.toFloat(),
                                    innerPaintType
                                )
                            }
                        }
                    } else {
                        yPos =
                            (mCellHeight + mStateDescriptionSize - mSpacing - mDescTopSpaceDecrementer + 16f).toInt() //mSpacing = mStateNumberForegroundPaint.getTextSize()
                        canvas.drawText(
                            if (mIsStateNumberDescending) mStateDescriptionData[mStateDescriptionData.size - 1 - i] else mStateDescriptionData[i],
                            xPos.toFloat(),
                            yPos.toFloat(),
                            innerPaintType
                        )
                    }
                    mNextCellWidth += mCellWidth
                }
            }
        }
        mNextCellWidth = mCellWidth
    }

    private fun drawStateTextAmt(canvas: Canvas) {
        var xPos: Int
        var yPos: Int
        var innerPaintType: Paint
        if (!mStateTextAData.isEmpty()) {
            for (i in mStateTextAData.indices) {
                if (i < mMaxStateNumber) {
                    innerPaintType = selectDescriptionPaint(mCurrentStateNumber, i)
                    xPos = (mNextCellWidth - mCellWidth / 2).toInt()
                    if (mIsDescriptionMultiline && mMaxDescriptionLine > 1) {
                        val stateDescription = mStateTextAData[i]
                        var nextLineCounter = 0
                        var newXPos = 0
                        val stateDescriptionLines: Array<String> =
                            stateDescription.split(STATE_DESCRIPTION_LINE_SEPARATOR)
                                .toTypedArray()
                        for (line in stateDescriptionLines) {
                            nextLineCounter += 1
                            if (mJustifyMultilineDescription && nextLineCounter > 1) {
                                newXPos = getNewXPosForDescriptionMultilineJustification(
                                    stateDescriptionLines[0], line, innerPaintType, xPos
                                )
                            }
                            if (nextLineCounter <= mMaxDescriptionLine) {
                                var rNumberVal = 0.0f
                                if (nextLineCounter > 1) {
                                    rNumberVal = mDescriptionLinesSpacing * (nextLineCounter - 1) * 2
                                }
                                yPos =
                                    ((mCellHeight + nextLineCounter * mStateDescriptionSize - mSpacing - mDescTopSpaceDecrementer + mDescTopSpaceIncrementer + rNumberVal).toInt())
                                canvas.drawText(
                                    line,
                                    (if (newXPos == 0) xPos else newXPos.toFloat()) as Float,
                                    yPos.toFloat(),
                                    innerPaintType
                                )
                            }
                        }
                    } else {
                        yPos =
                            (mCellHeight + mStateDescriptionSize - mSpacing - mDescTopSpaceDecrementer + mDescTopSpaceIncrementer).toInt()
                        canvas.drawText(
                            mStateTextAData[i],
                            xPos.toFloat(),
                            yPos.toFloat(),
                            innerPaintType
                        )
                    }
                    mNextCellWidth += mCellWidth
                }
            }
        }
        mNextCellWidth = mCellWidth
    }

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

    private fun selectDescriptionPaint(currentState: Int, statePosition: Int): Paint {
        var currentState = currentState
        currentState =
            if (mIsStateNumberDescending) mMaxStateNumber + 1 - currentState else currentState
        return if (statePosition + 1 == currentState) {
            mStateDescriptionPaint!!
        } else {
            mStateDescriptionPaint!!
        }
    }


    fun setStateTextRowOneData(stateTextOneData: List<String?>) {
        mStateTextAData = stateTextOneData as ArrayList<String>
        requestLayout()
    }

    fun setStateDescriptionData(stateDescriptionData: List<String>) {
        mStateDescriptionData = stateDescriptionData as ArrayList<String>
        requestLayout()
    }

    private fun convertDpToPixel(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale
    }

    private fun convertSpToPixel(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }

    private companion object {
        private var mStateDescriptionData = arrayListOf<String>()
        private var mStateTextAData = arrayListOf<String>()

        private var mStateRadius = 0f
        private var mStateSize = 0f
        private var mStateLineThickness = 0f
        private var mStateNumberTextSize = 0f
        private var mStateDescriptionSize = 0f

        /**
         * width of one cell = stageWidth/noOfStates
         */
        private var mCellWidth = 0f

        private var mCellHeight = 0f

        /**
         * next cell(state) from previous cell
         */
        private var mNextCellWidth = 0f

        /**
         * center of first cell(state)
         */
        private var mStartCenterX = 0f

        /**
         * center of last cell(state)
         */
        private var mEndCenterX = 0f

        private var mMaxStateNumber = 0
        private var mCurrentStateNumber = 0

        private var mAnimStartDelay = 0
        private var mAnimDuration = 0

        private var mSpacing = 0f

        private var mDescTopSpaceDecrementer = 0f
        private var mDescTopSpaceIncrementer = 0f

        private const val DEFAULT_TEXT_SIZE = 15f
        private const val DEFAULT_STATE_SIZE = 25f

        /**
         * Paints for drawing
         */
        private var mBackgroundPaint: Paint? = null
        private var mForegroundPaint: Paint? = null
        private var mStateDescriptionPaint: Paint? = null

        private var mBackgroundColor = 0
        private var mForegroundColor = 0
        private var mStateDescriptionColor = 0

        private var mIsStateNumberDescending = false

        private var mCustomStateNumberTypeface: Typeface? = null
        private var mCustomStateDescriptionTypeface: Typeface? = null
        private var mDefaultTypefaceBold: Typeface? = null
        private var mIsDescriptionMultiline = false
        private var mMaxDescriptionLine = 0
        private var mDescriptionLinesSpacing = 0f
        private var STATE_DESCRIPTION_LINE_SEPARATOR = "\n"
        private var mJustifyMultilineDescription = false
        private var mAnimateToCurrentProgressState = false
        private var mEnableAllStatesCompleted = false
        private var mCheckStateCompleted = false
        private var mCheckFont: Typeface? = null

    }
}