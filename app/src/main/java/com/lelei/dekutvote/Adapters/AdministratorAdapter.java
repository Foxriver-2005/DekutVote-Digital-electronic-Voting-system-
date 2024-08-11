package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lelei.dekutvote.AdminElection;
import com.lelei.dekutvote.AdminPoll;
import com.lelei.dekutvote.AdminVoters;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.model.AdministratorList;

import java.util.List;

public class AdministratorAdapter extends RecyclerView.Adapter<AdministratorAdapter.ViewHolder> {
    private List<AdministratorList> myListList;
    private Context ct;
    public AdministratorAdapter(List<AdministratorList> myListList, Context ct) {
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
        AdministratorList myList=myListList.get(position);
        holder.textView.setText(myList.getText());
        holder.imageView.setImageDrawable(ct.getResources().getDrawable(myList.getImage()));
        holder.textView.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(myList.getText()) {
                    case "Create Election":
                        ct.startActivity(new Intent(ct, AdminElection.class));
                        break;
                    case "Create a Poll":
                        ct.startActivity(new Intent(ct, AdminPoll.class));
                        break;
                    case "Add Voter":
                        ct.startActivity(new Intent(ct, AdminVoters.class));
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
        private TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.myimage);
            textView=(TextView)itemView.findViewById(R.id.mytext);
        }
    }
}
