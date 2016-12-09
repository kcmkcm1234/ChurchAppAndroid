package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Home extends AppCompatActivity {
    AsyncTask getTowns=null;
    AutoCompleteTextView searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView searchImage =(ImageView)findViewById(R.id.searchImage);
        TextView alltownsview=(TextView)findViewById(R.id.view_all_towns);
        searchText=(AutoCompleteTextView) findViewById(R.id.searchView);
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
                Intent intent=new Intent(Home.this,AllTowns.class);
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
        //Getting towns for autocomplete---------
        if (isOnline()) {
            getTowns=new GetAllTowns().execute();
        } else {
            Toast.makeText(Home.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
        }
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
    //----------------------AsyncTasks----------------------------
    public class GetAllTowns extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ArrayList<String> townListItems =new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/AllTown";
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-type", "application/json; charset=utf-16");
                c.setRequestProperty("Content-length","0");
                c.setDoInput(true);
                c.setDoOutput(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.connect();//Since no post data
                status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201: BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        br.close();
                        int a=sb.indexOf("[");
                        int b=sb.lastIndexOf("]");
                        strJson=sb.substring(a, b + 1);
                        //   strJson=cryptography.Decrypt(strJson);
                        strJson="{\"JSON\":" + strJson.replace("\\\"","\"").replace("\\\\","\\") + "}";
                }
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                msg=ex.getMessage();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                        msg=ex.getMessage();
                    }
                }
            }
            if(strJson!=null)
            {try {
                JSONObject jsonRootObject = new JSONObject(strJson);
                jsonArray = jsonRootObject.optJSONArray("JSON");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    msg=jsonObject.optString("Message");
                    pass=jsonObject.optBoolean("Flag",true);
                    townListItems.add(jsonObject.optString("Name"));
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(!pass) {
            }
            else {
                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (Home.this,android.R.layout.simple_list_item_activated_1,townListItems);
                searchText.setAdapter(adapter);
                searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent=new Intent(Home.this,SearchResults.class);
                        intent.putExtra("searchkey",searchText.getText().toString());
                        startActivity(intent);
                    }
                });
            }
        }
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
