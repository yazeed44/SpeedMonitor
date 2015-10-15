package net.yazeed44.speedmonitor;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();

    //User activity aka Main Activity

    private SpeedometerGauge speedView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        speedView = (SpeedometerGauge) findViewById(R.id.speed_text);

        initSpeedView();
    }

    private void initSpeedView() {
        final int maxSpeed = 240;
        speedView.setMaxSpeed(maxSpeed);
        speedView.setMajorTickStep(30);
        speedView.setMinorTicks(2);
        speedView.addColoredRange(Report.DUMMY_SPEED_LIMIT, maxSpeed, Color.RED);

        speedView.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_monitor_email, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add_monitor_email:
                askForMonitorEmail();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void askForMonitorEmail() {

        new MaterialDialog.Builder(this)
                .content(R.string.content_ask_monitor_email)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {


                        //TODO Check if email is valid
                        Log.d(TAG,"Monitor email  " + charSequence);
                        addMonitorEmailToDb(charSequence);

                    }


                }).show();

    }

    private void addMonitorEmailToDb(final CharSequence charSequence) {

        final Realm realm = Realm.getInstance(this);

        realm.beginTransaction();

        final Monitor monitor = realm.createObject(Monitor.class);

        monitor.setEmail(String.valueOf(charSequence));

        realm.commitTransaction();

        Log.d(TAG, "Monitors email " + realm.allObjects(Monitor.class));


        realm.close();
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

    public void onClickStartMonitoring(View view) {
        final Button startMonitoringBtn = (Button)view;

        if (startMonitoringBtn.getText().equals(getResources().getString(R.string.btn_start_monitoring))){

            startService(new Intent(this,MonitorService.class));
            startMonitoringBtn.setText(R.string.btn_send_report);
            Toast.makeText(this,R.string.toast_monitoring_started,Toast.LENGTH_LONG).show();
        }

        else {
            sendEmailToMonitors();
            startMonitoringBtn.setText(R.string.btn_start_monitoring);

        }


    }

    private void sendEmailToMonitors() {
        

    }

    public void onEvent(final Events.NewSpeedCapturedEvent newSpeedCapturedEvent){

        final String text = newSpeedCapturedEvent.speed + "  km/h";
        speedView.setSpeed(newSpeedCapturedEvent.speed,true);
    }
}
