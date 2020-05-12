package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

public class alarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TimePicker mTimePicker = (TimePicker) findViewById(R.id.timePicker);

        Calendar mCalendar = Calendar.getInstance();
        int hour,min;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            hour = mTimePicker.getHour();
            min = mTimePicker.getMinute();
        }else{
            hour = mTimePicker.getCurrentHour();
            min = mTimePicker.getCurrentMinute();
        }


    }
}
