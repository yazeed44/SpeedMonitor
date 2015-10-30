package net.yazeed44.speedmonitor.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import net.yazeed44.speedmonitor.R;
import net.yazeed44.speedmonitor.util.Events;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by yazeed44 on 10/29/15.
 */
public class ChartFragment extends Fragment {

    private LineChart mLineChart;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_chart,container,false);
        mLineChart = (LineChart) layout.findViewById(R.id.chart);

        setupLimit();



        return layout;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().registerSticky(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void setupLimit() {

        final LimitLine speedLimitLine = new LimitLine(130f, getResources().getString(R.string.speed_limit_line));
        speedLimitLine.setLineWidth(4f);
        speedLimitLine.enableDashedLine(10f, 10f, 0f);
        speedLimitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        speedLimitLine.setTextSize(10f);

        mLineChart.getAxisLeft().addLimitLine(speedLimitLine);
    }

    public void onEvent(final Events.PostReportEvent reportEvent){
        mLineChart.clear();
        final ArrayList<String> xVals = new ArrayList<String>();

        final ArrayList<Entry> speedEntries = new ArrayList<>();

        for (int i = 0; i < reportEvent.report.getSpeedRecords().size(); i++) {
            final int speed = reportEvent.report.getSpeedRecords().get(i);
            speedEntries.add(new Entry(speed, 0));
            xVals.add(String.valueOf(i));


        }
        final LineDataSet speedDataSet = new LineDataSet(speedEntries,"Speed");
        speedDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        final ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(speedDataSet);

        ;
        final LineData speedData = new LineData(xVals,dataSets);
        mLineChart.setData(speedData);
        mLineChart.invalidate();
    }
}
