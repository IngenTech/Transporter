package weatherrisk.com.wrms.transporter.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import weatherrisk.com.wrms.transporter.AppController;

/**
 * Created by WRMS on 02-04-2016.
 */
public class MyUtility {


    public static class URL{

        public static final String ADDRESS_OF_LAT_LNG_API = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+ Locale.getDefault().getCountry();

        public static final String API_ROOT = "http://nimbumircheeapi.co.in/nimbu_mirchee_api/v1/transporter/";

        public static final String LOGIN_API = API_ROOT+"user_login";
        public static final String REGISTRATION_API = API_ROOT+"register";

        public static final String FIRM_LIST_API = API_ROOT+"transporter";
        public static final String VEHICLE_LIST_API = API_ROOT+"vehicle_list";

        public static final String STATE_API = API_ROOT+"state";
        public static final String CITY_API = API_ROOT+"city";
        public static final String MATERIAL_API = API_ROOT+"material";


        //running trips that need to be closed
      //  public static final String RUNNING_TRIP_API = API_ROOT+"trip";

        // accepted customer orders that need to be converted in to trips
        public static final String ACCEPTED_CUSTOMER_ORDER_API = API_ROOT+"acceptedCustomerOrders";

        // orders that need to be accepted or rejected by transporter
        public static final String PENDING_ORDERS_LIST_API = API_ROOT+"transporter_orders";
        public static final String CANCEL_ORDERS_LIST_API = API_ROOT+"transporter_orders";

        public static final String RUNNING_TRIP_API = API_ROOT+"running_trip";
        public static final String HISTORY_TRIP_API = API_ROOT+"trip_history";
        public static final String CANCEL_ORDER_API = API_ROOT+"cancel_order";

        public static final String PROCCESS_CUSTOME_ORDER_API = API_ROOT+"orders/";


        public static final String CLOSE_TRIP_API = API_ROOT+"close_trip/";
        public static final String ON_ROAD_ASSISTANCE_REQUEST_API = API_ROOT+"on_road_assistance_list";
        public static final String SEARCH_TRANSPORTER_API = API_ROOT+"searchTransporter";
        public static final String BOOK_ON_ROAD_ASSISTANCE_API = API_ROOT+"book_case";
        public static final String CLOSE_ON_ROAD_ASSISTANCE_API = API_ROOT+"close_on_road_assistance";

        public static final String ADD_TRIP_API = API_ROOT+"add_trip";
        public static final String ORDER_TO_TRIP_API = API_ROOT+"orderToTrip";
        public static final String ADD_CUSTOMER_ORDER_API = API_ROOT+"newOrder";
        public static final String UPLOAD_TRIP_INVOICE_API = API_ROOT+"upload_invoice";
        public static final String UPLOAD_ROAD_PERMIT_API = API_ROOT+"upload_road_permit";
       // public static final String EDIT_TRANSPORTER_PROFILE_API = API_ROOT+"update_firm/";
        public static final String LAST_LOCATION_API = API_ROOT+"last_data";

        public static final String VIEW_INVICE_LIST = API_ROOT+"invoice_list";
        public static final String VIEW_ROADPERMIT_LIST = API_ROOT+"permit_list";
        public static final String DOWNLOAD_INVOICE_IMAGE = API_ROOT+"download_invoice";
        public static final String DOWNLOAD_ROADTRIP_IMAGE = API_ROOT+"download_road_permit";

        public static final String HALT_REPORT_API = API_ROOT+"halt_report";
        public static final String DISTANCE_REPORT = API_ROOT+"distance_report";
        public static final String LIVE_API = API_ROOT+"live";
        public static final String TRAVEL_REPORT_API = API_ROOT+"travel_report";
        public static final String TRACK_API = API_ROOT+"track";
        public static final String TRACK_API_MAP = API_ROOT+"track";
        public static final String UPDATE_TRANSPORTER_API = API_ROOT+"update_transporter_profile";
        public static final String CHANGE_PASSWORD = API_ROOT+"change_password";

        public static final String GET_PROFILE = API_ROOT+"get_profile";
        public static final String LOGOUT = API_ROOT+"log_out";
        public static final String OTP_API = API_ROOT+"verify_otp";

        public static final String VEHICAL_MODEL_API = API_ROOT+"vehicle_model_list";
        public static final String VEHICAL_TYPE_API = API_ROOT+"vehicle_type_list";
        public static final String ADD_VEHICLE_API = "http://nimbumircheeapi.co.in/nimbu_mirchee_api/v1/transporter/add_vehicle";
        public static final String DOCUMENT_TITLE_API = API_ROOT+"document_title_list";
        public static final String UPLOAD_VEHICLE_DOCUMENT = API_ROOT+"upload_vehicle_document";
        public static final String VIEW_VEHICLE_DOCUMENTS = API_ROOT+"vehicle_document_list";
        public static final String DELETE_DOCUMENT_API = API_ROOT+"delete_vehicle_document";
        public static final String EXPENSE_TYPE_API= API_ROOT+"expense_type_list";
        public static final String DRIVER_EXPENSE_LIST = API_ROOT+"driver_expense_list";
        public static final String VEHICLE_EXPENSE_LIST = API_ROOT+"vehicle_expense_list";
        public static final String ADD_VEHICLE_EXPENSE= API_ROOT+"add_vehicle_expense";
        public static final String ADD_DRIVER_EXPENSE= API_ROOT+"add_driver_expense";
        public static final String DRIVER_LIST_API= API_ROOT+"driver_list";

        public static final String BRANCH_LIST_API = API_ROOT+"branch_list";
        public static final String BRANCH_DELETE_API = API_ROOT+"delete_branch";
        public static final String ADD_BRANCH_API = API_ROOT+"add_branch";
        public static final String EDIT_BRANCH_API = API_ROOT+"edit_branch";

        public static final String REMINDER_LIST_API = API_ROOT+"reminder_list";

    }

    public static class PREF{
        public static final String NIMBOMIRCHEE_PREF = "nimboo_mirchee";
        public static final String IMEI = "imei";
        /*public static final String NIMBOMIRCHEE_PREF = "nimboo_mirchee";*/
    }

    public static final String COMPANY = "company";

    public static boolean addCompanyItem(Activity activity, String favoriteItem){
        //Get previous favorite items
        String companyList = getStringFromPreferences(activity,null,COMPANY);
        // Append new Favorite item
        if(companyList!=null){
            companyList = companyList+","+favoriteItem;
        }else{
            companyList = favoriteItem;
        }
        // Save in Shared Preferences
        return putStringInPreferences(activity,companyList,COMPANY);
    }
    public static String[] getCompanyList(Activity activity){
        String companyList = getStringFromPreferences(activity,null,COMPANY);
        return convertStringToArray(companyList);
    }
    private static boolean putStringInPreferences(Activity activity,String nick,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, nick);
        editor.commit();
        return true;
    }
    private static String getStringFromPreferences(Activity activity,String defaultValue,String key){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }

    private static String[] convertStringToArray(String str){
        String[] arr = str.split(",");
        return arr;
    }

    public static String getAccountId(Activity activity){
        SharedPreferences sharedPreferences = activity.getPreferences(Activity.MODE_PRIVATE);
        String temp = sharedPreferences.getString(AppController.PREFERENCE_ACCOUNT_ID, "0");
        return temp;
    }

    public static String md5(String input) {

        String md5 = null;

        if(null == input) return null;

        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes(), 0, input.length());
            md5 = new BigInteger(1, digest.digest()).toString(16);

        } catch (NoSuchAlgorithmException e) {

            e.printStackTrace();
        }
        return md5;
    }

    public static String macAddress(Activity activity){

        WifiManager wifiManager = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        return macAddress;
    }


}
