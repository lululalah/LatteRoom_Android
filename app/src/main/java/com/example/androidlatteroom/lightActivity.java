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

public class LightActivity extends AppCompatActivity {
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
            pr.println(msg);
            pr.flush();

        }

    }

    private SharedObject shared = new SharedObject();
    private Button on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        final TextView lightPower = (TextView) findViewById(R.id.lightPower);
        Button onBtn = findViewById(R.id.lightOn);
        Button offBtn = findViewById(R.id.lightOff);

        //lightPower.setText("msg");

        Thread t = new Thread(() -> {
            try {
                socket = new Socket("70.12.60.94", 55566);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                pr = new PrintWriter(socket.getOutputStream());

                Thread getData = new Thread(() -> {
                    try {
                        String msg = "";
                        while ((msg = br.readLine()) != null) {

                            Log.i("test", "!!!!");
                            Log.i("test", msg);
                            curTmp = msg;
                            if (!"Off".equals(curTmp)) {
                                curtmp = Integer.valueOf(curTmp);
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


                });
                getData.start();

            } catch (IOException e) {
            }
        });

        t.start();

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


        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String preTmp = String.valueOf(curtmp);
                sb.setProgress(curtmp);
                lightPower.setText(preTmp);
                Thread t = new Thread(() -> {
                    shared.send(preTmp);
                });
                t.start();
            }
        });
        offBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb.setProgress(0);
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
