package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.model.UserPollSelectList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPollSelectAdapter extends RecyclerView.Adapter<UserPollSelectAdapter.ViewHolder> {
    private List<UserPollSelectList> myListList;
    private Context ct;
    private Integer counts = 0;
    public UserPollSelectAdapter(List<UserPollSelectList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_bar, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        UserPollSelectList myList = myListList.get(position);
        holder.choiceText.setText(myList.getName());
        holder.votesText.setText(myList.getCounts().toString() + " votes");
        holder.choiceText.setSelected(true);
        holder.votesText.setSelected(true);
        DocumentReference reference = mStore.collection("Poll")
                .document(myList.getDocu1()).collection("total").document(myList.getDocu2());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    Double result = snapshot.getDouble("votes");
                    final int totalVotes = Integer.valueOf(result.intValue());
                    mStore.collection("Poll")
                            .document(myList.getDocu1()).collection("questions").document(myList.getDocu2()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if(documentSnapshot.exists()){
                                        if (e != null) {
                                            //Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }
                                        if (documentSnapshot != null && documentSnapshot.exists()) {
                                            //  Log.d(TAG, "Current data: " + snapshot.getData());
                                            Double result = documentSnapshot.getDouble(myList.getName());
                                            int i = Integer.valueOf(result.intValue());
                                            holder.votesText.setText(String.valueOf(i)+" votes");
                                            int percent = (int) (((double) i / (double) totalVotes) * 100);
                                            holder.percentText.setText(String.valueOf(percent+"%"));
                                            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    percent
                                            );
                                            holder.pollCounts.setLayoutParams(param);

                                        } else {

                                        }
                                    }
                                }
                            });
                } else {
                    //  Log.d(TAG, "Current data: null");
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserProfile.userType.equals("Faculty") || UserProfile.userType.equals("Administrator")) {
                    Snackbar.make(view, "[Read only] " + UserProfile.userType + " users cannot participate", Snackbar.LENGTH_LONG).show();
                }else {
                    if (holder.pollCounts.getCardBackgroundColor().getDefaultColor() == ContextCompat.getColor(ct,
                            R.color.mainColorLight)) {
                        holder.pollCounts.setCardBackgroundColor(ContextCompat.getColor(ct,
                                R.color.silverLight));
                        DocumentReference per = mStore.collection("Poll").document(myList.getDocu1()).collection("total").document(myList.getDocu2());
                        per.update("votes", FieldValue.increment(-1));
                        DocumentReference ref = mStore.collection("Poll").document(myList.getDocu1()).collection("questions").document(myList.getDocu2());
                        ref.update(myList.getName(), FieldValue.increment(-1));
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
                                        logsList.put(new Date().toString(), "Removed choice '" + myList.getName() + "' in poll question '" + myList.getDocu2() + "'.");
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

                    } else {
                        if (counts < myList.getLimit()) {
                            holder.pollCounts.setCardBackgroundColor(ContextCompat.getColor(ct,
                                    R.color.mainColorLight));
                            DocumentReference per = mStore.collection("Poll").document(myList.getDocu1()).collection("total").document(myList.getDocu2());
                            per.update("votes", FieldValue.increment(1));
                            DocumentReference ref = mStore.collection("Poll").document(myList.getDocu1()).collection("questions").document(myList.getDocu2());
                            ref.update(myList.getName(), FieldValue.increment(1));
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
                                            logsList.put(new Date().toString(), "You chose '" + myList.getName() + "' in poll question '" + myList.getDocu2() + "'.");
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
                        } else {
                            Snackbar.make(view, "limit : " + myList.getLimit().toString() + " vote(s)", Snackbar.LENGTH_LONG).show();
                            //   Toast.makeText(ct,"limit : "+myList.getLimit().toString()+" vote(s)", Toast.LENGTH_SHORT).show();
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
    public  void listener(){

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView choiceText;
        private TextView votesText;
        private TextView percentText;
        private CardView pollCounts;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            choiceText = (TextView) itemView.findViewById(R.id.choiceText);
            votesText = (TextView) itemView.findViewById(R.id.votesText);
            percentText = (TextView) itemView.findViewById(R.id.percentText);
            pollCounts = (CardView) itemView.findViewById(R.id.pollCounts);
            FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        }
    }
}
