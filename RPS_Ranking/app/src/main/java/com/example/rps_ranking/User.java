package com.example.rps_ranking;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final int bestScore;

    public User(String id, int bestScore) {
        this.id = id;
        this.bestScore = bestScore;
    }

    public String getId() {
        return id;
    }

    public int getBestScore() {
        return this.bestScore;
    }
}
