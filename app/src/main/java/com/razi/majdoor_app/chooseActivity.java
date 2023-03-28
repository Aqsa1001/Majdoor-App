package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class chooseActivity extends AppCompatActivity {

    ImageButton customerbtn,laborbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        customerbtn = findViewById(R.id.customerbtn);
        laborbtn = findViewById(R.id.laborbtn);



        customerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chooseActivity.this, SigninActivity.class);
                startActivity(intent);
            }
        });
        laborbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(chooseActivity.this, RecentProjectActivity.class);
                startActivity(intent);
            }
        });
    }


}