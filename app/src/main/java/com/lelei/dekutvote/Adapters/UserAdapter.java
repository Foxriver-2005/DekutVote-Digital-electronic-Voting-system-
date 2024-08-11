package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.About;
import com.lelei.dekutvote.AdminApproval;
import com.lelei.dekutvote.AdminVerification;
import com.lelei.dekutvote.Feedback;
import com.lelei.dekutvote.Logs;
import com.lelei.dekutvote.ManageUsers;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserPoll;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.UserVote;
import com.lelei.dekutvote.VotingReceipts;
import com.lelei.dekutvote.model.UserList;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UserList> myListList;
    private Context ct;
    public UserAdapter(List<UserList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_list,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserList myList=myListList.get(position);
        holder.textView.setText(myList.getText());
        holder.imageView.setImageDrawable(ct.getResources().getDrawable(myList.getImage()));
        holder.textView.setSelected(true);
        holder.notifCircle.setVisibility(View.INVISIBLE);
        FirebaseFirestore mStoreElection = FirebaseFirestore.getInstance();
        FirebaseFirestore mStorePoll = FirebaseFirestore.getInstance();

        if (myList.getText().matches("Election")){
            mStoreElection.collection("Election").document().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    mStoreElection.collection("Election").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.get("approved").toString().equals("true")){
                                        count++;
                                    }
                                }
                                if (myList.getText().matches("Election")){
                                    holder.notifCircle.setVisibility(View.VISIBLE);
                                    holder.notifCounts.setText(String.valueOf(count));
                                }else{
                                    holder.notifCircle.setVisibility(View.INVISIBLE);
                                }

                            } else {

                            }
                        }
                    });
                }
            });
        }
        if (myList.getText().matches("Poll")){
            mStorePoll.collection("Poll").document().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    mStorePoll.collection("Poll").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int count = 0;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document.get("approved").toString().equals("true")){
                                        count++;
                                    }
                                }
                                if (myList.getText().matches("Poll")){
                                    holder.notifCircle.setVisibility(View.VISIBLE);
                                    holder.notifCounts.setText(String.valueOf(count));
                                }else{
                                    holder.notifCircle.setVisibility(View.INVISIBLE);
                                }

                            } else {

                            }
                        }
                    });
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(myList.getText()) {
                    case "Election":
                        UserProfile.category = "vote";
                        ct.startActivity(new Intent(ct, UserVote.class));
                        break;
                    case "Poll":
                        UserProfile.category = "poll";
                        ct.startActivity(new Intent(ct, UserPoll.class));
                        break;
                    case "Poll Results":
                        UserProfile.category = "poll";
                        ct.startActivity(new Intent(ct, UserPoll.class));
                        break;
                    case "Live updates":
                        UserProfile.category = "live";
                        ct.startActivity(new Intent(ct, UserVote.class));
                        break;
                    case "Administer an election":
                        UserProfile.category = "create_election";
                        ct.startActivity(new Intent(ct, UserVote.class));
                        break;
                    case "Conduct a Poll":
                        UserProfile.category = "create_poll";
                        ct.startActivity(new Intent(ct, UserPoll.class));
                        break;
                    case "Manage users":
                        ct.startActivity(new Intent(ct, ManageUsers.class));
                        break;
                    case "Verify Accounts":
                        ct.startActivity(new Intent(ct, AdminVerification.class));
                        break;
                    case "Approve request":
                        UserProfile.category = "approve_request";
                        ct.startActivity(new Intent(ct, AdminApproval.class));
                        break;
                    case "Pending request":
                        UserProfile.category = "pending_request";
                        ct.startActivity(new Intent(ct, AdminApproval.class));
                        break;
                    case "Voting Receipts":
                        ct.startActivity(new Intent(ct, VotingReceipts.class));
                        break;
                    case "Work logs":
                        ct.startActivity(new Intent(ct, Logs.class));
                        break;
                    case "Feedback":
                        ct.startActivity(new Intent(ct, Feedback.class));
                        break;
                    case "About":
                        ct.startActivity(new Intent(ct, About.class));
                        break;
                    default:
                        // setContentView(R.layout.default);
                        break;
                }
            } });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView textView,notifCounts;
        private ConstraintLayout notifCircle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.myimage);
            textView=(TextView)itemView.findViewById(R.id.mytext);
            notifCircle = (ConstraintLayout) itemView.findViewById(R.id.notifCircle);
            notifCounts = (TextView) itemView.findViewById(R.id.notifCounts);
        }
    }
}
