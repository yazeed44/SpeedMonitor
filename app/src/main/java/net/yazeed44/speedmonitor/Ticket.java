package net.yazeed44.speedmonitor;

import android.location.Location;

/**
 * Created by Yazeed Ahmed Almuqwishi on 10/13/15.
 */
public class Ticket {

    public final Location location;
    public final int speed;//Speed in km/h

    public Ticket(final Location location,final int speed){

        this.location = location;
        this.speed = speed;

    }



}
