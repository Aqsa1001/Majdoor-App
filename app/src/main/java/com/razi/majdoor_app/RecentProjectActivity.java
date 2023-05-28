package com.razi.majdoor_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecentProjectActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView galleryIcon,image1,image2,image3,delImage;
    private TextView chooseFileText;
    private Button doneBtn;
    private Uri imageUri1,imageUri2,imageUri3;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_project);

        galleryIcon = findViewById(R.id.galleryIcon);
        chooseFileText = findViewById(R.id.chooseFile);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        doneBtn = findViewById(R.id.doneBtn);
        delImage = findViewById(R.id.delImage);

        galleryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToDb();
            }
        });

        chooseFileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        delImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null || imageUri2 != null || imageUri3 != null) {
                    imageUri1 = null;
                    imageUri2 = null;
                    imageUri3 = null;
                    image1.setImageURI(null);
                    image2.setImageURI(null);
                    image3.setImageURI(null);
                    Toast.makeText(RecentProjectActivity.this, "Images deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RecentProjectActivity.this, "No images to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadDataToDb() {
        // Pic Upload Parameter
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        Date now = new Date();
        String fileName = formatter.format(now);
        storageReference = FirebaseStorage.getInstance().getReference("images/" + fileName);

        List<Uri> imageUris = new ArrayList<>();

        if (imageUri1 != null) {
            imageUris.add(imageUri1);
        }

        if (imageUri2 != null) {
            imageUris.add(imageUri2);
        }

        if (imageUri3 != null) {
            imageUris.add(imageUri3);
        }

        if (!imageUris.isEmpty()) {
            // Uploading Pictures to Storage
            List<Task<Uri>> uploadTasks = new ArrayList<>();

            for (Uri imageUri : imageUris) {
                uploadTasks.add(storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Get the download URL
                        return storageReference.getDownloadUrl();
                    }
                }));
            }

            Tasks.whenAllComplete(uploadTasks)
                    .addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
                        @Override
                        public void onSuccess(List<Task<?>> tasks) {
                            boolean allTasksSuccessful = true;

                            for (Task<?> task : tasks) {
                                if (!task.isSuccessful()) {
                                    allTasksSuccessful = false;
                                    break;
                                }
                            }

                            if (allTasksSuccessful) {
                                Toast.makeText(RecentProjectActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                                // Clear the ImageViews and image URIs
                                image1.setImageURI(null);
                                image2.setImageURI(null);
                                image3.setImageURI(null);
                                imageUri1 = null;
                                imageUri2 = null;
                                imageUri3 = null;

                                startActivity(new Intent(RecentProjectActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(RecentProjectActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RecentProjectActivity.this, "Failed to Upload", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            startActivity(new Intent(RecentProjectActivity.this, MainActivity.class));
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                // Multiple images selected
                int imageCount = clipData.getItemCount();
                if (imageCount <= 3) {
                    for (int i = 0; i < imageCount; i++) {
                        Uri selectedImageUri = clipData.getItemAt(i).getUri();
                        if (imageUri1 == null) {
                            imageUri1 = selectedImageUri;
                            image1.setImageURI(imageUri1);
                        } else if (imageUri2 == null) {
                            imageUri2 = selectedImageUri;
                            image2.setImageURI(imageUri2);
                        } else if (imageUri3 == null) {
                            imageUri3 = selectedImageUri;
                            image3.setImageURI(imageUri3);
                        } else {
                            Toast.makeText(this, "Maximum of 3 images can be uploaded", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                } else {
                    Toast.makeText(this, "Maximum of 3 images can be uploaded", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Single image selected
                Uri selectedImageUri = data.getData();
                if (imageUri1 == null) {
                    imageUri1 = selectedImageUri;
                    image1.setImageURI(imageUri1);
                } else if (imageUri2 == null) {
                    imageUri2 = selectedImageUri;
                    image2.setImageURI(imageUri2);
                } else if (imageUri3 == null) {
                    imageUri3 = selectedImageUri;
                    image3.setImageURI(imageUri3);
                } else {
                    Toast.makeText(this, "Maximum of 3 images can be uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}