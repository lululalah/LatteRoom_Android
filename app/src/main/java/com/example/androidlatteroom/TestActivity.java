package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TimePicker time = findViewById(R.id.TimePicker);
//        int i = time.getHour();
//
//        String value = String.valueOf();
//        Log.i("test");

    }
}
