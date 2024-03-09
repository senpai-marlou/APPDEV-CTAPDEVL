package com.loompa.tapandshoot;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardPage extends AppCompatActivity {

    private ImageView back2;
    private boolean animationsCompleted = false;

    public static DatabaseHelper dbHelper;
    public static SQLiteDatabase database;

    private TextView[] nameTextViews = new TextView[5];
    private TextView[] scoreTextViews = new TextView[5];

    public static boolean isGameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_page);

        // Set the page Fullscreen
        FullScreen.setFullscreen(getWindow());

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Initialize TextViews
        nameTextViews[0] = findViewById(R.id.top1Name);
        nameTextViews[1] = findViewById(R.id.top2Name);
        nameTextViews[2] = findViewById(R.id.top3Name);
        nameTextViews[3] = findViewById(R.id.top4Name);
        nameTextViews[4] = findViewById(R.id.top5Name);

        scoreTextViews[0] = findViewById(R.id.top1Score);
        scoreTextViews[1] = findViewById(R.id.top2Score);
        scoreTextViews[2] = findViewById(R.id.top3Score);
        scoreTextViews[3] = findViewById(R.id.top4Score);
        scoreTextViews[4] = findViewById(R.id.top5Score);

        updateTopPlayers();

        back2 = findViewById(R.id.back2);
        back2.setClickable(false); // Initially set back2 not clickable

        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!animationsCompleted) {
                    return; // Don't allow click if animations are not completed
                }

                // Your onClick code here
                if (MainActivity.sfxButtonPress.isPlaying()) {
                    MainActivity.sfxButtonPress.seekTo(0); // Rewind the sound effect to the beginning
                } else {
                    MainActivity.sfxButtonPress.seekTo(0);
                    MainActivity.sfxButtonPress.start(); // Play the sound effect
                }

                back2.setImageResource(R.drawable.a_back2_off);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        back2.setImageResource(R.drawable.a_back2_on);
                    }
                }, 70);


                if (isGameOver) {
                    finish();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LeaderboardPage.this, GameOverPage.class);
                            startActivity(intent);
                            isGameOver = !isGameOver;
                        }
                    }, 10);
                    return;
                }

                finish();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LeaderboardPage.this, MenuPage.class);
                        intent.putExtra("existingPlayerName", MainActivity.existingPlayerName.getText().toString());
                        startActivity(intent);
                    }
                }, 10);
            }
        });
        // Temporary Animations
        animateAllImageViews(); // Animation of the Logo Title
    }

    private void updateTopPlayers() {
        List<Player> topPlayers = getTopPlayers();

        for (int i = 0; i < 5; i++) {
            if (i < topPlayers.size()) {
                Player player = topPlayers.get(i);
                nameTextViews[i].setText(player.getName());
                scoreTextViews[i].setText(String.valueOf(player.getScore()));
            } else {
                // If no player or less than 5 players available, set text to empty
                nameTextViews[i].setText("");
                scoreTextViews[i].setText("");
            }
        }
    }

    private List<Player> getTopPlayers() {
        List<Player> topPlayers = new ArrayList<>();

        // Query the database to get the top 5 players based on their scores
        Cursor cursor = database.query(
                PlayerContract.PlayerEntry.TABLE_NAME,
                new String[]{PlayerContract.PlayerEntry.COLUMN_NAME, PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE},
                null,
                null,
                null,
                null,
                PlayerContract.PlayerEntry.COLUMN_HIGH_SCORE + " DESC, " + PlayerContract.PlayerEntry.COLUMN_NAME + " ASC", // Order by score descending, name ascending
                "5"
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
                        // Check if all animations are completed
                        if (view.getId() == R.id.back2) {
                            animationsCompleted = true;
                            back2.setClickable(true); // Enable click after all animations are done
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
        animateImageView(findViewById(R.id.leaderBoardTitle), delay);

        // Group 1
        delay += 100;
        animateImageView(findViewById(R.id.top1), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top1Name), delay);
        animateImageView(findViewById(R.id.top1Score), delay);

        // Group 2
        delay += 100;
        animateImageView(findViewById(R.id.top2), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top2Name), delay);
        animateImageView(findViewById(R.id.top2Score), delay);

        // Group 3
        delay += 100;
        animateImageView(findViewById(R.id.top3), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top3Name), delay);
        animateImageView(findViewById(R.id.top3Score), delay);

        // Group 4
        delay += 100;
        animateImageView(findViewById(R.id.top4), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top4Name), delay);
        animateImageView(findViewById(R.id.top4Score), delay);

        // Group 5
        delay += 100;
        animateImageView(findViewById(R.id.top5), delay);
        delay += 120;
        animateImageView(findViewById(R.id.top5Name), delay);
        animateImageView(findViewById(R.id.top5Score), delay);

        // Remaining views
        delay += 100;
        animateImageView(findViewById(R.id.back2), delay);
    }

    Boolean shouldAllowBack = false;
    @Override
    public void onBackPressed() {
        if (shouldAllowBack) {
            super.onBackPressed();
        }
    }
}
