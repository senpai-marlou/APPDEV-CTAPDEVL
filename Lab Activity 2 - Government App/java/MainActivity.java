package com.tobello.govermentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private boolean isChecked = false; // Track the checked state
    EditText password;
    private EditText passwordEditText;
    EditText ID;
    boolean passwordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // User ID validator example
        ID = findViewById(R.id.ID);

        // Add a TextWatcher to monitor changes in the EditText
        ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if the entered text is "POGIMARLOU"
                if (editable.toString().equals("POGIMARLOU")) {
                    // Set the image drawable for drawableEnd
                    ID.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_validate, 0);
                } else {
                    // Clear the drawable if the entered text is not "POGIMARLOU"
                    ID.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
            }
        });


        // Hide and Show Password
        password=findViewById(R.id.password);

        password.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ResourceType")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP) {
                    if(event.getRawX()>=password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection=password.getSelectionEnd();
                        if(passwordVisible) {
                            // Set drawable image
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_hide,0);
                            // For hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        } else {
                            // Set drawable image
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_show,0);
                            // For hide password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        password.setSelection(selection);
                    }
                }
                return false;
            }
        });

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

        final ImageView customCheckBox = findViewById(R.id.customCheckBox);
        customCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle checkbox click
                isChecked = !isChecked; // Toggle the checked state
                updateCheckboxState(customCheckBox, isChecked);
            }
        });
    }
    private void updateCheckboxState(ImageView checkbox, boolean isChecked) {
        // Set the appropriate image based on the checked state
        checkbox.setImageResource(isChecked ? R.drawable.check : R.drawable.uncheck);
    }
}