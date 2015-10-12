package net.yazeed44.speedmonitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import de.greenrobot.event.EventBus;


/**
 * Created by yazeed44 on 10/1/15.
 */
public class MonitorService extends Service implements LocationListener {

    private static final int LOCATION_UPDATE_INTERVAL_MILLIS = 500;

    private static final String TAG = "service_test";
    private static final double DUMMY_SPEED_LIMIT = 110;

    private LocationManager mLocationManager;

    private final Report mReport = new Report();


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



        Log.d(TAG, location.toString());

        if (location.getSpeed() != 0){
            final int speed = (int) Math.round(convertMetersToKmPerHour(location.getSpeed()));

            EventBus.getDefault().post(new Events.NewSpeedCapturedEvent(speed));

            mReport.recordSpeed(speed);
        }







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
