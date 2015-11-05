package net.yazeed44.speedmonitor.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.kristijandraca.backgroundmaillibrary.BackgroundMail;
import com.kristijandraca.backgroundmaillibrary.EmailSendListener;
import com.kristijandraca.backgroundmaillibrary.Utils;

import net.yazeed44.speedmonitor.R;
import net.yazeed44.speedmonitor.model.Monitor;
import net.yazeed44.speedmonitor.model.Report;
import net.yazeed44.speedmonitor.model.ReportEmail;
import net.yazeed44.speedmonitor.util.PrivateInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by yazeed44 on 11/2/15.
 */
public class SendReportService extends BroadcastReceiver {
    private static final String TAG = SendReportService.class.getSimpleName();

    private File createFileForReport(final String reportText) {

        final File reportFile = new File(Environment.getExternalStorageDirectory() + "/report_"+Math.random() +".txt");

        try {
            reportFile.createNewFile();
            final FileOutputStream writer = new FileOutputStream(reportFile);
            writer.write(reportText.getBytes());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }

        return reportFile;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        final Realm db = Realm.getInstance(context);

        final RealmResults<ReportEmail> reportEmails = db.allObjects(ReportEmail.class);

        Log.d(TAG,reportEmails.toString());

        if (reportEmails.isEmpty()){
            db.close();
            return;
        }

        if (!Utils.isNetworkAvailable(context)){
            Toast.makeText(context, R.string.no_network_to_send_report,Toast.LENGTH_LONG).show();
            db.close();
            return;
        }

        final BackgroundMail bm = new BackgroundMail(context);
        bm.setGmailUserName(PrivateInfo.EMAIL);
        bm.setGmailPassword(PrivateInfo.PASSWORD);
        bm.setProcessVisibility(false);
        bm.sendInBackground(true);
        bm.setListener(new EmailSendListener() {
            @Override
            public void onSuccessfullySend() {
                final Handler mainHandler = new Handler(context.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.report_sended, Toast.LENGTH_SHORT).show();
                    }
                });

                final Realm realm = Realm.getInstance(context);
                realm.beginTransaction();
                realm.clear(ReportEmail.class);
                realm.commitTransaction();
                realm.close();
            }

            @Override
            public void failedToSend(Exception exception) {

            }
        });
        bm.setFormSubject("Speed report");
        bm.setFormBody("Body");


        for (final ReportEmail reportEmail: reportEmails){

            bm.setAttachment(createFileForReport(reportEmail.getReportText()).getAbsolutePath());

        }

        final RealmResults<Monitor> monitors = db.allObjects(Monitor.class);

        String emails = "";
        for (final Monitor monitor: monitors){

            emails += monitor.getEmail() + ",";
        }

        bm.setMailTo(emails);

        bm.send();

        //If sent successfully then remove the email from db

        db.close();
    }
}
