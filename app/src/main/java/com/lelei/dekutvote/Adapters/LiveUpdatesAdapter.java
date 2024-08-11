package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.lelei.dekutvote.LiveUpdatesSelected;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.model.LiveUpdatesList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LiveUpdatesAdapter extends RecyclerView.Adapter<LiveUpdatesAdapter.ViewHolder> {
    private List<LiveUpdatesList> myListList;
    private Context ct;
    private Integer counts = 0;
    public LiveUpdatesAdapter(List<LiveUpdatesList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tally_list, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        LiveUpdatesList myList = myListList.get(position);
        holder.tally_position.setText(myList.getPosition());
        holder.tally_candidate.setText(myList.getName());
        if(myList.getName() != null){
            holder.tally_position.setVisibility(View.GONE);
        }
        if (myList.getVotes() != null){
            holder.tally_votes.setText("Votes: " +myList.getVotes());
            holder.itemView.setElevation(0);
        }else
        {
            holder.itemView.setElevation(0);
            holder.tally_votes.setVisibility(View.GONE);
        }
        holder.tally_position.setSelected(true);
        holder.tally_candidate.setSelected(true);
        holder.tally_votes.setSelected(true);
        DocumentReference reference = mStore.collection("Election")
                .document(myList.getDocu()).collection("tally").document(myList.getPosition());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    holder.tally_position.setText(myList.getPosition());
                    holder.tally_candidate.setText(myList.getName());
                    if (myList.getVotes() != null){
                        Calendar calendar;
                        SimpleDateFormat simpledateformat;
                        String Date;
                        calendar = Calendar.getInstance();
                        simpledateformat = new SimpleDateFormat("MM/dd/yyy hh:mm:ss");
                        Date = simpledateformat.format(calendar.getTime());
                        LiveUpdatesSelected.timeText.setText("Synced on: "+Date);

                        new CountDownTimer(3000,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                LiveUpdatesSelected.updatedPop.setVisibility(View.VISIBLE);
                                LiveUpdatesSelected.timeText.setTextColor(ContextCompat.getColor(ct,
                                        R.color.mainColor1));
                            }
                            @Override
                            public void onFinish() {
                                LiveUpdatesSelected.updatedPop.setVisibility(View.GONE);
                                LiveUpdatesSelected.timeText.setTextColor(ContextCompat.getColor(ct,
                                        R.color.black));
                            }
                        }.start();

                        Double votes = snapshot.getDouble(myList.getName());
                        //      Toast.makeText(ct, "tt"+myList.getName() + "tt" +String.valueOf(votes), Toast.LENGTH_SHORT).show();
                        int i = Integer.valueOf(votes.intValue());
                        holder.tally_votes.setText(String.valueOf(i)+ " votes");
                        holder.itemView.setElevation(0);
                        mStore.collection("Election")
                                .document(myList.getDocu()).collection("total").document(myList.getPosition()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if(documentSnapshot.exists()){
                                            if (e != null) {
                                                return;
                                            }
                                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                                Double result = documentSnapshot.getDouble("total_votes");
                                                int t = Integer.valueOf(result.intValue());
                                                int percent = (int) (((double) i / (double) t) * 100);
                                                //    Toast.makeText(ct, String.valueOf(t)+ " pos "+ myList.getPosition(), Toast.LENGTH_SHORT).show();
                                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        percent
                                                );
                                                holder.tally_bar.setLayoutParams(param);

                                            } else {

                                            }
                                        }


                                    }
                                });
                    }else
                    {
                        holder.itemView.setElevation(0);
                        holder.tally_votes.setVisibility(View.GONE);
                    }
                } else {

                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tally_position;
        private TextView tally_candidate;
        private TextView tally_votes;
        private TextView timeText;
        private CardView tally_bar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tally_bar = (CardView) itemView.findViewById(R.id.tally_bar);
            tally_position = (TextView) itemView.findViewById(R.id.tally_position);
            tally_candidate = (TextView) itemView.findViewById(R.id.tally_candidate);
            tally_votes = (TextView) itemView.findViewById(R.id.tally_votes);
        }
    }
}
