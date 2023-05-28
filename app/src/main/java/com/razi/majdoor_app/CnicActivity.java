package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CnicActivity extends AppCompatActivity {
    EditText cnicNo;
    Button doneBtn,btnBal,btnBal2;
    ImageView cnic_pic,cnic_pic1;
    Uri imageUri1,imageUri2;
    StorageReference storageReference;

    String category;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnic);

        cnicNo = findViewById(R.id.cnicNo);
        doneBtn = findViewById(R.id.doneBtn);
        btnBal = findViewById(R.id.btnBal);
        btnBal2 = findViewById(R.id.btnBal2);
        cnic_pic = findViewById(R.id.cnic_pic);
        cnic_pic1 = findViewById(R.id.cnic_pic1);
        imageUri1 =  null;
        imageUri2 = null;

        if (getIntent().hasExtra("Category")) {
            category = getIntent().getStringExtra("Category");
        }

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cnicNo.getText().toString().isEmpty()) {
                    cnicNo.setError("Can't Be Empty!");
                    cnicNo.requestFocus();
                    return;
                } else {
                    cnicChecking();
                    uploadImagesToDb();
                    intentScreen123();
                }
            }
        });



        btnBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        btnBal2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage2();
            }
        });


    }

    private void uploadImagesToDb(){
        // Pic Upload Parameter
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/" + fileName);
        // End

        if (imageUri1 == null) {
            Toast.makeText(getApplicationContext(), "Please Add Front Image of CNIC", Toast.LENGTH_SHORT).show();
            return;
        }else if (imageUri2 == null) {
            Toast.makeText(getApplicationContext(), "Please Back Image Of CNIC", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            storageReference.putFile(imageUri1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            cnic_pic.setImageDrawable(getDrawable(R.drawable.cnic_pic));
                            cnic_pic.setImageURI(null);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    if (category.matches("individual")) {
                                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Front Url").setValue(uri.toString());
                                    } if (category.matches("contract")) {
                                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Front Url").setValue(uri.toString());
                                    }
                                }
                            });
                            Toast.makeText(CnicActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CnicActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });

            storageReference.putFile(imageUri2)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            cnic_pic1.setImageDrawable(getDrawable(R.drawable.cnic_pic));
                            cnic_pic1.setImageURI(null);

                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    if (category.matches("individual")) {
                                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Back Url").setValue(uri.toString());
                                        databaseReference.child("Individual").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Status").setValue("1");
                                    } if (category.matches("contract")) {
                                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Back Url").setValue(uri.toString());
                                        databaseReference.child("Contractor").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("CNIC").child("Status").setValue("1");
                                    }
                                }
                            });
                            Toast.makeText(CnicActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CnicActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }
    private void selectImage2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 50);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            imageUri1 = data.getData();
            cnic_pic.setImageURI(imageUri1);
        }
        if (requestCode == 50 && data != null && data.getData() != null) {
            imageUri2 = data.getData();
            cnic_pic1.setImageURI(imageUri2);
        }
    }
    public void intentScreen123(){
        if (category.matches("individual")) {
            startActivity(new Intent(CnicActivity.this, VerifyWorkActivity.class));
        } if (category.matches("contract")) {
            startActivity(new Intent(CnicActivity.this, RecentProjectActivity.class));
        }
    }

    private void cnicChecking() {
        String temp = cnicNo.getText().toString();
        String url = "https://cute-snaps-toad.cyclic.app/person/" + temp;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Boolean temp = response.getBoolean("criminalStatus");
                    if (temp == true) {
                        Toast.makeText(CnicActivity.this, "Detected Criminal!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(CnicActivity.this, "Not Criminal", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(CnicActivity.this, "Please Check CNIC. Person Not Found Against this CNIC", Toast.LENGTH_SHORT).show();
                    Log.i("Error:", "In Response Catch");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    // URL not found

                    Log.i("Error","Server Error");
                } else {
                    // Other network errors
                    error.printStackTrace();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}