package com.razi.majdoor_app;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {
    TextView forgotfield, createAccountText;
    EditText signInEmailField, signInPassField;
    Button signInBtn;
    private FirebaseAuth mauth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        forgotfield = findViewById(R.id.forgotPassField);
        signInBtn = findViewById(R.id.signInBtn);
        mauth = FirebaseAuth.getInstance();
        signInBtn.setOnClickListener(this);
        signInEmailField = findViewById(R.id.signInEmailField);
        signInPassField = findViewById(R.id.signInPassField);
        createAccountText = findViewById(R.id.CreateAccountText);


        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        forgotfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, Forgot1Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.forgotPassField:
                startActivity(new Intent(this,Forgot1Activity.class));
                break;
            case R.id.signInBtn:
                LoginUser();
                break;

        }
    }

    private void LoginUser() {
        String email= signInEmailField.getText().toString().trim();
        String password= signInPassField.getText().toString().trim();

        if(email.isEmpty())
        {
            signInEmailField.setError("Enter Email");
            signInEmailField.requestFocus();
            return;
        }

        if(password.isEmpty())
        {
            signInPassField.setError("Enter Password");
            signInPassField.requestFocus();
            return;
        }
        if(!(Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        {
            signInEmailField.setError("Please Provide Valid Email");
            signInEmailField.requestFocus();
            return;
        }
        if(password.length() <8)
        {
            signInPassField.setError("Enter atleast 8 characters");
            signInPassField.requestFocus();
            return;
        }
        mauth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                            if(user.isEmailVerified()) {
                                startActivity(new Intent(SigninActivity.this, home.class));
                            }
                            else {
                                user.sendEmailVerification();
                                Toast.makeText(SigninActivity.this, "Check your email for verification", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            Toast.makeText(SigninActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}