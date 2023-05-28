package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Choose2Activity extends AppCompatActivity {

    ImageButton indBtn,contractBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose2);
        indBtn = findViewById(R.id.IndBtn);
        contractBtn = findViewById(R.id.contractbtn);



        indBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Choose2Activity.this, SigninActivity.class);
                intent.putExtra("Category","individual");
                startActivity(intent);
            }
        });
        contractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Choose2Activity.this, SigninActivity.class);
                intent.putExtra("Category","contract");
                startActivity(intent);
            }
        });
    }

}