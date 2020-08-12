package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Man_Hinh_Chao_Activity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); // set cho ảnh full màn hình

        setContentView(R.layout.activity_man__hinh__chao_);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();  // ẩn thanh actionBar

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Man_Hinh_Chao_Activity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT); // handler giúp lên lịch chuyển sang màn hình main sau 3s
    }
}
