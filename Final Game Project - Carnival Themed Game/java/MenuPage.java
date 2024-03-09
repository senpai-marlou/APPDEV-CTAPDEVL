package com.loompa.tapandshoot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

@SuppressLint({"StaticFieldLeak", "ClickableViewAccessibility"})
public class MenuPage extends AppCompatActivity {

    public static ImageView backgroundMusicImageView;
    public static ImageView sfxMusicImageView;
    public static String EXISTINGPLAYER;
    private ImageView playButton;
    private ImageView leaderboardButton;
    private ImageView gameplayButton;

    private ImageView backBtn;
    private ImageView nicknameEditText;
    private EditText nicknamePlayer;
    private ImageView startButton;
    private ImageView existingPlayer;
    private ImageView newPlayer;
    public static ImageView exitButtonImageView;

    private boolean isExit = true;
    private boolean isStartOn = true;

    private boolean isExistingSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        // Make it Full Screen
        FullScreen.setFullscreen(getWindow());

        // Existing player | Temporary
        EXISTINGPLAYER = getIntent().getStringExtra("existingPlayerName");

        if (MainActivity.bgInGameLoop == null || !MainActivity.bgInGameLoop.isPlaying()) {
            MainActivity.bgInGameLoop = MediaPlayer.create(MenuPage.this, R.raw.bg_gamesound);
            MainActivity.bgInGameLoop.setLooping(true);
            if (MainActivity.isMusicMuted) {
                MainActivity.bgInGameLoop.setVolume(0.0f, 0.0f); // Mute
            } else {
                MainActivity.bgInGameLoop.setVolume(1.0f, 1.0f); // Unmute
            }
            MainActivity.bgInGameLoop.start();
        }

        // Play Button animation
        playButton = findViewById(R.id.playButton);
        playButton.setOnTouchListener(new View.OnTouchListener() {
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
                        playButton.setImageResource(R.drawable.a_play_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        playButton.setImageResource(R.drawable.a_play_on);
                        animatePlayToLeft(); // Play Button Animation
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                animateNicknameToLeft(); // Nickname Animation
                            }
                        }, 100);
                        if (isExistingSelected) {
                            existingPlayer.setClickable(false);
                        }
                        break;
                }
                return true; // Consume the event
            }
        });

        // Leaderboard Button animation
        leaderboardButton = findViewById(R.id.leaderboardButton);
        leaderboardButton.setOnTouchListener(new View.OnTouchListener() {
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
                        leaderboardButton.setImageResource(R.drawable.a_leaderboard_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MenuPage.this, LeaderboardPage.class);
                                intent.putExtra("existingPlayerName", MainActivity.existingPlayerName.getText().toString());
                                startActivity(intent);
                            }
                        }, 10);

                        leaderboardButton.setImageResource(R.drawable.a_leaderboard_on);
                        break;
                }
                return true; // Consume the event
            }
        });

        // Gameplay Button animation
        gameplayButton = findViewById(R.id.gameplayButton);
        gameplayButton.setOnTouchListener(new View.OnTouchListener() {
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
                        gameplayButton.setImageResource(R.drawable.a_gameplay_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        gameplayButton.setImageResource(R.drawable.a_gameplay_on);
                        MainActivity.isGameplay = true;
                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MenuPage.this, TutorialPage.class);
                                startActivity(intent);
                            }
                        }, 100);
                        break;
                }
                return true; // Consume the event
            }
        });

        // --------------------------------------------------------------------------------------------------

        // Retrieve the value of isMusicMuted
        boolean isMusicMuted = getIntent().getBooleanExtra("isMusicMuted", MainActivity.isMusicMuted);

        // Retrieve the value of isMusicMuted
        boolean isSfxMuted = getIntent().getBooleanExtra("isSfxMuted", MainActivity.isSfxMuted);

        // --------------------------------------------------------------------------------------------------

        // Back to Play Buttons
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MainActivity.sfxButtonPress.isPlaying()) {
                    MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    MainActivity.sfxButtonPress.seekTo(0);
                    MainActivity.sfxButtonPress.start(); // Play the sound effect
                }

                animatePlayToRight();
                animateNicknameToRight();
            }
        });

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isStartOn) {
                    if (MainActivity.sfxButtonPress.isPlaying()) {
                        MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                    } else {
                        MainActivity.sfxButtonPress.seekTo(0);
                        MainActivity.sfxButtonPress.start(); // Play the sound effect
                    }

                    startButton.setImageResource(R.drawable.a_start_off);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startButton.setImageResource(R.drawable.a_start_on);
                        }
                    }, 100);

                    // TO GAME PAGE
                    MainActivity.existingPlayerName.setText(nicknamePlayer.getText());
                    String playerName = nicknamePlayer.getText().toString().toUpperCase().trim(); // Retrieve player's name from EditText

                    if (!isExistingSelected) {
                        MainActivity.insertNewPlayer(playerName); // Insert player's name into the database
                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MenuPage.this, TutorialPage.class);
                                startActivity(intent);
                            }
                        }, 10);
                        return;
                    }
                    // Set here the tutorial Game
                    MainActivity.animateReady();
                    finish();
                } else {
                    startButton.setImageResource(R.drawable.a_start_off);
                }
            }
        });

        // Initialize the ImageViews first
        newPlayer = findViewById(R.id.newPlayer);
        existingPlayer = findViewById(R.id.existingPlayer);

        // Set the initial selection state
        newPlayer.setImageResource(R.drawable.a_newplayer_on);
        existingPlayer.setImageResource(R.drawable.a_existingplayer_off);

        // Set the ImageViews clickable
        // newPlayer.setClickable(true);
        // existingPlayer.setClickable(false);

        nicknamePlayer = findViewById(R.id.nicknamePlayer);
        validateExistingPlayer();

        existingPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.sfxButtonPress.isPlaying()) {
                    MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    MainActivity.sfxButtonPress.seekTo(0);
                    MainActivity.sfxButtonPress.start(); // Play the sound effect
                }

                existingPlayer.setImageResource(R.drawable.a_existingplayer_off);
                newPlayer.setImageResource(R.drawable.a_newplayer_on);

                isExistingSelected = true;
                validateExistingPlayer();
                isStartOn = true;
                startButton.setImageResource(R.drawable.a_start_on);
                nicknameEditText.setImageResource(R.drawable.a_nickname);

                existingPlayer.setClickable(false);
                newPlayer.setClickable(true);
            }
        });

        newPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.sfxButtonPress.isPlaying()) {
                    MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    MainActivity.sfxButtonPress.seekTo(0);
                    MainActivity.sfxButtonPress.start(); // Play the sound effect
                }

                newPlayer.setImageResource(R.drawable.a_newplayer_off);
                existingPlayer.setImageResource(R.drawable.a_existingplayer_on);

                nicknamePlayer.setText("");
                startButton.setImageResource(R.drawable.a_start_off);

                isStartOn = false;
                newPlayer.setClickable(false);
                existingPlayer.setClickable(true);
                nicknamePlayer.setEnabled(true);

                isExistingSelected = false;
                validateNewPlayer();
                nicknameEditText.setImageResource(R.drawable.a_nickname);
            }
        });


        // BG Music Button
        backgroundMusicImageView = findViewById(R.id.backgroundMusic);
        if (isMusicMuted) {
                backgroundMusicImageView.setImageResource(R.drawable.ic_music_off);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backgroundMusicImageView.setImageResource(R.drawable.ic_music_on);
                }
            }, 150);
        }
        backgroundMusicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMusicMuted) {
                    backgroundMusicImageView.setImageResource(R.drawable.ic_music_off);
                } else {
                    backgroundMusicImageView.setImageResource(R.drawable.ic_music_on);
                }
                MainActivity.backgroundMusicImageView.performClick();
            }
        });

        // Sfx Button
        sfxMusicImageView = findViewById(R.id.sfxMusic);
        if (isSfxMuted) {
                sfxMusicImageView.setImageResource(R.drawable.ic_sfx_off);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sfxMusicImageView.setImageResource(R.drawable.ic_sfx_on);
                }
            }, 150);
        }
        sfxMusicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSfxMuted) {
                    sfxMusicImageView.setImageResource(R.drawable.ic_sfx_off);
                } else {
                    sfxMusicImageView.setImageResource(R.drawable.ic_sfx_on);
                }
                MainActivity.sfxMusicImageView.performClick();
            }
        });

        // Exit Button
        exitButtonImageView = findViewById(R.id.exitButton);
        exitButtonImageView.setOnTouchListener(new View.OnTouchListener() {
            private long lastClickTime = 0;
            private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Adjust this value as needed

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        long clickTime = System.currentTimeMillis();
                        // Check if the click is a double click
                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                            return true; // Ignore double-click
                        }
                        lastClickTime = clickTime;

                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        exitButtonImageView.setImageResource(R.drawable.ic_exit_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Start the exit activity
                        Intent intent = new Intent(MenuPage.this, ExitPage.class);
                        startActivity(intent);

                        // Re-enable the button after a delay
                        exitButtonImageView.setEnabled(false);
                        exitButtonImageView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                exitButtonImageView.setEnabled(true);
                            }
                        }, 500); // Adjust this delay as needed
                        break;
                }
                return true; // Consume the event
            }
        });


        // To game page
        Button backButton = findViewById(R.id.button_back_to_main);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                finish();
            }
        });


        // Define the initial and final Y positions
        float initialY = playButton.getY();
        float finalY = initialY - 30; // Adjust this value as needed for your desired pop-up distance

        // Create and start animations for each ImageView
        animateView(playButton, finalY, initialY);
        animateView(leaderboardButton, finalY, initialY);
        animateView(gameplayButton, finalY, initialY);
        animateView(backgroundMusicImageView, finalY, initialY);
        animateView(sfxMusicImageView, finalY, initialY);
        animateView(exitButtonImageView, finalY, initialY);

        animateGameTitle(); // Animation of the Logo Title

        // Temporary
        nicknameEditText = findViewById(R.id.nicknameEditText);
    }

    private void validateExistingPlayer() {

        nicknamePlayer.setText(EXISTINGPLAYER);

        filterText();

        // Logic to check if player exists in the database
        nicknamePlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for live validation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for live validation
            }

            @Override
            public void afterTextChanged(Editable s) {
                String playerName = s.toString().trim().toUpperCase();
                boolean playerExists = MainActivity.checkPlayerExists(playerName);

                if (playerExists) {
                    // Player exists
                    nicknameEditText.setImageResource(R.drawable.a_nickname);
                    startButton.setImageResource(R.drawable.a_start_on);
                    isStartOn = true;
                } else {
                    // Player does not exist
                    nicknameEditText.setImageResource(R.drawable.a_player_notexist);
                    startButton.setImageResource(R.drawable.a_start_off);
                    isStartOn = false;
                }
            }
        });
    }

    private void validateNewPlayer() {
        filterText();

        // Logic to check if player is available in the database
        nicknamePlayer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for live validation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for live validation
            }

            @Override
            public void afterTextChanged(Editable s) {
                String playerName = s.toString().trim().toUpperCase();

                if (playerName.isEmpty()) {
                    // No input
                    nicknameEditText.setImageResource(R.drawable.a_nickname);
                    startButton.setImageResource(R.drawable.a_start_off);
                    isStartOn = false;
                } else {
                    boolean playerExists = MainActivity.checkPlayerExists(playerName);

                    if (playerExists) {
                        // Player exists
                        nicknameEditText.setImageResource(R.drawable.a_nickname_exist);
                        startButton.setImageResource(R.drawable.a_start_off);
                        isStartOn = false;
                    } else {
                        // Player available
                        nicknameEditText.setImageResource(R.drawable.a_nickname_available);
                        startButton.setImageResource(R.drawable.a_start_on);
                        isStartOn = true;
                    }
                }
            }
        });
    }

    private void filterText() {
        // Adding validation to EditText
        nicknamePlayer.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(10), // Limit to 10 characters
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // Only allow letters or numbers
                        if (end > start) {
                            for (int i = start; i < end; ++i) {
                                if (!Character.isLetterOrDigit(source.charAt(i))) {
                                    return "";
                                }
                            }
                        }
                        return null;
                    }
                }
        });
    }

    private void animateGameTitle() {
        // Find the ImageView
        ImageView gameTitleImageView = findViewById(R.id.gameTitle);

        // Apply the scale animation using ViewPropertyAnimator
        gameTitleImageView.setScaleX(0.0f);
        gameTitleImageView.setScaleY(0.0f);
        gameTitleImageView.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }
    private void animateView(View view, float finalY, float initialY) {
        // Create the ObjectAnimator for Y translation
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "translationY", finalY, initialY);
        animatorY.setInterpolator(new BounceInterpolator()); // Apply bounce interpolation for a bouncy effect
        animatorY.setDuration(800); // Set the duration of the animation in milliseconds

        // Start the animation
        animatorY.start();
    }

    private void animatePlayToLeft() { // Animation of Play Buttons
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator playAnim = ObjectAnimator.ofFloat(playButton, "translationX", -playButton.getWidth() - 130);
        playAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        playAnim.setDuration(800);

        ObjectAnimator leaderboardAnim = ObjectAnimator.ofFloat(leaderboardButton, "translationX", -leaderboardButton.getWidth() - 190);
        leaderboardAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        leaderboardAnim.setStartDelay(100);
        leaderboardAnim.setDuration(800);

        ObjectAnimator gameplayAnim = ObjectAnimator.ofFloat(gameplayButton, "translationX", -gameplayButton.getWidth() - 190);
        gameplayAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        gameplayAnim.setStartDelay(200);
        gameplayAnim.setDuration(800);

        animatorSet.playTogether(playAnim, leaderboardAnim, gameplayAnim);
        animatorSet.start();
    }
    private void animatePlayToRight() { // Back to Original Position Animation of Play Buttons
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator playAnim = ObjectAnimator.ofFloat(playButton, "translationX", 0f);
        playAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        playAnim.setDuration(800);

        ObjectAnimator leaderboardAnim = ObjectAnimator.ofFloat(leaderboardButton, "translationX", 0f);
        leaderboardAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        leaderboardAnim.setStartDelay(100);
        leaderboardAnim.setDuration(800);

        ObjectAnimator gameplayAnim = ObjectAnimator.ofFloat(gameplayButton, "translationX", 0f);
        gameplayAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        gameplayAnim.setStartDelay(200);
        gameplayAnim.setDuration(800);

        animatorSet.playTogether(playAnim, leaderboardAnim, gameplayAnim);
        animatorSet.start();
    }

    private void animateNicknameToLeft() { // Animation of Nickname Buttons

        // Ensure ImageViews are visible before animating
        nicknameEditText.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        existingPlayer.setVisibility(View.VISIBLE);
        newPlayer.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setClickable(true);
        nicknamePlayer.setVisibility(View.VISIBLE);

        // Calculate the width of the screen
        int screenWidth = 849;

        // Set initial translation values for each ImageView
        nicknameEditText.setTranslationX(screenWidth);
        startButton.setTranslationX(screenWidth);
        existingPlayer.setTranslationX(screenWidth);
        newPlayer.setTranslationX(screenWidth);
        backBtn.setTranslationX(-backBtn.getWidth() - 50);
        nicknamePlayer.setTranslationX(screenWidth);

        // Create animations to bring ImageViews to their current positions
        ObjectAnimator nicknameAnim = ObjectAnimator.ofFloat(nicknameEditText, "translationX", 0f);
        nicknameAnim.setInterpolator(new DecelerateInterpolator());
        nicknameAnim.setDuration(800);

        ObjectAnimator nicknamePlayerAnim = ObjectAnimator.ofFloat(nicknamePlayer, "translationX", 0f);
        nicknamePlayerAnim.setInterpolator(new DecelerateInterpolator());
        nicknamePlayerAnim.setDuration(800);

        ObjectAnimator startButtonAnim = ObjectAnimator.ofFloat(startButton, "translationX", 0f);
        startButtonAnim.setInterpolator(new DecelerateInterpolator());
        startButtonAnim.setStartDelay(100);
        startButtonAnim.setDuration(800);


        ObjectAnimator existingPlayerAnim = ObjectAnimator.ofFloat(existingPlayer, "translationX", 0f);
        existingPlayerAnim.setInterpolator(new DecelerateInterpolator());
        existingPlayerAnim.setStartDelay(200);
        existingPlayerAnim.setDuration(800);

        ObjectAnimator newPlayerAnim = ObjectAnimator.ofFloat(newPlayer, "translationX", 0f);
        newPlayerAnim.setInterpolator(new DecelerateInterpolator());
        newPlayerAnim.setStartDelay(200);
        newPlayerAnim.setDuration(800);

        ObjectAnimator backBtnAnim = ObjectAnimator.ofFloat(backBtn, "translationX", 0f);
        backBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        backBtnAnim.setStartDelay(500);
        backBtnAnim.setDuration(800);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nicknamePlayer.setEnabled(true);
            }
        }, 250);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(nicknameAnim, nicknamePlayerAnim, startButtonAnim, existingPlayerAnim, newPlayerAnim, backBtnAnim);
        animatorSet.start();
    }

    private void animateNicknameToRight() { // Animation to move ImageViews to the right end
        // Ensure ImageViews are visible before animating
        nicknameEditText.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);
        existingPlayer.setVisibility(View.VISIBLE);
        newPlayer.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setClickable(false);
        nicknamePlayer.setEnabled(false);

        // Calculate the width of the screen
        int screenWidth = 849;

        // Create animations to move ImageViews to the right end
        ObjectAnimator nicknameAnim = ObjectAnimator.ofFloat(nicknameEditText, "translationX", screenWidth);
        nicknameAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        nicknameAnim.setDuration(800);

        // Create animations to move ImageViews to the right end
        ObjectAnimator nicknamePlayerAnim = ObjectAnimator.ofFloat(nicknamePlayer, "translationX", screenWidth);
        nicknamePlayerAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        nicknamePlayerAnim.setDuration(800);

        ObjectAnimator startButtonAnim = ObjectAnimator.ofFloat(startButton, "translationX", screenWidth);
        startButtonAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        startButtonAnim.setStartDelay(100);
        startButtonAnim.setDuration(800);

        ObjectAnimator backBtnAnim = ObjectAnimator.ofFloat(backBtn, "translationX", screenWidth);
        backBtnAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        backBtnAnim.setStartDelay(100);
        backBtnAnim.setDuration(900);

        ObjectAnimator existingPlayerAnim = ObjectAnimator.ofFloat(existingPlayer, "translationX", screenWidth);
        existingPlayerAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        existingPlayerAnim.setStartDelay(200);
        existingPlayerAnim.setDuration(800);

        ObjectAnimator newPlayerAnim = ObjectAnimator.ofFloat(newPlayer, "translationX", screenWidth);
        newPlayerAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        newPlayerAnim.setStartDelay(200);
        newPlayerAnim.setDuration(800);

        // Play animations together
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(nicknameAnim, nicknamePlayerAnim, startButtonAnim, existingPlayerAnim, newPlayerAnim, backBtnAnim);
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