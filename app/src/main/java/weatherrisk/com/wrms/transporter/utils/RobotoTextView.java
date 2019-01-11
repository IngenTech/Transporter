package weatherrisk.com.wrms.transporter.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import weatherrisk.com.wrms.transporter.R;

/**
 * Created by Admin on 25-11-2016.
 */

public class RobotoTextView extends TextView {

    public RobotoTextView(Context context) {
        super(context);
        if (isInEditMode()) return;
        parseAttributes(null);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) return;
        parseAttributes(attrs);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;
        parseAttributes(attrs);
    }

    private void parseAttributes(AttributeSet attrs) {
        int typeface;
        if (attrs == null) { //Not created from xml
            typeface = Roboto.ROBOTO_REGULAR;
        } else {
            TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.RobotoTextView);
            typeface = values.getInt(R.styleable.RobotoTextView_typeface, Roboto.ROBOTO_REGULAR);
            values.recycle();
        }
        setTypeface(getRoboto(typeface));
    }

    public void setRobotoTypeface(int typeface) {
        setTypeface(getRoboto(typeface));
    }

    private Typeface getRoboto(int typeface) {
        return getRoboto(getContext(), typeface);
    }

    public static Typeface getRoboto(Context context, int typeface) {
        switch (typeface) {
            case Roboto.ROBOTO_BOLD_CONDENSED: {
                if (Roboto.sRobotoBoldCondensed == null) {
                    Roboto.sRobotoBoldCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-BoldCondensed.ttf");
                }
                return Roboto.sRobotoBoldCondensed;
            }
            case Roboto.ROBOTO_CONDENSED: {
                if (Roboto.sRobotoCondensed == null) {
                    Roboto.sRobotoCondensed = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Condensed.ttf");
                }
                return Roboto.sRobotoCondensed;
            }
            case Roboto.ROBOTO_LIGHT: {
                if (Roboto.sRobotoLight == null) {
                    Roboto.sRobotoLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
                }
                return Roboto.sRobotoLight;
            }
            case Roboto.ROBOTO_MEDIUM: {
                if (Roboto.sRobotoMedium == null) {
                    Roboto.sRobotoMedium = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf");
                }
                return Roboto.sRobotoMedium;
            }
            default:
            case Roboto.ROBOTO_THIN: {
                if (Roboto.sRobotoThin == null) {
                    Roboto.sRobotoThin = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Thin.ttf");
                }
                return Roboto.sRobotoThin;
            }
        }
    }

    public static class Roboto {
        public static final int ROBOTO_BOLD_CONDENSED = 4;
        public static final int ROBOTO_CONDENSED = 6;
        public static final int ROBOTO_LIGHT = 9;
        public static final int ROBOTO_MEDIUM = 11;
        public static final int ROBOTO_REGULAR = 13;
        public static final int ROBOTO_THIN = 14;

        private static Typeface sRobotoBoldCondensed;
        private static Typeface sRobotoCondensed;
        private static Typeface sRobotoLight;
        private static Typeface sRobotoMedium;
        private static Typeface sRobotoThin;
    }
}
