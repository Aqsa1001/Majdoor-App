package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Random;

public class Forgot2Activity extends AppCompatActivity {
    String emailTemp, otpTemp;
    EditText otp1,otp2,otp3,otp4,otp5,otp6;
    String OTP;
    Button verifyBtn;
    TextView sendCodeAgain;
    EmailHandler emailHandler = EmailHandler.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot2);

        if (getIntent().hasExtra("email") && getIntent().hasExtra("code")) {
            emailTemp = getIntent().getStringExtra("email");
            otpTemp = getIntent().getStringExtra("code");
            Log.i("Email", emailTemp);
            Log.i("OTP", String.valueOf(otpTemp));

        } else {
            Log.i("Forgot2Activity", "Data Not Received From 1 ");
        }

        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp6 = findViewById(R.id.otp6);
        verifyBtn = findViewById(R.id.verifyBtn);
        sendCodeAgain = findViewById(R.id.sendCodeAgain);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otp1.getText().toString().isEmpty()){
                    otp1.setError("Missing");
                    otp1.requestFocus();
                    return;
                }
                else if(otp2.getText().toString().isEmpty()){
                    otp2.setError("Missing");
                    otp2.requestFocus();
                    return;
                }
                else if(otp3.getText().toString().isEmpty()){
                    otp3.setError("Missing");
                    otp3.requestFocus();
                    return;
                }
                else if(otp4.getText().toString().isEmpty()){
                    otp4.setError("Missing");
                    otp4.requestFocus();
                    return;
                }
                else if(otp5.getText().toString().isEmpty()){
                    otp5.setError("Missing");
                    otp5.requestFocus();
                    return;
                }
                else if(otp6.getText().toString().isEmpty()){
                    otp6.setError("Missing");
                    otp6.requestFocus();
                    return;
                }
                else{
                    OTP = otp1.getText().toString().trim() + otp2.getText().toString().trim()
                            + otp3.getText().toString().trim() + otp4.getText().toString().trim()
                            + otp5.getText().toString().trim() + otp6.getText().toString().trim();

                    if(OTP.equals(otpTemp)){
                        Toast.makeText(Forgot2Activity.this, "OTP Match!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(Forgot2Activity.this, "Enter Correct OTP!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        sendCodeAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp1.setText("");
                otp2.setText("");
                otp3.setText("");
                otp4.setText("");
                otp5.setText("");
                otp6.setText("");
                String otp = genOtp();
                boolean isEmailSent = emailHandler.sendEmail(emailTemp, "Your One Time Password is:" + otp, "Your One Time Password is:" + otp, getApplicationContext());
                if(isEmailSent){
                    Toast.makeText(Forgot2Activity.this, "Sent Again Please Check", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Forgot2Activity.this, "Not Sent", Toast.LENGTH_SHORT).show();
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
