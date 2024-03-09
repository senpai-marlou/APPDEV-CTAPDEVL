package com.loompa.tapandshoot;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class TutorialPage extends AppCompatActivity {

    private ImageView imageView;
    private ImageView imageFillTop;
    private ImageView imageFillBottom;
    private ImageView next1;
    private ImageView next2;
    private ImageView getIt;
    private int[] imageArray = {R.drawable.t_one, R.drawable.t_two, R.drawable.t_three};
    private int currentIndex = 0;

    private int nextPage = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_page);

        // Make it Full Screen
        FullScreen.setFullscreen(getWindow());

        imageView = findViewById(R.id.imageView);
        imageFillTop = findViewById(R.id.imageFillTop);
        imageFillBottom = findViewById(R.id.imageFillBottom);

        // Buttons
        next1 = findViewById(R.id.next1);
        next2 = findViewById(R.id.next2);
        getIt = findViewById(R.id.getIt);

        next1.setClickable(true);
        next2.setClickable(false);
        getIt.setClickable(false);

        next1.setVisibility(View.VISIBLE);
        next2.setVisibility(View.INVISIBLE);
        getIt.setVisibility(View.INVISIBLE);

        // Animate Elements
        MainActivity.resetValues(); // Reset all values
        MainActivity.animateTutorialUp();

        next1.setOnTouchListener(new View.OnTouchListener() {
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
                        next1.setImageResource(R.drawable.a_next_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (nextPage == 1) {
                            MainActivity.animateTutorialDown();
                            MainActivity.animateSlideBack(); // Slide Top Elements Animation
                            next1.setImageResource(R.drawable.a_next_on);
                            next1.setClickable(false);
                            next1.setVisibility(View.INVISIBLE);

                            next2.setClickable(true);
                            next2.setVisibility(View.VISIBLE);

                            imageView.setTranslationY(-10);
                            imageView.setImageResource(R.drawable.t_two);

                            imageFillTop.setVisibility(View.INVISIBLE);
                            imageFillBottom.setTranslationY(-10);
                        } else {
                            next1.setImageResource(R.drawable.a_next_on);
                            imageView.setImageResource(R.drawable.t_one_one);
                        } nextPage++;
                        break;
                }
                return true; // Consume the event
            }
        });

        next2.setOnTouchListener(new View.OnTouchListener() {
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
                        next2.setImageResource(R.drawable.a_next_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        MainActivity.animatePopOutBullets(); // Bullets Animation

                        next2.setClickable(false);
                        next2.setVisibility(View.INVISIBLE);
                        next2.setImageResource(R.drawable.a_next_on);

                        getIt.setClickable(true);
                        getIt.setVisibility(View.VISIBLE);
                        imageView.setTranslationY(10);
                        imageView.setImageResource(R.drawable.t_three);

                        imageFillTop.setVisibility(View.VISIBLE);
                        imageFillBottom.setVisibility(View.INVISIBLE);
                        imageFillTop.setTranslationY(10);
                        break;
                }
                return true; // Consume the event
            }
        });

        getIt.setOnTouchListener(new View.OnTouchListener() {
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
                        getIt.setImageResource(R.drawable.a_get_off);
                        break;
                    case MotionEvent.ACTION_UP:

                        if (MainActivity.isGameplay) {
                            finish();
                            MainActivity.animateSlideOut();
                            MainActivity.animatePopInBullets();
                            MainActivity.animateSlideOutGun();
                            MainActivity.isGameplay = !MainActivity.isGameplay;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(TutorialPage.this, MenuPage.class);
                                    intent.putExtra("existingPlayerName", MainActivity.existingPlayerName.getText().toString());
                                    startActivity(intent);
                                }
                            }, 50);
                        } else {
                            getIt.setClickable(false);
                            getIt.setVisibility(View.INVISIBLE);
                            getIt.setImageResource(R.drawable.a_get_on);

                            finish();
                            MainActivity.animateReady2();
                        }
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