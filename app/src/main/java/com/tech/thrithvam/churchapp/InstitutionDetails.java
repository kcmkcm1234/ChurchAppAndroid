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

public class InstitutionDetails extends AppCompatActivity {
    Bundle extras;
    String URL;
    TextView Ins_Name,Founder,Founded,Mobile,Email,Address,History;
    ImageView Institution_image;
    Typeface typeQuicksand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_details);
        extras=getIntent().getExtras();

        URL=extras.getString("URL");

        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        Ins_Name=(TextView)findViewById(R.id.activity_Institution_head);
        Ins_Name.setTypeface(typeQuicksand);
        Founder=(TextView)findViewById(R.id.Founder);
        Founded=(TextView)findViewById(R.id.Founded);
        Mobile=(TextView)findViewById(R.id.Mobile_Number);
        Email=(TextView)findViewById(R.id.Email_id);
        Address=(TextView)findViewById(R.id.Address_details);
        History=(TextView)findViewById(R.id.history_details);
        Institution_image=(ImageView)findViewById(R.id.Institution_image);

        if(getIntent().hasExtra("Name")){
            Ins_Name.setText(extras.getString("Name"));
        }
        if(getIntent().hasExtra("desc")){
            History.setText(extras.getString("desc"));
        }
        if(getIntent().hasExtra("Address")){
            Address.setText(extras.getString("Address"));
        }
        if(getIntent().hasExtra("Email")){
            Email.setText(extras.getString("Email"));
        }

        if(getIntent().hasExtra("Mobile")){
            Mobile.setText(extras.getString("Mobile"));
        }
        if(getIntent().hasExtra("Founded")){
            Founded.setText(extras.getString("Founded"));
        }
        if(getIntent().hasExtra("Founder")){
            Founder.setText(extras.getString("Founder"));
        }

        //image loading using url
        if(!URL.equals("null")){
            Glide.with(InstitutionDetails.this)
                    .load(getResources().getString(R.string.url) +URL.substring((URL).indexOf("img")))
                    .placeholder(R.drawable.priest)
                    .thumbnail(0.1f)
                    .crossFade()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            Institution_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            Glide.with(InstitutionDetails.this)
                                    .load(R.drawable.church)
                                    .into(Institution_image)
                            ;
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(Institution_image)
            ;
        }

    }
}
