package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;

public class ProgressThread extends Thread implements UserCardSelectCallback {
    private final Handler handler;
    private final int progressMax = 100;
    private boolean hasUserCard;

    public ProgressThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        int count = MainActivity.STANDARD_TIME - MainActivity.round * MainActivity.GAME_LEVEL_SET;

        for (int i = 0; i <= this.progressMax; i++) {
            if (this.hasUserCard) {
                return;
            }

            MainActivity.timeSleep(count / this.progressMax);

            Message message = new Message();
            message.what = 0;
            message.arg1 = i;
            this.handler.sendMessage(message);
        }
    }

    @Override
    public void onUserCardSelect(boolean bool) {
        this.hasUserCard = bool;
    }
}
