package net.yazeed44.speedmonitor.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.yazeed44.speedmonitor.R;
import net.yazeed44.speedmonitor.model.Report;
import net.yazeed44.speedmonitor.util.Events;

import de.greenrobot.event.EventBus;

/**
 * Created by yazeed44 on 10/24/15.
 */
public class SpeedGaugeFragment extends Fragment {


    private SpeedometerGauge mSpeedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragment = inflater.inflate(R.layout.fragment_speed_gauge,container,false);
        mSpeedView = (SpeedometerGauge)fragment.findViewById(R.id.speed_gauge);
        initSpeedView();

        return fragment;
    }

    private void initSpeedView() {
        final int maxSpeed = 240;
        mSpeedView.setMaxSpeed(maxSpeed);
        mSpeedView.setMajorTickStep(30);
        mSpeedView.setMinorTicks(2);
        mSpeedView.addColoredRange(Report.DUMMY_SPEED_LIMIT, maxSpeed, Color.RED);

        mSpeedView.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onEvent(final Events.NewSpeedCapturedEvent event){

        mSpeedView.setSpeed(event.speed,true);
    }
}
