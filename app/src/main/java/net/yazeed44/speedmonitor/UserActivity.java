package net.yazeed44.speedmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

        speedView.setMaxSpeed(110);



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

        Log.d(TAG,"Monitors email " + realm.allObjects(Monitor.class));


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

    public void onClickLaunchService(View view) {
        startService(new Intent(this, MonitorService.class));
    }

    public void onEvent(final Events.NewSpeedCapturedEvent newSpeedCapturedEvent){

        final String text = newSpeedCapturedEvent.speed + "  km/h";
        speedView.setSpeed(newSpeedCapturedEvent.speed,true);
    }
}
