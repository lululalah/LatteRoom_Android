package com.example.androidlatteroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
//<<<<<<< HEAD
import android.os.Handler;
import android.os.Message;
import android.util.Log;
//=======
import android.util.DisplayMetrics;
//>>>>>>> upstream/master
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {

    private static final String DEVICE_ID = "Android01";
    private static final String DEVICE_TYPE = "USER";

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    private static String host = "70.12.60.105";
//    private static String host = "70.12.60.99";

    private String alarmDate;

    public static String getDeviceId() {
        return DEVICE_ID;
    }

    public static String getDeviceType() {
        return DEVICE_TYPE;
    }

    class SharedObject {
        private Object MONITOR = new Object();
        private LinkedList<String> list = new LinkedList<String>();
        private LinkedList<LatteMessage> msgList = new LinkedList<LatteMessage>();

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

        public void put(LatteMessage s) {
            synchronized (MONITOR) {
                msgList.addLast(s);
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

        public LatteMessage popMsg() {
            LatteMessage result = null;

            synchronized (MONITOR) {
                if (msgList.isEmpty()) {
                    // 리스트 안에 문자열이 없으니까 일시 대기해야 한다.
                    try {
                        MONITOR.wait();
                        // 큐 구조에서 가져옴
                        result = msgList.removeFirst();
                    } catch (Exception e) {
                        Log.i("ArduinoTest", e.toString());
                    }
                } else {
                    result = msgList.removeFirst();
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

                pr.println(gson.toJson(msg));
                pr.flush();

            } catch (Exception e) {
                Log.i("test", e.toString());
            }


        }

    }


    SharedObject shared = new SharedObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceI = new Intent(getApplicationContext(), DeviceSettingService.class);
        startService(serviceI);
        final TextView mainTemperature = findViewById(R.id.mainTemperature);
        final TextView mainDate = findViewById(R.id.mainDate);
        final TextView mainHumidity = findViewById(R.id.mainHumidity);
        final TextView AlarmActivity = findViewById(R.id.AlarmActivity);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String result = "";

                if ((result = msg.getData().getString("TEMP")) != null) {
                    mainTemperature.setText(result);
                }else if ((result = msg.getData().getString("LIGHT")) != null) {
                      mainHumidity.setText(result);
                }else if ((result = msg.getData().getString("DATE")) != null) {
                    mainDate.setText(result);
                }else if((result = msg.getData().getString("ALERT")) != null){
                    AlarmActivity.setText(result);
                }



            }
        };
        Thread timeSetting = new Thread(() -> {
            while (true) {
                Date nowTime = new Date(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd(E)");
                Bundle bundle = new Bundle();
                Message msg = new Message();
                bundle.putString("DATE",sdf.format(nowTime));
                msg.setData(bundle);
                handler.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timeSetting.start();

        Thread t = new Thread(() -> {
            try {
                socket = new Socket(host, 55566);

                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
//                LatteMessage initData;
                // 초기 셋팅값 서버에 요청
                //
                pr.println(getDeviceId());
                pr.println(getDeviceType());
                shared.put(new LatteMessage(getDeviceId(), "SensorList", null));

//                initData = new LatteMessage("Temperature");
                shared.put(new LatteMessage("TEMP"));
                shared.put(new LatteMessage("ALERT"));
                shared.put(new LatteMessage("LIGHT"));
                //
                //
                //
                //shared.send();


                Thread getMsg = new Thread(() -> {
                    String fromServer = "";
                    while (true) {

                        try {
                            if (!((fromServer = br.readLine()) != null)) break;
                            Log.i("start", fromServer);
                            Bundle bundle = new Bundle();
                            Message message = new Message();


                            // 여기서 핸들러에 데이터 넣는 처리.
//
                            try{
                                LatteMessage msg = gson.fromJson(fromServer, LatteMessage.class);
                                SensorData data = gson.fromJson(msg.getJsonData(), SensorData.class);
                              Log.i("check",msg.toString());
//                            bundle.putString(data.getSensorID(), data.getStates());
//                            알람처리
                                String code = data.getSensorID();
                                String value = "";
                                if ("ALERT".equals(data.getSensorID())) {
                                    value = data.getStateDetail();
//                                    bundle.putString("ALERT", data.getStateDetail());
//                                    message.setData(bundle);
//                                    Log.i("check",data.getStateDetail());
                                }else if("TEMP".equals(data.getSensorID())){
                                    value = data.getStates();
//                                    bundle.putString("TEMP",data.getStates());
//                                    message.setData(bundle);
//                                    Log.i("check",data.getStateDetail());
                                }else if("LIGHT".equals(data.getSensorID())){
                                    value = data.getStateDetail();
//                                    bundle.putString("LIGHT",data.getStateDetail());
//                                    message.setData(bundle);
//                                    Log.i("check",data.getStateDetail());
                                }
                                bundle.putString(code,value);
                                message.setData(bundle);
                            message.setData(bundle);
                            handler.sendMessage(message);
//
//                            Log.i("start", msg.getJsonData());
                            }catch(Exception e2){
                                Log.i("start",e2.toString());
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                getMsg.start();

                while (true) {
                    LatteMessage msg = shared.popMsg();
                    shared.send(msg);
                }

            } catch (IOException e) {

            }

        });
        t.start();

        ImageButton main_light = (ImageButton) findViewById(R.id.main_light);
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

        ImageButton main_bed = (ImageButton) findViewById(R.id.main_bed);
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


        ImageButton main_thermo = (ImageButton) findViewById(R.id.main_thermo);
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



        AlarmActivity.setOnClickListener(new View.OnClickListener() {
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

        Switch AlarmSwitch = findViewById(R.id.AlarmSwitch);
        AlarmSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(AlarmSwitch.isChecked()){


//                    Alert time = new Alert();

//                    LatteMessage msg = new LatteMessage(time);
//                    shared.put(msg);
                }
            }

        });

/*
* // 스위치 버튼입니다.
        SwitchButton switchButton = (SwitchButton) findViewById(R.id.AlarmSwitch);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           // 스위치 버튼이 체크되었는지 검사하여 텍스트뷰에 각 경우에 맞게 출력합니다.
                if (isChecked){
                   // 알람켜기

                }else{
                   // 알람끄기

                }
            }
        });
* */

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        shared.put(new LatteMessage("TEMP"));
        shared.put(new LatteMessage("ALERT"));
        shared.put(new LatteMessage("LIGHT"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3000 && resultCode == 7000) {
            String msg = (String) data.getExtras().get("ResultValue");

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}