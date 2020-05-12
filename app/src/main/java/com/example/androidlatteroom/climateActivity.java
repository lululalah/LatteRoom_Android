package com.example.androidlatteroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.LinkedList;


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

    private TextView climateMSG;
    private TextView climateCondition;
    private TextView climate_status;
    private SeekBar climate_seekBar;
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;

    class SharedObject {
        private Object MONITOR = new Object();
        private LinkedList<String> list = new LinkedList<String>();

        SharedObject() {

        } // 생성자

        public void put(String s) {
            synchronized (MONITOR) {
                list.addLast(s);
                Log.i("test", "공용객체에 데이터 입력");
                // 리스트 안에 문자열이 없어 대기하던 pop 매서드를 꺠워서 실행시킨다.
                MONITOR.notify();
            }
        }

        public String pop() {
            String result = "";

            synchronized (MONITOR) {
                if (list.isEmpty()) {
                    // 리스트 안에 문자열이 없으니까 일시 대기해야 한다.
                    try {
                        MONITOR.wait();
                        // 큐 구조에서 가져옴
                        result = list.removeFirst();
                        send(result);
                    } catch (Exception e) {
                        Log.i("ArduinoTest", e.toString());
                    }
                } else {
                    result = list.removeFirst();
                    send(result);
                    Log.i("ArduinoTest", "공용객체에서 데이터 추출");
                }
            }
            return result;
        }

        public void send(String msg) {
            try {
                pr.println(msg);
                pr.flush();

            } catch (Exception e) {
                Log.i("test", e.toString());
            }


        }

        public void firstSetting() {
            while (!list.isEmpty()) {
                list.pop();
            }
        }

    }

    private SharedObject shared = new SharedObject();


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


        climateMSG = findViewById(R.id.climateMSG);
        climateCondition = findViewById(R.id.climateCondition);
        climate_status = findViewById(R.id.climate_status);
        climate_seekBar = findViewById(R.id.climate_seekBar);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String result = "";

                if ((result = msg.getData().getString("curTmp")) != null) {
                    climateMSG.setText(result);
                }
                if ((result = msg.getData().getString("hopeTmp")) != null) {
                    climateCondition.setText(result);
                }
                if ((result = msg.getData().getString("status")) != null) {
                    climate_status.setText(result);
                }

            }
        };

        Thread t = new Thread(() -> {
            try {
                socket = new Socket("70.12.60.94", 55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
                GetDataClimate runnable = new GetDataClimate(br, climateMSG, climateCondition,
                        climate_status, climate_seekBar, shared, handler);
                Thread getData = new Thread(runnable);
                getData.start();

            } catch (IOException e) {
                Log.i("test", e.toString());
            }
        });
        t.start();


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


class GetDataClimate implements Runnable {
    private String msg;
    private String getData;
    private BufferedReader br;
    //    private PrintWriter pr;
    private TextView climateMSG;
    private TextView climateCondition;
    private TextView climate_status;
    private SeekBar climate_seekBar;
    private Handler handler;
    private climateActivity.SharedObject shared;

    GetDataClimate(BufferedReader br, TextView climateMSG,
                   TextView climateCondition, TextView climate_status,
                   SeekBar climate_seekBar, climateActivity.SharedObject shared, Handler handler) {
        this.br = br;
//        this.pr = pr;
        this.climateMSG = climateMSG;
        this.climateCondition = climateCondition;
        this.climate_status = climate_status;
        this.climate_seekBar = climate_seekBar;
        this.shared = shared;
        this.handler = handler;

    }


    @Override
    public void run() {
        shared.put("curTmp,-25`");
        shared.put("hopeTmp,25`");
        shared.put("status,On");

        Thread t = new Thread(() -> {
            while (true) {

                shared.pop();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

//        pr.println();
//        pr.flush();
        //shared.send("this");
        try {


            String code = "";
            String value = "";
            t.start();
            while ((msg = br.readLine()) != null) {
                Message message = new Message();

                Bundle bundle = new Bundle();
//                bundle.putString("msg",msg);
//                message.setData(bundle);
//                handler.sendMessage(message);
//                    handler.post(()->{
//
//                        climateMSG.setText(msg);
//                    });
                Log.i("test", msg);

                if (msg.split(",").length == 2) {
                    code = msg.split(",")[0];
                    value = msg.split(",")[1];
                }
                //현재온도가 들어오면
                if ("curTmp".equals(code)) {
                    bundle.putString("curTmp", value);
                    message.setData(bundle);
//                    handler.sendMessage(message);
//                    handler.post(()->{
//
//                        climateMSG.setText(msg);
//                    });
//                    climateMSG.setText(value);
                }
                if ("hopeTmp".equals(code)) {
                    bundle.putString("hopeTmp", value);
                    message.setData(bundle);
//                    handler.sendMessage(message);
//                    handler.post(()->{
//
//                    climateCondition.setText(msg);
//                    });
                }
                if ("status".equals(code)) {
                    bundle.putString("status", value);
                    message.setData(bundle);
//                    handler.sendMessage(message);
//                    handler.post(()->{
//
//                        climate_status.setText(msg);
//                    });

                }
                handler.sendMessage(message);

            }
        } catch (Exception e) {
            Log.i("test", e.toString());
//

        }
    }
}

