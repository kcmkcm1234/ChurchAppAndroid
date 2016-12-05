package com.tech.thrithvam.churchapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MyChurch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_church);

        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        Typeface typeBLKCHCRY = Typeface.createFromAsset(getAssets(),"fonts/blackchancery.ttf");
        Typeface typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        //Fonts---------------
        TextView activtyHead=(TextView)findViewById(R.id.activity_head);
        TextView churchName=(TextView)findViewById(R.id.church_name);
        TextView churchDetail1=(TextView)findViewById(R.id.church_detail1);
        TextView churchDetail2=(TextView)findViewById(R.id.church_detail2);

        activtyHead.setTypeface(typeQuicksand);
        churchName.setTypeface(typeBLKCHCRY);
        churchDetail1.setTypeface(typeSegoe);
        churchDetail2.setTypeface(typeSegoe);

        TextView change_my_church=(TextView)findViewById(R.id.change_mychurch);
        change_my_church.setTypeface(typeSegoe);

        TextView notices=(TextView)findViewById(R.id.notices);
        TextView events=(TextView)findViewById(R.id.events);
        TextView familyUnits=(TextView)findViewById(R.id.family_units);
        TextView gallery=(TextView)findViewById(R.id.gallery);
        TextView timings=(TextView)findViewById(R.id.timings);
        TextView about=(TextView)findViewById(R.id.about);

        notices.setTypeface(typeSegoe);
        events.setTypeface(typeSegoe);
        familyUnits.setTypeface(typeSegoe);
        gallery.setTypeface(typeSegoe);
        timings.setTypeface(typeSegoe);
        about.setTypeface(typeSegoe);
    }
}
