package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class climateActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;

    class SharedObject{
        private Object MONITOR = new Object();
        private int i; //seekBar에서 설정한 온도

        SharedObject(){}

        public void put(int i){
            synchronized (MONITOR){
                this.i=i;
                Log.i("ArduinoTest", "공용객체에 데이터 입력");
                MONITOR.notify();
            }
        }

        public int pop(){
            int result = 0;
            synchronized (MONITOR){
                result = this.i;
                Log.i("ArduinoTest", "공용객체에서 데이터 추출");
            }
            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);

        SeekBar climate_seekBar = findViewById(R.id.climate_seekBar);
        TextView climate_sbValue = findViewById(R.id.climate_sbValue); //희망온도설정
        TextView climateSensorValue = findViewById(R.id.climateSensorValue); //현재온도

        final SharedObject shared = new SharedObject();

        climate_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                setClimate(i);
                climate_sbValue.append( i + "°C");
                shared.put(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        }); // end setOnSeekBarChangeListener()

        Runnable r = () -> {
            try {
                socket = new Socket("70.12.60.111", 55566);
                br = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
                Log.i("ServerTest", "서버에 접속성공");

                while(true){
                    int i = shared.pop();
                    pr.println(i);
                    pr.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        Thread t = new Thread(r);
        t.start();


    } //end onCreate

    private void setClimate(int value) {
        if (value < 10) {
            value = 10;
        } else if (value > 40) {
            value = 40;
        }
    }



}



