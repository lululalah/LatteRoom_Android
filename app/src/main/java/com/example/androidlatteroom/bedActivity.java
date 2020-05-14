package com.example.androidlatteroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class bedActivity extends AppCompatActivity {

    // 접속할 서버주소 상수.
    private static String host = "70.12.60.99";

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;



    class SharedObject {
        private Object MONITOR = new Object();
        private LinkedList<String> list = new LinkedList<String>();
        Gson gson = new Gson();
        SharedObject() {
        } // 생성자

        public void put(String s) {
            synchronized (MONITOR) {
                list.addLast(s);
                Log.i("ArduinoTest", "공용객체에 데이터 입력");
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
                    } catch (Exception e) {
                        Log.i("ArduinoTest", e.toString());
                    }
                } else {
                    result = list.removeFirst();
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
        public void send(LatteMessage msg) {
            try {
                String temp = gson.toJson(msg);
                pr.println(temp);
                pr.flush();

            } catch (Exception e) {
                Log.i("test", e.toString());
            }


        }


    }// SharedObject class end

    private SharedObject shared = new SharedObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed);

        ImageButton bed30btn = (ImageButton) findViewById(R.id.bed30btn);
        ImageButton bed45btn = (ImageButton) findViewById(R.id.bed45btn);
        ImageButton bed90btn = (ImageButton) findViewById(R.id.bed90btn);

        Button testBtn = findViewById(R.id.testBtn);
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DeviceSettingService.getDevice();
//                com.example.androidlatteroom.Message msg =
//                        DeviceSettingService.makeMessage("bed","degree");
//                Log.i("test","---------------");
//                Log.i("test",msg.getDeviceID());
//                Log.i("test",msg.getDataType());
//                Log.i("test",msg.getJsonData());
            }
        });

        final TextView bedSetting = findViewById(R.id.bedSetting);
        bed30btn.setOnClickListener((v)->{
            Thread t = new Thread(()->{

                SensorData data = new SensorData("bedMotor","On","30");
                LatteMessage msg = new LatteMessage(data);
                shared.send(msg);

            });
            t.start();
        });

        bed45btn.setOnClickListener((v)->{
            Thread t = new Thread(()->{
                SensorData data = new SensorData("BedMotor","On","45");
                LatteMessage msg = new LatteMessage(data);
                shared.send(msg);

            });
            t.start();
        });
        bed90btn.setOnClickListener((v)->{
            Thread t = new Thread(()->{
                SensorData data = new SensorData("BedMotor","On","90");
                LatteMessage msg = new LatteMessage(data);
                shared.send(msg);
            });
            t.start();
        });
        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String result = "";

                // 로직 처리 예시
                 if ((result = msg.getData().getString("BedDeg")) != null) {
                     bedSetting.setText(result + "°");
                 }
                // else if ((result = msg.getData().getString("hopeTmp")) != null) {
                //     climate_sbValue.setText(result + "°C");
                // } else if ((result = msg.getData().getString("status")) != null) {
                //     climate_status.setText(result);
                // } else if ((result = msg.getData().getString("deviceStatus")) != null) {
                //     climateCondition.setText(result);
                // }

            }
        };

        Thread t = new Thread(() -> {
            try {
                socket = new Socket(host, 55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
                // 서버에서 계속 받아오는 Thread
                GetDataBed runnable = new GetDataBed(br, shared, handler);
                Thread getData = new Thread(runnable);
                getData.start();

                // 처음 서버 접속에 보내줄 데이터 밑에 정의
                //공유객체안에 매서드를 생성하여 보내는 형식으로 사용.
                //shared.connectServer();
                //shared.checkDeviceState();
            } catch (IOException e) {
                Log.i("test", e.toString());
            }
        });
        t.start();

    }// onCreate() end
}// Activity class end

class GetDataBed implements Runnable {
    private String msg;

    private BufferedReader br;
    private Handler handler;
    private bedActivity.SharedObject shared;

    GetDataBed(BufferedReader br,
               bedActivity.SharedObject shared, Handler handler) {
        this.br = br;
        this.shared = shared;
        this.handler = handler;
    }
    Gson gson  = new Gson();


    @Override
    public void run() {

        try {

            String code = "";
            String value = "";
            String test;
            // 서버에서 받아온 문자에 따라 Activity에 보내줄 코드와 값을 정의하여 handler에 넣는다.
            while ((msg = br.readLine()) != null) {

                Message message = new Message();
                Bundle bundle = new Bundle();
                Log.i("test", msg);

                LatteMessage msgJson = gson.fromJson(msg,LatteMessage.class);



//                Log.i("test", msg);
//                Log.i("json", Msgjson.getJsonData());
                SensorData sensordata =gson.fromJson(msgJson.getJsonData(), SensorData.class);



                if("On".equals(sensordata.getStates())){
                    bundle.putString("BedDeg",sensordata.getStateDetail());
                    message.setData(bundle);
                }

//                // 추후 Gson 을 이용하여 받아온 json을 풀어쓰는 형태로 바꿀 예정.
//                if (msg.split(",").length == 2) {
//                    code = msg.split(",")[0];
//                    value = msg.split(",")[1];
//                }
//
//                if("BedDeg".equals(code)){
//                    bundle.putString(code,value);
//                    message.setData(bundle);
//                }


                handler.sendMessage(message);
            }
        } catch (Exception e) {
            Log.i("test", e.toString());
        }
    }
}// getDataClimate class end