package com.iset.listeproduits.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.iset.listeproduits.R;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        new CountDownTimer(2000, 1000){
            @Override
            public void onTick(long l) { }

            @Override
            public void onFinish() {
                startActivity(new Intent(IntroActivity.this, MainActivity.class));
            }
        }.start();
    }
}