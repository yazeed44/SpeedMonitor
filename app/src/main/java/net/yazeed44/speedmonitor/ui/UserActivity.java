package net.yazeed44.speedmonitor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.kristijandraca.backgroundmaillibrary.BackgroundMail;

import net.yazeed44.speedmonitor.util.Events;
import net.yazeed44.speedmonitor.model.Monitor;
import net.yazeed44.speedmonitor.services.MonitorService;
import net.yazeed44.speedmonitor.util.PrivateInfo;
import net.yazeed44.speedmonitor.R;
import net.yazeed44.speedmonitor.model.Report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = UserActivity.class.getSimpleName();

    //User activity aka Main Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager pager = (ViewPager)findViewById(R.id.user_pager);
        final TabLayout tabLayout = (TabLayout)findViewById(R.id.user_tab);


        setupViewPager(pager, tabLayout);

    }

    private void setupViewPager(final ViewPager pager, final TabLayout tabLayout) {

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addNewFragment(new SpeedGaugeFragment(),getResources().getString(R.string.tab_gauge));
        adapter.addNewFragment(new ChartFragment(),getResources().getString(R.string.tab_chart));

        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_monitor_email, menu);
        getMenuInflater().inflate(R.menu.start_monitoring, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_add_monitor_email:
                askForMonitorEmail();
                break;

            case R.id.action_start_monitoring:
                onClickStartMonitoring(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void askForMonitorEmail() {

        new MaterialDialog.Builder(this)
                .content(R.string.content_ask_monitor_email)
                .inputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog materialDialog, CharSequence charSequence) {


                        //TODO Check if email is valid
                        Log.d(TAG,"Monitor email  " + charSequence);
                        addMonitorEmailToDb(charSequence);

                    }


                }).show();

    }

    private void addMonitorEmailToDb(final CharSequence charSequence) {

        final Realm realm = Realm.getInstance(this);

        realm.beginTransaction();

        final Monitor monitor = realm.createObject(Monitor.class);

        monitor.setEmail(String.valueOf(charSequence));

        realm.commitTransaction();

        Log.d(TAG, "Monitors email " + realm.allObjects(Monitor.class));


        realm.close();
    }

    public void onClickStartMonitoring(MenuItem menuItem) {


        if (menuItem.getTitle().equals(getResources().getString(R.string.action_start_monitoring))){

            startService(new Intent(this,MonitorService.class));
            menuItem.setTitle(R.string.action_send_report);
            Toast.makeText(this,R.string.toast_monitoring_started,Toast.LENGTH_LONG).show();
        }

        else {
            EventBus.getDefault().getStickyEvent(Events.PostReportEvent.class).report.endReport();
            stopService(new Intent(this,MonitorService.class));
            sendEmailToMonitors();
            menuItem.setTitle(R.string.action_start_monitoring);

        }


       }

    private void sendEmailToMonitors() {
        final Report report = EventBus.getDefault().getStickyEvent(Events.PostReportEvent.class).report;
        final Realm realm = Realm.getInstance(this);

        final RealmResults<Monitor> monitorQuery = realm.allObjects(Monitor.class);

        final Monitor[] monitors = new Monitor[monitorQuery.size()];

        final BackgroundMail bm = new BackgroundMail(this);
        bm.setGmailUserName(PrivateInfo.EMAIL);
        bm.setGmailPassword(PrivateInfo.PASSWORD);
        bm.setAttachment(createFileForReport(report).getAbsolutePath());
        bm.setFormSubject("Report - " + report.getReportStartingDate());
        bm.setFormBody("Body");


        String recipients = "";
        for (int i = 0; i < monitors.length; i++) {
            monitors[i] = monitorQuery.get(i);

            recipients +=   monitors[i].getEmail() + "," ;
        }
        bm.setMailTo(recipients);
        bm.send();
        realm.close();
    }

    private File createFileForReport(final Report report) {
        final File reportFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/report_" + report.getReportStartingDate()+".txt");

        try {
            reportFile.createNewFile();
            final FileOutputStream writer = new FileOutputStream(reportFile);
            writer.write(report.generateReportText().getBytes());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }


        return reportFile;
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void addNewFragment(final Fragment fragment, final String title){

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }
}
