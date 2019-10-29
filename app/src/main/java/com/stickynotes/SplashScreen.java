package com.stickynotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            // USER IS ALREADY LOGGED IN
                            startActivity(
                                    Constants.getClearHistoryIntent(
                                            SplashScreen.this,
                                            MainActivity.class
                                    )
                            );
                        } else {
                            startActivity(
                                    Constants.getClearHistoryIntent(
                                            SplashScreen.this,
                                            LoginActivity.class
                                    )
                            );
                        }
                    }
                },
                4000
        );
    }
}
