package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.lelei.dekutvote.AdminElection;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.model.AdminElectionList;

import java.util.ArrayList;
import java.util.List;

public class AdminElectionAdapter extends RecyclerView.Adapter<AdminElectionAdapter.ViewHolder> {
    private List<AdminElectionList> myListList;
    private Context ct;
    public AdminElectionAdapter(List<AdminElectionList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.create_candidate,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.layoutCandidate = (ConstraintLayout) holder.itemView.findViewById(R.id.layoutCandidate);
        holder.layoutAdd = (CardView) holder.itemView.findViewById(R.id.layoutAdd);
        holder.layoutAddButton = (TextView) holder.itemView.findViewById(R.id.layoutAddText);
        AdminElectionList myList=myListList.get(position);
        if (myList.getPosition() == null){

        }else{
            String candidString = String.valueOf(myList.getCandidates().toString()).replace("{", "[").replace("}", "]").replace("=","-");
            holder.layoutCandidate.setVisibility(View.VISIBLE);
            holder.layoutAdd.setVisibility(View.GONE);
            holder.position.setText(myList.getPosition());
            holder.candidatesArray.setText(candidString);
            holder.limit.setText("Limit: "+myList.getLimit().toString());
        }
        holder.removeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminElection.myLists.remove(position);
                AdminElection.adapter = new AdminElectionAdapter(AdminElection.myLists,ct);
                AdminElection.rv.setAdapter(AdminElection.adapter);
            }
        });
        holder.layoutAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.adminElection.startAnother(ct);
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView layoutAdd,addTest;
        TextView layoutAddButton;
        AdminElection adminElection;
        ConstraintLayout layoutCandidate;
        TextView position,candidatesArray,limit;
        ImageButton removeCard;
        ArrayList<String> candidateArray;
        ArrayAdapter<String> adapter;
        View myLayout;
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adminElection = new AdminElection();
            removeCard = (ImageButton) itemView.findViewById(R.id.removeCard);
            candidatesArray=(TextView)itemView.findViewById(R.id.candidateArray);
            position=(TextView)itemView.findViewById(R.id.position);
            limit=(TextView)itemView.findViewById(R.id.limit);
        }
    }
}