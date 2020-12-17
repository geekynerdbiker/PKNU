package com.example.pknu.mobprog.midt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Switch switch1;
    TextView question;
    RadioGroup radioGroup;
    RadioButton alg;
    RadioButton obj;
    RadioButton and;
    ImageView image;
    Button quit;
    Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAppInfo();

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println(isChecked);
                if (isChecked) {
                    question.setVisibility(View.VISIBLE);
                    radioGroup.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    quit.setVisibility(View.VISIBLE);
                    goBack.setVisibility(View.VISIBLE);
                }
            }
        });

        goBack.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch1.setChecked(false);
                question.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                quit.setVisibility(View.INVISIBLE);
                goBack.setVisibility(View.INVISIBLE);
            }
        });

        quit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    protected void getAppInfo() {
        switch1 = findViewById(R.id.switch1);
        question = findViewById(R.id.textView3);
        radioGroup = findViewById(R.id.radioGroup);
        alg = findViewById(R.id.radioButton1);
        obj = findViewById(R.id.radioButton2);
        and = findViewById(R.id.radioButton3);
        image = findViewById(R.id.imageView);
        quit = findViewById(R.id.button);
        goBack = findViewById(R.id.button2);
    }

    public void onClickAlg(View view){
        image.setImageResource(R.drawable.img1);
    }
    public void onClickObj(View view){
        image.setImageResource(R.drawable.img2);
    }
    public void onClickAnd(View view){
        image.setImageResource(R.drawable.img3);
    }
}

