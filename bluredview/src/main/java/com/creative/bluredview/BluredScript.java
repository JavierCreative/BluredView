package com.creative.bluredview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BluredScript
{
    private static RenderScript mRenderScript;

    private static final boolean BLUR_IS_SUPPORTED = Build.VERSION.SDK_INT >= 17;
    private static final int MAX_RADIUS = 25;

    @Nullable
    public static Drawable getBluredBackground(@NonNull Bitmap bitmap, float radius, int repeat, Context context)
    {
        mRenderScript = RenderScript.create(context);
        //Check if blur is supported by the app
        if (!BLUR_IS_SUPPORTED)
        {
            return null;
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
        return background;
    }
}
