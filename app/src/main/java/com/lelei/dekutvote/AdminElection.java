package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lelei.dekutvote.Adapters.AdminElectionAdapter;
import com.lelei.dekutvote.Adapters.CourseAdapter;
import com.lelei.dekutvote.model.AdminElectionList;
import com.lelei.dekutvote.model.CourseList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminElection extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    public static List<AdminElectionList> myLists;
    public static RecyclerView rv;
    public static AdminElectionAdapter adapter;
    EditText startVote,endVote,titleText,allowedText;
    Calendar startCalendar;
    Calendar endCalendar;
    CardView addText;
    public static LayoutInflater inflater;
    public static View myLayout;
    public Context ct;
    TextView selectAllowed;
    static final Context context1 = null;
    Spinner spinnerCourse;
    String str;
    public static List<String> partyList;
    private static final int REQUEST_CODE_EXAMPLE = 0x9988;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_election);
        rv = (RecyclerView) findViewById(R.id.adminelecrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        titleText = findViewById(R.id.titleText);
        titleText.setFilters(new InputFilter[] { filter });
        allowedText = findViewById(R.id.allowedText);
        allowedText.setFilters(new InputFilter[] { filter });
        partyList = new ArrayList<>();
        // Adding the parties to the partyList
        partyList.add("Independent");
        partyList.add("Wiber");
        partyList.add("UDA");
        partyList.add("Democratic");
        partyList.add("ODM");
        partyList.add("Jubilee");
        partyList.add("Republican");
        ct = getApplicationContext();
        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        TextView selectText = (TextView)findViewById(R.id.textSelect);
        selectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // spinnerCourse.performClick();
                selectCourse();
            }
        });
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = allowedText.getText().toString();
                if (text.isEmpty()){
                    allowedText.setText(spinnerCourse.getSelectedItem().toString());
                }
                else if (text.equals("ALL")){
                    allowedText.setText(null);
                    allowedText.setText(spinnerCourse.getSelectedItem().toString());

                }
                else if (spinnerCourse.getSelectedItem().toString().equals("ALL")){
                    allowedText.setText(null);
                    allowedText.setText(spinnerCourse.getSelectedItem().toString());
                }
                else{
                    allowedText.setText(text+","+spinnerCourse.getSelectedItem().toString());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        FloatingActionButton fabSendData = (FloatingActionButton) findViewById(R.id.fabSendData);
        fabSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleText.getText().toString().trim();
                String allowed = allowedText.getText().toString().trim();
                String voteS = startVote.getText().toString().trim();
                String voteE = endVote.getText().toString().trim();
                if (title.isEmpty()) {
                    titleText.setError("Election name cannot be empty");
                    titleText.requestFocus();
                    return;
                }
                if (voteS.isEmpty()) {
                    startVote.setError("Starting date is required");
                    startVote.requestFocus();
                    return;
                }
                if (voteE.isEmpty()) {
                    endVote.setError("End date is required");
                    endVote.requestFocus();

                    return;
                }
                if (myLists.size() == 1){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add some candidates", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    return;
                }
                if (allowed.isEmpty()) {
                    allowedText.setText("ALL");
                }

                List<String> positionList = new ArrayList<>();
                for(int x = 0;x < myLists.size() -1;x++){
                    positionList.add(myLists.get(x).getPosition().toString());
                }
                String str = allowedText.getText().toString().trim();
                List<String> allowedList = Arrays.asList(str.split(","));
                Map<String, Object> election = new HashMap<>();
                election.put("allowed", allowedList);
                election.put("date_created", new Date());
                election.put("party", partyList);
                election.put("position",positionList);
                election.put("end", endCalendar.getTime());
                election.put("start", startCalendar.getTime());
                election.put("title", title);


                if(UserProfile.userType.matches("Faculty")){
                    election.put("approved", false);
                    election.put("creator_name", UserProfile.creatorName);
                    election.put("creator_id", UserProfile.creatorId);
                }else{
                    election.put("approved", true);
                    election.put("creator_name", UserProfile.creatorName);
                    election.put("creator_id", UserProfile.creatorId);
                }
                election.put("updates", 0);
                DocumentReference documentReference = mStore.collection("Election").document();
                documentReference.set(election).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Map<String, Integer> insert = new HashMap<>();
                        for(int x = 0;x < myLists.size() -1;x++){
                            insert.put(myLists.get(x).getPosition(),myLists.get(x).getLimitFinal().get(myLists.get(x).getPosition()));

                        }
                        for(int x = 0;x < myLists.size() -1;x++){
                            mStore.collection("Election").document(documentReference.getId()).collection("party-list").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidatesParty());
                            mStore.collection("Election").document(documentReference.getId()).collection("details").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidates());
                            mStore.collection("Election").document(documentReference.getId()).collection("limit").document("position").set(insert);
                            mStore.collection("Election").document(documentReference.getId()).collection("tally").document(myLists.get(x).getPosition()).set(myLists.get(x).getTally());
                            mStore.collection("Election").document(documentReference.getId()).collection("total").document(myLists.get(x).getPosition()).set(myLists.get(x).getTotal());
                            mStore.collection("Election").document(documentReference.getId()).collection("profile").document(myLists.get(x).getPosition()).set(myLists.get(x).getCandidatesId());
                        }
                        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
                                        logsList.put(new Date().toString(),"You created an election " + title +".");
                                        DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                        documentReference.update("logs",logsList);
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
                        if (UserProfile.userType.equals("Administrator")){
                            Intent myIntent = new Intent(AdminElection.this, UserVote.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminElection.this.startActivity(myIntent);
                        }
                        else if (UserProfile.userType.equals("Faculty")){
                            Intent myIntent = new Intent(AdminElection.this, AdminApproval.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminElection.this.startActivity(myIntent);
                        }
                    }
                });
            }
        });
        inflater = getLayoutInflater();
        myLayout = LayoutInflater.from(ct).inflate(R.layout.candidates_list, null);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startVote = (EditText) findViewById(R.id.startVote);
        DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartVote();
            }

        };
        startVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AdminElection.this, startdate, startCalendar
                        .get(Calendar.YEAR), startCalendar.get(Calendar.MONTH),
                        startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endVote = (EditText) findViewById(R.id.endVote);
        DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateEndVote();
            }
        };
        endVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AdminElection.this, enddate, endCalendar
                        .get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        myLists.clear();
        myLists.add(new AdminElectionList(null, null, null,null,null,null,null,null ));
        adapter = new AdminElectionAdapter(myLists, this);
        rv.setAdapter(adapter);
            }
    static void delete(int pos) {
        myLists.remove(pos);
        adapter = new AdminElectionAdapter(myLists, context1);
        rv.setAdapter(adapter);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateStartVote() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        TextView startError = (TextView) findViewById(R.id.startError);
        startVote.setText(sdf.format(startCalendar.getTime()));
        startVote.setError(null);
        startError.setText("");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateEndVote() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        TextView endError = (TextView) findViewById(R.id.endError);
        if (endCalendar.getTime().after(new Date())) {
            if (endCalendar.getTime().after(startCalendar.getTime())) {
                endVote.setText(sdf.format(endCalendar.getTime()));
                endVote.setError(null);
                endError.setText("");
            }
            else {
                endError.setText("Date must be greater than starting date");
            }
        }
        else {
            endError.setText("Date must be greater than today's date");
        }
    }
    public void selectCourse(){
        String[] arrayCourse = new String[] {
                "ALL",
                "BSc IT",
                "BSc CS",
                "BBIT",
                "BSc Civil",
                "BSc Mechanical",
                "BSc EEE",
                "BSc Mechatronics",
                "BSc TIE",
                "BSc Acturial Science",
                "BSc Maths & Modelling",
                "BSc Industrial Chem",
                "BSc Organic Chem",
                "BSc Textile & Leather",
                "BSc Nursing",
                "BSc Nutrition & Dietetics",
                "BSc Food Science",
                "BSc Chemical Eng",
                "BSc Tourism & Hospitality",
                "BSc GIS",
                "BSc GEGIS",
                "BBA Commerce",
                "BBA Economics",
                "BBA Procurement",
                "BBA Business Mngnt",
                "BBA Business Admin",
                "BSc Building Tech",
                "Bed Electrical",
                "Bed Mechanical",
                "Bed Civil",
                "BBA Accounting"
        };

        String[] department = new String[] {
                "",
                "School of Computer Science & IT",
                "School of Computer Science & IT",
                "School of Computer Science & IT",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Engineering",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Science & Mathematics",
                "School of Nursing",
                "School of IFBT",
                "School of IFBT",
                "School of Engineering",
                "School of ITHOM",
                "School of IGRES",
                "School of IGRES",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Business",
                "School of Technology",
                "School of Education",
                "School of Education",
                "School of Education",
                "School of Business"
        };
        myLayout = LayoutInflater.from(this).inflate(R.layout.courses, null);
        List<CourseList> myLists1;
        RecyclerView rv1;
        CourseAdapter adapter1;
        rv1 = (RecyclerView) myLayout.findViewById(R.id.selectCourse);
        rv1.setHasFixedSize(true);
        rv1.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists1 = new ArrayList<>();
        for (int x = 0; x < arrayCourse.length;x++){
            myLists1.add(new CourseList(arrayCourse[x],department[x],false ));
        }
        adapter1 = new CourseAdapter(myLists1, this);
        rv1.setAdapter(adapter1);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(myLayout).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str="";
                for (int y = 0; y < myLists1.size(); y ++){
                    if (myLists1.get(y).getIsChecked() == true){
                        str+=myLists1.get(y).getCourse()+",";
                    }
                }
                allowedText.setText(str);
                dialog.dismiss();
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void addCandid(String position, Integer limit,HashMap<String, String> candidates,HashMap<String, String> candidatesId,HashMap<String, String> candidatesParty,HashMap<String, Integer> limitFinal,HashMap<String, Integer> tally,HashMap<String, Integer> total){
        int listSize = myLists.size() - 1;
        myLists.remove(listSize);
        myLists.add(new AdminElectionList(position,limit,candidates,candidatesId,candidatesParty,limitFinal,tally,total));
        myLists.add(new AdminElectionList(null,null,null,null,null,null,null,null));
        adapter = new AdminElectionAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXAMPLE) {
            if (resultCode == Activity.RESULT_OK) {
                String position = data.getStringExtra("position");
                Integer limit = data.getIntExtra("limit",0);
                ArrayList<String> partylist = data.getStringArrayListExtra("partylist");
                HashMap<String, String> candidateData = (HashMap<String, String>) data.getSerializableExtra("candidateData");
                HashMap<String, String> candidateProfile = (HashMap<String, String>) data.getSerializableExtra("candidateProfile");
                HashMap<String, String> candidateParty = (HashMap<String, String>) data.getSerializableExtra("candidateParty");
                HashMap<String, Integer> limitFinal = (HashMap<String, Integer>) data.getSerializableExtra("limitFinal");
                HashMap<String, Integer> tally = (HashMap<String, Integer>) data.getSerializableExtra("tally");
                HashMap<String, Integer> total = (HashMap<String, Integer>) data.getSerializableExtra("total");
                addCandid(position,limit,candidateData,candidateProfile,candidateParty,limitFinal,tally,total);
            }
            else {

            }
        }
    }
    public void startAnother(Context context){
        Intent intent = new Intent(context, CreateElection.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EXAMPLE);
    }
}