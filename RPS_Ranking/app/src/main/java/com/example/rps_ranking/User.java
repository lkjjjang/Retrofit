package com.example.rps_ranking;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String password;
    private final String email;
    private int bestScore;
    private int score;

    public User(String id, String password, int bestScore, String email) {
        this.id = id;
        this.password = password;
        this.bestScore = bestScore;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBestScore() {
        return this.bestScore;
    }

    public void getBestScore(int score) {
        this.bestScore = score;
    }
}
