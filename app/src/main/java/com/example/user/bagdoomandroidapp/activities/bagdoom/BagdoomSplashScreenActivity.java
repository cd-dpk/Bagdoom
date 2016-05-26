package com.example.user.bagdoomandroidapp.activities.bagdoom;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.bagdoomandroidapp.R;
import com.example.user.bagdoomandroidapp.activities.installation.PhoneVerificationStep1Activity;

public class BagdoomSplashScreenActivity extends AppCompatActivity {

    private static int splashInterval = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        final boolean bool = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent intent = new Intent(BagdoomSplashScreenActivity.this, PhoneVerificationStep1Activity.class);
                startActivity(intent);
                this.finish();
            }

            private void finish() {
            }
        }, splashInterval);
    }
    ;
}
