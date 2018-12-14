package com.rdfirebase.activities.storage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rdfirebase.R;
import com.rdfirebase.activities.authentication.LoginActivity;
import com.rdfirebase.activities.home.MainActivity;

public class StorageActivity extends AppCompatActivity
{
    Button upload_images,upload_videos;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //check for user login session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(StorageActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        upload_images=findViewById(R.id.upload_images);
        upload_videos=findViewById(R.id.upload_videos);

        upload_images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               startActivity(new Intent(StorageActivity.this,ImageUploadActivity.class));
            }
        });

        upload_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StorageActivity.this,VideoUploadActivity.class));
            }
        });

    }
}
