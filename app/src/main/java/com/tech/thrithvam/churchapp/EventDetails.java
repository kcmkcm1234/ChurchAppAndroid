package com.tech.thrithvam.churchapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class EventDetails extends AppCompatActivity {
    Bundle extras;
    String URL;
    TextView Date,Events_head,Events_content;
    ImageView eventsImage;
    Typeface typeQuicksand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        extras=getIntent().getExtras();

        URL=extras.getString("URL");
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        Events_head=(TextView)findViewById(R.id.activity_event_head);
        Events_head.setTypeface(typeQuicksand);
        Date=(TextView)findViewById(R.id.Event_date);
        Events_content=(TextView)findViewById(R.id.Event_details);
        eventsImage =(ImageView)findViewById(R.id.event_img);

        if(getIntent().hasExtra("EventName")){
            Events_head.setText(extras.getString("EventName"));
        }
        if(getIntent().hasExtra("StartDate")){
            Date.setText(extras.getString("StartDate"));
        }
        if(getIntent().hasExtra("Description")){
            Events_content.setText(extras.getString("Description"));
        }
        //image loading using url
        if(!URL.equals("null")){
            Glide.with(EventDetails.this)
                    .load(getResources().getString(R.string.url) +URL.substring((URL).indexOf("img")))
                    .dontTransform()
                    .thumbnail(0.1f)
                    .into(eventsImage)
            ;
        }
        else {
            eventsImage.setVisibility(View.GONE);
        }
    }
}
