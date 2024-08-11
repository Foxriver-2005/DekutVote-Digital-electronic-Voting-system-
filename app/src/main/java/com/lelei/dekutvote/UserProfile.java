package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.Adapters.UserAdapter;
import com.lelei.dekutvote.model.UserList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    public List<UserList> myLists;
    public RecyclerView rv;
    public UserAdapter adapter;
    String userId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    TextView fullnameText,idText,emailText;
    ImageView logoutButton;
    Login login;
    LayoutInflater inflater;
    View myLayout;
    FirebaseUser mUser;
    public static String userType;
    public static String category;
    public static String userEmail;
    public static String userPassword;
    SimpleDateFormat simpleDateFormat;
    EditText firstnameEdit;
    EditText middleinitialEdit;
    EditText lastnameEdit;
    EditText courseEdit;
    TextView idNumberText,profileType;
    EditText emailEdit;
    TextView dateprofileText;
    public static String myCourse;
    public static String creatorId;
    public static String creatorName;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    int electionsCount = 0,pollsCount = 0;
    int notifCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        rv=(RecyclerView)findViewById(R.id.rec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        myLists=new ArrayList<>();
        fullnameText = findViewById(R.id.fullnameText);
        idText = findViewById(R.id.idText);
        emailText = findViewById(R.id.emailText);
        logoutButton = (ImageView) findViewById(R.id.logoutButton);
        inflater = getLayoutInflater();
        myLayout = inflater.inflate(R.layout.verification_box, null);
        login = new Login();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mUser  = mAuth.getCurrentUser();
        notifications();
        notificationsLive();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        userId=user.getEmail();
        fullnameText.setSelected(true);
        idText.setSelected(true);
        emailText.setSelected(true);
        Button infoButton = (Button) findViewById(R.id.infoButton);
        Button closeButton = (Button) findViewById(R.id.closeButton);
        Button updateButton = (Button) findViewById(R.id.updateButton);
        Button emailButton = (Button) findViewById(R.id.updateEmail);
        firstnameEdit = (EditText) findViewById(R.id.firstnameEdit);
        middleinitialEdit = (EditText) findViewById(R.id.middleinitialEdit);
        lastnameEdit = (EditText) findViewById(R.id.lastnameEdit);
        courseEdit = (EditText) findViewById(R.id.courseEdit);
        idNumberText = (TextView) findViewById(R.id.idNumberText);
        emailEdit = (EditText) findViewById(R.id.emailEdit);
        dateprofileText = (TextView) findViewById(R.id.dateprofileText);
        profileType = (TextView) findViewById(R.id.profileType);
        profileType.setVisibility(View.GONE);
        CardView infoView = (CardView) findViewById(R.id.infoView);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView.setVisibility(View.VISIBLE);
            }
        });
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoView.setVisibility(View.GONE);
            }
        });
        userId = mAuth.getCurrentUser().getUid();
        mStore.collection("Users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            CardView cardView = (CardView)findViewById(R.id.cardView);
                            cardView.setVisibility(View.VISIBLE);
                            DocumentSnapshot document = task.getResult();
                            userType = document.getString("type");
                            userEmail = document.getString("email");
                            userPassword = document.getString("password");
                            ImageView imageViewemail = (ImageView) findViewById(R.id.imageViewemail) ;
                            ImageView imageViewId = (ImageView) findViewById(R.id.imageViewid) ;
                            if (userType.matches("Student")){
                                if(!mUser.isEmailVerified()){
                                    verification();
                                }
                                imageViewemail.setVisibility(View.VISIBLE);
                                imageViewId.setVisibility(View.VISIBLE);
                                getdataStudent();
                                studentUser();
                            }
                            else if (userType.matches("Faculty")){
                                if(!mUser.isEmailVerified()){
                                    verification();
                                }
                                imageViewemail.setVisibility(View.VISIBLE);
                                imageViewId.setVisibility(View.VISIBLE);
                                getDataFaculty();
                                facultyUser();
                            }
                            else if (userType.matches("Administrator"))
                            {
                                imageViewemail.setVisibility(View.VISIBLE);
                                imageViewId.setVisibility(View.VISIBLE);
                                getDataAdmin();
                                adminUser();
                            }
                        }
                    }

                });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> upd = new HashMap<>();
                upd.put("firstname",firstnameEdit.getText().toString());
                upd.put("middlename",middleinitialEdit.getText().toString());
                upd.put("lastname",lastnameEdit.getText().toString());
                upd.put("course",courseEdit.getText().toString());
                upd.put("id",idNumberText.getText().toString());

                DocumentReference documentReference = mStore.collection("Users").document(userId);
                documentReference.update(upd).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mStore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                        logsList.put(new Date().toString(), "Profile updated.");
                                        DocumentReference documentReference = mStore.collection("Users").document(userId);
                                        documentReference.update("logs",logsList);
                                    }
                                    else {
                                        Log.d("TAG", "No such document");
                                    }
                                }
                                else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
                        View parentLayout = findViewById(android.R.id.content);
                        infoView.setVisibility(View.GONE);
                        Snackbar.make(parentLayout, "Profile updated", Snackbar.LENGTH_LONG)
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                                .show();
                    }
                });
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UserProfile.this, Login.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                UserProfile.this.startActivity(myIntent);
            }
        });
    }
    public void notif(){

    }
    public void studentUser(){
        DocumentReference documentReference = mStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                creatorId = userId;
                fullnameText.setText(value.getString("firstname")+" "+value.getString("middlename")+" "+value.getString("lastname"));
                idText.setText("ID Number: "+value.getString("id"));
                emailText.setText("Email: "+value.getString("email"));
                userType = value.getString("type");
                myCourse = value.getString("course");
                firstnameEdit.setText(value.getString("firstname"));
                middleinitialEdit.setText(value.getString("middlename"));
                lastnameEdit.setText(value.getString("lastname"));
                courseEdit.setText(value.getString("course"));
                idNumberText.setText(value.getString("id"));
                emailEdit.setText(value.getString("email"));
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyy hh:mm aa");
                Date date = value.getDate("date_created");
                dateprofileText.setText("Account created: "+simpleDateFormat.format(date.getTime()));
            }
        });
    }
    public void facultyUser(){
        DocumentReference documentReference = mStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                //     Toast.makeText(UserProfile.this, value.getString("type"), Toast.LENGTH_SHORT).show();
                profileType.setVisibility(View.VISIBLE);
                fullnameText.setText(value.getString("firstname")+" "+value.getString("middlename")+" "+value.getString("lastname"));
                idText.setText("ID Number: "+value.getString("id"));
                emailText.setText("Email: "+value.getString("email"));
                userType = value.getString("type");
                creatorId = userId;
                creatorName = value.getString("firstname")+" "+value.getString("middlename")+" "+value.getString("lastname");
                profileType.setText("("+userType+")");
                myCourse = value.getString("course");
                firstnameEdit.setText(value.getString("firstname"));
                middleinitialEdit.setText(value.getString("middlename"));
                lastnameEdit.setText(value.getString("lastname"));
                courseEdit.setText(value.getString("course"));
                idNumberText.setText(value.getString("id"));
                emailEdit.setText(value.getString("email"));
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyy hh:mm aa");
                Date date = value.getDate("date_created");
                dateprofileText.setText("Account created: "+simpleDateFormat.format(date.getTime()));
            }
        });
    }

    public void adminUser(){
        DocumentReference documentReference = mStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profileType.setVisibility(View.VISIBLE);
                fullnameText.setText(value.getString("firstname")+" "+value.getString("middlename")+" "+value.getString("lastname"));
                idText.setText("ID Number: "+value.getString("id"));
                emailText.setText("Email: "+value.getString("email"));
                userType = value.getString("type");
                creatorId = userId;
                creatorName = value.getString("firstname")+" "+value.getString("middlename")+" "+value.getString("lastname");
                profileType.setText("("+userType+")");
                myCourse = value.getString("course");
                firstnameEdit.setText(value.getString("firstname"));
                middleinitialEdit.setText(value.getString("middlename"));
                lastnameEdit.setText(value.getString("lastname"));
                courseEdit.setText(value.getString("course"));
                idNumberText.setText(value.getString("id"));
                emailEdit.setText(value.getString("email"));
                simpleDateFormat = new SimpleDateFormat("MM/dd/yyy hh:mm aa");
                Date date = value.getDate("date_created");
                dateprofileText.setText("Account created: "+simpleDateFormat.format(date.getTime()));
            }
        });
    }
    public void notifications(){
        mStore.collection("Election").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    int count = 0;
                    for (DocumentSnapshot document : task.getResult()) {
                        count++;
                    }
                    electionsCount = count;
                    mStore.collection("Polls").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                int count = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    count++;
                                }
                                pollsCount = count;
                                NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                if(Build.VERSION.SDK_INT >= 26)
                                {
                                    //When sdk version is larger than26
                                    String id = "channel_1";
                                    String description = "143";
                                    int importance = NotificationManager.IMPORTANCE_LOW;
                                    NotificationChannel channel = new NotificationChannel(id, description, importance);
                                    channel.enableLights(true);
                                    channel.enableVibration(true);//
                                    manager.createNotificationChannel(channel);
                                    Notification notification = new Notification.Builder(UserProfile.this, id)
                                            .setCategory(Notification.CATEGORY_MESSAGE)
                                            .setSmallIcon(R.drawable.promotion)
                                            .setContentTitle("eVPOLL")
                                            .setContentText("Elections: " +electionsCount+ " Polls: " +pollsCount)
                                            //.setContentIntent(pendingIntent)
                                            .setAutoCancel(true)
                                            .build();
                                    manager.notify(0, notification);
                                }
                                else
                                {
                                    //When sdk version is less than26
                                    Notification notification = new NotificationCompat.Builder(UserProfile.this)
                                            .setContentTitle("eVPOLL")
                                            .setContentText("Elections: " +electionsCount+  " Polls: " +pollsCount)
                                            //  .setContentIntent(pendingIntent)
                                            .setSmallIcon(R.drawable.promotion)
                                            .build();
                                    manager.notify(0,notification);
                                }
                            }
                            else {

                            }
                        }
                    });
                }
                else {

                }
            }
        });
    }
    public void notificationsLive(){
        ArrayList<String> liveList= new ArrayList<>();
        notifCount = 1;
        mStore.collection("Election").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            mStore.collection("Election").document(document.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Timestamp timestampStart,timestampEnd;
                                    Date date_start,date_end;
                                    Boolean start = false, end = false;
                                    String name = task.getResult().get("title").toString();
                                    timestampStart = (Timestamp) task.getResult().get("start");
                                    date_start = timestampStart.toDate();
                                    timestampEnd = (Timestamp) task.getResult().get("end");
                                    date_end = timestampEnd.toDate();
                                    if (new Date().after(date_start)){
                                        start = true;
                                    }
                                    if (new Date().before(date_end)){
                                        end = true;
                                    }
                                    if (start == true && end == true ){
                                        liveList.add(name);
                                        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                                        if(Build.VERSION.SDK_INT >= 26)
                                        {
                                            //When sdk version is larger than26
                                            String id = name;
                                            String description = name;
                                            int importance = NotificationManager.IMPORTANCE_LOW;
                                            NotificationChannel channel = new NotificationChannel(id, description, importance);
                                            channel.enableLights(true);
                                            channel.enableVibration(true);//
                                            manager.createNotificationChannel(channel);
                                            Notification notification = new Notification.Builder(UserProfile.this, id)
                                                    .setCategory(Notification.CATEGORY_MESSAGE)
                                                    .setSmallIcon(R.drawable.promotion)
                                                    .setContentTitle("Live now")
                                                    .setContentText(name)
                                                    //.setContentIntent(pendingIntent)
                                                    .setAutoCancel(true)
                                                    .build();
                                            manager.notify(notifCount, notification);
                                        }
                                        else
                                        {
                                            //When sdk version is less than26
                                            Notification notification = new NotificationCompat.Builder(UserProfile.this)
                                                    .setContentTitle("Live now")
                                                    .setContentText(name)
                                                    //  .setContentIntent(pendingIntent)
                                                    .setSmallIcon(R.drawable.promotion)
                                                    .build();
                                            manager.notify(notifCount,notification);
                                        }
                                        notifCount++;
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    public void verification(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        String userEmail = mAuth.getCurrentUser().getEmail();
        Button buttonSend = (Button)myLayout.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserProfile.this, "Email verification has been sent to: "+userEmail, Toast.LENGTH_LONG).show();
                        Toast.makeText(UserProfile.this, "You will be redirected to the login page unless your email is verified", Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(UserProfile.this, Login.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        UserProfile.this.startActivity(myIntent);
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Intent myIntent = new Intent(UserProfile.this, Login.class);
                        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        UserProfile.this.startActivity(myIntent);
                        //  verification();
                    }
                });

            }
        });
        builder.setView(myLayout);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void getdataStudent() {
        myLists.add(new UserList(R.drawable.cast,"Election"));
        myLists.add(new UserList(R.drawable.poll,"Poll"));
        myLists.add(new UserList(R.drawable.counts,"Live updates"));
        myLists.add(new UserList(R.drawable.clock,"Voting Receipts"));
        myLists.add(new UserList(R.drawable.worklogs,"Work logs"));
        myLists.add(new UserList(R.drawable.feedback,"Feedback"));
        myLists.add(new UserList(R.drawable.information,"About"));
        adapter=new UserAdapter(myLists,this);
        rv.setAdapter(adapter);
    }

    private void getDataFaculty() {
        myLists.add(new UserList(R.drawable.cast,"Election"));
        myLists.add(new UserList(R.drawable.poll,"Poll"));
        myLists.add(new UserList(R.drawable.politicians, "Administer an election"));
        myLists.add(new UserList(R.drawable.poll_list, "Conduct a Poll"));
        myLists.add(new UserList(R.drawable.folder, "Pending request"));
        myLists.add(new UserList(R.drawable.counts,"Live updates"));
        myLists.add(new UserList(R.drawable.clock,"Voting Receipts"));
        myLists.add(new UserList(R.drawable.worklogs,"Work logs"));
        myLists.add(new UserList(R.drawable.information,"About"));
        adapter=new UserAdapter(myLists,this);
        rv.setAdapter(adapter);
    }
    private void getDataAdmin() {
        myLists.add(new UserList(R.drawable.politicians, "Administer an election"));
        myLists.add(new UserList(R.drawable.poll_list, "Conduct a Poll"));
        myLists.add(new UserList(R.drawable.group, "Manage users"));
        myLists.add(new UserList(R.drawable.analysis,"Poll Results"));
        myLists.add(new UserList(R.drawable.approval, "Approve request"));
        myLists.add(new UserList(R.drawable.counts, "Live updates"));
        myLists.add(new UserList(R.drawable.verifiedaccount, "Verify Accounts"));
        myLists.add(new UserList(R.drawable.worklogs,"Work logs"));
        myLists.add(new UserList(R.drawable.information,"About"));
        adapter = new UserAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                notifications();
                notificationsLive();
            }
        }, delay);
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}