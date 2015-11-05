package net.yazeed44.speedmonitor.model;

import io.realm.RealmObject;

/**
 * Created by yazeed44 on 11/2/15.
 */
public class ReportEmail extends RealmObject{
    private String reportText;


    public void setReportText(final String reportText){
        this.reportText = reportText;
    }

    public String getReportText(){
        return reportText;
    }
}
