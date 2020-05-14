package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.Handler;
import android.os.Message;
import android.util.Log;
=======
import android.widget.Button;
>>>>>>> upstream/master
import android.widget.TimePicker;



import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;

public class alarmActivity extends AppCompatActivity {

    private static String host = "70.12.60.99";
    private static String deviceName = "Android";
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

    }

    private SharedObject shared = new SharedObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button alarm_confirm = (Button)findViewById(R.id.alarm_confirm);
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




    }// onCreate() end
}// Activity class end

class GetDataTimer implements Runnable {
    private String msg;

    private BufferedReader br;
    private Handler handler;
    private climateActivity.SharedObject shared;

    GetDataTimer(BufferedReader br,
                   climateActivity.SharedObject shared, Handler handler) {
        this.br = br;
        this.shared = shared;
        this.handler = handler;
    }


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

                // 추후 Gson 을 이용하여 받아온 json을 풀어쓰는 형태로 바꿀 예정.
                if (msg.split(",").length == 2) {
                    code = msg.split(",")[0];
                    value = msg.split(",")[1];
                }

                // 들어온 message에 따라 Activity로 보낼 코드값을 지정.
                // 예시 현재온도가 들어오면
                /* if ("curTmp".equals(code)) {
                    bundle.putString("curTmp", value);
                    message.setData(bundle);
                }*/

                handler.sendMessage(message);
            }
        } catch (Exception e) {
            Log.i("test", e.toString());
        }
    }
}// getDataClimate class end