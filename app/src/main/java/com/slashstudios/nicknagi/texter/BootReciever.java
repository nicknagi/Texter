package com.slashstudios.nicknagi.texter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nicknagi on 9/15/2015.
 */
public class BootReciever extends BroadcastReceiver {

    private static String TAG = "Inchoo.net tutorial";
    SharedPreferences EventID;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Booting Completed", Toast.LENGTH_LONG).show();

        EventID = PreferenceManager.getDefaultSharedPreferences(context);

        ContactDatabaseReference databaseReference = new ContactDatabaseReference();

        boolean foundNullFinishValueAtStartReference = false;
        Long NullEndValueReference = 1l, indexReference = 1l;
        while (!foundNullFinishValueAtStartReference) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, indexReference);
            indexReference++;
            if (databaseReference == null) {
                NullEndValueReference++;
                Log.d(TAG, "Stuck 1");
            } else {
                foundNullFinishValueAtStartReference = true;
            }
        }

        long databaseMinimumRangeReference, databaseMaximumRangeReference;

        databaseMinimumRangeReference = NullEndValueReference;
        databaseMaximumRangeReference = EventID.getLong("EVENTID", -1);

        int x;
        int AlarmID;
        Long EventID, time;
        for (x = (int) databaseMinimumRangeReference; x <= (int) databaseMaximumRangeReference; x++) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, (long) x);
            if (databaseReference == null) continue;
            else {
                AlarmID = databaseReference.Alarmid;
                EventID = databaseReference.Eventid;
                time = databaseReference.Time;

                Intent intentForAlarm = new Intent(context.getApplicationContext(), MessageSendingService.class);
                intentForAlarm.putExtra("EVENTID", EventID);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pintent = PendingIntent.getService(context.getApplicationContext(), AlarmID, intentForAlarm, 0);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, time, pintent);
            }
        }

        //Toast.makeText(context, "AlarmScheduling Complete", Toast.LENGTH_LONG).show();
    }
}
