package com.tech.thrithvam.churchapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Notifications extends AppCompatActivity {
    Typeface typeQuicksand;
    TextView activity_head;
    DatabaseHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");
        activity_head =(TextView)findViewById(R.id.activity_head);
        activity_head.setTypeface(typeQuicksand);
         db=DatabaseHandler.getInstance(this);
        final ArrayList<String[]> notifications=db.GetNotifications();
        if(notifications.size()==0)
        {
            Intent noItemsIntent=new Intent(Notifications.this,NothingToDisplay.class);
            noItemsIntent.putExtra("msg",R.string.no_items_to_display);
            noItemsIntent.putExtra("activityHead","Notifications");
            startActivity(noItemsIntent);
            finish();
        }
        CustomAdapter adapter=new CustomAdapter(Notifications.this, notifications,"Notifications");
        ListView notificationList=(ListView) findViewById(R.id.notification_list);
        notificationList.setAdapter(adapter);
        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(notifications.get(position)[3].equals("EduForum")){
                    Intent intent = new Intent(Notifications.this, EducationForumEvents.class);
                    startActivity(intent);
                }
                try {
                    Toast.makeText(Notifications.this,notifications.get(position)[3]+"\n"+notifications.get(position)[4],Toast.LENGTH_LONG).show();
                }
                catch (Exception e){

                }
            }
        });
    }
    public void flushNotifications(View view){
        new AlertDialog.Builder(Notifications.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                .setMessage(R.string.delete_notifications)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       db.flushNotifications();
                        finish();
                    }
                }).setNegativeButton(R.string.cancel, null).show();
    }
}
