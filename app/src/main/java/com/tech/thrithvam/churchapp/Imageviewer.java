package com.tech.thrithvam.churchapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import uk.co.senab.photoview.PhotoView;

public class ImageViewer extends AppCompatActivity {

    PhotoView photoView;
    Bundle extras;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageviewer);
        extras=getIntent().getExtras();
        String imageURL=extras.getString("URL");
        photoView=(PhotoView)findViewById(R.id.punchAttachView);

        if (isOnline()){
            Glide.with(ImageViewer.this)
                    .load(getResources().getString(R.string.url) +imageURL.substring((imageURL).indexOf("img")))
                    .thumbnail(0.1f)
                    .crossFade()
                    .dontTransform()
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            Glide.with(ImageViewer.this)
                                    .load(R.drawable.church)
                                    .into(photoView);
                            return true;
                        }
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    }).into(photoView);
        }
        else {
            Toast.makeText(ImageViewer.this,R.string.network_off_alert,Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //for rotating the image
    public void rotateImage(View view){
        photoView.setRotationBy(90f);
    }
}
