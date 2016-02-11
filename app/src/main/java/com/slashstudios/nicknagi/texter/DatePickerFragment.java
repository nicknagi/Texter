package com.slashstudios.nicknagi.texter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by nicknagi on 14-03-2015.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        EditText etDate = (EditText) getActivity().findViewById(R.id.etDate);
        String epochDate = String.valueOf(etDate.getText());

        int year = 0, day = 0, month = 0;
        if (epochDate.equals("")) {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String a = epochDate.substring(0, 2);
            String b = epochDate.substring(3, 5);
            String c = epochDate.substring(6, 10);

            month = Integer.parseInt(a);
            month--;
            day = Integer.parseInt(b);
            year = Integer.parseInt(c);
        }


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        /*Global g =((Global)getActivity().getApplicationContext());
        g.setSendYEAR(year);
        g.setSendMONTH(month);
        g.setSendDAY(day);  */
        Contact ContactObject = ((Contact) getActivity().getApplicationContext());
        ContactObject.setSendYEAR(year);
        ContactObject.setSendMONTH(month);
        ContactObject.setSendDAY(day);
        String[] m;
        m = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


        //SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        // Calendar calendar = new GregorianCalendar(ContactObject.getSendYEAR(),ContactObject.getSendMONTH(),ContactObject.getSendDAY());
        EditText etDate = (EditText) getActivity().findViewById(R.id.etDate);
        month = month + 1;
        String monthString = String.valueOf(month);
        String dayString = String.valueOf(day);
        if (month < 10) {
            monthString = String.format("%02d", month);
            // plug in s in setext and do the same thing for timepicker
        }
        if (day < 10) {
            dayString = String.format("%02d", day);
        }
        etDate.setText(monthString + "/" + dayString + "/" + year);

    }
}
