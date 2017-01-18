package com.tech.thrithvam.churchapp;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class VideoPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        String vidAddress = "http://jdadmin-001-site4.itempurl.com/vid/e54fbbe7-7b2c-4f8c-aa16-0669e5ad4b0a.mp4";
        Uri vidUri = Uri.parse(vidAddress);

        VideoView vidView=(VideoView)findViewById(R.id.video_view);
        vidView.setVideoURI(vidUri);

        vidView.start();


        MediaController vidControl = new MediaController(this);

        RelativeLayout playerScreen=(RelativeLayout)findViewById(R.id.activity_video_player);
        vidControl.setAnchorView(playerScreen);
        vidView.setMediaController(vidControl);
    }
}
