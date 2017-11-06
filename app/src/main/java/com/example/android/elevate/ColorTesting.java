package com.example.android.elevate;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import android.widget.ImageView;

import static com.example.android.elevate.R.id.imageView;


public class ColorTesting extends AppCompatActivity {

    private double task = 0.0;
    private double task2 = 0.0;
    private double task3 = 0.0;
    private double numTasks = 3.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colortesting);

        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    task = 1;
                } else {
                    task = 0;
                }
                changeColor();
            }
        });

        ToggleButton toggle2 = (ToggleButton) findViewById(R.id.toggleButton2);
        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    task2 = 1;
                } else {
                    task2 = 0;
                }
                changeColor();
            }
        });

        ToggleButton toggle3 = (ToggleButton) findViewById(R.id.toggleButton3);
        toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    task3 = 1;
                } else {
                    task3 = 0;
                }
                changeColor();
            }
        });

    }

    private void changeColor() {
        ImageView image1 = (ImageView) findViewById(R.id.imageView); //This part is messy but it just converts the percentage of tasks completed and converts it to the correct color
        image1.setColorFilter(Color.argb(255, (int) (255-(255*Math.max((task+task2+task3-(numTasks/2.0))/(numTasks/2.0), 0.0))), (int) (255*Math.min((task+task2+task3)/(numTasks/2.0), 1.0)), 0));

        ImageView image2 = (ImageView) findViewById(R.id.imageView2);
        image2.setColorFilter(Color.argb(255, (int) (255*Math.min((task+task2+task3)/(numTasks/2.0), 1.0)),  (int) (255-(255*Math.max((task+task2+task3-(numTasks/2.0))/(numTasks/2.0), 0.0))), 0));

    }
}
