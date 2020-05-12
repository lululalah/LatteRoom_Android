package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class lightActivity extends AppCompatActivity {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;
    private String curTmp = "";
    private int curtmp = 0;

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

            }catch(Exception e){
                Log.i("test",e.toString());
            }


        }

    }

    private SharedObject shared = new SharedObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        final TextView lightPower = (TextView) findViewById(R.id.lightPower);
        Button onBtn = findViewById(R.id.lightOn);
        Button offBtn = findViewById(R.id.lightOff);

        //lightPower.setText("msg");



        SeekBar sb = (SeekBar) findViewById(R.id.lightSeekbar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            Thread t;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                t = new Thread(() -> {
                    shared.send(String.valueOf(progress));

                });


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                t.start();
            }
        });

        Thread t = new Thread(() -> {
            try {
                socket = new Socket("70.12.60.94", 55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());



                // 해당 Runnable 객체에 BufferedRead와 변경되어야 하는 컴포넌트들을 주입시킨후
                // 해당 Runnable 객체에서 메서드를 이용하여 컴포넌트 값들의 값을 서버로 전송 or 받기.
                GetDataLight getdataR = new GetDataLight(br,lightPower,curtmp,sb,shared);
                Thread getDataT = new Thread(getdataR);
//                Thread getData = new Thread(() -> {
//                    try {
//                        String msg = "";
//                        while ((msg = br.readLine()) != null) {
//
//                            Log.i("test", "!!!!");
//                            Log.i("test", msg);
//                            curTmp = msg;
//                            if (!"Off".equals(curTmp)) {
//                                curtmp = Integer.valueOf(curTmp);
//                            }
//                            lightPower.setText(msg + "`");
//
//
//                        }
//                    } catch (IOException e) {
//                        Log.i("test", e.toString());
////                        try {
////                            br.close();
////                            pr.close();
////                            socket.close();
////                        } catch (IOException ex) {
////                            ex.printStackTrace();
////                        }
//
//                    }
//
//
//                });
                getDataT.start();

            } catch (IOException e) {
            }
        });

        t.start();
//        connServer conn = new connServer(socket,br,pr,lightPower,this.curtmp);
//        Thread t = new Thread(conn);
//        t.start();


        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread(() -> {
                    shared.send("On");
                });
                t.start();
            }
        });
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sb.setProgress(0);
                lightPower.setText("0");
                Thread t = new Thread(() -> {
                    shared.send("Off");
                });
                t.start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("test", " " + socket.isConnected());

    }
}

//class connServer implements Runnable {
//    private Socket socket;
//    private BufferedReader br;
//    private PrintWriter pr;
//
//    private TextView lightPower;
//    private int setData;
//
//    connServer(Socket socket,BufferedReader br,PrintWriter pr,TextView lightPower,int setData){
//        this.socket = socket;
//        this.br = br;
//        this.pr = pr;
//        this.lightPower =lightPower;
//        this.setData = setData;
//    }
//
//
//    @Override
//    public void run() {
//        try {
//            socket = new Socket("70.12.60.94", 55566);
//            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            pr = new PrintWriter(socket.getOutputStream());
//            Log.i("test","connect");
//            GetData getdata = new GetData(this.br,this.lightPower,this.setData);
//           Thread t = new Thread(getdata);
//            t.start();
//
//
//    }catch (IOException e){
//        }
//    }
//
//
//}


class GetDataLight implements Runnable{
    private String msg;
    private String getData;
    private BufferedReader br;
    private TextView lightPower;
    private SeekBar sb;
    private Object shared;
    private int setData;

    GetDataLight(BufferedReader br,TextView lightPower,int setData,SeekBar sb,Object shared){
        this.br = br;
        this.lightPower = lightPower;
        this.setData = setData;
        this.sb = sb;
        this.shared = shared;
    }


    @Override
    public void run() {
        try {
            Log.i("test","doGetData");
            Log.i("test",String.valueOf(setData));
            while ((msg = br.readLine()) != null) {

                Log.i("test", "!!!!");
                Log.i("test", msg);
                getData = msg;



                if (!"Off".equals(getData)) {

                    setData = Integer.valueOf(getData);
                }
                if("On".equals(getData)){
                    //sb.setProgress(100);
                }else if("Off".equals(getData)){
                    Log.i("test","이전 온도"+Integer.toString(setData));
                    sb.setProgress(0);
                }
                lightPower.setText(msg + "`");


            }
        } catch (IOException e) {
            Log.i("test", e.toString());
//                        try {
//                            br.close();
//                            pr.close();
//                            socket.close();
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }

        }
    }
}
