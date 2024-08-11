package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.Adapters.VotingReceiptsAdapter;
import com.lelei.dekutvote.model.VotingReceiptsList;

import java.util.ArrayList;
import java.util.List;

public class VotingReceipts extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    static List<VotingReceiptsList> myLists;
    static RecyclerView rv;
    static VotingReceiptsAdapter adapter;
    static View parentLayout;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_receipts);
        rv = (RecyclerView) findViewById(R.id.userreceiptrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        parentLayout = findViewById(android.R.id.content);
        mStore.collection("Users").document(UserProfile.creatorId).collection("receipts_details").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        myLists.add(new VotingReceiptsList(document.getId(),document.get("election_name").toString(),document.getDate("date_voted")));
                    }
                    displayView();
                }
            }
        });
    }
    public void displayView(){
        adapter = new VotingReceiptsAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
}