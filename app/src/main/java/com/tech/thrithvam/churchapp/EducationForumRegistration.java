package com.tech.thrithvam.churchapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EducationForumRegistration extends AppCompatActivity {
    DatabaseHandler db;
    String churchID;
    TextView aboutEduForum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_forum_registration);
        db=DatabaseHandler.getInstance(this);
        churchID=db.GetMyChurch("ChurchID");
        if(db.GetMyChurch("ChurchID")==null){
            Intent intent=new Intent(EducationForumRegistration.this,MyChurchWizard.class);
            startActivity(intent);
            finish();
        }
        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        Typeface typeBLKCHCRY = Typeface.createFromAsset(getAssets(),"fonts/blackchancery.ttf");
        Typeface typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        //Fonts---------------
        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        aboutEduForum=(TextView)findViewById(R.id.about);
        activityHead.setTypeface(typeQuicksand);
        aboutEduForum.setTypeface(typeSegoe);
        //Education forum----------
        if(db.GetMyChurch("Denomination").equals("LC"))
        {
            activityHead.setText(R.string.navadarsan);
            ImageView eduForumIcon=(ImageView)findViewById(R.id.edu_forum_image);
            Glide.with(EducationForumRegistration.this)
                    .load(R.drawable.logo_navdarshan)
                    .dontTransform()
                    .into(eduForumIcon)
            ;
        }
        new GetEduForumAbout().execute();
    }
    public class GetEduForumAbout extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        String eduForumAbout;
        ScrollView aboutEduForumScrollView=(ScrollView)findViewById(R.id.about_edu_forum_scrollview);
        boolean pass=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            aboutEduForumScrollView.setVisibility(View.GONE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetEduForumAbout";
            HttpURLConnection c = null;
            try {
                postData =  "{\"ChurchID\":\"" + churchID+ "\"}";
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
                    eduForumAbout=jsonObject.optString("EduForumAbout");
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
                Intent noItemsIntent=new Intent(EducationForumRegistration.this,NothingToDisplay.class);
                noItemsIntent.putExtra("msg",msg);
                noItemsIntent.putExtra("activityHead","Education Forum");
                startActivity(noItemsIntent);
                finish();
            }
            else {
                aboutEduForumScrollView.setVisibility(View.VISIBLE);
                aboutEduForum.setText(eduForumAbout);
            }
        }
    }
}
