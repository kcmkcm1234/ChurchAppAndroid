package com.tech.thrithvam.churchapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.provider.CalendarContract;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationForumEventsDetails extends AppCompatActivity {
    Bundle extras;
    DatabaseHandler db;
    String URL;
    TextView Date, eventsHead, eventsContent,setReminder;
    ImageView eventsImage;
    Typeface typeQuicksand,typeSegoe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_forum_events_details);    extras=getIntent().getExtras();
        db=DatabaseHandler.getInstance(this);
        URL=extras.getString("URL");
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        eventsHead =(TextView)findViewById(R.id.activity_event_head);
        eventsHead.setTypeface(typeQuicksand);
        Date=(TextView)findViewById(R.id.event_date);
        eventsContent =(TextView)findViewById(R.id.event_details);
        eventsImage =(ImageView)findViewById(R.id.event_img);
        setReminder=(TextView)findViewById(R.id.set_reminder);
        setReminder.setTypeface(typeSegoe);
        eventsContent.setTypeface(typeSegoe);


        if(getIntent().hasExtra("EventName")){
            eventsHead.setText(extras.getString("EventName"));
        }
        if(getIntent().hasExtra("StartDateInMillis")){
            final Calendar cal=Calendar.getInstance();
            SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
            cal.setTimeInMillis(Long.parseLong(extras.getString("StartDateInMillis")));
            cal.set(Calendar.HOUR,5);
            String startDate=formatted.format(cal.getTime());
            Date.setText(startDate);
            Date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60*1000);
                    intent.putExtra(CalendarContract.Events.TITLE,extras.getString("EventName"));
                    intent.putExtra(CalendarContract.Events.DESCRIPTION,"Reminder set from goChurch app");
                    if(getIntent().hasExtra("ChurchName")){
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,extras.getString("ChurchName"));
                    }
                    intent.putExtra(CalendarContract.Events.HAS_ALARM,true);
                    startActivity(intent);
                }
            });
            setReminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.item/event");
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60*1000);
                    intent.putExtra(CalendarContract.Events.TITLE,extras.getString("EventName"));
                    intent.putExtra(CalendarContract.Events.DESCRIPTION,"Reminder set from goChurch app");
                    if(getIntent().hasExtra("ChurchName")){
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,extras.getString("ChurchName"));
                    }
                    intent.putExtra(CalendarContract.Events.HAS_ALARM,true);
                    startActivity(intent);
                }
            });
        }
        if(getIntent().hasExtra("Description")){
            eventsContent.setText(extras.getString("Description"));
        }
        //image loading using url
        if(!URL.equals("null")){
            Glide.with(EducationForumEventsDetails.this)
                    .load(getResources().getString(R.string.url) +URL.substring((URL).indexOf("img")))
                    .dontTransform()
                    .thumbnail(0.1f)
                    .into(eventsImage)
            ;
            eventsImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent photoIntent=new Intent(EducationForumEventsDetails.this,ImageViewerActivity.class);
                    photoIntent.putExtra("URL",URL);
                    startActivity(photoIntent);
                }
            });
        }
        else {
            eventsImage.setVisibility(View.GONE);
        }

        //Response------------------------
        RadioGroup responseRadioGroup=(RadioGroup)findViewById(R.id.response);
        if(!extras.getString("ResponseCode").equals("null")){
            switch (Integer.parseInt(extras.getString("ResponseCode"))){
                case 1:responseRadioGroup.check(R.id.response_1);
                    break;
                case 2:responseRadioGroup.check(R.id.response_2);
                    break;
                case 3:responseRadioGroup.check(R.id.response_3);
                    break;
            }
        }
        responseRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                new SendResponse().execute();
            }
        });
    }
    private class SendResponse extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        ProgressDialog pDialog=new ProgressDialog(EducationForumEventsDetails.this);
        SendResponse( ){
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.show();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/InsertEduForumEventResponse";
            HttpURLConnection c = null;
            try {
                postData =  "{\"churchID\":\""+ db.GetMyChurch("ChurchID")
                        +"\",\"registrationID\":\"" +  db.GetMyChurch("eduForumMemberRegistrationID")
                        +"\",\"eventID\":\"" + extras.getString("EventID")
                        +"\",\"memberResponseJson\":"+"[{\"MemberID\":\"a8e7326c-3f82-48eb-9629-556a0aa6f029\",\"ResponseCode\":\"50\"}]"+ "}";
                java.net.URL u = new URL(url);
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
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(!pass) {
                new AlertDialog.Builder(EducationForumEventsDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                        .setMessage(msg)//R.string.no_items)
                        .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false).show();
            }
            else {
                Toast.makeText(EducationForumEventsDetails.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}