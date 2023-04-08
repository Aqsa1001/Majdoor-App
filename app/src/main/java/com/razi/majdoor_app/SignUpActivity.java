package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText signupEmailField, signupPassField, signupConfPassField;
    Button signupBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupBtn = findViewById(R.id.signupBtn);
        signupEmailField = findViewById(R.id.signupEmailField);
        signupPassField = findViewById(R.id.signupPassField);
        signupConfPassField = findViewById(R.id.signupConfPassField);
        auth = FirebaseAuth.getInstance();
        signupBtn.setOnClickListener(this);

    }

    private void registerUser() {
        String email = signupEmailField.getText().toString().trim();
        String password = signupPassField.getText().toString().trim();
        String confpassword = signupConfPassField.getText().toString().trim();

        if (email.isEmpty()) {
            signupEmailField.setError("Enter Email");
            signupEmailField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            signupPassField.setError("Enter Password");
            signupPassField.requestFocus();
            return;
        }
        if (confpassword.isEmpty()) {
            signupConfPassField.setError("Enter Confirm Password");
            signupConfPassField.requestFocus();
            return;
        }
        if (!(password.equals(confpassword))) {
            signupPassField.setError("Confirm Password not matched with Password");
            signupPassField.requestFocus();
            return;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            signupEmailField.setError("Please Provide Valid Email");
            signupEmailField.requestFocus();
            return;
        }

        if (password.length() < 8) {
            signupPassField.setError("Enter At least 8 characters");
            signupPassField.requestFocus();
            return;
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(email, password);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(SignUpActivity.this, "oyueee", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, "This User Already Exists!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Banner:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.signupBtn:
                registerUser();
                break;
        }
    }
}