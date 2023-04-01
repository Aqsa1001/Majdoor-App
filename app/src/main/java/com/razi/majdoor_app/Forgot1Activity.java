package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
            String email = emailEditText.getText().toString().trim();
            String otp = genOtp();
            if (email.isEmpty()) {
                Toast.makeText(Forgot1Activity.this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            } else {
                boolean isEmailSent = emailHandler.sendEmail(email, "Your One Time Password is:" + otp, "Your One Time Password is:" + otp, this);
                if (isEmailSent) {
                    Toast.makeText(Forgot1Activity.this, "OTP has been sent to your provided email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Forgot1Activity.this, "Something wet wrong, please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String genOtp() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format(new Locale("en-Us"), "%06d", number);
    }

}
