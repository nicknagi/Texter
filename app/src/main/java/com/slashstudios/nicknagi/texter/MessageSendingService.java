package com.slashstudios.nicknagi.texter;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by nicknagi on 26-07-2015.
 */
public class MessageSendingService extends Service {

    private static String TAG = "Inchoo.net tutorial";
    SharedPreferences CurrentMainDatabaseIndex;
    SharedPreferences EventID;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SendSMSFromDatabase(intent);
        Log.d(TAG, "MessageService ended");
        stopSelf();

        return super.onStartCommand(intent, flags, startId);
    }

    public void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }


    public void SendSMSFromDatabase(Intent intent) {
        Log.d(TAG, "MessageService started 2");
        CurrentMainDatabaseIndex = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EventID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] numbers = new String[250];
        String[] names = new String[250];
        String message = "";

        final Long eventID = intent.getLongExtra("EVENTID", -1);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (eventID == -1) {
            Toast.makeText(getApplicationContext(), "App Error Occured Reinstall App", Toast.LENGTH_SHORT).show();
            Uri packageUri = Uri.parse("package:com.slashstudios.nicknagi.texter");
            Intent uninstallIntent =
                    new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(uninstallIntent);
        }

        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();

        long databaseMinimumRange, databaseMaximumRange, databaseMinimumRangeReference, databaseMaximumRangeReference;

        boolean foundNullFinishValueAtStart = false;
        Long NullEndValue = 1l, index = 1l;
        while (!foundNullFinishValueAtStart) {
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, index);
            index++;
            if (databaseMain == null) {
                NullEndValue++;
                Log.d(TAG, "Stuck 1");
            } else {
                foundNullFinishValueAtStart = true;

            }
        }
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);

        Log.d(TAG, "datbaseMax Range is in service " + databaseMaximumRange);
        Log.d(TAG, "datbaseMin Range is in service " + databaseMinimumRange);

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

        databaseMinimumRangeReference = NullEndValueReference;
        databaseMaximumRangeReference = EventID.getLong("EVENTID", -1);


        int i;
        Log.d(TAG, "Entering Loop");
        boolean foundEventID = false;

        for (i = (int) databaseMinimumRange; i <= (int) databaseMaximumRange; i++) {
            Log.d(TAG, "In the for loop");
            if (foundEventID == true) {
                break;
            }

            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, ((long) i));
            if (databaseMain == null) continue;

            if (databaseMain.Eventid == eventID) {
                int x = 0, rowNumber = i;
                boolean foundEnd = false;
                numbers[x] = databaseMain.Number;
                names[x] = databaseMain.Name;
                x++;
                UpdateDatabase((long) rowNumber, 1);
                foundEventID = true;

                while (!foundEnd) {
                    rowNumber++;
                    if (rowNumber > databaseMaximumRange) break;
                    databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) rowNumber);
                    if (databaseMain == null) continue;
                    if (databaseMain.Eventid != eventID) {
                        Log.d(TAG, "We Have a Break in the Service null ");
                        foundEnd = true;
                        break;
                    }

                    numbers[x] = databaseMain.Number;
                    names[x] = databaseMain.Name;
                    x++;
                    Log.d(TAG, "Number array value is " + databaseMain.Number);
                    UpdateDatabase((long) rowNumber, 1);

                }

            }

        }
        int x;
        for (x = (int) databaseMinimumRangeReference; x <= (int) databaseMaximumRangeReference; x++) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, (long) x);
            if (databaseReference == null) continue;
            if (databaseReference.Eventid == eventID) {
                message = databaseReference.Message;
                UpdateDatabase((long) x, 2);
                break;
            }
        }

        //Log.d(TAG, "FUCK U !!!!");
        int y;
        for (y = 0; y < 250; y++) {
            //Log.d(TAG, String.valueOf(y));
            if (numbers[y] != null) {
                sendSMS(numbers[y], message);
            }
            // if (y == 249) {
            //Log.d(TAG, String.valueOf(y));
            //Log.d(TAG, "i==249 IS TRUE ");
                /*Handler mHandler = new Handler(getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Message(s) Sent", Toast.LENGTH_SHORT).show();
                    }
                });*/

            //}
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            //Intent smsApplication = new Intent(android.content.Intent.ACTION_VIEW);
            //smsApplication.setType("vnd.android-dir/mms-sms");


        }

        Intent smsApplication = new Intent(Intent.ACTION_MAIN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                smsApplication.setPackage(defaultSmsPackageName);
            }

        } else // For early versions, do what worked for you before.
        {
            smsApplication = new Intent(Intent.ACTION_MAIN);
            smsApplication.addCategory(Intent.CATEGORY_LAUNCHER);
            smsApplication.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
        }
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        smsApplication,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        int c;
        String s = "";
        for (c = 0; c < 250; c++) {
            if (names[c] != null) {
                s = s + names[c] + ", ";
            }
        }
        int q = s.length();
        s = s.substring(0, q - 2);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_stat_notification)
                        .setColor(Color.DKGRAY)
                        .setContentTitle("Texter")
                        .setContentText("Message sent to " + s)
                        .setAutoCancel(true)
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{1000, 500})
                        .setLights(0xffffffff, 1500, 500);
        // Sets an ID for the notification
        Random rand = new Random();
        int mNotificationId = rand.nextInt(100 - 1 + 1) + 1;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }


    public void UpdateDatabase(Long rowNumber, int databaseType) { //Type 1 is Database Main and 2 is Reference
        if (databaseType == 1) {
            ContactDatabaseMain databaseMainDeletion = ContactDatabaseMain.findById(ContactDatabaseMain.class, rowNumber);
            databaseMainDeletion.delete();
        } else if (databaseType == 2) {
            ContactDatabaseReference databaseReferenceDeletion = ContactDatabaseMain.findById(ContactDatabaseReference.class, rowNumber);
            databaseReferenceDeletion.delete();
        }
    }
}
