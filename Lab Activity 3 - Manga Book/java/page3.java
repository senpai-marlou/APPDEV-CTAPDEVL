package com.tobello.mangabook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

public class page3 extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private ScrollView scrollView;
    private LinearLayout buttonContainer;
    private boolean areButtonsVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the navigation bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Set the background Full Screen
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        // Zoom In | Zoom Out
        PhotoView photoView = (PhotoView) findViewById(R.id.page3);
        photoView.setImageResource(R.drawable.chapt192);

        // Scroll View Button Animation
        scrollView = findViewById(R.id.scrollView);
        buttonContainer = findViewById(R.id.buttonContainer);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // Check if the user has reached the bottom of the ScrollView
                View child = scrollView.getChildAt(0);
                if (child != null && scrollView.getHeight() + scrollView.getScrollY() >= child.getHeight()) {
                    // User is at the bottom, make the buttons visible with animation
                    if (!areButtonsVisible) {
                        slideUp(buttonContainer);
                        areButtonsVisible = true;
                    }
                } else {
                    // User is not at the bottom, hide the buttons with animation
                    if (areButtonsVisible) {
                        slideDown(buttonContainer);
                        areButtonsVisible = false;
                    }
                }
            }
        });
    }

    public void goToPage2(View v){
        Intent i = new Intent(this, page2.class);
        startActivity(i);
        finish();
    }

    public void goToPage4(View v){
        Intent i = new Intent(this, page4.class);
        startActivity(i);
        finish();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v, Gravity.END);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.chapter_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        SparseArray<Class<?>> classMap = new SparseArray<>();
        classMap.put(R.id.item1, MainActivity.class);
        classMap.put(R.id.item2, page1.class);
        classMap.put(R.id.item3, page2.class);
        classMap.put(R.id.item4, page3.class);
        classMap.put(R.id.item5, page4.class);
        classMap.put(R.id.item6, page5.class);
        classMap.put(R.id.item7, page6.class);
        classMap.put(R.id.item8, page7.class);
        classMap.put(R.id.item9, page8.class);
        classMap.put(R.id.item10, page9.class);

        Class<?> selectedClass = classMap.get(itemId);

        if (selectedClass != null) {
            startActivity(new Intent(this, selectedClass));
            finish();
            return true;
        } else {
            return false;
        }
    }

    int j = 1;

    public void onBackPressed(){
        if (j == 1){
            j++;
            Toast.makeText(this, "Press the back button again to exit.",
                    Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    // Animation for sliding up
    private void slideUp(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 175, 0);
        animate.setDuration(400);
        animate.setFillAfter(true);
        animate.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animate);
    }

    // Animation for sliding down
    private void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, 175);
        animate.setDuration(450);
        animate.setFillAfter(true);
        animate.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(animate);
    }
}