package com.ng.techhouse.tinggqr;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.ng.techhouse.tinggqr.util.M;

/**
 * Created by rabiu on 24/02/2017.
 */

public  class MyBaseActivity extends AppCompatActivity {

    public final long DISCONNECT_TIMEOUT = 100000; // 5 min = 5 * 60 * 1000 ms

    private  Handler disconnectHandler = new Handler(){
        public void handleMessage(Message msg) {
        }
    };

    private static Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            M.logOut();
            System.out.println("Log out here  ");
            // Perform any required operation on disconnect
        }
    };

    public void resetDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer(){
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction(){
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}