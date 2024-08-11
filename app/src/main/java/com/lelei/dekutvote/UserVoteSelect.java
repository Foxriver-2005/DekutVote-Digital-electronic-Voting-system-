package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.Adapters.BulletVotingAdapter;
import com.lelei.dekutvote.Adapters.UserVoteSelectAdapter;
import com.lelei.dekutvote.model.BulletVotingList;
import com.lelei.dekutvote.model.UserVoteSelectList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserVoteSelect extends AppCompatActivity {

    String electionId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    public static List<UserVoteSelectList> myLists;
    RecyclerView rv;
    UserVoteSelectAdapter adapter;
    List<BulletVotingList> myListsParty;
    RecyclerView rvParty;
    BulletVotingAdapter adapterParty;
    Intent intent;
    Bundle bundle;
    ArrayList<String> positionList;
    ArrayList<String> candidateList;
    ArrayList<String> detailsList;
    ArrayList<String> limitList;
    ArrayList<String> partyList;
    TextView textPosition,textNumber;
    Button buttonNext;
    int x = 0;
    TextView pageNumber;
    LayoutInflater inflater;
    View myLayout,myLayoutBullet;

    public static androidx.appcompat.app.AlertDialog dialogBullet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vote_select);
        rv = (RecyclerView) findViewById(R.id.uservoteselectrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        myLists = new ArrayList<>();
        textPosition = (TextView) findViewById(R.id.textPosition);
        textNumber  = (TextView) findViewById(R.id.textNumber);
        mStore = FirebaseFirestore.getInstance();
        intent= getIntent();
        bundle = intent.getExtras();
        positionList = new ArrayList<>();
        candidateList = new ArrayList<>();
        detailsList = new ArrayList<>();
        partyList = new ArrayList<>();
        limitList = new ArrayList<>();
        buttonNext  = (Button) findViewById(R.id.buttonNext);
        pageNumber = findViewById(R.id.pageNumber);
        inflater = getLayoutInflater();
        myLayout = inflater.inflate(R.layout.vote_finish, null);
        myLayoutBullet = inflater.inflate(R.layout.bullet_voting, null);
        mAuth = FirebaseAuth.getInstance();
        showBullet();
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x < positionList.size()){
                    detailsList.clear();
                    candidateList.clear();
                    clearView();
                    getdata();
                }else{
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(UserVoteSelect.this);
                    Button buttonSend = (Button)myLayout.findViewById(R.id.voteFinish);
                    buttonSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mStore.collection("Users")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Map<String,Object> logs = new HashMap<>();
                                            mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                            logsList.put(new Date().toString(),"Finished voting ("+bundle.get("title").toString()+")" );
                                                            DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                                            documentReference.update("logs",logsList);
                                                        } else {
                                                            Log.d("TAG", "No such document");
                                                        }
                                                    } else {
                                                        Log.d("TAG", "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                            Intent myIntent = new Intent(UserVoteSelect.this, UserProfile.class);
                                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            UserVoteSelect.this.startActivity(myIntent);
                                        }

                                    });
                        }
                    });
                    builder.setView(myLayout);
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                }

            }
        });
        mStore.collection("Election")
                .document(bundle.get("key").toString())
                .collection("limit").document("position")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();

                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            limitList.add(entry.getValue().toString());
                        }
                    }
                }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        mStore.collection("Election")
                                .document(bundle.get("key").toString())
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentReference documentReference = mStore.collection("Users").document(UserProfile.creatorId).collection("receipts").document(bundle.get("key").toString());
                                            Map <String,Object> receipts = new HashMap<>();
                                            DocumentReference documentReference1 = mStore.collection("Users").document(UserProfile.creatorId).collection("others").document("receipts");
                                            Map <String,Object> receipts1 = new HashMap<>();
                                            DocumentReference documentReference2 = mStore.collection("Users").document(UserProfile.creatorId).collection("receipts_details").document(bundle.get("key").toString());
                                            Map <String,Object> receipts2 = new HashMap<>();
                                            List<String> positions = new ArrayList<>();
                                            positions.clear();
                                            String[] values;
                                            values = String.valueOf(task.getResult().get("position").toString()).replace("[", "").replace("]", "").replace("","").split(", ");
                                            for (int x = 0; x < values.length;x++){
                                                positionList.add(values[x]);
                                                List<String> toInsert = new ArrayList<>();
                                                positions.add(values[x]);
                                                receipts.put(values[x].toString(),toInsert.toString());
                                            }
                                            receipts1.put(bundle.get("key").toString(),positions);
                                            receipts2.put("date_voted",new Date());
                                            receipts2.put("election_name",bundle.get("title").toString());
                                            documentReference.set(receipts);
                                            documentReference1.set(receipts1);
                                            documentReference2.set(receipts2);
                                            getdata();
                                            pageNumber.setText("Page 1 of "+positionList.size());
                                        }
                                    }
                                });
                    }
                });
    }
    public void getCandidates(String positionId, String limit) {
        mStore.collection("Election")
                .document(bundle.get("key").toString())
                .collection("party-list").document(positionId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> map = document.getData();
                                if (map != null) {
                                    candidateList.clear();
                                    detailsList.clear();
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        candidateList.add(entry.getValue().toString());
                                        detailsList.add(entry.getKey());
                                    }
                                    // Clear and re-populate the list
                                    myLists.clear();
                                    for (int y = 0; y < candidateList.size(); y++) {
                                        myLists.add(new UserVoteSelectList(positionId, candidateList.get(y), detailsList.get(y), bundle.get("key").toString(), positionId, Integer.valueOf(limit), false));
                                    }
                                    displayView();
                                } else {
                                    Log.d("TAG", "No data found");
                                }
                            } else {
                                Log.d("TAG", "Document does not exist");
                            }
                        } else {
                            Log.d("TAG", "Failed to retrieve data", task.getException());
                        }
                    }
                });
    }
    public void getLimit(String c1,String d1,String position){
        DocumentReference documentReference = mStore.collection(c1).document(d1).collection("limit").document("position");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {

                    } else {
                        Log.d("LOGGER", "No such document");
                    }

                } else {
                    Log.d("LOGGER", "get failed with ", task.getException());
                }
                //textNumber.setText("Choose "+limitList.get(x)+" below");
                textNumber.setText("Choose below");
            }
        });

    }
    public void clearView() {
        if (myLists != null) {
            myLists.clear();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    public void displayView() {
        if (adapter == null) {
            adapter = new UserVoteSelectAdapter(myLists, this);
            rv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
    public void candidProfile(Context context,String id){
        FirebaseFirestore cStore = FirebaseFirestore.getInstance();
        cStore.collection("Users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.exists()){
                                    myLayout = LayoutInflater.from(context).inflate(R.layout.user_box, null);
                                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                                    TextView profileId = (TextView)myLayout.findViewById(R.id.profileId);
                                    TextView profileName = (TextView)myLayout.findViewById(R.id.profileName);
                                    TextView profileDetails = (TextView)myLayout.findViewById(R.id.profileDetails);
                                    TextView profileMotto = (TextView)myLayout.findViewById(R.id.profileMotto);
                                    String strDetails = document.getString("course").trim();
                                    profileId.setText(id);
                                    profileName.setText(document.getString("firstname") +" " + document.getString("middlename") +" " + document.getString("lastname") +" ");
                                    if (strDetails.isEmpty()){
                                        profileDetails.setText(null);
                                    }else{
                                        profileDetails.setText(strDetails);
                                    }
                                    builder.setView(myLayout).setPositiveButton("", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });

                                    builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //     dialog.cancel();
                                            // finish();
                                        }
                                    });
                                    androidx.appcompat.app.AlertDialog dialog = builder.create();

                                    dialog.setCancelable(false);
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

                                    Button profileClose = (Button) myLayout.findViewById(R.id.profileClose);
                                    profileClose.setOnClickListener(new View.OnClickListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }

                                    });
                                }
                            }

                        } else {

                        }
                    }
                });
    }
    public void showBullet(){
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(UserVoteSelect.this);
        rvParty = (RecyclerView) myLayoutBullet.findViewById(R.id.partyrec);
        rvParty.setHasFixedSize(true);
        rvParty.setLayoutManager(new GridLayoutManager(this, 1));
        myListsParty = new ArrayList<>();
        builder.setView(myLayoutBullet);
        dialogBullet = builder.create();
        mStore.collection("Election").document(bundle.get("key").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    if (task.getResult().exists()){
                        String[] values;
                        ArrayList<String> partylist = new ArrayList<>();
                        values = String.valueOf(task.getResult().get("party").toString()).replace("[", "").replace("]", "").replace("","").split(", ");
                        if (!values.toString().matches("")){

                            for (int x = 0; x < values.length;x++){

                                if (!values[x].matches("Independent")){
                                    partylist.add(values[x]);
                                    myListsParty.add(new BulletVotingList(bundle.get("key").toString(),values[x],bundle.get("title").toString(),true));
                                }
                            }
                            myListsParty.add(new BulletVotingList(null,null,bundle.get("title").toString(),false));
                            adapterParty = new BulletVotingAdapter(myListsParty, UserVoteSelect.this);
                            rvParty.setAdapter(adapterParty);
                        }else{
                            dialogBullet.dismiss();
                        }
                    }else{
                        dialogBullet.dismiss();
                    }
                }

            }
        });
        dialogBullet.setCancelable(false);
        dialogBullet.show();
    }
    public void getdata() {
        int num = x+1;
        pageNumber.setText("Page "+ num+" of "+positionList.size());
        if (x < positionList.size()){
            textPosition.setText(positionList.get(x));
            textPosition.setSelected(true);
            if (limitList.get(x).equals("1")){
                textNumber.setVisibility(View.GONE);
                getCandidates(positionList.get(x),limitList.get(x));
            }else{
                textNumber.setVisibility(View.VISIBLE);
                textNumber.setText("Select ("+limitList.get(x)+") on the list");

                getCandidates(positionList.get(x),limitList.get(x));
            }
        }
        if(x == positionList.size() - 1){
            buttonNext.setText("Finish");
        }
        x++;
    }
    @Override
    public void onBackPressed()
    {
        myLayout = LayoutInflater.from(this).inflate(R.layout.back_press, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        Button voteCancel = (Button)myLayout.findViewById(R.id.voteCancel);
        Button voteLeave = (Button)myLayout.findViewById(R.id.voteLeave);
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
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
            }
        });

        voteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        voteLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(UserVoteSelect.this, UserVote.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                UserVoteSelect.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_out_right, R.anim.no_animation);
            }
        });
    }
}