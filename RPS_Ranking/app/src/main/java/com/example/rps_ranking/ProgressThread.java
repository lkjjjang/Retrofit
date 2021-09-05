package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;

public class ProgressThread extends Thread {
    private final Handler handler;

    public ProgressThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        int count = MainActivity.STANDARD_TIME - MainActivity.round * MainActivity.GAME_LEVEL_SET;

        for (int i = 0; i <= 100; i++) {
            MainActivity.timeSleep(count / 100);

            Message message = new Message();
            message.what = 0;
            message.arg1 = i;
            this.handler.sendMessage(message);
        }

    }
}
