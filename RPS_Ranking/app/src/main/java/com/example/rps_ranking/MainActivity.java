package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import kotlin.jvm.internal.PropertyReference0Impl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int COUNT_DOWN = 3;
    private final int IMG_ONE = R.drawable.one;
    private final int IMG_TWO = R.drawable.two;
    private final int IMG_THREE = R.drawable.three;
    private final int IMG_ROCK = R.drawable.rock;
    private final int IMG_PAPER = R.drawable.paper;
    private final int IMG_SCISSORS = R.drawable.scissors;
    private final int CARD_TYPE_SCISSORS = 1;
    private final int CARD_TYPE_ROCK = 2;
    private final int CARD_TYPE_PAPER = 3;
    private final int WIN_POINT = 2;
    private final int LOSE_POINT = 0;
    private final int DRAW_POINT = 1;

    private TextView tv_id;
    private TextView tv_password;
    private TextView tv_1st;
    private TextView tv_score;
    private TextView tv_1st_score;
    private ImageView iv_count;
    private ImageView iv_computer;
    private ImageView iv_start;
    private ImageView iv_scissors;
    private ImageView iv_paper;
    private ImageView iv_rock;
    private ImageView iv_user;


    private Handler countHandler;
    private Handler computerActionHandler;
    private Handler userActionHandler;
    private boolean isStart;
    private int userCard;
    private int computerCard;
    private int score;
    private int result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("user");

        tv_score = (TextView) findViewById(R.id.tv_score);

        tv_1st = (TextView) findViewById(R.id.tv_1st);
        tv_1st.setText("lkj");

        tv_1st_score = (TextView) findViewById(R.id.tv_1st_score);
        tv_1st_score.setText("10");

        iv_start = (ImageView) findViewById(R.id.iv_start);
        iv_start.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int imgResource = 0;
        int userCardType = 0;
        switch (view.getId()) {
            case R.id.iv_start:
                iv_start.setImageResource(0);
                isStart = true;
                showCountDown();
                return;
            case R.id.iv_scissors:
                imgResource = IMG_SCISSORS;
                userCardType = CARD_TYPE_SCISSORS;
                break;
            case R.id.iv_rock:
                imgResource = IMG_ROCK;
                userCardType = CARD_TYPE_ROCK;
                break;
            case R.id.iv_paper:
                imgResource = IMG_PAPER;
                userCardType = CARD_TYPE_PAPER;
                break;
            default:
                break;
        }

        iv_user = (ImageView) findViewById(R.id.iv_user);
        iv_user.setImageResource(imgResource);
        userCard = userCardType;
    }

    private void amountScore(int score) {
        this.score += score;
        String result = Integer.toString(this.score);
        tv_score.setText(result);
    }


    private void sleepStart() {
        try {
            sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectWinner() {
        userActionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (userCard == 0 || computerCard == 0) {
                    finish();
                }

                int match = userCard - computerCard;

                if (match == -2 || match == 1) {
                    result = WIN_POINT;
                } else if (match == -1 || match == 2) {
                    result = LOSE_POINT;
                } else {
                    result = DRAW_POINT;
                }

                if (result == LOSE_POINT) {
                    finish();
                }

                amountScore(result);
                result = 0;
            }
        };

        class TimeSleep implements Runnable {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    userActionHandler.sendEmptyMessage(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        TimeSleep timeSleep = new TimeSleep();
        Thread thread = new Thread(timeSleep);
        thread.start();
    }

    private void createUserCard() {
        iv_paper = (ImageView) findViewById(R.id.iv_paper);
        iv_rock = (ImageView) findViewById(R.id.iv_rock);
        iv_scissors = (ImageView) findViewById(R.id.iv_scissors);

        iv_paper.setOnClickListener(this);
        iv_rock.setOnClickListener(this);
        iv_scissors.setOnClickListener(this);
    }

    private void showComputerAction() {
        computerActionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Random random = new Random();
                int randomNum = random.nextInt(3) + 1;
                int imgResource = 0;

                switch (randomNum) {
                    case 1:
                        imgResource = IMG_SCISSORS;
                        computerCard = CARD_TYPE_SCISSORS;
                        break;
                    case 2:
                        imgResource = IMG_ROCK;
                        computerCard = CARD_TYPE_ROCK;
                        break;
                    case 3:
                        imgResource = IMG_PAPER;
                        computerCard = CARD_TYPE_PAPER;
                        break;
                    default:
                        break;
                }
                iv_computer = (ImageView) findViewById(R.id.iv_computer);
                iv_computer.setImageResource(imgResource);

                createUserCard();

                selectWinner(); //정해진 시간안에 패를 고르지않으면 패배 하는 메서드
            }
        };
    }

    private void showCountDown() {
        countHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                int imgResource = 0;

                switch (msg.arg1) {
                    case 0:
                        imgResource = 0;
                        break;
                    case 1:
                        imgResource = IMG_ONE;
                        break;
                    case 2:
                        imgResource = IMG_TWO;
                        break;
                    case 3:
                        imgResource = IMG_THREE;
                        break;
                    default:
                        break;
                }
                iv_count = (ImageView) findViewById(R.id.iv_count);
                iv_count.setImageResource(imgResource);
            }
        };

        class CountDownThread implements Runnable {
            @Override
            public void run() {
                // 반복문이 종료 되는 시점에서 카운트 이미지 지움
                for (int i = COUNT_DOWN; i >= 0; i--) {
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = i;
                    countHandler.sendMessage(message);

                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (i == 1) { // 컴퓨터가 선택하는 시점
                        showComputerAction();
                        computerActionHandler.sendEmptyMessage(0);
                    }
                }
            }
        }

        CountDownThread countDownThread = new CountDownThread();
        Thread countThread = new Thread(countDownThread);
        countThread.start();
    }
}