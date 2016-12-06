package com.tech.thrithvam.churchapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class PiousOrgDetails extends AppCompatActivity {
    Bundle extras;
    String URL;
    TextView P_Name,about;
    ImageView Patron_image;
    Typeface typeQuicksand;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pious_org_details);
        extras=getIntent().getExtras();

        URL=extras.getString("URL");

        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        P_Name=(TextView)findViewById(R.id.activity_Org_head);
        about=(TextView)findViewById(R.id.history_details);
        Patron_image =(ImageView)findViewById(R.id.Parton_image);
        P_Name.setTypeface(typeQuicksand);

        if(getIntent().hasExtra("PatronName")){
            P_Name.setText(extras.getString("PatronName"));
        }
        if(getIntent().hasExtra("Desc")){
            about.setText(extras.getString("Desc"));
        }
        //image loading using url
        if(!URL.equals("null")){
            Glide.with(PiousOrgDetails.this)
                    .load(getResources().getString(R.string.url) +URL.substring((URL).indexOf("img")))
                    .placeholder(R.drawable.priest)
                    .thumbnail(0.1f)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Patron_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            Glide.with(PiousOrgDetails.this)
                                    .load(R.drawable.church)
                                    .into(Patron_image)
                            ;
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(Patron_image)
            ;
        }




    }
}
