package com.example.rps_ranking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.icu.util.ValueIterator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private final String MSG_REGISTER_SUCCESS = "회원가입 성공";
    private final String MSG_REGISTER_FAIL = "회원가입 실패";
    private final String MSG_CHECK_VALIDATE = "중복체크 해야 합니다.";
    private final String MSG_CHECK_EMPTY = "빈칸 없이 입력 해주세요";
    private final String MSG_CHECK_PASSWORD = "비밀번호를 확인 해주세요";
    private final String MSG_NOT_USED_ID = "사용할수 없는 아이디 입니다.";
    private final String MSG_USED_ID = "사용할수 있는 아이디 입니다.";
    private final String MSG_CHECK_EMAIL = "이메일을 확인 해주세요";

    private final String RESPONSE_CODE = "success";
    private final String EMPTY = "";

    private String userID;
    private String userPassword;
    private String userEmail;
    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText et_userId = (EditText) findViewById(R.id.et_userId);
        EditText et_userPassword = (EditText) findViewById(R.id.et_userPassword);
        EditText et_rePassword = (EditText) findViewById(R.id.et_rePassword);
        EditText et_userEmail = (EditText) findViewById(R.id.et_userEmail);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

        Button validateButton = (Button) findViewById(R.id.bt_validId);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_userId.getText().toString();

                if(validate) {
                    return;
                }

                if (userId.equals(EMPTY)) {
                    dialogPrint(builder, MSG_CHECK_EMPTY);
                    return;
                }

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                            if (success) {
                                dialogPrint(builder, MSG_USED_ID);
                                et_userId.setEnabled(false);
                                validate = true;
                            } else {
                                dialogPrint(builder, MSG_NOT_USED_ID);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(userId, listener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        Button registerButton = (Button) findViewById(R.id.bt_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = et_userId.getText().toString();
                String userPassword = et_userPassword.getText().toString();
                String userRePassword = et_rePassword.getText().toString();
                String userEmail = et_userEmail.getText().toString();

                if (!validate) {
                    dialogPrint(builder, MSG_CHECK_VALIDATE);
                    return;
                }

                if (userId.equals(EMPTY) || userEmail.equals(EMPTY) || userPassword.equals(EMPTY)) {
                    dialogPrint(builder, MSG_CHECK_EMPTY);
                    return;
                }

                if (!userRePassword.equals(userPassword)) {
                    dialogPrint(builder, MSG_CHECK_PASSWORD);
                    return;
                }

                if (!checkEmail(userEmail)) {
                    dialogPrint(builder, MSG_CHECK_EMAIL);
                    return;
                }

                Response.Listener<String> listener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean(RESPONSE_CODE);

                            if (success) {
                                dialogPrint(builder, MSG_REGISTER_SUCCESS);
                                finish();
                            } else {
                                dialogPrint(builder, MSG_REGISTER_FAIL);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(userId, userPassword, userEmail, listener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private boolean checkEmail(String email) {
        char[] chars = email.toCharArray();

        for(char ch : chars) {
            if (ch == '@') {
                return true;
            }
        }
        return false;
    }

    private void dialogPrint(AlertDialog.Builder builder, String msg) {
        String PASS = "확인";
        dialog = builder.setMessage(msg).setNegativeButton(PASS, null).create();
        dialog.show();
    }
}