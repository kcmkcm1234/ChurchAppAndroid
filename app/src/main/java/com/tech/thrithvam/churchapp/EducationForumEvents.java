package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionMenu;
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

public class EducationForumEvents extends AppCompatActivity {
    Typeface typeSegoe;
    DatabaseHandler db;
    String ChurchID;
    Typeface typeQuicksand;
    TextView eduEventsHead;
    AsyncTask getEvents=null;
    Boolean latestEvents;
    FloatingActionMenu floatingActionMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_forum_events);
        db=DatabaseHandler.getInstance(this);
        if(db.GetMyChurch("eduForumMemberRegistrationID")==null){
            Intent intent=new Intent(EducationForumEvents.this,MyChurchWizard.class);
            startActivity(intent);
            finish();
        }
        ChurchID=db.GetMyChurch("ChurchID");

        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        eduEventsHead =(TextView)findViewById(R.id.activity_head);
        eduEventsHead.setTypeface(typeQuicksand);
        floatingActionMenu=(FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
        if (isOnline()) {
            latestEvents=true;
            getEvents=new GetEducationForumEvents().execute();
        } else {
            Toast.makeText(EducationForumEvents.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
            Intent noItemsIntent=new Intent(EducationForumEvents.this,NothingToDisplay.class);
            noItemsIntent.putExtra("msg",getResources().getString(R.string.network_off_alert));
            noItemsIntent.putExtra("activityHead","Events");
            startActivity(noItemsIntent);
            finish();
        }
        //Eduforum----------
        if(db.GetMyChurch("Denomination").equals("LC"))
        {
            eduEventsHead.setText(R.string.navadarsan);
            ImageView eduForumIcon=(ImageView)findViewById(R.id.edu_forum_image);
            Glide.with(EducationForumEvents.this)
                    .load(R.drawable.logo_navdarshan)
                    .dontTransform()
                    .into(eduForumIcon)
            ;
            eduForumIcon.clearColorFilter();
        }
    }
    //-----------------------------Async Tasks----------------------------------------
    private class GetEducationForumEvents extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        ArrayList<String[]> eduForumEventsListItems =new ArrayList<>();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetAllEduForumEventsbyChurchID";
            HttpURLConnection c = null;
            try {
                postData =  "{\"churchID\":\"" + ChurchID +"\",\"isOld\":\"" + !latestEvents +"\",\"registrationID\":\"" + db.GetMyChurch("eduForumMemberRegistrationID") + "\"}";
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
                    String[] data=new String[6];
                    data[0]=jsonObject.optString("StartDate").replace("/Date(", "").replace(")/", "");
                    data[1]=jsonObject.optString("EventName");
                    data[2]=jsonObject.optString("Description");
                    data[3]=jsonObject.optString("URL");
                    data[4]=jsonObject.optString("ResponseCode");
                    data[5]=jsonObject.optString("ID");
                    eduForumEventsListItems.add(data);
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
                if(latestEvents){
                    Toast.makeText(EducationForumEvents.this,R.string.no_items_to_display,Toast.LENGTH_SHORT).show();
                    floatingActionMenu.open(true);
                }
                else {
                    Intent noItemsIntent=new Intent(EducationForumEvents.this,NothingToDisplay.class);
                    noItemsIntent.putExtra("msg",msg);
                    noItemsIntent.putExtra("activityHead","Education Forum Events");
                    startActivity(noItemsIntent);
                    finish();
                }
            }
            else {
                CustomAdapter adapter=new CustomAdapter(EducationForumEvents.this, eduForumEventsListItems,"EduForumEvents");
                ListView eventsList=(ListView) findViewById(R.id.edu_events_list);
                eventsList.setAdapter(adapter);
                eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(EducationForumEvents.this,EducationForumEventsDetails.class);
                        intent.putExtra("StartDate", eduForumEventsListItems.get(position)[0]);
                        intent.putExtra("EventName", eduForumEventsListItems.get(position)[1]);
                        intent.putExtra("Description", eduForumEventsListItems.get(position)[2]);
                        intent.putExtra("URL", eduForumEventsListItems.get(position)[3]);
                        intent.putExtra("StartDateInMillis", eduForumEventsListItems.get(position)[0]);
                        intent.putExtra("ChurchName",db.GetMyChurch("ChurchName"));
                        intent.putExtra("ResponseCode",eduForumEventsListItems.get(position)[4]);
                        intent.putExtra("EventID",eduForumEventsListItems.get(position)[5]);
                        intent.putExtra("isOld",!latestEvents);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }
    }

    public void old_events(View view){
        latestEvents=false;
        if (isOnline()) {
            getEvents=new GetEducationForumEvents().execute();
            floatingActionMenu.close(true);
            floatingActionMenu.setVisibility(View.GONE);
            eduEventsHead.setText(R.string.old_events);
        } else {
            Toast.makeText(EducationForumEvents.this, R.string.network_off_alert, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (floatingActionMenu.isOpened()) {
                Rect outRect = new Rect();
                floatingActionMenu.getGlobalVisibleRect(outRect);
                if(!outRect.contains((int)event.getRawX(), (int)event.getRawY()))
                    floatingActionMenu.close(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }
    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
