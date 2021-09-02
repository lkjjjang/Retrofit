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
    private final int IMG_READY = R.drawable.ready;
    private final int IMG_WIN = R.drawable.win;
    private final int IMG_LOSE = R.drawable.lose;
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
    private ImageView iv_result;

    private Handler computerActionHandler;
    private Handler userActionHandler;
    private int userCard;
    private int computerCard;
    private int score;
    private int result;
    private int round;

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

        //사용될 이미지 초기화
        iv_result = (ImageView) findViewById(R.id.iv_win);
        iv_count = (ImageView) findViewById(R.id.iv_count);
        iv_computer = (ImageView) findViewById(R.id.iv_computer);
        iv_user = (ImageView) findViewById(R.id.iv_user);

        iv_start = (ImageView) findViewById(R.id.iv_start);
        iv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_start.setImageResource(0);
                iv_start.setEnabled(false);
                startGame();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        // 버튼 잠금 메소드 추가
        switch (view.getId()) {
            case R.id.iv_scissors:
                iv_user.setImageResource(IMG_SCISSORS);
                userCard = CARD_TYPE_SCISSORS;
                break;
            case R.id.iv_rock:
                iv_user.setImageResource(IMG_ROCK);
                userCard = CARD_TYPE_ROCK;
                break;
            case R.id.iv_paper:
                iv_user.setImageResource(IMG_PAPER);
                userCard = CARD_TYPE_PAPER;
                break;
            default:
                break;
        }

        setUserCardTouch(false);
    }

    private void setScore(int score) {
        this.score += score;
        String result = Integer.toString(this.score);
        tv_score.setText(result);
    }

    private void clearScore() {
        this.score = 0;
        tv_score.setText("0");
    }

    private void timeSleep(int time) {
        try {
            sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectWinner() {
        userActionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                setUserCardTouch(false);
                if (userCard == 0) {
                    setLoseCase();
                }

                int matchResult = userCard - computerCard;

                if (matchResult == -2 || matchResult == 1) {
                    result = WIN_POINT;
                } else if (matchResult == -1 || matchResult == 2) {
                    result = LOSE_POINT;
                } else {
                    result = DRAW_POINT;
                }

                if (result == LOSE_POINT) {
                    setLoseCase();
                } else {
                    setWinCase();
                }

            }
        };

        class WinRun implements Runnable {
            @Override
            public void run() {
                timeSleep(1000);
                userActionHandler.sendEmptyMessage(0);
            }
        }

        WinRun winRun = new WinRun();
        Thread thread = new Thread(winRun);
        thread.start();
    }

    private void setWinCase() {
        this.round++;
        setScore(result);
        iv_result.setImageResource(IMG_WIN);

        onStop();

        iv_result.setEnabled(true);
        iv_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setScreen();
                startGame();
            }
        });
    }

    private void setLoseCase() {
        iv_result.setImageResource(IMG_LOSE);

        onStop();

        iv_result.setEnabled(true);
        iv_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearScore();
                setScreen();
                startGame();
            }
        });
    }

    private void setScreen() {
        userCard = 0;
        iv_computer.setImageResource(IMG_READY);
        iv_user.setImageResource(IMG_READY);
        iv_result.setImageResource(0);
        iv_result.setEnabled(false);
    }

    private void createUserCard() {
        iv_paper = (ImageView) findViewById(R.id.iv_paper);
        iv_rock = (ImageView) findViewById(R.id.iv_rock);
        iv_scissors = (ImageView) findViewById(R.id.iv_scissors);

        iv_paper.setOnClickListener(this);
        iv_rock.setOnClickListener(this);
        iv_scissors.setOnClickListener(this);
    }

    private void setUserCardTouch(boolean bool) {
        iv_scissors.setEnabled(bool);
        iv_rock.setEnabled(bool);
        iv_paper.setEnabled(bool);
    }

    private void showComputerAction() {
        computerActionHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                Random random = new Random();
                int randomNum = random.nextInt(3) + 1;

                switch (randomNum) {
                    case 1:
                        iv_computer.setImageResource(IMG_SCISSORS);
                        computerCard = CARD_TYPE_SCISSORS;
                        break;
                    case 2:
                        iv_computer.setImageResource(IMG_ROCK);
                        computerCard = CARD_TYPE_ROCK;
                        break;
                    case 3:
                        iv_computer.setImageResource(IMG_PAPER);
                        computerCard = CARD_TYPE_PAPER;
                        break;
                    default:
                        break;
                }

                createUserCard();
                setUserCardTouch(true);
                selectWinner();
            }
        };
    }

    private void startGame() {
        Handler countHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.arg1) {
                    case 0:
                        iv_count.setImageResource(0);
                        break;
                    case 1:
                        iv_count.setImageResource(IMG_ONE);
                        break;
                    case 2:
                        iv_count.setImageResource(IMG_TWO);
                        break;
                    case 3:
                        iv_count.setImageResource(IMG_THREE);
                        break;
                    default:
                        break;
                }
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

                    timeSleep(1000);

                    if (i == 1) { // 컴퓨터가 선택하는 시점
                        showComputerAction();
                        computerActionHandler.sendEmptyMessage(0);
                    }
                }
            }
        }

        CountDownThread countDownThread = new CountDownThread();
        Thread thread = new Thread(countDownThread);
        thread.start();
    }
}