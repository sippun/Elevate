package com.example.android.elevate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import static com.example.android.elevate.R.layout.repeating_activity_layout;

/**
 * Created by katierosengrant on 10/29/17.
 *
 * Dummy activity (habit logger page) for when user taps on notification.
 */

class RepeatingActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(repeating_activity_layout);
    }
}
