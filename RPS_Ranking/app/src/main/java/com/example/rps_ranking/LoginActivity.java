package com.example.rps_ranking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText et_id;
    private EditText et_password;
    private Button bt_login;
    private TextView tv_register;
    private TextView tv_withdrawal;
    private AlertDialog dialog;
    private boolean isLogin = false;

    private User player;

    private final String RESPONSE_CODE = "success";
    private final String MSG_EMPTY_CHECK = "아이디와 비밀번호를 확인 해주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //회원탈퇴
        tv_withdrawal = (TextView) findViewById(R.id.tv_withdrawal);
        tv_withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserRemoveActivity.class);
                startActivity(intent);
            }
        });

        //회원가입
        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        et_id = (EditText) findViewById(R.id.et_id);
        et_password = (EditText) findViewById(R.id.et_password);

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_id.getText().toString();
                String userPassword = et_password.getText().toString();

                if (userId.equals("") || userPassword.equals("")) {
                    dialogPrint(builder, MSG_EMPTY_CHECK);
                    return;
                } else {
                    isLogin = true;
                }
                
                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (!isLogin) {
                                return;
                            }

                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                            if (success) {
                                String id = jsonObject.getString("userId");
                                int bestScore = Integer.parseInt(jsonObject.getString("score"));

                                player = new User(id, bestScore);

                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();

                                rankRequest();
                            } else {
                                dialogPrint(builder, MSG_EMPTY_CHECK);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(userId, userPassword, listener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    private void rankRequest() {
        Response.Listener<String> rankListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<User> rankers = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject rank = jsonArray.getJSONObject(i);

                        String id = rank.get("id").toString();
                        int score = Integer.parseInt(rank.get("score").toString());

                        User ranker = new User(id, score);
                        rankers.add(ranker);
                    }

                    GameData gameData = new GameData(player, rankers.get(0), rankers.get(1), rankers.get(2));
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("user", gameData);

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        RankRequest rankRequest = new RankRequest(rankListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(rankRequest);
    }

    private void dialogPrint(AlertDialog.Builder builder, String msg) {
        String PASS = "확인";
        dialog = builder.setMessage(msg).setNegativeButton(PASS, null).create();
        dialog.show();
    }
}