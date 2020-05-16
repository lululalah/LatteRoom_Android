package com.example.androidlatteroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.LinkedList;

public class alarmActivity extends AppCompatActivity {
/*
*
* 출처 http://susemi99.kr/732/
* https://kd3302.tistory.com/63
*
* public class MainActivity extends Activity
{
   private static final String BASE_PATH = Environment.getExternalStorageDirectory() + "/myapp";
   private static final String NORMAL_PATH = BASE_PATH + "/normal";

   private AlarmManager _am;

   private ToggleButton _toggleSun, _toggleMon, _toggleTue, _toggleWed, _toggleThu, _toggleFri, _toggleSat;

   @Override
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      _am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
      _toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
      _toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
      _toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
      _toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
      _toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
      _toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
      _toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);


   }

   public void onRegist(View v)
   {
      Log.i("MainActivity.java | onRegist", "|" + "========= regist" + "|");

      boolean[] week = { false, _toggleSun.isChecked(), _toggleMon.isChecked(), _toggleTue.isChecked(), _toggleWed.isChecked(),
            _toggleThu.isChecked(), _toggleFri.isChecked(), _toggleSat.isChecked() }; // sunday=1 이라서 0의 자리에는 아무 값이나 넣었음

      Intent intent = new Intent(this, AlarmReceiver.class);
*
*
* */
    private static String host = "70.12.60.99";
    private static String deviceName = "Android";
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();

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
        setContentView(R.layout.activity_alarm);

        //Button alarm_confirm = (Button)findViewById(R.id.alarm_confirm);
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

        int[] toggle_id = {
                R.id.toggle_sun, R.id.toggle_mon, R.id.toggle_tue,
                R.id.toggle_wed, R.id.toggle_thu, R.id.toggle_fri, R.id.toggle_sat
        };

        ToggleButton[] toggleList = new ToggleButton[7];

        for (int i=0 ; i<7 ; i++) {
            toggleList[i] = findViewById(toggle_id[i]);
        }




        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                String result = "";
                if((result = msg.getData().getString("sendSuc"))!=null){
                    Toast sendMsg = Toast.makeText(getApplicationContext(),
                            "알람정보 서버로 전송완료",Toast.LENGTH_SHORT);
                    sendMsg.show();

                }

            }
        };

        Thread t = new Thread(()->{
            try {
                socket = new Socket(host,55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());

                GetDataTimer runnable = new GetDataTimer(br,shared,handler);
                Thread getData = new Thread(runnable);
                getData.start();

                while(true){
                    Bundle bundle = new Bundle();
                    Message handleMessage = new Message();
                    LatteMessage msg = shared.popMsg();
                    shared.send(msg);
                    bundle.putString("sendSuc",msg.toString());
                    handleMessage.setData(bundle);
                    handler.sendMessage(handleMessage);
                }


            }catch(IOException e){

            }
        });



        t.start();

        Button sendToServer = findViewById(R.id.sendToServer);
        sendToServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String weeks = "";
                for (int j=0 ; j<7 ; j++) {
                    if(toggleList[j].isChecked()){

                        String day = toEngDay(toggleList[j].getText().toString());
                        weeks += day + ",";
                    }
                    if(j==6){
                        StringBuffer c = new StringBuffer(weeks);
                        c.deleteCharAt(c.length()-1);
                        weeks = c.toString();
                    }
                }
                int h=0;
                int m=0;

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    h = mTimePicker.getHour();
                    m = mTimePicker.getMinute();
                }


                Alert data = new Alert(h,m,weeks,true);
                LatteMessage msg = new LatteMessage(data);
                Log.i("Alert",msg.toString());
                shared.put(msg);
            }
        });

//        String[] weeks = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};








//        ToggleButton toggle_sun = findViewById(R.id.toggle_sun);
//
//        ToggleButton toggle_mon = findViewById(R.id.toggle_mon);
//
//        ToggleButton toggle_tue = findViewById(R.id.toggle_tue);
//
//        ToggleButton toggle_wed = findViewById(R.id.toggle_wed);
//
//        ToggleButton toggle_thu = findViewById(R.id.toggle_thu);
//
//        ToggleButton toggle_fri = findViewById(R.id.toggle_fri);
//
//        ToggleButton toggle_sat = findViewById(R.id.toggle_sat);







    }// onCreate() end
    public String toEngDay(String s){
        String result = "";

        if("일".equals(s)){
            result = "SUN";
        }
        if("월".equals(s)){
            result = "MON";
        }
        if("화".equals(s)){
            result = "TUE";
        }
        if("수".equals(s)){
            result = "WED";
        }
        if("목".equals(s)){
            result = "THU";
        }
        if("금".equals(s)){
            result = "FRI";
        }
        if("토".equals(s)){
            result = "SAT";
        }
        return result;
    }


}// Activity class end

class GetDataTimer implements Runnable {
    private String msg;

    private BufferedReader br;
    private Handler handler;
    private alarmActivity.SharedObject shared;

    GetDataTimer(){

    }

    GetDataTimer(BufferedReader br,
                 alarmActivity.SharedObject shared, Handler handler) {
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