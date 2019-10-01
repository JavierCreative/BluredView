package com.creative.bluredview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BluredView extends ConstraintLayout
{
    private RenderScript mRenderScript;
    private Context mContext;
    private boolean mIsAnimated;
    private int mTimeMilliseconds;

    private static final boolean BLUR_IS_SUPPORTED = Build.VERSION.SDK_INT >= 17;
    private static final int MAX_RADIUS = 25;

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
        this.mRenderScript = RenderScript.create(context);
        this.setVisibility(INVISIBLE);
        this.setElevation(5f);

    }

    @Nullable
    public void applyBlurToView(@NonNull Bitmap bitmap, float radius, int repeat)
    {
        //Check if blur is supported by the app
        if (!BLUR_IS_SUPPORTED)
        {
            Toast.makeText(mContext,"Your app doesn't supports blur", Toast.LENGTH_SHORT).show();
            return;
        }

        //Re-assign raidus if radiuds is greater than MAX_RADIUS
        if (radius > MAX_RADIUS)
            radius = (float) MAX_RADIUS;

        //Assign the width & height for the blured area
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //Creates the allocation type
        Type bitmapType = new Type.Builder(mRenderScript, Element.RGBA_8888(mRenderScript))
                .setX(width)
                .setY(height)
                .setMipmaps(false)
                .create();

        //Let's create the Allocation
        Allocation allocation = Allocation.createTyped(mRenderScript,bitmapType);

        //Creates the blur script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(mRenderScript,Element.U8_4(mRenderScript));
        blurScript.setRadius(radius);

        //Copy the data from the bitmap to allocation
        allocation.copyFrom(bitmap);

        //Setting up the input to blurscript
        blurScript.setInput(allocation);

        //Invoke the blurscript to blur effect
        blurScript.forEach(allocation);

        //Add extra effect blur
        for (int i = 0; i < repeat; i++)
        {
            blurScript.forEach(allocation);
        }

        //Copy back
        allocation.copyTo(bitmap);

        //Release all the memory
        allocation = null;
        blurScript = null;
        Drawable background = new BitmapDrawable(bitmap);
        animateBlur(background);
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
