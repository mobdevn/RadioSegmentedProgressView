package com.android.radiosegmentedprogressview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.radiosegmentedprogressview.stateprogress.SurveyProgressBar
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    /*private final String[] views = {"View 1", "View 2", "View 3", "View 4", "View 5", "View 6",
            "View 7", "View 8", "View 9", "View 10", "View 11", "View 12"};*/

    private val amtList: MutableList<String> = arrayListOf("$65k", "$65k", "$65k", "$65k", "$65k")
    private val descList: MutableList<String> = arrayListOf("05/07/2022", "05/07/2022", "05/07/2022", "05/07/2022", "05/07/2022")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*StateProgressBar stateProgressBar = (StateProgressBar) findViewById(R.id.your_state_progress_bar_id);
    stateProgressBar.setStateDescriptionData(descriptionData);*/
        val stateProgressBar =  findViewById<SurveyProgressBar>(R.id.progressIndicatorViewLayout)
        stateProgressBar.setStateTextRowOneData(descList as ArrayList<String>)
        stateProgressBar.setStateDescriptionData(amtList as ArrayList<String>)
//mStateTextAData
        stateProgressBar.setStateSize(5f)
        stateProgressBar.setMaxStateNumber(4)
        //stateProgressBar.setAllStatesCompleted(true)
        stateProgressBar.setCurrentStateNumber(2)

        stateProgressBar.setStateSubtextColor(getColor(androidx.constraintlayout.widget.R.color.material_blue_grey_800))
        stateProgressBar.setStateTextValueColor(getColor(R.color.black))

        stateProgressBar.setForegroundColor(getColor(R.color.purple_700))

        //stateProgressBar.setAllStatesCompleted(true)
        //stateProgressBar.setStateNumberTextSize(1f)
        stateProgressBar.setStateLineThickness(3.3f)


        stateProgressBar.setDescriptionTopSpaceIncrementer(75f)
        stateProgressBar.setStateTextValueSize(12f)
        stateProgressBar.setStateSubtextSize(10f)

        /*stateProgressBar.setCurrentStateDescriptionColor(this.getColor(this, R.color.description_foreground_color));
        stateProgressBar.setStateDescriptionColor(this.getColor(this,  R.color.description_background_color));
*/
        /*stateProgressBar.setStateDescriptionTypeface("fonts/RobotoSlab-Light.ttf")
        stateProgressBar.setStateNumberTypeface("fonts/Questrial-Regular.ttf")*/

        /*stateProgressBar.setMaxDescriptionLine(1)
        stateProgressBar.setJustifyMultilineDescription(false)
        stateProgressBar.setDescriptionLinesSpacing(20f)*/

        //stateProgressBar.setStateNumberIsDescending(false)

       /* val progressIndicatorView =
            findViewById<ProgressIndicatorView>(R.id.progressIndicatorViewLayout)

        //progressIndicatorView.setCompletedPosition(i % (amtList.size))
        progressIndicatorView.setLabelsList(amtList, descList)
        progressIndicatorView.setBarIndicatorColor(getResources().getColor(R.color.black))
        progressIndicatorView.setProgressIndicatorColor(getResources().getColor(R.color.purple_200))
        progressIndicatorView.setAmtLabelIndicatorColor(getResources().getColor(R.color.teal_200))
        progressIndicatorView.setDescIndicatorColor(getResources().getColor(R.color.teal_700))
        //progressIndicatorView.setViewCount(amtList.size)
        progressIndicatorView.drawView()*/
    }

    /*inner class ClassViewAdapter(context: Context, resource: Int) :
        ArrayAdapter<String>(context, resource) {


        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return super.getView(position, convertView, parent)
        }

        inner class AdapterViewHolder {

            constructor(view: View) {

            }
        }
    }*/
    /*public static class MyAdapter extends ArrayAdapter<String> {

        private final String[] labels = {"Step 1", "Step 2", "Step 3", "Step 4", "Step 5"};

        public MyAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.mLabel.setText(getItem(position));

            holder.mStepsView.setCompletedPosition(position % labels.length)
                    .setLabels(labels)
                    .setBarColorIndicator(
                            getContext().getResources().getColor(R.color.material_blue_grey_800))
                    .setProgressColorIndicator(getContext().getResources().getColor(R.color.orange))
                    .setLabelColorIndicator(getContext().getResources().getColor(R.color.orange))
                    .drawView();

            return convertView;
        }

        static class ViewHolder {

            TextView mLabel;
            StepsView mStepsView;

            public ViewHolder(View view) {
                mLabel = (TextView) view.findViewById(R.id.label);
                mStepsView = (StepsView) view.findViewById(R.id.stepsView);
            }
        }
    }*/
}