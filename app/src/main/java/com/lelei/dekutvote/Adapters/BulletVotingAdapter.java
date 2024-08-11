package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.UserVoteSelect;
import com.lelei.dekutvote.model.BulletVotingList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulletVotingAdapter extends RecyclerView.Adapter<BulletVotingAdapter.ViewHolder> {
    private List<BulletVotingList> myListList;
    private Context ct;
    public BulletVotingAdapter(List<BulletVotingList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.party_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        BulletVotingList myList = myListList.get(position);
        holder.positionList= new ArrayList<>();
        if (myList.getId() != null){
            mStore.collection("Election")
                    .document(myList.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String[] values;
                                values = String.valueOf(task.getResult().get("position").toString()).replace("[", "").replace("]", "").replace("", "").split(", ");
                                for (int x = 0; x < values.length; x++) {
                                    holder.positionList.add(values[x]);
                                }
                            }
                        }
                    });
        }
        if (myList.getIsPartylist() == true){
            holder.partySet.setVisibility(View.VISIBLE);
            holder.manualSet.setVisibility(View.GONE);
            holder.partyName.setText(myList.getParty_name());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStore.collection("Election").document(myList.getId()).collection("party-list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(ct);
                                builder1.setMessage("You have chosen " + myList.getParty_name());
                                builder1.setCancelable(false);
                                builder1.setPositiveButton(
                                        "Proceed",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String details = "";
                                                details += "You have successfully voted all the candidates in " + myList.getParty_name() + System.getProperty("line.separator")+ System.getProperty("line.separator");
                                                List<String> candidList = new ArrayList<>();
                                                candidList.clear();
                                                DocumentReference documentReference1 = mStore.collection("Users").document(UserProfile.creatorId).collection("receipts").document(myList.getId());
                                                Map<String,Object> receipts = new HashMap<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    if (document.getString(myList.getParty_name()) == null){

                                                    }else{
                                                        DocumentReference ref = mStore.collection("Election").document(myList.getId()).collection("tally").document(document.getId());
                                                        ref.update(document.getString(myList.getParty_name()), FieldValue.increment(1));
                                                        DocumentReference per = mStore.collection("Election").document(myList.getId()).collection("total").document(document.getId());
                                                        per.update("total_votes", FieldValue.increment(1));
                                                        DocumentReference up = mStore.collection("Election").document(myList.getId());
                                                        up.update("updates", FieldValue.increment(1));
                                                        details += document.getId() + " - " + document.getString(myList.getParty_name()) + System.getProperty("line.separator");
                                                        //receipt
                                                        candidList.add(document.getString(myList.getParty_name()));
                                                    }
                                                    receipts.put(document.getId().toString(),candidList.toString());
                                                    documentReference1.update(receipts);
                                                    candidList.clear();
                                                }
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ct);
                                                //builder.setTitle(myList.getParty_name());
                                                builder.setMessage(details);
                                                builder.setCancelable(true);
                                                builder.setPositiveButton(
                                                        "Exit",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                                mStore.collection("Users")
                                                                        .document(mAuth.getCurrentUser().getUid())
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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
                                                                                                logsList.put(new Date().toString(),"You voted all the candidates in ("+myList.getParty_name()+")" + " in election "+  myList.getElection_name());
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
                                                                                Intent myIntent = new Intent(ct, UserProfile.class);
                                                                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                ct.startActivity(myIntent);
                                                                            }

                                                                        });
                                                            }
                                                        });
                                                AlertDialog alert = builder.create();
                                                alert.show();
                                                TextView messageView = (TextView)alert.findViewById(android.R.id.message);
                                                messageView.setGravity(Gravity.CENTER);
                                                dialog.cancel();
                                            }
                                        });
                                builder1.setNegativeButton(
                                        "Back",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        }
                    });
                }
            });
            holder.partyCandidates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.details = "";
                    holder.details +=  myList.getParty_name() + System.getProperty("line.separator")+ System.getProperty("line.separator");
                    mStore.collection("Election").document(myList.getId()).collection("party-list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && !task.getResult().isEmpty()){
                                for (int x = 0; x < holder.positionList.size(); x ++){
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (document.getId().matches(holder.positionList.get(x))){
                                            if (document.get(myList.getParty_name()) == null){
                                                holder.details += document.getId().toString() +" : " + " None" + System.getProperty("line.separator");
                                            }else{
                                                holder.details += document.getId().toString() +" : " +document.get(myList.getParty_name()) + System.getProperty("line.separator");
                                            }
                                        }
                                    }
                                }
                            }
                            show(holder.details);
                        }
                    });
                }
            });
        }else{
            holder.partySet.setVisibility(View.GONE);
            holder.manualSet.setVisibility(View.VISIBLE);
            holder.partyName.setText(myList.getParty_name());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserVoteSelect.dialogBullet.dismiss();
                }
            });
        }
    }
    public void show(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(ct);
        //builder.setTitle(myList.getParty_name());
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton(
                "Hide",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        TextView messageView = (TextView)alert.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }

    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView partyName;
        private ConstraintLayout partySet,manualSet,partyCandidates;
        String details;
        ArrayList<String> positionList;
        FirebaseFirestore mStore;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            partyName = (TextView) itemView.findViewById(R.id.partyName);
            partySet = (ConstraintLayout) itemView.findViewById(R.id.partySet);
            manualSet = (ConstraintLayout) itemView.findViewById(R.id.manualSet);
            partyCandidates = (ConstraintLayout) itemView.findViewById(R.id.partyCandidates);
        }
    }
}
