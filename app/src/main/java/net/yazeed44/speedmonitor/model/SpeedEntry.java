package net.yazeed44.speedmonitor.model;

import android.location.Location;

/**
 * Created by yazeed44 on 10/30/15.
 */
public class SpeedEntry {

    public final int speed;
    public final Location location;

    public SpeedEntry(final int speed, final Location location) {
        this.speed = speed;
        this.location = location;
    }

    public boolean isTicket(){
        return speed > Report.DUMMY_SPEED_LIMIT;
    }
}
