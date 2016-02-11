package com.slashstudios.nicknagi.texter;


import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.view.MaterialListView;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.software.shell.fab.ActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

//import com.dexafree.materialList.card.OnButtonClickListener;
//import com.dexafree.materialList.card.provider.BasicImageButtonsCardProvider;


/**
 * Created by nicknagi on 03-07-2015.
 */
public class HomeScreen extends ActionBarActivity {

    private static String TAG = "Inchoo.net tutorial";
    Long databaseLength = 0l;// = preferences.getLong("LENGTH", -1);
    Long databaseReferenceLength = 0l;// = preferences.getLong("REFERENCE_LENGTH",-1);

    SharedPreferences EventID;
    SharedPreferences AlarmID;
    SharedPreferences CurrentMainDatabaseIndex;
    Boolean stateOfContactType;
    MaterialListView mListView;
    List<Card> cards;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState) {

        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        // }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_screen);

        //setupWindowAnimations();

        Log.d("In Oncreate", TAG);

        cards = new ArrayList<>();
        mListView = (MaterialListView) findViewById(R.id.material_listview);


        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        //Create a Card
        /*rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm); */

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        EventID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        AlarmID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        CurrentMainDatabaseIndex = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);

       /* final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("Warning")
                .setMessage("Be sure not to schedule any important messages through this app. It is still under development and could still have major bugs. As the app is in the alpha version you can only schedule messages but cannot edit or delete it as of now.    Be sure to report any major bugs.")
                .setPositiveButton("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                    }
                }); */


        //mMaterialDialog.show();

        //For Loop Fix to long
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},1);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }


        if (isFirstRun) {

            Log.d(TAG, "It is the first run YAYYY!!");


            // Code to run once
            SharedPreferences.Editor ed = EventID.edit();
            ed.putLong("EVENTID", 0);
            ed.apply();

            SharedPreferences.Editor dataEditor = CurrentMainDatabaseIndex.edit();
            dataEditor.putLong("CURRENTMAINDATABASEINDEX", 0);
            dataEditor.apply();

            SharedPreferences.Editor AlarmED = AlarmID.edit();
            AlarmED.putInt("ALARMID", 0);
            AlarmED.apply();

            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();

            Long eventID = EventID.getLong("EVENTID", -1);
            Log.d(TAG, eventID.toString());

            new ShowcaseView.Builder(this)
                    .withNewStyleShowcase()
                    .setTarget(new ViewTarget(findViewById(R.id.tvDisplay)))
                    .setContentTitle("Home Screen")
                    .setContentText("Here scheduled messages are shown.")
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setShowcaseEventListener(new OnShowcaseEventListener() {
                        @Override
                        public void onShowcaseViewHide(ShowcaseView showcaseView) {
                            ShowcaseView b = new ShowcaseView.Builder(HomeScreen.this)
                                    .withNewStyleShowcase()
                                    .setTarget(new ViewTarget(findViewById(R.id.fabNew)))
                                    .setContentTitle("New Message")
                                    .setContentText("Select this button to schedule a new message.")
                                    .setStyle(R.style.CustomShowcaseTheme)
                                    .hideOnTouchOutside()
                                    .build();

                            b.hideButton();
                            b.show();
                        }

                        @Override
                        public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                        }

                        @Override
                        public void onShowcaseViewShow(ShowcaseView showcaseView) {

                        }
                    })
                    .hideOnTouchOutside()
                    .build()
                    .show();

        }


        //EventID = this.getPreferences(Context.MODE_PRIVATE);
        //Long eventID = EventID.getLong("EVENTID", -1);


        ActionButton fabNew = (ActionButton) findViewById(R.id.fabNew);

        fabNew.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
        fabNew.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
        fabNew.setImageResource(R.drawable.fab_plus_icon);

        // DefaultItemAnimator animator = new DefaultItemAnimator();
        //animator.setAddDuration(10000);
        //animator.setRemoveDuration(10000);
        //mListView.setItemAnimator(animator);
        mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull Card card, int position) {
                // Show a toast
                String s = (String) card.getTag();
                Log.d(TAG, "card tag when retrieved is :" + s);
                Toast.makeText(getApplicationContext(), "Delete Succesful", Toast.LENGTH_LONG).show();
                DeleteCardData(s, card);
            }
        });

        /*mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(Card card, int position) {
                Log.d("SHORT_CLICK", card.getTag().toString());
                EditCardData(card.getTag().toString(), card);
            }

            @Override
            public void onItemLongClick(Card card, int position) {
                Log.d("LONG_CLICK", card.getTag().toString());
            }
        }); */

        //RecyclerView.LayoutManager m =  mListView.getLayoutManager();
        //m.offsetChildrenVertical(10);
        //mListView.setLayoutManager(m);
        //cards are refreshed every 10 seconds here


        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        final Handler h = new Handler();
        final int delay = 10000; //milliseconds

        h.postDelayed(new Runnable() {
            public void run() {
                //do something
                RefreshCards();
                h.postDelayed(this, delay);
            }
        }, delay);

        //mListView.getLayoutManager().offsetChildrenVertical(1000); //Cards spacing set here doesnt work
    }

    @Override
    protected void onResume() {
        super.onResume();
// refresh complete
// load more refresh complete
        //materialRefreshLayout.finishRefreshLoadMore(
        RefreshCards();

    }

    public void RefreshCards() {
        AnimatorTypeWorker();
        Log.d(TAG, "In Resfresh Cards");
        long i;
        boolean isDatabaseEmpty = true;
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();
        //Log.d(TAG, "In OnResume starting loop");
        Long eventID = EventID.getLong("EVENTID", -1);
        // Log.d(TAG, "EventID Value IS" + eventID);
        for (i = 1; i <= eventID; i++) {
            //Log.d(TAG, "In OnResume loop "+i);
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, i);
            if (databaseReference != null) {
                isDatabaseEmpty = false;
                //Log.d(TAG, "The Database is not empty OnResume");
                //Log.d(TAG, "On resume database reference is : "+ databaseReference.Message);
                break;
            }
        }
        //Log.d(TAG, "In OnResume checking if empty");
        //Log.d(TAG, "In OnResume isDatabaseEmpty value : "+isDatabaseEmpty);
        TextView tvDisplay = (TextView) findViewById(R.id.tvDisplay);
        if (!isDatabaseEmpty) {
            //mListView.getAdapter().clearAll();
            mListView.getAdapter().clearAll();
            tvDisplay.setEnabled(false);
            tvDisplay.setVisibility(View.INVISIBLE);
            //setContentView(R.layout.home_screen);
            AnimatorTypeWorker();
            //Log.d(TAG, "Cleared Listview");
            InitializeCardsInfoListData();
        } else {
            //mListView.getAdapter().clearAll();
            mListView.getAdapter().clearAll();
            tvDisplay.setEnabled(true);
            tvDisplay.setTextColor(Color.GRAY);
            tvDisplay.setVisibility(View.VISIBLE);
            //setContentView(R.layout.home_screen);
            AnimatorTypeWorker();

        }


        MaterialRefreshLayout materialRefreshLayout;
        materialRefreshLayout = (MaterialRefreshLayout) findViewById(R.id.refresh);
        materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                Random rand = new Random();

                int n = rand.nextInt(1000) + 2700;
                materialRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        RefreshCards();
                    }
                }, n);
            }

        });
        materialRefreshLayout.finishRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void InitializeCardsInfoListData() {
        AnimatorTypeWorker();

        //List<CardsInfo> CardInfoList = new ArrayList<CardsInfo>();


        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        //ContactDatabaseReference databaseReference = new ContactDatabaseReference();

        boolean foundNullFinishValueAtStart = false;
        Long NullEndValue = 1l, index = 1l, counterX = 0l;
        while (!foundNullFinishValueAtStart) {
            //counterX++;
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, index);
            index++;
            if (databaseMain == null) {
                NullEndValue++;
                // Log.d(TAG, "Stuck 1" + "Intitalization 1");
            } else {
                //if (counterX==1)NullEndValue=1l;
                foundNullFinishValueAtStart = true;

            }
        }
        long databaseMinimumRange, databaseMaximumRange;
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);
        //  Log.d(TAG, "databasemaximium range in homescreen cards is   " + databaseMaximumRange);


        int arrayValue = 0;
        //long eventIDForLoop,previousEventID=-100,counter=0;
        String[] names = new String[250];
        long forLooper = 0, previousEventID = 0, currentEventID, counter = 0;

        for (forLooper = databaseMinimumRange; forLooper <= databaseMaximumRange + 1; forLooper++) {
            counter++;
            // Log.d(TAG, "In the for loop cardsview");
            // Log.d(TAG, "Minimum Range is " + databaseMinimumRange);
            // Log.d(TAG, "Maximum Range is " + databaseMaximumRange);
            // Log.d(TAG, "forLooper value is " + forLooper);
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, forLooper);

            if (counter == 1) previousEventID = databaseMain.Eventid;

            //Some problem here makes 1 less card always

            if (databaseMain != null) {
                currentEventID = databaseMain.Eventid;

                if (previousEventID == currentEventID) {
                    // Log.d(TAG, "eventIDs ARE EQUAL");
                    names[arrayValue] = databaseMain.Name;
                    arrayValue++;
                } else {
                    //  Log.d(TAG, "eventIDs ARE NOT EQUAL");
                    int x;
                    String s = "";
                    int lastvalue = 0;
                    if (names[1] != null) {
                        for (x = 0; x < names.length; x++) {
                            if (names[x] != null) {
                                s = s + names[x] + ", ";
                            }
                        }
                        s = s.substring(0, s.length() - 2);
                    } else {
                        s = s + names[0];
                    }
                    Bitmap Image = setCardDrawable(previousEventID);
                    Drawable ContactImage = new BitmapDrawable(getResources(), Image);

                    String messageDisplay = "";
                    if (getMessageText(previousEventID).length() > 50) {
                        messageDisplay = getMessageScheduleTime(previousEventID).toUpperCase() + "\n\n\n" + getMessageText(previousEventID).substring(0, 50) + "\n" + "(Open EDIT Mode To View Full Message)";
                    } else {
                        messageDisplay = getMessageScheduleTime(previousEventID).toUpperCase() + "\n\n\n" + getMessageText(previousEventID);
                    }

                    Card card = new Card.Builder(this)
                            .setTag(getMessageText(previousEventID))
                            .setDismissible()
                            .withProvider(new CardProvider())
                            .setLayout(R.layout.material_basic_image_buttons_card_layout)
                            .setDividerVisible(true)
                            .addAction(R.id.right_text_button, new TextViewAction(this)
                                    .setText("DELETE")
                                    .setTextColor(Color.RED)
                                    .setListener(new OnActionClickListener() {
                                        @Override
                                        public void onActionClicked(View view, final Card card) {
                                            //deletion method
                                            final String s = (String) card.getTag();
                                            // Log.d(TAG, "card tag when retrieved is :" + s);
                                            // Log.d("DELETE_CLICK", card.getTag().toString());
                                            //mListView.getAdapter().remove(card, true);

                                            AlertDialog alert = new AlertDialog.Builder(HomeScreen.this)
                                                    .create();
                                            alert.setTitle("Are You Sure?");
                                            alert.setMessage("Are you sure that you want to delete the scheduled message?");
                                            alert.setButton("YES", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // closing the application
                                                    DeleteCardData(s, card);
                                                    Toast.makeText(getApplicationContext(), "Delete Succesful", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                            alert.setButton2("NO", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            alert.show();


                                        }

                                    }))
                            .addAction(R.id.left_text_button, new TextViewAction(this)
                                    .setListener(new OnActionClickListener() {
                                        @Override
                                        public void onActionClicked(View view, Card card) {
                                            EditCardData(card.getTag().toString(), card);
                                        }
                                    })
                                    .setText("EDIT")
                                    .setTextColor(Color.BLACK))
                            .setTitle(s)
                            .setDescription(messageDisplay)
                            .setDrawable(ContactImage)
                            .endConfig()
                            .build();

// Add card
                    //mListView.getAdapter().add(card);
                    cards.add(card);
                    // it thinks that long touch and button press r the same so fix that.

// Remove all card


                    counter = 0;
                    // Log.d(TAG, "array values are" + s);
                    arrayValue = 0;
                    forLooper--;
                    for (x = 0; x < names.length; x++) {
                        if (names[x] != null)
                            names[x] = null;
                    }
                }
            }

            if (forLooper > databaseMaximumRange) {
                //Log.d(TAG, "eventIDs ARE NOT EQUAL");
                // Log.d(TAG, "added a new cardslist value");
                databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (forLooper - 1l));
                int x;
                String s = "";
                //Log.d(TAG, "names.length is " + names.length);
                if (names[1] != null) {
                    for (x = 0; x < names.length; x++) {
                        if (names[x] != null)
                            s = s + names[x] + ", ";
                    }
                    s = s.substring(0, s.length() - 2);
                } else {
                    s = s + names[0];
                }

                Bitmap Image = setCardDrawable(previousEventID);
                Drawable ContactImage = new BitmapDrawable(getResources(), Image);

                String messageDisplay = "";
                if (getMessageText(previousEventID).length() > 50) {
                    messageDisplay = getMessageScheduleTime(previousEventID).toUpperCase() + "\n\n\n" + getMessageText(previousEventID).substring(0, 50) + "\n" + "(Open EDIT Mode To View Full Message)";
                } else {
                    messageDisplay = getMessageScheduleTime(previousEventID).toUpperCase() + "\n\n\n" + getMessageText(previousEventID);
                }

                Card card = new Card.Builder(this)
                        .setTag(getMessageText(previousEventID))
                        .setDismissible()
                        .withProvider(new CardProvider())
                        .setLayout(R.layout.material_basic_image_buttons_card_layout)
                        .setDividerVisible(true)
                        .addAction(R.id.right_text_button, new TextViewAction(this)
                                .setText("DELETE")
                                .setTextColor(Color.RED)
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, final Card card) {
                                        //deletion method
                                        final String s = (String) card.getTag();
                                        // Log.d(TAG, "card tag when retrieved is :" + s);
                                        //Log.d("DELETE_CLICK", card.getTag().toString());
                                        //mListView.getAdapter().remove(card, true);
                                        AlertDialog alert = new AlertDialog.Builder(HomeScreen.this)
                                                .create();
                                        alert.setTitle("Are You Sure?");
                                        alert.setMessage("Are you sure that you want to delete the scheduled message?");
                                        alert.setButton("YES", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // closing the application
                                                DeleteCardData(s, card);
                                                Toast.makeText(getApplicationContext(), "Delete Succesful", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        alert.setButton2("NO", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        alert.show();
                                    }

                                }))
                        .addAction(R.id.left_text_button, new TextViewAction(this)
                                .setText("EDIT")
                                .setListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        EditCardData(card.getTag().toString(), card);
                                    }
                                })
                                .setTextColor(Color.BLACK))
                        .setTitle(s)
                        .setDescription(messageDisplay)
                        .setDrawable(ContactImage)
                        .endConfig()
                        .build();

// Add card
                //mListView.getAdapter().add(card);
                cards.add(card);


            }

        }
        //int z = 0;
        //for (z = 0; z < 5; z++) cards.add(AddMockCards(z));
        mListView.getAdapter().addAll(cards);
        cards.clear();

        //Log.d(TAG, "list size is "+CardInfoList.size());

        //ContactInfoAdapter adapter = new ContactInfoAdapter(CardInfoList);
        // CardsInfo ci =  CardInfoList.get(4);
        //Log.d(TAG, "First Value in object  list is " + ci.time + ci.getNameAtValue(0) + ci.message);
        //adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        //rv.setAdapter(adapter);

    }

    public Bitmap setCardDrawable(Long eventID) {
        Bitmap bitmap;
        String phoneNumber = getPhoneNumber(eventID);
        if (!stateOfContactType) bitmap = retrieveContactPhoto(this, phoneNumber);
        else {
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_contact);
        }
        return bitmap;
    }

    private void DeleteCardData(String Message, Card card) {
        Log.d(TAG, "In the delete method ");
        // mListView.setItemAnimator(new SlideInDownAnimator());
        //mListView.getItemAnimator().setAddDuration(300);
        //mListView.getItemAnimator().setRemoveDuration(300);
        // mListView.remove(card);
        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();
        long row = 1;
        while (true) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
            row++;
            //Log.d(TAG, "loop in database reference");
            if (databaseReference != null) {
                // Log.d(TAG, "not null");
                //Log.d(TAG, "message in database is  : " + databaseReference.Message);
                String s = Message;
                //Log.d(TAG, s);
                if (databaseReference.Message.equals(Message)) {
                    // Log.d(TAG, "found matching message");
                    break;
                }
            }
        }
        row = row - 1;
        databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);

        Long eventID = databaseReference.Eventid;
        int AlarmID = databaseReference.Alarmid;

        databaseReference.delete();

        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent cancelServiceIntent = new Intent(getApplicationContext(), MessageSendingService.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getService(getApplicationContext(), AlarmID, cancelServiceIntent, 0);
        am.cancel(cancelServicePendingIntent);

        Log.d(TAG, "database reference deleted");

        boolean foundNullFinishValueAtStart = false;
        Long NullEndValue = 1l, index = 1l, counterX = 0l;
        while (!foundNullFinishValueAtStart) {
            //counterX++;
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, index);
            index++;
            if (databaseMain == null) {
                NullEndValue++;
                // Log.d(TAG, "Stuck 1 Deletion");
            } else {
                //if (counterX==1)NullEndValue=1l;
                foundNullFinishValueAtStart = true;

            }
        }
        long databaseMinimumRange, databaseMaximumRange;
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);
        // Log.d(TAG, "databasemaximium range in homescreen cards is   " + databaseMaximumRange);

        int i;
        boolean foundEventID = false;

        Log.d(TAG, "Past all calculations ");

        for (i = (int) databaseMinimumRange; i <= (int) databaseMaximumRange; i++) {
            // Log.d(TAG, "In the for loop");
            if (foundEventID == true) {
                break;
            }

            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, ((long) i));
            if (databaseMain == null) continue;

            if (databaseMain.Eventid == eventID) {
                int x = 0, rowNumber = i;
                boolean foundEnd = false;
                UpdateDatabase((long) rowNumber, 1);
                foundEventID = true;

                while (!foundEnd) {
                    rowNumber++;
                    if (rowNumber > databaseMaximumRange) break;
                    databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) rowNumber);
                    if (databaseMain == null) continue;
                    if (databaseMain.Eventid != eventID) {
                        foundEnd = true;
                        break;
                    }
                    UpdateDatabase((long) rowNumber, 1);

                }

            }

        }
        //df //add more stuff here
        Log.d(TAG, "calling Onresume");
        RefreshCards();
    }

    private void EditCardData(String Message, Card card) {
        Log.d(TAG, "In the edit method ");
        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();
        long row = 1;
        while (true) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
            row++;
            //Log.d(TAG, "loop in database reference");
            if (databaseReference != null) {
                // Log.d(TAG, "not null");
                //  Log.d(TAG, "message in database is  : " + databaseReference.Message);
                String s = Message;
                //   Log.d(TAG, s);
                if (databaseReference.Message.equals(Message)) {
                    //       Log.d(TAG, "found matching message");
                    break;
                }
            }
        }
        row = row - 1;
        databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);

        Long eventID = databaseReference.Eventid;
        int alarmID = databaseReference.Alarmid;

        Intent intent = new Intent(this, SchedulingScreen.class);
        intent.putExtra("ALARMID", alarmID);
        intent.putExtra("EVENTID", eventID);
        startActivity(intent);

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

    public String getMessageScheduleTime(long eventID) {
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();
        long row = 1;
        while (true) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
            row++;
            if (databaseReference != null) {
                if (databaseReference.Eventid == eventID) break;
            }
        }
        row = row - 1;
        databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        // Log.d(TAG, "Row Value is " + row);

        calendar.setTimeInMillis(databaseReference.Time);
        return formatter.format(calendar.getTime());
    }

    public String getMessageText(long eventID) {
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();
        long row = 1;
        while (true) {
            databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
            row++;
            if (databaseReference != null) {
                if (databaseReference.Eventid == eventID) break;
            }
        }
        row = row - 1;
        databaseReference = ContactDatabaseReference.findById(ContactDatabaseReference.class, row);
        return databaseReference.Message;
    }

    public void StartNewScreen(View view) {
        Intent intent = new Intent(this, SchedulingScreen.class);
        Contact ContactObject = ((Contact) getApplicationContext());
        ContactObject.clearClass();
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        //}
        //else {
        startActivity(intent);
        //}
    }

    public Card AddMockCards(int z) {
        Card card = new Card.Builder(this)
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setDividerVisible(true)
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("DELETE")
                        .setTextColor(Color.RED)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                //deletion method
                                //String s = (String) card.getTag();
                                //Log.d(TAG, "card tag when retrieved is :" + s);
//                                Log.d("DELETE_CLICK", card.getTag().toString());
                                card.dismiss();
                                //DeleteCardData(s, card);
                                //Toast.makeText(getApplicationContext(), "Delete Succesful", Toast.LENGTH_LONG).show();
                            }

                        }))
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("EDIT")
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                // EditCardData(card.getTag().toString(),card);
                            }
                        })
                        .setTextColor(Color.BLACK))
                .setTitle("Mock Card " + z)
                .setDescription("This is a mock card")
                .setDrawable(R.drawable.ic_contact)
                .endConfig()
                .build();
        return card;
    }

    public void AnimatorTypeWorker() {
        mListView.setItemAnimator(new SlideInUpAnimator());
        mListView.getItemAnimator().setAddDuration(1000);
        mListView.getItemAnimator().setRemoveDuration(1000);
    }

    public Bitmap retrieveContactPhoto(Context context, String number) {

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        if (permissionCheck==PackageManager.PERMISSION_GRANTED) {


            ContentResolver contentResolver = context.getContentResolver();
            String contactId = null;
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

            Cursor cursor =
                    contentResolver.query(
                            uri,
                            projection,
                            null,
                            null,
                            null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }

            Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_contact);

            try {
                if (contactId != null) {
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactId)));

                    if (inputStream != null) {
                        photo = BitmapFactory.decodeStream(inputStream);
                    }

                    if (inputStream != null)
                        inputStream.close();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return photo;
        }
        else{
            Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_contact);
            return photo;
        }
    }

    public String getPhoneNumber(Long eventID) {
        Log.d(TAG, "MessageService started 1");
        CurrentMainDatabaseIndex = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String[] numbers = new String[250];

        ContactDatabaseMain databaseMain = new ContactDatabaseMain();
        ContactDatabaseReference databaseReference = new ContactDatabaseReference();

        long databaseMinimumRange, databaseMaximumRange;

        boolean foundNullFinishValueAtStart = false;
        Long NullEndValue = 1l, index = 1l;
        while (!foundNullFinishValueAtStart) {
            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, index);
            index++;
            if (databaseMain == null) {
                NullEndValue++;
                // Log.d(TAG, "Stuck 1");
            } else {
                foundNullFinishValueAtStart = true;

            }
        }
        databaseMinimumRange = NullEndValue;

        databaseMaximumRange = CurrentMainDatabaseIndex.getLong("CURRENTMAINDATABASEINDEX", -1);

        //  Log.d(TAG, "datbaseMax Range is in service " + databaseMaximumRange);
        //  Log.d(TAG, "datbaseMin Range is in service " + databaseMinimumRange);


        int i;
        //  Log.d(TAG, "Entering Loop");
        boolean foundEventID = false;

        for (i = (int) databaseMinimumRange; i <= (int) databaseMaximumRange; i++) {
            //  Log.d(TAG, "In the for loop");
            if (foundEventID == true) {
                break;
            }

            databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, ((long) i));
            if (databaseMain == null) continue;

            if (databaseMain.Eventid == eventID) {
                int x = 0, rowNumber = i;
                boolean foundEnd = false;
                numbers[x] = databaseMain.Number;
                x++;
                foundEventID = true;

                while (!foundEnd) {
                    rowNumber++;
                    if (rowNumber > databaseMaximumRange) break;
                    databaseMain = ContactDatabaseMain.findById(ContactDatabaseMain.class, (long) rowNumber);
                    if (databaseMain == null) continue;
                    if (databaseMain.Eventid != eventID) {
                        //     Log.d(TAG, "We Have a Break in the Service null ");
                        foundEnd = true;
                        break;
                    }

                    numbers[x] = databaseMain.Number;
                    x++;
                    //
                    //
                    //   Log.d(TAG, "Number array value is " + databaseMain.Number);

                }

            }

        }
        IsGroupMessage(numbers);
        return numbers[0];
    }

    public void IsGroupMessage(String[] numbers) {
        stateOfContactType = false;
        int i;
        //for (i=0;i<250;i++) {Log.d(TAG, String.valueOf(numbers[i]));}
        if (numbers[1] != null) stateOfContactType = true;
        //Log.d(TAG, String.valueOf(stateOfContactType));
    }

}
