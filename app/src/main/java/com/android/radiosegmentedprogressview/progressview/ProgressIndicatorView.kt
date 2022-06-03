package com.android.radiosegmentedprogressview.progressview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.radiosegmentedprogressview.R

class ProgressIndicatorView : ConstraintLayout, ProgressViewIndicator.OnDrawListener {

    constructor(context: Context) : super(context, null) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs, 0) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        // do nothing
    }

    init {
        val rootView = LayoutInflater.from(context).inflate(R.layout.progress_view_indicator, this)
        progressViewIndicator = rootView.findViewById(R.id.custom_progressViewIndicator)
        progressViewIndicator.setDrawListener(this)
        mAmountLabelsLayout = rootView.findViewById(R.id.labels_amount_container)
        mDescLabelsLayout = rootView.findViewById(R.id.labels_description_container)
    }


    fun getAmountLabels(): List<String> = mAmountLabelsList

    fun getDescLabels(): List<String> = mDescLabelsList

    fun getProgressIndicatorColor(): Int = mProgressColorIndicator

    fun getAmtLabelIndicatorColor(): Int = mAmtLabelColorIndicator

    fun getDescIndicatorColor(): Int = mDecLabelColorIndicator

    fun getBarIndicatorColor(): Int = mBarColorIndicator

    fun getCompletedPosition(): Int = mCompletedPosition

    fun setCompletedPosition(position: Int) {
        mCompletedPosition = position
        progressViewIndicator.setCompletedPosition(mCompletedPosition)
    }

    fun setBarIndicatorColor(color: Int) {
        mBarColorIndicator = color
        progressViewIndicator.setBarColor(mBarColorIndicator)
    }

    fun setProgressIndicatorColor(color: Int) {
        mProgressColorIndicator = color
        progressViewIndicator.setProgressColor(mProgressColorIndicator)
    }

    fun setAmtLabelIndicatorColor(color: Int) {
        mAmtLabelColorIndicator = color
    }

    fun setDescIndicatorColor(color: Int) {
        mDecLabelColorIndicator = color
    }


    fun setLabelsList(amtlist: List<String>, desclist: List<String>) {
        mAmountLabelsList.addAll(amtlist)
        mDescLabelsList.addAll(desclist)
    }

    fun drawView() {
        progressViewIndicator.invalidate()
        onReady()
    }

    fun setViewCount(count: Int) {
        progressViewIndicator.setStepSize(count)
    }

    override fun onReady() {
        drawLabels()
    }

    private fun drawLabels() {
        val indicatorPosition = progressViewIndicator.getThumbContainerXPosition()

        if (mAmountLabelsList.size != 0) {
            for (i in 0 until mAmountLabelsList.size - 1) {
                if (indicatorPosition.size == 0) {
                    mAmountLabelsLayout.addView(getTextView(mAmountLabelsList[i], 0.0f))
                } else {
                    //mAmountLabelsLayout.addView(getTextView(mAmountLabelsList[i], indicatorPosition[i]))
                    mAmountLabelsLayout.addView(getTextView(mAmountLabelsList[i], 0.0f))
                }
            }
        }

        if (mDescLabelsList.size != 0) {
            for (i in 0 until mDescLabelsList.size - 1) {
                if (indicatorPosition.size == 0) {
                    mDescLabelsLayout.addView(getTextView(mDescLabelsList[i], 0.0f))
                } else {
                    //mDescLabelsLayout.addView(getTextView(mDescLabelsList[i], indicatorPosition[i]))
                    mDescLabelsLayout.addView(getTextView(mDescLabelsList[i], 0.0f))
                }
            }
        }
    }

    private fun getTextView(text: String, position: Float): TextView {
        val textView = TextView(context)
        textView.setText(text)
        textView.setTextColor(mAmtLabelColorIndicator)
        textView.x = position
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return textView
    }


    private companion object {
        var mProgressColorIndicator = Color.YELLOW
        var mAmtLabelColorIndicator = Color.BLACK
        var mDecLabelColorIndicator = Color.GRAY
        var mBarColorIndicator = Color.GRAY
        var mCompletedPosition = 0

        val mAmountLabelsList = mutableListOf<String>()
        val mDescLabelsList = mutableListOf<String>()

        lateinit var progressViewIndicator: ProgressViewIndicator
        lateinit var mAmountLabelsLayout: FrameLayout
        lateinit var mDescLabelsLayout: FrameLayout
    }
}