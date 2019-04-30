package com.example.hetzi_beta.Offers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.example.hetzi_beta.Offers.OfferDetailsPopupActivity;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    OfferDetailsPopupActivity launched_from;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        launched_from.offer_date.start_year = year;
        launched_from.offer_date.start_month = month + 1;
        launched_from.offer_date.start_day = day;
        launched_from.displayChosenDate(day, month + 1, year);
    }

    public void setLaunchingActivity(OfferDetailsPopupActivity launched_from) {
        this.launched_from = launched_from;
    }
}