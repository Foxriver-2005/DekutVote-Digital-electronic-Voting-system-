package com.lelei.dekutvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.method.DigitsKeyListener;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lelei.dekutvote.Adapters.CourseAdapter;
import com.lelei.dekutvote.Adapters.ManageUsersAdapter;
import com.lelei.dekutvote.model.CourseList;
import com.lelei.dekutvote.model.ManageUsersList;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageUsers extends AppCompatActivity {
    List<ManageUsersList> myLists;
    RecyclerView rv;
    ManageUsersAdapter adapter;
    FloatingActionButton fabSearch,fabAdd;
    CoordinatorLayout coordinatorLayout;
    CardView userSearch;
    SearchView searchUser;
    List<String> usersList;
    public static int spinnerTypeActivated = 0;
    LayoutInflater inflater;
    public static View myLayout;
    String userId;
    private FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    String[] types = { "Student", "Faculty"};
    Spinner spinnerRegisterTypeUser;
    public static EditText courseRBox;
    public static boolean active = false;
    public static androidx.appcompat.app.AlertDialog dialog;
    androidx.appcompat.app.AlertDialog dialogAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        rv=(RecyclerView)findViewById(R.id.users_rec);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this,1));
        myLists=new ArrayList<>();
        userSearch = findViewById(R.id.userSearch);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        //mUser  = mAuth.getCurrentUser();
        fabSearch = findViewById(R.id.fabSearch);
        fabAdd   = findViewById(R.id.fabAdd);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        searchUser = findViewById(R.id.searchUser);
        inflater = getLayoutInflater();
        getUserList();
        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                coordinatorLayout.setVisibility(View.GONE);
                userSearch.setVisibility(View.VISIBLE);
                searchUser.setFocusable(true);
                searchUser.setIconified(false);
                fabSearch.setVisibility(View.GONE);
            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLayout = inflater.inflate(R.layout.add_user, null);
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ManageUsers.this);
                String blockCharacterSet = "~#^|$%&*!.,1234567890";
                InputFilter filter = new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (source != null && blockCharacterSet.contains(("" + source))) {
                            return "";
                        }
                        return null;
                    }
                };
                Spinner spinnerRegisterTypeUser = (Spinner) myLayout.findViewById(R.id.typeBoxUser);
                EditText firstnameBox = (EditText) myLayout.findViewById(R.id.firstnameBoxUser);
                firstnameBox.setFilters(new InputFilter[] { filter });
                EditText middleinitialBox = (EditText) myLayout.findViewById(R.id.middleinitialBoxUser);
                middleinitialBox.setFilters(new InputFilter[] { filter });
                EditText lastnameBox = (EditText) myLayout.findViewById(R.id.lastnameBoxUser);
                lastnameBox.setFilters(new InputFilter[] { filter });
                courseRBox = (EditText) myLayout.findViewById(R.id.courseRBoxUser);
                courseRBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] arrayCourse = new String[] {
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
                        myLayout = LayoutInflater.from(ManageUsers.this).inflate(R.layout.courses, null);
                        List<CourseList> myLists1;
                        RecyclerView rv1;
                        CourseAdapter adapter1;
                        TextView textCourses;
                        textCourses = (TextView) myLayout.findViewById(R.id.textCourses);
                        textCourses.setVisibility(View.GONE);
                        rv1 = (RecyclerView) myLayout.findViewById(R.id.selectCourse);
                        rv1.setHasFixedSize(true);
                        rv1.setLayoutManager(new GridLayoutManager(ManageUsers.this,1 ));
                        myLists1 = new ArrayList<>();
                        for (int x = 0; x < arrayCourse.length;x++){
                            myLists1.add(new CourseList(arrayCourse[x],department[x],false ));
                        }
                        adapter1 = new CourseAdapter(myLists1, ManageUsers.this);
                        rv1.setAdapter(adapter1);
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ManageUsers.this);
                        builder.setView(myLayout).setPositiveButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton("", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog= builder.create();
                        dialog.setCancelable(true);
                        dialog.show();
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
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
                });
                EditText idnumberBox = (EditText) myLayout.findViewById(R.id.idnumberBoxUser);
                idnumberBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD); // Allow uppercase letters
                idnumberBox.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.CHARACTERS, true) {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                    }


                    protected char[] getAcceptedChars() {
                        return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-/".toCharArray();
                    }
                });
                EditText emailBox = (EditText) myLayout.findViewById(R.id.emailBoxUser);
                EditText passwordBox = (EditText) myLayout.findViewById(R.id.passwordBoxUser);
                EditText passwordconfirmBox = (EditText) myLayout.findViewById(R.id.passwordconfirmBoxUser);
                ArrayAdapter aa = new ArrayAdapter(ManageUsers.this,android.R.layout.simple_list_item_1,types);
                aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spinnerRegisterTypeUser.setAdapter(aa);
                Button registerButtonUser = (Button) myLayout.findViewById(R.id.registerButtonUser);
                registerButtonUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String type,firstname,middlename,lastname,idnumber,course,email,password,confirmpassword;
                        type = spinnerRegisterTypeUser.getSelectedItem().toString();
                        firstname = firstnameBox.getText().toString().trim();
                        middlename = middleinitialBox.getText().toString().trim();
                        lastname = lastnameBox.getText().toString().trim();
                        idnumber = idnumberBox.getText().toString().trim();
                        course = courseRBox.getText().toString().trim();
                        email = emailBox.getText().toString().trim();
                        password = passwordBox.getText().toString();
                        confirmpassword = passwordconfirmBox.getText().toString();

                        if(firstname.isEmpty()){
                            firstnameBox.setError("First name is required");
                            firstnameBox.requestFocus();
                            return;
                        }
                        if(middlename.isEmpty()){
                            middleinitialBox.setError("Middle name is required");
                            middleinitialBox.requestFocus();
                            return;
                        }
                        if(lastname.isEmpty()){
                            lastnameBox.setError("Last name is required");
                            lastnameBox.requestFocus();
                            return;
                        }
                        if(idnumber.isEmpty()){
                            idnumberBox.setError("Reg number is required");
                            idnumberBox.requestFocus();
                            return;
                        }
                        // Check ID number format
                        String idPattern = "[A-Z]\\d{3}-\\d{2}-\\d{4}/\\d{4}";
                        if (!idnumber.matches(idPattern)) {
                            idnumberBox.setError("Invalid Reg number format");
                            idnumberBox.requestFocus();
                            return;
                        }
                        if(course.isEmpty()){
                            courseRBox.setError("Course is required");
                            courseRBox.requestFocus();
                            return;
                        }
                        if(email.isEmpty()){
                            emailBox.setError("Email is required");
                            emailBox.requestFocus();
                            return;
                        }
                        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\..+[a-z]+";
                        if(!email.matches(emailPattern)){
                            emailBox.setError("Invalid email");
                            emailBox.requestFocus();
                            return;
                        }
                        if(password.isEmpty()){
                            passwordBox.setError("Password is required");
                            passwordBox.requestFocus();
                            return;
                        }
                        if(password.length() < 6){
                            passwordBox.setError("Password should be atleast 6 characters");
                            passwordBox.requestFocus();
                            return;
                        }
                        if(confirmpassword.isEmpty()){
                            passwordconfirmBox.setError("Confirm your password");
                            passwordconfirmBox.requestFocus();
                            return;
                        }
                        if (!confirmpassword.equals(password)){
                            passwordconfirmBox.setError("Password does not match");
                            passwordconfirmBox.requestFocus();
                            return;
                        }
                        mStore.collection("Users")
                                .whereEqualTo("id", idnumber)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (document.exists()){
                                                    idnumberBox.setError("ID number already exist");
                                                    idnumberBox.requestFocus();
                                                    return;
                                                }else{

                                                }
                                            }
                                            mAuth.createUserWithEmailAndPassword(email ,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()){
                                                        FirebaseUser mUser = mAuth.getCurrentUser();
                                                        mUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(ManageUsers.this, "Email verification has been sent to the user", Toast.LENGTH_LONG).show();
                                                                ArrayList<String> insert = new ArrayList<>();
                                                                Map<String, Object> logsInsert = new HashMap<>();
                                                                logsInsert.put(new Date().toString(),"Account created");
                                                                Map<String,Object> user = new HashMap<>();
                                                                user.put("firstname",firstname);
                                                                user.put("middlename",middlename);
                                                                user.put("lastname",lastname);
                                                                user.put("email",email);
                                                                user.put("id",idnumber);
                                                                user.put("logs",logsInsert);
                                                                user.put("date_created",new Date());
                                                                user.put("course",course);
                                                                user.put("type",type);
                                                                user.put("elections_participated",insert);
                                                                user.put("polls_participated",insert);
                                                                user.put("password",password);
                                                                userId = mAuth.getCurrentUser().getUid();
                                                                DocumentReference documentReference = mStore.collection("Users").document(userId);
                                                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Map <String,String> receipts = new HashMap<>();
                                                                        receipts.put("null",null);
                                                                        DocumentReference documentReference1 = mStore.collection("Users").document(userId).collection("receipts").document("null");
                                                                        documentReference1.set(receipts);
                                                                        mAuth.signInWithEmailAndPassword(UserProfile.userEmail,UserProfile.userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                                            }
                                                                        });
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
                                                                                        logsList.put(new Date().toString(),"You registered a user : "+ firstname + " " + middlename + " " + lastname);
                                                                                        DocumentReference documentReference = mStore.collection("Users").document(UserProfile.creatorId);
                                                                                        documentReference.update("logs",logsList);
                                                                                    }
                                                                                    else {
                                                                                        Log.d("TAG", "No such document");
                                                                                    }
                                                                                } else {
                                                                                    Log.d("TAG", "get failed with ", task.getException());
                                                                                }
                                                                            }
                                                                        });
                                                                        Toast.makeText(ManageUsers.this, "Success", Toast.LENGTH_SHORT).show();
                                                                        dialogAdd.dismiss();
                                                                        getUserList();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        emailBox.setError("Failed: " + e.getMessage());
                                                                        emailBox.requestFocus();
                                                                        Toast.makeText(ManageUsers.this, "Failed to register: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                                dialogAdd.dismiss();
                                                                //  return;
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                return;
                                                            }
                                                        });
                                                    }else{
                                                        Toast.makeText(ManageUsers.this, "Failed to register: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                        else {
                                            Toast.makeText(ManageUsers.this, "Failed to register: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                builder.setView(myLayout);
                dialogAdd = builder.create();
                dialogAdd.setCancelable(true);
                dialogAdd.show();
            }
        });
        searchUser.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                coordinatorLayout.setVisibility(View.VISIBLE);
                userSearch.setVisibility(View.GONE);
                spinnerTypeActivated = 1;
                fabSearch.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                spinnerTypeActivated = 0;
                if (text.matches("[a-zA-Z- .]+")){
                    filterByName(text);
                }else {
                    filterById(text);
                }
                return true;
            }
        });
    }
    public void getUserList(){
        usersList = new ArrayList<>();
        myLists.clear();
        mStore.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // usersList.add(document.getId());
                        mStore.collection("Users")
                                .document(document.getId())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            usersList.add(document.getId());
                                            DocumentSnapshot document = task.getResult();
                                            myLists.add(new ManageUsersList(document.getId(),document.getString("firstname")+ " " +document.getString("middlename")+ " "+document.getString("lastname"),document.getString("id"),document.getString("type"),document.getString("email")));
                                        }
                                        TextView numberUsers = (TextView) findViewById(R.id.numberUsers);
                                        numberUsers.setText(String.valueOf(usersList.size()+ " registered users"));
                                        showData();
                                    }
                                });
                    }
                }
                //     Toast.makeText(ManageUsers.this, usersList.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void filterByType(String text){
        List<ManageUsersList> temp = new ArrayList();
        for(ManageUsersList d: myLists){
            if (text.matches("Student")){
                if(d.getName().toLowerCase().contains(text.toLowerCase())){
                    temp.add(d);
                }
            }else if (text.matches("Faculty")){

            }else if(text.matches("Administrator")) {

            }
        }
        adapter.updateList(temp);
    }
    public void filterByName(String text){
        List<ManageUsersList> temp = new ArrayList();
        for(ManageUsersList d: myLists){
            if(d.getName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
            if (text.matches("Student")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }else if (text.matches("Faculty")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }else if (text.matches("Administrator")) {
                if (d.getType().toLowerCase().contains(text.toLowerCase())) {
                    temp.add(d);
                }
            }
        }
        adapter.updateList(temp);
    }
    public void filterById(String text){
        List<ManageUsersList> temp = new ArrayList();
        for(ManageUsersList d: myLists){
            if(d.getID().contains(text)){
                temp.add(d);
            }
        }
        adapter.updateList(temp);
    }
    private void showData() {
        adapter = new ManageUsersAdapter(myLists, this);
        rv.setAdapter(adapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
}