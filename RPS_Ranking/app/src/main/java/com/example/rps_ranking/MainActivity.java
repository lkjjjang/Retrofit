package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Random;

import kotlin.jvm.internal.PropertyReference0Impl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int STANDARD_TIME = 1000;
    private final int GAME_LEVEL_SET = 100;
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
    private final String ZERO_POINT = "0";
    private final String RESPONSE_CODE = "success";
    private final int VIBRATOR_TIME = 200;

    private ImageView iv_count;
    private ImageView iv_computer;
    private ImageView iv_start;
    private ImageView iv_scissors;
    private ImageView iv_paper;
    private ImageView iv_rock;
    private ImageView iv_user;
    private ImageView iv_result;

    private TextView tv_1st_id;
    private TextView tv_1st_score;
    private TextView tv_2nd_id;
    private TextView tv_2nd_score;
    private TextView tv_3rd_id;
    private TextView tv_3rd_score;
    private TextView tv_player_bestScore;
    private TextView tv_player_score;

    private Button bt_menu;

    private GameData gameData;
    private int userCard;
    private int computerCard;
    private int score;
    private int result;
    private int round;
    private long backKeyPressedTime = 0;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        gameData = (GameData) intent.getSerializableExtra("user");

        rankScorePrint();

        //사용될 뷰 초기화
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

        bt_menu = (Button) findViewById(R.id.bt_menu);
        bt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, How.class);
                startActivity(intent);
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

    private void selectWinner() {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                setUserCardTouch(false);

                if (userCard == 0) {
                    setLoseCase();
                    return;
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
                timeSleep(STANDARD_TIME - round * GAME_LEVEL_SET);
                handler.sendEmptyMessage(0);
            }
        }

        WinRun winRun = new WinRun();
        Thread thread = new Thread(winRun);
        thread.start();
    }

    private void updateScore() {
        if (score <= gameData.getPlayer().getBestScore()) {
            return;
        }

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                    if (!success) {
                        updateScore();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        String id = gameData.getPlayer().getId();
        String updateScore = Integer.toString(score);

        ScoreUpdateRequest scoreUpdateRequest = new ScoreUpdateRequest(id, updateScore, listener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(scoreUpdateRequest);
    }

    Handler computerActionHandler = new Handler(Looper.getMainLooper()) {
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

                    timeSleep(STANDARD_TIME - round * GAME_LEVEL_SET);

                    if (i == 1) { // 컴퓨터가 선택하는 시점
                        computerActionHandler.sendEmptyMessage(0);
                    }
                }
            }
        }

        CountDownThread countDownThread = new CountDownThread();
        Thread thread = new Thread(countDownThread);
        thread.start();
    }

    private void setWinCase() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(this.VIBRATOR_TIME);
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
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(this.VIBRATOR_TIME);

        updateScore();

        this.round = 0;
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

    private void setScore(int score) {
        this.score += score;
        String result = Integer.toString(this.score);
        tv_player_score.setText(result);
    }

    private void clearScore() {
        this.score = 0;
        tv_player_score.setText("0");
    }

    public static void timeSleep(int time) {
        if (time < 1) {
            time = 1;
        }

        if (time > 1000) {
            time = 1000;
        }

        try {
            sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void rankScorePrint() {
        tv_1st_id = (TextView) findViewById(R.id.tv_1st_id);
        tv_1st_id.setText(this.gameData.getRank_1st().getId());

        tv_1st_score = (TextView) findViewById(R.id.tv_1st_score);
        String score = Integer.toString(this.gameData.getRank_1st().getBestScore());
        tv_1st_score.setText(score);

        tv_2nd_id = (TextView) findViewById(R.id.tv_2nd_id);
        tv_2nd_id.setText(this.gameData.getRank_2nd().getId());

        tv_2nd_score = (TextView) findViewById(R.id.tv_2nd_score);
        score = Integer.toString(this.gameData.getRank_2nd().getBestScore());
        tv_2nd_score.setText(score);

        tv_3rd_id = (TextView) findViewById(R.id.tv_3rd_id);
        tv_3rd_id.setText(this.gameData.getRank_3rd().getId());

        tv_3rd_score = (TextView) findViewById(R.id.tv_3rd_score);
        score = Integer.toString(this.gameData.getRank_3rd().getBestScore());
        tv_3rd_score.setText(score);

        tv_player_bestScore = (TextView) findViewById(R.id.tv_player_bestScore);
        score = Integer.toString(this.gameData.getPlayer().getBestScore());
        tv_player_bestScore.setText(score);

        tv_player_score = (TextView) findViewById(R.id.tv_player_score);
        tv_player_score.setText(this.ZERO_POINT);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
}