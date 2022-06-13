package com.android.radiosegmentedprogressview.stateprogress

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.android.radiosegmentedprogressview.R


/**
 * @Intro
 * This is the helper class for Subway Point Progress bar(SPB).
 *
 * @Feature
 * This class init all xml attributes of the SPB and all Params that are mainly used
 * to show the SPB
 * */
class SurveyProgressBarUtil(context: Context, attrs: AttributeSet?) {
    /**
     * Subway point(circle) size defined as default value
     * */
    var mStateRadius = STATE_RADIUS_DEFAULT_VALUE

    /**
     * Subway point(circle) size
     * */
    var mStateSize = STATE_SIZE_DEFAULT_VALUE

    /**
     * Define each line thickness from Point-Point
     * */
    var mStateLineThickness = LINE_THICKNESS_DEFAULT_VALUE

    /**
     * Defines Main-Text text size
     * */
    var mStateSubtextSize = DESC_TEXT_SIZE_DEFAULT_VALUE

    /**
     * Defines Sub-Text text size
     * */
    var mStateTextValueSize = VALUE_TEXT_SIZE_DEFAULT_VALUE

    /**
     * Maximum number of subway points
     * */
    var mMaxStateNumber = MAX_SUBWAY_POINTS_DEFAULT_VALUE

    /**
     * Current state-selected subway point
     * */
    var mCurrentStateNumber = CURRENT_SUBWAY_POINT_POSITION_DEFAULT_VALUE

    var mSpacing = DEFAULT_VALUE_FLOAT_ZERO

    var mDescTopSpaceDecrementer = DEFAULT_VALUE_FLOAT_ZERO
    var mDescTopSpaceIncrementer = DEFAULT_TOP_SPACE_INCREMENTER

    /**
     * Paints for drawing
     */
    var mBackgroundPaint: Paint? = null
    var mForegroundPaint: Paint? = null
    var mStateTextValuePaint: Paint? = null
    var mStateSubtextPaint: Paint? = null
    var mDefaultTypefaceBold: Typeface? = null
    var mDefaultTypefaceNormal: Typeface? = null

    var mBackgroundColor = DEFAULT_VALUE_ZERO
    var mForegroundColor = DEFAULT_VALUE_ZERO
    var mStateTextValueColor = DEFAULT_VALUE_ZERO
    var mStateSubtextColor = DEFAULT_VALUE_ZERO

    var mMaxDescriptionLine = DEFAULT_VALUE_ZERO
    var mDescriptionLinesSpacing = DEFAULT_VALUE_FLOAT_ZERO
    var STATE_DESCRIPTION_LINE_SEPARATOR = "\n"

    var mIsDescriptionMultiline = false
    var mJustifyMultilineDescription = false
    var mEnableAllStatesCompleted = false
    var mCheckStateCompleted = false

    init {
        attrs.let {
            val a = context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar, 0, 0)

            mBackgroundColor = a.getColor(
                R.styleable.StateProgressBar_spb_stateBackgroundColor,
                mBackgroundColor
            )

            mForegroundColor =
                a.getColor(
                    R.styleable.StateProgressBar_spb_stateForegroundColor,
                    mForegroundColor
                )

            mStateSize = a.getDimension(
                R.styleable.StateProgressBar_spb_stateSize,
                mStateSize
            )

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

            mMaxStateNumber = a.getInteger(
                R.styleable.StateProgressBar_spb_maxSubwayPoints,
                mMaxStateNumber
            )

            mCurrentStateNumber = a.getInteger(
                R.styleable.StateProgressBar_spb_currentSubwayPosition,
                mCurrentStateNumber
            )

            mDescriptionLinesSpacing = a.getDimension(
                R.styleable.StateProgressBar_spb_descriptionLinesSpacing,
                mDescriptionLinesSpacing
            )

            //resolveStateSize(context)
            validateLineThickness(mStateLineThickness)
            validateStateNumber(mCurrentStateNumber)

            a.recycle()
        }

        setupMetrics(context)
        setupColor(context)
    }

    /**
     * Part of init method
     * */
    private fun setupColor(context: Context) {
        mBackgroundColor = ContextCompat.getColor(context, R.color.background_color)
        mForegroundColor = ContextCompat.getColor(context, R.color.foreground_color)

        mStateTextValueColor = ContextCompat.getColor(context, R.color.background_text_color)
        mStateSubtextColor = ContextCompat.getColor(context, R.color.black)
    }

    /**
     * Initialise default values of the SPB
     * */
    private fun setupMetrics(context: Context) {
        mStateTextValueSize = context.dp(mStateTextValueSize)
        mStateSubtextSize = context.sp(mStateSubtextSize)
        mStateLineThickness = context.dp(mStateLineThickness)
        mSpacing = context.dp(mSpacing)
        mDefaultTypefaceBold = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        mDefaultTypefaceNormal = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
    }

    /**
     * This method checks and maintains the thickness of the line at desired thickness on refresh
     * of the canvas
     * */
    fun validateLineThickness(lineThickness: Float) {
        val halvedStateSize = mStateSize / 2
        if (lineThickness > halvedStateSize) {
            mStateLineThickness = halvedStateSize
        }
    }

    private fun resolveStateSize(context: Context) {
        if (mStateSize != 0.0f) {
            mStateSize = context.dp(mStateSize)
        }
    }

    private fun validateStateNumber(stateNumber: Int) {
        check(stateNumber <= mMaxStateNumber) { "State number ($stateNumber) cannot be greater than total number of states $mMaxStateNumber" }
    }

    /**
     * Convert dp to pixels - For text
     * */
    fun Context.dp(dpVal: Float): Float = (dpVal * resources.displayMetrics.density)

    /**
     * Convert sp to pixels - For text
     * */
    fun Context.sp(spVal: Float): Float = (spVal * resources.displayMetrics.scaledDensity)

    private companion object {
        const val STATE_SIZE_DEFAULT_VALUE = 5.0f
        const val STATE_RADIUS_DEFAULT_VALUE = 24.0f
        const val LINE_THICKNESS_DEFAULT_VALUE = 15.0f
        const val DESC_TEXT_SIZE_DEFAULT_VALUE = 13.0f
        const val VALUE_TEXT_SIZE_DEFAULT_VALUE = 15.0f
        const val MAX_SUBWAY_POINTS_DEFAULT_VALUE = 4
        const val CURRENT_SUBWAY_POINT_POSITION_DEFAULT_VALUE = 0
        const val DEFAULT_VALUE_ZERO = 0
        const val DEFAULT_VALUE_FLOAT_ZERO = 0.0f
        const val DEFAULT_TOP_SPACE_INCREMENTER = 80.0f
    }
}