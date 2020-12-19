package com.example.pknu.mobprog.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.videoView);
        Resources res = getResources();
        int id_video = res.getIdentifier("video", "raw", getPackageName());

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + id_video);

        videoView.setVideoURI(uri);
        videoView.start();
    }
}