package com.tech.thrithvam.churchapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDetails extends AppCompatActivity {
    Bundle extras;
    String URL;
    TextView Date, eventsHead, eventsContent,setReminder;
    ImageView eventsImage;
    Typeface typeQuicksand,typeSegoe;
    AsyncTask getEventDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        extras=getIntent().getExtras();
        typeQuicksand = Typeface.createFromAsset(getAssets(), "fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(), "fonts/segoeui.ttf");
        eventsHead = (TextView) findViewById(R.id.activity_event_head);
        eventsHead.setTypeface(typeQuicksand);
        Date = (TextView) findViewById(R.id.event_date);
        eventsContent = (TextView) findViewById(R.id.event_details);
        eventsImage = (ImageView) findViewById(R.id.event_img);
        setReminder = (TextView) findViewById(R.id.set_reminder);
        setReminder.setTypeface(typeSegoe);
        eventsContent.setTypeface(typeSegoe);
        if(getIntent().hasExtra("EventID")){
            getEventDetails=new GetEvent().execute();
        }
        else {
            URL = extras.getString("URL");

            if (getIntent().hasExtra("EventName")) {
                eventsHead.setText(extras.getString("EventName"));
            }
            if (getIntent().hasExtra("StartDateInMillis")) {
                final Calendar cal = Calendar.getInstance();
                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                cal.setTimeInMillis(Long.parseLong(extras.getString("StartDateInMillis")));
                cal.set(Calendar.HOUR, 5);
                String startDate = formatted.format(cal.getTime());
                Date.setText(startDate);
                Date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60*1000);
                        intent.putExtra(CalendarContract.Events.TITLE, extras.getString("EventName"));
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Reminder set from goChurch app");
                        if (getIntent().hasExtra("ChurchName")) {
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, extras.getString("ChurchName"));
                        }
                        intent.putExtra(CalendarContract.Events.HAS_ALARM, true);
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
                        intent.putExtra(CalendarContract.Events.TITLE, extras.getString("EventName"));
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Reminder set from goChurch app");
                        if (getIntent().hasExtra("ChurchName")) {
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, extras.getString("ChurchName"));
                        }
                        intent.putExtra(CalendarContract.Events.HAS_ALARM, true);
                        startActivity(intent);
                    }
                });
            }
            if (getIntent().hasExtra("Description")) {
                eventsContent.setText(extras.getString("Description"));
            }
            //image loading using url
            if (!URL.equals("null")) {
                Glide.with(EventDetails.this)
                        .load(getResources().getString(R.string.url) + URL.substring((URL).indexOf("img")))
                        .dontTransform()
                        .thumbnail(0.1f)
                        .into(eventsImage)
                ;
                eventsImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent photoIntent = new Intent(EventDetails.this, ImageViewerActivity.class);
                        photoIntent.putExtra("URL", URL);
                        startActivity(photoIntent);
                    }
                });
            } else {
                eventsImage.setVisibility(View.GONE);
            }
        }
    }
    //---------------------------------Async Tasks--------------------------------------
    public class GetEvent extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        String eventName="",description="",imageURL="",startDate="";
        ScrollView activityScrollview=(ScrollView)findViewById(R.id.activity_scrollview);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            activityScrollview.setVisibility(View.GONE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetEventbyEventID";
            HttpURLConnection c = null;
            try {
                postData =  "{\"EventID\":\"" + getIntent().getExtras().getString("EventID")+ "\"}";
                java.net.URL u = new java.net.URL(url);
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
                    eventName=jsonObject.optString("EventName");
                    description=jsonObject.optString("Descrtiption");
                    imageURL=jsonObject.optString("URL");
                    startDate=jsonObject.getString("StartDate").replace("/Date(", "").replace(")/", "");;
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
                Intent intent=new Intent(EventDetails.this,Events.class);
                DatabaseHandler db=DatabaseHandler.getInstance(EventDetails.this);
                intent.putExtra("churchID",db.GetMyChurch("ChurchID"));
                startActivity(intent);
                finish();
            }
            else {
                URL = imageURL;

                if (!eventName.equals("null")) {
                    eventsHead.setText(eventName);
                }
                else{
                    eventsHead.setText("");
                }

                if (!startDate.equals("null")) {
                    final Calendar cal = Calendar.getInstance();
                    SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                    cal.setTimeInMillis(Long.parseLong(startDate));
                    cal.set(Calendar.HOUR, 5);
                    final String startDate = formatted.format(cal.getTime());
                    Date.setText(startDate);
                    final DatabaseHandler db=DatabaseHandler.getInstance(EventDetails.this);
                    Date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_EDIT);
                            intent.setType("vnd.android.cursor.item/event");
                            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
                            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
//                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis()+60*60*1000);
                            intent.putExtra(CalendarContract.Events.TITLE, eventName);
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Reminder set from goChurch app");
                            if (getIntent().hasExtra("ChurchName")) {
                                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, db.GetMyChurch("ChurchName")==null?"":db.GetMyChurch("ChurchName"));
                            }
                            intent.putExtra(CalendarContract.Events.HAS_ALARM, true);
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
                            intent.putExtra(CalendarContract.Events.TITLE, eventName);
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, "Reminder set from goChurch app");
                            if (getIntent().hasExtra("ChurchName")) {
                                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, db.GetMyChurch("ChurchName")==null?"":db.GetMyChurch("ChurchName"));
                            }
                            intent.putExtra(CalendarContract.Events.HAS_ALARM, true);
                            startActivity(intent);
                        }
                    });
                }
                if (!description.equals("null")) {
                    eventsContent.setText(description);
                }
                else {
                    eventsContent.setText("");
                }
                //image loading using url
                if (!URL.equals("null")) {
                    Glide.with(EventDetails.this)
                            .load(getResources().getString(R.string.url) + URL.substring((URL).indexOf("img")))
                            .dontTransform()
                            .thumbnail(0.1f)
                            .into(eventsImage)
                    ;
                    eventsImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent photoIntent = new Intent(EventDetails.this, ImageViewerActivity.class);
                            photoIntent.putExtra("URL", URL);
                            startActivity(photoIntent);
                        }
                    });
                } else {
                    eventsImage.setVisibility(View.GONE);
                }
                activityScrollview.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getEventDetails!=null)getEventDetails.cancel(true);
    }
}
