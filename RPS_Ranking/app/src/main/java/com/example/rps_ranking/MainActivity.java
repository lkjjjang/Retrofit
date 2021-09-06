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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Random;

import kotlin.jvm.internal.PropertyReference0Impl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static int STANDARD_TIME = 1000;
    public final static int GAME_LEVEL_SET = 10;
    public final static int COUNT_DOWN = 3;
    public static int round;

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
    private final int TIMEOUT = 100;

    private ImageView iv_count;
    private ImageView iv_computer;
    private ImageView iv_start;
    private ImageView iv_scissors;
    private ImageView iv_paper;
    private ImageView iv_rock;
    private ImageView iv_user;
    private ImageView iv_matchResult;

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
    //private int result;
    private long backKeyPressedTime = 0;
    private Toast toast;
    private ProgressBar progressBar;
    private ProgressThread progressThread;

    private boolean hasUserCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        gameData = (GameData) intent.getSerializableExtra("user");

        rankScorePrint();

        //사용될 뷰 초기화
        iv_matchResult = (ImageView) findViewById(R.id.iv_matchResult);
        iv_count = (ImageView) findViewById(R.id.iv_count);
        iv_computer = (ImageView) findViewById(R.id.iv_computer);
        iv_user = (ImageView) findViewById(R.id.iv_user);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

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

        this.hasUserCard = true;
        setUserCardTouch(false);
    }

    private void selectWinner() {
        this.hasUserCard = false;

        if (userCard == 0) {
            setLoseCase();
            return;
        }

        int matchResult = userCard - computerCard;

        if (matchResult == -2 || matchResult == 1) {
            score += WIN_POINT;
            setWinCase();
        } else if (matchResult == -1 || matchResult == 2) {
            setLoseCase();
        } else {
            score += DRAW_POINT;
            setWinCase();
        }
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

    private void startGame() {
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
                
                createUserCard();        // 유저카드 생성
                setUserCardTouch(true);  // 유저카드 활성화
                startProgressBar();      // 프로그래스바 종료 되면 selectWinner() 호출
            }
        };

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

        CountDownThread countDownThread = new CountDownThread(countHandler, computerActionHandler);
        countDownThread.start();
    }

    private void startProgressBar() {
        Handler progressHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // 제한시간내 유저 카드 선택 시 progressBar 정지 승자판별
                if (hasUserCard) {
                    UserCardSelectCallback callback = (UserCardSelectCallback) progressThread;
                    callback.onUserCardSelect(true);
                    selectWinner();
                }

                int millSecond = msg.arg1;
                progressBar.setProgress(millSecond);

                if (millSecond == TIMEOUT) {
                    setUserCardTouch(false);
                    selectWinner();
                }
            }
        };

        progressThread = new ProgressThread(progressHandler);
        progressThread.start();
    }

    private void setWinCase() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(this.VIBRATOR_TIME);

        round++;
        scorePrint();
        iv_matchResult.setImageResource(IMG_WIN);

        onStop();

        iv_matchResult.setEnabled(true);
        iv_matchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setProgress(0);
                setScreen();
                startGame();
            }
        });
    }

    private void setLoseCase() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(this.VIBRATOR_TIME);

        updateScore();

        round = 0;
        iv_matchResult.setImageResource(IMG_LOSE);

        onStop();

        iv_matchResult.setEnabled(true);
        iv_matchResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setProgress(0);
                clearScore();
                setScreen();
                startGame();
            }
        });
    }

    private void scorePrint() {
        String result = Integer.toString(this.score);
        tv_player_score.setText(result);

        if (this.score > gameData.getPlayer().getBestScore()) {
            tv_player_bestScore.setText(result);
        }
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
        iv_matchResult.setImageResource(0);
        iv_matchResult.setEnabled(false);
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
            toast = Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }
}