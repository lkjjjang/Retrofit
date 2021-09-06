package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;

public class CountDownThread extends Thread {
    private final Handler countHandler;
    private final Handler computerActionHandler;

    public CountDownThread(Handler countHandler, Handler computerActionHandler) {
        this.countHandler = countHandler;
        this.computerActionHandler = computerActionHandler;
    }

    @Override
    public void run() {
        // 반복문이 종료 되는 시점에서 카운트 이미지 지움
        int time = MainActivity.STANDARD_TIME - MainActivity.round * MainActivity.GAME_LEVEL_SET;

        for (int i = MainActivity.COUNT_DOWN; i >= 0; i--) {
            Message message = new Message();
            message.what = 0;
            message.arg1 = i;
            this.countHandler.sendMessage(message);

            MainActivity.timeSleep(time);

            if (i == 1) { // 컴퓨터가 선택하는 시점
                this.computerActionHandler.sendEmptyMessage(0);
            }
        }
    }
}
