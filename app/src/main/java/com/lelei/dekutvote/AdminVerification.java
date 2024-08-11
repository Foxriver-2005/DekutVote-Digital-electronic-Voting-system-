package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lelei.dekutvote.Adapters.AdminVerificationAdapter;
import com.lelei.dekutvote.model.AdminVerificationList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminVerification extends AppCompatActivity {
    public static List<AdminVerificationList> myLists;
    public static RecyclerView rv;
    public static AdminVerificationAdapter adapter;
    FloatingActionButton fabSearchUsers;
    CoordinatorLayout coordinatorLayout;
    CardView userSearch;
    SearchView searchUser;
    List<String> usersList;
    static int spinnerTypeActivated = 0;
    LayoutInflater inflater;
    View myLayout,myLayoutImage;
    ImageView viewAttached;
    androidx.appcompat.app.AlertDialog dialog;
    public static TextView numberUsers;
    View parentLayout;
    String userId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verification);
        rv=(RecyclerView)findViewById(R.id.admin_verification);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1));
        myLists=new ArrayList<>();
        userSearch = findViewById(R.id.idSearch);
        numberUsers = (TextView) findViewById(R.id.usersVerification);
        inflater = getLayoutInflater();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        fabSearchUsers = findViewById(R.id.fabSearchUsers);
        coordinatorLayout = findViewById(R.id.coordinatorLayout1);
        searchUser = findViewById(R.id.searchID);
        getUserList();
        fabSearchUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordinatorLayout.setVisibility(View.GONE);
                userSearch.setVisibility(View.VISIBLE);
                searchUser.setFocusable(true);
                searchUser.setIconified(false);
            }
        });
        searchUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                coordinatorLayout.setVisibility(View.VISIBLE);
                userSearch.setVisibility(View.GONE);
                return false;
            }
        });
        searchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                if (text.matches("[a-zA-Z- .]+")){
                    filterByName(text);
                }
                else {
                    filterById(text);
                }
                return true;
            }
        });
    }
    public void createAcc(String cloudID, Context context){
        FirebaseFirestore cStore = FirebaseFirestore.getInstance();
        FirebaseAuth cAuth = FirebaseAuth.getInstance();
        cStore.collection("Verification").document(cloudID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()){
                    String studentID = task.getResult().getString("id").toString();
                    ArrayList<String> insert = new ArrayList<>();
                    Map<String, Object> logsInsert = new HashMap<>();
                    logsInsert.put(new Date().toString(),"Account verified");
                    Map<String,Object> user = new HashMap<>();
                    user.put("firstname",task.getResult().getString("firstname"));
                    user.put("middlename",task.getResult().getString("middlename"));
                    user.put("lastname",task.getResult().getString("lastname"));
                    user.put("email",task.getResult().getString("email"));
                    user.put("id",task.getResult().getString("id"));
                    user.put("date_created",new Date());
                    user.put("logs",logsInsert);
                    user.put("course",task.getResult().getString("course"));
                    user.put("type",task.getResult().getString("type"));
                    user.put("elections_participated",insert);
                    user.put("polls_participated",insert);
                    user.put("password",task.getResult().getString("password"));
                    cAuth.createUserWithEmailAndPassword(task.getResult().getString("email") ,task.getResult().getString("password")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                FirebaseUser mUser = cAuth.getCurrentUser();
                                mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userId = cAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = cStore.collection("Users").document(mUser.getUid());
                                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                cStore.collection("Verification").document(cloudID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                    }
                                                });
                                                Map <String,String> receipts = new HashMap<>();
                                                receipts.put("null",null);
                                                DocumentReference documentReference1 = cStore.collection("Users").document(mUser.getUid()).collection("receipts").document("null");
                                                documentReference1.set(receipts);
                                                cAuth.signInWithEmailAndPassword(UserProfile.userEmail,UserProfile.userPassword);
                                                cStore.collection("Users").document(UserProfile.creatorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful() && task.getResult().exists()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            Map<String, Object> logsList = new HashMap<>();
                                                            if (document.exists()) {
                                                                Map<String, Object> logs = document.getData();
                                                                for (Map.Entry<String, Object> entry : logs.entrySet()) {
                                                                    if (entry.getKey().equals("logs")) {
                                                                        Map<String, Object> list = (Map<String, Object>) entry.getValue();
                                                                        for (Map.Entry<String, Object> e : list.entrySet()) {
                                                                            logsList.put(e.getKey().toString(),e.getValue().toString());
                                                                        }
                                                                    }
                                                                }
                                                                logsList.put(new Date().toString(),"You approved a user (" + studentID +")");
                                                                DocumentReference documentReference = cStore.collection("Users").document(UserProfile.creatorId);
                                                                documentReference.update("logs",logsList);
                                                            } else {
                                                                Log.d("TAG", "No such document");
                                                            }
                                                        } else {
                                                            Log.d("TAG", "get failed with ", task.getException());
                                                        }
                                                    }
                                                });
                                                Toast.makeText(context, "User account approved", Toast.LENGTH_LONG).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed to register: "+ e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                });
                            }else{
                                Toast.makeText(context, "Failed to register: "+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

            }
        });

    }
    public void getUserList(){
        usersList = new ArrayList<>();
        mStore.collection("Verification").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mStore.collection("Verification")
                                .document(document.getId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            usersList.add(document.getId());
                                            DocumentSnapshot document = task.getResult();
                                            myLists.add(new AdminVerificationList(document.getId(),document.getDate("date_created"),document.getString("firstname")+ " " +document.getString("middlename")+ " "+document.getString("lastname"),document.getString("id"),document.getString("type"),document.getString("email")));
                                        }
                                        numberUsers.setText(String.valueOf(myLists.size()+ " unverified account(s)"));
                                        showData();
                                    }
                                });
                    }
                }
            }
        });
    }
    public void filterByType(String text){
        List<AdminVerificationList> temp = new ArrayList();
        for(AdminVerificationList d: myLists){
            if (text.matches("Student")){
                if(d.getName().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }else if (text.matches("Faculty")){

            }else if(text.matches("Administrator")) {
            }
        }
        adapter.updateList(temp);
    }
    public void verificationPop(Context context, String id, int position){
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        mStore.collection("Verification").document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String studentID = task.getResult().getString("id");
                            if(task.getResult().exists()){
                                myLayout = LayoutInflater.from(context).inflate(R.layout.verification_user_box, null);
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                                TextView verificationId = (TextView)myLayout.findViewById(R.id.verificationId);
                                TextView verificationType = (TextView)myLayout.findViewById(R.id.verificationType);
                                TextView verificationDate = (TextView)myLayout.findViewById(R.id.verificationDate);
                                TextView verificationName = (TextView)myLayout.findViewById(R.id.verificationName);
                                TextView verificationCourse = (TextView)myLayout.findViewById(R.id.verificationCourse);
                                TextView verificationEmail = (TextView)myLayout.findViewById(R.id.verificationEmail);
                                TextView verificationDocu = (TextView)myLayout.findViewById(R.id.verificationDocu);
                                TextView verificationSelfie = (TextView)myLayout.findViewById(R.id.verificationSelfie);
                                verificationDocu.setPaintFlags(verificationDocu.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                verificationSelfie.setPaintFlags(verificationSelfie.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                                parentLayout = myLayout.findViewById(android.R.id.content);
                                verificationDocu.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent myIntent = new Intent(context, ViewImage.class);
                                        myIntent.putExtra("imageType", "document");
                                        myIntent.putExtra("userID", id);
                                        myIntent.putExtra("viewName",task.getResult().getString("firstname") + " " +task.getResult().getString("middlename")+ " " + task.getResult().getString("lastname"));
                                        myIntent.putExtra("viewId", task.getResult().getString("id"));
                                        context.startActivity(myIntent);
                                    }
                                });
                                verificationSelfie.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent myIntent = new Intent(context, ViewImage.class);
                                        myIntent.putExtra("imageType", "selfie");
                                        myIntent.putExtra("userID", id);
                                        myIntent.putExtra("viewName",task.getResult().getString("firstname") + " " +task.getResult().getString("middlename")+ " " + task.getResult().getString("lastname"));
                                        myIntent.putExtra("viewId", task.getResult().getString("id"));
                                        context.startActivity(myIntent);
                                    }
                                });
                                verificationId.setText(task.getResult().getString("id"));
                                verificationType.setText(task.getResult().getString("type"));
                                verificationDate.setText(DateFormat.format("MMM dd yyyy hh:mm aa",task.getResult().getDate("date_created")));
                                verificationName.setText(task.getResult().getString("firstname") + " " +task.getResult().getString("middlename")+ " " + task.getResult().getString("lastname"));
                                verificationCourse.setText(task.getResult().getString("course"));
                                verificationEmail.setText(task.getResult().getString("email"));
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
                                dialog = builder.create();
                                dialog.setCancelable(true);
                                dialog.show();
                                dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }
                                });
                                TextView verificationClose = (TextView) myLayout.findViewById(R.id.verificationClose);
                                verificationClose.setText("Close");
                                verificationClose.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                    }

                                });
                                Button verificationReject = (Button) myLayout.findViewById(R.id.verificationReject);
                                verificationReject.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onClick(View v) {
                                        FirebaseFirestore cStore = FirebaseFirestore.getInstance();
                                        cStore.collection("Verification").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    myLists.remove(position);
                                                    adapter = new AdminVerificationAdapter(myLists,context);
                                                    rv.setAdapter(adapter);
                                                    cStore.collection("Users").document(UserProfile.creatorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                DocumentSnapshot document = task.getResult();
                                                                Map<String, Object> logsList = new HashMap<>();
                                                                if (document.exists()) {
                                                                    Map<String, Object> logs = document.getData();
                                                                    for (Map.Entry<String, Object> entry : logs.entrySet()) {
                                                                        if (entry.getKey().equals("logs")) {
                                                                            Map<String, Object> list = (Map<String, Object>) entry.getValue();
                                                                            for (Map.Entry<String, Object> e : list.entrySet()) {
                                                                                logsList.put(e.getKey().toString(),e.getValue().toString());
                                                                            }
                                                                        }
                                                                    }
                                                                    logsList.put(new Date().toString(),"User account ("+studentID + ") rejected");
                                                                    DocumentReference documentReference = cStore.collection("Users").document(UserProfile.creatorId);
                                                                    documentReference.update("logs",logsList);
                                                                } else {
                                                                    Log.d("TAG", "No such document");
                                                                }
                                                            } else {
                                                                Log.d("TAG", "get failed with ", task.getException());
                                                            }
                                                        }
                                                    });
                                                    numberUsers.setText(String.valueOf(AdminVerification.myLists.size()+ " unverified account(s)"));
                                                    Toast.makeText(context, "Rejected", Toast.LENGTH_LONG).show();
                                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                                    StorageReference storageRefDocu = storage.getReference();
                                                    StorageReference documentRef = storageRefDocu.child("Verification/"+id+"/document.jpg");
                                                    documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            StorageReference storageRefSelfie = storage.getReference();
                                                            StorageReference selfieRef = storageRefSelfie.child("Verification/"+id+"/selfie.jpg");
                                                            selfieRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception exception) {
                                                                    Toast.makeText(context, "Failed : "+exception.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            Toast.makeText(context, "Failed : "+exception.getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                                Button verificationApprove = (Button) myLayout.findViewById(R.id.verificationApprove);
                                verificationApprove.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                    @Override
                                    public void onClick(View v) {
                                        createAcc(id,context);
                                        myLists.remove(position);
                                        adapter = new AdminVerificationAdapter(myLists,context);
                                        rv.setAdapter(adapter);
                                        FirebaseStorage storage = FirebaseStorage.getInstance();
                                        StorageReference storageRefDocu = storage.getReference();
                                        StorageReference documentRef = storageRefDocu.child("Verification/"+id+"/document.jpg");
                                        documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(context, "Failed : "+exception.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        StorageReference storageRefSelfie = storage.getReference();
                                        StorageReference selfieRef = storageRefSelfie.child("Verification/"+id+"/selfie.jpg");
                                        selfieRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                Toast.makeText(context, "Failed : "+exception.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                        else {

                        }
                    }
                });
    }
    public void filterByName(String text){
        List<AdminVerificationList> temp = new ArrayList();
        for(AdminVerificationList d: myLists){
            if(d.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
            if (text.matches("Student")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
            else if (text.matches("Faculty")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
            else if (text.matches("Administrator")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        }
        adapter.updateList(temp);
    }
    public void filterById(String text){
        List<AdminVerificationList> temp = new ArrayList();
        for(AdminVerificationList d: myLists){
            if(d.getID().contains(text)){
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }
    private void showData() {
        adapter = new AdminVerificationAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
}