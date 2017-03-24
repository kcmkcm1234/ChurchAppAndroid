package com.tech.thrithvam.churchapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class EducationForumEvents extends AppCompatActivity {
    Typeface typeSegoe,typeQuicksand;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_forum_events);
        db=DatabaseHandler.getInstance(this);
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        Toast.makeText(this, db.GetMyChurch("eduForumMemberRegistrationID"), Toast.LENGTH_SHORT).show();
    }
}
