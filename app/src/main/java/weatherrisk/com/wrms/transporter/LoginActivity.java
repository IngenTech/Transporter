package weatherrisk.com.wrms.transporter;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.PasswordDecoder;

public class LoginActivity extends AppCompatActivity {

    DBAdapter db;
    EditText user_name;
    EditText password;
    Button login;
    Button signupButton;
    SharedPreferences prefs;


    CheckBox remeberCheck;
    String remeberType = "0";
    SharedPreferences sharedpreferences;
    TextView website_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        website_link = (TextView) findViewById(R.id.website_link);

        if (!checkGooglePlayServicesAvailable()) {
            return;
        }

        if (!checkPermissionStorage()) {
            return;
        }

        if (!checkPermissionPhone()) {
            return;
        }
        if (!checkPermissionLocation1()) {
            return;
        }
        if (!checkPermissionLocation2()) {
            return;
        }


        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        String uN = prefs.getString(AppController.PREFERENCE_USERNAME, "");
        String psw = prefs.getString(AppController.PREFERENCE_PASSWORD, "");
        if (uN != null && uN.length() > 0) {

            startMainActivity();
        } else {

            db = new DBAdapter(LoginActivity.this);
            db.open();

            user_name = (EditText) findViewById(R.id.userName);
            password = (EditText) findViewById(R.id.pass);
            login = (Button) findViewById(R.id.loginButton);
            signupButton = (Button) findViewById(R.id.signupButton);

            sharedpreferences = getSharedPreferences("LoginSession", Context.MODE_PRIVATE);

            remeberCheck = (CheckBox) findViewById(R.id.check_password_remeber);

            remeberCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {

                        remeberType = "1";
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("remember", remeberType);
                        editor.commit();
                    } else {
                        remeberCheck.setChecked(false);
                        remeberType = "0";
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("remember", remeberType);
                        editor.commit();
                    }
                }
            });

            String str = sharedpreferences.getString("remember", null);

            if (str != null && str.length() > 0) {
                if (str.equalsIgnoreCase("1")) {
                    remeberCheck.setChecked(true);
                    remeberType = "1";
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("remember", remeberType);

                    user_name.setText(sharedpreferences.getString("user_name", ""));
                    password.setText(sharedpreferences.getString("password", ""));

                    editor.commit();
                } else {
                    remeberCheck.setChecked(false);
                    remeberType = "0";
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("remember", remeberType);
                    editor.commit();
                }
            }


            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (isValid()) {
                        String userId = user_name.getText().toString();
                        String pass = password.getText().toString();


                        String physicalAdd = macAddress();
                        loginRequest(userId, pass, physicalAdd);

                    }

                }
            });

            signupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            });

        }

        website_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.nimbumirchee.co.in/"));
                startActivity(intent);
            }
        });

    }

    private boolean isValid() {
        if (!(user_name.getText().toString() != null && user_name.getText().toString().trim().length() > 0)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(password.getText().toString() != null && password.getText().toString().trim().length() > 0)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void loginRequest(final String userId, final String pass, final String physicalAddress) {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "Login",
                "Please wait...", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.LOGIN_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {


                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {

                            System.out.println("Login Response : " + loginResponse);
                            JSONObject jsonObject = new JSONObject(loginResponse);

                            if (jsonObject.has("Status") && jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equals("Success")) {

                                    login.setEnabled(false);

                                    String accessToken = jsonObject.getString("AccessToken");
                                    String accountId = jsonObject.getString("UserId");

                                    String accountName = jsonObject.getString("Name");
                                    String userName = jsonObject.getString("UserName");

                                    if (prefs == null) {
                                        prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                                    }
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString(AppController.PREFERENCE_USERNAME, userName);
                                    System.out.println("userId : " + userId);
                                    editor.putString(AppController.PREFERENCE_PASSWORD, pass);
                                    System.out.println("pass : " + pass);
                                    editor.putString(AppController.PREFERENCE_ACCOUNT_ID, accountId);
                                    editor.putString(AppController.PREFERENCE_USER_ID, accountId);
                                    System.out.println("accountId : " + accountId);
                                    editor.putString(AppController.PREFERENCE_ACCOUNT_NAME, accountName);
                                    System.out.println("accountName : " + accountName);
                                    editor.putString(AppController.ACCESS_TOKEN, accessToken);
                                    System.out.println("accountType : " + accessToken);

                                    SharedPreferences prefs_log = getSharedPreferences("LoginSession", MODE_PRIVATE);
                                    SharedPreferences.Editor editor1 = prefs_log.edit();
                                    editor1.putString("user_name", userName);
                                    editor1.putString("password", pass);
                                    editor1.commit();

                                    boolean isEdited = editor.commit();
                                    System.out.println("isEdited : " + isEdited);

                                    loadVehicleList(accountId);


                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();

                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                String md5_pass = PasswordDecoder.md5(pass);
                map.put("PhysicalAddress", physicalAddress);
                map.put("Username", userId);
                map.put("Password", md5_pass);
                map.put("UserType", "transporter");

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }

    /*private void loadFirmList(final String accountId) {
        System.out.println("loadFirm called");
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.FIRM_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String transporterResponse) {
                        dialog.dismiss();
                        try {
                            *//*{"result":"success","account_id":2082,"user_id":"demo_transporter","user_type":"transporter","user_name":"demo_transporter"}*//*

                            System.out.println("Transporter Response : " + transporterResponse);
                            JSONObject jsonObject = new JSONObject(transporterResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.getString("result").equals("success")) {
                                    JSONArray accountArray = jsonObject.getJSONArray("Transporter");
                                    if (accountArray.length() > 0) {
                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_FIRM);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_FIRM + "(" + DBAdapter.ID + "," +
                                                DBAdapter.FIRM_ID + "," +
                                                DBAdapter.FIRM_NAME + "," +
                                                DBAdapter.CONTACT_NAME + "," +
                                                DBAdapter.PERSON_CONTACT_NO + "," +
                                                DBAdapter.HOME_CONTACT_NO + "," +
                                                DBAdapter.OFFICE_CONTACT_NO + "," +
                                                DBAdapter.TIN_NO + "," +
                                                DBAdapter.ADDRESS + "," +
                                                DBAdapter.EMAIL + "," +
                                                DBAdapter.STATE_ID + "," +
                                                DBAdapter.CITY_ID + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,? )";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < accountArray.length(); i++) {
                                            JSONObject jObject = accountArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("firm_id"));
                                            stmt.bindString(3, jObject.getString("firm_name"));
                                            stmt.bindString(4, jObject.getString("contact_name"));
                                            stmt.bindString(5, jObject.getString("personal_contact_no"));
                                            stmt.bindString(6, jObject.getString("home_contact_no"));
                                            stmt.bindString(7, jObject.getString("office_contact_no"));
                                            stmt.bindString(8, jObject.getString("tin_no"));
                                            stmt.bindString(9, jObject.getString("address"));
                                            stmt.bindString(10, jObject.getString("email_id"));
                                            stmt.bindString(11, jObject.getString("state_id"));
                                            stmt.bindString(12, jObject.getString("city_id"));

                                            stmt.execute();
                                        }
                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();

                                    } else {
                                        Toast.makeText(LoginActivity.this, "No Transporter Found in this Account", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        loadVehicleList(accountId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                loadVehicleList(accountId);
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getResources().getString(R.string.server_api_key);
                Map<String, String> map = new HashMap<>();
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching State.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    } */

    private void loadVehicleList(final String accountId) {
        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Fetching State.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        System.out.println("load Vehicle called");

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICLE_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String vehicleResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehicle Response : " + vehicleResponse);
                            JSONObject jsonObject = new JSONObject(vehicleResponse);

                            if (jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equals("Success")) {

                                    JSONArray vehicleArray = jsonObject.getJSONArray("VehicleList");
                                    if (prefs == null) {
                                        prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                                    }
                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString(AppController.PREFERENCE_VEHICLE_COUNT, String.valueOf(vehicleArray.length()));
                                    System.out.println("vehicleCount : " + String.valueOf(vehicleArray.length()));
                                    boolean isEdited = editor.commit();
                                    System.out.println("isEdited : " + isEdited);

                                    SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                    SqliteDB.beginTransaction();

                                    if (vehicleArray.length() > 0) {
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_VEHICLE);
                                        String insertVehicleQuery = "INSERT INTO " + DBAdapter.TABLE_VEHICLE + "(" + DBAdapter.ID + ","
                                                + DBAdapter.VEHICLE_ID + ","
                                                + DBAdapter.VEHICLE_NO + ","
                                                + DBAdapter.IMEI + ","
                                                + DBAdapter.MODEL_NO + ","
                                                + DBAdapter.REGISTRATION_NO + ","
                                                + DBAdapter.INSURANCE_NO + ","
                                                + DBAdapter.VALIDITY_DATE + ","
                                                + DBAdapter.POLLUTION_NO + ","
                                                + DBAdapter.YEAR_OF_PURCHASE + ","
                                                + DBAdapter.CAPACITY + ","
                                                + DBAdapter.VEHICLE_MODEL_ID + ","
                                                + DBAdapter.VEHICLE_DATE_RC + ","
                                                + DBAdapter.VEHICLE_DATE_POLLUTION + ","
                                                + DBAdapter.VEHICLE_ROAD_TAX + ","
                                                + DBAdapter.VEHICLE_DATE_ROAD_TAX + ","
                                                + DBAdapter.VEHICLE_PERMIT_TYPE + ","
                                                + DBAdapter.VEHICLE_PERMIT_REGION_ID + ","
                                                + DBAdapter.VEHICLE_DATE_PERMIT + ","
                                                + DBAdapter.VEHICLE_FITNESS + ","
                                                + DBAdapter.VEHICLE_DATE_FITNESS + ","
                                                + DBAdapter.VEHICLE_OTHER_DATE + ","
                                                + DBAdapter.VEHICLE_OTHER_NO + ","
                                                + DBAdapter.VEHICLE_CREATE_ID + ","
                                                + DBAdapter.VEHICLE_CREATE_DATE + ","
                                                + DBAdapter.VEHICLE_EDIT_ID + ","
                                                + DBAdapter.VEHICLE_STATUS + ","
                                                + DBAdapter.VEHICLE_REMARKS + ","
                                                + DBAdapter.VEHICLE_TYPE + ","
                                                + DBAdapter.VEHICLE_CATEGORY + ","
                                                + DBAdapter.VEHICLE_MAX_SPEED + ","
                                                + DBAdapter.REFRIGERATED + ","
                                                + DBAdapter.CLOSED_DORE + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

                                        SQLiteStatement insertVehicleStatement = SqliteDB.compileStatement(insertVehicleQuery);

                                        for (int i = 0; i < vehicleArray.length(); i++) {

                                            JSONObject jObject = vehicleArray.getJSONObject(i);

                                            insertVehicleStatement.bindString(1, "" + i);
                                            insertVehicleStatement.bindString(2, jObject.getString("vehicle_id"));
                                            insertVehicleStatement.bindString(3, jObject.getString("vehicle_no"));
                                            insertVehicleStatement.bindString(4, "IMEI");
                                            insertVehicleStatement.bindString(5, jObject.getString("model_no"));
                                            insertVehicleStatement.bindString(6, jObject.getString("registration_no"));
                                            insertVehicleStatement.bindString(7, jObject.getString("insurance_no"));
                                            insertVehicleStatement.bindString(8, jObject.getString("insurance_validity"));
                                            insertVehicleStatement.bindString(9, jObject.getString("pollution_no"));
                                            insertVehicleStatement.bindString(10, jObject.getString("year_of_purchase"));
                                            insertVehicleStatement.bindString(11, jObject.getString("capacity"));

                                            insertVehicleStatement.bindString(12, jObject.getString("model_id"));
                                            insertVehicleStatement.bindString(13, jObject.getString("date_rc"));
                                            insertVehicleStatement.bindString(14, jObject.getString("date_pollution"));
                                            insertVehicleStatement.bindString(15, jObject.getString("road_tax_no"));
                                            insertVehicleStatement.bindString(16, jObject.getString("date_road_tax"));
                                            insertVehicleStatement.bindString(17, jObject.getString("permit_type"));
                                            insertVehicleStatement.bindString(18, jObject.getString("permit_region_id"));
                                            insertVehicleStatement.bindString(19, jObject.getString("date_permit_type"));
                                            insertVehicleStatement.bindString(20, jObject.getString("fitness_no"));
                                            insertVehicleStatement.bindString(21, jObject.getString("date_fitness"));

                                            insertVehicleStatement.bindString(22, jObject.getString("date_others"));
                                            insertVehicleStatement.bindString(23, jObject.getString("others_no"));
                                            insertVehicleStatement.bindString(24, jObject.getString("create_id"));
                                            insertVehicleStatement.bindString(25, jObject.getString("create_date"));
                                            insertVehicleStatement.bindString(26, jObject.getString("edit_id"));
                                            insertVehicleStatement.bindString(27, jObject.getString("status"));
                                            insertVehicleStatement.bindString(28, jObject.getString("remarks"));
                                            insertVehicleStatement.bindString(29, jObject.getString("vehicle_type"));
                                            insertVehicleStatement.bindString(30, jObject.getString("category"));
                                            insertVehicleStatement.bindString(31, jObject.getString("max_speed"));

                                            insertVehicleStatement.bindString(32, jObject.getString("refrigerated_nonRefrigerated"));
                                            insertVehicleStatement.bindString(33, jObject.getString("close_open"));

                                            insertVehicleStatement.execute();
                                        }
                                    }
                                    SqliteDB.setTransactionSuccessful();
                                    SqliteDB.endTransaction();
                                    loadStateList(accountId);

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadStateList(accountId);
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");


                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private void startMainActivity() {

        Intent intent = new Intent(LoginActivity.this, TransporterMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadStateList(final String accountId) {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching State.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

      /*  Cursor stateCursor = db.stateList();
        if (!(stateCursor.getCount() > 0)) {*/

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.STATE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray stateArray = jsonObject.getJSONArray("States");
                                    if (stateArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_STATE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_STATE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.STATE_ID + "," +
                                                DBAdapter.STATE_NAME + "," +
                                                DBAdapter.ROAD_PERMIT + "," +
                                                DBAdapter.MIN_ROAD_PERMIT_AMOUNT + ") VALUES (?,?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < stateArray.length(); i++) {
                                            JSONObject jObject = stateArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("StateId"));
                                            stmt.bindString(3, jObject.getString("StateName"));
                                            stmt.bindString(4, "No");
                                            stmt.bindString(5, "0");
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No State Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        loadCityList(accountId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadCityList(accountId);
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");


                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;

            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
        /*} else {
            loadCityList(accountId);
        }
        stateCursor.close();*/

    }

    private void loadCityList(final String accountId) {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching City.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
       /* Cursor cityCursor = db.cityList();
        if (!(cityCursor.getCount() > 0)) {
*/
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.CITY_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("City Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray cityArray = jsonObject.getJSONArray("Cities");
                                    if (cityArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_CITY);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_CITY + "(" + DBAdapter.ID + "," +
                                                DBAdapter.CITY_ID + "," +
                                                DBAdapter.CITY_NAME + "," +
                                                DBAdapter.STATE_ID + ") VALUES (?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < cityArray.length(); i++) {
                                            JSONObject jObject = cityArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("CityId"));
                                            stmt.bindString(3, jObject.getString("CityName"));
                                            stmt.bindString(4, jObject.getString("StateId"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "City Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        loadMaterialList(accountId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadMaterialList(accountId);
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        int x = 2;// retry count
        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
        /*} else {
            loadMaterialList(accountId);
        }*/
    }

    private void loadMaterialList(final String accountId) {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Fetching Materials.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Material Called");
        if (prefs == null) {
            prefs = LoginActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.MATERIAL_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Material Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("MaterialType");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_MATERIAL);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_MATERIAL + "(" + DBAdapter.ID + "," +
                                                DBAdapter.MATERIAL_ID + "," +
                                                DBAdapter.MATERIAL_NAME + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("MaterialTypeId"));
                                            stmt.bindString(3, jObject.getString("MaterialTypeName"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Material Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                        }
                        //
                        documentTitleList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                // vehicalModelList();
                documentTitleList();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    private void documentTitleList() {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching Document Titles.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Title Called");
        if (prefs == null) {
            prefs = LoginActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DOCUMENT_TITLE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehical Model Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("DocumentTitleList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_DOCUMENT_TITLE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_DOCUMENT_TITLE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.DOCUMENT_TITLE + "," +
                                                DBAdapter.DOCUMENT_TITLE_ID + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("DocumentTitle"));
                                            stmt.bindString(3, jObject.getString("DocumentTitleId"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Document Title Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        vehicalTypeList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                vehicalTypeList();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void vehicalTypeList() {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "", "Fetching Vehical Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Vehical Type Called");
        if (prefs == null) {
            prefs = LoginActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICAL_TYPE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehical Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("VehicleTypeList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_VEHICAL_TYPE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_VEHICAL_TYPE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.VEHICAL_TYPE + ") VALUES (?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("VehicleType"));

                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Vehical type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                            driverList();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                            driverList();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                driverList();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void driverList() {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching Vehical Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load driver Type Called");
        if (prefs == null) {
            prefs = LoginActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DRIVER_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Driver Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("DriverList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_DRIVER);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_DRIVER + "(" + DBAdapter.ID + "," +
                                                DBAdapter.DRIVER_ID + "," +
                                                DBAdapter.DRIVER_NAME + "," +
                                                DBAdapter.DRIVER_DL + "," +
                                                DBAdapter.DRIVER_EMAIL + "," +
                                                DBAdapter.DRIVER_AGE + "," +
                                                DBAdapter.DRIVER_PHONE + "," +
                                                DBAdapter.DRIVER_SEX + "," +
                                                DBAdapter.DRIVER_REMARK + ") VALUES (?,?,?,?,?,?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("driver_id"));
                                            stmt.bindString(3, jObject.getString("name"));
                                            stmt.bindString(4, jObject.getString("dl_number"));
                                            stmt.bindString(5, jObject.getString("email"));
                                            stmt.bindString(6, jObject.getString("age"));
                                            stmt.bindString(7, jObject.getString("phone_no"));
                                            stmt.bindString(8, jObject.getString("sex"));
                                            stmt.bindString(9, jObject.getString("remarks"));


                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Vehical type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                            expenseTypeList();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                            expenseTypeList();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                expenseTypeList();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void expenseTypeList() {

        final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "",
                "Fetching Expense Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Expense Type Called");
        if (prefs == null) {
            prefs = LoginActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.EXPENSE_TYPE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Expense Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray expenseArray = jsonObject.getJSONArray("ExpenseTypeList");
                                    if (expenseArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_EXPENSE_TYPE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_EXPENSE_TYPE + "(" + DBAdapter.ID + "," + DBAdapter.EXPENSE_TYPE_ID + "," + DBAdapter.EXPENSE_TYPE + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < expenseArray.length(); i++) {
                                            JSONObject jObject = expenseArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("ExpenseTypeId"));
                                            stmt.bindString(3, jObject.getString("ExpenseTypeName"));

                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Expense type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }

                            startMainActivity();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                            startMainActivity();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                startMainActivity();
                Toast.makeText(LoginActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("onStop called");
        //  db.close();
    }


    public String macAddress() {

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        return macAddress;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }

    private boolean checkGooglePlayServicesAvailable() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status == ConnectionResult.SUCCESS) {
            return true;
        }
        Log.e("LOGIN", "Google Play Services not available: " + GooglePlayServicesUtil.getErrorString(status));
        if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
            final Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(status, this, 1);
            if (errorDialog != null) {
                errorDialog.show();
            }
        }

        return false;
    }

    public static final int WRITE_EXTERNAL_STORAGE = 101;
    public static final int EXCESS_FINE_LOCATION = 102;
    public static final int EXCESS_COURSE_LOCATION = 103;
    public static final int READ_PHONE_STATE = 104;


    private boolean checkPermissionStorage() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionPhone() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionLocation1() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, EXCESS_COURSE_LOCATION);
                return false;
            }

        }
        return true;
    }

    private boolean checkPermissionLocation2() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, EXCESS_FINE_LOCATION);
                return false;
            }

        }
        return true;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case WRITE_EXTERNAL_STORAGE:
            case EXCESS_COURSE_LOCATION:
            case EXCESS_FINE_LOCATION:
            case READ_PHONE_STATE:
                activityRestart();
                break;
        }
    }

    public void activityRestart() {
        finish();
        startActivity(new Intent(this, LoginActivity.class));
    }

}
