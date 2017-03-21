package com.tech.thrithvam.churchapp;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.clans.fab.FloatingActionMenu;

public class MyChurch extends AppCompatActivity {
    DatabaseHandler db;
    FloatingActionMenu floatingActionMenu;
    String churchID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_church);
        db=DatabaseHandler.getInstance(this);
        churchID=db.GetMyChurch("ChurchID");
        floatingActionMenu=(FloatingActionMenu)findViewById(R.id.material_design_android_floating_action_menu);
        if(db.GetMyChurch("ChurchID")==null){
            Intent intent=new Intent(MyChurch.this,MyChurchWizard.class);
            startActivity(intent);
            finish();
        }
else {
        Typeface typeSegoe = Typeface.createFromAsset(getAssets(),"fonts/segoeui.ttf");
        Typeface typeBLKCHCRY = Typeface.createFromAsset(getAssets(),"fonts/blackchancery.ttf");
        Typeface typeQuicksand = Typeface.createFromAsset(getAssets(),"fonts/quicksandbold.otf");

        //Fonts---------------
        TextView activityHead=(TextView)findViewById(R.id.activity_head);
        TextView churchName=(TextView)findViewById(R.id.church_name);
        TextView churchDetail1=(TextView)findViewById(R.id.church_detail1);
        TextView churchDetail2=(TextView)findViewById(R.id.church_detail2);

        activityHead.setTypeface(typeQuicksand);
        churchName.setTypeface(typeBLKCHCRY);
        churchDetail1.setTypeface(typeSegoe);
        churchDetail2.setTypeface(typeSegoe);

        TextView change_my_church=(TextView)findViewById(R.id.change_mychurch);
        change_my_church.setTypeface(typeSegoe);

        TextView eduForum=(TextView)findViewById(R.id.text_edu_forum);
        TextView events=(TextView)findViewById(R.id.events);
        TextView familyUnits=(TextView)findViewById(R.id.family_units);
        TextView gallery=(TextView)findViewById(R.id.gallery);
        TextView timings=(TextView)findViewById(R.id.timings);
        TextView about=(TextView)findViewById(R.id.about);

        eduForum.setTypeface(typeSegoe);
        events.setTypeface(typeSegoe);
        familyUnits.setTypeface(typeSegoe);
        gallery.setTypeface(typeSegoe);
        timings.setTypeface(typeSegoe);
        about.setTypeface(typeSegoe);

        //Eduforum----------
        if(db.GetMyChurch("Denomination").equals("LC"))
        {
            eduForum.setText(R.string.navadarsan);
            ImageView eduForumIcon=(ImageView)findViewById(R.id.image_edu_forum);
            Glide.with(MyChurch.this)
                    .load(R.drawable.logo_navdarshan)
                    .dontTransform()
                    .into(eduForumIcon)
            ;
            eduForumIcon.clearColorFilter();
        }

        //Church image--------------
        final ImageView myChurchImage=(ImageView)findViewById(R.id.my_church_image);
        String imageURLString=db.GetMyChurch("Image");
        if(imageURLString!=null && !imageURLString.equals("null")){
        Glide.with(MyChurch.this)
                .load(getResources().getString(R.string.url) +imageURLString.substring((imageURLString).indexOf("img")))
                .thumbnail(0.1f)
                .dontTransform()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Glide.with(MyChurch.this)
                                .load(R.drawable.my_church_sample)
                                .centerCrop()
                                .into(myChurchImage)
                        ;
                        return true;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(myChurchImage)
        ;
        }

        //Data on labels------
        churchName.setText(db.GetMyChurch("ChurchName"));
        churchDetail1.setText(db.GetMyChurch("Town"));
        churchDetail2.setText(db.GetMyChurch("Address"));
    }
    }

    public void family_units_click(View view){
        Intent intent=new Intent(MyChurch.this,FamilyUnits.class);
        intent.putExtra("ChurchID",churchID);
        startActivity(intent);
    }
    public void notice_click (View view){
        Intent intent=new Intent(MyChurch.this,Notices.class);
        intent.putExtra("churchID",churchID);
        startActivity(intent);
    }
    public void gallery_click (View view){
        Intent intent=new Intent(MyChurch.this,Gallery.class);
        intent.putExtra("ChurchID",churchID);
        startActivity(intent);
    }
    public void events_click (View view){
        Intent intent=new Intent(MyChurch.this,Events.class);
        intent.putExtra("ChurchID",churchID);
        intent.putExtra("ChurchName",db.GetMyChurch("ChurchName"));
        startActivity(intent);
    }
    public void edu_forum_click (View view){
        Intent intent=new Intent(MyChurch.this,EducationForumRegistration.class);
        intent.putExtra("ChurchID",churchID);
        startActivity(intent);
    }
    public void change_my_church_click (View view){
        Intent intent=new Intent(MyChurch.this,MyChurchWizard.class);
        startActivity(intent);
        finish();
    }
    public void timings_click (View view){
        Intent intent=new Intent(MyChurch.this,MyChurchDetails.class);
        intent.putExtra("from","timings");
        startActivity(intent);
    }
    public void about_click (View view){
        Intent intent=new Intent(MyChurch.this,ChurchDetails.class);
        intent.putExtra("churchID",churchID);
        intent.putExtra("churchname", db.GetMyChurch("ChurchName"));
     //   churchDetail1.setText(db.GetMyChurch("Town"));
     //   churchDetail2.setText(db.GetMyChurch("Address"));
     //   intent.putExtra("town",churchItems.get(position)[2]);
     //   intent.putExtra("address",churchItems.get(position)[4]);
        startActivity(intent);
    }

    public void pious_org_click (View view){
        Intent intent=new Intent(MyChurch.this,PiousOrgs.class);
        intent.putExtra("ChurchID",churchID);
        floatingActionMenu.close(true);
        startActivity(intent);
    }
    public void institutions_click (View view){
        Intent intent=new Intent(MyChurch.this,Institutions.class);
        intent.putExtra("ChurchID",churchID);
        floatingActionMenu.close(true);
        startActivity(intent);
    }
    public void novenas_click (View view){
        Intent intent=new Intent(MyChurch.this,NovenaDetailsList.class);
        intent.putExtra("churchID",churchID);
        intent.putExtra("churchName","Novenas at "+db.GetMyChurch("ChurchName"));
        intent.putExtra("from","church");
        floatingActionMenu.close(true);
        startActivity(intent);
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
}
