package com.example.hetzi_beta.BusinessApp.EditOffers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.example.hetzi_beta.BusinessApp.EditOffers.OfferDetailsPopupActivity;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    OfferDetailsPopupActivity launched_from;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        launched_from.offer_date.start_hour = hourOfDay;
        launched_from.offer_date.start_minute = minute;
        launched_from.displayChosenTime(launched_from.offer_date.start_hour, launched_from.offer_date.start_minute);
        launched_from.checkEnablePublishButton();
    }

    public void setLaunchingActivity(OfferDetailsPopupActivity launched_from) {
        this.launched_from = launched_from;
    }
}