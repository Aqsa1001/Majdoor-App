package com.razi.majdoor_app;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class VerifyWorkActivity extends AppCompatActivity {
    Spinner categorySpinner;
    List<String> categoryList = new ArrayList<>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    TextView chooseFileText;
    LinearLayout linearLayoutVideos;
    ImageView delVideoIcon;
    Button doneBtn;
    private Uri selectedVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_work);

        categorySpinner = findViewById(R.id.categorySpinner);
        chooseFileText = findViewById(R.id.chooseFileText);
        linearLayoutVideos = findViewById(R.id.linear_layout_videos);
        delVideoIcon = findViewById(R.id.delVideoIcon);
        doneBtn = findViewById(R.id.DoneBtn);

        setCategoriesInSpinner();

        chooseFileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();
                if (isCategoryAlreadyAdded(selectedCategory)) {
                    Toast.makeText(VerifyWorkActivity.this, "Only one video allowed per Category", Toast.LENGTH_SHORT).show();
                } else if (getVideoCount() >= 3) {
                    Toast.makeText(VerifyWorkActivity.this, "You can upload a maximum of 3 Categories Videos", Toast.LENGTH_SHORT).show();
                } else {
                    openGallery();
                }
            }
        });

        delVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutVideos.removeAllViews();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSizeAndUploadToFirebase();
            }
        });

    }

    private void checkSizeAndUploadToFirebase(){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            int totalVideos = getVideoCount();
            final int[] uploadedVideos = {0};

            // Show progress bar
            ProgressDialog progressDialog = new ProgressDialog(VerifyWorkActivity.this);
            progressDialog.setMessage("Uploading videos...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(totalVideos);
            progressDialog.show();

            for (int i = 0; i < linearLayoutVideos.getChildCount(); i++) {
                View childView = linearLayoutVideos.getChildAt(i);
                TextView textView = childView.findViewById(R.id.catText);
                VideoView videoView = childView.findViewById(R.id.videoView);
                if (textView != null && videoView != null && videoView.getVisibility() == View.VISIBLE) {
                    String category = textView.getText().toString();
                    if (selectedVideoUri != null) {
                        long fileSize = getFileSize(selectedVideoUri);
                        if (fileSize > 10 * 1024 * 1024) {
                            // File size exceeds 10MB
                            progressDialog.dismiss();
                            Toast.makeText(VerifyWorkActivity.this, "File size cannot exceed 5MB", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        StorageReference videoRef = storageRef.child("videos/" + userId + "/" + category + ".mp4");
                        UploadTask uploadTask = videoRef.putFile(selectedVideoUri);

                        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get the current progress
                                        int progress = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setProgress(progress);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        uploadedVideos[0]++;
                                        if (uploadedVideos[0] == totalVideos) {
                                            // All videos uploaded successfully
                                            progressDialog.dismiss();
                                            Toast.makeText(VerifyWorkActivity.this, "All videos uploaded successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(VerifyWorkActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to upload the video
                                        progressDialog.dismiss();
                                        Toast.makeText(VerifyWorkActivity.this, "Failed to upload video", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 1);
    }

    private long getFileSize(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                long size = cursor.getLong(sizeIndex);
                cursor.close();
                return size;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getVideoCount() {
        int count = 0;
        for (int i = 0; i < linearLayoutVideos.getChildCount(); i++) {
            View childView = linearLayoutVideos.getChildAt(i);
            VideoView videoView = childView.findViewById(R.id.videoView);
            if (videoView != null && videoView.getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        return count;
    }

    private boolean isCategoryAlreadyAdded(String category) {
        for (int i = 0; i < linearLayoutVideos.getChildCount(); i++) {
            View childView = linearLayoutVideos.getChildAt(i);
            TextView textView = childView.findViewById(R.id.catText);
            if (textView != null && textView.getText().toString().equals(category)) {
                VideoView videoView = childView.findViewById(R.id.videoView);
                if (videoView != null && videoView.getVisibility() == View.VISIBLE) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            if (selectedVideoUri != null) {
                String selectedCategory = categorySpinner.getSelectedItem().toString();

                View videoHolderView = getLayoutInflater().inflate(R.layout.video_holder, null);
                TextView newCatText = videoHolderView.findViewById(R.id.catText);
                VideoView newVideoView = videoHolderView.findViewById(R.id.videoView);

                newCatText.setText(selectedCategory);
                newVideoView.setVideoURI(selectedVideoUri);
                newVideoView.setMediaController(new MediaController(this));
                newVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        // Capture the first frame and set it as the thumbnail
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                newVideoView.seekTo(1); // Seek to 1 millisecond to capture the first frame
                                newVideoView.pause(); // Pause the video
                                mp.setOnVideoSizeChangedListener(null); // Remove the listener
                            }
                        });
                    }
                });
                newVideoView.start();

                linearLayoutVideos.addView(videoHolderView);
                newVideoView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to get video", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setCategoriesInSpinner() {
        databaseReference.child("Categories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String category = categorySnapshot.getValue(String.class);
                    categoryList.add(category);
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(VerifyWorkActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, categoryList);
                categorySpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VerifyWorkActivity.this, "Failed to retrieve categories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}