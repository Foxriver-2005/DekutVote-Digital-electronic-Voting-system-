package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.lelei.dekutvote.CreatePoll;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.model.CreatePollList;

import java.util.List;

public class CreatePollAdapter extends RecyclerView.Adapter<CreatePollAdapter.ViewHolder> {
    private List<CreatePollList> myListList;
    private Context ct;
    public CreatePollAdapter(List<CreatePollList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.poll_choices,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreatePollList myList=myListList.get(position);
        if(myList.getChoice() == null){

        }else{
            holder.choicePoll.setText(myList.getChoice());
        }
        if (myList.getChoice() == null){

        }else{
            holder.layoutChoice.setVisibility(View.VISIBLE);
            holder.layoutAddChoice.setVisibility(View.GONE);
        }
        holder.layoutAddChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.createPoll.addChoice(ct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Toast.makeText(ct, myList.getChoice().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.deleteChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatePoll.myLists.remove(position);
                CreatePoll.adapter = new CreatePollAdapter(CreatePoll.myLists,ct);
                CreatePoll.rv.setAdapter(CreatePoll.adapter);
            }
        });
        holder.choicePoll.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                //   myList.setText(mEdit.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
                myList.setText(s.toString());
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView deleteChoice;
        private EditText choicePoll;
        private ConstraintLayout layoutAddChoice,layoutChoice;
        CreatePoll createPoll;
        private String blockCharacterSet = "~^|$*.,";
        private InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null && blockCharacterSet.contains(("" + source))) {
                    return "";
                }
                return null;
            }
        };
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            createPoll = new CreatePoll();
            choicePoll=(EditText)itemView.findViewById(R.id.choicePoll);
            choicePoll.setFilters(new InputFilter[] { filter });
            deleteChoice=(ImageView)itemView.findViewById(R.id.deleteChoice);
            layoutChoice=(ConstraintLayout)itemView.findViewById(R.id.layoutChoice);
            layoutAddChoice=(ConstraintLayout)itemView.findViewById(R.id.layoutAddChoice);
            //timeView=(TextView)itemView.findViewById(R.id.pollAllowed);
        }
    }
}