package com.loompa.tapandshoot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {

    private float outerStrokeWidth = 20.0f;
    private int outerStrokeColor = 0xFF84592C; // Default outer stroke color is black
    private float innerStrokeWidth = 12.0f;
    private int innerStrokeColor = 0xFFBA7F44; // Default inner stroke color is white

    public StrokeTextView(Context context) {
        super(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // If you want to customize stroke width or color via XML attributes, you can do it here
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Save the current text color and paint settings
        int currentTextColor = getCurrentTextColor();

        // Draw the outer stroke
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(outerStrokeWidth);
        getPaint().setStrokeJoin(Paint.Join.ROUND); // Set stroke join to round
        getPaint().setStrokeCap(Paint.Cap.ROUND); // Set stroke cap to round
        setTextColor(outerStrokeColor);
        super.onDraw(canvas);

        // Draw the inner stroke
        getPaint().setStyle(Paint.Style.STROKE);
        getPaint().setStrokeWidth(innerStrokeWidth);
        setTextColor(innerStrokeColor);
        super.onDraw(canvas);

        // Restore the original text color and paint settings
        getPaint().setStyle(Paint.Style.FILL);
        setTextColor(currentTextColor);
        super.onDraw(canvas);
    }
}

