package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    View popupView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView searchImage =(ImageView)findViewById(R.id.searchImage);
        searchText=(AutoCompleteTextView) findViewById(R.id.searchView);

        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText(searchText.getText().toString().trim());
                if(!searchText.getText().toString().equals("")) {
                    Intent intent = new Intent(Home.this, SearchResults.class);
                    intent.putExtra("searchkey", searchText.getText().toString());
                    searchText.setText("");
                    startActivity(intent);
                }
            }
        });
        //Getting towns for autocomplete---------
        if (isOnline()) {
            getTowns=new GetAllTowns().execute();
        } else {
            Toast.makeText(Home.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
        }
        //Fonts--------------
        Typeface typeCopperplateGothic = Typeface.createFromAsset(getAssets(),"fonts/copperplate-gothic.ttf");
        Typeface typeoldeng = Typeface.createFromAsset(getAssets(),"fonts/oldeng.ttf");
        TextView appName=(TextView)findViewById(R.id.app_name);
        TextView searchLabel=(TextView)findViewById(R.id.search_label);
        TextView view_all_towns=(TextView)findViewById(R.id.view_all_towns);
        TextView novenas=(TextView)findViewById(R.id.novenasText);
        TextView my_church=(TextView)findViewById(R.id.mychurch);
        TextView nearby_church=(TextView)findViewById(R.id.nearbychurch);

        view_all_towns.setTypeface(typeCopperplateGothic);
        novenas.setTypeface(typeCopperplateGothic);
        my_church.setTypeface(typeCopperplateGothic);
        nearby_church.setTypeface(typeCopperplateGothic);
        appName.setTypeface(typeoldeng);
        searchText.setTypeface(typeCopperplateGothic);
        searchLabel.setTypeface(typeCopperplateGothic);
        //Information button-----------
        ImageView info=(ImageView) findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupView = getLayoutInflater().inflate(R.layout.item_app_info, null);
                final TextView email=(TextView)popupView.findViewById(R.id.email);
                email.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email.getText().toString()});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Mail from Church App User");
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                TextView shareApp=(TextView)popupView.findViewById(R.id.share_app);
                shareApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("text/*");
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is an innovative app to stay connected with Churches & Novenas in town \nhttps://goo.gl/jDMq6M");
                        startActivity(Intent.createChooser(shareIntent, "Share goChurch app"));
                    }
                });
                new AlertDialog.Builder(Home.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setView(popupView)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).setCancelable(false).show();
            }
        });
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
    public void view_all_towns(View view){
        Intent intent=new Intent(Home.this,AllTownsList.class);
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
                        searchText.setText("");
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getTowns!=null)getTowns.cancel(true);
    }
}
