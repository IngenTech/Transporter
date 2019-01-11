package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.utils.MyUtility;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final String TAG = OtpVerificationActivity.class.getSimpleName();

    public static final String OTP_TYPE = "otp_type";
    public static final String SIGN_UP = "sign_up";
    public static final String FORGET_PASSWORD = "forget_password";
    public static final String SIGN_IN = "sign_in";

    public static final String OTP = "otp";
    public static final String CONTACT_NO = "contact_no";
    public static final String USER_NAME = "user_name";
    public static final String USER_ID = "user_id";
    public static final String EMAIL = "email";
    public static final String PASS = "pass";
    public static final String USER_TYPE = "user_type";

    private ProgressDialog dialog;
    private SharedPreferences prefs;

    EditText otp_edt;
    Button verify_otp_button;

    String otpType;
   // String otpString;
    String contactNoString;
    String userNameString;
    String userIdString;
    String passString;
    String emailIdString;


    boolean isForgetPassword = false;
    String user_ID;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


        otp_edt = (EditText) findViewById(R.id.otp_edt);
        verify_otp_button = (Button) findViewById(R.id.verify_otp_button);

      //  otpString = getIntent().getStringExtra(OTP);
        otpType = getIntent().getStringExtra(OTP_TYPE);
        if(otpType.equals(SIGN_UP)) {

            user_ID = getIntent().getStringExtra("userID");
            contactNoString = getIntent().getStringExtra(CONTACT_NO);
            userNameString = getIntent().getStringExtra(USER_NAME);
            userIdString = getIntent().getStringExtra(USER_ID);
            emailIdString = getIntent().getStringExtra(EMAIL);
            passString = getIntent().getStringExtra(PASS);
            userType = getIntent().getStringExtra(USER_TYPE);
        }
        if(otpType.equals(SIGN_IN)){
            passString = getIntent().getStringExtra(PASS);
            userIdString = getIntent().getStringExtra(USER_ID);
        }

        if(otpType.equals(FORGET_PASSWORD)){
            contactNoString = getIntent().getStringExtra(CONTACT_NO);
            userIdString = getIntent().getStringExtra(USER_ID);
        }



        verify_otp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (isValidOtp(otpString)) {

                    if(otpType.equals(SIGN_IN)) {
                        if (prefs == null) {
                            prefs = getSharedPreferences(AppConstant.Preference.APP_PREFERENCE, MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(AppConstant.Preference.IS_LOGIN, true);
                        editor.commit();
                        Intent intent = new Intent(OtpVerificationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }*/

                    if(otpType.equals(SIGN_UP)) {
                        final ProgressDialog progressDialog = new ProgressDialog(OtpVerificationActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("OTP verification...");
                        progressDialog.show();

                        String otp = otp_edt.getText().toString().trim();
                        String physical_add = macAddress();
                        user_ID = getIntent().getStringExtra("userID");

                        Log.v("userIDDDDD",user_ID);

                        otpRequest(user_ID, otp, physical_add, progressDialog);
                    }

                }

        });
    }


    public void onSignupSuccess(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("REGISTRATION").
                setMessage(message).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        setResult(RESULT_OK, null);
                        Intent intent = new Intent(OtpVerificationActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
        builder.show();

    }


    private void otpRequest(final String userID, final String otp, final String physical_add, final ProgressDialog progressDialog) {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.OTP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Otp verification Response : " + loginResponse);
                        try {

                            JSONObject jsonObject = new JSONObject(loginResponse);
                            String status = jsonObject.has("Status") ? jsonObject.getString("Status") : "";

                            if (status!=null && status.equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equalsIgnoreCase("success")) {
                                    insertAccountInfo(jsonObject);
                                    onSignupSuccess("User has been registered");
                                }
                            } else {
                                String failMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Blank Response";

                                onSignupFailed(failMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onSignupFailed("Not able parse response");
                        }
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                progressDialog.cancel();
                onSignupFailed("Not able to connect with server");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("UserId", userID);
                map.put("OTP", otp);
                map.put("PhysicalAddress", physical_add);


                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }

    public void onSignupFailed(String meaasge) {
        Toast.makeText(getBaseContext(), meaasge, Toast.LENGTH_LONG).show();
    }

    private boolean insertAccountInfo(JSONObject jsonObject) {
        boolean result = false;

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();
        try {

           /* String accountName = jsonObject.has("user_name") ? jsonObject.getString("user_name") : "";
            editor.putString(AppConstant.Preference.ACCOUNT_NAME, accountName);

            String accountId = jsonObject.has("account_id") ? jsonObject.getString("account_id") : "";
            editor.putString(AppConstant.Preference.ACCOUNT_ID, accountId);

            String contactNo = jsonObject.has("mobile") ? jsonObject.getString("mobile") : "";
            editor.putString(AppConstant.Preference.CONTACT_NO, contactNo);

            String accountType = jsonObject.has("app_type") ? jsonObject.getString("app_type") : "";
            editor.putString(AppConstant.Preference.ACCOUNT_TYPE, accountType);

            String userId = jsonObject.has("user_id") ? jsonObject.getString("user_id") : "";
            editor.putString(AppConstant.Preference.USER_ID, userId);

            editor.putString(AppConstant.Preference.PASSWORD, passString);*/


            String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "";
       //   Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();


            String accessToken = jsonObject.has("AccessToken") ? jsonObject.getString("AccessToken") : "";
            editor.putString(AppController.ACCESS_TOKEN, accessToken);

            String userId = jsonObject.has("UserId") ? jsonObject.getString("UserId") : "";
            editor.putString(AppController.PREFERENCE_USER_ID, userId);

            editor.putString(AppController.PREFERENCE_PASSWORD, passString);


        } catch (Exception e) {
            e.printStackTrace();
        }

        result = editor.commit();


        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String macAddress(){

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
                    || y < w.getTop() || y > w.getBottom()) ) {

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }

}
