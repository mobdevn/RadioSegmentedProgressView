package com.android.radiosegmentedprogressview.stateprogress

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.android.radiosegmentedprogressview.R

class SubwayProgressCustomView(context: Context, attrs: AttributeSet ): LinearLayout(context, attrs) {

    private val customTitle: TextView
    private val spb: SurveyProgressBar = SurveyProgressBar(context, attrs)
    init {
        orientation = VERTICAL
        val view = inflate(context, R.layout.subway_progress_customview, this)
        customTitle = view.findViewById<TextView>(R.id.customview_text_title)

        context.obtainStyledAttributes(attrs, R.styleable.StateProgressBar).let {

            title = it.getString(R.styleable.StateProgressBar_spb_title).orEmpty()

            it.recycle()
        }
    }

    private var title: String = ""
        set(value) {
            field = value
            customTitle.text = field
        }

    public fun setCustomViewTitle(text: String) {
        customTitle.text = text
    }

    public fun setStateSubTextData(amtList: List<String>) {
        spb.setStateDescriptionData(amtList)
        invalidate()
    }

    public fun setStateTextValueData(desc: List<String>) {
        spb.setStateTextRowOneData(desc)
        invalidate()
    }

    public fun setSubwayProgressPoints(points: Int) {
        spb.setMaxStateNumber(points)
        invalidate()
    }

    public fun setProgressCurrentCompletedState(position: Int) {
        spb.setCurrentStateNumber(position)
        invalidate()
    }

    public fun setStateTextValueColor(color: Int) {
        spb.setStateTextValueColor(color)
        invalidate()
    }

    public fun setStateSubTextColor(color: Int) {
        spb.setStateSubtextColor(color)
        invalidate()
    }

    public fun setProgressbarSelectedColor(color: Int) {
        spb.setForegroundColor(color)
        invalidate()
    }
}