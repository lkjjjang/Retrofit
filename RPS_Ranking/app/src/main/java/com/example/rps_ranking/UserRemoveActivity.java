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

public class UserRemoveActivity extends AppCompatActivity {

    private EditText et_userId;
    private EditText et_userPassword;
    private EditText et_rePassword;
    private Button bt_register;

    private AlertDialog dialog;

    private final String RESPONSE_CODE = "success";
    private final String MSG_EMPTY_CHECK = "아이디와 비밀번호를 확인 해주세요";
    private final String MSG_REMOVE_USER = "회원정보를 삭제 하였습니다.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        et_userId = (EditText) findViewById(R.id.et_userId);
        et_userPassword = (EditText) findViewById(R.id.et_userPassword);
        et_rePassword = (EditText) findViewById(R.id.et_rePassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(UserRemoveActivity.this);

        bt_register = (Button) findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = et_userId.getText().toString();
                String password = et_userPassword.getText().toString();
                String rePassword = et_rePassword.getText().toString();

                if (id.equals("") || password.equals("") || rePassword.equals("")) {
                    dialogPrint(builder, MSG_EMPTY_CHECK);
                }

                if (!password.equals(rePassword)) {
                    dialogPrint(builder, MSG_EMPTY_CHECK);
                    return;
                }

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                            if (success) {
                                String serverId = jsonObject.getString("userId");
                                int serverPass = Integer.parseInt(jsonObject.getString("userPassword"));
                                int clientPass = Integer.parseInt(password);

                                User server = new User(serverId, serverPass);
                                User client = new User(id, clientPass);

                                if (client.equals(server)) {
                                    removeUser(id);
                                }
                            } else {
                                dialogPrint(builder, MSG_EMPTY_CHECK);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(id, password, listener);
                RequestQueue queue = Volley.newRequestQueue(UserRemoveActivity.this);
                queue.add(loginRequest);
            }
        });
    }

    private void removeUser(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UserRemoveActivity.this);

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                    if (success) {
                        dialogPrint(builder, MSG_REMOVE_USER);
                    } else {
                        dialogPrint(builder, MSG_EMPTY_CHECK);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        RemoveRequest loginRequest = new RemoveRequest(id, listener);
        RequestQueue queue = Volley.newRequestQueue(UserRemoveActivity.this);
        queue.add(loginRequest);
    }

    private void dialogPrint(AlertDialog.Builder builder, String msg) {
        String PASS = "확인";
        dialog = builder.setMessage(msg).setNegativeButton(PASS, null).create();
        dialog.show();
    }
}