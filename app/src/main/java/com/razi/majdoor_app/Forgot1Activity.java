package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Random;

public class Forgot1Activity extends AppCompatActivity {
    private EditText emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot1);

        emailEditText = findViewById(R.id.sendCodeEmail);
        Button sendButton = findViewById(R.id.sendCodeBtn);
        EmailHandler emailHandler = EmailHandler.getInstance();
        sendButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String emale = snapshot.child(auth.getCurrentUser().getUid()).child("email").getValue(String.class);
                    if (!(emale.equals(emailEditText.getText().toString()))) {
                        Toast.makeText(Forgot1Activity.this, "User Don't Exist. Register First!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Forgot1Activity.this, SignUpActivity.class));
                    } else {
                        Log.i("ID", auth.getCurrentUser().getUid());
                        String email = emailEditText.getText().toString().trim();
                        String otp = genOtp();
                        if (email.isEmpty()) {
                            emailEditText.setError("Email Can't Be Empty");
                            emailEditText.requestFocus();
                            return;
                        } else if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                            emailEditText.setError("Please Provide Valid Email");
                            emailEditText.requestFocus();
                            return;
                        } else {
                            boolean isEmailSent = emailHandler.sendEmail(email, "Your One Time Password is:" + otp, "Your One Time Password is:" + otp, getApplicationContext());
                            if (isEmailSent) {
                                Toast.makeText(Forgot1Activity.this, "OTP has been sent to your provided email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Forgot2Activity.class);
                                intent.putExtra("email", emailEditText.getText().toString());
                                intent.putExtra("code", otp);
                                startActivity(intent);

                            } else {
                                Toast.makeText(Forgot1Activity.this, "Something wet wrong, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    private String genOtp() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format(new Locale("en-Us"), "%06d", number);
    }

}