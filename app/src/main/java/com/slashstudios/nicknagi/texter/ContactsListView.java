package com.slashstudios.nicknagi.texter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import java.util.ArrayList;

public class ContactsListView extends ActionBarActivity {
    private static String TAG = "Inchoo.net tutorial";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list_view);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //toolbar.setNavigationIcon(R.drawable.ic_action);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        final Contact ContactObject = ((Contact) getApplicationContext());
        final DynamicListView lvSelectedContacts = (DynamicListView) findViewById(R.id.lvSelectedContacts);
        String[] Values;
        Values = ContactObject.getName();
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 2; i < Values.length; i++) {
            if (Values[i] != null) {
                list.add(Values[i]);
            }
        }
        String[] Numbers;
        Numbers = ContactObject.getPhoneNumber();
        final ArrayList<String> numbers = new ArrayList<>();
        for (int i = 2; i < Numbers.length; i++) {
            if (Numbers[i] != null) {
                numbers.add(Numbers[i]);
            }
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);

        //lvSelectedContacts.setAdapter(adapter);
        // AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(adapter);
        //animationAdapter.setAbsListView(lvSelectedContacts);
        //lvSelectedContacts.setAdapter(animationAdapter);
        lvSelectedContacts.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions) {
                            //Toast.makeText(getApplicationContext(), adapter.getItem(position) + " " + numbers.get(position), Toast.LENGTH_SHORT).show();
                            /*for (dynamicPosition=0;dynamicPosition<250;dynamicPosition++){
                                if(Objects.equals(adapter.getItem(dynamicPosition), ContactObject.getNameArrayValue(dynamicPosition + 2))){
                                    position=dynamicPosition;
                                }
                             }*/

                            // Log all contact database values here
                            int z;
                            String[] array = new String[250];
                            array = ContactObject.getName();
                            String s;
                            for (z = 0; z < 250; z++) {
                                if (array[z] == null) s = "null";
                                else {
                                    s = array[z];
                                }
                                Log.d(TAG, s);
                            }

                            int i;
                            for (i = 0; i < 250; i++) {
                                if (numbers.get(position).equals(ContactObject.getPhoneNumberArrayValue(i))) {
                                    //Toast.makeText(getApplicationContext(), "We Found A Match at " + String.valueOf(i), Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            adapter.remove(ContactObject.getNameArrayValue(i));
                            //Toast.makeText(getApplicationContext(),"Deleting "+ ContactObject.getPhoneNumberArrayValue(i), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Deleted " + ContactObject.getNameArrayValue(i), Toast.LENGTH_SHORT).show();
                            ContactObject.setPhoneNumberArrayValue(null, i);
                            ContactObject.setNameArrayValue(null, i);
                            numbers.remove(position);
                            //fix this shit

                        }
                    }
                }
        );

        lvSelectedContacts.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts_list_view, menu);
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


}
