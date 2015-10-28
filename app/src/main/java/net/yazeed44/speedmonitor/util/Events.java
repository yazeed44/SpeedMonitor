package net.yazeed44.speedmonitor.util;

import net.yazeed44.speedmonitor.model.Report;

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

    public final static class PostReportEvent {
        public final Report report;
        public PostReportEvent(final Report report) {
            this.report = report;
        }
    }
}
