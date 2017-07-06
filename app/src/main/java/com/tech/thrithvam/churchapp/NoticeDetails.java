package com.tech.thrithvam.churchapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

public class NoticeDetails extends AppCompatActivity {
    Bundle extras;
    String URL;
    TextView noticeHead, noticeContent;
    ImageView noticeImage;
    Typeface typeQuicksand,typeSegoe;
    AsyncTask getNotice=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);
        extras = getIntent().getExtras();


        typeQuicksand = Typeface.createFromAsset(getAssets(), "fonts/quicksandbold.otf");
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        noticeHead = (TextView) findViewById(R.id.activity_notice_head);
        noticeHead.setTypeface(typeQuicksand);

        noticeContent = (TextView) findViewById(R.id.notice_details);
        noticeContent.setTypeface(typeSegoe);
        noticeImage = (ImageView) findViewById(R.id.notice_img);

        if(getIntent().hasExtra("NoticeID")){//open specific notice (coming from notification)
            getNotice=new GetNotice().execute();
        }
        else {
            if (getIntent().hasExtra("NoticeName")) {
                noticeHead.setText(extras.getString("NoticeName"));
            }
            if (getIntent().hasExtra("Description")) {
                noticeContent.setText(extras.getString("Description"));
            }
            URL = extras.getString("URL");
            //image loading using url
            if (!URL.equals("null")) {
                Glide.with(NoticeDetails.this)
                        .load(getResources().getString(R.string.url) + URL.substring((URL).indexOf("img")))
                        .dontTransform()
                        .thumbnail(0.1f)
                        .crossFade()
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                noticeImage.setVisibility(View.GONE);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(noticeImage)
                ;
            } else {
                noticeImage.setVisibility(View.GONE);
            }
        }
    }
    //---------------------------------Async Tasks--------------------------------------
    public class GetNotice extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        String noticeName="",description="",imageURL="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator.setVisibility(View.VISIBLE);
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/GetNoticesByNoticeID";
            HttpURLConnection c = null;
            try {
                postData =  "{\"NoticeID\":\"" + getIntent().getExtras().getString("NoticeID")+ "\"}";
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
                    noticeName=jsonObject.optString("NoticeName");
                    description=jsonObject.optString("Description");
                    imageURL=jsonObject.optString("URL");
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
                Intent intent=new Intent(NoticeDetails.this,Notices.class);
                DatabaseHandler db=DatabaseHandler.getInstance(NoticeDetails.this);
                intent.putExtra("churchID",db.GetMyChurch("ChurchID"));
                startActivity(intent);
                finish();
            }
            else {
                if (!noticeName.equals("null")) {
                    noticeHead.setText(noticeName);
                }
                else {
                    noticeHead.setText("");
                }
                if (!description.equals("null")) {
                    noticeContent.setText(description);
                }
                else {
                    noticeContent.setText("");
                }
                if(!imageURL.equals("null")){
                    URL=imageURL;
                    Glide.with(NoticeDetails.this)
                            .load(getResources().getString(R.string.url) + URL.substring((URL).indexOf("img")))
                            .dontTransform()
                            .thumbnail(0.1f)
                            .crossFade()
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    noticeImage.setVisibility(View.GONE);
                                    return true;
                                }

                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(noticeImage)
                    ;
                } else {
                    noticeImage.setVisibility(View.GONE);
                }
            }
        }
    }

    public void  imageclick (View view){
        Intent intent=new Intent(NoticeDetails.this,ImageViewerActivity.class);
        intent.putExtra("URL",URL);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getNotice!=null)getNotice.cancel(true);
    }
}
