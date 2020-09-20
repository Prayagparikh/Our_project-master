package com.development.apptest;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    PieChart pieChart;
    int pos;
    int neg;
    int neu;
    int tot;
    ArrayList<Float> yData = new ArrayList<>();
    String[] xData ;


    public void startAnimation() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim);
        pieChart.startAnimation(animation);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        yData.clear();

        xData = new String[3];
        pos = Integer.parseInt(getIntent().getStringExtra("pos"));
        neg = Integer.parseInt(getIntent().getStringExtra("neg"));
        neu = Integer.parseInt(getIntent().getStringExtra("neu"));;
        tot = Integer.parseInt(getIntent().getStringExtra("tot"));


        yData.add((float)(pos * 100/tot) );
        yData.add((float)(neg* 100/tot));
        yData.add((float)(neu* 100/tot));

        xData[0] = "% Positive" ;
        xData[1] = "% Negative" ;
        xData[2] = "% Neutral" ;

        Toast.makeText(getApplicationContext(),"Total Positive Comments: "+ pos+"\n"+"Total Negative Comments: "+ neg+"\n"+"Total Neutral Comments: "+ neu+"\n"+"Total Comments: "+ tot,Toast.LENGTH_SHORT).show();
        System.out.println(yData);
        //sentimentGeneration();
        //pieChartGenerate();
        setupPieChart(xData, yData);
    }

    private void setupPieChart(String[] xData, ArrayList<Float> yData) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for(int i=0;i <yData.size();i++){
            pieEntries.add(new PieEntry(yData.get(i), xData[i]));

        }

        PieDataSet dataSet = new PieDataSet(pieEntries,"");ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        dataSet.setColors(colors);

        dataSet.setSliceSpace(2);
        dataSet.setValueTextSize(25);
        PieData data = new PieData(dataSet);


        pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.getDescription().setText("Analysis of All Comments(in Percentage(%))");
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.animateXY(600, 720);
        pieChart.setData(data);
        pieChart.invalidate();
        Legend l = pieChart.getLegend();

        l.setTextSize(15f);
        l.setTextColor(Color.BLACK);
        l.setForm(Legend.LegendForm.CIRCLE);


    }

    /*private void sentimentGeneration() {

        MonkeyLearn ml = new MonkeyLearn("851f50eeb6a552b4a10d91a8251cc682209edfac");

        // Use the keyword extractor
        String[] textList = new String[2];
        textList[0] = "I love the movie";
        textList[1] = "Nice movie";
        ExtraParam[] extraParams = {new ExtraParam("max_keywords", "30")};
        MonkeyLearnResponse res = null;
        try {
            res = ml.extractors.extract("cl_pi3C7JiL", textList, extraParams);
        } catch (MonkeyLearnException e) {
            e.printStackTrace();
        }
        System.out.println( res.arrayResult );
    }*/

    private void pieChartGenerate() {
        //Log.d(TAG, "onCreate: starting to create chart");
        //pieChart = (PieChart) findViewById(R.id.pieChart);
        pieChart.getDescription().setText("Analysis of All Comments(in Percentage(%))");
        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        //pieChart.setDrawEntryLabels(true);

        addDataset(pieChart);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /*Log.d(TAG, "onValueSelected: Value selected form chart: ");
                Log.d(TAG, "onValueSelected:" + e.toString());
                Log.d(TAG, "onValueSelected:" + h.toString());*/

                int pos1 = e.toString().indexOf("(sum): ");
                String ness = e.toString().substring(pos1 + 17);
                //Toast.makeText(MainActivity.this, "Person "+" has\n" + "scurrility: " + ness + "%", Toast.LENGTH_SHORT).show();

                for(int i=0; i<yData.size(); i++) {
                    if(yData.get(i) == Float.parseFloat(ness)){
                        pos1 = i;
                        break;
                    }
                }
                String human = xData[pos1];
                Toast.makeText(ChartActivity.this, "Total "+ human + " Comments\n" + "in Pecentage: " + ness + "%", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }
    private void addDataset(PieChart chart) {
        //Log.d(TAG, "addDataset Started");
        ArrayList<PieEntry> yEntry = new ArrayList<>();
        ArrayList<String> xEntry = new ArrayList<> ();

        for(int i=0;i < yData.size(); i++) {
            yEntry.add(new PieEntry(yData.get(i), i));
        }

        for(int i=1; i< xData.length; i++) {
            xEntry.add(xData[i]);
        }

        //create the dataset
        PieDataSet pieDataSet = new PieDataSet(yEntry, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        pieDataSet.setColors(colors);

        //add legend to chart

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        //legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }
}
