package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ViewImage extends AppCompatActivity {
    Intent intent;
    Bundle bundle;
    ImageView closeImage,viewLoading;
    TextView viewName,viewId;
    Picasso picasso;
    ProgressBar loadBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        intent= getIntent();
        bundle = intent.getExtras();
        closeImage = findViewById(R.id.closeImage);
        loadBar = findViewById(R.id.loadBar);
        viewLoading = findViewById(R.id.viewLoading);
        viewName = findViewById(R.id.viewName);
        viewId = findViewById(R.id.viewId);
        viewName.setText(bundle.get("viewName").toString());
        viewId.setText(bundle.get("viewId").toString());
        PhotoView photoView = (PhotoView)findViewById(R.id.viewPhoto);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (bundle.get("imageType").toString().matches("document")){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("Verification/"+bundle.get("userID").toString()+"/document.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).error(R.drawable.warning).into(photoView, new Callback() {
                        @Override
                        public void onSuccess() {
                            loadBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ViewImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ViewImage.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }

        if (bundle.get("imageType").toString().matches("selfie")){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child("Verification/"+bundle.get("userID").toString()+"/selfie.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).error(R.drawable.warning).into(photoView,new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            loadBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ViewImage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ViewImage.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            });
        }
        if (bundle.get("imageType").toString().matches("view")){

        }
    }
}