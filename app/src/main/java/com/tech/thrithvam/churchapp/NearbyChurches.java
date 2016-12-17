package com.tech.thrithvam.churchapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NearbyChurches extends AppCompatActivity {
    Typeface typeQuicksand;
    AsyncTask getNearbyChurchList;
    final int MY_PERMISSIONS_LOCATION=555;
    String stringLatitude;
    String stringLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_churches);
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        activityHead.setTypeface(typeQuicksand);

        //Permission checking for higher versions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED /*&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED*/){
            if (ActivityCompat.shouldShowRequestPermissionRationale(NearbyChurches.this,Manifest.permission.ACCESS_FINE_LOCATION)) {//User Denied it before
                Toast.makeText(NearbyChurches.this,R.string.loc_permission_denied,Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(NearbyChurches.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_LOCATION);
            } else {//Has permission already
                ActivityCompat.requestPermissions(NearbyChurches.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_LOCATION);
            }
        }
        else {
           getLatLong();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[],int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    getLatLong();
                } else {
                    // permission denied
                    Toast.makeText(NearbyChurches.this,R.string.loc_permission_denied,Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void getLatLong(){
        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(NearbyChurches.this);
        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);

            //Converting to 6 decimal points
            stringLatitude=String.format(Locale.US,"%.6f",Double.parseDouble(stringLatitude));
            stringLongitude=String.format(Locale.US,"%.6f",Double.parseDouble(stringLongitude));

            Toast.makeText(NearbyChurches.this,"Found your location as\nLatitude: "+stringLatitude+"\nLongitude: "+stringLongitude,Toast.LENGTH_LONG).show();
            if (isOnline()) {
                getNearbyChurchList=new GetNearbyChurchList().execute();
            } else {
                Toast.makeText(NearbyChurches.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            // can't get location. GPS or Network is not enabled. Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }
    //----------------------------------Async Tasks--------------------------------------
    public class GetNearbyChurchList extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ArrayList<String[]> nearbyChurchItems=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetNearByChurches";
            HttpURLConnection c = null;
            try {
                postData = "{\"Latitude\":\"" + stringLatitude + "\",\"Longitude\":\"" + stringLongitude + "\",\"churchcount\":\"" + "100" + "\",\"maxdistance\":\"" + "100" +"\"}";
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
                    String[] data=new String[6];
                    data[0]=jsonObject.optString("ChurchID");
                    data[1]=jsonObject.optString("Name");
                    data[2]=jsonObject.optString("Address");
                    data[3]=jsonObject.optString("Latitude")+","+jsonObject.optString("Longitude");
                    data[4]=jsonObject.optString("Distance");
                    data[5]=jsonObject.optString("ImgURL");
                    nearbyChurchItems.add(data);
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
                new AlertDialog.Builder(NearbyChurches.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)//R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                CustomAdapter adapter=new CustomAdapter(NearbyChurches.this,nearbyChurchItems ,"NearbyChurchList");
                ListView nearbyChurchList=(ListView) findViewById(R.id.nearby_church_list);
                nearbyChurchList.setAdapter(adapter);
                nearbyChurchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(NearbyChurches.this,ChurchDetails.class);
                        intent.putExtra("churchID",nearbyChurchItems.get(position)[0]);
                        intent.putExtra("churchname",nearbyChurchItems.get(position)[1]);
                        intent.putExtra("address",nearbyChurchItems.get(position)[2]);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getNearbyChurchList!=null)getNearbyChurchList.cancel(true);
    }
}
