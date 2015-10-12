package net.yazeed44.speedmonitor;

/**
 * Created by yazeed44 on 10/3/15.
 */
public final class Events {

    public final static class NewSpeedCapturedEvent{
        public final double speed;

        public NewSpeedCapturedEvent(final double speed){
            this.speed = speed;
        }
    }
}
