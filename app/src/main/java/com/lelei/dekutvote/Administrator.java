package com.lelei.dekutvote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.lelei.dekutvote.Adapters.AdministratorAdapter;
import com.lelei.dekutvote.model.AdministratorList;

import java.util.ArrayList;
import java.util.List;

public class Administrator extends AppCompatActivity {
    List<AdministratorList> myLists;
    RecyclerView rv;
    AdministratorAdapter adapter;

    String userId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;

    TextView emailText;
    ImageView logoutButton;
    Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);
        rv = (RecyclerView) findViewById(R.id.adminrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this, 2));
        myLists = new ArrayList<>();



        emailText = findViewById(R.id.adminemailText);
        logoutButton = (ImageView) findViewById(R.id.adminlogoutButton);

        login = new Login();


        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        getdata();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getEmail();



        emailText.setSelected(true);

        userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = mStore.collection("Administrator").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                emailText.setText("Email: " + value.getString("email"));

            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent myIntent = new Intent(Administrator.this, Login.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Administrator.this.startActivity(myIntent);

            }
        });


    }

    private void getdata() {
        myLists.add(new AdministratorList(R.drawable.politicians, "Create Candidates"));
        myLists.add(new AdministratorList(R.drawable.poll, "Create a Poll"));
        myLists.add(new AdministratorList(R.drawable.voters, "Add Voter"));
        myLists.add(new AdministratorList(R.drawable.counts, "Live updates"));



        adapter = new AdministratorAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
}