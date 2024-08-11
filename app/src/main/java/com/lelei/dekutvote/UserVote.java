package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.Adapters.UserVoteAdapter;
import com.lelei.dekutvote.model.UserVoteList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserVote extends AppCompatActivity {
    String electionId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    public static List<UserVoteList> myLists;
    public static RecyclerView rv;
    public static UserVoteAdapter adapter;
    List<String> documentlist;
    ArrayList<String> electionsParticipated;
    TextView collectionText;
    Intent intent;
    Bundle bundle;
    public static String type;
    String[] values;
    Boolean isUpdated;
    public static View parentLayout;
    Switch showAvailable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vote);
        rv = (RecyclerView) findViewById(R.id.uservoterec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        myLists = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        intent = getIntent();
        bundle = intent.getExtras();
        documentlist = new ArrayList<>();
        electionsParticipated = new ArrayList<>();
        isUpdated = false;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        showAvailable = (Switch) findViewById(R.id.showAvailable);

        if ("Administrator".equals(UserProfile.userType)) {
            showAvailable.setVisibility(View.GONE);
            showAvailable.setChecked(false);
        } else {
            if (preferences.getBoolean("show", true)) {
                showAvailable.setChecked(true);
            } else {
                showAvailable.setChecked(false);
            }
        }

        showAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("show", isChecked);
                editor.apply();
                start();
            }
        });

        CardView electionCard = (CardView) findViewById(R.id.electionCard);
        ImageButton electionAdd = (ImageButton) findViewById(R.id.electionAdd);
        parentLayout = findViewById(android.R.id.content);

        if ("Student".equals(UserProfile.userType)) {
            // Do something for Student
        } else if ("Faculty".equals(UserProfile.userType)) {
            if ("create_election".equals(UserProfile.category)) {
                electionAdd.setVisibility(View.VISIBLE);
                electionCard.setVisibility(View.VISIBLE);
            }
        } else if ("Administrator".equals(UserProfile.userType)) {
            if ("create_election".equals(UserProfile.category)) {
                electionAdd.setVisibility(View.VISIBLE);
                electionCard.setVisibility(View.VISIBLE);
            }
        }

        electionAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UserVote.this, AdminElection.class);
                startActivity(myIntent);
            }
        });

        ImageView vote_image = (ImageView) findViewById(R.id.vote_image);
        TextView typeText = (TextView) findViewById(R.id.typeText);

        if ("vote".equals(UserProfile.category)) {
            vote_image.setBackgroundResource(R.drawable.select);
            typeText.setText("Vote");
        } else if ("poll".equals(UserProfile.category)) {
            vote_image.setBackgroundResource(R.drawable.counts);
            typeText.setText("Poll");
        } else if ("live".equals(UserProfile.category)) {
            vote_image.setBackgroundResource(R.drawable.counts);
            typeText.setText("Tally");
        } else if ("create_election".equals(UserProfile.category)) {
            vote_image.setBackgroundResource(R.drawable.counts);
            typeText.setText("Administer election");
        } else if ("conduct_poll".equals(UserProfile.category)) {
            vote_image.setBackgroundResource(R.drawable.counts);
            typeText.setText("Conduct a poll");
        }

        clearData();
        start();
    }
    public void clearData(){
        electionsParticipated.clear();
        documentlist.clear();
        myLists.clear();
        adapter = new UserVoteAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
    public void getElectionsParticipated(){
        String userId = mAuth.getCurrentUser().getUid();
        mStore.collection("Users")
                .document(userId)
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.exists()){
                            String currentString = value.get("elections_participated").toString();
                            values = String.valueOf(currentString).replace("[", "").replace("]", "").replace(" ","").split(",");
                            for (int x = 0; x < values.length;x++){
                                electionsParticipated.add(values[x]);
                            }
                        }
                    }
                });
    }
    public void getSpecificDocument(String docuID){
        DocumentReference documentReference = mStore.collection("Election").document(docuID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(isUpdated == true){
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else{
                    if (value.exists()){
                        Timestamp timestamp = (Timestamp) value.getTimestamp("date_created");
                        Date date_created = timestamp.toDate();
                        Timestamp timestamp1 = (Timestamp) value.getTimestamp("start");
                        Date start_date = timestamp1.toDate();
                        Timestamp timestamp2 = (Timestamp) value.getTimestamp("end");
                        Date end_date = timestamp2.toDate();
                        myLists.add(new UserVoteList(value.getString("title"),value.get("allowed").toString(),date_created,start_date,end_date, docuID,electionsParticipated,value.getString("creator_id"),value.getString("creator_name")));
                        getdata();
                    }
                }
            }
        });
    }
    static public void showFinish(){
        Snackbar.make(parentLayout, "You have already voted.", Snackbar.LENGTH_LONG)
                .show();
    }
    static public void showClose(){
        Snackbar.make(parentLayout, "This section is not yet open", Snackbar.LENGTH_LONG)
                .show();
    }
    public void getdata() {
        adapter = new UserVoteAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
    public void start(){
        getElectionsParticipated();
        mStore.collection("Election").whereEqualTo("approved", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()){
                            if (showAvailable.isChecked()){
                                String[] values;
                                ArrayList<String> allowedList = new ArrayList<>();
                                values = String.valueOf(document.get("allowed").toString()).replace("[", "").replace("]", "").replace(" ","").split(",");
                                for (int x = 0; x < values.length;x++){
                                    allowedList.add(values[x]);
                                }
                                if (allowedList.contains(UserProfile.myCourse) || allowedList.contains("ALL")){
                                    documentlist.add(document.getId());
                                    getSpecificDocument(document.getId());
                                }
                            }else{
                                documentlist.add(document.getId());
                                getSpecificDocument(document.getId());
                            }
                        }
                        else{
                            View parentLayout = findViewById(android.R.id.content);
                            Snackbar.make(parentLayout, "No elections yet", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
                if (task.isComplete()){
                    new CountDownTimer(1000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                        @Override
                        public void onFinish() {
                            isUpdated = true;
                        }
                    }.start();
                }
            }
        });
    }
    @Override
    public void onBackPressed()
    {
        Intent myIntent = new Intent(UserVote.this, UserProfile.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        UserVote.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_out_right, R.anim.no_animation);
    }

}