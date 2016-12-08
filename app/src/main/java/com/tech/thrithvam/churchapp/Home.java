package com.tech.thrithvam.churchapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView searchImage =(ImageView)findViewById(R.id.searchImage);
        TextView alltownsview=(TextView)findViewById(R.id.view_all_towns);
        final EditText searchText=(EditText)findViewById(R.id.searchView);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {    //for clicking enter on keyboard
                if (i == EditorInfo.IME_ACTION_GO) {
                    Intent intent=new Intent(Home.this,SearchResults.class);
                    intent.putExtra("searchkey",searchText.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        alltownsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this,Alltowns.class);
                /*intent.putExtra("searchkey",searchText.getText().toString());*/
                startActivity(intent);
            }
        });
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Home.this,SearchResults.class);
                intent.putExtra("searchkey",searchText.getText().toString());
                startActivity(intent);
            }
        });


        //Fonts--------------
        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        searchText.setTypeface(typeSegoe);
        TextView view_all_towns=(TextView)findViewById(R.id.view_all_towns);
        TextView novenas=(TextView)findViewById(R.id.novenasText);
        TextView my_church=(TextView)findViewById(R.id.mychurch);
        TextView nearby_church=(TextView)findViewById(R.id.nearbychurch);

        view_all_towns.setTypeface(typeSegoe);
        novenas.setTypeface(typeSegoe);
        my_church.setTypeface(typeSegoe);
        nearby_church.setTypeface(typeSegoe);
    }
    public void mychurch(View view){
        Intent intent=new Intent(Home.this,MyChurch.class);
        startActivity(intent);
    }
    public void novenas(View view){
        Intent intent=new Intent(Home.this,Novenas.class);
        startActivity(intent);
    }
    public void nearby_church(View view){
        Intent intent=new Intent(Home.this,NearbyChurches.class);
        startActivity(intent);
    }
}
