package com.example.rps_ranking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ScoreUpdateRequest extends StringRequest {

    final static private String URL = "https://lkjjjang85.cafe24.com/ScoreRegister.php";
    private final Map<String, String> parameters;

    public ScoreUpdateRequest(String userId, String score, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userId);
        parameters.put("score", score);
    }

    @Override
    public Map<String, String> getParams() {
        return this.parameters;
    }

}
