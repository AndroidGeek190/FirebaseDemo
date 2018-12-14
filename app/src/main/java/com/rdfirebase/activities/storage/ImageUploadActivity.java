package com.rdfirebase.activities.storage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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

import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rdfirebase.R;
import com.rdfirebase.activities.utils.CheckPermission;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUploadActivity extends AppCompatActivity {
    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    private StorageReference mStorageReferenceImages;

    private Button btnChoose, btnUpload;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;
    int j = 0;

    private Uri filePath;
    Uri[] fileList = new Uri[6];
    ArrayList<? extends Image> images;
    Uri[] uri;
    Bitmap[] bitmaps = new Bitmap[6];

    private final int PICK_IMAGE_REQUEST = 71;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        //Initialize Views
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        imageView1 = findViewById(R.id.imgView1);
        imageView2 = findViewById(R.id.imgView2);
        imageView3 = findViewById(R.id.imgView3);
        imageView4 = findViewById(R.id.imgView4);
        imageView5 = findViewById(R.id.imgView5);
        imageView6 = findViewById(R.id.imgView6);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
//                boolean result = CheckPermission.checkPermission(ImageUploadActivity.this);
//                Log.e("----permissionStatus",""+result);
//                if (result) {
                    chooseImage();
//                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadImage();

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            for (int i = 0; i < 6; i++) {
                fileList[i] = data.getData();
            }

            filePath = data.getData();
            try {
                for (int i = j; i < fileList.length; ) {
                    bitmaps[i] = MediaStore.Images.Media.getBitmap(getContentResolver(), fileList[i]);
                    Log.e("ImageUploadActivity", "---" + i + "--" + bitmaps[i].toString());
                    j = i + 1;
                    break;
                }
                /*--show images in imageview--*/
                if (imageView1.getDrawable() == null) {
                    imageView1.setImageBitmap(bitmaps[0]);
                } else if (imageView2.getDrawable() == null) {
                    imageView2.setImageBitmap(bitmaps[1]);
                } else if (imageView3.getDrawable() == null) {
                    imageView3.setImageBitmap(bitmaps[2]);
                } else if (imageView4.getDrawable() == null) {
                    imageView4.setImageBitmap(bitmaps[3]);
                } else if (imageView5.getDrawable() == null) {
                    imageView5.setImageBitmap(bitmaps[4]);
                } else if (imageView6.getDrawable() == null) {
                    imageView6.setImageBitmap(bitmaps[5]);
                }
                images = data.getParcelableArrayListExtra(Constants.ACTION_LOAD_IMAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            uri = new Uri[bitmaps.length];
            for (int i = 0; i < bitmaps.length; i++) {
                if (bitmaps[i] == null) {
                    break;
                } else {
                    uri[i] = getImageUri(this, bitmaps[i]);
                    Log.e("ImageUploadActivity", "---///" + i + "--" + uri[i].toString());

                    mStorageReferenceImages = storageReference.child("images");

                    StorageReference ref = mStorageReferenceImages.child(uri[i].getLastPathSegment());
                    ref.putFile(uri[i])
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    progressDialog.dismiss();
                                    Toast.makeText(ImageUploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ImageUploadActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    /*--convert bitmap to uri--*/
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
