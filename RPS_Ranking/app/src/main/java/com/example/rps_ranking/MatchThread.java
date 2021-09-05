package com.example.rps_ranking;

import android.os.Handler;

public class MatchThread extends Thread {
    private final Handler handler;

    public MatchThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        MainActivity.timeSleep(MainActivity.STANDARD_TIME - MainActivity.round * MainActivity.GAME_LEVEL_SET);
        handler.sendEmptyMessage(0);
    }
}
