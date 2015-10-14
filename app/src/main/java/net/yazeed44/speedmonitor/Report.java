package net.yazeed44.speedmonitor;

import android.location.Location;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/10/15.
 */
public class Report {

    public static final int DUMMY_SPEED_LIMIT = 110;

    public static final int DELAY_BETWEEN_TICKETS_SECONDS = 30;


    private final SparseArray<Ticket> mTickets = new SparseArray<>();
    private final SparseIntArray mSpeedRecords = new SparseIntArray();


    //Pause between tickets, if i get a ticket i won't get ticket for another 30 seconds
    private boolean mTicketPause = false;




    //Assuming there's no 0 speed here


    public void recordSpeed(final int speedInKmPerHour,final Location location){

        mSpeedRecords.append(mSpeedRecords.size() - 1, speedInKmPerHour);

        if (doesLastSpeedDeserveTicket()){
            mTickets.append(mTickets.size()-1,new Ticket(location,speedInKmPerHour));
        }

    }

    public boolean isLastSpeedAboveLimit(){
        return getLastSpeed() > DUMMY_SPEED_LIMIT;
    }

    public boolean doesLastSpeedDeserveTicket(){
        return !mTicketPause && isLastSpeedAboveLimit();
    }

    public void resumeTickets(){
        mTicketPause = false;
    }

    public void pauseTickets(){
        mTicketPause = true;
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

    public void resumeTicketsAfter(final int delayInMiliMeters) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                resumeTickets();
            }
        }, delayInMiliMeters);


    }

    public void addNewTicketFromLastSpeed(final Location location) {

        mTickets.append(mTickets.size()-1,new Ticket(location,getLastSpeed()));
    }
}
