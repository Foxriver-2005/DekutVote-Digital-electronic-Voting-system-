package com.lelei.dekutvote.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lelei.dekutvote.LiveUpdatesSelected;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.UserVote;
import com.lelei.dekutvote.UserVoteSelect;
import com.lelei.dekutvote.model.UserVoteList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserVoteAdapter extends RecyclerView.Adapter<UserVoteAdapter.ViewHolder> {
    private List<UserVoteList> myListList;
    private Context ct;
    public UserVoteAdapter(List<UserVoteList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.vote_list,parent,false);
        return new ViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        UserVoteList myList=myListList.get(position);
        holder.titleView.setText(myList.getTitle());
        holder.startView.setSelected(true);
        holder.endView.setSelected(true);
        holder.titleView.setSelected(true);
        holder.timeView.setSelected(true);
        String[] values;
        ArrayList<String> allowedList = new ArrayList<>();
        String allowedString = myList.getAllowed(); // Assuming this returns a string like "[BScIT, BScCS, BBIT]"

// Remove surrounding brackets and split by comma
        allowedString = allowedString.replace("[", "").replace("]", "");
        String[] val = allowedString.split(",");

// Add each value to the list, trimming any extra spaces
        for (String value : val) {
            allowedList.add(value.trim()); // Use trim() to remove any leading/trailing spaces
        }
        if (myList.getList().contains(myList.getId())){
            holder.doneView.setVisibility(View.VISIBLE);
            holder.startView.setVisibility(View.GONE);
            holder.endView.setVisibility(View.GONE);
            holder.timeView.setVisibility(View.VISIBLE);
            long timeRemaining = myList.getEnd().getTime() - new Date().getTime();
            new CountDownTimer(timeRemaining, 1000) {
                StringBuilder time = new StringBuilder();
                @Override
                public void onFinish() {
                    holder.timeView.setText(DateUtils.formatElapsedTime(0));
                    holder.timeView.setVisibility(View.GONE);
                    //mTextView.setText("Times Up!");
                }
                @Override
                public void onTick(long millisUntilFinished) {
                    Long serverUptimeSeconds =
                            millisUntilFinished  / 1000;
                    String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                    String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                    String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                    String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                    int days = Integer.parseInt(daysLeft);
                    int hrs = Integer.parseInt(hoursLeft);
                    int mins = Integer.parseInt(minutesLeft);
                    if(days >1){
                        holder.timeView.setText(daysLeft +" days " + hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours" + " left");
                    }else if( days == 1){
                        holder.timeView.setText(daysLeft +" day " + hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours"+ " left");
                    }else if( days ==0){
                        holder.timeView.setText(hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours"+ " left");
                    }
                }
            }.start();
        }else{
            holder.doneView.setVisibility(View.GONE);
            holder.resultsView.setVisibility(View.GONE);
            if (new Date().after(myList.getStart())) {
                if (new Date().after(myList.getEnd())){
                    holder.startView.setVisibility(View.GONE);
                    holder.endView.setVisibility(View.GONE);
                    holder.timeView.setText("");
                    holder.timeView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.close_vote, 0, 0, 0);
                    holder.timeView.setVisibility(View.VISIBLE);
                    holder.resultsView.setVisibility(View.VISIBLE);
                }else{
                    //   Toast.makeText(ct, myList.getEnd().toString() + " time now :"+ timeStampFormat.format(new Date().getTime()), Toast.LENGTH_SHORT).show();
                    long timeRemaining = myList.getEnd().getTime() - new Date().getTime();
                    new CountDownTimer(timeRemaining, 1000) {
                        StringBuilder time = new StringBuilder();
                        @Override
                        public void onFinish() {
                            holder.timeView.setText(DateUtils.formatElapsedTime(0));
                            //mTextView.setText("Times Up!");
                        }
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Long serverUptimeSeconds =
                                    millisUntilFinished  / 1000;
                            String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                            String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                            String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);
                            String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                            int days = Integer.parseInt(daysLeft);
                            int hrs = Integer.parseInt(hoursLeft);
                            int mins = Integer.parseInt(minutesLeft);
                            if(days >1){
                                holder.timeView.setText("Ends in: " +daysLeft +" days " + hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours");
                            }else if( days == 1){
                                holder.timeView.setText("Ends in: " +daysLeft +" day " + hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours");
                            }else if( days ==0){
                                holder.timeView.setText("Ends in: " +hoursLeft + ":" + minutesLeft + ":" + secondsLeft +" hours");
                            }
                        }
                    }.start();
                    holder.startView.setVisibility(View.GONE);
                    holder.endView.setVisibility(View.GONE);
                    holder.timeView.setText("Vote now");
                    holder.timeView.setVisibility(View.VISIBLE);
                    holder.liveImage.setVisibility(View.VISIBLE);
                }
            }else{
                holder.timeView.setVisibility(View.GONE);
                //Toast.makeText(ct, myList.getStart().toString() + " : " + myList.getEnd().toString(), Toast.LENGTH_SHORT).show();
                holder.startView.setText("Starts: " +timeStampFormat.format(myList.getStart()));
                holder.endView.setText("Ends: "+timeStampFormat.format(myList.getEnd()));
            }
        }
        if (UserProfile.category.matches("vote")){

        }
        else if (UserProfile.category.matches("live")){
            holder.resultsView.setVisibility(View.GONE);
        }
        else if (UserProfile.category.matches("create_election")){
            holder.deleteElection.setVisibility(View.VISIBLE);
            holder.liveImage.setVisibility(View.GONE);
            holder.resultsView.setVisibility(View.GONE);
            holder.editElection.setVisibility(View.VISIBLE);
        }
        holder.deleteElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserProfile.userType.matches("Faculty")){
                    if (UserProfile.creatorId.matches(myList.getCreator_id())){
                        DocumentReference up = mStore.collection("Election").document(myList.getId());
                        up.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(UserVote.parentLayout, myList.getTitle() + " successfully deleted", Snackbar.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(UserVote.parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                            }
                        });

                    }else{
                        Snackbar.make(UserVote.parentLayout, "[Failed] You didn't create this election", Snackbar.LENGTH_LONG).show();
                    }

                }else{
                    DocumentReference up = mStore.collection("Election").document(myList.getId());
                    up.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(UserVote.parentLayout, myList.getTitle() + " successfully deleted", Snackbar.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(UserVote.parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        holder.editElection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserProfile.userType.matches("Faculty")){
                    if (UserProfile.creatorId.matches(myList.getCreator_id())){
                        View snackView = v;
                        holder.myLayout = LayoutInflater.from(ct).inflate(R.layout.edit, null);
                        EditText editText = (EditText) holder.myLayout.findViewById(R.id.editName);
                        editText.setHint(myList.getTitle());
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                        builder.setView(holder.myLayout).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        androidx.appcompat.app.AlertDialog dialog = builder.create();
                        dialog.setCancelable(false);
                        dialog.show();
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View dV) {
                                DocumentReference up = mStore.collection("Election").document(myList.getId());
                                up.update("title",editText.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dialog.dismiss();
                                        Snackbar.make(UserVote.parentLayout, "Successfully renamed", Snackbar.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(dV, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View dV) {
                                dialog.dismiss();
                                Snackbar.make(UserVote.parentLayout, "Cancelled", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }else{
                        Snackbar.make(UserVote.parentLayout, "[Failed] You didn't create this election", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    View snackView = v;
                    holder.myLayout = LayoutInflater.from(ct).inflate(R.layout.edit, null);
                    EditText editText = (EditText) holder.myLayout.findViewById(R.id.editName);
                    editText.setHint(myList.getTitle());
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                    builder.setView(holder.myLayout).setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);

                    if (ct instanceof Activity && !((Activity) ct).isFinishing()) {
                        dialog.show();
                    }

                    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View dV) {
                            DocumentReference up = mStore.collection("Election").document(myList.getId());
                            up.update("title", editText.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            UserVote.myLists.remove(position);
                                            UserVote.adapter = new UserVoteAdapter(UserVote.myLists, ct);
                                            UserVote.rv.setAdapter(UserVote.adapter);
                                            if (dialog.isShowing()) {
                                                dialog.dismiss();
                                            }
                                            Snackbar.make(UserVote.parentLayout, "Successfully renamed", Snackbar.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(dV, e.getMessage(), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });

                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View dV) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            Snackbar.make(UserVote.parentLayout, "Cancelled", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        holder.resultsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.getCurrentUser().getUid();
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
                                logsList.put(new Date().toString(),"Reviewed the latest tally results of (" + myList.getTitle()+").");
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
                Intent myIntent = new Intent(ct, LiveUpdatesSelected.class);
                myIntent.putExtra("key", myList.getId());
                if (holder.timeView.getText().equals("") && holder.timeView.getVisibility() == View.VISIBLE){
                    myIntent.putExtra("election_status", "CLOSED");
                }else {
                    myIntent.putExtra("election_status", "ONGOING");
                }
                ct.startActivity(myIntent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserProfile.category.equals("vote")){
                    Log.d("DEBUG", "UserProfile.myCourse: " + UserProfile.myCourse);
                    Log.d("DEBUG", "allowedList: " + allowedList);
                    if (myList.getList().contains(myList.getId())){
                        UserVote.showFinish();
                    }else{
                        if (new Date().after(myList.getStart())) {
                            holder.startView.setText("Starts: "+timeStampFormat.format(myList.getStart()));
                            holder.endView.setText("Ends: "+timeStampFormat.format(myList.getEnd()));
                            if (UserProfile.category.equals("vote")){
                                if (holder.timeView.getText().equals("") && holder.timeView.getVisibility() == View.VISIBLE){
                                    Snackbar.make(view, "Election is already done", Snackbar.LENGTH_LONG).show();
                                }else{
                                    if (allowedList.contains(UserProfile.myCourse) || allowedList.contains("ALL")){
                                        holder.myLayout = LayoutInflater.from(ct).inflate(R.layout.vote_enter, null);
                                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ct);
                                        Button voteCancel = (Button)holder.myLayout.findViewById(R.id.voteCancel);
                                        voteCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                holder.dialog.dismiss();
                                            }
                                        });
                                        Button voteProceed = (Button)holder.myLayout.findViewById(R.id.voteProceed);
                                        voteProceed.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mStore.collection("Users")
                                                        .document(mAuth.getCurrentUser().getUid())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                                                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                                                mAuth.getCurrentUser().getUid();
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
                                                                                logsList.put(new Date().toString(),"Entered voting section (" + myList.getTitle()+").");
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
                                                                String[] values;
                                                                ArrayList<String> electionsParticipated = new ArrayList<>();
                                                                DocumentSnapshot document = task.getResult();
                                                                String currentString = document.get("elections_participated").toString();
                                                                values = String.valueOf(currentString).replace("[", "").replace("]", "").replace(" ","").split(",");
                                                                for (int x = 0; x < values.length;x++){
                                                                    electionsParticipated.add(values[x]);
                                                                }
                                                                // List<String> list = new ArrayList<>();
                                                                electionsParticipated.add(myList.getId());
                                                                mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("elections_participated",electionsParticipated).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        holder.dialog.dismiss();
                                                                        Intent myIntent = new Intent(ct, UserVoteSelect.class);
                                                                        myIntent.putExtra("key", myList.getId());
                                                                        myIntent.putExtra("title", myList.getTitle());
                                                                        ct.startActivity(myIntent);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        holder.dialog.dismiss();
                                                                        Snackbar.make(view, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                            }
                                                        });
                                            }
                                        });
                                        builder.setView(holder.myLayout);
                                        holder.dialog = builder.create();
                                        holder.dialog.setCancelable(false);
                                        holder.dialog.show();

                                    }else{
                                        Snackbar.make(view, "Only "+allowedList+" are allowed to participate.", Snackbar.LENGTH_LONG).show();
                                    }
                                }
                            }
                            if (UserProfile.category.equals("live")){
                                FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.getCurrentUser().getUid();
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
                                                logsList.put(new Date().toString(),"Reviewed the latest tally results of (" + myList.getTitle()+").");
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
                                Intent myIntent = new Intent(ct, LiveUpdatesSelected.class);
                                myIntent.putExtra("key", myList.getId());
                                if (holder.timeView.getText().equals("") && holder.timeView.getVisibility() == View.VISIBLE){
                                    myIntent.putExtra("election_status", "CLOSED");
                                }else {
                                    myIntent.putExtra("election_status", "ONGOING");
                                }
                                ct.startActivity(myIntent);
                            }
                        }else{
                            UserVote.showClose();
                        }
                    }
                }else if(UserProfile.category.equals("live")){
                    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.getCurrentUser().getUid();
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
                                    logsList.put(new Date().toString(),"Reviewed the latest tally results of (" + myList.getTitle()+").");
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
                    Intent myIntent = new Intent(ct, LiveUpdatesSelected.class);
                    myIntent.putExtra("key", myList.getId());
                    if (holder.timeView.getText().equals("") && holder.timeView.getVisibility() == View.VISIBLE){
                        myIntent.putExtra("election_status", "CLOSED");
                    }else {
                        myIntent.putExtra("election_status", "ONGOING");
                    }
                    ct.startActivity(myIntent);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        UserVote userVote;
        private TextView titleView;
        private Button resultsView;
        private ImageView liveImage,deleteElection,editElection;
        private TextView timeView,startView,endView,doneView;
        View myLayout;
        androidx.appcompat.app.AlertDialog dialog;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userVote = new UserVote();
            titleView=(TextView)itemView.findViewById(R.id.voteTitle);
            timeView=(TextView)itemView.findViewById(R.id.voteTime);
            startView=(TextView)itemView.findViewById(R.id.startTime);
            endView=(TextView)itemView.findViewById(R.id.endTime);
            doneView=(TextView)itemView.findViewById(R.id.doneText);
            resultsView=(Button)itemView.findViewById(R.id.resultsView);
            liveImage = (ImageView) itemView.findViewById(R.id.liveImage);
            deleteElection= (ImageView) itemView.findViewById(R.id.deleteElection);
            editElection= (ImageView) itemView.findViewById(R.id.editElection);
        }
    }
}
