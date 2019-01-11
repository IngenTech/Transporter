package weatherrisk.com.wrms.transporter;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.net.PortUnreachableException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import weatherrisk.com.wrms.transporter.transporter.LocalReportSender;


@ReportsCrashes(formKey = "", // will not be used
		mailTo = "vishal.tripathi@iembsys.com",
		customReportContent = {
				ReportField.APP_VERSION_CODE,
				ReportField.APP_VERSION_NAME,
				ReportField.ANDROID_VERSION,
				ReportField.PACKAGE_NAME,
				ReportField.REPORT_ID,
				ReportField.BUILD,
				ReportField.STACK_TRACE
		},
		mode = ReportingInteractionMode.TOAST,
		resToastText = R.string.toast_crash)

public class AppController extends Application {

	public static final String ACCOUNT_PREFRENCE = "AccountPrefrence";
	public static final String PREFERENCE_USERNAME = "UserName";
	public static final String PREFERENCE_PASSWORD = "Password";

	public static final String PREFERENCE_USER_ID = "userID";
	public static final String ACCESS_TOKEN = "accessToken";

	public static final String PREFERENCE_ACCOUNT_ID = "AccountId";
	public static final String PREFERENCE_ACCOUNT_TYPE = "AccountType";
	public static final String PREFERENCE_ACCOUNT_NAME = "AccountName";
	public static final String PREFERENCE_VEHICLE_COUNT = "VehicleCount";

	public static final String PENDING_ORDER_TYPE = "2";
	public static final String CONFIRM_ORDER_TYPE = "1";
	public static final String CANCEL_ORDER_TYPE = "0";


	public static final String IS_TRIP_UPDATED = "isTripUpdated";

	public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat S_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd");


	public static final SimpleDateFormat WEATHER_API_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm");

	public static final String TAG = AppController.class.getSimpleName();

	private RequestQueue mRequestQueue;

	private static AppController mInstance;
    private static boolean activityVisible;

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "IN-57639076-1";

    public static int GENERAL_TRACKER = 0;
    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }



    @Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		/*ACRA.init(this);
		ACRA.getErrorReporter().setReportSender(new LocalReportSender(this));*/

        Log.d("AppController: ", "Application Created!");
	}

	@Override
    public void onLowMemory() {
		super.onLowMemory();
		// free your memory, clean cache for example
		//Toast.makeText(getApplicationContext(), "Application on Low memory.", Toast.LENGTH_LONG).show();
		Log.d("AppController: ", "Application on Low memory.");
	}


	public static synchronized AppController getInstance() {
		return mInstance;
	}



	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }


    /*
    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
    */

    public static void updateLanguage(Context ctx, String lang) {

		Configuration cfg = new Configuration();
		String language = lang;

		if (TextUtils.isEmpty(language) && lang == null) {
			cfg.locale = Locale.getDefault();
			String tmp_locale = "";
			tmp_locale = Locale.getDefault().toString().substring(0, 2);
			//manager.SaveValueToSharedPrefs("force_locale", tmp_locale);

		} else if (lang != null) {
			cfg.locale = new Locale(lang);
			//manager.SaveValueToSharedPrefs("force_locale", lang);

		} else if (!TextUtils.isEmpty(language)) {
			cfg.locale = new Locale(language);
		}
		ctx.getResources().updateConfiguration(cfg, null);

	}


}