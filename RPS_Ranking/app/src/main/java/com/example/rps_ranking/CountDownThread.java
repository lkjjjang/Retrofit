package com.example.rps_ranking;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;

public class CountDownThread implements Runnable {

    private Handler countHandler;

    public CountDownThread(Handler handler) {
        this.countHandler = handler;
    }

    @Override
    public void run() {
        for (int i = 3; i >= 0; i--) {
            Message message = new Message();
            message.what = 0;
            message.arg1 = i;
            countHandler.sendMessage(message);
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Handler getCountHandler() {
        return this.countHandler;
    }
}
