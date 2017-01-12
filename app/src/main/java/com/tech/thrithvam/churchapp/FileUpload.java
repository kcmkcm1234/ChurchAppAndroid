package com.tech.thrithvam.churchapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class FileUpload implements Runnable{
    URL connectURL;
    Context context;
    int responseCode;
    String responseString;
    String ChurchName,Address,Place,UserName,UserContact,Remarks,Email;
    String fileName="";
    FileInputStream fileInputStream = null;

    FileUpload(Context context, String urlString, FileInputStream fStream, String fileName,
               String ChurchName,String Address,String Place,String UserName,String UserContact,String Remarks,String Email){
        try{
            this.context=context;
            connectURL = new URL(urlString);
            fileInputStream = fStream;
            this.fileName=fileName;
            this.ChurchName=ChurchName;
            this.Address=Address;
            this.Place=Place;
            this.UserName=UserName;
            this.UserContact=UserContact;
            this.Remarks=Remarks;
            this.Email=Email;
        }catch(Exception ex){
              Log.i("FileUpload", "URL Malformatted");
        }
    }

    void Sending(){
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        try
        {
            //         Log.e(Tag,"Starting Http File Sending to URL");

            // Open a HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();

            // Allow Inputs
            conn.setDoInput(true);

            // Allow Outputs
            conn.setDoOutput(true);

            // Don't use a cached copy.
            conn.setUseCaches(false);

            // Use a post method.
            conn.setRequestMethod("POST");

            conn.setRequestProperty("Connection", "Keep-Alive");

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ChurchName\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(ChurchName);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"Address\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Address);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"Place\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Place);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"UserName\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(UserName);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"UserContact\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(UserContact);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"Remarks\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Remarks);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"Email\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Email);
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + fileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            //    Log.e(Tag,"Headers are written");

            // create a buffer of maximum size
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];

            // read file and write it into form...
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();

            dos.flush();

            Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseCode()));

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;

            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
            String s=b.toString();
            Log.i("Response",s);

            //extracting message
            int start=s.indexOf("Message");
            int end=s.lastIndexOf("<");
            responseString=s.substring(start,end);
            responseCode =conn.getResponseCode();
            dos.close();
        } catch (IOException ioe)
        {
            // Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        }
    }

    @Override
    public void run() {

    }


    void UploadFileFn(){
        new UploadFile().execute();
    }

    public class UploadFile extends AsyncTask<Void , Void, Void> {
        ProgressDialog pDialog=new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage(context.getResources().getString(R.string.wait));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Sending();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle("")
                    .setMessage(responseString)
                    .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(responseCode ==200){
                                ((Activity)context).finish();
                            }
                        }
                    }).setCancelable(false).show();
        }
    }
}