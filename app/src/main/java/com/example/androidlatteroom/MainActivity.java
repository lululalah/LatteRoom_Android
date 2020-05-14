package com.example.androidlatteroom;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceI = new Intent(getApplicationContext(),DeviceSettingService.class);
        startService(serviceI);




        ImageButton main_light = (ImageButton)findViewById(R.id.main_light);
        main_light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.lightActivity");
                i.setComponent(cname);
                startActivity(i);
            }
        });

        ImageButton main_bed = (ImageButton)findViewById(R.id.main_bed);
        main_bed.setOnClickListener(new View.OnClickListener() {
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

//        ImageButton main_alarm = (ImageButton)findViewById(R.id.alarm_confirm);
//        main_alarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent();
//                ComponentName cname = new ComponentName
//                        ("com.example.androidlatteroom",
//                                "com.example.androidlatteroom.alarmActivity");
//                i.setComponent(cname);
//                startActivity(i);
//            }
//        });

        ImageButton main_thermo = (ImageButton)findViewById(R.id.main_thermo);
        main_thermo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                ComponentName cname = new ComponentName
                        ("com.example.androidlatteroom",
                                "com.example.androidlatteroom.climateActivity");
                i.setComponent(cname);
                startActivity(i);
            }
        });
//<<<<<<< HEAD
////<<<<<<< HEAD
//        //Button test = findViewById(R.id.test);
////=======
////        Button test = findViewById(R.id.test);
////>>>>>>> upstream/master
//=======
//
////Button test = findViewById(R.id.test);
//>>>>>>> upstream/master
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent();
//                ComponentName cname = new ComponentName("com.example.androidlatteroom",
//                        "com.example.androidlatteroom.TestActivity");
//                i.setComponent(cname);
//                startActivity(i);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==3000 && resultCode==7000){
            String msg = (String)data.getExtras().get("ResultValue");

            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
        }
    }
}