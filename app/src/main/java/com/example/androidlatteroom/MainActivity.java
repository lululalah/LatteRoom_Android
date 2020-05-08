package com.example.androidlatteroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
=======
import android.annotation.SuppressLint;
>>>>>>> upstream/master
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.widget.ImageView;
=======
import android.widget.Button;
import android.widget.Toast;
>>>>>>> upstream/master

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        final ImageView light = findViewById(R.id.mainLight);
        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName("com.example.androidlatteroom",
                        "com.example.androidlatteroom.LightActivity");
=======
        Button main_light = (Button)findViewById(R.id.main_light);
        main_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.lightActivity");
>>>>>>> upstream/master
                i.setComponent(cname);
                startActivity(i);
            }
        });

<<<<<<< HEAD
=======
        Button main_bed = (Button)findViewById(R.id.main_bed);
        main_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.bedActivity");
                i.setComponent(cname);
                startActivity(i);
            }
        });

        Button main_alarm = (Button)findViewById(R.id.main_alarm);
        main_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.alarmActivity");
                i.setComponent(cname);
                startActivity(i);
            }
        });

        Button main_thermo = (Button)findViewById(R.id.main_thermo);
        main_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.thermoActivity");
                i.setComponent(cname);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==3000 && resultCode==7000){
            String msg = (String)data.getExtras().get("ResultValue");

            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }
>>>>>>> upstream/master
    }
}
