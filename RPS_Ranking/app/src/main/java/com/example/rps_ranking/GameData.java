package com.example.rps_ranking;

import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 2L;

    private final User rank_1st;
    private final User rank_2nd;
    private final User rank_3rd;
    private final User player;

    public GameData(User player, User rank_1st, User rank_2nd, User rank_3rd) {
        this.player = player;
        this.rank_1st = rank_1st;
        this.rank_2nd = rank_2nd;
        this.rank_3rd = rank_3rd;
    }
    
    public User getPlayer() {
        return this.player;
    }

    public User getRank_1st() {
        return rank_1st;
    }

    public User getRank_2nd() {
        return rank_2nd;
    }

    public User getRank_3rd() {
        return rank_3rd;
    }
}
