package net.yazeed44.speedmonitor;

import android.location.Location;
import android.util.SparseIntArray;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/10/15.
 */
public class Report {

    private final SparseIntArray mSpeedRecords = new SparseIntArray();

    //Assuming there's no 0 speed here


    public void recordSpeed(final int speedInKmPerHour){

        mSpeedRecords.append(mSpeedRecords.size() - 1, speedInKmPerHour);
    }

    public double getAverageSpeed(){
        return (double)getSum()/mSpeedRecords.size();
    }

    private int getSum(){
        int sum = 0;
        for(int i = 0; i < mSpeedRecords.size();i++){
            final int speed = mSpeedRecords.get(i);
            sum += speed;
        }

        return sum;
    }

    public int getLastSpeed(){
        return mSpeedRecords.get(mSpeedRecords.size()-1);
    }
}
