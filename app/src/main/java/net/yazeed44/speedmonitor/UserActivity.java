package net.yazeed44.speedmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

public class UserActivity extends AppCompatActivity {

    //User activity aka Main Activity

    private SpeedometerGauge speedView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        speedView = (SpeedometerGauge) findViewById(R.id.speed_text);

        speedView.setMaxSpeed(110);



    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public void onClickLaunchService(View view) {
        startService(new Intent(this, MonitorService.class));
    }

    public void onEvent(final Events.NewSpeedCapturedEvent newSpeedCapturedEvent){

        final String text = newSpeedCapturedEvent.speed + "  km/h";
        speedView.setSpeed(newSpeedCapturedEvent.speed,true);
    }
}
