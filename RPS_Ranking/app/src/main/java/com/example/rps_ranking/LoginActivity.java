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

public class LoginActivity extends AppCompatActivity {

    private EditText et_id;
    private EditText et_password;
    private Button bt_login;
    private TextView tv_register;
    private AlertDialog dialog;
    private boolean isLogin = false;

    private final String RESPONSE_CODE = "success";
    private final String MSG_EMPTY_CHECK = "아이디와 비밀번호를 확인 해주세요";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                                String pass = jsonObject.getString("userPassword");

                                Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                User user = new User(id, pass, 1, "email");
                                intent.putExtra("user", user);
                                startActivity(intent);
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

    private void dialogPrint(AlertDialog.Builder builder, String msg) {
        String PASS = "확인";
        dialog = builder.setMessage(msg).setNegativeButton(PASS, null).create();
        dialog.show();
    }
}