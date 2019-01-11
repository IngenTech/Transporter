package weatherrisk.com.wrms.transporter.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

/**
 * Created by Admin on 25-11-2016.
 */

public class FontCache {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public static Typeface getTypeface(String fontname, Context context) {
        Typeface typeface = fontCache.get(fontname);

        if (typeface == null) {
            try {

//                Typeface.createFromAsset(getContext().getAssets(), "robotobold.ttf");

                typeface = Typeface.createFromAsset(context.getAssets(), fontname);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            fontCache.put(fontname, typeface);
        }

        return typeface;
    }
}
