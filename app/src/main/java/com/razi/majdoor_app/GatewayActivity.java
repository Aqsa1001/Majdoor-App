package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GatewayActivity extends AppCompatActivity {
    Button jazzcashBtn,easypaisaBtn;
    String actionStr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        jazzcashBtn = findViewById(R.id.jazzcashBtn);
        easypaisaBtn = findViewById(R.id.easypaisaBtn);


        if (getIntent().hasExtra("Deposit")) {
            actionStr = getIntent().getStringExtra("Deposit");
        }
        if (getIntent().hasExtra("Withdrawal")) {
            actionStr = getIntent().getStringExtra("Withdrawal");
        }

        jazzcashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GatewayActivity.this, GatewayDetailActivity.class);
                intent.putExtra("action",actionStr);
                startActivity(intent);
            }
        });
        easypaisaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GatewayActivity.this, GatewayDetailActivity.class);
                intent.putExtra("action",actionStr);
                startActivity(intent);            }
        });
    }
}