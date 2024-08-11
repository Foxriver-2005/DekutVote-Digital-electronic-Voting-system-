package com.lelei.dekutvote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class Feedback extends AppCompatActivity {
    ImageView star1,star2,star3,star4,star5;
    Boolean fstar1,fstar2,fstar3,fstar4,fstar5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        fstar1 = false;
        fstar2 = false;
        fstar3 = false;
        fstar4 = false;
        fstar5 = false;
        star1=  (ImageView)findViewById(R.id.star1);
        star2=  (ImageView)findViewById(R.id.star2);
        star3=  (ImageView)findViewById(R.id.star3);
        star4=  (ImageView)findViewById(R.id.star4);
        star5=  (ImageView)findViewById(R.id.star5);
        EditText text = (EditText) findViewById(R.id.editFeedback);
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fstar1 == false){
                    fstar1 = true;
                    ImageViewCompat.setImageTintList(star1, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.colorAccent)));
                }else{
                    fstar1 = false;
                    ImageViewCompat.setImageTintList(star1, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.silver)));
                }
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fstar2 == false){
                    fstar2 = true;
                    ImageViewCompat.setImageTintList(star2, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.colorAccent)));
                }else{
                    fstar2 = false;
                    ImageViewCompat.setImageTintList(star2, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.silver)));
                }
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fstar3 == false){
                    fstar3 = true;
                    ImageViewCompat.setImageTintList(star3, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.colorAccent)));
                }else{
                    fstar3 = false;
                    ImageViewCompat.setImageTintList(star3, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.silver)));
                }
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fstar4 == false){
                    fstar4 = true;
                    ImageViewCompat.setImageTintList(star4, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.colorAccent)));
                }else{
                    fstar4 = false;
                    ImageViewCompat.setImageTintList(star4, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.silver)));
                }
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fstar5 == false){
                    fstar5 = true;
                    ImageViewCompat.setImageTintList(star5, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.colorAccent)));
                }else{
                    fstar5 = false;
                    ImageViewCompat.setImageTintList(star5, ColorStateList.valueOf(ContextCompat.getColor(Feedback.this, R.color.silver)));
                }
            }
        });
        Button sendFeedback = (Button) findViewById(R.id.sendFeedback);
        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = text.getText().toString();
                if (string.isEmpty()){
                    text.setError("Feedback is required");
                    text.requestFocus();
                }else{
                    Toast.makeText(Feedback.this, "Feedback sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}