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
    TextView insName, founder, founded, mobile, email, address, history;
    ImageView institution_image;
    Typeface typeQuicksand;
    Typeface typeSegoe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_details);
        extras=getIntent().getExtras();

        String imageURL=extras.getString("URL");

        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");

        insName =(TextView)findViewById(R.id.activity_head);
        insName.setTypeface(typeQuicksand);
        TextView addressLabel=(TextView)findViewById(R.id.address_label);
        TextView genInfoLabel=(TextView)findViewById(R.id.general_info_label);
        TextView historyLabel=(TextView)findViewById(R.id.history_label);
        founder =(TextView)findViewById(R.id.founder_name);
        founded =(TextView)findViewById(R.id.founded);
        mobile =(TextView)findViewById(R.id.mobile_number);
        email =(TextView)findViewById(R.id.email_id);
        address =(TextView)findViewById(R.id.address_details);
        history =(TextView)findViewById(R.id.history_details);
        institution_image =(ImageView)findViewById(R.id.institution_image);

        addressLabel.setTypeface(typeQuicksand);
        genInfoLabel.setTypeface(typeQuicksand);
        historyLabel.setTypeface(typeQuicksand);
        founder.setTypeface(typeSegoe);
        founded.setTypeface(typeSegoe);
        mobile.setTypeface(typeSegoe);
        email.setTypeface(typeSegoe);
        address.setTypeface(typeSegoe);
        history.setTypeface(typeSegoe);

        //Loading data------------
        if(getIntent().hasExtra("Name")){
            insName.setText(extras.getString("Name"));
        }
        if(getIntent().hasExtra("desc")){
            history.setText(extras.getString("desc"));
        }
        if(getIntent().hasExtra("Address")){
            address.setText(extras.getString("Address"));
        }
        if(getIntent().hasExtra("Email")){
            email.setText(extras.getString("Email"));
        }

        if(getIntent().hasExtra("Mobile")){
            mobile.setText(extras.getString("Mobile"));
        }
        if(getIntent().hasExtra("Founded")){
            founded.setText(extras.getString("Founded"));
        }
        if(getIntent().hasExtra("Founder")){
            founder.setText(extras.getString("Founder"));
        }

        //image loading using url
        if(!imageURL.equals("null")){
            Glide.with(InstitutionDetails.this)
                    .load(getResources().getString(R.string.url) +imageURL.substring((imageURL).indexOf("img")))
                    .thumbnail(0.1f)
                    .crossFade()
                    .dontTransform()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            institution_image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            Glide.with(InstitutionDetails.this)
                                    .load(R.drawable.church)
                                    .into(institution_image)
                            ;
                            return true;
                        }
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(institution_image)
            ;
        }
    }
}
