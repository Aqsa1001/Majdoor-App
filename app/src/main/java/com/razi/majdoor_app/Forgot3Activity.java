package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Forgot3Activity extends AppCompatActivity {
    EditText passField, confPassField;
    Button resetButton;
    String emailTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot3);

        if (getIntent().hasExtra("email")) {
            emailTemp = getIntent().getStringExtra("email");

        } else {
            Log.i("Forgot3Activity", "Data Not Received From 2 ");
        }

        passField = findViewById(R.id.passField);
        confPassField = findViewById(R.id.confPassField);
        resetButton = findViewById(R.id.resetBtn);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passField.getText().toString().isEmpty()) {
                    passField.setError("Enter Password");
                    passField.requestFocus();
                    return;
                } else if (confPassField.getText().toString().isEmpty()) {
                    confPassField.setError("Enter Confirm Password");
                    confPassField.requestFocus();
                    return;
                } else if (!(passField.getText().toString().equals(confPassField.getText().toString()))) {
                    passField.setError("Confirm Password not matched with Password");
                    passField.requestFocus();
                    return;
                } else if (passField.getText().toString().length() < 8) {
                    passField.setError("Enter At least 8 characters");
                    passField.requestFocus();
                    return;
                } else {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String email = snapshot.child(auth.getCurrentUser().getUid()).child("email").getValue(String.class);
                            if (email.equals(emailTemp)) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users").child(auth.getCurrentUser().getUid()).child("password").setValue(passField.getText().toString());
                                user.updatePassword(passField.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(Forgot3Activity.this, "Auth Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Forgot3Activity.this, "Auth Not Updated!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                Toast.makeText(Forgot3Activity.this, "Man Changed Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Forgot3Activity.this, SigninActivity.class));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Forgot3Activity.this, "Man Not Updated!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}