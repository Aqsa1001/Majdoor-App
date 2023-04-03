package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kotlin.contracts.Returns;

public class BasicInfoActivity extends AppCompatActivity {
    ImageView selectorImg;
    EditText fNameField, lNameField, pNoField;
    Button nextBtn;
    Uri imageUri;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);

        selectorImg = findViewById(R.id.selectImg);
        fNameField = findViewById(R.id.fNameField);
        lNameField = findViewById(R.id.lNameField);
        pNoField = findViewById(R.id.phNoField);
        nextBtn = findViewById(R.id.nextBtn);
        imageUri = null;


        selectorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.nextBtn:
                        uploadDataToDb();
                        break;
                }
            }
        });
    }

    private void uploadDataToDb() {
        //Pic Upload Parameter
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + fileName);
        //Ends

        //Getting Text from Fields
        String fName = fNameField.getText().toString().trim();
        String lName = lNameField.getText().toString().trim();
        String pNo = pNoField.getText().toString().trim();

        if (imageUri == null) {
            Toast.makeText(getApplicationContext(), "Please Select an Image to Continue", Toast.LENGTH_SHORT).show();
            return;
        } else if (fName.isEmpty()) {
            fNameField.setError("Enter First Name");
            fNameField.requestFocus();
            return;
        } else if (lName.isEmpty()) {
            lNameField.setError("Enter Last Name");
            lNameField.requestFocus();
            return;
        } else if (pNo.isEmpty()) {
            pNoField.setError("Enter Phone No");
            pNoField.requestFocus();
            return;
        }

        if (!(fName.isEmpty() && lName.isEmpty() && pNo.isEmpty() && imageUri == null)) {
            //Uploading Picture to Storage
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            selectorImg.setImageDrawable(getDrawable(R.drawable.profile_pic));
                            selectorImg.setImageURI(null);
                            Toast.makeText(BasicInfoActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        /*if (progressDialog.isShowing())
                            progressDialog.dismiss();*/
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        /*if (progressDialog.isShowing())
                            progressDialog.dismiss();*/
                            Toast.makeText(BasicInfoActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });

            //Inserting Data in DB
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("First Name").setValue(fNameField.getText().toString());
            databaseReference.child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Last Name").setValue(lNameField.getText().toString());
            databaseReference.child("Customers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("BasicInfo").child("Phone No").setValue(pNoField.getText().toString());
            startActivity(new Intent(BasicInfoActivity.this, BasicInfo2Activity.class));

            fNameField.setText("");
            lNameField.setText("");
            pNoField.setText("");

            finish();
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectorImg.setImageURI(imageUri);
        }
    }
}