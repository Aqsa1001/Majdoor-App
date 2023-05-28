package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText signupEmailField, signupPassField, signupConfPassField;
    Button signupBtn;
    ImageView googleImg;
    private FirebaseAuth auth;
    private GoogleSignInClient client;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signupBtn = findViewById(R.id.signupBtn);
        googleImg = findViewById(R.id.googleImage);
        signupEmailField = findViewById(R.id.signupEmailField);
        signupPassField = findViewById(R.id.signupPassField);
        signupConfPassField = findViewById(R.id.signupConfPassField);
        auth = FirebaseAuth.getInstance();
        signupBtn.setOnClickListener(this);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        client = GoogleSignIn.getClient(this, options);

        if (getIntent().hasExtra("Category")) {
            category = getIntent().getStringExtra("Category");
        }

        googleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          Intent i = client.getSignInIntent();
                startActivityForResult(i,1234);
            }
        });

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

        if (category.matches("individual")) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(email, password);
                                FirebaseDatabase.getInstance().getReference("Individual")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                    if (currentUser != null) {
                                                        //uploadStatus();
                                                        Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                                        intent.putExtra("Category","individual");
                                                        startActivity(intent);
                                                    }

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
        } else  if (category.matches("customer")) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(email, password);
                                FirebaseDatabase.getInstance().getReference("Customer")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                    if (currentUser != null) {
                                                        //uploadStatus();
                                                        Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                                        intent.putExtra("Category","customer");
                                                        startActivity(intent);
                                                    }

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
        } else if (category.matches("contract")) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(email, password);
                                FirebaseDatabase.getInstance().getReference("Contractor")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                                    if (currentUser != null) {
                                                        //uploadStatus();
                                                        Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                                        intent.putExtra("Category","contract");
                                                        startActivity(intent);
                                                    }

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
    }

    private void googleUser() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personEmail = acct.getEmail();
            storeGoogleSignInData(personEmail);
        }
    }
    private <string> void storeGoogleSignInData(string email) {

        String email1 = email.toString().trim();

        if (category.matches("individual")) {
            User user = new User(email1, null);
            FirebaseDatabase.getInstance().getReference("Individual")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null) {
                                    //uploadStatus();
                                    Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                    intent.putExtra("Category","individual");
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(SignUpActivity.this, "oyueee", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUpActivity.this, "This User Already Exists!", Toast.LENGTH_SHORT).show();
        }
        if (category.matches("customer")) {
            User user = new User(email1, null);
            FirebaseDatabase.getInstance().getReference("Customer")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                if (currentUser != null) {
                                    //uploadStatus();
                                    Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                    intent.putExtra("Category","customer");
                                    startActivity(intent);
                                }

                            } else {
                                Toast.makeText(SignUpActivity.this, "oyueee", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUpActivity.this, "This User Already Exists!", Toast.LENGTH_SHORT).show();
        }
        if (category.matches("contract")) {
            User user = new User(email1, null);
            FirebaseDatabase.getInstance().getReference("Contractor")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "Successfully Registered!", Toast.LENGTH_SHORT).show();
                                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                               if (currentUser != null) {
                                    //uploadStatus();
                                   Intent intent = new Intent(SignUpActivity.this, BasicInfoActivity.class);
                                   intent.putExtra("Category","contract");
                                   startActivity(intent);
                               }

                            } else {
                                Toast.makeText(SignUpActivity.this, "oyueee", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUpActivity.this, "This User Already Exists!", Toast.LENGTH_SHORT).show();
        }
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1234){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    googleUser();
                                }else{
                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                });
            }
            catch (ApiException e){
                e.printStackTrace();
            }
        }
    }

    /*private void uploadStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        if (category.matches("customer")) {
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference();
                    statusRef.child("Customer")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status").setValue("0");

            statusRef.child("Customer")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status2").setValue("0");


        }  if(category.matches("individual")) {
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference();
                    statusRef.child("Individual")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status").setValue("0");

            statusRef.child("Individual")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status2").setValue("0");


        }  if(category.matches("contract")) {
            DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference();
                    statusRef.child("Contractor")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status").setValue("0");

            statusRef.child("Contractor")
                    .child(userId)
                    .child("BasicInfo")
                    .child("Status2").setValue("0");

            statusRef.child("Contractor")
                    .child(userId)
                    .child("CompanyInfo")
                    .child("Status").setValue("0");

        }
    }*/


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            //Toast.makeText(SignUpActivity.this, "Successfully Registered! ", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, BasicInfoActivity.class);
            intent.putExtra("Category",category);
            startActivity(intent);
        }
    }
}