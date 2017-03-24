package com.tech.thrithvam.churchapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

public class EducationForumRegistration extends AppCompatActivity {
    DatabaseHandler db;
    String churchID;
    Typeface typeSegoe,typeQuicksand;
    TextView aboutEduForum;
    static int PARENT=1;
    static int STUDENT=2;
    int registerAs=0;
    ScrollView aboutEduForumScrollView;
    LinearLayout otpVerificationCard;
    EditText contactMobileInput;
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
        typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        aboutEduForumScrollView=(ScrollView)findViewById(R.id.about_edu_forum_scrollview);
        otpVerificationCard=(LinearLayout)findViewById(R.id.otp_verification_card);
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
    private class GetEduForumAbout extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading);
        String eduForumAbout;
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
                TextView registerP=(TextView)findViewById(R.id.register_p);
                TextView registerS=(TextView)findViewById(R.id.register_s);
                registerP.setTypeface(typeSegoe);registerS.setTypeface(typeSegoe);
                registerP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerAs=PARENT;
                        startRegistration();
                    }
                });
                registerS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        registerAs=STUDENT;
                        startRegistration();
                    }
                });
            }
        }
    }
    void startRegistration(){
        aboutEduForumScrollView.setVisibility(View.GONE);
        otpVerificationCard.setVisibility(View.VISIBLE);
        contactMobileInput=(EditText)findViewById(R.id.contact_mobile_input);
        contactMobileInput.requestFocus();
        final TextView verifyMobile=(TextView)findViewById(R.id.verify_mobile);
        verifyMobile.setTypeface(typeSegoe);
        verifyMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(contactMobileInput.getText().toString().trim().length()==10)){
                    contactMobileInput.setError(getResources().getString(R.string.give_valid_info));
                }
                else {
                    new GetOTP().execute();
                    verifyMobile.setVisibility(View.GONE);
                }
            }
        });
    }
    private class GetOTP extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        AVLoadingIndicatorView loadingIndicator2 =(AVLoadingIndicatorView)findViewById(R.id.itemsLoading2);
        String mobileNumberString;
        String OTP, memberRegistrationID;
        Boolean isMember;
        boolean pass=false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingIndicator2.setVisibility(View.VISIBLE);
            mobileNumberString=contactMobileInput.getText().toString().trim();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/EduForumOTP";
            HttpURLConnection c = null;
            try {
                postData =  "{\"mobile\":\""+mobileNumberString+"\",\"ChurchID\":\"" + churchID+ "\"}";
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
                    OTP=jsonObject.optString("OTP");
                    isMember=jsonObject.optBoolean("IsMember");
                    memberRegistrationID =jsonObject.optString("RegistrationID");
                }
            } catch (Exception ex) {
                msg=ex.getMessage();
            }}
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            loadingIndicator2.setVisibility(View.GONE);
            if(!pass) {
                Toast.makeText(EducationForumRegistration.this,R.string.tyr_again,Toast.LENGTH_LONG).show();
                TextView verifyMobile=(TextView)findViewById(R.id.verify_mobile);
                verifyMobile.setVisibility(View.VISIBLE);
            }
            else {
                UserVerification();
            }
        }
        void UserVerification(){
            Toast.makeText(EducationForumRegistration.this,OTP,Toast.LENGTH_LONG).show();
            AlertDialog.Builder alert = new AlertDialog.Builder(EducationForumRegistration.this);
            alert.setTitle(R.string.enter_otp);
            final EditText otpInput=new EditText(EducationForumRegistration.this);
            otpInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            otpInput.setGravity(Gravity.CENTER_HORIZONTAL);
            alert.setView(otpInput);
            alert.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (otpInput.getText().toString().equals("")) {
                        UserVerification();
                    }
                    else if(otpInput.getText().toString().equals(OTP)){
                        if(isMember){
                            Toast.makeText(EducationForumRegistration.this,"already a member",Toast.LENGTH_LONG).show();
                            db.InsertEduForumMemberID(memberRegistrationID);
                            Intent intentUser = new Intent(EducationForumRegistration.this, EducationForumEvents.class);
                            finish();
                            startActivity(intentUser);
                        }
                        else {
                            ProceedRegistration();
                        }
                    }
                    else {
                        Toast.makeText(EducationForumRegistration.this,"Not Matching",Toast.LENGTH_LONG).show();
                        UserVerification();
                    }
                }
            });
            alert.setCancelable(false);
            alert.show();
        }

    }
    void ProceedRegistration(){
        otpVerificationCard.setVisibility(View.GONE);
        if(registerAs==PARENT){
            final ScrollView scrollView=(ScrollView)findViewById(R.id.registration_parent_edu_forum_scrollview);
            scrollView.setVisibility(View.VISIBLE);
            EditText contactNumber=(EditText)findViewById(R.id.contact_number);
            contactNumber.setText(contactMobileInput.getText().toString());

            //Child
            final LinearLayout childDetailsPlaceholder=(LinearLayout)findViewById(R.id.child_details);
            final ArrayList<LinearLayout> childDetails=new ArrayList<>();
            final TextView submitButton=(TextView)findViewById(R.id.submit_p);
            //Adding child
            final TextView addChild=(TextView)findViewById(R.id.add_child);
            addChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final LinearLayout child=(LinearLayout) getLayoutInflater().inflate(R.layout.item_child_input, null);
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,5,0,5);
                    child.setLayoutParams(params);
                    (child.findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            childDetailsPlaceholder.removeView(child);
                            childDetails.remove(child);
                            if(childDetails.size()==0){
                                submitButton.setVisibility(View.GONE);
                                addChild.setText(R.string.add_child);
                            }
                            else {
                                submitButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    (child.findViewById(R.id.name_of_child)).requestFocus();
                    childDetails.add(child);
                    childDetailsPlaceholder.addView(child);
                    submitButton.setVisibility(View.VISIBLE);
                    addChild.setText(R.string.add_another_child);
                }
            });
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String childJson="[";
                    for(int i=0;i<childDetails.size();i++){
                        String name,_class,school,dob;
                        name=((TextView)childDetails.get(i).findViewById(R.id.name_of_child)).getText().toString().trim();
                        _class= ((TextView)childDetails.get(i).findViewById(R.id.child_class)).getText().toString().trim();
                        school=((TextView)childDetails.get(i).findViewById(R.id.school)).getText().toString().trim();
                        dob=((TextView)childDetails.get(i).findViewById(R.id.dob)).getText().toString().trim();
                        if(name.length()==0){
                            ((TextView)childDetails.get(i).findViewById(R.id.name_of_child)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else if(_class.length()==0){
                            ((TextView)childDetails.get(i).findViewById(R.id.child_class)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else if(school.length()==0){
                            ((TextView)childDetails.get(i).findViewById(R.id.school)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else if(dob.length()==0){
                            ((TextView)childDetails.get(i).findViewById(R.id.dob)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else {
                            childJson+="{\"Name\":\""+name+"\",\"Class\":\""+_class+"\",\"School\":\""+school+"\",\"DOB\":\""+dob+"\"},";
                        }
                    }
                    childJson=childJson.substring(0,childJson.lastIndexOf(","))+"]";
                    //Parent details
                    String parentName,familyUnit,email;
                    parentName=((TextView)findViewById(R.id.name_of_parent)).getText().toString().trim();
                    familyUnit=((TextView)findViewById(R.id.family_unit)).getText().toString().trim();
                    email=((TextView)findViewById(R.id.email)).getText().toString().trim();
                    if(parentName.length()==0){
                        ((TextView)findViewById(R.id.name_of_parent)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else if(familyUnit.length()==0){
                        ((TextView)findViewById(R.id.family_unit)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else {
                        new RegisterMember(parentName,familyUnit,email,childJson).execute();
                    }
                }
            });
        }
        else if (registerAs==STUDENT){
            final ScrollView scrollView=(ScrollView)findViewById(R.id.registration_student_edu_forum_scrollview);
            scrollView.setVisibility(View.VISIBLE);
            EditText contactNumber=(EditText)findViewById(R.id.contact_number_s);
            contactNumber.setText(contactMobileInput.getText().toString());
            TextView submitButton=(TextView)findViewById(R.id.submit_s);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String studentName,_class,school,dob,email;
                    studentName=((TextView)findViewById(R.id.name_of_student)).getText().toString().trim();
                    _class= ((TextView)findViewById(R.id.child_class)).getText().toString().trim();
                    school=((TextView)findViewById(R.id.school)).getText().toString().trim();
                    dob=((TextView)findViewById(R.id.dob)).getText().toString().trim();
                    if(studentName.length()==0){
                        ((TextView)findViewById(R.id.name_of_student)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else if(_class.length()==0){
                        ((TextView)findViewById(R.id.child_class)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else if(school.length()==0){
                        ((TextView)findViewById(R.id.school)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else if(dob.length()==0){
                        ((TextView)findViewById(R.id.dob)).setError(getResources().getString(R.string.give_valid_info));
                        return;
                    }
                    else {
                        email=((TextView)findViewById(R.id.email_s)).getText().toString().trim();
                        String childJson="[{\"Name\":\""+studentName+"\",\"Class\":\""+_class+"\",\"School\":\""+school+"\",\"DOB\":\""+dob+"\"}]";
                        //Parent details
                        String parentName,familyUnit;
                        parentName=((TextView)findViewById(R.id.name_of_parent_s)).getText().toString().trim();
                        familyUnit=((TextView)findViewById(R.id.family_unit_s)).getText().toString().trim();
                        if(parentName.length()==0){
                            ((TextView)findViewById(R.id.name_of_parent_s)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else if(familyUnit.length()==0){
                            ((TextView)findViewById(R.id.family_unit_s)).setError(getResources().getString(R.string.give_valid_info));
                            return;
                        }
                        else {
                            new RegisterMember(parentName,familyUnit,email,childJson).execute();
                        }
                    }
                }
            });
        }
    }
    private class RegisterMember extends AsyncTask<Void , Void, Void> {
        int status;StringBuilder sb;
        String strJson, postData;
        JSONArray jsonArray;
        String msg;
        boolean pass=false;
        String parentName,familyUnit,email,childJson,mobileNumberString, registrationID;
        ProgressDialog pDialog=new ProgressDialog(EducationForumRegistration.this);
        RegisterMember( String parentName,String familyUnit,String email,String childJson){
            this.parentName=parentName;
            this.familyUnit=familyUnit;
            this.email=email;
            this.childJson=childJson;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(getResources().getString(R.string.wait));
            pDialog.show();
            mobileNumberString=contactMobileInput.getText().toString().trim();
            //----------encrypting ---------------------------
            // usernameString=cryptography.Encrypt(usernameString);
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            String url =getResources().getString(R.string.url) + "WebServices/WebService.asmx/RegisterEduForumMember";
            HttpURLConnection c = null;
            try {
                postData =  "{\"churchID\":\""+churchID+"\",\"parentName\":\"" + parentName+"\",\"familyUnit\":\"" + familyUnit+"\",\"contactNo\":\"" + mobileNumberString+"\",\"email\":\"" + email+"\",\"childJson\":" + childJson+ "}";
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
                    registrationID =jsonObject.optString("RegistrationID");
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
                Intent noItemsIntent=new Intent(EducationForumRegistration.this,NothingToDisplay.class);
                noItemsIntent.putExtra("msg",msg);
                noItemsIntent.putExtra("activityHead","Education Forum");
                startActivity(noItemsIntent);
                //finish();
            }
            else {
                db.InsertEduForumMemberID(registrationID);
                Intent intentUser = new Intent(EducationForumRegistration.this, EducationForumEvents.class);
                finish();
                startActivity(intentUser);
            }
        }
    }
}
