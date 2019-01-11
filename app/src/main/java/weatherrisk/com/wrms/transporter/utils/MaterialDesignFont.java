package weatherrisk.com.wrms.transporter.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Admin on 25-11-2016.
 */

public class MaterialDesignFont  extends TextView {

    public MaterialDesignFont(Context context) {
        super(context);

        applyCustomFont(context);
    }

    public MaterialDesignFont(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context);
    }

    public MaterialDesignFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context);
    }
//Roboto-Black For Title
    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface("fonts/Roboto-Thin.ttf", context);
        setTypeface(customFont);
    }

    public class BoldCondensed  extends TextView{
        public BoldCondensed(Context context) {
            super(context);

            applyCustomFont(context);
        }

        public BoldCondensed(Context context, AttributeSet attrs) {
            super(context, attrs);

            applyCustomFont(context);
        }

        public BoldCondensed(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            applyCustomFont(context);
        }
        //Roboto-Black For Title
        private void applyCustomFont(Context context) {
            Typeface customFont = FontCache.getTypeface("fonts/Roboto-BoldCondensed.ttf", context);
            setTypeface(customFont);
        }
    }
}
