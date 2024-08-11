package com.lelei.dekutvote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lelei.dekutvote.Adapters.CreateElectionAdapter;
import com.lelei.dekutvote.model.CreateElectionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateElection extends AppCompatActivity {
    public static List<CreateElectionList> myLists;
    public static RecyclerView rv;
    public static CreateElectionAdapter adapter;
    FloatingActionButton fabElectionDone;
    Context context;
    EditText positionElection,candidateName,candidateID,candidateInfo,candidateParty;
    ConstraintLayout candidateAdd;
    ImageView imageDown;
    //ArrayList<String> partyList;
    Spinner spinnerParty, spinnerElection;
    Boolean partyEnable;
    private String blockCharacterSet = "~#^|$%&*!.,";
    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };
    public static final String EXTRA_DATA = "EXTRA_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_election);
        rv = (RecyclerView) findViewById(R.id.electioncreaterec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();
        context = getApplicationContext();
        fabElectionDone = findViewById(R.id.fabElectionDone);
        positionElection = findViewById(R.id.positionElection);
        positionElection.setFilters(new InputFilter[] { filter });
        candidateName = findViewById(R.id.candidateName);
        candidateName.setFilters(new InputFilter[] { filter });
        candidateID = findViewById(R.id.candidateID);
        candidateInfo = findViewById(R.id.candidateInfo);
        candidateInfo.setFilters(new InputFilter[] { filter });
        candidateParty = findViewById(R.id.candidateParty);
        candidateParty.setFilters(new InputFilter[] { filter });
        candidateAdd = findViewById(R.id.candidateAdd);
        imageDown = findViewById(R.id.imageDown);
        candidateID.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        candidateID.setKeyListener(DigitsKeyListener.getInstance("0123456789-"));
        spinnerParty = (Spinner) findViewById(R.id.spinnerParty);
        ArrayAdapter<String> adapterParty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, AdminElection.partyList);
        adapterParty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerParty.setAdapter(adapterParty);
        partyEnable = false;

        candidateAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCandid();
            }
        });
        spinnerParty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (partyEnable == true){
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    candidateParty.setText(selectedItem);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        imageDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyEnable = true;
                spinnerParty.performClick();
                spinnerParty.setAdapter(null);
                spinnerParty.setAdapter(adapterParty);
            }
        });

        String[] arraySpinner = new String[] {
                "1", "2", "3", "4", "5", "6", "7","8","9"
        };
        spinnerElection = (Spinner) findViewById(R.id.spinnerElection);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerElection.setAdapter(adapterS);
        Integer holderPosition = getIntent().getIntExtra("holderPosition",0);
        myLists.clear();
        adapter = new CreateElectionAdapter(myLists, this);
        rv.setAdapter(adapter);
        fabElectionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String position = positionElection.getText().toString().trim();
                if (position.isEmpty()) {
                    positionElection.setError("Position cannot be empty");
                    positionElection.requestFocus();
                    return;
                }
                if(myLists.size() == 0){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add some candidates", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    return;
                }
                Map<String, String> candidateData = new HashMap<>();
                Map<String, String> candidateProfile = new HashMap<>();
                Map<String, String> candidateParty = new HashMap<>();
                Map<String, Integer> limitFinal = new HashMap<>();
                Map<String, Integer> tally = new HashMap<>();
                Map<String, Integer> total = new HashMap<>();
                for(int x = 0;x < myLists.size();x++){
                    candidateProfile.put(myLists.get(x).getCandidName(), myLists.get(x).getCandidID().toString());
                    candidateData.put(myLists.get(x).getCandidName(), myLists.get(x).getCandidParty());
                    candidateParty.put(myLists.get(x).getCandidParty(),myLists.get(x).getCandidName() );
                    tally.put(myLists.get(x).getCandidName(), 0);
                }
                limitFinal.put(positionElection.getText().toString(), Integer.valueOf(spinnerElection.getSelectedItem().toString()));
                total.put("total_votes", 0);
                final Intent data = new Intent();
                data.putExtra("position", positionElection.getText().toString());
                data.putExtra("candidateData", (Serializable) candidateData);
                data.putExtra("candidateProfile", (Serializable) candidateProfile);
                data.putExtra("candidateParty", (Serializable) candidateParty);
                data.putExtra("limitFinal", (Serializable) limitFinal);
                data.putExtra("tally", (Serializable) tally);
                data.putExtra("total", (Serializable) total);
                data.putExtra("limit", Integer.valueOf(spinnerElection.getSelectedItem().toString()));
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
        candidateParty.setText("");
    }
    public void addCandid(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String candidName = candidateName.getText().toString().trim();
        String candidID = candidateID.getText().toString().trim();
        String candidInfo = candidateInfo.getText().toString().trim();
        String candidParty = candidateParty.getText().toString().trim();
        if (candidName.isEmpty()) {
            candidateName.setError("Name cannot be empty");
            candidateName.requestFocus();
            return;
        }

        if (candidID.isEmpty()) {
            candidateID.setError("ID Number cannot be empty");
            candidateID.requestFocus();
            return;
        }

        if (candidInfo.isEmpty()) {
            candidateInfo.setError("Course and Section cannot be empty");
            candidateInfo.requestFocus();
            return;
        }

        if (candidParty.isEmpty()) {
            candidateParty.setError("Party-list cannot be empty");
            candidateParty.requestFocus();
            return;
        }
        myLists.add(new CreateElectionList(candidName,candidID,candidInfo,candidParty));
        adapter = new CreateElectionAdapter(myLists, this);
        rv.setAdapter(adapter);
        if (!AdminElection.partyList.contains(candidParty)){
            AdminElection.partyList.add(candidParty);
        }
        candidateName.setText("");
        candidateID.setText("");
        candidateInfo.setText("");
        candidateParty.setText("");

    }
}