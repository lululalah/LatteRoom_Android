package com.example.androidlatteroom;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class climateActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climate);


    Runnable r = ()->{
        try {
            socket = new Socket("70.12.60.111",55566);
            br = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            pr = new PrintWriter(socket.getOutputStream());
            Log.i("ServerTest","서버에 접속성공");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    }
}
