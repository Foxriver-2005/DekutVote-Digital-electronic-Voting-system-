package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lelei.dekutvote.ManageUsers;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.UserProfile;
import com.lelei.dekutvote.model.ManageUsersList;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUsersAdapter extends RecyclerView.Adapter<ManageUsersAdapter.ViewHolder> {
    private List<ManageUsersList> myListList;
    private Context ct;
    public ManageUsersAdapter(List<ManageUsersList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    public void updateList(List<ManageUsersList> list){
        myListList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ManageUsersList myList = myListList.get(position);
        String[] arraySpinner = new String[] {
                "Student", "Faculty", "Administrator"
        };
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ct,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerType.setAdapter(adapter);
        holder.userdisplayName.setText(myList.getName());
        holder.userId.setText(myList.getID());
        holder.userType.setText(myList.getType());
        holder.userDisplayEmail.setText(myList.getEmail());
        //holder.spinnerType.setSelection(adapter.getPosition(myList.getType()));
        holder.changeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.spinnerType.performClick();
                ManageUsers.spinnerTypeActivated = 1;
                holder.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //  Toast.makeText(ct, holder.spinnerType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        //  Toast.makeText(ct, myList.getID(), Toast.LENGTH_SHORT).show();
                        if (ManageUsers.spinnerTypeActivated == 1) {
                            DocumentReference ref = mStore.collection("Users").document(myList.getCloudID());
                            ref.update("type", holder.spinnerType.getSelectedItem().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    myList.setType(holder.spinnerType.getSelectedItem().toString());
                                    holder.userType.setText(holder.spinnerType.getSelectedItem().toString());
                                    holder.spinnerType.setSelection(adapter.getPosition(myList.getType()));

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
                                                    logsList.put(new Date().toString(),myList.getName() + " changed type to " +holder.spinnerType.getSelectedItem().toString());
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
                                    Snackbar.make(view, "Changed type successfully", Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView changeType;
        private TextView userdisplayName;
        private TextView userId;
        private TextView userType;
        private TextView userDisplayEmail;
        private Spinner spinnerType;
        private int spinnerTypeActivated = 0;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userDisplayEmail = (TextView)itemView.findViewById(R.id.userDisplayEmail);
            userdisplayName = (TextView)itemView.findViewById(R.id.userdisplayName);
            userId = (TextView)itemView.findViewById(R.id.userId);
            userType = (TextView)itemView.findViewById(R.id.userType);
            changeType = (ImageView)itemView.findViewById(R.id.changeType);
            spinnerType = (Spinner)itemView.findViewById(R.id.spinnerType);
        }
    }
}
