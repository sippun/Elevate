package com.example.android.elevate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

public class MoodInputUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_input_ui);

        // initiate rating bar and a button
        final RatingBar moodRatingBar = findViewById(R.id.moodRatingBar);
        Button submitButton = findViewById(R.id.submitMood);
        // Mood Input UI Submit Button ClickListener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Displays a toast message with number of stars submitted
                int rating = (int)moodRatingBar.getRating();

                String starCheck;
                if (rating == 1) {       // Singular
                    starCheck = "star";
                }
                else {                   // Plural
                    starCheck = "stars";
                }
                MainActivity.database.recordMood(rating);
                // Toast Message
                String ratingString = rating + " " + starCheck + " recorded";
                Toast.makeText(getApplicationContext(), ratingString, Toast.LENGTH_LONG).show();
                startActivity(new Intent(MoodInputUI.this, MainActivity.class));
            }
        });
    }
}
