package net.yazeed44.speedmonitor.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import net.yazeed44.speedmonitor.R;
import net.yazeed44.speedmonitor.util.Events;
import net.yazeed44.speedmonitor.model.Report;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;


/**
 * Created by yazeed44 on 10/1/15.
 */
public class MonitorService extends Service implements LocationListener {

    private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 500;

    private static final String TAG = "service_test";

    private LocationManager mLocationManager;

    private Report mReport = new Report();

    public static final ToneGenerator BEEP_GENERATOR = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,ToneGenerator.MAX_VOLUME);


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        Log.d(TAG, "Started monitor service.");

        final Criteria criteria = new Criteria();

        criteria.setSpeedRequired(true);

        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        final String bestProvider = mLocationManager.getBestProvider(criteria, false);

        mLocationManager.requestLocationUpdates(bestProvider, LOCATION_UPDATE_INTERVAL_MILLIS, 0, this);
    }


    @Override
    public void onLocationChanged(final Location location) {
        final int speed = (int) Math.round(convertMetersToKmPerHour(location.getSpeed()));
        if (mReport.getSpeedRecords().size() == 0){
            Toast.makeText(getApplicationContext(), R.string.toast_monitoring_started,Toast.LENGTH_SHORT).show();
        }
        mReport.recordSpeed(speed, location);
        Log.d(TAG, location.toString());


        if (location.getSpeed() != 0){
            if (mReport.doesLastSpeedDeserveTicket()) {
                //Notify user about it and beep sound
                playBeepSound();
                mReport.pauseTickets();
                mReport.resumeTicketsAfter(Report.DELAY_BETWEEN_TICKETS_SECONDS * 1000);

            }
        }
        EventBus.getDefault().post(new Events.NewSpeedCapturedEvent(speed));
        EventBus.getDefault().postSticky(new Events.PostReportEvent(mReport));

    }

    private void playBeepSound() {
        BEEP_GENERATOR.startTone(ToneGenerator.TONE_PROP_BEEP);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        mLocationManager.removeUpdates(this);

        //Report that the user closed the app or something
    }

    private double convertMetersToKmPerHour(final double metersPerSecond){
        return metersPerSecond * 3.6;
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
