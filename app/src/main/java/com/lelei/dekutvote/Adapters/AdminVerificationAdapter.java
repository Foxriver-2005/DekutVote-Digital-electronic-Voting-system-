package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lelei.dekutvote.AdminVerification;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.model.AdminVerificationList;

import java.util.List;

public class AdminVerificationAdapter extends RecyclerView.Adapter<AdminVerificationAdapter.ViewHolder> {
    private List<AdminVerificationList> myListList;
    private Context ct;
    public AdminVerificationAdapter(List<AdminVerificationList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    public void updateList(List<AdminVerificationList> list){
        myListList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.verify_list, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminVerificationList myList = myListList.get(position);
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        holder.userdisplayName.setText(myList.getName());
        holder.userId.setText(myList.getID());
        holder.userType.setText(myList.getType());
        holder.userDisplayEmail.setText(myList.getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.adminVerification.verificationPop(ct,myList.getCloudID(),position);
            }
        });
        holder.userVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminVerification.myLists.remove(position);
                AdminVerification.adapter = new AdminVerificationAdapter(AdminVerification.myLists,ct);
                AdminVerification.rv.setAdapter(AdminVerification.adapter);
                AdminVerification.numberUsers.setText(String.valueOf(AdminVerification.myLists.size()+ " unverified account(s)"));
                holder.adminVerification.createAcc(myList.getCloudID(),ct);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRefDocu = storage.getReference();
                StorageReference documentRef = storageRefDocu.child("Verification/"+myList.getCloudID()+"/document.jpg");
                documentRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ct, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                StorageReference storageRefSelfie = storage.getReference();
                StorageReference selfieRef = storageRefSelfie.child("Verification/"+myList.getCloudID()+"/selfie.jpg");
                selfieRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Toast.makeText(ct, exception.getMessage(), Toast.LENGTH_SHORT).show();
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
        private Button userVerify;
        AdminVerification adminVerification;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userDisplayEmail = (TextView)itemView.findViewById(R.id.verifyDisplayEmail);
            userdisplayName = (TextView)itemView.findViewById(R.id.verifydisplayName);
            userId = (TextView)itemView.findViewById(R.id.verifyId);
            userType = (TextView)itemView.findViewById(R.id.verifyType);
            userVerify = (Button)itemView.findViewById(R.id.verifyUser);
            adminVerification = new AdminVerification();
        }
    }
}
