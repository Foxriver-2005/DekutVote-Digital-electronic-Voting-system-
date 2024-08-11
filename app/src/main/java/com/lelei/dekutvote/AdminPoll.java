package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.lelei.dekutvote.Adapters.AdminPollAdapter;
import com.lelei.dekutvote.Adapters.CourseAdapter;
import com.lelei.dekutvote.model.AdminPollList;
import com.lelei.dekutvote.model.CourseList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPoll extends AppCompatActivity {
    public static List<AdminPollList> myLists;
    public static RecyclerView rv;
    public static AdminPollAdapter adapter;
    FloatingActionButton pollSendData;
    Calendar startCalendar;
    Calendar endCalendar;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    EditText pollTitleText,pollAllowedText,pollDescriptionText,startPoll,endPoll;
    Spinner spinnerCourse1;
    public static View myLayout;
    String str;
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
    private static final int REQUEST_CODE_EXAMPLE = 0x9988;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_poll);
        rv = (RecyclerView) findViewById(R.id.adminpollrec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1 ));
        myLists = new ArrayList<>();
        pollSendData = findViewById(R.id.pollSendData);
        pollTitleText = findViewById(R.id.pollTitleText);
        pollTitleText.setFilters(new InputFilter[] { filter });
        pollAllowedText = findViewById(R.id.pollAllowedText);
        pollAllowedText.setFilters(new InputFilter[] { filter });
        pollDescriptionText = findViewById(R.id.pollDescriptionText);
        pollDescriptionText.setFilters(new InputFilter[] { filter });
        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        TextView selectText = (TextView)findViewById(R.id.textSelect1);
        selectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCourse();
            }
        });
        myLists.clear();
        myLists.add(new AdminPollList(null, null,null,null,null,null ));
        adapter = new AdminPollAdapter(myLists, this);
        rv.setAdapter(adapter);
        pollSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = pollTitleText.getText().toString().trim();
                String allowed = pollAllowedText.getText().toString().trim();
                String description = pollDescriptionText.getText().toString().trim();
                if (title.isEmpty()) {
                    pollTitleText.setError("Poll name cannot be empty");
                    pollTitleText.requestFocus();
                    return;
                }
                if (allowed.isEmpty()) {
                    pollAllowedText.setError("Allowed voters is required");
                    pollAllowedText.requestFocus();
                    return;
                }
                if (description.isEmpty()) {
                    pollDescriptionText.setError("Description is required");
                    pollDescriptionText.requestFocus();
                    return;
                }
                if (myLists.size() == 1){
                    View parentLayout = findViewById(android.R.id.content);
                    Snackbar.make(parentLayout, "Add some questions", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                    return;
                }
                if (allowed.isEmpty()) {
                    pollAllowedText.setText("ALL");

                }
                List<String> allowedList = new ArrayList<>();
                allowedList.add(allowed);
                Map<String, Object> poll = new HashMap<>();
                poll.put("allowed", allowedList);
                poll.put("description", description);
                poll.put("date_created", new Date());
                //election.put("description",firstname);
                poll.put("end", endCalendar.getTime());
                poll.put("start", new Date().getTime());
                poll.put("title", title);
                if(UserProfile.userType.matches("Faculty")){
                    poll.put("approved", false);
                    poll.put("creator_name", UserProfile.creatorName);
                    poll.put("creator_id", UserProfile.creatorId);
                }
                else{
                    poll.put("approved", true);
                    poll.put("creator_name", UserProfile.creatorName);
                    poll.put("creator_id", UserProfile.creatorId);
                }
                DocumentReference documentReference = mStore.collection("Poll").document();
                documentReference.set(poll).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        HashMap<String, Integer> insert = new HashMap<>();
                        for(int x = 0;x < myLists.size() -1;x++){
                            insert.put(myLists.get(x).getQuestion(),myLists.get(x).getLimitFinal().get(myLists.get(x).getQuestion()));
                        }
                        for(int x = 0;x < myLists.size() -1;x++){
                            mStore.collection("Poll").document(documentReference.getId()).collection("limit").document("questions").set(insert);
                            mStore.collection("Poll").document(documentReference.getId()).collection("questions").document(myLists.get(x).getQuestion()).set(myLists.get(x).getTally());
                            mStore.collection("Poll").document(documentReference.getId()).collection("total").document(myLists.get(x).getQuestion()).set(myLists.get(x).getTotal());
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
                                        logsList.put(new Date().toString(),"You created a poll " + title +".");
                                        DocumentReference documentReference = mStore.collection("Users").document(mAuth.getCurrentUser().getUid());
                                        documentReference.update("logs",logsList);
                                    }
                                    else {
                                        Log.d("TAG", "No such document");
                                    }
                                }
                                else {
                                    Log.d("TAG", "get failed with ", task.getException());
                                }
                            }
                        });
                        if (UserProfile.userType.equals("Administrator")){
                            Intent myIntent = new Intent(AdminPoll.this, UserPoll.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminPoll.this.startActivity(myIntent);
                        }
                        else if (UserProfile.userType.equals("Faculty")){
                            Intent myIntent = new Intent(AdminPoll.this, AdminApproval.class);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            AdminPoll.this.startActivity(myIntent);
                        }
                    }
                });
            }
        });
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        endPoll = (EditText) findViewById(R.id.endPoll);
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
        endPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AdminPoll.this, enddate, endCalendar
                        .get(Calendar.YEAR), endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateEndVote() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        TextView endError = (TextView) findViewById(R.id.endPollError);
        if (endCalendar.getTime().after(new Date())) {
            if (endCalendar.getTime().after(startCalendar.getTime())) {
                endPoll.setText(sdf.format(endCalendar.getTime()));
                endPoll.setError(null);
                endError.setText("");
            }
            else {
                endPoll.setText("");
                endError.setText("Date must be greater than starting date");
            }
        }
        else {
            endError.setText("Date must be greater than today's date");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXAMPLE) {
            if (resultCode == Activity.RESULT_OK) {
                String question = data.getStringExtra("question");
                Integer limit = data.getIntExtra("limit",0);
                ArrayList<String> choices = data.getStringArrayListExtra("choice");
                HashMap<String, Integer> tally = (HashMap<String, Integer>) data.getSerializableExtra("tally");
                HashMap<String, Integer> total = (HashMap<String, Integer>) data.getSerializableExtra("total");
                HashMap<String, Integer> limitFinal = (HashMap<String, Integer>) data.getSerializableExtra("limitFinal");
                addPoll(question,choices,limit,tally,limitFinal,total);
            }
            else {

            }
        }
    }
    public void startAnother(Context context){
        Intent intent = new Intent(context, CreatePoll.class);
        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_EXAMPLE);
    }
    public void addPoll(String question, ArrayList<String> choices,Integer limit,HashMap<String, Integer> tally,HashMap<String, Integer> limitFinal,HashMap<String, Integer> total){
        int listSize = myLists.size() - 1;
        myLists.remove(listSize);
        myLists.add(new AdminPollList(question,limit,choices,limitFinal,tally,total));
        myLists.add(new AdminPollList(null,null,null,null,null,null));
        adapter = new AdminPollAdapter(myLists, this);
        rv.setAdapter(adapter);
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
                        // Toast.makeText(ct,  myLists1.get(y).getCourse(), Toast.LENGTH_SHORT).show();
                    }
                }
                pollAllowedText.setText(str);
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
    private void getdata() {
        adapter = new AdminPollAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
}