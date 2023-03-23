package com.razi.majdoor_app;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SigninActivity extends AppCompatActivity {
GoogleSignInOptions gso ;
GoogleSignInClient gsc;
ImageView  googleImage;
TextView forgotfield,createAccountText;
Button signInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        forgotfield = findViewById(R.id.forgotText);
        signInBtn=findViewById(R.id.signInbtn);
        createAccountText=findViewById(R.id.CreateAccountText);
        googleImage =findViewById(R.id.googleImage);
         gso= new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
         gsc=GoogleSignIn.getClient(this,gso);

        forgotfield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, home.class);
                startActivity(intent);
            }
        });
        createAccountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        googleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SignIn();
            }
        });
    }
    void SignIn()
    {
        Intent signInIntent=gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000)
        {
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                navigateToSecondActivity();
            }catch (ApiException e)
            {
                Toast. makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_LONG).show();
            }
        }
    }
    void navigateToSecondActivity(){
        finish();
        Intent intent = new Intent(SigninActivity.this, home.class);
        startActivity(intent);
    }
}