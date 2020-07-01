package com.example.android.mvvm.Reminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.mvvm.R;

import java.sql.Time;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    //Get the current time
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.time_picker, null);

        //TimePickerDialog takes in the context of the activity, a Listener,
        // the time we want to show when we open it and whether it will be 24hrs
        //We set it to the user's settings(24 hr or not)
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), 3, (TimePickerDialog.OnTimeSetListener)getActivity(),
                hour, minute, DateFormat.is24HourFormat(getActivity()));
        //timePickerDialog.setView(view);

        return new TimePickerDialog(getActivity(),  (TimePickerDialog.OnTimeSetListener)getActivity(),
                hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
}
