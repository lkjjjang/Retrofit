package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView tv_id;
    private TextView tv_password;
    private ImageView iv_count;
    private ImageView iv_computer;



    private Handler countHandler;
    private Handler computerActionHandler;
    private Thread thread1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        showCountDown();



    }
    private void showComputerAction() throws InterruptedException {
        computerActionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Random random = new Random();
                int randomNum = random.nextInt(3) + 1;
                int imgResource = 0;

                switch (randomNum) {
                    case 1:
                        imgResource = R.drawable.scissors;
                        break;
                    case 2:
                        imgResource = R.drawable.rock;
                        break;
                    case 3:
                        imgResource = R.drawable.paper;
                        break;
                    default:
                        break;
                }

                iv_computer = (ImageView) findViewById(R.id.iv_computer);
                iv_computer.setImageResource(imgResource);
            }
        };

        class ComputerActionThread implements Runnable {
            @Override
            public void run() {
                computerActionHandler.sendEmptyMessage(0);
            }
        }

        ComputerActionThread computerActionThread = new ComputerActionThread();
        Thread actionThread = new Thread(computerActionThread);
        actionThread.start();

    }

    private void showCountDown() {
        countHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                iv_count = (ImageView) findViewById(R.id.iv_count);
                int imgResource = 0;

                switch (msg.arg1) {
                    case 0:
                        imgResource = 0;
                        break;
                    case 1:
                        imgResource = R.drawable.one;
                        break;
                    case 2:
                        imgResource = R.drawable.two;
                        break;
                    case 3:
                        imgResource = R.drawable.three;
                        break;
                    default:
                        break;
                }

                iv_count.setImageResource(imgResource);
            }
        };

        class CountDownThread implements Runnable {
            @Override
            public void run() {
                for (int i = 3; i >= 0; i--) {
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = i;
                    countHandler.sendMessage(message);
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        CountDownThread countDownThread = new CountDownThread();
        Thread thread = new Thread(countDownThread);
        thread.start();


    }
}