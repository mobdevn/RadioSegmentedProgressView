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
import kotlin.collections.ArrayList

class StateProgressView(context: Context, attrs: AttributeSet, defStyle: Int) : View(
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
            /*mStateNumberBackgroundColor = a.getColor(
                R.styleable.StateProgressBar_spb_stateNumberBackgroundColor,
                mStateNumberBackgroundColor
            )
            mStateNumberForegroundColor = a.getColor(
                R.styleable.StateProgressBar_spb_stateNumberForegroundColor,
                mStateNumberForegroundColor
            )
            mCurrentStateDescriptionColor = a.getColor(
                R.styleable.StateProgressBar_spb_currentStateDescriptionColor,
                mCurrentStateDescriptionColor
            )*/
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


            /*if (!mAnimateToCurrentProgressState) {
                stopAnimation()
            }*/

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
        /*mStateNumberBackgroundColor = ContextCompat.getColor(context, R.color.background_text_color)
        mStateNumberForegroundColor = ContextCompat.getColor(context, R.color.foreground_text_color)
        mCurrentStateDescriptionColor = ContextCompat.getColor(context, R.color.foreground_color)*/
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

       /* mStateNumberForegroundPaint = (mCustomStateNumberTypeface ?: mDefaultTypefaceBold)?.let {
            setPaintAttributes(
                mStateNumberTextSize, mStateNumberForegroundColor,
                it
            )
        }
        mStateCheckedForegroundPaint =
            mCheckFont?.let {
                setPaintAttributes(
                    mStateNumberTextSize, mStateNumberForegroundColor,
                    it
                )
            }

        mStateNumberBackgroundPaint = (mCustomStateNumberTypeface ?: mDefaultTypefaceBold)?.let {
            setPaintAttributes(
                mStateNumberTextSize, mStateNumberBackgroundColor,
                it
            )
        }
        mCurrentStateDescriptionPaint =
            (mCustomStateDescriptionTypeface ?: mDefaultTypefaceBold)?.let {
                setPaintAttributes(
                    mStateDescriptionSize, mCurrentStateDescriptionColor,
                    it
                )
            }*/

        mStateDescriptionPaint = (mCustomStateDescriptionTypeface ?: mDefaultTypefaceBold)?.let {
            setPaintAttributes(
                mStateDescriptionSize, mStateDescriptionColor,
                it
            )
        }
    }

    fun setStateNumberTypeface(pathToFont: String) {
        mCustomStateNumberTypeface = FontManager().getTypeface(context, pathToFont)
       /* mStateNumberForegroundPaint!!.typeface =
            if (mCustomStateNumberTypeface != null) mCustomStateNumberTypeface else mDefaultTypefaceBold
        mStateNumberBackgroundPaint!!.typeface =
            if (mCustomStateNumberTypeface != null) mCustomStateNumberTypeface else mDefaultTypefaceBold*/
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

   /* fun getStateNumberBackgroundColor(): Int {
        return mStateNumberBackgroundColor
    }*/

    /*fun setStateNumberForegroundColor(stateNumberForegroundColor: Int) {
        mStateNumberForegroundColor = stateNumberForegroundColor
        mStateNumberForegroundPaint!!.color = mStateNumberForegroundColor
        mStateCheckedForegroundPaint!!.color = mStateNumberForegroundColor
        invalidate()
    }

    fun getStateNumberForegroundColor(): Int {
        return mStateNumberForegroundColor
    }*/

    fun setStateDescriptionColor(stateDescriptionColor: Int) {
        mStateDescriptionColor = stateDescriptionColor
        mStateDescriptionPaint!!.color = mStateDescriptionColor
        invalidate()
    }

    fun getStateDescriptionColor(): Int {
        return mStateDescriptionColor
    }

   /* fun setCurrentStateDescriptionColor(currentStateDescriptionColor: Int) {
        mCurrentStateDescriptionColor = currentStateDescriptionColor
        mCurrentStateDescriptionPaint!!.color = mCurrentStateDescriptionColor
        invalidate()
    }

    fun getCurrentStateDescriptionColor(): Int {
        return mCurrentStateDescriptionColor
    }*/

    fun setCurrentStateNumber(currentStateNumber: Int) {
        //validateStateNumber(currentStateNumber.getValue())
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

    fun getStateSize(): Float {
        return mStateSize
    }

    fun setStateNumberTextSize(textSize: Float) {
        /*mStateNumberTextSize = convertSpToPixel(textSize)
        resetStateSizeValues()*/
    }


    private fun resetStateSizeValues() {
        resolveStateSize()
        /*mStateNumberForegroundPaint!!.textSize = mStateNumberTextSize
        mStateNumberBackgroundPaint!!.textSize = mStateNumberTextSize
        mStateCheckedForegroundPaint!!.textSize = mStateNumberTextSize*/
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
        //mCurrentStateDescriptionPaint!!.textSize = mStateDescriptionSize
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
            //mStateDescriptionPaint!!.color = mCurrentStateDescriptionPaint!!.color
        } else {
            mStateDescriptionPaint!!.color = mStateDescriptionPaint!!.color
        }
    }


    /*fun enableAnimationToCurrentState(animateToCurrentProgressState: Boolean) {
        mAnimateToCurrentProgressState = animateToCurrentProgressState
        if (mAnimateToCurrentProgressState && mAnimator == null) {
            startAnimator()
        }
        invalidate()
    }*/


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

    fun setAnimationDuration(animDuration: Int) {
        mAnimDuration = animDuration
        invalidate()
    }

    fun getAnimationDuration(): Int {
        return mAnimDuration
    }

    fun setAnimationStartDelay(animStartDelay: Int) {
        mAnimStartDelay = animStartDelay
        invalidate()
    }

    fun getAnimationStartDelay(): Int {
        return mAnimStartDelay
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


    fun getMaxDescriptionLine(): Int {
        return mMaxDescriptionLine
    }


    fun setMaxDescriptionLine(maxDescriptionLine: Int) {
        mMaxDescriptionLine = maxDescriptionLine
        requestLayout()
    }


    fun isJustifyMultilineDescription(): Boolean {
        return mJustifyMultilineDescription
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

    /*override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mOnStateItemClickListener == null) {
            return false
        }
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            if (isPointInCircle(event.x.toInt(), event.y.toInt())) {
                performClick()
                return true
            }
            return false
        }
        return false
    }*/

    /*override fun performClick(): Boolean {
        super.performClick()
        if (mOnStateItemClickListener != null) {
            mOnStateItemClickListener!!.onStateItemClick(
                this,
                getStateItem(mStateItemClickedNumber),
                mStateItemClickedNumber,
                getCurrentStateNumber() == mStateItemClickedNumber
            )
            return true
        }
        return false
    }*/


    /*private fun isPointInCircle(clickX: Int, clickY: Int): Boolean {
        var isTouched = false
        for (i in 0 until mMaxStateNumber) {
            isTouched =
                !(clickX < mCellWidth * (i + 1) - mCellWidth / 2 - mStateRadius || clickX > mCellWidth * (i + 1) - mCellWidth / 2 + mStateRadius || clickY < mCellHeight / 2 - mStateRadius || clickY > mCellHeight / 2 + mStateRadius)
            if (isTouched) {
                mStateItemClickedNumber =
                    if (mIsStateNumberDescending) mMaxStateNumber - i else i + 1
                return isTouched
            }
        }
        return isTouched
    }
*/
    /*private fun getStateItem(stateItemClickedNumber: Int): StateItem? {
        val isCurrentState = getCurrentStateNumber() == stateItemClickedNumber
        val isForegroundColor = getCurrentStateNumber() >= stateItemClickedNumber
        val isCompletedState = getCurrentStateNumber() > stateItemClickedNumber
        val stateSize = getStateSize()
        val stateColor = if (isForegroundColor) mForegroundColor else mBackgroundColor
        var isCheckedState = false
        var stateItemDescription: StateItemDescription? = null
        if (isCompletedState && mCheckStateCompleted) {
            isCheckedState = true
        }
        *//*val stateNumberColor =
            if (isForegroundColor) mStateNumberForegroundColor else mStateNumberBackgroundColor*//*
        //val stateNumberSize = getStateNumberTextSize()
        *//*val stateDescriptionColor =
            if (isCurrentState) mCurrentStateDescriptionColor else mStateDescriptionColor*//*
        *//*val stateItemNumber: StateItemNumber? =
            StateItemNumber().builder().color(stateNumberColor)?.size(stateNumberSize)
                ?.number(stateItemClickedNumber)
                ?.build()*//*
        *//*if (!getStateDescriptionData()?.isEmpty()!! && stateItemClickedNumber <= getStateDescriptionData()?.size!!) {
            val stateDescriptionSize = getStateDescriptionSize()
            stateItemDescription = StateItemDescription().builder()?.color(stateDescriptionColor)
                ?.size(stateDescriptionSize)
                ?.text(getStateDescriptionData()?.get(if (mIsStateNumberDescending) if (getStateDescriptionData()?.size!! >= mMaxStateNumber) stateItemClickedNumber - 1 + (getStateDescriptionData()?.size!! - mMaxStateNumber) else stateItemClickedNumber - 1 else stateItemClickedNumber - 1))
                ?.build()
        }*//*
        return StateItem().builder()?.color(stateColor)?.size(stateSize)
            ?.build()
            *//*StateItem().builder()?.color(stateColor)?.size(stateSize)
            ?.stateItemNumber(stateItemNumber)?.isCurrentState(isCurrentState)
            ?.isStateChecked(isCheckedState)?.stateItemDescription(stateItemDescription)?.build()*//*
    }*/

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
        //setAnimatorStartEndCenterX()
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

    /*private fun setAnimatorStartEndCenterX() {
        if (mCurrentStateNumber > MIN_STATE_NUMBER && mCurrentStateNumber < MAX_STATE_NUMBER + 1) {
            val count =
                if (mIsStateNumberDescending) mMaxStateNumber - mCurrentStateNumber + 1 else mCurrentStateNumber - 1
            for (i in 0 until count) {
                mStartCenterX = if (i == 0) {
                    mNextCellWidth - mCellWidth / 2
                } else {
                    mEndCenterX
                }
                mNextCellWidth += mCellWidth
                mEndCenterX = mNextCellWidth - mCellWidth / 2
            }
        } else {
            resetStateAnimationData()
        }
    }
*/

    private fun drawCurrentStateJoiningLine(canvas: Canvas) {
        if (mAnimateToCurrentProgressState && mCurrentStateNumber > 1) {
            //animateToCurrentState(canvas)
        } else {
            drawLineToCurrentState(canvas)
        }
    }


    private fun drawLineToCurrentState(canvas: Canvas) {
        canvas.drawLine(
            mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2,
            mForegroundPaint!!
        )
        mNextCellWidth = mCellWidth
        //stopAnimation()
    }


   /* private fun animateToCurrentState(canvas: Canvas) {
        if (!mIsCurrentAnimStarted) {
            mAnimStartXPos = mStartCenterX
            mAnimEndXPos = mAnimStartXPos
            mIsCurrentAnimStarted = true
        }
        if (mAnimEndXPos < mStartCenterX || mStartCenterX > mEndCenterX) {
            stopAnimation()
            enableAnimationToCurrentState(false)
            invalidate()
        } else if (mAnimEndXPos <= mEndCenterX) {
            if (!mIsStateNumberDescending) {
                canvas.drawLine(
                    mStartCenterX, mCellHeight / 2, mAnimEndXPos, mCellHeight / 2,
                    mForegroundPaint!!
                )
                canvas.drawLine(
                    mAnimEndXPos, mCellHeight / 2, mEndCenterX, mCellHeight / 2,
                    mBackgroundPaint!!
                )
            } else {
                canvas.drawLine(
                    mEndCenterX,
                    mCellHeight / 2,
                    mEndCenterX - (mAnimEndXPos - mStartCenterX),
                    mCellHeight / 2,
                    mForegroundPaint!!
                )
                canvas.drawLine(
                    mEndCenterX - (mAnimEndXPos - mStartCenterX),
                    mCellHeight / 2,
                    mStartCenterX,
                    mCellHeight / 2,
                    mBackgroundPaint!!
                )
            }
            mAnimStartXPos = mAnimEndXPos
        } else {
            if (!mIsStateNumberDescending) canvas.drawLine(
                mStartCenterX, mCellHeight / 2, mEndCenterX, mCellHeight / 2,
                mForegroundPaint!!
            ) else canvas.drawLine(
                mEndCenterX, mCellHeight / 2, mStartCenterX, mCellHeight / 2,
                mForegroundPaint!!
            )
        }
        mNextCellWidth = mCellWidth
    }*/


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
                            //if (mIsStateNumberDescending) mStateTextAData[mStateTextAData.size - 1 - i] else mStateTextAData[i]
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


    private fun resolveStateDescriptionDataSize(stateDescriptionData: MutableList<String>) {
        val stateDescriptionDataSize = stateDescriptionData.size
        if (stateDescriptionDataSize < mMaxStateNumber) {
            for (i in 0 until mMaxStateNumber - stateDescriptionDataSize) {
                stateDescriptionData.add(
                    stateDescriptionDataSize + i,
                    EMPTY_SPACE_DESCRIPTOR
                )
            }
        }
    }

    fun setStateTextRowOneData(stateTextOneData: List<String?>) {
        mStateTextAData = stateTextOneData as ArrayList<String>
        //resolveStateDescriptionDataSize(mStateTextAData)
        requestLayout()
    }

    fun setStateDescriptionData(stateDescriptionData: List<String>) {
        mStateDescriptionData = stateDescriptionData as ArrayList<String>
        //resolveStateDescriptionDataSize(mStateDescriptionData)
        requestLayout()
    }

   /* fun getStateDescriptionData(): List<String?>? {
        return mStateDescriptionData
    }*/

    /*private fun resetStateAnimationData() {
        if (mStartCenterX > 0 || mStartCenterX < 0) mStartCenterX = 0f
        if (mEndCenterX > 0 || mEndCenterX < 0) mEndCenterX = 0f
        if (mAnimEndXPos > 0 || mAnimEndXPos < 0) mAnimEndXPos = 0f
        if (mIsCurrentAnimStarted) mIsCurrentAnimStarted = false
    }*/


    /*private fun drawStateNumberText(canvas: Canvas, noOfCircles: Int) {
        var xPos: Int
        var yPos: Int
        var innerPaintType: Paint
        var isChecked: Boolean
        for (i in 0 until noOfCircles) {
            innerPaintType = selectPaintType(mCurrentStateNumber, i, mCheckStateCompleted)
            xPos = (mCellWidth * (i + 1) - mCellWidth / 2).toInt()
            yPos =
                (mCellHeight / 2 - (innerPaintType.descent() + innerPaintType.ascent()) / 2).toInt()
            isChecked = isCheckIconUsed(mCurrentStateNumber, i)
            if (mCheckStateCompleted && isChecked) {
                canvas.drawText(
                    context.getString(R.string.check_icon),
                    xPos.toFloat(),
                    yPos.toFloat(),
                    innerPaintType
                )
            } else {
                if (mIsStateNumberDescending) canvas.drawText(
                    (noOfCircles - i).toString(),
                    xPos.toFloat(),
                    yPos.toFloat(),
                    innerPaintType
                ) else canvas.drawText(
                    (i + 1).toString(),
                    xPos.toFloat(),
                    yPos.toFloat(),
                    innerPaintType
                )
            }
        }
    }*/


   /* private fun selectPaintType(
        currentState: Int,
        statePosition: Int,
        checkStateCompleted: Boolean
    ): Paint? {
        var currentState = currentState
        currentState = if (mIsStateNumberDescending) mMaxStateNumber - currentState else currentState
        *//*val foregroundPaint = if (mIsStateNumberDescending) mStateNumberBackgroundPaint!! else mStateNumberForegroundPaint!!
        val backgroundPaint = if (mIsStateNumberDescending) mStateNumberForegroundPaint!! else mStateNumberBackgroundPaint!!*//*
        return if (checkStateCompleted) {
            applyCheckStateCompletedPaintType(currentState, statePosition, checkStateCompleted)
        } else {
            if (statePosition + 1 == currentState || statePosition + 1 < currentState && !checkStateCompleted) {
                mForegroundPaint
            } else {
                mBackgroundPaint
            }
        }
    }*/


   /* private fun applyCheckStateCompletedPaintType(
        currentState: Int,
        statePosition: Int,
        checkStateCompleted: Boolean
    ): Paint {
        return if (checkStateCompleted(currentState, statePosition, checkStateCompleted)) {
            mForegroundPaint!!
        } else if (statePosition + 1 == (if (mIsStateNumberDescending) currentState + 1 else currentState)) {
            mForegroundPaint!!
        } else {
            mBackgroundPaint!!
        }
    }*/


   /* private fun checkStateCompleted(
        currentState: Int,
        statePosition: Int,
        checkStateCompleted: Boolean
    ): Boolean {
        if (!mIsStateNumberDescending) {
            if (mEnableAllStatesCompleted && checkStateCompleted || statePosition + 1 < currentState && checkStateCompleted) {
                return true
            }
        } else {
            if (mEnableAllStatesCompleted && checkStateCompleted || statePosition + 1 > currentState + 1 && checkStateCompleted) {
                return true
            }
        }
        return false
    }*/


   /* private fun isCheckIconUsed(currentState: Int, statePosition: Int): Boolean {
        var currentState = currentState
        currentState =
            if (mIsStateNumberDescending) mMaxStateNumber + 1 - currentState else currentState
        return if (!mIsStateNumberDescending) mEnableAllStatesCompleted || statePosition + 1 < currentState else mEnableAllStatesCompleted || statePosition + 1 > currentState
    }*/


   /* private fun startAnimator() {
        mAnimator = Animator()
        mAnimator?.start()
    }

    private fun stopAnimation() {
        if (mAnimator != null) {
            mAnimator?.stop()
        }
    }

    inner internal class Animator : Runnable {
        private val mScroller: Scroller
        private var mRestartAnimation = false
        override fun run() {
            if (mAnimator !== this) return
            if (mRestartAnimation) {
                mScroller.startScroll(
                    0,
                    mStartCenterX.toInt(),
                    0,
                    mEndCenterX.toInt(),
                    mAnimDuration
                )
                mRestartAnimation = false
            }
            val scrollRemains = mScroller.computeScrollOffset()
            mAnimStartXPos = mAnimEndXPos
            mAnimEndXPos = mScroller.currY.toFloat()
            if (scrollRemains) {
                invalidate()
                post(this)
            } else {
                stop()
                enableAnimationToCurrentState(false)
            }
        }

        fun start() {
            mRestartAnimation = true
            postDelayed(this, mAnimStartDelay.toLong())
        }

        fun stop() {
            removeCallbacks(this)
            mAnimator = null
        }

        init {
            mScroller = Scroller(context, AccelerateDecelerateInterpolator())
        }
    }
*/

    private fun convertDpToPixel(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale
    }

    private fun convertSpToPixel(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }


    /*override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //startAnimator()
    }


    override fun onDetachedFromWindow() {
        //stopAnimation()
        super.onDetachedFromWindow()
    }


    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        //startAnimator()
    }*/


    /*override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable(
            INSTANCE_STATE,
            super.onSaveInstanceState()
        )
        bundle.putFloat(
            END_CENTER_X_KEY,
            mEndCenterX
        )
        bundle.putFloat(
            START_CENTER_X_KEY,
            mStartCenterX
        )
        bundle.putFloat(
            ANIM_START_X_POS_KEY,
            mAnimStartXPos
        )
        bundle.putFloat(
            ANIM_END_X_POS_KEY,
            mAnimEndXPos
        )
        bundle.putBoolean(
            IS_CURRENT_ANIM_STARTED_KEY,
            mIsCurrentAnimStarted
        )
        bundle.putBoolean(
            ANIMATE_TO_CURRENT_PROGRESS_STATE_KEY,
            mAnimateToCurrentProgressState
        )
        bundle.putBoolean(
            IS_STATE_NUMBER_DESCENDING_KEY,
            mIsStateNumberDescending
        )
        bundle.putFloat(STATE_SIZE_KEY, mStateSize)
        bundle.putFloat(
            STATE_LINE_THICKNESS_KEY,
            mStateLineThickness
        )
        bundle.putFloat(
            STATE_NUMBER_TEXT_SIZE_KEY,
            mStateNumberTextSize
        )
        bundle.putFloat(
            STATE_DESCRIPTION_SIZE_KEY,
            mStateDescriptionSize
        )
        bundle.putInt(
            MAX_STATE_NUMBER_KEY,
            mMaxStateNumber
        )
        bundle.putInt(
            CURRENT_STATE_NUMBER_KEY,
            mCurrentStateNumber
        )
        bundle.putInt(
            ANIM_START_DELAY_KEY,
            mAnimStartDelay
        )
        bundle.putInt(
            ANIM_DURATION_KEY,
            mAnimDuration
        )
        bundle.putFloat(
            DESC_TOP_SPACE_DECREMENTER_KEY,
            mDescTopSpaceDecrementer
        )
        bundle.putFloat(
            DESC_TOP_SPACE_INCREMENTER_KEY,
            mDescTopSpaceIncrementer
        )
        bundle.putFloat(
            DESCRIPTION_LINE_SPACING_KEY,
            mDescriptionLinesSpacing
        )
        bundle.putInt(
            BACKGROUND_COLOR_KEY,
            mBackgroundColor
        )
        bundle.putInt(
            FOREGROUND_COLOR_KEY,
            mForegroundColor
        )
        *//*bundle.putInt(
            STATE_NUMBER_BACKGROUND_COLOR_KEY,
            mStateNumberBackgroundColor
        )
        bundle.putInt(
            STATE_NUMBER_FOREGROUND_COLOR_KEY,
            mStateNumberForegroundColor
        )
        bundle.putInt(
            CURRENT_STATE_DESC_COLOR_KEY,
            mCurrentStateDescriptionColor
        )*//*
        bundle.putInt(
            STATE_DESC_COLOR_KEY,
            mStateDescriptionColor
        )
        bundle.putBoolean(
            CHECK_STATE_COMPLETED_KEY,
            mCheckStateCompleted
        )
        bundle.putBoolean(
            ENABLE_ALL_STATES_COMPLETED_KEY,
            mEnableAllStatesCompleted
        )
        bundle.putBoolean(
            JUSTIFY_MULTILINE_DESC_KEY,
            mJustifyMultilineDescription
        )
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            mEndCenterX =
                bundle.getFloat(END_CENTER_X_KEY)
            mStartCenterX =
                bundle.getFloat(START_CENTER_X_KEY)
            mAnimStartXPos =
                bundle.getFloat(ANIM_START_X_POS_KEY)
            mAnimEndXPos =
                bundle.getFloat(ANIM_END_X_POS_KEY)
            mIsCurrentAnimStarted =
                bundle.getBoolean(IS_CURRENT_ANIM_STARTED_KEY)
            mAnimateToCurrentProgressState =
                bundle.getBoolean(ANIMATE_TO_CURRENT_PROGRESS_STATE_KEY)
            mIsStateNumberDescending =
                bundle.getBoolean(IS_STATE_NUMBER_DESCENDING_KEY)
            mStateNumberTextSize =
                bundle.getFloat(STATE_NUMBER_TEXT_SIZE_KEY)
            mStateSize =
                bundle.getFloat(STATE_SIZE_KEY)
            resetStateSizeValues()
            mStateLineThickness =
                bundle.getFloat(STATE_LINE_THICKNESS_KEY)
            resolveStateLineThickness()
            mStateDescriptionSize =
                bundle.getFloat(STATE_DESCRIPTION_SIZE_KEY)
            resolveStateDescriptionSize()
            mMaxStateNumber =
                bundle.getInt(MAX_STATE_NUMBER_KEY)
            mCurrentStateNumber =
                bundle.getInt(CURRENT_STATE_NUMBER_KEY)
            resolveMaxStateNumber()
            mAnimStartDelay =
                bundle.getInt(ANIM_START_DELAY_KEY)
            mAnimDuration =
                bundle.getInt(ANIM_DURATION_KEY)
            mDescTopSpaceDecrementer =
                bundle.getFloat(DESC_TOP_SPACE_DECREMENTER_KEY)
            mDescTopSpaceIncrementer = bundle.getFloat(DESC_TOP_SPACE_INCREMENTER_KEY)
            mDescriptionLinesSpacing =
                bundle.getFloat(DESCRIPTION_LINE_SPACING_KEY)
            setDescriptionTopSpaceIncrementer(mDescTopSpaceIncrementer) // call requestLayout
            mBackgroundColor = bundle.getInt(BACKGROUND_COLOR_KEY)
            mForegroundColor =
                bundle.getInt(FOREGROUND_COLOR_KEY)
            *//*mStateNumberBackgroundColor =
                bundle.getInt(STATE_NUMBER_BACKGROUND_COLOR_KEY)
            mStateNumberForegroundColor =
                bundle.getInt(STATE_NUMBER_FOREGROUND_COLOR_KEY)
            mCurrentStateDescriptionColor =
                bundle.getInt(CURRENT_STATE_DESC_COLOR_KEY)*//*
            mStateDescriptionColor =
                bundle.getInt(STATE_DESC_COLOR_KEY)
            mJustifyMultilineDescription =
                bundle.getBoolean(JUSTIFY_MULTILINE_DESC_KEY)

            initializePainters()

            checkStateCompleted(bundle.getBoolean(CHECK_STATE_COMPLETED_KEY)) // call invalidate
            setAllStatesCompleted(bundle.getBoolean(ENABLE_ALL_STATES_COMPLETED_KEY))
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }*/

    private companion object {
        private const val MIN_STATE_NUMBER = 1
        private const val MAX_STATE_NUMBER = 5

        private const val STATE_SIZE_KEY = "mStateSize"
        private const val STATE_LINE_THICKNESS_KEY = "mStateLineThickness"
        private const val STATE_NUMBER_TEXT_SIZE_KEY = "mStateNumberTextSize"
        private const val STATE_DESCRIPTION_SIZE_KEY = "mStateDescriptionSize"

        private const val MAX_STATE_NUMBER_KEY = "mMaxStateNumber"
        private const val CURRENT_STATE_NUMBER_KEY = "mCurrentStateNumber"

        private const val ANIM_START_DELAY_KEY = "mAnimStartDelay"
        private const val ANIM_DURATION_KEY = "mAnimDuration"

        private const val DESC_TOP_SPACE_DECREMENTER_KEY = "mDescTopSpaceDecrementer"
        private const val DESC_TOP_SPACE_INCREMENTER_KEY = "mDescTopSpaceIncrementer"

        private const val BACKGROUND_COLOR_KEY = "mBackgroundColor"
        private const val FOREGROUND_COLOR_KEY = "mForegroundColor"
        private const val STATE_NUMBER_BACKGROUND_COLOR_KEY = "mStateNumberBackgroundColor"
        private const val STATE_NUMBER_FOREGROUND_COLOR_KEY = "mStateNumberForegroundColor"

        private const val CURRENT_STATE_DESC_COLOR_KEY = "mCurrentStateDescriptionColor"
        private const val STATE_DESC_COLOR_KEY = "mStateDescriptionColor"

        private const val CHECK_STATE_COMPLETED_KEY = "mCheckStateCompleted"

        private const val ENABLE_ALL_STATES_COMPLETED_KEY = "mEnableAllStatesCompleted"

        private const val JUSTIFY_MULTILINE_DESC_KEY = "mJustifyMultilineDescription"

        private const val DESCRIPTION_LINE_SPACING_KEY = "mDescriptionLinesSpacing"

        private const val END_CENTER_X_KEY = "mEndCenterX"
        private const val START_CENTER_X_KEY = "mStartCenterX"
        private const val ANIM_START_X_POS_KEY = "mAnimStartXPos"
        private const val ANIM_END_X_POS_KEY = "mAnimEndXPos"
        private const val IS_CURRENT_ANIM_STARTED_KEY = "mIsCurrentAnimStarted"
        private const val ANIMATE_TO_CURRENT_PROGRESS_STATE_KEY = "mAnimateToCurrentProgressState"
        private const val IS_STATE_NUMBER_DESCENDING_KEY = "mIsStateNumberDescending"
        private const val INSTANCE_STATE = "saved_instance"

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
        //private var mStateNumberForegroundPaint: Paint? = null
        private var mStateCheckedForegroundPaint: Paint? = null
        //private var mStateNumberBackgroundPaint: Paint? = null
        private var mBackgroundPaint: Paint? = null
        private var mForegroundPaint: Paint? = null
        //private var mCurrentStateDescriptionPaint: Paint? = null
        private var mStateDescriptionPaint: Paint? = null

        private var mBackgroundColor = 0
        private var mForegroundColor = 0
        /*private var mStateNumberBackgroundColor = 0
        private var mStateNumberForegroundColor = 0
        private var mCurrentStateDescriptionColor = 0*/
        private var mStateDescriptionColor = 0
        private var mStateTxtAColor = 0

        /**
         * animate inner line to current progress state
         *//*
        private var mAnimator: Animator? = null
*/
        /**
         * tracks progress of line animator
         */
        private var mAnimStartXPos = 0f
        private var mAnimEndXPos = 0f

        private var mIsCurrentAnimStarted = false

        /**
         * 5 4 3 2 1  (RTL Support)
         */
        private var mIsStateNumberDescending = false


       // private var mStateItemClickedNumber = 0
        private var EMPTY_SPACE_DESCRIPTOR = ""
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
        //private var mOnStateItemClickListener: OnStateItemClickListener? = null
    }

    /*interface OnStateItemClickListener {
        fun onStateItemClick(
            stateProgressBar: StateProgressView?,
            stateItem: StateItem?,
            stateNumber: Int,
            isCurrentState: Boolean
        )
    }*/
}