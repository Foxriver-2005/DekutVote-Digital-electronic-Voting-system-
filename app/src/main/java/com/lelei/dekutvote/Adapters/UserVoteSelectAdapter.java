package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.UserVoteSelect;
import com.lelei.dekutvote.model.UserVoteSelectList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserVoteSelectAdapter extends RecyclerView.Adapter<UserVoteSelectAdapter.ViewHolder> {
    private List<UserVoteSelectList> myListList;
    private Context ct;
    private Integer counts = 0;
    public UserVoteSelectAdapter(List<UserVoteSelectList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserVoteSelectList myList = myListList.get(position);
        holder.selectName.setText(myList.getName());
        holder.selectDetails.setText(myList.getDetails());
        holder.selectName.setSelected(true);
        holder.selectDetails.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                mStore.collection("Election").document(myList.getDocu1()).collection("profile").document(myList.getDocu2())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists()){
                                    holder.userVoteSelect.candidProfile(ct,task.getResult().getString(myList.getName()));
                                }else{
                                    //  Toast.makeText(ct, "not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        holder.selectView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.selectView.getText().equals("Voted")){
                    myList.setHasVoted(false);
                    holder.selectView.setText("Vote");
                    holder.selectView.setTextColor(ContextCompat.getColor(ct,R.color.mainColor));
                    holder.selectView.setBackgroundColor(ContextCompat.getColor(ct,R.color.white));
                    FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                    DocumentReference ref = mStore.collection("Election").document(myList.getDocu1()).collection("tally").document(myList.getDocu2());
                    ref.update(myList.getName(), FieldValue.increment(-1));
                    DocumentReference per = mStore.collection("Election").document(myList.getDocu1()).collection("total").document(myList.getDocu2());
                    per.update("total_votes", FieldValue.increment(-1));
                    DocumentReference up = mStore.collection("Election").document(myList.getDocu1());
                    up.update("updates", FieldValue.increment(-1));
                    counts--;
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
                                    logsList.put(new Date().toString(),"You withdrew your vote '" + myList.getName() + "' for '" +myList.getDocu2()+"'.");
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
                    List<String> candidList = new ArrayList<>();
                    candidList.clear();
                    for (int x = 0; x<UserVoteSelect.myLists.size(); x++){
                        if (UserVoteSelect.myLists.get(x).getHasVoted() == true){
                            candidList.add(UserVoteSelect.myLists.get(x).getName());
                        }
                    }
                    Map <String,Object> receipts = new HashMap<>();
                    receipts.put(myList.getPosition(),candidList.toString());
                    DocumentReference documentReference1 = mStore.collection("Users").document(UserProfile.creatorId).collection("receipts").document(myList.getDocu1());
                    documentReference1.update(receipts);
                }else{
                    if(counts < myList.getLimit()){
                        myList.setHasVoted(true);
                        holder.selectView.setText("Voted");
                        holder.selectView.setTextColor(ContextCompat.getColor(ct,R.color.white));
                        holder.selectView.setBackgroundColor(ContextCompat.getColor(ct,R.color.mainColor));
                        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                        DocumentReference ref = mStore.collection("Election").document(myList.getDocu1()).collection("tally").document(myList.getDocu2());
                        DocumentReference per = mStore.collection("Election").document(myList.getDocu1()).collection("total").document(myList.getDocu2());
                        per.update("total_votes", FieldValue.increment(1));
                        ref.update(myList.getName(), FieldValue.increment(1));
                        DocumentReference up = mStore.collection("Election").document(myList.getDocu1());
                        up.update("updates", FieldValue.increment(1));
                        counts++;
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
                                        logsList.put(new Date().toString(),"You voted '" + myList.getName() + "' for '" +myList.getDocu2()+"'.");
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

                        List<String> candidList = new ArrayList<>();
                        candidList.clear();
                        for (int x = 0; x<UserVoteSelect.myLists.size(); x++){
                            if (UserVoteSelect.myLists.get(x).getHasVoted() == true){
                                candidList.add(UserVoteSelect.myLists.get(x).getName());
                            }
                        }
                        Map <String,Object> receipts = new HashMap<>();
                        receipts.put(myList.getPosition(),candidList.toString());
                        DocumentReference documentReference1 = mStore.collection("Users").document(UserProfile.creatorId).collection("receipts").document(myList.getDocu1());
                        documentReference1.update(receipts);
                    }else{
                        Snackbar.make(view, "limit : "+myList.getLimit().toString()+" vote(s)", Snackbar.LENGTH_LONG).show();
                        //      Toast.makeText(ct,"limit : "+myList.getLimit().toString()+" vote(s)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView selectName;
        private TextView selectDetails;
        private Button selectView;
        UserVoteSelect userVoteSelect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userVoteSelect = new UserVoteSelect();
            selectName = (TextView) itemView.findViewById(R.id.selectName);
            selectDetails = (TextView) itemView.findViewById(R.id.selectDetails);
            selectView = (Button) itemView.findViewById(R.id.selectView);
        }
    }
}