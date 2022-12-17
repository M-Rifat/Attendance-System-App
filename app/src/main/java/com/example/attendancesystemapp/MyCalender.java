package com.example.attendancesystemapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyCalender extends DialogFragment {
    Calendar calender = Calendar.getInstance();
    public interface OnCalendarOkClickListener{
        void onClick(int year, int month, int day);
    }
    public OnCalendarOkClickListener onCalendarOkClickListener;

    public void setOnCalendarOkClickListener (OnCalendarOkClickListener onCalendarOkClickListener){
        this.onCalendarOkClickListener = onCalendarOkClickListener;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(),((view, year, month, dayOfMonth) -> {
                onCalendarOkClickListener.onClick(year,month,dayOfMonth);
        }),calender.get(Calendar.YEAR),calender.get(Calendar.MONTH),calender.get(Calendar.DAY_OF_MONTH));
    }
    void setDate(int year,int month,int day){
         calender.set(Calendar.YEAR,year);
        calender.set(Calendar.MONTH,month);
        calender.set(Calendar.DAY_OF_MONTH,day);

    }
     String getDate(){
        return DateFormat.format("dd.MM.yyyy",calender).toString();
     }
}
