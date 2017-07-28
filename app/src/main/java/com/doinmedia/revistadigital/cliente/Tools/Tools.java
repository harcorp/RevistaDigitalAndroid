package com.doinmedia.revistadigital.cliente.Tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Created by davidrodriguez on 27/01/17.
 */

public class Tools {

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getDrawable(id);
        } else {
            //noinspection deprecation
            return context.getResources().getDrawable(id);
        }
    }
}
