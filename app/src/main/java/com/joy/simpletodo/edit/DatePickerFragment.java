package com.joy.simpletodo.edit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.joy.simpletodo.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Joy on 2017/2/6.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "DatePickerFragment";
    private static final boolean DEBUG = MainActivity.DEBUG;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final GregorianCalendar calendar = new GregorianCalendar();
        // Get the item's due date.
        if (savedInstanceState != null) {
            Date date = (Date) savedInstanceState.getSerializable(EditItemActivity.BUNDLE_EXTRA_DATE);
            if (date != null) {
                calendar.setTime(date);
            }
        }

        return new DatePickerDialog(getActivity(), this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (DEBUG)
            Log.i(TAG, "onDateSet year=" + year + ", month=" + month + ", day=" + dayOfMonth);
        if (getActivity() instanceof EditItemActivity) {
            ((EditItemActivity) getActivity()).onSetDueDateInDialog(year, month, dayOfMonth);
        }
    }
}
