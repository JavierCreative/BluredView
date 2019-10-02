package com.creative.bluredview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.RenderScript;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

public class BluredView extends ConstraintLayout
{


    private boolean mIsAnimated;
    private int mTimeMilliseconds;
    private Context mContext;

    public BluredView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BluredView,
                0, 0);

        try
        {
            mIsAnimated = attributes.getBoolean(R.styleable.BluredView_animate_blur, false);
            mTimeMilliseconds = attributes.getInt(R.styleable.BluredView_animation_time,1000);
        }
        finally
        {
            attributes.recycle();
        }

        this.mContext = context;
        this.setVisibility(INVISIBLE);
        this.setElevation(5f);
    }

    public void applyBlurEffect(Bitmap bitmap, float radius, int repeat, Context context)
    {
        Drawable drawable = BluredScript.getBluredBackground(bitmap,radius,repeat,context);
        animateBlur(drawable);
    }

    private void animateBlur(Drawable image)
    {
        if (mIsAnimated)
        {
            this.setBackground(image);
            this.setAlpha(0f);
            this.setVisibility(VISIBLE);
            this.animate()
                    .alpha(1f)
                    .setDuration(mTimeMilliseconds);
        }
        else
        {
            this.setBackground(image);
            this.setVisibility(VISIBLE);
        }
    }

}
