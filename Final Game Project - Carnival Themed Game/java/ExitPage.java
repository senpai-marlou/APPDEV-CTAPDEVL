package com.loompa.tapandshoot;

import static com.loompa.tapandshoot.MenuPage.exitButtonImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class ExitPage extends AppCompatActivity {

    private ImageView yesButton;
    private ImageView noButton;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit_page);

        FullScreen.setFullscreen(getWindow());

        yesButton = findViewById(R.id.yesButton);
        yesButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        yesButton.setImageResource(R.drawable.a_yes_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        System.exit(0);
                        break;
                }
                return true; // Consume the event
            }
        });

        noButton = findViewById(R.id.noButton);
        noButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        noButton.setImageResource(R.drawable.a_no_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        finish();
                        noButton.setImageResource(R.drawable.a_no_on);
                        exitButtonImageView.setImageResource(R.drawable.ic_exit_on);
                        break;
                }
                return true; // Consume the event
            }
        });


    }

    Boolean shouldAllowBack = false;
    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }
}