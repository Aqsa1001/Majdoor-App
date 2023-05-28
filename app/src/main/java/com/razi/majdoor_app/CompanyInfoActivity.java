package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompanyInfoActivity extends AppCompatActivity {
    EditText cName,noOfLabors;
    Spinner cat1,cat2,cat3;
    List<String> categoryList = new ArrayList<>();
    List<String> categoryList2 = new ArrayList<>();
    List<String> categoryList3 = new ArrayList<>();

    Button doneBtn;
    String category;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_info);


        cName = findViewById(R.id.etComName);
        noOfLabors = findViewById(R.id.noOfLabors);
        cat1 = findViewById(R.id.cat1);
        cat2 = findViewById(R.id.cat2);
        cat3 = findViewById(R.id.cat3);
        doneBtn = findViewById(R.id.doneBtn);

        if (getIntent().hasExtra("Category")) {
            category = getIntent().getStringExtra("Category");
        }

        setCategoriesInSpinner1();
        setCategoriesInSpinner2();
        setCategoriesInSpinner3();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cName.getText().toString().isEmpty() || noOfLabors.getText().toString().isEmpty() ){
                    Toast.makeText(CompanyInfoActivity.this, "Fields Can't Be Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (cat1.getSelectedItem() == null || cat1.getSelectedItem().equals("Select Category")){
                    Toast.makeText(CompanyInfoActivity.this, "Choose Valid Category", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    // Save the data to Firebase Realtime Database
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    String userID = firebaseAuth.getCurrentUser().getUid();

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Contractor").child(userID);

                    String companyName = cName.getText().toString();
                    String numOfLabors = noOfLabors.getText().toString();
                    String selectedCategory1 = cat1.getSelectedItem().toString();
                    String selectedCategory2 = cat2.getSelectedItem().toString();
                    String selectedCategory3 = cat3.getSelectedItem().toString();

                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("companyName", companyName);
                    dataMap.put("numOfLabors", numOfLabors);
                    dataMap.put("category1", selectedCategory1);

                    // Conditionally add cat2 if it is not "Select Category"
                    if (!selectedCategory2.equals("Select Category")) {
                        dataMap.put("category2", selectedCategory2);
                    }

                    // Conditionally add cat3 if it is not "Select Category"
                    if (!selectedCategory3.equals("Select Category")) {
                        dataMap.put("category3", selectedCategory3);
                    }


                    userRef.child("CompanyInfo").setValue(dataMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Data saved successfully
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CompanyInfo").child("Status").setValue("1");
                                    Toast.makeText(CompanyInfoActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CompanyInfoActivity.this, CnicActivity.class);
                                    intent.putExtra("Category","contract");
                                    startActivity(intent);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to save data
                                    Toast.makeText(CompanyInfoActivity.this, "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        });
    }

    private void setCategoriesInSpinner1() {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                categoryList.add(0, "Select Category"); // Add "Select Category" as the first item
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList.add(category);
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CompanyInfoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, categoryList);
                cat1.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CompanyInfoActivity.this, "Failed to retrieve categories", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setCategoriesInSpinner2() {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList2.clear();
                categoryList2.add(0, "Select Category"); // Add "Select Category" as the first item
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList2.add(category);
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CompanyInfoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, categoryList2);
                cat2.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CompanyInfoActivity.this, "Failed to retrieve categories", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setCategoriesInSpinner3() {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList3.clear();
                categoryList3.add(0, "Select Category"); // Add "Select Category" as the first item
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList3.add(category);
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(CompanyInfoActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, categoryList3);
                cat3.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CompanyInfoActivity.this, "Failed to retrieve categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

}