package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.M;


public class SplashScreen extends AppCompatActivity {

    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 4000; //3 second
    ConnectionDetector connectionDetector;
    ImageView splashImage;
    String message,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        connectionDetector = new ConnectionDetector(this);

        pushnotification();

        splashImage = (ImageView) findViewById(R.id.splashImage);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
        splashImage.setAnimation(anim);

            Handler handler = new Handler();

            // run a thread after 3 seconds to start the home screen
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if(!connectionDetector.isConnectingToInternet())
                    {
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                        Intent mIntent = new Intent(SplashScreen.this,Login.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);
                        bundle.putString("title", title);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        finish();
                    }else {
                        Intent mIntent = new Intent(SplashScreen.this,Login.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("message", message);
                        bundle.putString("title", title);
                        mIntent.putExtras(bundle);
                        startActivity(mIntent);
                        finish();

                    }
                }

            }, SPLASH_DURATION);

    }

    private void pushnotification(){

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("message") != null) {
                message = getIntent().getExtras().getString("message");
                title = getIntent().getExtras().getString("title");
            }
        }
    }


    @Override
    public void onBackPressed() {
        // set the flag to true so the next activity won't start up
        mIsBackButtonPressed = true;
        super.onBackPressed();

    }

}