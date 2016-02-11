package com.slashstudios.nicknagi.texter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

//import com.codinguser.android.contactpicker.ContactsPickerActivity;
//import com.codinguser.android.contactpicker.OnContactSelectedListener;


public class SchedulingScreen extends ActionBarActivity {

    private static final int CONTACT_PICKER_RESULT = 1001;
    private static String TAG = "Inchoo.net tutorial";
    private static Context context;
    EditText etMessage;
    EditText etNumber;
    BootstrapButton bLabel1;
    BootstrapButton bLabel2;
    BootstrapButton bLabel3;
    SharedPreferences EventID;
    SharedPreferences AlarmID;
    Boolean isOldSchedule;
    int alarmIDForCancel;
    //SharedPreferences FirstRun;
    //ContactDatabaseMain databaseMain;
    //Contact ContactObject;
    int bLabel1ArrayDisplayed, bLabel2ArrayDisplayed;
    EditText etDate;
    EditText etTime;
    Button bContacts;
    SharedPreferences CurrentMainDatabaseIndex;
    private Toolbar toolbar;
    private String contactID;
    private Uri uriContact;

    public static Context getAppContext() {
        return SchedulingScreen.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "in oncreate");

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_screen);
        //setupWindowAnimations();

        etMessage = (EditText) findViewById(R.id.etMessage);
        bContacts = (Button) findViewById(R.id.bContacts);

        // setupSearchView();

        EventID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        AlarmID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        CurrentMainDatabaseIndex = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //FirstRun=this.getPreferences(Context.MODE_PRIVATE);


//Failsafe Meachanism
        context = getApplicationContext();


        Long eventID = EventID.getLong("EVENTID", -1);
        int AlarmIdentifier = AlarmID.getInt("ALARMID", -1);
        long MainDatabaseIndex = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);
        //Log.d(TAG, eventID.toString() + " Parrt two");
        //Log.d(TAG, AlarmIdentifier + " Parrt two");

        if (eventID == -1 || AlarmIdentifier == -1 || MainDatabaseIndex == -1) {
            Toast.makeText(getApplicationContext(), "App Error Occured Reinstall App", Toast.LENGTH_SHORT).show();
            Uri packageUri = Uri.parse("package:com.slashstudios.nicknagi.texter");
            Intent uninstallIntent =
                    new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            startActivity(uninstallIntent);
        }


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //toolbar.setNavigationIcon(R.drawable.ic_action);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        bLabel1 = (BootstrapButton) findViewById(R.id.bLabel1);
        bLabel2 = (BootstrapButton) findViewById(R.id.bLabel2);
        bLabel3 = (BootstrapButton) findViewById(R.id.bLabel3);

        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        bLabel1.setVisibility(View.GONE);
        bLabel2.setVisibility(View.GONE);
        bLabel3.setVisibility(View.GONE);


        etDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    GetScheduleDate();
                    View current = getCurrentFocus();
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
                    if (current != null) linearLayout.requestFocus();
                }
            }
        });

        etTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    GetScheduleTime();
                    View current = getCurrentFocus();
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinearLayout);
                    if (current != null) linearLayout.requestFocus();
                }
            }
        });

        Intent intent = getIntent();
        Long l = intent.getLongExtra("EVENTID", -1);
        alarmIDForCancel = intent.getIntExtra("ALARMID", -1);
        //Toast.makeText(getApplicationContext(),l.toString(), Toast.LENGTH_SHORT).show();
        if (l != -1 && alarmIDForCancel != -1) {
            EditMessage(l);
            Toast.makeText(getApplicationContext(), "Opened for Edit", Toast.LENGTH_LONG).show();
            isOldSchedule = true;
            //set the messages and contactobject initialization here...
        } else {
            isOldSchedule = false;
        }

        SharedPreferences schedulingScreenPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstRun = schedulingScreenPreference.getBoolean("FIRSTRUNSECOND", true);

        if (isFirstRun) {

            new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .setTarget(new ViewTarget(findViewById(R.id.bContacts)))
                    .setContentTitle("Add A Recepient")
                    .setContentText("Add Recepients either from your contacts or add a custom selection.\n" +
                            "Multiple contacts can be selected as well.")
                    .setStyle(R.style.CustomShowcaseTheme)
                    .hideOnTouchOutside()
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                            new ShowcaseView.Builder(SchedulingScreen.this)
                                    .withNewStyleShowcase()
                                    .setTarget(new ViewTarget(findViewById(R.id.etMessage)))
                                    .setContentTitle("Message")
                                    .setContentText("Type in the message that has to be sent.")
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .hideOnTouchOutside()
                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                        @Override
                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                            new ShowcaseView.Builder(SchedulingScreen.this)
                                                    .withNewStyleShowcase()
                                                    .setTarget(new ViewTarget(findViewById(R.id.etDate)))
                                                    .setContentTitle("Date & Time")
                                                    .setContentText("Select the date and time for the message to be scheduled.")
                                                    .setStyle(R.style.CustomShowcaseTheme)
                                                    .hideOnTouchOutside()
                                                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                        @Override
                                                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                            new ShowcaseView.Builder(SchedulingScreen.this)
                                                                    .withNewStyleShowcase()
                                                                    .setTarget(new ViewTarget(findViewById(R.id.action_search)))
                                                                    .setContentTitle("Done")
                                                                    .setContentText("Select to schedule the message.")
                                                                    .setStyle(R.style.CustomShowcaseTheme)
                                                                    .hideOnTouchOutside()
                                                                    .build()
                                                                    .show();
                                                        }

                                                        @Override
                                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                        }

                                                        @Override
                                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                        }
                                                    })
                                                    .build()
                                                    .show();
                                        }

                                        @Override
                                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                        }

                                        @Override
                                        public void onShowcaseViewShow(ShowcaseView showcaseView) {
                                        }
                                    })
                                    .build()
                                    .show();
                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }
                    })
                    .build()
                    .show();


            SharedPreferences.Editor editor = schedulingScreenPreference.edit();
            editor.putBoolean("FIRSTRUNSECOND", false);
            editor.commit();
        }

        //String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
        //Settings.Secure.ANDROID_ID);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
         mAdView.loadAd(adRequest);


    }

    private void setupWindowAnimations() {
        Fade fade = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fade = new Fade();
            fade.setDuration(750);
            getWindow().setEnterTransition(fade);
        }
    }

    public void EditMessage(Long Eventid) {

        Contact ContactObject = ((Contact) getApplicationContext());

        //Determining database range
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
                //Log.d(TAG, "Stuck 1 3");
            } else {
                foundNullFinishValueAtStart = true;

            }
        }
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);

        // Log.d(TAG, "datbaseMax Range is in service "+databaseMaximumRange);
        // Log.d(TAG, "datbaseMin Range is in service "+databaseMinimumRange);

        boolean foundNullFinishValueAtStartReference = false;
        Long NullEndValueReference = 1l, indexReference = 1l;
        while (!foundNullFinishValueAtStartReference) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, indexReference);
            indexReference++;
            if (databaseReference == null) {
                NullEndValueReference++;
                //Log.d(TAG, "Stuck 1 4");
            } else {
                foundNullFinishValueAtStartReference = true;
            }
        }

        databaseMinimumRangeReference = NullEndValueReference;
        databaseMaximumRangeReference = EventID.getLong("EVENTID", -1);
        //Toast.makeText(getApplicationContext(),String.valueOf(databaseMaximumRangeReference), Toast.LENGTH_SHORT).show();


        //getting All Data
        int i;
        //Log.d(TAG, "Entering Loop");
        String[] names = new String[250];
        String[] numbers = new String[250];
        String message = "";
        Long time = 0l;
        boolean foundEventID = false;

        for (i = (int) databaseMinimumRange; i <= (int) databaseMaximumRange; i++) {
            //Log.d(TAG, "In the for loop");
            if (foundEventID == true) {
                break;
            }

            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, ((long) i));
            if (databaseMain == null) continue;

            if (databaseMain.Eventid == Eventid) {
                int x = 0, rowNumber = i;
                boolean foundEnd = false;
                names[x] = databaseMain.Name;
                numbers[x] = databaseMain.Number;
                x++;
                foundEventID = true;

                while (!foundEnd) {
                    rowNumber++;
                    if (rowNumber > databaseMaximumRange) break;
                    databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) rowNumber);
                    if (databaseMain == null) continue;
                    if (databaseMain.Eventid != Eventid) {
                        //Log.d(TAG, "We Have a Break in the Service null ");
                        foundEnd = true;
                        break;
                    }

                    names[x] = databaseMain.Name;
                    numbers[x] = databaseMain.Number;
                    x++;
                    //
                    //Log.d(TAG, "Number array value is " + databaseMain.Number);

                }

            }

        }

        ContactObject.setName(names);
        ContactObject.setPhoneNumber(numbers);
        LabelArranger();
        int x;
        for (x = (int) databaseMinimumRangeReference; x <= (int) databaseMaximumRangeReference; x++) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, (long) x);
            if (databaseReference == null) continue;
            if (databaseReference.Eventid == Eventid) {
                message = databaseReference.Message;
                time = databaseReference.Time;
                break;
            }
        }
        //Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
        etMessage.setText(message);

        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.setTimeInMillis(time);

        ContactObject.setSendDAY(cal.get(Calendar.DATE));  //1-31
        ContactObject.setSendMONTH(cal.get(Calendar.MONTH));  //first month is 0!!! January is zero!!!
        ContactObject.setSendYEAR(cal.get(Calendar.YEAR));//year...

        ContactObject.setSendHOUR(cal.get(Calendar.HOUR_OF_DAY));  //HOUR
        ContactObject.setSendMINUTE(cal.get(Calendar.MINUTE));       //MIN

        Date date = new Date(time);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = formatter.format(date);

        formatter = new SimpleDateFormat("HH:mm:ss");
        String timeString = formatter.format(date);

        etDate.setText(dateString);
        etTime.setText(timeString);
    }

    public void GetScheduleDate() {
        Contact ContactObject = ((Contact) getApplicationContext());
        DialogFragment selectDate = new DatePickerFragment();
        selectDate.show(getSupportFragmentManager(), "datePicker");
    }

    public void GetScheduleTime() {
        Contact ContactObject = ((Contact) getApplicationContext());
        DialogFragment selectTime = new TimePickerFragment();
        selectTime.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Contact ContactObject = ((Contact) getApplicationContext());
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //else if(id==android.R.id.home)onBackPressed();


        else if (id == R.id.action_search) {

            SetDatabses();

            Intent intent = getIntent();
            Long Eventid = intent.getLongExtra("EVENTID", -1);
            if (Eventid != -1) {
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent cancelServiceIntent = new Intent(getApplicationContext(), MessageSendingService.class);
                PendingIntent cancelServicePendingIntent = PendingIntent.getService(getApplicationContext(), alarmIDForCancel, cancelServiceIntent, 0);
                am.cancel(cancelServicePendingIntent);

                DeleteOldData();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void DeleteOldData() {

        Intent intent = getIntent();
        Long Eventid = intent.getLongExtra("EVENTID", -1);
        if (Eventid == -1)
            Toast.makeText(getApplicationContext(), "Error in getting Intent", Toast.LENGTH_SHORT).show();

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
                // Log.d(TAG, "Stuck 1 1");
            } else {
                foundNullFinishValueAtStart = true;

            }
        }
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);

        // Log.d(TAG, "datbaseMax Range is in service "+databaseMaximumRange);
        // Log.d(TAG, "datbaseMin Range is in service "+databaseMinimumRange);

        boolean foundNullFinishValueAtStartReference = false;
        Long NullEndValueReference = 1l, indexReference = 1l;
        while (!foundNullFinishValueAtStartReference) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, indexReference);
            indexReference++;
            if (databaseReference == null) {
                NullEndValueReference++;
                // Log.d(TAG, "Stuck 1 2");
            } else {
                foundNullFinishValueAtStartReference = true;
            }
        }

        databaseMinimumRangeReference = NullEndValueReference;
        databaseMaximumRangeReference = EventID.getLong("EVENTID", -1);
        //Toast.makeText(getApplicationContext(),String.valueOf(databaseMaximumRangeReference), Toast.LENGTH_SHORT).show();


        //getting All Data
        int i;
        boolean foundEventID = false;

        for (i = (int) databaseMinimumRange; i <= (int) databaseMaximumRange; i++) {
            //Log.d(TAG, "In the for loop");
            if (foundEventID == true) {
                break;
            }

            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, ((long) i));
            if (databaseMain == null) continue;

            if (databaseMain.Eventid == Eventid) {
                int x = 0, rowNumber = i;
                boolean foundEnd = false;
                databaseMain.delete();
                x++;
                foundEventID = true;

                while (!foundEnd) {
                    rowNumber++;
                    if (rowNumber > databaseMaximumRange) break;
                    databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) rowNumber);
                    if (databaseMain == null) continue;
                    if (databaseMain.Eventid != Eventid) {
                        // Log.d(TAG, "We Have a Break in the Service null ");
                        foundEnd = true;
                        break;
                    }

                    databaseMain.delete();
                    x++;
                    // Log.d(TAG, "Number array value is " + databaseMain.Number);

                }

            }

        }

        int x;
        for (x = (int) databaseMinimumRangeReference; x <= (int) databaseMaximumRangeReference; x++) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, (long) x);
            if (databaseReference == null) continue;
            if (databaseReference.Eventid == Eventid) {
                databaseReference.delete();
                break;
            }
        }
    }

    public void LaunchContactPicker(View view) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(SchedulingScreen.this);
        //builderSingle.setIcon(R.drawable.ic_launcher);
        //builderSingle.setTitle("Choose One Option");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                SchedulingScreen.this,
                R.layout.dialog_layout);
        arrayAdapter.add("           Choose From Contacts");
        arrayAdapter.add("           Add New Phone Number");

        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                            startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                        }
                        if (which == 1) getPhoneNumber();
                        dialog.dismiss();
                    }
                });
        builderSingle.show();


    }

    public void getPhoneNumber() {
        final Contact ContactObject = ((Contact) getApplicationContext());
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SchedulingScreen.this);
        alertDialog.setTitle("Please Enter Phone Number");

        final EditText input = new EditText(SchedulingScreen.this);
        input.setEms(10);
        InputFilter[] filters = new InputFilter[2];
        filters[0] = new InputFilter.LengthFilter(13); //Filter to 10 characters
        filters[1] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    //boolean done = false;
                    // Your condition here
                    if ((!Character.isDigit(source.charAt(i)) && (source.charAt(i) != '+'))) {
                        return "";
                    }
                }
                return null;
            }
        };
        input.setFilters(filters);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNo = input.getText().toString();
                        int i, counter = 0;
                        for (i = 0; i < phoneNo.length(); i++) {
                            if (phoneNo.charAt(i) == '+') {
                                counter++;
                            }
                        }
                        if (counter < 2) {
                            ContactObject.setName(phoneNo);
                            ContactObject.setPhoneNumber(phoneNo);
                            LabelArranger();
                        } else {
                            Toast.makeText(SchedulingScreen.this, "Invalid Number", Toast.LENGTH_LONG).show();

                        }
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Contact ContactObject = ((Contact) getApplicationContext());
        if (resultCode == RESULT_OK) {
            uriContact = data.getData();


            //retrieveContactNumber();
            String phoneNo = null;
            String Name = null;
            Cursor cursor = getContentResolver().query(uriContact, null, null, null, null);
            cursor.moveToFirst();

            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int NumberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            //String image_uri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            phoneNo = cursor.getString(phoneIndex);
            Name = cursor.getString(NumberIndex);

            String[] previousPhoneNumbers = new String[250];
            previousPhoneNumbers = ContactObject.getFilledPhoneNumbers();
            int i;
            boolean phoneThere = false;
            for (i = 0; i < 250; i++) {
                if (phoneNo.equals(previousPhoneNumbers[i])) {
                    phoneThere = true;
                    break;
                }
            }

            if (!phoneThere) {
                ContactObject.setName(Name);
                ContactObject.setPhoneNumber(phoneNo);
                LabelArranger();
                Toast.makeText(this, Name + "  " + phoneNo, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Contact Already Selected", Toast.LENGTH_LONG).show();
            }

        }
    }


    public void RemoveContact(View view) {
        Contact ContactObject = ((Contact) getApplicationContext());
        view.setVisibility(View.GONE);
        String s;
        if (view.getId() == R.id.bLabel1) {
            bLabel1.setText("a");
            ContactObject.setNameArrayValue(null, bLabel1ArrayDisplayed);
            ContactObject.setPhoneNumberArrayValue(null, bLabel1ArrayDisplayed);
            LabelArranger();

        } else if (view.getId() == R.id.bLabel2) {
            bLabel2.setText("b");
            ContactObject.setNameArrayValue(null, bLabel2ArrayDisplayed);
            ContactObject.setPhoneNumberArrayValue(null, bLabel2ArrayDisplayed);
            LabelArranger();

        }
    }

    public void LabelArranger() {
        Contact ContactObject = ((Contact) getApplicationContext());
        int i, i_2 = 0, i_3 = 0;
        int counter = 0;
        bLabel1.setVisibility(View.GONE);
        bLabel2.setVisibility(View.GONE);
        bLabel3.setVisibility(View.GONE);

        for (i = 0; i < 250; i++) {
            if (!(ContactObject.getNameArrayValue(i) == null)) {
                bLabel1.setVisibility(View.VISIBLE);
                bLabel1.setText(ContactObject.getNameArrayValue(i));
                bLabel1ArrayDisplayed = i;
                i_2 = i + 1;
                i_3 = i + 1;
                break;
            }
        }

        for (i = i_2; i < 250; i++) {
            if (!(ContactObject.getNameArrayValue(i) == null)) {
                bLabel2.setText(ContactObject.getNameArrayValue(i));
                bLabel2ArrayDisplayed = i;
                bLabel2.setVisibility(View.VISIBLE);
                i_3 = i + 1;
                break;
            }
        }

        if (bLabel2.getVisibility() == View.VISIBLE) {
            for (i = i_3; i < 250; i++) {
                if (!(ContactObject.getNameArrayValue(i) == null)) {
                    counter++;
                }
            }
        }
        //Toast.makeText(this, Integer.toString(counter), Toast.LENGTH_LONG).show();
        if (counter > 0) {
            bLabel3.setVisibility(View.VISIBLE);
            bLabel3.setText(".." + counter + " more");
        }

    }

    public void ShowList(View view) {
        Intent intent = new Intent(this, ContactsListView.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Contact ContactObject = ((Contact) getApplicationContext());
        LabelArranger();
        //etMessage.setText(ContactObject.getMessage());
    }

    @Override
    public void onBackPressed() {
        // do something on back.
        if (isOldSchedule == false) {
            new AlertDialog.Builder(this)
                    .setMessage("Warning: No Data Will Be Saved")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                            startActivity(intent);
                            SchedulingScreen.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setMessage("Warning: No Edited Data Will Be Saved")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                            startActivity(intent);
                            SchedulingScreen.this.finish();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }


    }

    public void SetDatabses() {
        Contact ContactObject = ((Contact) getApplicationContext());
        //Toast.makeText(getApplicationContext(),"Done button clicked", Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(),etMessage.getText(), Toast.LENGTH_SHORT).show();

        int x;
        boolean isEmpty = true;
        for (x = 0; x < 250; x++) {
            if (ContactObject.getPhoneNumberArrayValue(x) != null) {
                isEmpty = false;
                break;
            }
        }
        String s = String.valueOf(etMessage.getText());
        if (!s.equals("")) {
            if (s.length() <= 160) {
                if (!isEmpty) { //problem her ewith zero
                    if (ContactObject.getSendYEAR() != 0 && ContactObject.getSendHOUR() != 0) {
                        //Toast.makeText(getApplicationContext(),"Message Succecsful", Toast.LENGTH_SHORT).show();
                        ContactObject.setMessage(s);
                        //etMessage.setText(ContactObject.getNameArrayValue(0)+"  "+s+"  "+ContactObject.getSendYEAR());

                        SetDatabaseValues();
                        ContactObject.clearClass();
                        finish();

                    } else {
                        Toast.makeText(getApplicationContext(), "Select Time", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Select A Recipient", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Message Longer Than 160 Characters", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Message in Text Field", Toast.LENGTH_SHORT).show();
        }
    }

    public void SetDatabaseValues() {
        Contact ContactObject = ((Contact) getApplicationContext());
        String[] names;
        String[] numbers;
        String message;

        names = ContactObject.getFilledNames();
        numbers = ContactObject.getFilledPhoneNumbers();
        message = ContactObject.getMessage();

        Long eventID = EventID.getLong("EVENTID", -1);
        int AlarmIdentifier = AlarmID.getInt("ALARMID", -1);
        AlarmIdentifier = AlarmIdentifier + 1;
        eventID = eventID + 1;

        if (AlarmIdentifier == 0 || eventID == 0) {
            Toast.makeText(getApplicationContext(), "App Error Reinstall App", Toast.LENGTH_SHORT).show();
            Uri packageUri = Uri.parse("package:com.slashstudios.nicknagi.texter");
            Intent uninstallIntent =
                    new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            startActivity(uninstallIntent);
        }
//        etMessage.setText(eventID.toString());
        Calendar cal = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        cal.set(Calendar.DATE, ContactObject.getSendDAY());  //1-31
        cal.set(Calendar.MONTH, ContactObject.getSendMONTH());  //first month is 0!!! January is zero!!!
        cal.set(Calendar.YEAR, ContactObject.getSendYEAR());//year...

        cal.set(Calendar.HOUR_OF_DAY, ContactObject.getSendHOUR());  //HOUR
        cal.set(Calendar.MINUTE, ContactObject.getSendMINUTE());       //MIN
        cal.set(Calendar.SECOND, 0);

        Long time = cal.getTimeInMillis();
        //Save all this data in a database
        List collection = new ArrayList();
        //int i = ContactObject.getNumberOfNumbers();
        int x;
        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        int counter = 0;

        int looper;
        /*Log.d(TAG, "before adding database values :-");
        for (looper=1;looper<=10;looper++){
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class,(long)looper);
            if (databaseMain!=null)
            Log.d(TAG, databaseMain.Eventid.toString() + "  "+ databaseMain.Name + "  "+ databaseMain.Number);
        }
        */

        for (x = 0; x < 250; x++) {
            //Toast.makeText(getApplicationContext(),names[x] + " "+ numbers[x], Toast.LENGTH_SHORT).show();
            if (names[x] != null) {
                collection.add(new ContactDatabaseMain(eventID, names[x], numbers[x]));
            }
            //counter++;
            //Log.d(TAG, "Added in loop");
            //ContactDatabaseMain databaseMainTest = new ContactDatabaseMain(eventID, names[x], numbers[x]);
            //databaseMainTest.save();

        }
        ContactDatabaseMain.saveInTx(collection);


        ContactDatabaseReference databaseReference = new ContactDatabaseReference(eventID, message, time, AlarmIdentifier);
        databaseReference.save();

        //Toast.makeText(getApplicationContext(), "Save Succesful", Toast.LENGTH_SHORT).show();

        //Toast.makeText(getApplicationContext(),"EventId is: "+eventID.toString(),Toast.LENGTH_LONG).show();

        SharedPreferences.Editor ed = EventID.edit();
        ed.putLong("EVENTID", eventID);
        ed.apply();

        SharedPreferences.Editor name = CurrentMainDatabaseIndex.edit();
        long previousValue = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);
        name.putLong("CURRENTMAINDATABASEINDEX", previousValue + collection.size());
        name.apply();

        SharedPreferences.Editor AlarmED = AlarmID.edit();
        AlarmED.putInt("ALARMID", AlarmIdentifier);
        AlarmED.apply();


        Log.d(TAG, "Database Info AFter Adding:- ");
        for (looper = 1; looper <= 10; looper++) {
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) looper);
            if (databaseMain != null)
                Log.d(TAG, databaseMain.Eventid.toString() + "  " + databaseMain.Name + "  " + databaseMain.Number);
        }

        ScheduleMessage(AlarmIdentifier, time, eventID);
        //Intent intent = new Intent(this,MessageSendingService.class);
        //intent.putExtra("EVENTID",eventID);
        //startService(intent);

    }

    public void ScheduleMessage(int AlarmIdentifier, Long time, Long eventID) {
        Intent intent = new Intent(getApplicationContext(), MessageSendingService.class);
        intent.putExtra("EVENTID", eventID);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        PendingIntent pintent = PendingIntent.getService(getApplicationContext(), AlarmIdentifier, intent, 0);

//or if you start an Activity
//PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0,intent, 0);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, time, pintent);
        Toast.makeText(this, "Message Scheduled", Toast.LENGTH_LONG).show();
    }
}

