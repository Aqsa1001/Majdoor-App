package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class BasicInfo2Activity extends AppCompatActivity {
    TextView dob;
    EditText addressField;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info2);
        dob = findViewById(R.id.dobText);
        addressField = findViewById(R.id.addressField);
        doneBtn = findViewById(R.id.doneBtn);


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
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("DOB").setValue(dob.getText().toString());
                    databaseReference.child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Address").setValue(addressField.getText().toString());
                    startActivity(new Intent(BasicInfo2Activity.this, SigninActivity.class));
                    Toast.makeText(BasicInfo2Activity.this, "Successfully Registered! Sign In To Continue", Toast.LENGTH_SHORT).show();
                    dob.setText("");
                    addressField.setText("");
                    finish();
                }

            }
        });

    }


}