package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    Animation animation;
    private int lastPosition;
//    DatabaseHandler db;
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
//        db=DatabaseHandler.getInstance(context);
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
        TextView novenaCaption,novenaChurchName,novenaDescription,novenaDate,dayAndTime;
        ImageView novenaImg;
            //Nearby Church List-------------------
        TextView nearbyChurchName,nearbyChurchAddress,distance;
        ImageView nearChurchImg,nearbyViewMap;
            //Pious Organisation--------------------
        TextView OrgName,OrgPatronName;
        ImageView Orgpartonimage;
            //Institutions---------------------
        TextView Institution_Name,Ins_Address;
        ImageView Institution_image;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
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
                            .into(holder.churchImage)
                            ;
                }
                else{
                    holder.churchImage.setPadding(15,15,15,15);
                    holder.churchImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    Glide.with(adapterContext)
                            .load(R.drawable.church)
                            .into(holder.churchImage)
                            ;
                }

                if(!objects.get(position)[4].equals("null")){
                    holder.address.setText(objects.get(position)[4]);
                    holder.address.setVisibility(View.VISIBLE);
                }
                else holder.address.setVisibility(View.INVISIBLE);

                holder.churchName.setTypeface(typeQuicksand);
                holder.town.setTypeface(typeSegoe);
                holder.address.setTypeface(typeSegoe);

               /* animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);*/
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
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
                        if(!objects.get(position)[3].equals("null")){
                            holder.patronImg1.setVisibility(View.VISIBLE);
                            holder.patronImg2.setVisibility(View.GONE);
                            Glide.with(adapterContext)
                                    .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                                    .thumbnail(0.1f)
                                    .into(holder.patronImg1)
                            ;
                        }
                }
                else {
                        if(!objects.get(position)[3].equals("null")){
                            holder.patronImg1.setVisibility(View.GONE);
                            holder.patronImg2.setVisibility(View.VISIBLE);
                            Glide.with(adapterContext)
                                    .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                                    .thumbnail(0.1f)
                                    .into(holder.patronImg2)
                            ;
                        }
                }
                /*animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);*/
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            case "NovenaDetailsList":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_novena_church, null);
                    holder.novenaCaption=(TextView)convertView.findViewById(R.id.novena_caption);
                    holder.novenaChurchName=(TextView)convertView.findViewById(R.id.church_name);
                    holder.novenaDescription=(TextView)convertView.findViewById(R.id.novena_description);
                    holder.novenaDate=(TextView)convertView.findViewById(R.id.novena_date);
                    holder.novenaImg=(ImageView)convertView.findViewById(R.id.detail_image);
                    holder.dayAndTime=(TextView)convertView.findViewById(R.id.novena_time);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //Label loading--------------------
                holder.novenaCaption.setText(objects.get(position)[1]);
                holder.novenaCaption.setTypeface(typeQuicksand);
                holder.novenaChurchName.setText(objects.get(position)[2]);
                holder.novenaChurchName.setTypeface(typeBLKCHCRY);

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

                if(!objects.get(position)[7].equals("null")){
                    holder.dayAndTime.setText(objects.get(position)[7]);
                    holder.dayAndTime.setVisibility(View.VISIBLE);
                }
                else holder.dayAndTime.setVisibility(View.INVISIBLE);
                holder.dayAndTime.setTypeface(typeSegoe);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.novenaDescription.setMaxLines(100);
                    }
                });
                /*animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);*/
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            case "nearbyChurchList":
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
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (objects.get(position)[3])));
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
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + (objects.get(position)[3])));
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
                                            .into(holder.nearChurchImg)
                                    ;
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

               /* animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);*/
                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //
            case "ChurchPiousOrgResults":

                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_pious_organisation, null);

                    holder.Orgpartonimage =(ImageView)convertView.findViewById(R.id.patron_image );
                    holder.OrgName  = (TextView) convertView.findViewById(R.id.Pious_org_name );
                    holder.OrgPatronName =(TextView)convertView.findViewById(R.id.Pious_org_patron_name);

                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------

                holder.OrgName.setText(objects.get(position)[1]);
                holder.OrgPatronName.setText(objects.get(position)[2]);

                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.Orgpartonimage)
                    ;
                }

                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            //---------------------ChurchInstutionsResults-------------------------
            case "ChurchInstutionsResults":

                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.item_institution, null);

                    holder.Institution_image =(ImageView)convertView.findViewById(R.id.Institution_image );
                    holder.Institution_Name = (TextView) convertView.findViewById(R.id.Institution_name );
                    holder.Ins_Address =(TextView)convertView.findViewById(R.id.Institution_Address);

                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                //----------------Label loading--------------------

                holder.Institution_Name.setText(objects.get(position)[1]);
                holder.Ins_Address.setText(objects.get(position)[2]);

                if(!objects.get(position)[3].equals("null")){
                    Glide.with(adapterContext)
                            .load(adapterContext.getResources().getString(R.string.url) +objects.get(position)[3].substring((objects.get(position)[3]).indexOf("img")))
                            .thumbnail(0.1f)
                            .into(holder.Institution_image)
                    ;
                }

                if(position>lastPosition){
                    animation = AnimationUtils.loadAnimation(adapterContext, R.anim.up_from_bottom);
                    convertView.startAnimation(animation);
                }
                lastPosition = position;
                break;
            default:
                break;
        }
        return convertView;
    }

}
