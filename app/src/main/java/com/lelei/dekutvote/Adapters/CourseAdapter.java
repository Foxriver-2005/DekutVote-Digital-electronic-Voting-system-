package com.lelei.dekutvote.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lelei.dekutvote.ManageUsers;
import com.lelei.dekutvote.R;
import com.lelei.dekutvote.Registration;
import com.lelei.dekutvote.model.CourseList;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private List<CourseList> myListList;
    private Context ct;
    public CourseAdapter(List<CourseList> myListList, Context ct) {
        this.myListList = myListList;
        this.ct = ct;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_course,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseList myList=myListList.get(position);
        holder.textCourse.setText(myList.getCourse());
        holder.textDepartment.setText(myList.getDepartment());
        holder.checkCourse.setChecked(myList.getIsChecked());
        holder.checkCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myList.getIsChecked() == true){
                    myList.setIsChecked(false);

                }else{
                    myList.setIsChecked(true);
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myList.getIsChecked() == true){
                    myList.setIsChecked(false);
                }else{
                    myList.setIsChecked(true);
                }
            }
        });
        if (Registration.active == true){
            //   holder.textCourses.setVisibility(View.INVISIBLE);
            holder.checkCourse.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Registration.courseRBox.setText(myList.getCourse());
                    Registration.dialog.dismiss();
                }
            });
        }
        if (ManageUsers.active == true){
            //   holder.textCourses.setVisibility(View.INVISIBLE);
            holder.checkCourse.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManageUsers.courseRBox.setText(myList.getCourse());
                    ManageUsers.dialog.dismiss();
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return myListList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textCourse;
        private TextView textDepartment,textCourses;
        private CheckBox checkCourse;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse=(TextView)itemView.findViewById(R.id.courseSelect);
            textDepartment=(TextView)itemView.findViewById(R.id.departmentSelect);
            checkCourse=(CheckBox)itemView.findViewById(R.id.courseBox);
        }
    }
}
