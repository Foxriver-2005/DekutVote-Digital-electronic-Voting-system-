package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserPoll;
import com.lelei.dekutvote.UserPollSelect;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.model.UserPollList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPollAdapter extends RecyclerView.Adapter<UserPollAdapter.ViewHolder> {
    private List<UserPollList> myListList;
    private Context ct;
    public UserPollAdapter(List<UserPollList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_list,parent,false);
        return new ViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserPollList myList=myListList.get(position);
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        holder.titleView.setText(myList.getTitle());
        holder.timeView.setText("Date created: " + DateFormat.format("MMM dd yyyy hh:mm aa",myList.getTime()));
        holder.descView.setText(myList.getDescription());
        holder.titleView.setSelected(true);
        holder.timeView.setSelected(true);
        if (myList.getList().contains(myList.getId())){
            //   holder.timeView.setVisibility(View.GONE);
        }else{

        }
        if (UserProfile.category.matches("poll")){

        }
        else if (UserProfile.category.matches("create_poll")){
            holder.deletePoll.setVisibility(View.VISIBLE);
            //  holder.editPoll.setVisibility(View.VISIBLE);
        }
        holder.deletePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserProfile.userType.matches("Faculty")){
                    if (UserProfile.creatorId.matches(myList.getCreator_id())){
                        DocumentReference up = mStore.collection("Poll").document(myList.getId());
                        up.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Snackbar.make(UserPoll.parentLayout, "Deleted successfully.", Snackbar.LENGTH_LONG).show();
                                UserPoll.myLists.remove(position);
                                UserPoll.adapter = new UserPollAdapter(UserPoll.myLists,ct);
                                UserPoll.rv.setAdapter(UserPoll.adapter);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(UserPoll.parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }else {
                        Snackbar.make(UserPoll.parentLayout, "[Failed] You didn't create this poll", Snackbar.LENGTH_LONG).show();
                    }
                }else{
                    DocumentReference up = mStore.collection("Poll").document(myList.getId());
                    up.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(UserPoll.parentLayout, "Deleted successfully.", Snackbar.LENGTH_LONG).show();
                            UserPoll.myLists.remove(position);
                            UserPoll.adapter = new UserPollAdapter(UserPoll.myLists,ct);
                            UserPoll.rv.setAdapter(UserPoll.adapter);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(UserPoll.parentLayout, e.getMessage().toString(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

            }
        });
        String[] values;
        ArrayList<String> allowedList = new ArrayList<>();;
        values = String.valueOf(myList.getAllowed()).replace("[", "").replace("]", "").replace(" ","").split(",");
        for (int x = 0; x < values.length;x++){
            allowedList.add(values[x]);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserProfile.category.equals("poll")){
                    if (UserProfile.userType.matches("Faculty") || UserProfile.userType.matches("Administrator")) {
                        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.getCurrentUser().getUid();
                        if (UserProfile.userType.equals("Faculty") || UserProfile.userType.equals("Administrator")){
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
                                            logsList.put(new Date().toString(),"Viewed a poll (" + myList.getTitle()+").");
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
                            Intent myIntent = new Intent(ct, UserPollSelect.class);
                            myIntent.putExtra("key", myList.getId());
                            myIntent.putExtra("title", myList.getTitle());
                            ct.startActivity(myIntent);
                        }else{
                            if (myList.getList().contains(myList.getId())){
                                //      Toast.makeText(ct, "done", Toast.LENGTH_SHORT).show();
                                Snackbar.make(view, "You have already participated in this poll", Snackbar.LENGTH_LONG).show();
                            }else{
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
                                                logsList.put(new Date().toString(),"Viewed a poll (" + myList.getTitle()+").");
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
                                Intent myIntent = new Intent(ct, UserPollSelect.class);
                                myIntent.putExtra("key", myList.getId());
                                myIntent.putExtra("title", myList.getTitle());
                                ct.startActivity(myIntent);
                            }
                        }
                    }else{
                        if (allowedList.contains(UserProfile.myCourse) || allowedList.contains("ALL"))
                        {
                            FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            mAuth.getCurrentUser().getUid();
                            if (UserProfile.userType.equals("Faculty") || UserProfile.userType.equals("Administrator")){
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
                                                logsList.put(new Date().toString(),"Viewed a poll (" + myList.getTitle()+").");
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
                                Intent myIntent = new Intent(ct, UserPollSelect.class);
                                myIntent.putExtra("key", myList.getId());
                                myIntent.putExtra("title", myList.getTitle());
                                ct.startActivity(myIntent);
                            }else{
                                if (myList.getList().contains(myList.getId())){
                                    //      Toast.makeText(ct, "done", Toast.LENGTH_SHORT).show();
                                    Snackbar.make(view, "You have already participated in this poll", Snackbar.LENGTH_LONG).show();
                                }else{
                                    // Toast.makeText(ct, "not yet", Toast.LENGTH_SHORT).show();
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
                                                                            logsList.put(new Date().toString(),"Entered poll section (" + myList.getTitle()+").");
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
                                                            ArrayList<String> pollsParticipated = new ArrayList<>();
                                                            DocumentSnapshot document = task.getResult();
                                                            String currentString = document.get("polls_participated").toString();
                                                            values = String.valueOf(currentString).replace("[", "").replace("]", "").replace(" ","").split(",");
                                                            for (int x = 0; x < values.length;x++){
                                                                pollsParticipated.add(values[x]);
                                                            }
                                                            // List<String> list = new ArrayList<>();
                                                            pollsParticipated.add(myList.getId());
                                                            mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("polls_participated",pollsParticipated).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Intent myIntent = new Intent(ct, UserPollSelect.class);
                                                                    myIntent.putExtra("key", myList.getId());
                                                                    myIntent.putExtra("title", myList.getTitle());
                                                                    ct.startActivity(myIntent);
                                                                    holder.dialog.dismiss();
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
                                }
                            }
                        }else{
                            Snackbar.make(view, "Only " + myList.getAllowed().toString() + " are allowed to answer this poll.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView titleView,timeView,allowedView,descView;
        View myLayout;
        androidx.appcompat.app.AlertDialog dialog;
        private ImageView deletePoll;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView=(TextView)itemView.findViewById(R.id.pollTitle);
            timeView=(TextView)itemView.findViewById(R.id.pollDate);
            //  allowedView=(TextView)itemView.findViewById(R.id.pollAllowed);
            descView=(TextView)itemView.findViewById(R.id.pollDesc);
            deletePoll= (ImageView) itemView.findViewById(R.id.deletePoll);
        }
    }
}
