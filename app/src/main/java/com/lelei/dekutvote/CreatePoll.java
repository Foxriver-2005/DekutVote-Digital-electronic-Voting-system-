package com.lelei.dekutvote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lelei.dekutvote.Adapters.CreatePollAdapter;
import com.lelei.dekutvote.model.CreatePollList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreatePoll extends AppCompatActivity {
    public static List<CreatePollList> myLists;
    public static RecyclerView rv;
    public static CreatePollAdapter adapter;
    FloatingActionButton fabPollDone;
    Context context;
    EditText questionPoll,limitNum;
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
    public static final String EXTRA_DATA = "EXTRA_DATA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        rv = (RecyclerView) findViewById(R.id.pollcreaterec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();
        context = getApplicationContext();
        fabPollDone = findViewById(R.id.fabPollDone);
        questionPoll = findViewById(R.id.questionPoll);
        questionPoll.setFilters(new InputFilter[] { filter });
        String[] arraySpinner = new String[] {
                "1", "2", "3", "4", "5", "6", "7","8","9"
        };
        Spinner s = (Spinner) findViewById(R.id.spinnerPoll);
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapterS);
        Integer holderPosition = getIntent().getIntExtra("holderPosition",0);
        myLists.clear();
        myLists.add(new CreatePollList(""));
        myLists.add(new CreatePollList(null));
        adapter = new CreatePollAdapter(myLists, this);
        rv.setAdapter(adapter);
        fabPollDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionPoll.getText().toString().trim();
                if (question.isEmpty()) {
                    questionPoll.setError("Question cannot be empty");
                    questionPoll.requestFocus();
                    return;
                }
                if(myLists.size() == 1){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add some choices", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    return;
                }
                ArrayList<String> insertChoices = new ArrayList();
                HashMap<String, Integer> insertTally = new HashMap<>();
                HashMap<String, Integer> insertlimitFinal = new HashMap<>();
                HashMap<String, Integer> total = new HashMap<>();
                for(int x = 0;x < myLists.size() -1;x++){
                    if (!myLists.get(x).getChoice().isEmpty()){
                        insertChoices.add(myLists.get(x).getChoice());
                        insertTally.put(myLists.get(x).getChoice(),0);
                    }
                }
                insertlimitFinal.put(questionPoll.getText().toString(),Integer.parseInt(s.getSelectedItem().toString()));
                total.put("votes", 0);
                final Intent data = new Intent();
                data.putExtra("question", questionPoll.getText().toString());
                data.putExtra("choice",  insertChoices);
                data.putExtra("tally", (Serializable) insertTally);
                data.putExtra("total", (Serializable) total);
                data.putExtra("limitFinal", (Serializable) insertlimitFinal);
                data.putExtra("limit", Integer.parseInt(s.getSelectedItem().toString()));
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
    }
    public void addChoice(Context context){
        int listSize = myLists.size() - 1;
        myLists.remove(listSize);
        myLists.add(new CreatePollList(""));
        myLists.add(new CreatePollList(null));
        adapter = new CreatePollAdapter(myLists, context);
        rv.setAdapter(adapter);
    }
}