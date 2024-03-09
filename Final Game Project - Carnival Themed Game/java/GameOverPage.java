package com.loompa.tapandshoot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GameOverPage extends AppCompatActivity {

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase database;

    private TextView[] nameTextViews = new TextView[5];
    private TextView[] scoreTextViews = new TextView[5];

    private ImageView retryButton;
    private ImageView toLeaderboardButton;
    private ImageView toHomeButton;

    // Elements
    private ImageView carnivalAsset;
    private ImageView largeBoard;
    private ImageView mascots;

    private ImageView top1Board;
    private ImageView top2Board;
    private ImageView top3Board;

    private TextView gameOverText;
    private TextView currentScore;
    private TextView highscore;

    private boolean animationsCompleted = false;
    private boolean animationsCompleted2 = false;


    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over_page);

        // Set the page Fullscreen
        FullScreen.setFullscreen(getWindow());

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Initialize TextViews
        nameTextViews[0] = findViewById(R.id.top1Name_GameOver);
        nameTextViews[1] = findViewById(R.id.top2Name_GameOver);
        nameTextViews[2] = findViewById(R.id.top3Name_GameOver);

        scoreTextViews[0] = findViewById(R.id.top1Score_GameOver);
        scoreTextViews[1] = findViewById(R.id.top2Score_GameOver);
        scoreTextViews[2] = findViewById(R.id.top3Score_GameOver);

        updateTopPlayers();

        gameOverText = findViewById(R.id.gameOverText);
        currentScore = findViewById(R.id.currentScore);
        highscore = findViewById(R.id.highscore);

        retryButton = findViewById(R.id.retryButton);
        toLeaderboardButton = findViewById(R.id.toLeaderboardButton);
        toHomeButton = findViewById(R.id.toHomeButton);

        // Elements
        carnivalAsset = findViewById(R.id.carnivalAsset);
        largeBoard = findViewById(R.id.largeBoard);
        mascots = findViewById(R.id.mascots);

        top1Board = findViewById(R.id.top1_GameOver);
        top2Board = findViewById(R.id.top2_GameOver);
        top3Board = findViewById(R.id.top3_GameOver);


        String currentPlayer = MainActivity.existingPlayerName.getText().toString().toUpperCase();
        int currentHighScore = getHighScore(currentPlayer);

        currentScore.setText("SCORE: " + MainActivity.score); // Current Score
        highscore.setText("HIGH SCORE: " + currentHighScore); // Current High Score


        // Buttons
        retryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!animationsCompleted2) {
                    return true; // Don't allow click if animations are not completed
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        retryButton.setImageResource(R.drawable.a_retry_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        retryButton.setImageResource(R.drawable.a_retry_on);
                        MainActivity.animateSlideOutCurtain();
                        finish();
                        break;
                }
                return true; // Consume the event
            }
        });

        toHomeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!animationsCompleted) {
                    return true; // Don't allow click if animations are not completed
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        toHomeButton.setImageResource(R.drawable.a_home_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        toHomeButton.setImageResource(R.drawable.a_home_on);
                        finish();
                        MainActivity.animateSlideOutCurtainReturn();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(GameOverPage.this, MenuPage.class);
                                intent.putExtra("existingPlayerName", MainActivity.existingPlayerName.getText().toString());
                                startActivity(intent);
                            }
                        }, 10);
                        break;
                }
                return true; // Consume the event
            }
        });

        toLeaderboardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (!animationsCompleted) {
                    return true; // Don't allow click if animations are not completed
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button press
                        if (MainActivity.sfxButtonPress.isPlaying()) {
                            MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                        } else {
                            MainActivity.sfxButtonPress.seekTo(0);
                            MainActivity.sfxButtonPress.start(); // Play the sound effect
                        }
                        toLeaderboardButton.setImageResource(R.drawable.a_circleleaderboard_off);
                        break;
                    case MotionEvent.ACTION_UP:
                        toLeaderboardButton.setImageResource(R.drawable.a_circleleaderboard_on);
                        finish();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                LeaderboardPage.isGameOver = true;
                                Intent intent = new Intent(GameOverPage.this, LeaderboardPage.class);
                                startActivity(intent);
                            }
                        }, 10);
                        break;
                }
                return true; // Consume the event
            }
        });
        animateLarboard();
    }

    private void updateTopPlayers() {
        List<Player> topPlayers = getTopPlayers();

        for (int i = 0; i < 3; i++) {
            if (i < topPlayers.size()) {
                Player player = topPlayers.get(i);
                nameTextViews[i].setText(player.getName());
                scoreTextViews[i].setText(String.valueOf(player.getScore()));
            } else {
                // If no player or less than 3 players available, set text to empty
                nameTextViews[i].setText("");
                scoreTextViews[i].setText("");
            }
        }
    }

    private List<Player> getTopPlayers() {
        List<Player> topPlayers = new ArrayList<>();

        // Query the database to get the top 3 players based on their scores
        Cursor cursor = database.query(
                PlayerContract.PlayerEntry.TABLE_NAME,
                new String[]{PlayerContract.PlayerEntry.COLUMN_NAME, PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE},
                null,
                null,
                null,
                null,
                PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE + " DESC, " + PlayerContract.PlayerEntry.COLUMN_NAME + " ASC", // Order by score descending, name ascending
                "3"
        );

        // Iterate over the cursor to populate the list of top players
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(PlayerContract.PlayerEntry.COLUMN_NAME));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE));
                topPlayers.add(new Player(name, score));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return topPlayers;
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

    private void animateCarnival() {

        carnivalAsset.setVisibility(View.VISIBLE);

        carnivalAsset.setScaleX(0.0f);
        carnivalAsset.setScaleY(0.0f);
        carnivalAsset.setTranslationY(carnivalAsset.getHeight() / 2f); // Start from bottom

        carnivalAsset.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationY(0) // Move up to original position
                .setDuration(1200) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }

    private void animateLarboard() {

        largeBoard.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateCarnival();
                animateAllImageViews();
            }
        }, 100);

        largeBoard.setScaleX(0.0f);
        largeBoard.setScaleY(0.0f);
        largeBoard.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(1000) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }

    private void animateImageView(final View view, long delay) {
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        view.animate()
                .setStartDelay(delay)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator(1.0f))
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        if (view.getId() == R.id.retryButton) {
                            animationsCompleted = true;
                            toHomeButton.setClickable(true);
                            toLeaderboardButton.setClickable(true);
                        }

                        if (view.getId() == R.id.retryButton) {
                            animationsCompleted2 = true;
                            retryButton.setClickable(true);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .start();
    }

    private void animateAllImageViews() {
        long delay = 0;

        retryButton.setVisibility(View.VISIBLE);
        toLeaderboardButton.setVisibility(View.VISIBLE);
        toHomeButton.setVisibility(View.VISIBLE);

        top1Board.setVisibility(View.VISIBLE);
        top2Board.setVisibility(View.VISIBLE);
        top3Board.setVisibility(View.VISIBLE);

        nameTextViews[0].setVisibility(View.VISIBLE);
        nameTextViews[1].setVisibility(View.VISIBLE);
        nameTextViews[2].setVisibility(View.VISIBLE);

        scoreTextViews[0].setVisibility(View.VISIBLE);
        scoreTextViews[1].setVisibility(View.VISIBLE);
        scoreTextViews[2].setVisibility(View.VISIBLE);

        gameOverText.setVisibility(View.VISIBLE);
        currentScore.setVisibility(View.VISIBLE);
        highscore.setVisibility(View.VISIBLE);


        // Game over
        delay += 50;
        animateImageView(findViewById(R.id.gameOverText), delay);
        animateImageView(findViewById(R.id.currentScore), delay);
        animateImageView(findViewById(R.id.highscore), delay);

        // Group 1
        delay += 100;
        animateImageView(findViewById(R.id.top1_GameOver), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top1Name_GameOver), delay);
        animateImageView(findViewById(R.id.top1Score_GameOver), delay);

        // Group 2
        delay += 100;
        animateImageView(findViewById(R.id.top2_GameOver), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top2Name_GameOver), delay);
        animateImageView(findViewById(R.id.top2Score_GameOver), delay);

        // Group 3
        delay += 100;
        animateImageView(findViewById(R.id.top3_GameOver), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top3Name_GameOver), delay);
        animateImageView(findViewById(R.id.top3Score_GameOver), delay);


        // Remaining views
        delay += 100;
        animateImageView(findViewById(R.id.toHomeButton), delay);
        delay += 100;
        animateImageView(findViewById(R.id.toLeaderboardButton), delay);
        delay += 1000;
        animateImageView(findViewById(R.id.retryButton), delay);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animateGameMascots();
            }
        }, 500);

    }

    private void animateGameMascots() {
        mascots.setVisibility(View.VISIBLE);

        mascots.setScaleX(0.0f);
        mascots.setScaleY(0.0f);
        mascots.setTranslationY((mascots.getHeight() / 2f) - 40); // Start from bottom

        mascots.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .translationY(0) // Move up to original position
                .setDuration(1200) // Duration in milliseconds
                .setInterpolator(new OvershootInterpolator(1.0f))
                .start();
    }
    Boolean shouldAllowBack = false;
    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }
}