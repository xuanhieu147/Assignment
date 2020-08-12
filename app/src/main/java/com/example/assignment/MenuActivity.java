package com.example.assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private Button btnYeuThich, btnPhongCanh, btnGai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu2);
        btnYeuThich = findViewById(R.id.btnYeuThich);
        btnPhongCanh = findViewById(R.id.btnPhongCanh);
        btnGai = findViewById(R.id.btnGaiXinh);
        btnYeuThich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnPhongCanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,PhongCanhActivity.class);
                startActivity(intent);
            }
        });
        btnGai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this,GaiXinhActivity.class);
                startActivity(intent);
            }
        });
    }
}