package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lelei.dekutvote.AdminApproval;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.model.AdminApprovalList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminApprovalAdapter extends RecyclerView.Adapter<AdminApprovalAdapter.ViewHolder> {
    private List<AdminApprovalList> myListList;
    private Context ct;
    public AdminApprovalAdapter(List<AdminApprovalList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminApprovalList myList=myListList.get(position);
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        holder.pendingTitle.setText(myList.getTitle());
        holder.pendingType.setText("Type: "+ myList.getType());
        holder.pendingCreator.setText(myList.getCreator());
        holder.pendingDate.setText(DateFormat.format("MMM dd yyyy hh:mm aa",  myList.getTime()));
        if (UserProfile.userType.equals("Administrator")){
            holder.pendingApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (myList.getType().equals("Poll")) {
                        DocumentReference ref = mStore.collection("Poll").document(myList.getId());
                        ref.update("approved", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                AdminApproval.myLists.remove(position);
                                AdminApproval.adapter = new AdminApprovalAdapter(AdminApproval.myLists,ct);
                                AdminApproval.rv.setAdapter(AdminApproval.adapter);
                                Snackbar.make(AdminApproval.parentLayout, "Approved.", Snackbar.LENGTH_LONG)
                                        .show();
                                mStore.collection("Users").document(UserProfile.creatorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                logsList.put(new Date().toString(),"Approved a poll ("+ myList.getTitle()+")" );
                                                DocumentReference documentReference = mStore.collection("Users").document(UserProfile.creatorId);
                                                documentReference.update("logs",logsList);
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        });

                    }else if(myList.getType().equals("Election")) {
                        DocumentReference ref = mStore.collection("Election").document(myList.getId());
                        ref.update("approved", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                AdminApproval.myLists.remove(position);
                                AdminApproval.adapter = new AdminApprovalAdapter(AdminApproval.myLists,ct);
                                AdminApproval.rv.setAdapter(AdminApproval.adapter);
                                Snackbar.make(AdminApproval.parentLayout, "Approved.", Snackbar.LENGTH_LONG)
                                        .show();
                                mStore.collection("Users").document(UserProfile.creatorId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                                logsList.put(new Date().toString(),"Election approved ("+ myList.getTitle()+")" );
                                                DocumentReference documentReference = mStore.collection("Users").document(UserProfile.creatorId);
                                                documentReference.update("logs",logsList);
                                            } else {
                                                Log.d("TAG", "No such document");
                                            }
                                        } else {
                                            Log.d("TAG", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ct, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }else if (UserProfile.userType.equals("Faculty")){
            holder.pendingCreatorText.setVisibility(View.GONE);
            holder.pendingCreator.setVisibility(View.GONE);
            holder.pendingApprove.setText("Pending");
        }
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pendingTitle;
        private Button pendingApprove;
        private TextView pendingType;
        private TextView pendingCreator,pendingCreatorText;
        private TextView pendingDate,pendingDateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pendingTitle=(TextView)itemView.findViewById(R.id.pendingTitle);
            pendingApprove=(Button)itemView.findViewById(R.id.pendingApprove);
            pendingType=(TextView)itemView.findViewById(R.id.pendingType);
            pendingCreator=(TextView)itemView.findViewById(R.id.pendingCreator);
            pendingDate=(TextView)itemView.findViewById(R.id.pendingDate);
            pendingCreatorText=(TextView)itemView.findViewById(R.id.pendingCreatorText);
            pendingDateText=(TextView)itemView.findViewById(R.id.pendingDateText);
        }
    }
}
