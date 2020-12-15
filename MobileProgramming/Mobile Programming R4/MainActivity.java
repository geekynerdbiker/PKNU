package com.example.pknu.mobprog.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    boolean voiceIsPlaying = false;
    int pausePosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MediaPlayer voice = MediaPlayer.create(this, R.raw.voice);
        Button bl = findViewById(R.id.buttonListen);
        Button b1 = findViewById(R.id.button1);
        Button b2 = findViewById(R.id.button2);
        Button b3 = findViewById(R.id.button3);

        final MediaPlayer bgm = MediaPlayer.create(this, R.raw.videoplayback_1);
        bgm.start();

        bl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!voiceIsPlaying) {
                    voiceIsPlaying = true;
                    bgm.stop();
                    pausePosition = bgm.getCurrentPosition();
                    try {
                        bgm.prepare();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    bgm.seekTo(pausePosition);
                    voice.start();
                } else {
                    voiceIsPlaying = false;
                    voice.stop();
                    try {
                        voice.prepare();
                    } catch (IOException ie) {
                        ie.printStackTrace();
                    }
                    voice.seekTo(0);
                    bgm.start();
                }
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProfIntro.class);
                startActivity(intent);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Community.class);
                startActivity(intent);
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Announce.class);
                startActivity(intent);
            }
        });
    }
}