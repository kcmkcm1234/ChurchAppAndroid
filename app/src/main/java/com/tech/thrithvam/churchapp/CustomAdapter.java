package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomAdapter extends BaseAdapter {
    private Context adapterContext;
    private static LayoutInflater inflater=null;
    private ArrayList<String[]> objects;
    private String calledFrom;
    private Typeface typeSegoe;
    private Typeface typeBLKCHCRY;
    private Typeface typeQuicksand;
    private SimpleDateFormat formatted;
    private Calendar cal;
    private Animation animation;
    private int lastPosition;
    public CustomAdapter(Context context, ArrayList<String[]> objects, String calledFrom) {
        // super(context, textViewResourceId, objects);
        adapterContext=context;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.objects=objects;
        this.calledFrom=calledFrom;
        typeSegoe = Typeface.createFromAsset(context.getAssets(),"fonts/segoeui.ttf");
        typeBLKCHCRY = Typeface.createFromAsset(context.getAssets(),"fonts/blackchancery.ttf");
        typeQuicksand = Typeface.createFromAsset(context.getAssets(),"fonts/quicksandbold.otf");
        formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        cal= Calendar.getInstance();
        lastPosition=-1;
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder
    {
            //Searching Church---------------------
        TextView churchName,address,town;
        ImageView churchImage;
            //Novenas------------------------------
        TextView patronName,patronDescription;
        ImageView patronImg1,patronImg2;
            //Novena Church list-------------------
        TextView novenaCaption,novenaChurchName,townName,novenaDescription,novenaDate,dayAndTime;
        ImageView novenaImg;
        RelativeLayout viewMap;
            //Nearby Church List-------------------
        TextView nearbyChurchName,nearbyChurchAddress,distance;
        ImageView nearChurchImg,nearbyViewMap;
            //Pious Organisation-------------------
        TextView orgName, orgPatronName;
        ImageView orgPatronImage;
            //Institutions-------------------------
        TextView institutionName, institutionAddress;
        ImageView institutionImage;
            //Events-------------------------------
        TextView eventsHead, eventsDate;
        ImageView eventImage;
            //Towns--------------------------------
        TextView townHead;
            //Priest-------------------------------
        TextView pName, pDOB, pAbout, pDateOrdination, pDesign, pAddress, pEmail, pMob, pParish, pStatus, pBaptism;
        ImageView pImage;
            //Events-------------------------------
        TextView noticeHead, noticeType;
        ImageView noticeImage;
            //Gallery-----------------------------
        TextView albumTitle,itemCount;
        ImageView galleryAlbum,galleryItem;
            //Family------------------------------
        TextView familyUnitHead;
            //FamilyDetails------------------------
        TextView familyHead, familyName;
            //Family unit Executives---------------
        TextView personName, personMob, personPosition;
        ImageView personImg;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        final int fPos=position;
        switch (calledFrom) {
            //--------------------------for home screen items------------------
            case "ChurchTownSearchResults":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_church, null);
                    holder.churchName = (TextView) convertView.findViewById(R.id.church_name);
                    holder.address=(TextView)convertView.findViewById(R.id.address);
                    holder.town=(TextView)convertView.findViewById(R.id.town);
                    holder.churchImage=(ImageView)convertView.findViewById(R.id.church_image);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.churchName.setText(objects.get(position)[1]);
                if(!objects.get(position)[2].equals("null")) {
                    holder.town.setText(objects.get(position)[2]);
                    holder.town.setVisibility(View.VISIBLE);
                }
                else holder.town.setVisibility(View.INVISIBLE);

                if(!objects.get(position)[3].equals("null")){
                    holder.churchImage.setPadding(0,0,0,0);
                    holder.churchImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.churchImage);
                }
                else{
                    holder.churchImage.setPadding(15,15,15,15);
                    holder.churchImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .into(holder.churchImage);
                }
                if(!objects.get(position)[4].equals("null")){
                    holder.address.setText(objects.get(position)[4]);
                    holder.address.setVisibility(View.VISIBLE);
                }
                else holder.address.setVisibility(View.INVISIBLE);
                holder.churchName.setTypeface(typeQuicksand);
                holder.town.setTypeface(typeSegoe);
                holder.address.setTypeface(typeSegoe);
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //------------------------------Novenas----------------------------------------------
            case "Novenas":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_patron, null);
                    holder.patronName = (TextView) convertView.findViewById(R.id.patron_name);
                    holder.patronDescription=(TextView)convertView.findViewById(R.id.patron_description);
                    holder.patronImg1=(ImageView)convertView.findViewById(R.id.detail_image1);
                    holder.patronImg2=(ImageView)convertView.findViewById(R.id.detail_image2);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.patronName.setText(objects.get(position)[1]);

                if(!objects.get(position)[2].equals("null")){
                    holder.patronDescription.setText(objects.get(position)[2]);
                    holder.patronDescription.setVisibility(View.VISIBLE);
                }
                else holder.patronDescription.setVisibility(View.INVISIBLE);

                holder.patronName.setTypeface(typeQuicksand);
                holder.patronDescription.setTypeface(typeSegoe);

                if(position%2==0){
                    holder.patronImg1.setVisibility(View.VISIBLE);
                    holder.patronImg2.setVisibility(View.GONE);
                        if(!objects.get(position)[3].equals("null")){
                            Glide.with(adapterContext)
                                    .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                                    .thumbnail(0.1f)
                                    .dontTransform()
                                    .into(holder.patronImg1);
                        }
                    else {
                            Glide.with(adapterContext)
                                    .load(R.drawable.priest)
                                    .thumbnail(0.1f)
                                    .dontTransform()
                                    .into(holder.patronImg1);
                        }
                }
                else {
                    holder.patronImg1.setVisibility(View.GONE);
                    holder.patronImg2.setVisibility(View.VISIBLE);
                        if(!objects.get(position)[3].equals("null")){
                            Glide.with(adapterContext)
                                    .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                                    .thumbnail(0.1f)
                                    .dontTransform()
                                    .into(holder.patronImg2);
                        }
                        else {
                            Glide.with(adapterContext)
                                    .load(R.drawable.priest)
                                    .thumbnail(0.1f)
                                    .dontTransform()
                                    .into(holder.patronImg2);
                        }
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //-----------------------------------Novena Details-------------------------------------
            case "NovenaDetailsList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_novena_church, null);
                    holder.novenaCaption=(TextView)convertView.findViewById(R.id.novena_caption);
                    holder.novenaChurchName=(TextView)convertView.findViewById(R.id.church_name);
                    holder.townName=(TextView)convertView.findViewById(R.id.town_name);
                    holder.novenaDescription=(TextView)convertView.findViewById(R.id.novena_description);
                    holder.novenaDate=(TextView)convertView.findViewById(R.id.novena_date);
                    holder.novenaImg=(ImageView)convertView.findViewById(R.id.detail_image);
                    holder.dayAndTime=(TextView)convertView.findViewById(R.id.novena_time);
                    holder.viewMap=(RelativeLayout)convertView.findViewById(R.id.view_in_map);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.novenaCaption.setText(objects.get(position)[1]);
                holder.novenaCaption.setTypeface(typeQuicksand);
                holder.novenaChurchName.setText(objects.get(position)[2]);
                holder.novenaChurchName.setTypeface(typeBLKCHCRY);
                holder.townName.setText(objects.get(position)[9]);
                holder.townName.setTypeface(typeSegoe);

                if(!objects.get(position)[3].equals("null")){
                    holder.novenaDescription.setText(objects.get(position)[3]);
                    holder.novenaDescription.setVisibility(View.VISIBLE);
                }
                else holder.novenaDescription.setVisibility(View.INVISIBLE);
                holder.novenaDescription.setTypeface(typeSegoe);

                if(!objects.get(position)[4].equals("null")){
                    holder.novenaDate.setVisibility(View.VISIBLE);
                    if(!objects.get(position)[5].equals("null")){
                        cal.setTimeInMillis(Long.parseLong(objects.get(position)[4]));
                        String startDate=formatted.format(cal.getTime());
                        cal.setTimeInMillis(Long.parseLong(objects.get(position)[5]));
                        String endDate=formatted.format(cal.getTime());
                        holder.novenaDate.setText(adapterContext.getResources().getString(R.string.novena_dates,startDate,endDate));
                    }
                    else {
                        cal.setTimeInMillis(Long.parseLong(objects.get(position)[4]));
                        String startDate=formatted.format(cal.getTime());
                        holder.novenaDate.setText(startDate);
                    }
                }
                else {
                    holder.novenaDate.setVisibility(View.GONE);
                }
                holder.novenaDate.setTypeface(typeSegoe);

                if(!objects.get(position)[6].equals("null")){
                        Glide.with(adapterContext)
                                .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[6].substring((objects.get(position)[6]).indexOf("img")))
                                .thumbnail(0.1f)
                                .into(holder.novenaImg)
                        ;
                }
                else{
                    holder.novenaImg.setPadding(15,15,15,15);
                    holder.novenaImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .into(holder.novenaImg);
                }

                if(!objects.get(position)[7].equals("null")){
                    holder.dayAndTime.setText(objects.get(position)[7]);
                    holder.dayAndTime.setVisibility(View.VISIBLE);
                }
                else holder.dayAndTime.setVisibility(View.GONE);
                holder.dayAndTime.setTypeface(typeSegoe);

                if(!objects.get(position)[8].equals("null,null")){
                    holder.viewMap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (objects.get(fPos)[8])));
                                adapterContext.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(adapterContext.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    holder.viewMap.setVisibility(View.VISIBLE);
                }
                else {
                    holder.viewMap.setVisibility(View.GONE);
                }


                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.novenaDescription.setMaxLines(100);//Expand description
                    }
                });
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //------------------------Near By Church list-----------------------------------
            case "NearbyChurchList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_neaby_church, null);
                    holder.nearbyChurchName=(TextView)convertView.findViewById(R.id.church_name);
                    holder.nearbyChurchAddress=(TextView)convertView.findViewById(R.id.address);
                    holder.distance=(TextView)convertView.findViewById(R.id.distance);
                    holder.nearbyViewMap=(ImageView)convertView.findViewById(R.id.map_icon);
                    holder.nearChurchImg=(ImageView)convertView.findViewById(R.id.church_image);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.nearbyChurchName.setText(objects.get(position)[1]);
                holder.nearbyChurchName.setTypeface(typeQuicksand);
                holder.nearbyChurchAddress.setText(objects.get(position)[2]);
                holder.nearbyChurchAddress.setTypeface(typeSegoe);

                holder.nearbyViewMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (objects.get(fPos)[3])));
                            adapterContext.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(adapterContext, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                holder.distance.setText(objects.get(position)[4]);
                holder.distance.setTypeface(typeQuicksand);
                holder.distance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (objects.get(fPos)[3])));
                            adapterContext.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(adapterContext, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                if(!objects.get(position)[5].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[5].substring((objects.get(position)[5]).indexOf("img")))
                            .thumbnail(0.1f)
                            .listener(new RequestListener<String, GlideDrawable>() {
                                @Override
                                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                    holder.nearChurchImg.setPadding(15,15,15,15);
                                    holder.nearChurchImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                    Glide.with(adapterContext)
                                            .load(R.drawable.church)
                                            .into(holder.nearChurchImg);
                                    return true;
                                }
                                @Override
                                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.nearChurchImg)
                    ;
                }
                else{
                    holder.nearChurchImg.setPadding(15,15,15,15);
                    holder.nearChurchImg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .into(holder.nearChurchImg)
                    ;
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //-----------------------------ChurchPiousOrgList-------------------------------------------
            case "ChurchPiousOrgList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_pious_organisation, null);
                    holder.orgPatronImage =(ImageView)convertView.findViewById(R.id.patron_image );
                    holder.orgName = (TextView) convertView.findViewById(R.id.Pious_org_name );
                    holder.orgPatronName =(TextView)convertView.findViewById(R.id.Pious_org_patron_name);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.orgName.setText(objects.get(position)[1]);
                holder.orgPatronName.setText(objects.get(position)[2]);
                holder.orgName.setTypeface(typeQuicksand);
                holder.orgPatronName.setTypeface(typeSegoe);
                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.orgPatronImage);
                }
                else {
                    Glide.with(adapterContext)
                            .load(R.drawable.priest)
                            .thumbnail(0.1f)
                            .into(holder.orgPatronImage);
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //---------------------ChurchInstitutions-------------------------
            case "ChurchInstitutions":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_institution, null);
                    holder.institutionImage =(ImageView)convertView.findViewById(R.id.institution_image );
                    holder.institutionName = (TextView) convertView.findViewById(R.id.institution_name );
                    holder.institutionAddress =(TextView)convertView.findViewById(R.id.institution_address);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.institutionName.setText(objects.get(position)[1]);
                holder.institutionAddress.setText(objects.get(position)[2]);
                holder.institutionName.setTypeface(typeQuicksand);
                holder.institutionAddress.setTypeface(typeSegoe);

                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .dontTransform()
                            .into(holder.institutionImage);
                }
                else{
                    holder.institutionImage.setPadding(15,15,15,15);
                    holder.institutionImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .into(holder.institutionImage)
                    ;
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //-------------------Church Events-------------------------------
            case "ChurchEvents":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_events, null);
                    holder.eventImage =(ImageView)convertView.findViewById(R.id.events_image);
                    holder.eventsHead = (TextView) convertView.findViewById(R.id.events_head );
                    holder.eventsDate =(TextView)convertView.findViewById(R.id.events_date);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.eventsHead.setText(objects.get(position)[1]);
                holder.eventsHead.setTypeface(typeSegoe);
                holder.eventsDate.setTypeface(typeSegoe);

                if(!objects.get(position)[0].equals("null")){
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[0]));
                    String startDate=formatted.format(cal.getTime());
                    holder.eventsDate.setText(startDate);
                }
                else {
                    holder.eventsDate.setText("");
                }
                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.eventImage);
                }
                else{
                    holder.eventImage.setPadding(15,15,15,15);
                    holder.eventImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.events)
                            .into(holder.eventImage)
                    ;
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //-------------------AllTownsResults-------------------------------
            case "AllTownsList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_town, null);
                    holder.townHead = (TextView) convertView.findViewById(R.id.town_name);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.townHead.setText(objects.get(position)[1]);
                break;

            //------------------------------PriestList-----------------------------
            case "PriestList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_priest, null);
                    holder.pImage =(ImageView)convertView.findViewById(R.id.priest_image);
                    holder.pName = (TextView) convertView.findViewById(R.id.priest_name );
                    holder.pParish =(TextView)convertView.findViewById(R.id.parish);
                    holder.pBaptism = (TextView) convertView.findViewById(R.id.baptismalname);
                    holder.pDesign =(TextView)convertView.findViewById(R.id.designation);
                    holder.pStatus = (TextView) convertView.findViewById(R.id.priest_status);
                    holder.pDOB =(TextView)convertView.findViewById(R.id.date_of_birth);
                    holder.pMob = (TextView) convertView.findViewById(R.id.priest_mobile);
                    holder.pDateOrdination =(TextView)convertView.findViewById(R.id.date_of_ordination);
                    holder.pEmail =(TextView)convertView.findViewById(R.id.priest_email);
                    holder.pAddress =(TextView)convertView.findViewById(R.id.priest_address);
                    holder.pAbout =(TextView)convertView.findViewById(R.id.priest_about);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------

                holder.pName.setText(objects.get(position)[1]);
                holder.pAddress.setText(objects.get(position)[2]);
                holder.pAbout.setText(objects.get(position)[4]);
                holder.pParish.setText(objects.get(position)[5]);
                holder.pEmail.setText(objects.get(position)[8]);
                holder.pMob.setText(objects.get(position)[9]);
                holder.pDesign.setText(objects.get(position)[10]);
                holder.pStatus.setText(objects.get(position)[11]);
                holder.pBaptism.setText(objects.get(position)[12]);


                if(!objects.get(position)[6].equals("null")){
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[6]));
                    String dob=formatted.format(cal.getTime());
                    holder.pDOB.setText(dob);
                }
                else {
                    holder.pDOB.setText("-");
                }

                if(!objects.get(position)[7].equals("null")){
                    cal.setTimeInMillis(Long.parseLong(objects.get(position)[7]));
                    String date=formatted.format(cal.getTime());
                    holder.pDateOrdination.setText(date);
                }
                else {
                    holder.pDateOrdination.setText("-");
                }

                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.pImage);
                }
                else {
                    Glide.with(adapterContext)
                            .load(R.drawable.priest)
                            .thumbnail(0.1f)
                            .into(holder.pImage);
                }


                holder.pMob.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri number = Uri.parse("tel:" +objects.get(fPos)[9]);
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                        adapterContext.startActivity(callIntent);
                    }
                });
                holder.pEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("*/*");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{objects.get(fPos)[8]});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Mail from Church App User");
                        if (intent.resolveActivity(adapterContext.getPackageManager()) != null) {
                            adapterContext.startActivity(intent);
                        }
                    }
                });
                break;
            //------------------ChurchNotices---------------------------
            case "ChurchNotices":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_notices, null);
                    holder.noticeImage =(ImageView)convertView.findViewById(R.id.notice_image );
                    holder.noticeHead = (TextView) convertView.findViewById(R.id.notice_name );
                    holder.noticeType =(TextView)convertView.findViewById(R.id.notice_type);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.noticeHead.setText(objects.get(position)[1]);
                holder.noticeType.setText(objects.get(position)[4]);
                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.noticeImage);
                }
                else {
                    Glide.with(adapterContext)
                            .load(R.drawable.notices)
                            .thumbnail(0.1f)
                            .into(holder.noticeImage);
                }
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //--------------------Gallery Album grid----------------------
            case "GalleryAlbums":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_gallery_album, null);
                    holder.galleryAlbum =(ImageView)convertView.findViewById(R.id.grid_img );
                    holder.albumTitle = (TextView) convertView.findViewById(R.id.grid_txt );
                    holder.itemCount  =(TextView)convertView.findViewById(R.id.item_count);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------
                holder.albumTitle.setText(objects.get(position)[1]);
                holder.itemCount.setText(objects.get(position)[2]);
                holder.albumTitle.setTypeface(typeQuicksand);
                holder.itemCount.setTypeface(typeSegoe);
                if(!objects.get(position)[3].equals("null")){
                    holder.galleryAlbum.setVisibility(View.VISIBLE);
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .dontTransform()
                            .thumbnail(0.1f)
                            .into(holder.galleryAlbum);
                }
                else {
                    holder.galleryAlbum.setVisibility(View.INVISIBLE);
                }
                holder.albumTitle.setMaxLines(1);
                holder.albumTitle.setEllipsize(TextUtils.TruncateAt.END);
                break;
            //--------------------Gallery Album grid----------------------
            case "GalleryItems":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_gallery_item, null);
                    holder.galleryItem =(ImageView)convertView.findViewById(R.id.image);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                if(!objects.get(position)[1].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[1].substring((objects.get(position)[1]).indexOf("img")))
                            .dontTransform()
                            .thumbnail(0.1f)
                            .into(holder.galleryItem);
                }
                else {
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .dontTransform()
                            .thumbnail(0.1f)
                            .into(holder.galleryItem);
                }
                break;
            //------------------Church family units---------------------------
            case "ChurchFamilyUnits":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_familyunits, null);
                    holder.familyUnitHead = (TextView) convertView.findViewById(R.id.familyunit_name );
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.familyUnitHead.setText(objects.get(position)[1]);
                break;
            //------------------FamilyDetails---------------------------
            case "FamilyDetails":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_family, null);
                    holder.familyHead = (TextView) convertView.findViewById(R.id.family_head );
                    holder.familyName =(TextView) convertView.findViewById(R.id.family_name );
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.familyHead.setText(objects.get(position)[2]+" "+objects.get(position)[3]);
                holder.familyName.setText(objects.get(position)[1]);
                break;
            //------------------FamilyDetails---------------------------
            case "FamilyExecutive":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_fam_unit_executives, null);
                    holder.personName = (TextView) convertView.findViewById(R.id.per_name );
                    holder.personMob =(TextView) convertView.findViewById(R.id.per_mobile );
                    holder.personPosition =(TextView) convertView.findViewById(R.id.per_postion );
                    holder.personImg =(ImageView)convertView.findViewById(R.id.person_img );
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.personName.setText(objects.get(position)[1]+" "+objects.get(position)[2]);
                holder.personMob.setText(objects.get(position)[5]);
                holder.personPosition.setText(objects.get(position)[4]);
                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .dontTransform()
                            .thumbnail(0.1f)
                            .into(holder.personImg);
                }
                break;
            default:
                break;
        }
        return convertView;
    }
}
