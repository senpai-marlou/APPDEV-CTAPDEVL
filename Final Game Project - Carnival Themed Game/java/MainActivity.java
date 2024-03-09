package com.loompa.tapandshoot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {
    private int screenWidth;
    private static Context context;
    private ImageView[] targetImageViews;
    private ValueAnimator[] animators;
    private ImageView waveImageView1;
    private ImageView waveImageView2;
    private ImageView waveImageView3;
    private static ImageView bulletsImageView;
    private TextView scoreUpdate;
    private TextView timeUpdate;
    private ImageView hitMarkerImageView;
    private static TextView scoreTextView;
    private static TextView timerTextView;
    private static TextView bulletsTextView;
    public static TextView existingPlayerName;
    private static Button startButton;
    private boolean[] isTargetHit;
    private boolean[] isTargetSlidingUp;
    private ValueAnimator[] initialSlideUpAnimators;
    private ValueAnimator[] bounceAnimators;
    private boolean areTargetsTargetable = false;
    private static boolean isReloading = false;
    private int slideUpSpeed = 300;
    public static int score = 0;
    private static int bullets = 8; // Total number of bullets
    private static int totalBullets = 160;
    private static int timer = 60;
    private boolean isTimerRunning = false; // Add this line to declare isTimerRunning
    private Handler timerHandler = new Handler(); // Add this line to declare timerHandler
    private Runnable timerRunnable; // Declare timerRunnable
    // public static MediaPlayer bgInGame;
    public static MediaPlayer bgInGameLoop;
    public static MediaPlayer sfxDuck;
    public static MediaPlayer sfxTincan;
    public static MediaPlayer sfxCow;
    public static MediaPlayer sfxBell;
    public static MediaPlayer sfxReload;
    public static MediaPlayer sfxGunshot;
    public static MediaPlayer sfxNoBullets;
    public static MediaPlayer sfxButtonPress;
    public static MediaPlayer sfxMetal;
    public static MediaPlayer sfxWater;
    public static boolean isMusicMuted = false;
    public static boolean isSfxMuted = false;
    private static boolean isNoAmmo = false;
    private float gunPositionX = -1;
    private float gunPositionY = -1;

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase database;

    public static ImageView backgroundMusicImageView;
    public static ImageView sfxMusicImageView;

    // Elements to animate
    private static ImageView clockBoard;
    private static ImageView scoreBoard;
    private static ImageView nickname;
    private static ImageView gun;
    private static ImageView gunShell;
    private static ImageView readyImageView;
    private static ImageView shootImageView;
    private static ImageView reloadTxt;
    private float initialPositionX = -1;
    private float initialPositionY = -1;

    // Tutorial Elements
    public static ImageView t_target1;
    public static ImageView t_target2;
    public static ImageView t_target3;
    public static ImageView t_target4;
    public static ImageView t_target5;
    public static ImageView t_target6;
    public static ImageView t_target7;
    public static ImageView t_target8;
    public static ImageView t_target9;
    public static ImageView t_target10;

    public static Boolean isGameplay = false;

    private static ImageView closingCurtainRight;
    private static ImageView closingCurtainLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Set the page Fullscreen
        FullScreen.setFullscreen(getWindow());
        screenWidth = 852;

        // Set the Player Name
        existingPlayerName = findViewById(R.id.existingPlayerName);
        existingPlayerName.setText(""); // Initial Value

        // Waves
        waveImageView1 = findViewById(R.id.wave1);
        waveImageView2 = findViewById(R.id.wave2);
        waveImageView3 = findViewById(R.id.wave3);

        // Bullets
        bulletsImageView = findViewById(R.id.bullets);

        // Score | Time Updates
        scoreUpdate = findViewById(R.id.scoreUpdate);
        timeUpdate = findViewById(R.id.timeUpdate);

        // Target Images
        targetImageViews = new ImageView[]{
                findViewById(R.id.target1),
                findViewById(R.id.target2),
                findViewById(R.id.target3),
                findViewById(R.id.target4),
                findViewById(R.id.target5),
                findViewById(R.id.target6),
                findViewById(R.id.target7),
                findViewById(R.id.target8),
                findViewById(R.id.target9),
                findViewById(R.id.target10)
        };

        hitMarkerImageView = findViewById(R.id.hitMarkerImageView);
        scoreTextView = findViewById(R.id.scoreTextView);
        timerTextView = findViewById(R.id.timerTextView);
        bulletsTextView = findViewById(R.id.bulletsTextView);
        startButton = findViewById(R.id.startButton);
        startButton.setClickable(false);

        // Elements
        clockBoard = findViewById(R.id.clockBoard);
        scoreBoard = findViewById(R.id.scoreBoard);
        nickname = findViewById(R.id.nickname);
        gun = findViewById(R.id.gun);
        gunShell = findViewById(R.id.gunShell);
        closingCurtainRight = findViewById(R.id.closingCurtainRight);
        closingCurtainLeft = findViewById(R.id.closingCurtainLeft);
        readyImageView = findViewById(R.id.readyImageView);
        shootImageView = findViewById(R.id.shootImageView);
        reloadTxt = findViewById(R.id.reloadTxt);

        // Tutorial
        t_target1 = findViewById(R.id.t_target1);
        t_target2 = findViewById(R.id.t_target2);
        t_target3 = findViewById(R.id.t_target3);
        t_target4 = findViewById(R.id.t_target4);
        t_target5 = findViewById(R.id.t_target5);
        t_target6 = findViewById(R.id.t_target6);
        t_target7 = findViewById(R.id.t_target7);
        t_target8 = findViewById(R.id.t_target8);
        t_target9 = findViewById(R.id.t_target9);
        t_target10 = findViewById(R.id.t_target10);

        isTargetHit = new boolean[targetImageViews.length];
        Arrays.fill(isTargetHit, false);

        isTargetSlidingUp = new boolean[targetImageViews.length];
        Arrays.fill(isTargetSlidingUp, false);

        initialSlideUpAnimators = new ValueAnimator[targetImageViews.length];
        bounceAnimators = new ValueAnimator[targetImageViews.length];

        // Get the location of the table ImageView
        ImageView tableImageView = findViewById(R.id.table);
        int[] tableLocation = new int[2];
        tableImageView.getLocationOnScreen(tableLocation);

        final Rect tableRect = new Rect(tableLocation[0], tableLocation[1],
                tableLocation[0] + tableImageView.getWidth(), tableLocation[1] + tableImageView.getHeight());

        bgInGameLoop = MediaPlayer.create(MainActivity.this, R.raw.bg_gamesound_loop);
        bgInGameLoop.setLooping(true);
        bgInGameLoop.start();

        sfxDuck = MediaPlayer.create(MainActivity.this, R.raw.sfx_duck);
        sfxTincan = MediaPlayer.create(MainActivity.this, R.raw.sfx_tincan);
        sfxCow = MediaPlayer.create(MainActivity.this, R.raw.sfx_cow);
        sfxBell = MediaPlayer.create(MainActivity.this, R.raw.sfx_bell);
        sfxReload = MediaPlayer.create(MainActivity.this, R.raw.sfx_reload);
        sfxGunshot = MediaPlayer.create(MainActivity.this, R.raw.sfx_gunsound);
        sfxNoBullets = MediaPlayer.create(MainActivity.this, R.raw.sfx_nobullets);
        sfxButtonPress = MediaPlayer.create(MainActivity.this, R.raw.sfx_pressbtn);
        sfxMetal = MediaPlayer.create(MainActivity.this, R.raw.sfx_metal);
        sfxWater = MediaPlayer.create(MainActivity.this, R.raw.sfx_water);

        // Set the volume based on the mute state
        if (isMusicMuted) {
            // bgInGame.setVolume(0.0f, 0.0f); // Mute
            bgInGameLoop.setVolume(0.0f, 0.0f);
        } else {
            // bgInGame.setVolume(1.0f, 1.0f); // Unmute
            bgInGameLoop.setVolume(1.0f, 1.0f);
        }

        // Set the volume based on the mute state (Short-hand)
        float volume = isSfxMuted ? 0.0f : 1.0f;
        sfxDuck.setVolume(volume, volume);
        sfxTincan.setVolume(volume, volume);
        sfxCow.setVolume(volume, volume);
        sfxBell.setVolume(volume, volume);
        sfxReload.setVolume(volume, volume);
        sfxNoBullets.setVolume(volume, volume);
        sfxButtonPress.setVolume(volume, volume);
        sfxMetal.setVolume(volume, volume);
        sfxWater.setVolume(volume, volume);

        if (isSfxMuted) {
            sfxGunshot.setVolume(0.0f, 0.0f); // Mute
        } else {
            sfxGunshot.setVolume(0.3f, 0.3f);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set the target visible
                for (ImageView targetImageView : targetImageViews) {
                    targetImageView.setVisibility(View.VISIBLE);
                }

                View[] targets = {t_target1, t_target2, t_target3, t_target4, t_target5, t_target6, t_target7, t_target8, t_target9, t_target10};

                for (View target : targets) {
                    target.setVisibility(View.GONE);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTimer(); // Start the timer Animation
                        initialLoopingAnimations();
                        sfxBell.start(); // Play the start sound effect

                        // Set targets as targetable
                        areTargetsTargetable = true;

                        // Check if the MediaPlayer object is null or not prepared
                        if (bgInGameLoop == null || !bgInGameLoop.isPlaying()) {
                            // Create and prepare the MediaPlayer object
                            bgInGameLoop = MediaPlayer.create(MainActivity.this, R.raw.bg_gamesound);
                            bgInGameLoop.setLooping(true);
                            // Set the volume based on the mute state
                            if (isMusicMuted) {
                                bgInGameLoop.setVolume(0.0f, 0.0f); // Mute
                            } else {
                                bgInGameLoop.setVolume(1.0f, 1.0f); // Unmute
                            }
                        } else {
                            // Rewind to the beginning if already playing
                            bgInGameLoop.seekTo(0);
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startClockShakeAnimation(); // Clock shake Animation
                            }
                        }, 250);
                        bgInGameLoop.start(); // Start or restart mediaPlayer
                    }
                }, 400);
            }
        });


        // Set OnClickListener for the table ImageView
        tableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if reloading is ongoing
                if (!isReloading && bullets < 8) {
                    if (sfxReload.isPlaying()) {
                        sfxReload.seekTo(0); // Rewind the sound effect to the beginning
                    } else {
                        sfxReload.start(); // Play the sound effect
                    }
                    // Reload the bullets
                    reloadBullets();
                }
            }
        });

        // BG Music
        backgroundMusicImageView = findViewById(R.id.backgroundMusic);
        backgroundMusicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Sfx Button press
                if (sfxButtonPress.isPlaying()) {
                    sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxButtonPress.seekTo(0);
                    sfxButtonPress.start(); // Play the sound effect
                }

                if (bgInGameLoop != null) {
                    if (isMusicMuted) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                backgroundMusicImageView.setImageResource(R.drawable.ic_music_on);
                                MenuPage.backgroundMusicImageView.setImageResource(R.drawable.ic_music_on);
                            }
                        }, 150);
                        bgInGameLoop.setVolume(1.0f, 1.0f); // Unmut
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                backgroundMusicImageView.setImageResource(R.drawable.ic_music_off);
                                MenuPage.backgroundMusicImageView.setImageResource(R.drawable.ic_music_off);
                            }
                        }, 150);
                        bgInGameLoop.setVolume(0.0f, 0.0f); // Mute
                    }
                    isMusicMuted = !isMusicMuted; // Toggle the mute state
                }
            }
        });

        // SFX Music
        sfxMusicImageView = findViewById(R.id.sfxMusic);
        sfxMusicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Sfx Button press
                if (sfxButtonPress.isPlaying()) {
                    sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxButtonPress.start(); // Play the sound effect
                }

                if (sfxDuck != null) {
                    if (isSfxMuted) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sfxMusicImageView.setImageResource(R.drawable.ic_sfx_on);
                                MenuPage.sfxMusicImageView.setImageResource(R.drawable.ic_sfx_on);
                            }
                        }, 150);
                        sfxDuck.setVolume(1.0f, 1.0f); // Unmute
                        sfxTincan.setVolume(1.0f, 1.0f);
                        sfxCow.setVolume(1.0f, 1.0f);
                        sfxBell.setVolume(1.0f, 1.0f);
                        sfxReload.setVolume(1.0f, 1.0f);
                        sfxGunshot.setVolume(0.3f, 0.3f);
                        sfxNoBullets.setVolume(1.0f, 1.0f);
                        sfxButtonPress.setVolume(1.0f, 1.0f);
                        sfxMetal.setVolume(1.0f, 1.0f);
                        sfxWater.setVolume(1.0f, 1.0f);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sfxMusicImageView.setImageResource(R.drawable.ic_sfx_off);
                                MenuPage.sfxMusicImageView.setImageResource(R.drawable.ic_sfx_off);
                            }
                        }, 150);
                        sfxDuck.setVolume(0.0f, 0.0f); // Mute
                        sfxTincan.setVolume(0.0f, 0.0f);
                        sfxCow.setVolume(0.0f, 0.0f);
                        sfxBell.setVolume(0.0f, 0.0f);
                        sfxReload.setVolume(0.0f, 0.0f);
                        sfxGunshot.setVolume(0.0f, 0.0f);
                        sfxNoBullets.setVolume(0.0f, 0.0f);
                        sfxButtonPress.setVolume(0.0f, 0.0f);
                        sfxMetal.setVolume(0.0f, 0.0f);
                        sfxWater.setVolume(0.0f, 0.0f);
                    }
                    isSfxMuted = !isSfxMuted; // Toggle the mute state
                }
            }
        });

        ImageView topCurtainImageView = findViewById(R.id.topCurtain);
        topCurtainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call back the MenuPage
//                if (!isMenuPageRunning()) {
//                    Intent intent = new Intent(MainActivity.this, MenuPage.class);
//                    intent.putExtra("existingPlayerName", existingPlayerName.getText().toString());
//                    intent.putExtra("isMusicMuted", isMusicMuted);
//                    intent.putExtra("isSfxMuted", isSfxMuted);
//                    startActivity(intent);
//                }
                return;
            }
        });

        // Start continuous looping animations for all targets
        startWaveAnimation(waveImageView1, -84, 1200);
        startWaveAnimation(waveImageView2, 62, 1000);
        startWaveAnimation(waveImageView3, -91, 1500);

        // Set the context
        context = this;

        // Start the main Menu
        Intent intent = new Intent(this, MenuPage.class);
        startActivity(intent);
    }

    public static void animateReady() {
        // Find the ImageView
        readyImageView.setVisibility(View.VISIBLE);

        // Animate Elements
        resetValues(); // Reset all values
        animateSlideBack(); // Slide Top Elements Animation
        animatePopOutBullets(); // Bullets Animation
        animateSlideBackGun(); // Gun Animation
        animatePopOutPlayer(); // Player Animation

        // Apply the scale animation using ViewPropertyAnimator
        readyImageView.setScaleX(0.0f);
        readyImageView.setScaleY(0.0f);
        readyImageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Apply the reverse animation
                        readyImageView.animate()
                                .scaleX(0.0f)
                                .scaleY(0.0f)
                                .setDuration(800) // Duration in milliseconds
                                .setInterpolator(new OvershootInterpolator(0.0f))
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateShoot();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    public static void animateReady2() {

        animateSlideBackGun(); // Gun Animation
        animatePopOutPlayer(); // Player Animation

        // Find the ImageView
        readyImageView.setVisibility(View.VISIBLE);

        // Apply the scale animation using ViewPropertyAnimator
        readyImageView.setScaleX(0.0f);
        readyImageView.setScaleY(0.0f);
        readyImageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Apply the reverse animation
                        readyImageView.animate()
                                .scaleX(0.0f)
                                .scaleY(0.0f)
                                .setDuration(800) // Duration in milliseconds
                                .setInterpolator(new OvershootInterpolator(0.0f))
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        animateShoot();
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private static void animateShoot() {
        // Find the ImageView
        shootImageView.setVisibility(View.VISIBLE);

        // Apply the scale animation using ViewPropertyAnimator
        shootImageView.setScaleX(0.0f);
        shootImageView.setScaleY(0.0f);
        shootImageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        startButton.performClick();
                    }
                })
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // Apply the reverse animation
                        shootImageView.animate()
                                .scaleX(0.0f)
                                .scaleY(0.0f)
                                .setDuration(300) // Duration in milliseconds
                                .setInterpolator(new OvershootInterpolator(0.0f))
                                .start();
                    }
                })
                .start();
    }


    private boolean isMenuPageRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
            ComponentName componentName = taskInfo.topActivity;
            if (componentName != null && componentName.getClassName().equals(MenuPage.class.getName())) {
                return true;
            }
        }
        return false;
    }

    static void animateSlideBack() {

        // Ensure ImageViews are visible before animating
        timerTextView.setVisibility(View.VISIBLE);
        clockBoard.setVisibility(View.VISIBLE);
        scoreBoard.setVisibility(View.VISIBLE);
        scoreTextView.setVisibility(View.VISIBLE);
        backgroundMusicImageView.setVisibility(View.VISIBLE);
        sfxMusicImageView.setVisibility(View.VISIBLE);

        // Calculate the width of the screen
        int screenHeight = -250;

        // Set initial translation values for each ImageView
        timerTextView.setTranslationY(screenHeight); //
        clockBoard.setTranslationY(screenHeight);
        scoreBoard.setTranslationY(screenHeight);
        scoreTextView.setTranslationY(screenHeight);
        backgroundMusicImageView.setTranslationY(screenHeight);
        sfxMusicImageView.setTranslationY(screenHeight);

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator timerTextViewAnim = ObjectAnimator.ofFloat(timerTextView, "translationY", 0f);
        timerTextViewAnim.setInterpolator(new DecelerateInterpolator());
        timerTextViewAnim.setDuration(800);

        ObjectAnimator clockBoardAnim = ObjectAnimator.ofFloat(clockBoard, "translationY", 0f);
        clockBoardAnim.setInterpolator(new DecelerateInterpolator());
        //startButtonAnim.setStartDelay(100);
        clockBoardAnim.setDuration(800);

        ObjectAnimator scoreBoardAnim = ObjectAnimator.ofFloat(scoreBoard, "translationY", 0f);
        scoreBoardAnim.setInterpolator(new DecelerateInterpolator());
        scoreBoardAnim.setDuration(800);

        ObjectAnimator scoreTextViewAnim = ObjectAnimator.ofFloat(scoreTextView, "translationY", 0f);
        scoreTextViewAnim.setInterpolator(new DecelerateInterpolator());
        scoreTextViewAnim.setDuration(800);

        ObjectAnimator backgroundMusicAnim = ObjectAnimator.ofFloat(backgroundMusicImageView, "translationY", 0f);
        backgroundMusicAnim.setInterpolator(new DecelerateInterpolator());
        backgroundMusicAnim.setDuration(800);

        ObjectAnimator sfxMusicAnim = ObjectAnimator.ofFloat(sfxMusicImageView, "translationY", 0f);
        sfxMusicAnim.setInterpolator(new DecelerateInterpolator());
        sfxMusicAnim.setDuration(800);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(timerTextViewAnim, clockBoardAnim, scoreBoardAnim, scoreTextViewAnim, backgroundMusicAnim, sfxMusicAnim);
        animatorSet.start();
    }

    static void animateSlideOut() {

        // Calculate the width of the screen
        int screenHeight = -250;

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator timerTextViewAnim = ObjectAnimator.ofFloat(timerTextView, "translationY", screenHeight);
        timerTextViewAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        timerTextViewAnim.setDuration(1100);

        ObjectAnimator clockBoardAnim = ObjectAnimator.ofFloat(clockBoard, "translationY", screenHeight);
        clockBoardAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        //startButtonAnim.setStartDelay(100);
        clockBoardAnim.setDuration(1100);

        ObjectAnimator scoreBoardAnim = ObjectAnimator.ofFloat(scoreBoard, "translationY", screenHeight);
        scoreBoardAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreBoardAnim.setDuration(1100);

        ObjectAnimator scoreTextViewAnim = ObjectAnimator.ofFloat(scoreTextView, "translationY", screenHeight);
        scoreTextViewAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scoreTextViewAnim.setDuration(1100);

        ObjectAnimator backgroundMusicAnim = ObjectAnimator.ofFloat(backgroundMusicImageView, "translationY", screenHeight);
        backgroundMusicAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        backgroundMusicAnim.setDuration(1100);

        ObjectAnimator sfxMusicAnim = ObjectAnimator.ofFloat(sfxMusicImageView, "translationY", screenHeight);
        sfxMusicAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        sfxMusicAnim.setDuration(1100);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(timerTextViewAnim, clockBoardAnim, scoreBoardAnim, scoreTextViewAnim, backgroundMusicAnim, sfxMusicAnim);
        animatorSet.start();
    }

    static void animatePopOutBullets() { // Animation
        bulletsImageView.setVisibility(View.VISIBLE);
        bulletsTextView.setVisibility(View.VISIBLE);

        bulletsImageView.setScaleX(0.0f);
        bulletsImageView.setScaleY(0.0f);
        bulletsTextView.setScaleX(0.0f);
        bulletsTextView.setScaleY(0.0f);

        bulletsImageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();

        bulletsTextView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }

    static void animatePopInBullets() { // Animation

        bulletsImageView.setVisibility(View.VISIBLE);
        bulletsTextView.setVisibility(View.VISIBLE);

        bulletsImageView.setScaleX(1.0f);
        bulletsImageView.setScaleY(1.0f);
        bulletsTextView.setScaleX(1.0f);
        bulletsTextView.setScaleY(1.0f);

        bulletsImageView.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(0.0f))
                .start();

        bulletsTextView.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(0.0f))
                .start();
    }

    static void animateSlideBackGun() {

        // Ensure ImageViews are visible before animating
        gun.setVisibility(View.VISIBLE);
        gunShell.setVisibility(View.VISIBLE);

        // Set initial translation values for each ImageView
        gun.setTranslationX(gun.getWidth()); //
        gunShell.setTranslationX(gunShell.getWidth() + gun.getWidth());

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator gunAnim = ObjectAnimator.ofFloat(gun, "translationX", 0f);
        gunAnim.setInterpolator(new DecelerateInterpolator());
        gunAnim.setDuration(800);

        ObjectAnimator gunShellAnim = ObjectAnimator.ofFloat(gunShell, "translationX", 200);
        gunShellAnim.setInterpolator(new DecelerateInterpolator());
        gunShellAnim.setDuration(800);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(gunAnim, gunShellAnim);
        animatorSet.start();
    }

    static void animateSlideOutGun() {

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator gunAnim = ObjectAnimator.ofFloat(gun, "translationX", gun.getWidth());
        gunAnim.setInterpolator(new DecelerateInterpolator());
        gunAnim.setDuration(1100);

        ObjectAnimator gunShellAnim = ObjectAnimator.ofFloat(gunShell, "translationX", gunShell.getWidth() + gun.getWidth() + 200);
        gunShellAnim.setInterpolator(new DecelerateInterpolator());
        gunShellAnim.setDuration(1100);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(gunAnim, gunShellAnim);
        animatorSet.start();
    }

    static void animatePopOutPlayer() {

        nickname.setVisibility(View.VISIBLE);
        existingPlayerName.setVisibility(View.VISIBLE);

        // Apply the scale animation using ViewPropertyAnimator
        nickname.setScaleX(0.0f);
        nickname.setScaleY(0.0f);
        nickname.animate()
                .setStartDelay(150)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();

        existingPlayerName.setScaleX(0.0f);
        existingPlayerName.setScaleY(0.0f);
        existingPlayerName.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }

    private void animatePopInPlayer() {
        // Apply the scale animation using ViewPropertyAnimator
        nickname.setScaleX(1.0f);
        nickname.setScaleY(1.0f);
        nickname.animate()
                .setStartDelay(50)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(0.0f))
                .start();

        existingPlayerName.setScaleX(1.0f);
        existingPlayerName.setScaleY(1.0f);
        existingPlayerName.animate()
                .scaleX(0.0f)
                .scaleY(0.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(0.0f))
                .start();
    }

    private void animateSlideInCurtain() {

        // Ensure ImageViews are visible before animating
        closingCurtainRight.setVisibility(View.VISIBLE);
        closingCurtainLeft.setVisibility(View.VISIBLE);

        // Set initial translation values for each ImageView
        closingCurtainRight.setTranslationX(-closingCurtainRight.getWidth()); //
        closingCurtainLeft.setTranslationX(closingCurtainLeft.getWidth());

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator closingCurtainRightAnim = ObjectAnimator.ofFloat(closingCurtainRight, "translationX", 0f);
        closingCurtainRightAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainRightAnim.setDuration(1100);

        ObjectAnimator closingCurtainLeftAnim = ObjectAnimator.ofFloat(closingCurtainLeft, "translationX", 0f);
        closingCurtainLeftAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainLeftAnim.setDuration(1100);


        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(closingCurtainRightAnim, closingCurtainLeftAnim);
        animatorSet.start();
    }

    static void animateSlideOutCurtain() {

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator closingCurtainRightAnim = ObjectAnimator.ofFloat(closingCurtainRight, "translationX", -closingCurtainRight.getWidth());
        closingCurtainRightAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainRightAnim.setDuration(1000);

        ObjectAnimator closingCurtainLeftAnim = ObjectAnimator.ofFloat(closingCurtainLeft, "translationX", closingCurtainLeft.getWidth());
        closingCurtainLeftAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainLeftAnim.setDuration(1000);


        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(closingCurtainRightAnim, closingCurtainLeftAnim);
        animatorSet.start();

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animateReady();
            }
        });
    }

    static void animateSlideOutCurtainReturn() {

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator closingCurtainRightAnim = ObjectAnimator.ofFloat(closingCurtainRight, "translationX", -closingCurtainRight.getWidth());
        closingCurtainRightAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainRightAnim.setDuration(1500);

        ObjectAnimator closingCurtainLeftAnim = ObjectAnimator.ofFloat(closingCurtainLeft, "translationX", closingCurtainLeft.getWidth());
        closingCurtainLeftAnim.setInterpolator(new DecelerateInterpolator());
        closingCurtainLeftAnim.setDuration(1500);


        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(closingCurtainRightAnim, closingCurtainLeftAnim);
        animatorSet.start();
    }

    private void startWaveAnimation(ImageView imageView, float distanceToMove, long duration) {
        Animation animation = new TranslateAnimation(0, distanceToMove, 0, 0);
        animation.setDuration(duration);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(animation);
    }

    private void startClockShakeAnimation() {
        ImageView clockBoard = findViewById(R.id.clockBoard);
        TextView clockSecond = findViewById(R.id.timerTextView);

        // Shake animation for clockBoard
        ObjectAnimator shakeBoardAnimator = ObjectAnimator.ofFloat(clockBoard, "rotation", -13, 13);
        shakeBoardAnimator.setDuration(80);
        shakeBoardAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        shakeBoardAnimator.setRepeatCount(8);

        // Shake animation for clockSecond
        ObjectAnimator shakeSecondAnimator = ObjectAnimator.ofFloat(clockSecond, "rotation", -13, 13);
        shakeSecondAnimator.setDuration(80);
        shakeSecondAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        shakeSecondAnimator.setRepeatCount(8);

        // Reset animation for clockBoard
        ObjectAnimator resetBoardAnimator = ObjectAnimator.ofFloat(clockBoard, "rotation", 0);
        resetBoardAnimator.setDuration(80);

        // Reset animation for clockSecond
        ObjectAnimator resetSecondAnimator = ObjectAnimator.ofFloat(clockSecond, "rotation", 0);
        resetSecondAnimator.setDuration(80);

        // Animator set to play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(shakeBoardAnimator, shakeSecondAnimator);
        animatorSet.play(resetBoardAnimator).with(resetSecondAnimator).after(shakeBoardAnimator);

        animatorSet.start();
    }

    private void animateGun() {
        gun = findViewById(R.id.gun);

        // Store the initial position if not already stored
        if (gunPositionX == 0 && gunPositionY == 0) {
            gunPositionX = gun.getTranslationX();
            gunPositionY = gun.getTranslationY();
        }

        // Calculate the target position for the animation
        float targetTranslationX = gunPositionX + 20; // Adjust as needed
        float targetTranslationY = gunPositionY + 20; // Adjust as needed

        // Create ObjectAnimator for moving bottom-right
        ObjectAnimator moveAnimator = ObjectAnimator.ofPropertyValuesHolder(
                gun,
                PropertyValuesHolder.ofFloat("translationX", targetTranslationX),
                PropertyValuesHolder.ofFloat("translationY", targetTranslationY)
        );
        moveAnimator.setDuration(200); // Adjust duration as needed
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Change src during move animation if needed
                gun.setImageResource(R.drawable.a_gunfire); // Change to the desired drawable resource
            }
        });

        // Create ObjectAnimator for moving back to original position
        ObjectAnimator resetAnimator = ObjectAnimator.ofPropertyValuesHolder(
                gun,
                PropertyValuesHolder.ofFloat("translationX", gunPositionX),
                PropertyValuesHolder.ofFloat("translationY", gunPositionY)
        );
        resetAnimator.setDuration(200); // Adjust duration as needed
        resetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // Change src during reset animation if needed
                gun.setImageResource(R.drawable.a_gun); // Change to the original drawable resource
            }
        });

        // Animator set to play animations sequentially
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(moveAnimator, resetAnimator);

        // Start the animation
        animatorSet.start();
    }
    private void animateGunShell() {
        gunShell = findViewById(R.id.gunShell);

        // Store the initial position if not already stored
        if (initialPositionX == 0 && initialPositionY == 0) {
            initialPositionX = gunShell.getX();
            initialPositionY = gunShell.getY();
        }

        // Reset the translation values before starting the animation
        gunShell.setX(initialPositionX);
        gunShell.setY(initialPositionY);

        // Create ObjectAnimator for curving motion
        ObjectAnimator curveAnimator = ObjectAnimator.ofPropertyValuesHolder(
                gunShell,
                PropertyValuesHolder.ofFloat("translationX", initialPositionX, initialPositionX + 90), // Adjust as needed
                PropertyValuesHolder.ofFloat("translationY", initialPositionY, initialPositionY - 50), // Adjust as needed
                PropertyValuesHolder.ofFloat("rotation", 0, 60) // Rotate 45 degrees to the right
        );
        curveAnimator.setDuration(300); // Adjust duration as needed

        // Start the animation
        curveAnimator.start();
    }


    private void initialLoopingAnimations() {

        int speed = calculateAnimationSpeed();
        animators = new ValueAnimator[targetImageViews.length];

        for (int i = 0; i < targetImageViews.length; i++) {

            isTargetHit[i] = false;
            final ImageView targetImageView = targetImageViews[i];

            final int finalIndex = i; // Create a final variable for the current index

            // Get a random X position within the screen width
            int randomX = new Random().nextInt(screenWidth - targetImageView.getWidth());

            // Determine the direction based on the initial position
            float initialX = (randomX < 260) ? -targetImageView.getWidth() : screenWidth;

            // Set the initial position of the target before starting animations
            targetImageView.setTranslationX(initialX);

            // Set the drawable resource and tag for the target image view
            String drawableName;
            if (i < 6) {
                boolean moveLeft = randomX < 260; // Determine the direction based on the initial position
                drawableName = getRandomDrawable(finalIndex, moveLeft);
            } else {
                drawableName = getRandomDrawableForTargets(finalIndex);
            }

            int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
            targetImageView.setImageResource(resID);
            targetImageView.setTag(drawableName); // Store the drawable name as a tag

            // Start the initial sliding up animation
            ValueAnimator initialSlideUpAnimator = ValueAnimator.ofFloat(slideUpSpeed, 0);
            initialSlideUpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) valueAnimator.getAnimatedValue();
                    targetImageView.setTranslationY(value);
                    targetImageView.requestLayout();
                }
            });
            initialSlideUpAnimator.setDuration(800); // You can adjust the duration as needed
            initialSlideUpAnimator.start();

            animators[i] = ValueAnimator.ofFloat(randomX, (initialX == -targetImageView.getWidth()) ? screenWidth : -targetImageView.getWidth());

            animators[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float value = (float) animators[finalIndex].getAnimatedValue();
                    targetImageView.setTranslationX(value);
                    targetImageView.requestLayout();

                    // Check if the target image reaches the end of the screen
                    float targetWidth = targetImageView.getWidth();
                    if ((value >= screenWidth - targetWidth && initialX == -targetWidth) ||
                            (value <= -targetWidth && initialX == screenWidth)) {
                        // If it reaches the end, stop the current animator
                        animators[finalIndex].cancel();

                        // Reset the position of the specific target to a new random X
                        int newRandomX = new Random().nextInt(screenWidth - targetImageView.getWidth());
                        targetImageView.setTranslationX(newRandomX);

                        // Determine the new direction based on the new initial position
                        float newInitialX = (newRandomX < 360) ? -targetImageView.getWidth() : screenWidth;

                        // Start the looping animation again for the specific target
                        startLoopingAnimation(finalIndex);
                    }
                }
            });
            animators[i].setDuration(speed);
            animators[i].start();
        }
    }
    private void startLoopingAnimation(int targetIndex) {

        int speed = calculateAnimationSpeed();

        String drawableName;
        // Get a random X position within the screen width
        int randomX = new Random().nextInt(screenWidth - targetImageViews[targetIndex].getWidth());
        boolean moveLeft = randomX < 360; // Adjust the threshold as needed

        if (targetIndex < 6) {
            drawableName = getRandomDrawable(targetIndex, moveLeft);
        } else {
            drawableName = getRandomDrawableForTargets(targetIndex);
        }
        int resID = getResources().getIdentifier(drawableName, "drawable", getPackageName());
        targetImageViews[targetIndex].setImageResource(resID);
        targetImageViews[targetIndex].setTag(drawableName); // Store the drawable name as a tag

        // Initialize the arrays for this targetIndex
        initialSlideUpAnimators[targetIndex] = new ValueAnimator();
        bounceAnimators[targetIndex] = new ValueAnimator();

        // Start the initial sliding up animation
        initialSlideUpAnimators[targetIndex] = ValueAnimator.ofFloat(slideUpSpeed, 0);
        initialSlideUpAnimators[targetIndex].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                targetImageViews[targetIndex].setTranslationY(value);
                targetImageViews[targetIndex].requestLayout();
            }
        });
        initialSlideUpAnimators[targetIndex].setDuration(800); // You can adjust the duration as needed
        // Reset the hit status of the specific target
        new Handler().postDelayed(() -> {
            isTargetHit[targetIndex] = false;
        }, 780);

        // Bouncing effect synchronized with sliding up animation
        bounceAnimators[targetIndex] = ValueAnimator.ofFloat(0, 30); // Adjust amplitude as needed
        bounceAnimators[targetIndex].setRepeatCount(ValueAnimator.INFINITE);
        bounceAnimators[targetIndex].setRepeatMode(ValueAnimator.REVERSE);
        bounceAnimators[targetIndex].setDuration(600); // Adjust bouncing speed as needed
        bounceAnimators[targetIndex].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                targetImageViews[targetIndex].setTranslationY(value);
                targetImageViews[targetIndex].requestLayout();
            }
        });

        initialSlideUpAnimators[targetIndex].addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Start the bouncing effect after the sliding up animation ends
                bounceAnimators[targetIndex].start();
            }
        });

        initialSlideUpAnimators[targetIndex].start();

        // Determine the direction based on the initial position
        float initialX = (randomX < 360) ? -targetImageViews[targetIndex].getWidth() : screenWidth;

        // Start the looping animation again for the specific target after the initial sliding up motion
        animators[targetIndex] = ValueAnimator.ofFloat(randomX, (initialX == -targetImageViews[targetIndex].getWidth()) ? screenWidth : -targetImageViews[targetIndex].getWidth());
        animators[targetIndex].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
                float value = (float) animators[targetIndex].getAnimatedValue();
                targetImageViews[targetIndex].setTranslationX(value);
                targetImageViews[targetIndex].requestLayout();

                // Check if the target image reaches the end of the screen
                float targetWidth = targetImageViews[targetIndex].getWidth();
                if ((value >= screenWidth - targetWidth && initialX == -targetWidth) ||
                        (value <= -targetWidth && initialX == screenWidth)) {

                    if (timer <= 0 || (totalBullets <= 0 && bullets == 0) || score <= -200 ) {
                        animators[targetIndex].cancel();
                        bounceAnimators[targetIndex].cancel();
                        return;
                    }

                    // If it reaches the end, stop the current animator and bouncing effect
                    animators[targetIndex].cancel();
                    bounceAnimators[targetIndex].cancel();

                    // Reset the position of the specific target to a new random X
                    int newRandomX = new Random().nextInt(screenWidth - targetImageViews[targetIndex].getWidth());
                    targetImageViews[targetIndex].setTranslationX(newRandomX);

                    // Determine the new direction based on the new initial position
                    float newInitialX = (newRandomX < 360) ? -targetImageViews[targetIndex].getWidth() : screenWidth;

                    // Start the looping animation again for the specific target
                    startLoopingAnimation(targetIndex);
                }
            }
        });
        // Start the looping animation
        animators[targetIndex].setDuration(speed);
        animators[targetIndex].start();
    }
    private String getRandomDrawable(int targetIndex, boolean moveLeft) {
        String[] drawables = {
                "d1_avoid_l", "d1_avoid_r", "d1_target_l", "d1_target_r",
                "d2_avoid_l", "d2_avoid_r", "d2_target_l", "d2_target_r",
                "d3_avoid_l", "d3_avoid_r", "d3_target_l", "d3_target_r",
                "s1_avoid_l", "s1_avoid_r", "s1_target_l", "s1_target_r"
        };

        // Adjust the probabilities for selecting target and avoid drawables
        double targetProbability = 0.7; // Probability of selecting a target drawable

        Random random = new Random();
        double randomValue = random.nextDouble();

        // Check if the randomly generated value falls within the target probability
        if (randomValue < targetProbability) {
            // Select a random target drawable
            List<String> targetDrawables = Arrays.asList(drawables).stream()
                    .filter(drawable -> drawable.contains("target"))
                    .filter(drawable -> (moveLeft && drawable.endsWith("_r")) || (!moveLeft && drawable.endsWith("_l")))
                    .collect(Collectors.toList());
            return targetDrawables.get(random.nextInt(targetDrawables.size()));
        } else {
            // Select a random avoid drawable
            List<String> avoidDrawables = Arrays.asList(drawables).stream()
                    .filter(drawable -> drawable.contains("avoid"))
                    .filter(drawable -> (moveLeft && drawable.endsWith("_r")) || (!moveLeft && drawable.endsWith("_l")))
                    .collect(Collectors.toList());
            return avoidDrawables.get(random.nextInt(avoidDrawables.size()));
        }
    }
    private String getRandomDrawableForTargets(int targetIndex) {
        String[] drawables = {
                "m1_avoid", "m2_target", "m2_target", "m3_target", "m3_avoid"
        };

        // Adjust the probabilities for selecting target and avoid drawables
        double targetProbability = 0.7; // Probability of selecting a target drawable

        Random random = new Random();
        double randomValue = random.nextDouble();

        // Check if the randomly generated value falls within the target probability
        if (randomValue < targetProbability) {
            // Select a random target drawable
            List<String> targetDrawables = Arrays.asList(drawables).stream()
                    .filter(drawable -> drawable.contains("target"))
                    .collect(Collectors.toList());
            return targetDrawables.get(random.nextInt(targetDrawables.size()));
        } else {
            // Select a random avoid drawable
            List<String> avoidDrawables = Arrays.asList(drawables).stream()
                    .filter(drawable -> drawable.contains("avoid"))
                    .collect(Collectors.toList());
            return avoidDrawables.get(random.nextInt(avoidDrawables.size()));
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!areTargetsTargetable) {
            // Targets are not targetable yet, do nothing
            return super.onTouchEvent(event);
        }

        if (isReloading) {
            // Ignore touch events during reloading animation
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (bullets > 0) { // Check if there are bullets remaining
                    // Record the first touch location
                    float touchX = event.getX();
                    float touchY = event.getY();

                    // Animate gun
                    animateGun();
                    animateGunShell();

                    // Sfx Gunshot
                    if (sfxGunshot.isPlaying()) {
                        sfxGunshot.seekTo(0); // Rewind the sound effect to the beginning
                    } else {
                        sfxGunshot.start(); // Play the sound effect
                    }

                    // Show hit marker slightly above the touch point
                    hitMarkerImageView.setX(touchX - (float) hitMarkerImageView.getWidth() / 2);
                    hitMarkerImageView.setY(touchY - (float) hitMarkerImageView.getHeight() + 38);
                    hitMarkerImageView.setVisibility(View.VISIBLE);

                    // Hide hit marker after a short delay (you can adjust the delay as needed)
                    new Handler().postDelayed(() -> hitMarkerImageView.setVisibility(View.INVISIBLE), 500); // Delay in milliseconds

                    // Check if the touch event is within any of the target bounds
                    boolean targetHit = false;
                    for (int i = 0; i < targetImageViews.length; i++) {
                        if (isTouchInsideTarget(targetImageViews[i], touchX, touchY)) {
                            // Handle target hit
                            handleTargetHit(i);
                            targetHit = true;
                            break; // Break out of the loop after handling the hit for one target
                        }
                    }

                    // If no target was hit, display "Missed"
                    if (!targetHit) {
                        showScoreUpdate("Missed", Color.RED, event.getX() - 80, event.getY());
                    }

                    // Decrease the bullet count
                    bullets--;
                    updateBulletsImageView(); // Update bullets ImageView
                } else {
                    // No bullets left
                    showScoreUpdate("No Bullets", Color.RED, event.getX() - 128, event.getY());
                    // Sfx Gunshot
                    if (sfxNoBullets.isPlaying()) {
                        sfxNoBullets.seekTo(0); // Rewind the sound effect to the beginning
                    } else {
                        sfxNoBullets.start(); // Play the sound effect
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // Reset the touch location on touch up
                // No action needed here
                break;
        }
        return super.onTouchEvent(event);
    }
    private void showScoreUpdate(String message, int color, float x, float y) {
        scoreUpdate.setText(message);
        scoreUpdate.setTextColor(color);
        scoreUpdate.setX(x);
        scoreUpdate.setY(y);
        scoreUpdate.setVisibility(View.VISIBLE);

        // Create animation for sliding up and fading out
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 0, -75);
        slideUp.setDuration(750);
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(750);

        // Combine animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(slideUp);
        animationSet.addAnimation(fadeOut);

        // Apply animation to scoreUpdate TextView
        scoreUpdate.startAnimation(animationSet);

        // Set a delay to make the TextView invisible after animation completes
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, make the TextView invisible
                scoreUpdate.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });
    }
    private void showTimeUpdate(String message, int color, float x, float y) {
        timeUpdate.setText(message);
        timeUpdate.setTextColor(color);
        timeUpdate.setX(x);
        timeUpdate.setY(y);
        timeUpdate.setVisibility(View.VISIBLE);

        // Create animation for sliding up and fading out
        TranslateAnimation slideUp = new TranslateAnimation(0, 0, 50, -75);
        slideUp.setDuration(750);
        AlphaAnimation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(750);

        // Combine animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(slideUp);
        animationSet.addAnimation(fadeOut);

        // Apply animation to scoreUpdate TextView
        timeUpdate.startAnimation(animationSet);

        // Set a delay to make the TextView invisible after animation completes
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, make the TextView invisible
                timeUpdate.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });
    }
    private void handleScoreUpdate(int scoreChange, float x, float y) {
        int color;
        String message;

        if (scoreChange < 0) {
            color = Color.RED;
            message = " " + scoreChange; // Display negative score change
        } else if (scoreChange == 0) {
            color = Color.RED;
            message = "Missed" + scoreChange; // Display missed the target
        } else {
            color = Color.GREEN;
            message = "+" + scoreChange; // Display positive score change
        }

        showScoreUpdate(message, color, x, y);
    }
    private void handleTimeUpdate(int timeChange, float x, float y) {
        if (timeChange != 0) { // Check if time is changing
            int color;
            String message;

            if (timeChange < 0) {
                color = Color.RED;
                message = " " + timeChange + " sec"; // Display negative time change
            } else {
                color = Color.GREEN;
                message = "+" + timeChange + " sec"; // Display positive time change
            }

            showTimeUpdate(message, color, x, y);
        }
    }
    private static void updateBulletsImageView() {
        String drawableName = "a_bullet" + bullets;
        int resID = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
        bulletsImageView.setImageResource(resID);

        // Update reloadTxt visibility
        if (bullets == 0 && totalBullets > 0) {
            reloadTxt.setVisibility(View.VISIBLE);
            animateReloadText(); // Call animateReloadText when bullets count reaches zero
        } else if (totalBullets <= 0) {
            reloadTxt.setVisibility(View.INVISIBLE);
            reloadTxt.clearAnimation(); // Stop animation when bullets count is not zero
        } else {
            reloadTxt.setVisibility(View.INVISIBLE);
            reloadTxt.clearAnimation(); // Stop animation when bullets count is not zero
        }
    }

    private static void animateReloadText() {
        // Create a fade in animation
        AlphaAnimation fadeInAnimation = new AlphaAnimation(0f, 1f);
        fadeInAnimation.setDuration(500); // Duration for fading in

        // Create a scale animation to zoom in and out
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500); // Duration for each half of the animation
        scaleAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation
        scaleAnimation.setRepeatCount(Animation.INFINITE); // Repeat indefinitely

        // Create an animation set to combine both animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeInAnimation);
        animationSet.addAnimation(scaleAnimation);

        reloadTxt.startAnimation(animationSet); // Start the animation
    }
    public static void reloadBullets() {
        // Set reloading flag to true
        isReloading = true;

        // Calculate how many bullets to reload
        int bulletsToReload = Math.min(8 - bullets, totalBullets);

        if (bulletsToReload > 0) {
            // Update totalBullets
            totalBullets -= bulletsToReload;
            bulletsTextView.setText(String.valueOf(totalBullets));

            // Create a ValueAnimator to gradually increase the bullet count to 8
            ValueAnimator animator = ValueAnimator.ofInt(bullets, bullets + bulletsToReload);
            animator.setDuration(bulletsToReload * 100); // Adjust duration based on bulletsToReload

            // Update the bullets count on each animation frame
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    // Get the current value of the animation
                    int newValue = (int) valueAnimator.getAnimatedValue();

                    // Update the bullet count
                    bullets = newValue;

                    // Update the bullets ImageView based on the bullet count
                    updateBulletsImageView();
                }
            });

            // Set listener to reset reloading flag when animation ends
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // Set reloading flag to false
                    isReloading = false;
                }
            });

            // Start the animator
            animator.start();
        } else if (totalBullets <= 0 && bullets == 0) {
            isNoAmmo = true;
            reloadTxt.setVisibility(View.INVISIBLE);
            reloadTxt.clearAnimation(); // Stop animation when bullets count is not zero
        } else {
            // Set reloading flag to false
            isReloading = false;
            // Inform the user that bullets are already at maximum
            //Toast.makeText(MainActivity.this, "Bullets already at maximum (8)", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isTouchInsideTarget(ImageView targetImageView, float touchX, float touchY) {
        if (!areTargetsTargetable) {
            // Targets are not targetable yet, so return false
            return false;
        }

        Rect targetRect = new Rect();
        targetImageView.getGlobalVisibleRect(targetRect);

        // Adjust the target bounds to consider only the top half
        targetRect.bottom -= ((targetImageView.getHeight() / 2) - 30);

        return targetRect.contains((int) touchX, (int) touchY);
    }
    private void handleTargetHit(int targetIndex) {
        // Check if the target is already hit
        if (isTargetHit[targetIndex]) {
            return; // If already hit, do nothing
        }

        // Mark the target as hit and disable it temporarily
        isTargetHit[targetIndex] = true;
        isTargetSlidingUp[targetIndex] = true;

        Object tag = targetImageViews[targetIndex].getTag();
        if (tag != null) {
            String drawableName = tag.toString();

            boolean scoringSystem = drawableName.startsWith("d1") || drawableName.startsWith("d2") ||
                    drawableName.startsWith("d3") || drawableName.startsWith("s1");

            // SFX Sound
            if (drawableName.startsWith("d") && drawableName.contains("avoid")) {
                if (sfxDuck.isPlaying()) {
                    sfxDuck.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxDuck.start(); // Play the sound effect
                }
            } else if (drawableName.startsWith("m1") && drawableName.contains("avoid")) {
                if (sfxMetal.isPlaying()) {
                    sfxMetal.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxMetal.start(); // Play the sound effect
                }
            } else if (drawableName.startsWith("m3") && drawableName.contains("avoid")) {
                if (sfxCow.isPlaying()) {
                    sfxCow.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxCow.start(); // Play the sound effect
                }
            } else if (drawableName.startsWith("s1") && drawableName.contains("avoid")) {
                if (sfxWater.isPlaying()) {
                    sfxWater.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxWater.start(); // Play the sound effect
                }
            } else {
                if (sfxTincan.isPlaying()) {
                    sfxTincan.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    sfxTincan.start(); // Play the sound effect
                }
            }

            int scoreChange = 0; // Initialize score change
            int timeChange = 0; // Initialize time change

            if (drawableName.contains("avoid")) {
                // Randomly score from score or 1 from timer
                if (scoringSystem) {
                    scoreChange = -30;
                    score -= 30;
                } else {
                    scoreChange = -50;
                    score -= 50;
                }
                if (score % 100 == 0) {
                    timeChange = -1;
                    timer--;
                }
            } else if (drawableName.contains("target")) {
                // Increase score and adjust timer every 10 hits
                if (scoringSystem) {
                    scoreChange = 20;
                    score += 20;
                } else {
                    scoreChange = 30;
                    score += 30;
                }
                if (score % 100 == 0) {
                    timeChange = +1;
                    timer += 1;
                }
            }

            // Update the score TextView and timer TextView
            scoreTextView.setText("" + score);

            if (score < 0) {
                scoreTextView.setTextColor(Color.RED);
            } else {
                scoreTextView.setTextColor(Color.WHITE);
            }

            timerTextView.setText("" + timer);

            // Call handleScoreUpdate with the calculated score change
            handleScoreUpdate(scoreChange, targetImageViews[targetIndex].getX(), targetImageViews[targetIndex].getY());
            // Call handleTimeUpdate with the calculated time change
            handleTimeUpdate(timeChange, targetImageViews[targetIndex].getX(), targetImageViews[targetIndex].getY());
        } else {
            // Handle null drawable name gracefully
            Log.e("TargetHit", "Drawable name is null");
        }

        if (score % 1000 == 0) {
            // Decrease the animation duration by 300
            int currentDuration = (int) animators[targetIndex].getDuration();
            int newDuration = currentDuration - 300;
            animators[targetIndex].setDuration(Math.max(newDuration, 500)); // Ensure the duration is at least 500
        }

        // Check if the animators are not null before canceling them
        if (animators[targetIndex] != null) {
            animators[targetIndex].cancel();
        }
        if (initialSlideUpAnimators[targetIndex] != null) {
            initialSlideUpAnimators[targetIndex].cancel();
        }
        if (bounceAnimators[targetIndex] != null) {
            bounceAnimators[targetIndex].cancel();
        }

        // Slide down animation for the hit target
        float initialY = targetImageViews[targetIndex].getTranslationY(); // Get the initial translationY
        ValueAnimator slideDownAnimator = ValueAnimator.ofFloat(initialY, initialY + targetImageViews[targetIndex].getHeight());
        slideDownAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                targetImageViews[targetIndex].setTranslationY(value);
                targetImageViews[targetIndex].requestLayout();
                // Check if the slide down animation is completed
                if (value >= initialY + targetImageViews[targetIndex].getHeight()) {
                    // Reset the flag for this target
                    isTargetSlidingUp[targetIndex] = false;
                }
            }
        });
        slideDownAnimator.setDuration(500); // Adjust the duration as needed
        slideDownAnimator.start();

        // Delay to restart the looping animation after sliding down
        new Handler().postDelayed(() -> {
            // Reset the position of the specific target
            targetImageViews[targetIndex].setTranslationX(-targetImageViews[targetIndex].getWidth());

            // Start the looping animation again for the specific target
            if (totalBullets <= 0 || timer == 0) {
                return;
            } else {
                startLoopingAnimation(targetIndex);
            }

        }, 350); // Adjust the delay as needed
    }
    private int calculateAnimationSpeed() {
        // Set the base speed to 4000
        int baseSpeed = 5500;
        // Decrease the speed by 300 for every 1000 points scored
        int scoreMultiplier = score / 1000;
        int adjustedSpeed = baseSpeed - (scoreMultiplier * 300);
        // Ensure the speed does not go below 500
        return Math.max(adjustedSpeed, 500);
    }
    private void startTimer() {
        if (isTimerRunning) {
            return; // Timer is already running, do nothing
        }
        isTimerRunning = true; // Set the timer running flag

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timer > 0 && !(totalBullets <= 0 && bullets == 0) && !(score <= -200)) {
                    timer--;
                    timerTextView.setText("" + timer);
                    timerHandler.postDelayed(this, 1000); // Schedule the next iteration after 1 second
                } else { endGame();
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000); // Start the timer loop with a delay of 1 second
    }
    // Method to end the timer anytime you want
    private void stopAnimations() {
        for (ValueAnimator animator : animators) {
            if (animator != null) {
                animator.cancel();
            }
        }
        for (ValueAnimator animator : initialSlideUpAnimators) {
            if (animator != null) {
                animator.cancel();
            }
        }
        for (ValueAnimator animator : bounceAnimators) {
            if (animator != null) {
                animator.cancel();
            }
        }
        // waveImageView1.clearAnimation();
        // waveImageView2.clearAnimation();
        // waveImageView3.clearAnimation();
    }

    public void endGame() {
        startClockShakeAnimation();

        animateSlideInCurtain();
        animateSlideOut();
        animatePopInBullets();
        animateSlideOutGun();
        animatePopInPlayer();

        sfxBell.start(); // Play the end sound effect
        timerHandler.removeCallbacks(timerRunnable);
        isReloading = true;

        bgInGameLoop.stop();
        // bgInGameLoop.seekTo(0); // Seek to the beginning
        // bgInGameLoop.start(); // Start playback

        areTargetsTargetable = false;
        isTimerRunning = false;
        isNoAmmo = false;

        timerTextView.setText("0");

        findViewById(R.id.reloadTxt).setVisibility(View.INVISIBLE);
        findViewById(R.id.reloadTxt).clearAnimation(); // Stop animation when bullets count is not zero

        // stopAnimations();
        // resetValues();

        updateHighScore(existingPlayerName.getText().toString().toUpperCase(), score);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, GameOverPage.class);
                startActivity(intent);
            }
        }, 1100);
    }

    public static void resetValues() {

        bgInGameLoop.stop();
        scoreTextView.setTextColor(Color.WHITE);

        // Reset timer
        timer = 60; // Set your initial timer value here
        timerTextView.setText("" + timer);

        // Reset score
        score = 0;
        scoreTextView.setText("" + score);

        // Reset Bullets
        totalBullets = 8;
        bulletsTextView.setText(String.valueOf(totalBullets));
        reloadBullets();
        totalBullets = 160;
        bulletsTextView.setText(String.valueOf(totalBullets));
    }

    public static void insertNewPlayer(String name) {
        ContentValues values = new ContentValues();
        values.put(PlayerContract.PlayerEntry.COLUMN_NAME, name);
        values.put(PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE, 0); // Initial high score

        long newRowId = database.insert(PlayerContract.PlayerEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            // Failed to insert
        } else {
            // Insert successful
        }
    }

    public static boolean checkPlayerExists(String playerName) {
        // Query the database to check if the player exists
        Cursor cursor = database.query(
                PlayerContract.PlayerEntry.TABLE_NAME,
                null,
                PlayerContract.PlayerEntry.COLUMN_NAME + " = ?",
                new String[]{playerName},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateHighScore(String name, int newHighScore) {
        // Check if the player exists in the database
        boolean playerExists = checkPlayerExists(name);

        if (!playerExists) {
            // Player not found
            Toast.makeText(this, "Player not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the new high score is greater than the current high score
        int currentHighScore = getHighScore(name);
        if (newHighScore <= currentHighScore) {
            // Current score is not greater than the existing high score
            return;
        }

        // Update the high score
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_HIGH_SCORE, newHighScore);

        String selection = DatabaseHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };

        int count = database.update(
                DatabaseHelper.TABLE_PLAYERS,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            // Update successful
            // Toast.makeText(this, "New High Score: " + newHighScore, Toast.LENGTH_LONG).show();
            bt();
        } else {
            // Update failed
            Toast.makeText(this, "Failed to update high score", Toast.LENGTH_SHORT).show();
        }
    }

    private int getHighScore(String name) {
        int highScore = 0;

        String[] projection = {DatabaseHelper.COLUMN_HIGH_SCORE};
        String selection = DatabaseHelper.COLUMN_NAME + " = ?";
        String[] selectionArgs = {name};

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_PLAYERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            highScore = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HIGH_SCORE));
            cursor.close();
        }

        return highScore;
    }

    public void bt() {
        // Create an ImageView
        ImageView i = new ImageView(getApplicationContext());

        // Set the desired width and height in dp
        int widthInDp = 132;
        int heightInDp = 30;

        // Convert dp to pixels
        int widthInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp, getResources().getDisplayMetrics());
        int heightInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp, getResources().getDisplayMetrics());

        // Load the image using Glide
        RequestOptions options = new RequestOptions()
                .override(widthInPixels, heightInPixels) // Set desired width and height
                .centerInside(); // Scale image uniformly (maintain the image's aspect ratio)
        Glide.with(getApplicationContext())
                .load(R.drawable.ic_highscore)
                .apply(options)
                .into(i);

        // Create the Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(i);

        // Set the gravity to adjust the position of the Toast
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 75    ); // Adjust the position as needed

        // Show the Toast
        toast.show();
    }

    public static void animateTutorialUp() {
        View[] targets = {t_target1, t_target2, t_target3, t_target4, t_target5, t_target6, t_target7, t_target8, t_target9, t_target10};

        for (View target : targets) {
            target.setVisibility(View.VISIBLE);
            target.setTranslationY(target.getWidth());

            ObjectAnimator targetAnim = ObjectAnimator.ofFloat(target, "translationY", 0f);
            targetAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            targetAnim.setDuration(500);
            targetAnim.start();
        }
    }

    public static void animateTutorialDown() {
        AnimatorSet animatorSet = new AnimatorSet();
        View[] targets = {t_target1, t_target2, t_target3, t_target4, t_target5, t_target6, t_target7, t_target8, t_target9, t_target10};

        List<Animator> animators = new ArrayList<>();
        for (View target : targets) {
            ObjectAnimator targetAnim = ObjectAnimator.ofFloat(target, "translationY", target.getWidth() + 100);
            targetAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            targetAnim.setDuration(500);
            animators.add(targetAnim);
        }

        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                for (View target : targets) {
                    target.setVisibility(View.GONE);
                }
            }
        });
        animatorSet.start();
    }


    Boolean shouldAllowBack = false;
    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }
}