package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
            //SearchingChurch--------------------
        TextView churchName,address,town;
        ImageView churchImage;
            //Novenas----------------------------
        TextView patronName,patronDescription;
        ImageView patronImg1,patronImg2;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

                animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);
                lastPosition = position;
                break;
            case "Novenas":
                if (convertView == null) {
                    holder = new Holder();
                    convertView = inflater.inflate(R.layout.patron_item, null);
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
                animation = AnimationUtils.loadAnimation(adapterContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
                convertView.startAnimation(animation);
                lastPosition = position;
                break;
            default:
                break;
        }
        return convertView;
    }

}
