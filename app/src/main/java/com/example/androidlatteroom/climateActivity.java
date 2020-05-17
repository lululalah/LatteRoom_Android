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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import java.util.LinkedList;


public class climateActivity extends AppCompatActivity {

    //Gson gson = new Gson();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
    //    private static String host = "70.12.60.99";
    private static String host = "70.12.60.105";
//    private Socket socket;
//    private BufferedReader br;
//    private PrintWriter pr;

//    class SharedObject{
//        private Object MONITOR = new Object();
//        private int i; //seekBar에서 설정한 온도
//
//        SharedObject(){}
//
//        public void put(int i){
//            synchronized (MONITOR){
//                this.i=i;
//                Log.i("ArduinoTest", "공용객체에 데이터 입력");
//                MONITOR.notify();
//            }
//        }
//
//        public int pop(){
//            int result = 0;
//            synchronized (MONITOR){
//                result = this.i;
//                Log.i("ArduinoTest", "공용객체에서 데이터 추출");
//            }
//            return result;
//        }
//    }

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

    private SharedObject shared = new SharedObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);

        SeekBar climate_seekBar = findViewById(R.id.climate_seekBar);
        TextView climate_sbValue = findViewById(R.id.climate_sbValue); //희망온도설정
        TextView climateSensorValue = findViewById(R.id.climateSensorValue); //현재온도
//        TextView climateCondition = findViewById(R.id.climateCondition);


        //final SharedObject shared = new SharedObject();

        climate_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Thread t;
            LatteMessage msg;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //setClimate(i);
                //climate_sbValue.setText( i + "°C");
                climate_sbValue.setText(progress + "°C");

//                t = new Thread(() -> {
                SensorData data = new SensorData("TEMP", Integer.toString(progress));
                msg = new LatteMessage(data);
//                    shared.send("hopeTmp," + Integer.toString(progress));

                //Json 문자열로 변환
//                    sendMsg msg = new sendMsg(code,value);
//                    Log.i("test",msg.makeJson());
//                    shared.send(msg.makeJson());
//                });

                //shared.put(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                shared.put(msg);
//                t.start();
            }
        });

//        climateMSG = findViewById(R.id.climateSensorValue);
//        climateCondition = findViewById(R.id.climateCondition);
//        climate_status = findViewById(R.id.climate_sbValue);
//        climate_seekBar = findViewById(R.id.climate_seekBar);

        @SuppressLint("HandlerLeak") Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String result = "";

//                if ((result = msg.getData().getString("curTmp")) != null) {
//                    climateSensorValue.setText(result + "°C");
//                } else if ((result = msg.getData().getString("hopeTmp")) != null) {
//                    climate_sbValue.setText(result + "°C");
//                } else if ((result = msg.getData().getString("status")) != null) {
//                    climate_status.setText(result);
//                } else if ((result = msg.getData().getString("deviceStatus")) != null) {
//                    climateCondition.setText(result);
//                }

                if ((result = msg.getData().getString("TEMP")) != null) {
                    Log.i("climate", result);
                    climateSensorValue.setText(result + "°C");
                }

            }
        };

        Thread t = new Thread(() -> {
            try {
                socket = new Socket(host, 55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());
                GetDataClimate runnable = new GetDataClimate(br, shared, handler);
                Thread getData = new Thread(runnable);
                getData.start();

                shared.put(new LatteMessage("TEMP"));

                while (true) {
                    LatteMessage msg = shared.popMsg();
                    shared.send(msg);
                }
                //shared.send(new LatteMessage("ClimateSensor"));

            } catch (IOException e) {
                Log.i("test", e.toString());
            }

        });
        t.start();


//        Runnable r = () -> {
//            try {
//                socket = new Socket("70.12.60.111", 55566);
//                br = new BufferedReader(new InputStreamReader(
//                        socket.getInputStream()));
//                pr = new PrintWriter(socket.getOutputStream());
//                Log.i("ServerTest", "서버에 접속성공");
//
//                while(true){
//                    int i = shared.pop();
//                    pr.println(i);
//                    pr.flush();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        };
        //Thread t = new Thread(r);
        //t.start();


    } //end onCreate
//
//    private void setClimate(int value) {
//        if (value < 10) {
//            value = 10;
//        } else if (value > 40) {
//            value = 40;
//        }
//    }


}


class GetDataClimate implements Runnable {
    private String msg;
    private String getData;
    private BufferedReader br;
    //    private PrintWriter pr;
//    private TextView climateMSG;
//    private TextView climateCondition;
//    private TextView climate_status;
//    private SeekBar climate_seekBar;
    private Handler handler;
    private climateActivity.SharedObject shared;

    GetDataClimate(BufferedReader br,
                   climateActivity.SharedObject shared, Handler handler) {
        this.br = br;
//        this.pr = pr;
//        this.climateMSG = climateMSG;
//        this.climateCondition = climateCondition;
//        this.climate_status = climate_status;
//        this.climate_seekBar = climate_seekBar;
        this.shared = shared;
        this.handler = handler;

    }

    //Gson gson  = new Gson();
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

    @Override
    public void run() {

        try {


            String code = "";
            String value = "";
            String test;

            while ((msg = br.readLine()) != null) {
                Message message = new Message();
                Bundle bundle = new Bundle();

                LatteMessage msgJson = gson.fromJson(msg, LatteMessage.class);
                SensorData data = gson.fromJson(msgJson.getJsonData(), SensorData.class);


                Log.i("climate", data.toString());
                if ("TEMP".equals(data.getSensorID())) {
                    bundle.putString(data.getSensorID(), data.getStates());
                    message.setData(bundle);
                }
                handler.sendMessage(message);

            }
        } catch (Exception e) {
            Log.i("test", e.toString());
//

        }
    }
}

//class sendMsg{
//
//    String code;
//    String value;
//
//    static Gson gson = new Gson();
//
//
//    sendMsg(String code,String value){
//        this.code = code;
//        this.value = value;
//    }
//
//    public String makeJson(){
//        String msg = gson.toJson(new sendMsg(this.code,this.value));
//        return msg;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public String getValue() {
//        return value;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public void setValue(String value) {
//        this.value = value;
//    }
//}

