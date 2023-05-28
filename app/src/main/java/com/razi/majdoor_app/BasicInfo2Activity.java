package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class BasicInfo2Activity extends AppCompatActivity {
    TextView dob;
    EditText addressField;
    Button doneBtn;
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info2);
        dob = findViewById(R.id.dobText);
        addressField = findViewById(R.id.addressField);
        doneBtn = findViewById(R.id.doneBtn);
        if (getIntent().hasExtra("Category")) {
            category = getIntent().getStringExtra("Category");
        }
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog dialog = new DatePickerDialog(BasicInfo2Activity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String date = dayOfMonth + "/" + month + "/" + year;
                        dob.setText(date);
                    }
                }, year, month, day);
                dialog.show();
            }
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dob.getText().toString().isEmpty()) {
                    dob.setError("Click to enter DOB");
                    dob.requestFocus();
                    return;
                } else if (addressField.getText().toString().isEmpty()) {
                    addressField.setError("Enter Address");
                    addressField.requestFocus();
                    return;
                } else {

                    if (category.matches("individual")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("DOB").setValue(dob.getText().toString());
                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Address").setValue(addressField.getText().toString());
                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Status2").setValue("1");
                        Intent intent = new Intent(BasicInfo2Activity.this, CnicActivity.class);
                        intent.putExtra("Category",category);
                        startActivity(intent);
                    } else if (category.matches("contract")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("DOB").setValue(dob.getText().toString());
                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Address").setValue(addressField.getText().toString());
                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Status2").setValue("1");
                        Intent intent = new Intent(BasicInfo2Activity.this, CompanyInfoActivity.class);
                        intent.putExtra("Category",category);
                        startActivity(intent);
                    } else if (category.matches("customer")) {
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("DOB").setValue(dob.getText().toString());
                        databaseReference.child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Address").setValue(addressField.getText().toString());
                        databaseReference.child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Status2").setValue("1");
                        Intent intent = new Intent(BasicInfo2Activity.this, SigninActivity.class);
                        intent.putExtra("Category",category);
                        startActivity(intent);
                    }

                    Toast.makeText(BasicInfo2Activity.this, "Successfully Registered! Sign In To Continue", Toast.LENGTH_SHORT).show();
                    dob.setText("");
                    addressField.setText("");
                    finish();
                }

            }
        });
    }


}