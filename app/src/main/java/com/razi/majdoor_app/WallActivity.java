package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WallActivity extends AppCompatActivity {
    TextView amountText;
    Button depositBtn, withBtn;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        amountText = findViewById(R.id.amountText);
        depositBtn = findViewById(R.id.depositBtn);
        withBtn = findViewById(R.id.withBtn);

        depositBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, GatewayActivity.class);
                intent.putExtra("Deposit","deposit");
                startActivity(intent);            }
        });
        withBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WallActivity.this, GatewayActivity.class);
                intent.putExtra("Withdrawal","withdrawal");
                startActivity(intent);
            }
        });

        // Get the user ID of the currently logged in user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setAmount(userId);

    }
    private void setAmount(String userId){
        // Get a reference to the user's wallet amount in the Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Wallet").child("ammount");

        // Attach a ValueEventListener to the database reference to retrieve the amount
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Get the amount value from the snapshot and cast it to float
                float amount = snapshot.getValue(Float.class);

                // Set the amount text in the TextView
                amountText.setText(String.format("%.2f", amount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Wallet", "Failed to read value.", error.toException());
            }
        });
    }

}