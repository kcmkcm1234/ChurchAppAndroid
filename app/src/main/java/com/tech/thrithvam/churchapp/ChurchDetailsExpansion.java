package com.tech.thrithvam.churchapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

public class ChurchDetailsExpansion extends AppCompatActivity {
    Bundle extras;
    Typeface typeQuicksand;
    Typeface typeSegoe;
//    Typeface typeBLKCHCRY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_church_details_expansion);
        extras=getIntent().getExtras();
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
//        typeBLKCHCRY = Typeface.createFromAsset(getAssets(),"fonts/blackchancery.ttf");

        TextView activtyHead=(TextView)findViewById(R.id.heading);
        TextView description=(TextView)findViewById(R.id.description);
        ImageView detailImage=(ImageView)findViewById(R.id.detail_image);
        activtyHead.setTypeface(typeQuicksand);
        description.setTypeface(typeSegoe);

        if(extras.getString("from").equals("view_more_about")){
            activtyHead.setText(extras.getString("heading"));
            description.setText(extras.getString("description"));
            if(!extras.getString("image").equals("null")){
                Glide.with(ChurchDetailsExpansion.this)
                        .load(getResources().getString(R.string.url) +extras.getString("image").substring((extras.getString("image")).indexOf("img")))
                        .placeholder(R.drawable.church)
                        .thumbnail(0.1f)
                        .into(detailImage)
                ;
            }
            else {
                detailImage.setVisibility(View.GONE);
            }

        }
        else if(extras.getString("from").equals("view_more_extra_detail")){
            activtyHead.setText(extras.getString("heading"));
            description.setText(extras.getString("description"));
            if(!extras.getString("image").equals("null")){
                Glide.with(ChurchDetailsExpansion.this)
                        .load(getResources().getString(R.string.url) +extras.getString("image").substring((extras.getString("image")).indexOf("img")))
                        .placeholder(R.drawable.church)
                        .thumbnail(0.1f)
                        .into(detailImage)
                ;
            }
            else {
                detailImage.setVisibility(View.GONE);
            }
        }
        else {
            finish();
        }
    }
}
