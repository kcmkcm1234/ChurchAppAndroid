package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyChurchWizard extends AppCompatActivity {
    AutoCompleteTextView searchText;
    AsyncTask getTowns=null,searching=null;
    String searchKey;
    int selectedChurchPosition=-1;
    DatabaseHandler db;
    ArrayList<String[]> churchItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_church_wizard);
        db=DatabaseHandler.getInstance(this);
        //Fonts---------------
        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        Typeface typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        activityHead.setTypeface(typeQuicksand);
        TextView instr1=(TextView)findViewById(R.id.instruction1);
        instr1.setTypeface(typeSegoe);
        Typeface typeCopperplateGothic = Typeface.createFromAsset(getAssets(),"fonts/copperplate-gothic.ttf");
        searchText=(AutoCompleteTextView) findViewById(R.id.searchViewText);
        searchText.setTypeface(typeCopperplateGothic);
        if(isOnline()){
            getTowns=new GetAllTowns().execute();
        }
        ImageView searchImage =(ImageView)findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText(searchText.getText().toString().trim());
                if(!searchText.getText().toString().equals("")) {
                    searching=new ChurchTownSearchResults().execute();
                    View views = MyChurchWizard.this.getCurrentFocus();
                    if (views != null) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(views.getWindowToken(), 0);
                    }
                }
            }
        });
    }
    //-----------------------------Async Tasks----------------------------------------
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
                        (MyChurchWizard.this,android.R.layout.simple_list_item_activated_1,townListItems);
                searchText.setAdapter(adapter);
                searchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        searchText.setText(searchText.getText().toString().trim());
                        if(!searchText.getText().toString().equals("")) {
                            View views = MyChurchWizard.this.getCurrentFocus();
                            if (views != null) {
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(views.getWindowToken(), 0);
                            }
                            searching=new ChurchTownSearchResults().execute();
                        }
                    }
                });
            }
        }
    }
    public class ChurchTownSearchResults extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ListView churchList=(ListView) findViewById(R.id.resultsGrid);;
        RelativeLayout setMychurchButton=(RelativeLayout)findViewById(R.id.set_mychurch_button);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            searchKey=searchText.getText().toString();
            churchItems=new ArrayList<>();
            selectedChurchPosition=-1;
            setMychurchButton.setVisibility(View.INVISIBLE);
            churchList.setVisibility(View.INVISIBLE);
            TextView instruction1=(TextView)findViewById(R.id.instruction1);
            instruction1.setVisibility(View.GONE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/SearchChurch";
            HttpURLConnection c = null;
            try {
                postData =  "{\"SearchTerm\":\"" + searchKey+ "\"}";
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-type", "application/json; charset=utf-16");
                c.setRequestProperty("Content-length", Integer.toString(postData.length()));
                c.setDoInput(true);
                c.setDoOutput(true);
                c.setUseCaches(false);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                DataOutputStream wr = new DataOutputStream(c.getOutputStream());
                wr.writeBytes(postData);
                wr.flush();
                wr.close();
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
                    String[] data=new String[5];
                    data[0]=jsonObject.optString("ID");
                    data[1]=jsonObject.optString("ChurchName");
                    data[2]=jsonObject.optString("Town");
                    data[3]=jsonObject.optString("ImageURL");
                    data[4]=jsonObject.optString("Address");
                    churchItems.add(data);
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadingIndicator.setVisibility(View.GONE);
            if(!pass) {
            }
            else {
                CustomAdapter adapter=new CustomAdapter(MyChurchWizard.this, churchItems,"ChurchTownSearchResults");
                churchList.setAdapter(adapter);
                churchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        view.setSelected(true);

                        selectedChurchPosition=position;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            setMychurchButton.setBackgroundColor(getColor(R.color.colorAccent));
                        }
                        else {
                            setMychurchButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        }
                    }
                });
                searchText.setSelectAllOnFocus(true);
                churchList.setVisibility(View.VISIBLE);
                setMychurchButton.setVisibility(View.VISIBLE);
                setMychurchButton.setBackgroundResource(R.drawable.white_transparent_card);

            }
        }
    }
    public void set_as_mychurch(View view){
        if(selectedChurchPosition>-1){
            Toast.makeText(MyChurchWizard.this,getResources().getString(R.string.mychurch_set_notification,churchItems.get(selectedChurchPosition)[1]),Toast.LENGTH_SHORT).show();
            db.SetMyChurch(churchItems.get(selectedChurchPosition)[0],
                    churchItems.get(selectedChurchPosition)[1],
                    churchItems.get(selectedChurchPosition)[2],
                    churchItems.get(selectedChurchPosition)[4]
                    );
            Intent intent=new Intent(MyChurchWizard.this,MyChurch.class);
            startActivity(intent);
            finish();
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
        if(searching!=null)searching.cancel(true);
        if(getTowns!=null)getTowns.cancel(true);
    }
}
