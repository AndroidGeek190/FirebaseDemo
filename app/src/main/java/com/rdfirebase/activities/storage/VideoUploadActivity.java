package com.rdfirebase.activities.storage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rdfirebase.R;

import java.util.ArrayList;

public class VideoUploadActivity extends AppCompatActivity {
    ImageView thumb_img1, thumb_img2, thumb_img3, thumb_img4, thumb_img5, thumb_img6;

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri filePath;

    ArrayList<Uri> fileList = new ArrayList<>();
    String[] file_path;

    private final int REQUEST_TAKE_GALLERY_VIDEO = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_upload);

        //Initialize Views
        Button btnChoose = findViewById(R.id.btnChoose);
        Button btnUpload = findViewById(R.id.btnUpload);
        thumb_img1 = findViewById(R.id.thumb_imgView1);
        thumb_img2 = findViewById(R.id.thumb_imgView2);
        thumb_img3 = findViewById(R.id.thumb_imgView3);
        thumb_img4 = findViewById(R.id.thumb_imgView4);
        thumb_img5 = findViewById(R.id.thumb_imgView5);
        thumb_img6 = findViewById(R.id.thumb_imgView6);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                chooseVideo();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadVideo();
            }
        });
    }

    private void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {


                filePath = data.getData();
                file_path = new String[]{MediaStore.Video.Media.DATA};
                for (int i = 0; i < file_path.length; i++) {
                    fileList.add(data.getData());
                }
                Log.e("VideoUploadActivity", "---file list size---" + fileList.size());
            }
        }
    }


    private void uploadVideo() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);

            for (int i = 0; i < fileList.size(); i++) {

                progressDialog.show();

                StorageReference mStorageReferenceVideos = storageReference.child("video");

                StorageReference ref = mStorageReferenceVideos.child(fileList.get(i).getLastPathSegment());

                Log.e("VideoUploadActivity", "reference.." + ref);

                ref.putFile(fileList.get(i))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.dismiss();


                                Toast.makeText(VideoUploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(VideoUploadActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                        .getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }
        }
    }
}
