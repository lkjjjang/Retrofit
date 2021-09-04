package com.example.rps_ranking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RankRequest extends StringRequest {

    final static private String URL = "https://lkjjjang85.cafe24.com/RankerScore.php";
    private final Map<String, String> parameters = new HashMap<>();

    public RankRequest(Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
    }

    @Override
    public Map<String, String> getParams() {
        return this.parameters;
    }

}