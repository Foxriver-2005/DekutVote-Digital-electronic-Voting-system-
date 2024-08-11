package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.lelei.dekutvote.Adapters.CourseAdapter;
import com.lelei.dekutvote.model.CourseList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Registration extends AppCompatActivity {
    EditText firstnameBox,middleinitialBox,lastnameBox,idnumberBox,emailBox,passwordBox,passwordconfirmBox;
    public static EditText courseRBox;
    Spinner spinnerRegisterType;
    TextView backButton;
    Button registerButton,nextButton;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    FirebaseFirestore db;
    String userId;
    public static View myLayout;
    String str;
    public static androidx.appcompat.app.AlertDialog dialog;
    ConstraintLayout registerAttachments,registerDetails,imageDocu,imageSelfie;
    LinearLayout uploadDocu,uploadSelfie;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    String type,firstname,middlename,lastname,idnumber,course,email,password,confirmpassword;
    ImageView docuImage,docuDelete,selfieImage,selfieDelete;
    String docuPhotoPath,selfiePhotoPath;
    public static boolean active = false;
    String[] types = { "Student", "Faculty"};
    TextView textProcess;
    LayoutInflater inflater;
    View myLayoutReview,myLayoutSelection,myLayoutWarning;
    androidx.appcompat.app.AlertDialog dialogReview,dialogSelection,dialogWarning;
    SharedPreferences preferences;
    private String blockCharacterSet = "~#^|$%&*!.,1234567890";
    private InputFilter filter = new InputFilter() {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        spinnerRegisterType = (Spinner) findViewById(R.id.typeBox);
        courseRBox = (EditText) findViewById(R.id.courseRBox);
        registerAttachments = findViewById(R.id.registerAttachments);
        registerDetails = findViewById(R.id.registerDetails);
        imageDocu = findViewById(R.id.imageDocu);
        imageSelfie = findViewById(R.id.imageSelfie);
        docuImage = findViewById(R.id.docuImage);
        docuDelete = findViewById(R.id.docuDelete);
        selfieImage = findViewById(R.id.selfieImage);
        selfieDelete = findViewById(R.id.selfieDelete);
        uploadDocu = findViewById(R.id.uploadDocu);
        uploadSelfie = findViewById(R.id.uploadSelfie);
        textProcess = findViewById(R.id.textProcess);
        textProcess.setText("Registration");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_list_item_1,types);
        aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinnerRegisterType.setAdapter(aa);

        courseRBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCourse();
            }
        });
        inflater = getLayoutInflater();
        backButton = (TextView) findViewById(R.id.registrationBack);
        firstnameBox = (EditText) findViewById(R.id.firstnameBox);
        firstnameBox.setFilters(new InputFilter[] { filter });
        middleinitialBox = (EditText) findViewById(R.id.middleinitialBox);
        middleinitialBox.setFilters(new InputFilter[] { filter });
        lastnameBox = (EditText) findViewById(R.id.lastnameBox);
        lastnameBox.setFilters(new InputFilter[] { filter });
        idnumberBox = (EditText) findViewById(R.id.idnumberBox);
        emailBox = (EditText) findViewById(R.id.emailBox);
        passwordBox = (EditText) findViewById(R.id.passwordBox);
        passwordconfirmBox = (EditText) findViewById(R.id.passwordconfirmBox);
        progressBar = findViewById(R.id.progressBar);
        nextButton = (Button) findViewById(R.id.nextButton);
        registerButton = (Button) findViewById(R.id.registerButton);
        idnumberBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        idnumberBox.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.CHARACTERS, true) {
            @Override
            public int getInputType() {
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            }


            protected char[] getAcceptedChars() {
                return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-/".toCharArray();
            }
        });
        backButton.setPaintFlags(backButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        backButton.setText("Back");
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();
        type = spinnerRegisterType.getSelectedItem().toString();
        firstname = firstnameBox.getText().toString().trim();
        middlename = middleinitialBox.getText().toString().trim();
        lastname = lastnameBox.getText().toString().trim();
        idnumber = idnumberBox.getText().toString().trim();
        course = courseRBox.getText().toString().trim();
        email = emailBox.getText().toString().trim();
        password = passwordBox.getText().toString();
        confirmpassword = passwordconfirmBox.getText().toString();

        docuDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageDocu.setVisibility(View.GONE);
                uploadDocu.setVisibility(View.VISIBLE);
            }
        });

        selfieDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelfie.setVisibility(View.GONE);
                uploadSelfie.setVisibility(View.VISIBLE);
            }
        });

        uploadDocu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED && ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(Registration.this, new String[] {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);
                }else{
                    showSelection("document");
                    //  takePhoto("document");
                }

            }
        });
        uploadSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(Registration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){

                    ActivityCompat.requestPermissions(Registration.this, new String[] {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_CAMERA_REQUEST_CODE);

                } else {
                    showWarning("selfie");
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (registerDetails.getVisibility() == View.VISIBLE){
                    onBackPressed();
                }
                if (registerAttachments.getVisibility() == View.VISIBLE){
                    registerAttachments.setVisibility(View.GONE);
                    registerDetails.setVisibility(View.VISIBLE);
                    textProcess.setText("Registration");
                }

            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageDocu.getVisibility() == View.VISIBLE && imageSelfie.getVisibility() == View.VISIBLE){
                    forVerification();
                }else{
                    Toast.makeText(Registration.this, "Upload your selfie and documents first", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDetails();
            }
        });
    }

    public void checkAttachments(){

    }
    public void takePhoto(String type){
        if (type.matches("document")){
            dispatchTakePictureIntentDocument();
        }
        if (type.matches("selfie")){
            dispatchTakePictureIntentSelfie();
        }
    }
    public void uploadPhoto(String id){
        FirebaseStorage storageDocu = FirebaseStorage.getInstance();
        StorageReference storageRefDocu = storageDocu.getReference();
        StorageReference docuImageRef = storageRefDocu.child("Verification/"+id+"/document.jpg");
        docuImage.setDrawingCacheEnabled(true);
        docuImage.buildDrawingCache();
        Bitmap docuBitmap = ((BitmapDrawable) docuImage.getDrawable()).getBitmap();
        ByteArrayOutputStream docuBaos = new ByteArrayOutputStream();
        docuBitmap.compress(Bitmap.CompressFormat.JPEG, 100, docuBaos);
        byte[] docuData = docuBaos.toByteArray();
        UploadTask uploadTaskDocu = docuImageRef.putBytes(docuData);
        uploadTaskDocu.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Toast.makeText(Registration.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

        FirebaseStorage storageSelfie = FirebaseStorage.getInstance();
        StorageReference storageRefSelfie = storageSelfie.getReference();
        StorageReference selfieImageRef = storageRefSelfie.child("Verification/"+id+"/selfie.jpg");
        selfieImage.setDrawingCacheEnabled(true);
        selfieImage.buildDrawingCache();
        Bitmap selfieBitmap = ((BitmapDrawable) selfieImage.getDrawable()).getBitmap();
        ByteArrayOutputStream selfieBaos = new ByteArrayOutputStream();
        selfieBitmap.compress(Bitmap.CompressFormat.JPEG, 100, selfieBaos);
        byte[] selfieData = selfieBaos.toByteArray();
        UploadTask uploadTaskSelfie = selfieImageRef.putBytes(selfieData);
        uploadTaskSelfie.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Registration.this, exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });
    }
    public void reviewPhotoDocument(){
        myLayoutReview = inflater.inflate(R.layout.review, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        ImageView imageReview = (ImageView)myLayoutReview.findViewById(R.id.imageReview);
        imageReview.setImageURI(Uri.fromFile(new File(docuPhotoPath)));
        Button buttonConfirm = (Button)myLayoutReview.findViewById(R.id.buttonConfirm);
        Button buttonRetry = (Button)myLayoutReview.findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto("document");
                dialogReview.dismiss();
            }
        });
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(docuPhotoPath);

                ImageView imageView = (ImageView) findViewById(R.id.docuImage);
                try {
                    imageView.setImageURI(Uri.fromFile(file));
                    uploadDocu.setVisibility(View.GONE);
                    imageDocu.setVisibility(View.VISIBLE);
                    dialogReview.dismiss();
                }catch (Exception e){
                    Toast.makeText(Registration.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setView(myLayoutReview);
        dialogReview = builder.create();
        dialogReview.setCancelable(false);
        dialogReview.show();
    }
    public void reviewPhotoSelfie(){
        myLayoutReview = inflater.inflate(R.layout.review, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        ImageView imageReview = (ImageView)myLayoutReview.findViewById(R.id.imageReview);
        imageReview.setImageURI(Uri.fromFile(new File(selfiePhotoPath)));
        Button buttonConfirm = (Button)myLayoutReview.findViewById(R.id.buttonConfirm);
        Button buttonRetry = (Button)myLayoutReview.findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto("selfie");
                dialogReview.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(selfiePhotoPath);
                ImageView imageView = (ImageView) findViewById(R.id.selfieImage);
                try {
                    imageView.setImageURI(Uri.fromFile(file));
                    uploadSelfie.setVisibility(View.GONE);
                    imageSelfie.setVisibility(View.VISIBLE);
                    dialogReview.dismiss();
                }catch (Exception e){
                    Toast.makeText(Registration.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(myLayoutReview);
        dialogReview = builder.create();
        dialogReview.setCancelable(false);
        dialogReview.show();
    }
    public void reviewPhotoDocumentPick(Bitmap bitmap){
        myLayoutReview = inflater.inflate(R.layout.review, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        ImageView imageReview = (ImageView)myLayoutReview.findViewById(R.id.imageReview);
        imageReview.setImageBitmap(bitmap);
        Button buttonConfirm = (Button)myLayoutReview.findViewById(R.id.buttonConfirm);
        Button buttonRetry = (Button)myLayoutReview.findViewById(R.id.buttonRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(gallery, 300);
                dialogReview.dismiss();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = (ImageView) findViewById(R.id.docuImage);
                try {
                    imageView.setImageBitmap(bitmap);
                    uploadDocu.setVisibility(View.GONE);
                    imageDocu.setVisibility(View.VISIBLE);
                    dialogReview.dismiss();
                }catch (Exception e){
                    Toast.makeText(Registration.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setView(myLayoutReview);
        dialogReview = builder.create();
        dialogReview.setCancelable(false);
        dialogReview.show();
    }

    public void showWarning(String type){
        myLayoutWarning = inflater.inflate(R.layout.warning, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        Button buttonOk = (Button)myLayoutWarning.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.matches("document")){
                    takePhoto("document");
                    dialogWarning.dismiss();
                }
                if (type.matches("selfie")){
                    takePhoto("selfie");
                    dialogWarning.dismiss();
                }

            }
        });

        builder.setView(myLayoutWarning);
        dialogWarning = builder.create();
        dialogWarning.setCancelable(false);
        dialogWarning.show();

    }


    public void reviewPhoto(String filename,int code){
        myLayoutReview = inflater.inflate(R.layout.review, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        //  File file = new File(filename);
        ImageView imageReview = (ImageView)myLayoutReview.findViewById(R.id.imageReview);
        imageReview.setImageURI(Uri.fromFile(new File(filename)));
        builder.setView(myLayoutReview);
        dialogReview = builder.create();
        dialogReview.setCancelable(false);
        dialogReview.show();

    }


    private File createImageFileDocument() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "document_eVPOLL_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        docuPhotoPath = image.getAbsolutePath();
        return image;
    }
    private File createImageFileSelfie() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "selfie_eVPOLL_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        selfiePhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntentDocument() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                docuPhotoPath = null;
                File photoFile = null;
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "document_eVPOLL_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
                photoFile = image;

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("docuPhotoPath", image.getAbsolutePath());
                editor.apply();
                docuPhotoPath = image.getAbsolutePath();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 100);
                }

            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void dispatchTakePictureIntentSelfie() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                selfiePhotoPath = null;
                File photoFile = null;
                //   photoFile = createImageFileSelfie();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "selfie_eVPOLL_" + timeStamp + "_";
                File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,
                        ".jpg",
                        storageDir
                );
                photoFile = image;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("selfiePhotoPath", image.getAbsolutePath());
                editor.apply();
                selfiePhotoPath = image.getAbsolutePath();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
                    takePictureIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
                    takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(takePictureIntent, 200);
                }

            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            docuPhotoPath = preferences.getString("docuPhotoPath",null);
            try {
                if (docuPhotoPath != null){
                    reviewPhotoDocument();
                }else{
                    Toast.makeText(this, "Error capturing image. Please try again", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(this, "Please try again :" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 200 && resultCode == Activity.RESULT_OK){

            selfiePhotoPath = preferences.getString("selfiePhotoPath",null);
            try {
                if (selfiePhotoPath != null){
                    reviewPhotoSelfie();
                }else{
                    Toast.makeText(this, "Error capturing image. Please try again", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(this, "Please try again :" + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == 300 && resultCode == Activity.RESULT_OK){
            try {
                uploadDocu.setVisibility(View.GONE);
                imageDocu.setVisibility(View.VISIBLE);
                ImageView imageView = (ImageView) findViewById(R.id.docuImage);
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                reviewPhotoDocumentPick(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Registration.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }
    public void showSelection(String selectiontype){
        myLayoutSelection = inflater.inflate(R.layout.selection, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Registration.this);
        LinearLayout pickSelection = (LinearLayout)myLayoutSelection.findViewById(R.id.pickSelection);
        LinearLayout takeSelection = (LinearLayout)myLayoutSelection.findViewById(R.id.takeSelection);
        pickSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectiontype.matches("document")){
                    Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(gallery, 300);
                    dialogSelection.dismiss();
                }
            }
        });
        takeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectiontype.matches("document")){
                    showWarning("document");
                    dialogSelection.dismiss();
                }
                if (selectiontype.matches("selfie")){
                    showWarning("selfie");
                    dialogSelection.dismiss();
                }
            }
        });

        builder.setView(myLayoutSelection);
        dialogSelection = builder.create();
        dialogSelection.setCancelable(true);
        dialogSelection.show();
    }
    public void checkDetails(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        type = spinnerRegisterType.getSelectedItem().toString();
        firstname = firstnameBox.getText().toString().trim();
        middlename = middleinitialBox.getText().toString().trim();
        lastname = lastnameBox.getText().toString().trim();
        idnumber = idnumberBox.getText().toString().trim();
        course = courseRBox.getText().toString().trim();
        email = emailBox.getText().toString().trim();
        password = passwordBox.getText().toString();
        confirmpassword = passwordconfirmBox.getText().toString();

        if(firstname.isEmpty()){
            firstnameBox.setError("First name is required");
            firstnameBox.requestFocus();
            return;
        }
        if(middlename.isEmpty()){
            middleinitialBox.setError("Middle name is required");
            middleinitialBox.requestFocus();
            return;
        }
        if(lastname.isEmpty()){
            lastnameBox.setError("Last name is required");
            lastnameBox.requestFocus();
            return;
        }
        if (idnumber.isEmpty()) {
            idnumberBox.setError("ID number is required");
            idnumberBox.requestFocus();
            return;
        }

// Regular expression pattern for the ID format C026-01-1629/2020
        String idPattern = "^[A-Z]\\d{3}-\\d{2}-\\d{4}/\\d{4}$";

// Validate the ID number against the pattern
        if (!idnumber.matches(idPattern)) {
            idnumberBox.setError("Invalid ID number format. Please enter in format C026-01-0000/2021");
            idnumberBox.requestFocus();
            return;
        }
        if(course.isEmpty()){
            courseRBox.setError("Course is required");
            courseRBox.requestFocus();
            return;
        }
        if(email.isEmpty()){
            emailBox.setError("Email is required");
            emailBox.requestFocus();
            return;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\..+[a-z]+";
        if(!email.matches(emailPattern)){
            emailBox.setError("Invalid email");
            emailBox.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordBox.setError("Password is required");
            passwordBox.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordBox.setError("Password should be atleast 6 characters");
            passwordBox.requestFocus();
            return;
        }
        if(confirmpassword.isEmpty()){
            passwordconfirmBox.setError("Confirm your password");
            passwordconfirmBox.requestFocus();
            return;
        }
        if (!confirmpassword.equals(password)){
            passwordconfirmBox.setError("Password does not match");
            passwordconfirmBox.requestFocus();
            return;
        }
        mStore.collection("Users")
                .whereEqualTo("id", idnumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()){
                                    idnumberBox.setError("ID number already exist");
                                    idnumberBox.requestFocus();
                                    return;
                                }else{

                                }
                            }
                            registerAttachments.setVisibility(View.VISIBLE);
                            registerDetails.setVisibility(View.GONE);
                            textProcess.setText("Verification");
                        } else {
                            Toast.makeText(Registration.this, "Failed to register: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void forVerification(){
        progressBar.setVisibility(View.VISIBLE);
        type = spinnerRegisterType.getSelectedItem().toString();
        firstname = firstnameBox.getText().toString().trim();
        middlename = middleinitialBox.getText().toString().trim();
        lastname = lastnameBox.getText().toString().trim();
        idnumber = idnumberBox.getText().toString().trim();
        course = courseRBox.getText().toString().trim();
        email = emailBox.getText().toString().trim();
        password = passwordBox.getText().toString();
        confirmpassword = passwordconfirmBox.getText().toString();

        ArrayList<String> insert = new ArrayList<>();
        Map<String,Object> user = new HashMap<>();
        user.put("firstname",firstname);
        user.put("middlename",middlename);
        user.put("lastname",lastname);
        user.put("email",email);
        user.put("id",idnumber);
        user.put("date_created",new Date());
        user.put("course",course);
        user.put("type",type);
        user.put("elections_participated",insert);
        user.put("polls_participated",insert);
        user.put("password",password);
        DocumentReference documentReference = mStore.collection("Verification").document();
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                uploadPhoto(documentReference.getId());
                Toast.makeText(Registration.this, "Account submitted. Please wait for the admin to verify your account", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration.this, "Failed to register: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void selectCourse(){
        String[] arrayCourse = new String[] {
                "BSc IT",
                "BSc CS",
                "BBIT",
                "BSc Civil",
                "BSc Mechanical",
                "BSc EEE",
                "BSc Mechatronics",
                "BSc TIE",
                "BSc Acturial Science",
                "BSc Maths & Modelling",
                "BSc Industrial Chem",
                "BSc Organic Chem",
                "BSc Textile & Leather",
                "BSc Nursing",
                "BSc Nutrition & Dietetics",
                "BSc Food Science",
                "BSc Chemical Eng",
                "BSc Tourism & Hospitality",
                "BSc GIS",
                "BSc GEGIS",
                "BBA Commerce",
                "BBA Economics",
                "BBA Procurement",
                "BBA Business Mngnt",
                "BBA Business Admin",
                "BSc Building Tech",
                "Bed Electrical",
                "Bed Mechanical",
                "Bed Civil",
                "BBA Accounting"
        };
        String[] department = new String[] {
                "School of Computer Science & IT",
                "School of Computer Science & IT",
                "School of Computer Science & IT",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Nursing",
                "School of IFBT",
                "School of IFBT",
                "School of Engineering",
                "School of ITHOM",
                "School of IGRES",
                "School of IGRES",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Technology",
                "School of Education",
                "School of Education",
                "School of Education",
                "School of Business"
        };
        myLayout = LayoutInflater.from(this).inflate(R.layout.courses, null);
        List<CourseList> myLists1;
        RecyclerView rv1;
        CourseAdapter adapter1;
        TextView textCourses;
        textCourses = (TextView) myLayout.findViewById(R.id.textCourses);
        textCourses.setVisibility(View.GONE);
        rv1 = (RecyclerView) myLayout.findViewById(R.id.selectCourse);
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists1 = new ArrayList<>();
        for (int x = 0; x < arrayCourse.length;x++){
            myLists1.add(new CourseList(arrayCourse[x],department[x],false ));
        }
        adapter1 = new CourseAdapter(myLists1, this);
        rv1.setAdapter(adapter1);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(myLayout).setPositiveButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog= builder.create();

        dialog.setCancelable(true);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
    }
    public void checkIdNumber(String id){

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("photoSelfie",selfiePhotoPath);
        outState.putString("photoDocument",docuPhotoPath);

    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)         {
        super.onRestoreInstanceState(savedInstanceState);
        nextButton.performClick();
        docuPhotoPath = preferences.getString("docuPhotoPath",null);
        if (docuPhotoPath != null){
            File file = new File(docuPhotoPath);
            ImageView imageViewDocu = (ImageView) findViewById(R.id.docuImage);
            try {
                imageViewDocu.setImageURI(Uri.fromFile(file));
                uploadDocu.setVisibility(View.GONE);
                imageDocu.setVisibility(View.VISIBLE);
                dialogReview.dismiss();
            }catch (Exception e){

            }
        }
        selfiePhotoPath = preferences.getString("selfiePhotoPath",null);
        if (selfiePhotoPath != null){
            File file = new File(selfiePhotoPath);
            ImageView imageView = (ImageView) findViewById(R.id.selfieImage);
            try {
                imageView.setImageURI(Uri.fromFile(file));
                uploadSelfie.setVisibility(View.GONE);
                imageSelfie.setVisibility(View.VISIBLE);
                dialogReview.dismiss();
            }catch (Exception e){

            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}