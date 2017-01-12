package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RequestChurch extends AppCompatActivity {

    ImageView newImage;
    TextView fileName;
    File imageFile;
    final int PHOTO_FROM_GALLERY=444;
    Bundle extras;
    EditText churchName,address,place,userName,userContact,remarks,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_church);
        extras=getIntent().getExtras();

        newImage=(ImageView)findViewById(R.id.new_image);
        fileName=(TextView) findViewById(R.id.file_name);
       newImage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
               intent.setType("image/*");
               startActivityForResult(Intent.createChooser(intent, "Select Church Image"), PHOTO_FROM_GALLERY);
           }
       });

        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        Typeface typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        activityHead.setTypeface(typeQuicksand);
        churchName=(EditText)findViewById(R.id.church_name);
        address=(EditText)findViewById(R.id.address);
        place=(EditText)findViewById(R.id.place);
        userName=(EditText)findViewById(R.id.user_name);
        userContact=(EditText)findViewById(R.id.user_contact);
        remarks=(EditText)findViewById(R.id.remarks);
        email=(EditText)findViewById(R.id.email);
        churchName.setTypeface(typeSegoe);
        address.setTypeface(typeSegoe);
        place.setTypeface(typeSegoe);
        userName.setTypeface(typeSegoe);
        userContact.setTypeface(typeSegoe);
        remarks.setTypeface(typeSegoe);
        email.setTypeface(typeSegoe);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;
        if (resultCode == RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            String[] projection = { MediaStore.MediaColumns.DATA };
            CursorLoader cursorLoader = new CursorLoader(this,selectedImageUri, projection, null, null,null);
            Cursor cursor =cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            bitmap = BitmapFactory.decodeFile(selectedImagePath);
            newImage.setImageBitmap(bitmap);
            imageFile =new File(cursor.getString(column_index));
            fileName.setText(imageFile.getName());
            cursor.close();
        }
    }


    public void Upload(View view){
        if(isOnline()) {
            if(fileName.getText().toString().equals
                    (getResources().getString(R.string.upload_church_image))){ //checking whether any image is selected
                Toast.makeText(RequestChurch.this,"Please upload a church image",Toast.LENGTH_LONG).show();
            }
            else if(churchName.getText().toString().equals("")){
                churchName.setError("Please enter church name");
            }
            else if(address.getText().toString().equals("")){
                address.setError("Please enter church address");
            }
            else if(place.getText().toString().equals("")){
                place.setError("Please enter place of church");
            }
            else if(userName.getText().toString().equals("")){
                userName.setError("Please enter your name");
            }
            else if(userContact.getText().toString().equals("")){
                userContact.setError("Please enter contact number");
            }
            else if(email.getText().toString().equals("")){
                email.setError("Please enter your email address");
            }
            else if(remarks.getText().toString().equals("")){
                remarks.setError("Please enter something about your request");
            }
            else {
                FileInputStream fStream= null;
                try {
                    fStream = new FileInputStream(imageFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileUpload hfu= new FileUpload(RequestChurch.this,getResources().getString(R.string.url) +"WebServices/WebService.asmx/RequestChurch",fStream,
                        fileName.getText().toString(),
                        churchName.getText().toString(),
                        address.getText().toString(),
                        place.getText().toString(),
                        userName.getText().toString(),
                        userContact.getText().toString(),
                        remarks.getText().toString(),
                        email.getText().toString());
                hfu.UploadFileFn();  //calling within app
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
