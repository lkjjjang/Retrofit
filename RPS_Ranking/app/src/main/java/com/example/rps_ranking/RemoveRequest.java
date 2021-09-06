package com.example.rps_ranking;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RemoveRequest extends StringRequest {

    final static private String URL = "https://lkjjjang85.cafe24.com/UserRemove.php";
    private final Map<String, String> parameters;

    public RemoveRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userId", userID);
    }

    @Override
    public Map<String, String> getParams() {
        return this.parameters;
    }

}
