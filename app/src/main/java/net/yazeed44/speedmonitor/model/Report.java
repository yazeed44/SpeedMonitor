package net.yazeed44.speedmonitor.model;

import android.location.Location;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/10/15.
 */
public class Report {

    public static final int DUMMY_SPEED_LIMIT = 110;

    public static final int DELAY_BETWEEN_TICKETS_SECONDS = 30;


    private final SparseArray<SpeedEntry> mSpeedRecords = new SparseArray<>();



    //Pause between ticket. if i get a ticket i won't get ticket for another 30 seconds
    private boolean mTicketPause = false;

    private long mTimeStarted;
    private long mTimeEnded;

    //Assuming there's no 0 speed here


    public void recordSpeed(final int speedInKmPerHour,final Location location){
        if (mSpeedRecords.size() == 0){
            mTimeStarted = location.getTime();
        }
        mSpeedRecords.append(mSpeedRecords.size(), new SpeedEntry(speedInKmPerHour,location));
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
            final int speed = mSpeedRecords.get(i).speed;
            sum += speed;
        }

        return sum;
    }

    public SparseArray<SpeedEntry> getSpeedRecords(){
        return mSpeedRecords;
    }

    public int getLastSpeed(){
        return mSpeedRecords.get(mSpeedRecords.size()-1).speed;
    }

    public SpeedEntry getLastSpeedRecord(){
        return mSpeedRecords.get(mSpeedRecords.size() - 1);
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

    public SparseArray<SpeedEntry> getTickets(){
        final SparseArray<SpeedEntry> tickets = new SparseArray<>();
        for (int i = 0; i < mSpeedRecords.size(); i++) {
            final SpeedEntry record = mSpeedRecords.get(i);
            if (record.isTicket()){
                tickets.append(i,record);
            }
        }
        return tickets;
    }

    public long getReportStartingDate(){
        return mTimeStarted;
    }

    public String generateReportText() {
        final StringBuilder reportText = new StringBuilder("Report Started at  " + getDateFormattedForReport(mTimeStarted));



        final String seperator = "==============";
        final SparseArray<SpeedEntry> tickets = getTickets();
        for (int i = 0; i < tickets.size(); i++) {
            final SpeedEntry ticket = tickets.get(i);
            reportText.append("Ticket - Speed: ")
                    .append(ticket.speed)
                    .append(",  time: ")
                    .append(getDateFormattedForTicket(ticket.location.getTime()))
                    .append("\n" + seperator + "\n");
        }

        reportText.append("\n")
                .append("\n")
                .append("Report ended at ")
                .append(getDateFormattedForReport(mTimeEnded));

        return reportText.toString();
    }

    private String getDateFormattedForReport(final long time) {

        final Date now = new Date();
        now.setTime(time);
        final DateFormat simpleDateFormat = SimpleDateFormat.getInstance();
        return simpleDateFormat.format(now);

    }

    private String getDateFormattedForTicket(final long time){

        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        //Example: Day: 25, 3:52:11

        return "Day: " + calendar.get(Calendar.DAY_OF_MONTH) +", " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) +":"+ calendar.get(Calendar.SECOND);

    }

    public void endReport(){
        mTimeEnded = getLastSpeedRecord().location.getTime();
    }
}
