package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class PriestDetails extends AppCompatActivity {

    Bundle extras;
    String ChurchID;
    Typeface typeQuicksand;
    TextView Priest_head;
    AsyncTask getPriestList=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priest_details);
        extras=getIntent().getExtras();

        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        Priest_head=(TextView)findViewById(R.id.activity_priest_head);
        Priest_head.setTypeface(typeQuicksand);

        if(getIntent().hasExtra("from")){
            if(extras.getString("from").equals("diocese")){
                (findViewById(R.id.itemsLoading)).setVisibility(View.GONE);
                Priest_head.setText("Verapoly ArchDiocesan Curia");

                ArrayList<String[]> priestListItems =new ArrayList<>();

                String[] data=new String[3];
                data[0]="Archbishop";
                data[1]="The Most Rev. Dr. Joseph Kalathiparambil";
                data[2]=Integer.toString(R.drawable.dr_joseph_kalathiparambil);
                priestListItems.add(data);

                String[] data1=new String[3];
                data1[0]="Vicar General";
                data1[1]="Very Rev. Msgr. Mathew Kallinkal";
                data1[2]="null";
                priestListItems.add(data1);

                String[] data2=new String[3];
                data2[0]="Vicar General";
                data2[1]="Very Rev. Msgr. Mathew Elanjimattam";
                data2[2]="null";
                priestListItems.add(data2);

                String[] data3=new String[3];
                data3[0]="Chancellor";
                data3[1]="Very Rev. Fr. Ebigin Arackal";
                data3[2]="null";
                priestListItems.add(data3);

                String[] data4=new String[3];
                data4[0]="Procurator";
                data4[1]="Very Rev. Fr. Peter Kochuveettil";
                data4[2]="null";
                priestListItems.add(data4);

                String[] data5=new String[3];
                data5[0]="Asst. Procurator";
                data5[1]="Rev. Fr. Alex Kurusuveetil";
                data5[2]="null";
                priestListItems.add(data5);

                String[] data6=new String[3];
                data6[0]="Judicial Vicar";
                data6[1]="Very Rev. Fr. Francis D’Silva";
                data6[2]="null";
                priestListItems.add(data6);

                CustomAdapter adapter=new CustomAdapter(PriestDetails.this, priestListItems,"DioceseList");
                ListView PriestsList=(ListView) findViewById(R.id.priest_list);
                PriestsList.setAdapter(adapter);
            }
        }
        else {//It is a priest list
            ChurchID=extras.getString("ChurchID");
            if (isOnline()) {
                getPriestList= new GetPriestList().execute();
            } else {
                Toast.makeText(PriestDetails.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
            }
        }
    }

    //--------------------------------------Async Tasks--------------------------------------
    public class GetPriestList extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ArrayList<String[]> priestListItems =new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetAllPriestsbyChurchID";
            HttpURLConnection c = null;
            try {
                postData =  "{\"ChurchID\":\"" + ChurchID+ "\"}";
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
                    String[] data=new String[13];
                    data[0]=jsonObject.optString("ID");
                    data[1]=jsonObject.optString("Name");
                    data[2]=jsonObject.optString("Address");
                    data[3]=jsonObject.optString("URL");
                    data[4]=jsonObject.optString("About");
                    data[5]=jsonObject.optString("Parish");
                    data[6]=jsonObject.optString("DOB").replace("/Date(", "").replace(")/", "");
                    data[7]=jsonObject.optString("DateOrdination").replace("/Date(", "").replace(")/", "");
                    data[8]=jsonObject.optString("Email");
                    data[9]=jsonObject.optString("Mobile");
                    data[10]=jsonObject.optString("Designation");
                    data[11]=jsonObject.optString("Status");
                    data[12]=jsonObject.optString("BaptismalName");
                    priestListItems.add(data);
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
                Intent noItemsIntent=new Intent(PriestDetails.this,NothingToDisplay.class);
                noItemsIntent.putExtra("msg",msg);
                noItemsIntent.putExtra("activityHead","Priests");
                startActivity(noItemsIntent);
                finish();
            }
            else {
                CustomAdapter adapter=new CustomAdapter(PriestDetails.this, priestListItems,"PriestList");
                ListView PriestsList=(ListView) findViewById(R.id.priest_list);
                PriestsList.setAdapter(adapter);
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
        if(getPriestList!=null)getPriestList.cancel(true);
    }
}
