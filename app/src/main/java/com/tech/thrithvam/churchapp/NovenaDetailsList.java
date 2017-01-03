package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
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

public class NovenaDetailsList extends AppCompatActivity {
    Typeface typeQuicksand;
    AsyncTask getNovenaChurchList=null;
    Bundle extras;
    String patronID;
    String churchID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novena_details_list);
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        extras=getIntent().getExtras();

        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        activityHead.setTypeface(typeQuicksand);

        switch (extras.getString("from")){
            case "novenas":                                         //List of churches with the novena
                patronID=extras.getString("patronID");
                activityHead.setText(extras.getString("patronName"));
                break;
            case  "church":                                         //List of novenas in the church
                churchID=extras.getString("churchID");
                activityHead.setText(extras.getString("churchName"));
                break;
            default:
                finish();
        }

        if (isOnline()) {
            getNovenaChurchList=new GetNovenaChurchList().execute();
        } else {
            Toast.makeText(NovenaDetailsList.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
        }
    }
    //Async tasks---------------------------------
    public class GetNovenaChurchList extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ArrayList<String[]> novenaChurchItems=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url="";
            switch (extras.getString("from")){
                case "novenas":
                    url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/NovenasByPatronID";
                    break;
                case  "church":
                    url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/NovenasByChurchID";
                    break;
            }
            HttpURLConnection c = null;
            try {
                switch (extras.getString("from")){
                    case "novenas":
                        postData =  "{\"PatronID\":\"" + patronID+ "\"}";
                        break;
                    case  "church":
                        postData =  "{\"ChurchID\":\"" + churchID+ "\"}";
                        break;
                }
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-type", "application/json; charset=utf-16");
                c.setRequestProperty("Content-length",Integer.toString(postData.length()));
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
                    String[] data=new String[10];
                    data[0]=jsonObject.optString("ID");
                    data[1]=jsonObject.optString("NovenaCaption");
                    data[2]=jsonObject.optString("ChurchName");
                    data[3]=jsonObject.optString("Description");
                    data[4]=jsonObject.optString("StartDate").replace("/Date(", "").replace(")/", "");
                    data[5]=jsonObject.optString("EndDate").replace("/Date(", "").replace(")/", "");
                    data[6]=jsonObject.optString("URL");
                    data[7]=jsonObject.optString("DayAndTime").replace("Dai-", "");
                    data[8]=jsonObject.optString("Latitude","null")+","+jsonObject.optString("Longitude","null");
                    data[9]=jsonObject.optString("TownName","null");
                    novenaChurchItems.add(data);
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
                Intent noItemsIntent=new Intent(NovenaDetailsList.this,NothingToDisplay.class);
                noItemsIntent.putExtra("msg",msg);
                noItemsIntent.putExtra("activityHead","Novenas");
                startActivity(noItemsIntent);
                finish();
            }
            else {
                CustomAdapter adapter=new CustomAdapter(NovenaDetailsList.this, novenaChurchItems,"NovenaDetailsList");
                ListView novenaChurchList=(ListView) findViewById(R.id.novena_church_list);
                novenaChurchList.setAdapter(adapter);
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
        if(getNovenaChurchList!=null)getNovenaChurchList.cancel(true);
    }
}
