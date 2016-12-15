package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

public class MyChurchDetails extends AppCompatActivity {
    AsyncTask getChurchDetails=null,getMassTimings=null;
    Bundle extras;
    DatabaseHandler db;
    Typeface typeQuicksand;
    Typeface typeSegoe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_church_details);
        extras=getIntent().getExtras();
        db=DatabaseHandler.getInstance(this);
        //Fonts------------
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        if (isOnline()) {
            getChurchDetails=new GetChurchDetails().execute();
        } else {
            Toast.makeText(MyChurchDetails.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
        }
    }
    //------------------------Async Tasks------------------------------------
    public class GetChurchDetails extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        String churchNameString,aboutString,townNameString,addressString,mapCoOrdinatesString,phone1String,phone2String, townCodeString,imageURLString,priestNameString,priestAboutString,parishString,priestMobileString,dateOrdinationString,priestURLStringString;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/ChurchDetailsByID";
            HttpURLConnection c = null;
            try {
                postData =  "{\"ChurchID\":\"" + db.GetMyChurch("ChurchID")+ "\"}";
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
                    churchNameString=jsonObject.optString("ChurchName");
                    aboutString=jsonObject.optString("About");
                    townNameString=jsonObject.optString("TownName");
                    addressString=jsonObject.optString("Address");
                    mapCoOrdinatesString=jsonObject.optString("Latitude")+","+jsonObject.optString("Longitude");
                    phone1String=jsonObject.optString("Phone1");
                    phone2String=jsonObject.optString("Phone2");
                    townCodeString=jsonObject.optString("TownCode");
                    imageURLString=jsonObject.optString("ImageURL");
                    priestNameString=jsonObject.optString("PriestName");
                    priestAboutString=jsonObject.optString("PriestAbout");
                    parishString=jsonObject.optString("Parish");
                    priestMobileString=jsonObject.optString("PriestMobile");
                    dateOrdinationString=jsonObject.optString("DateOrdination").replace("/Date(", "").replace(")/", "");
                    priestURLStringString=jsonObject.optString("PriestURL");
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
                new AlertDialog.Builder(MyChurchDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)//R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                switch (extras.getString("from")){
                    case "about":
                                        RelativeLayout aboutLayout=(RelativeLayout)findViewById(R.id.aboutLayout);
                                        aboutLayout.setVisibility(View.VISIBLE);
                                        TextView aboutTitle=(TextView)findViewById(R.id.about_title);
                                        aboutTitle.setTypeface(typeQuicksand);
                                        if(!aboutString.equals("null")){
                                            TextView about=(TextView)findViewById(R.id.about);
                                            about.setText(aboutString);
                                            about.setTypeface(typeSegoe);
                                        }
                                        //Church Image----
                                        ImageView churchImage=(ImageView)findViewById(R.id.church_image);
                                        if(!imageURLString.equals("null")){
                                            Glide.with(MyChurchDetails.this)
                                                    .load(getResources().getString(R.string.url) +imageURLString.substring((imageURLString).indexOf("img")))
                                                    .thumbnail(0.1f)
                                                    .dontTransform()
                                                    .crossFade()
                                                    .into(churchImage)
                                            ;
                                        }
                        break;
                    case "timings":
                                        getMassTimings=new GetMassTimings().execute();
                        break;
                    default:
                        finish();
                }

             /*   activityScrollView.setVisibility(View.VISIBLE);
                if(!churchNameString.equals("null")){
                    churchName.setText(churchNameString);
                    churchName.setVisibility(View.VISIBLE);
                }
                else {
                    churchName.setText("-");
                }

                if(!aboutString.equals("null")){
                    about.setText(aboutString);
                    about.setVisibility(View.VISIBLE);
                    aboutGlobal=aboutString;
                }
                else {
                    about.setText("-");
                    aboutGlobal="-";
                }

                if(!townNameString.equals("null")){
                    town.setText(townNameString);
                    town.setVisibility(View.VISIBLE);
                }
                else {
                    town.setText("-");
                }

                if(!addressString.equals("null")){
                    address.setText(addressString);
                    address.setVisibility(View.VISIBLE);
                    churchAddress.setText(addressString);
                }
                else {
                    address.setText("-");
                    churchAddress.setText("-");
                }

                //Church Image----
                if(!imageURLString.equals("null")){
                    Glide.with(ChurchDetails.this)
                            .load(getResources().getString(R.string.url) +imageURLString.substring((imageURLString).indexOf("img")))
                            .placeholder(R.drawable.my_church_sample)
                            .thumbnail(0.1f)
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    churchImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    Glide.with(ChurchDetails.this)
                                            .load(R.drawable.my_church_sample)
                                            .into(churchImage)
                                    ;
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(churchImage)
                    ;
                    churchImageGlobal=imageURLString;
                }
                else {
                    churchImageGlobal="null";
                }

                //Priest-----------
                if(!priestNameString.equals("null")){
                    priestName.setText(priestNameString);
                }
                else {
                    priestName.setText("-");
                }

                if(!priestAboutString.equals("null")){
                    priestAbout.setText(priestAboutString);
                }
                else {
                    priestAbout.setText("-");
                }

                if(!parishString.equals("null")){
                    parishName.setText(parishString);
                }
                else {
                    parishName.setText("-");
                }

                if(!priestMobileString.equals("null")){
                    priestMobile.setText(priestMobileString);
                    priestMobile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {                                   //Phone call function
                            Uri number = Uri.parse("tel:" + priestMobileString);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            startActivity(callIntent);
                        }
                    });
                }
                else {
                    priestMobile.setText("-");
                }

                if(!dateOrdinationString.equals("null")){
                    SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                    Calendar cal= Calendar.getInstance();
                    cal.setTimeInMillis(Long.parseLong(dateOrdinationString));
                    dateOrdination.setText(formatted.format(cal.getTime()));
                }
                else {
                    dateOrdination.setText("-");
                }

                if(!priestURLStringString.equals("null")){
                    Glide.with(ChurchDetails.this)
                            .load(getResources().getString(R.string.url) +priestURLStringString.substring((priestURLStringString).indexOf("img")))
                            //    .fitCenter()
                            //   .placeholder(R.drawable.priest)  //stretches image
                            .thumbnail(0.1f)
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    Glide.with(ChurchDetails.this)
                                            .load(R.drawable.priest)
                                            .into(priestImage)
                                    ;
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .dontTransform()
                            .into(priestImage)
                    ;
                }
                //Contact---------
                if(!phone1String.equals("null")){
                    phone1.setText(phone1String);
                    phone1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {                                   //Phone call function
                            Uri number = Uri.parse("tel:" + phone1String);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            startActivity(callIntent);
                        }
                    });
                }
                else {
                    phone1.setText("-");
                }

                if(!phone2String.equals("null")){
                    phone2.setText(phone2String);
                    phone2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {                                   //Phone call function
                            Uri number = Uri.parse("tel:" + phone2String);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                            startActivity(callIntent);
                        }
                    });
                }
                else {
                    phone2.setVisibility(View.GONE);
                }

                if(!mapCoOrdinatesString.equals("null,null")){
                    viewMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (mapCoOrdinatesString)));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    viewMap.setVisibility(View.GONE);
                }
                //Animation-----------------------------------------------------
                final Animation fromBottom = AnimationUtils.loadAnimation(ChurchDetails.this, R.anim.fade_in_from_bottom);
                final Animation fromBottom2 = AnimationUtils.loadAnimation(ChurchDetails.this, R.anim.fade_in_from_bottom);
                final Animation fromBottom3 = AnimationUtils.loadAnimation(ChurchDetails.this, R.anim.fade_in_from_bottom);

                Handler handler = new Handler();
                aboutLayout.startAnimation(fromBottom);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        priestLayout.setVisibility(View.VISIBLE);
                        priestLayout.startAnimation(fromBottom2);
                    }
                },500);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contactLayout.setVisibility(View.VISIBLE);
                        contactLayout.startAnimation(fromBottom3);
                    }
                },1000);

                getMassTimings=new ChurchDetails.GetMassTimings().execute();*/
            }
        }
    }
    public class GetMassTimings extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ArrayList<String> sunday=new ArrayList<>();
        ArrayList<String> monday=new ArrayList<>();
        ArrayList<String> tuesday=new ArrayList<>();
        ArrayList<String> wednesday=new ArrayList<>();
        ArrayList<String> thursday =new ArrayList<>();
        ArrayList<String> friday=new ArrayList<>();
        ArrayList<String> saturday=new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetMassTimings";
            HttpURLConnection c = null;
            try {
                postData =  "{\"ChurchID\":\"" + db.GetMyChurch("ChurchID")+ "\"}";
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
                    switch (jsonObject.optString("Day")){
                        case "Sun":
                            sunday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Mon":
                            monday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Tue":
                            tuesday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Wed":
                            wednesday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Thu":
                            thursday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Fri":
                            friday.add(jsonObject.optString("FormattedTime"));
                            break;
                        case "Sat":
                            saturday.add(jsonObject.optString("FormattedTime"));
                            break;
                        default:
                    }
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
                new AlertDialog.Builder(MyChurchDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)//R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                RelativeLayout timingsLayout=(RelativeLayout)findViewById(R.id.timingsLayout);
                timingsLayout.setVisibility(View.VISIBLE);
                TextView timingsTitle=(TextView)findViewById(R.id.mass_label);
                timingsTitle.setTypeface(typeQuicksand);
                TextView sundayLabel,mondayLabel,tuesdayLabel,wednesdayLabel,thursdayLabel,fridayLabel,saturdayLabel;
                TextView sundayTiming,mondayTiming,tuesdayTiming,wednesdayTiming,thursdayTiming,fridayTiming,saturdayTiming;
                sundayLabel=(TextView)findViewById(R.id.sunday_label);
                mondayLabel=(TextView)findViewById(R.id.monday_label);
                tuesdayLabel=(TextView)findViewById(R.id.tuesday_label);
                wednesdayLabel=(TextView)findViewById(R.id.wednesday_label);
                thursdayLabel=(TextView)findViewById(R.id.thursday_label);
                fridayLabel=(TextView)findViewById(R.id.friday_label);
                saturdayLabel=(TextView)findViewById(R.id.saturday_label);
                sundayTiming=(TextView)findViewById(R.id.sunday_timings);
                mondayTiming=(TextView)findViewById(R.id.monday_timings);
                tuesdayTiming=(TextView)findViewById(R.id.tuesday_timings);
                wednesdayTiming=(TextView)findViewById(R.id.wednesday_timings);
                thursdayTiming=(TextView)findViewById(R.id.thursday_timings);
                fridayTiming=(TextView)findViewById(R.id.friday_timings);
                saturdayTiming=(TextView)findViewById(R.id.saturday_timings);
                sundayLabel.setTypeface(typeSegoe);
                mondayLabel.setTypeface(typeSegoe);
                tuesdayLabel.setTypeface(typeSegoe);
                wednesdayLabel.setTypeface(typeSegoe);
                thursdayLabel.setTypeface(typeSegoe);
                fridayLabel.setTypeface(typeSegoe);
                saturdayLabel.setTypeface(typeSegoe);
                sundayTiming.setTypeface(typeSegoe);
                mondayTiming.setTypeface(typeSegoe);
                tuesdayTiming.setTypeface(typeSegoe);
                wednesdayTiming.setTypeface(typeSegoe);
                thursdayTiming.setTypeface(typeSegoe);
                fridayTiming.setTypeface(typeSegoe);
                saturdayTiming.setTypeface(typeSegoe);
                setTimingsToTextViews(sunday,sundayTiming,sundayLabel);
                setTimingsToTextViews(monday,mondayTiming,mondayLabel);
                setTimingsToTextViews(tuesday,tuesdayTiming,tuesdayLabel);
                setTimingsToTextViews(wednesday,wednesdayTiming,wednesdayLabel);
                setTimingsToTextViews(thursday,thursdayTiming,thursdayLabel);
                setTimingsToTextViews(friday,fridayTiming,fridayLabel);
                setTimingsToTextViews(saturday,saturdayTiming,saturdayLabel);
            }
        }
        private void setTimingsToTextViews(ArrayList<String> timings, TextView timingsDisplay, TextView dayLabel){
            //To set mass timings to corresponding text views
            String dayTimings="";
            for(int i=0;i<timings.size();i++) {
                if (i==0)//to avoid first comma
                {
                    dayTimings = timings.get(0);
                }
                else
                {
                    dayTimings = dayTimings+", "+timings.get(i);
                }
            }
            if(!dayTimings.equals(""))
                timingsDisplay.setText(dayTimings);
            else {
                dayLabel.setVisibility(View.GONE);
                timingsDisplay.setVisibility(View.GONE);
            }
        }

    }

    public void novenas_click (View view){
        Intent intent=new Intent(MyChurchDetails.this,NovenaDetailsList.class);
        intent.putExtra("churchID",db.GetMyChurch("ChurchID"));
        intent.putExtra("churchName","Novenas at "+db.GetMyChurch("ChurchName"));
        intent.putExtra("from","church");
        startActivity(intent);
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
