package com.slashstudios.nicknagi.texter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by nicknagi on 14-03-2015.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        EditText etTime = (EditText) getActivity().findViewById(R.id.etTime);
        String epochTime = String.valueOf(etTime.getText());

        int hour = 0, minute = 0;

        if (epochTime.equals("")) {
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
        } else {
            String a = epochTime.substring(0, 2);
            String b = epochTime.substring(3, 5);
            hour = Integer.parseInt(a);
            minute = Integer.parseInt(b);

        }

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        /*Global g =((Global)getActivity().getApplicationContext());
        g.setSendHOUR(hourOfDay);
        g.setSendMINUTE(minute);
        Toast.makeText(getActivity().getApplicationContext(), "time set", Toast.LENGTH_LONG).show(); */
        Contact ContactObject = ((Contact) getActivity().getApplicationContext());
        ContactObject.setSendHOUR(hourOfDay);
        ContactObject.setSendMINUTE(minute);
        //Toast.makeText(getActivity().getApplicationContext(), ContactObject.getNameArrayValue(0), Toast.LENGTH_LONG).show();
        EditText etTime = (EditText) getActivity().findViewById(R.id.etTime);
        if (minute == 0) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "00" + ":00");
        } else if (minute == 1) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "01" + ":00");
        } else if (minute == 2) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "02" + ":00");
        } else if (minute == 3) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "03" + ":00");
        } else if (minute == 4) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "04" + ":00");
        } else if (minute == 5) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "05" + ":00");
        } else if (minute == 6) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "06" + ":00");
        } else if (minute == 7) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "07" + ":00");
        } else if (minute == 8) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "08" + ":00");
        } else if (minute == 9) {
            etTime.setText(ContactObject.getSendHOUR() + ":" + "09" + ":00");
        } else {
            etTime.setText(ContactObject.getSendHOUR() + ":" + ContactObject.getSendMINUTE() + ":00");
        }
    }


}
