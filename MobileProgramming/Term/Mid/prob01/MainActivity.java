package com.example.pknu.mobprog.midt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    boolean colorChanged = false;
    View view;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    VideoView videoView;
    VideoView videoView2;

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

        view = findViewById(R.id.view);

        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        videoView2 = (VideoView) findViewById(R.id.videoView2);
        textView4 = (TextView) findViewById(R.id.textView4);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!colorChanged) {
                    view.setBackgroundColor(Color.parseColor("#2196F3"));
                    colorChanged = true;
                }
                textView4.setVisibility(View.INVISIBLE);
                videoView2.setVisibility(View.VISIBLE);
                Resources res2 = getResources();
                int id_video2 = res2.getIdentifier("video2", "raw", getPackageName());

                Uri uri2 = Uri.parse("android.resource://" + getPackageName() + "/" + id_video2);

                videoView2.setVideoURI(uri2);
                videoView2.start();
            }
        });

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!colorChanged){
                    view.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    colorChanged = true;
                }
                videoView2.setVisibility(View.INVISIBLE);
                textView4.setVisibility(View.VISIBLE);
            }
        });

    }
}