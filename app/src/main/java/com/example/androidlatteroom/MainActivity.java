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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {


    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    private static String host = "70.12.60.99";


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

        Intent serviceI = new Intent(getApplicationContext(),DeviceSettingService.class);
        startService(serviceI);

//<<<<<<< HEAD

        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

            }
        };

        Thread t = new Thread(()->{
            try {
                socket = new Socket(host,55566);

                br =  new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
                // 초기 셋팅값 서버에 요청
                //
                //LatteMessage initData = new LatteMessage("Temperature");
                //
                //
                //
                //shared.send();


                Thread getMsg = new Thread(()->{
                    String fromServer = "";
                    while (true) {
                        try {
                            if (!(( fromServer= br.readLine())!=null)) break;
                            Bundle bundle = new Bundle();
                            Message message = new Message();
                            // 여기서 핸들러에 데이터 넣는 처리.
                            try {
                                LatteMessage msg = gson.fromJson(fromServer, LatteMessage.class);
                                SensorData data = gson.fromJson(msg.getJsonData(), SensorData.class);
                            }catch(Exception e2) {
                                Log.i("Exception",e2.toString());
                            }

//
//=======
////        ImageView viewImg = findViewById(R.id.main_background);
////        DisplayMetrics metrics = new DisplayMetrics();
////        WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
////        manager.getDefaultDisplay().getMetrics(metrics);
////        ViewGroup.LayoutParams param = viewImg.getLayoutParams();
////        viewImg.setMaxHeight((int) (metrics.heightPixels*0.1)); //50%
//>>>>>>> upstream/master

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                while(true){
                    LatteMessage msg = shared.popMsg();
                    shared.send(msg);
                }
            }catch(IOException e){

            }

        });
//        t.start();

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

//         ImageButton main_exit = (ImageButton)findViewById(R.id.main_exit);
//        main_exit.setOnClickListener(new View.OnClickListener() {
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

        final TextView AlarmActivity = findViewById(R.id.AlarmActivity);
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

        ImageButton AlarmIcon = findViewById(R.id.AlarmIcon);

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