package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.PasswordDecoder;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name)
    EditText mNameTxt;

    @Bind(R.id.input_contact)
    EditText mContactTxt;
    @Bind(R.id.input_email)
    EditText mEmailTxt;

    @Bind(R.id.input_user_id)
    EditText mUserIdText;
    @Bind(R.id.input_password)
    EditText mPasswordText;
    @Bind(R.id.input_reEnterPassword)
    EditText mReEnterPasswordText;
    @Bind(R.id.btn_signup)
    Button mSignupButton;
    @Bind(R.id.link_login)
    TextView mLoginLink;

    private SharedPreferences prefs;
    private TelephonyManager mTelephonyManager;
    private String mIMEI;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);


        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
//                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed("Signup Failed");
            return;
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = mNameTxt.getText().toString();
        String userName = mUserIdText.getText().toString();
        String password = mPasswordText.getText().toString();
        String email = mEmailTxt.getText().toString();
        String contact = mContactTxt.getText().toString();


        otpRequest(userName, password,contact, name,email, progressDialog);

    }


    public void onSignupSuccess(String msg,String user_ID) {
        mSignupButton.setEnabled(true);
        setResult(RESULT_OK, null);

        String userName = mNameTxt.getText().toString();
        String userId = mUserIdText.getText().toString();
        String password = mPasswordText.getText().toString();
        String contactNo = mContactTxt.getText().toString();
        String email = mEmailTxt.getText().toString();

        finish();
        Intent otpVerificationIntent = new Intent(this, OtpVerificationActivity.class);
        //otpVerificationIntent.putExtra(OtpVerificationActivity.OTP, otp);
        otpVerificationIntent.putExtra(OtpVerificationActivity.CONTACT_NO, contactNo);
        otpVerificationIntent.putExtra(OtpVerificationActivity.USER_NAME, userName);
        otpVerificationIntent.putExtra(OtpVerificationActivity.USER_ID, userId);
        otpVerificationIntent.putExtra(OtpVerificationActivity.EMAIL, email);
        otpVerificationIntent.putExtra(OtpVerificationActivity.PASS, password);
        otpVerificationIntent.putExtra(OtpVerificationActivity.USER_TYPE, "transporter");
        otpVerificationIntent.putExtra(OtpVerificationActivity.OTP_TYPE, OtpVerificationActivity.SIGN_UP);
        otpVerificationIntent.putExtra("userID", user_ID);
        startActivity(otpVerificationIntent);

    }

    private void otpRequest(final String userName, final String pass,final String contact, final String name, final String email, final ProgressDialog progressDialog) {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.REGISTRATION_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        progressDialog.dismiss();
                        Log.d(TAG, "SIgnup Response : " + loginResponse);
                        try {

                            JSONObject jsonObject = new JSONObject(loginResponse);
                            String status = jsonObject.has("Status") ? jsonObject.getString("Status") : "";

                            if (status!=null && status.equalsIgnoreCase("1")) {

                                if (jsonObject.getString("Result").equalsIgnoreCase("success")) {
                                    //   String otpString = jsonObject.has("otp") ? jsonObject.getString("otp") : "";
                                    String messageString = jsonObject.has("Message") ? jsonObject.getString("Message") : "";
                                    String user_ID = jsonObject.has("UserId") ? jsonObject.getString("UserId") : "";

                                    Log.v("userID",user_ID);

                                    onSignupSuccess(messageString,user_ID);
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

                String password = PasswordDecoder.md5(pass);

                map.put("Name",name);
                map.put("Username", userName);
                map.put("Password", password);
                map.put("Email", email);
                map.put("ContactNo", contact);
                map.put("UserType","transporter");

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }


    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG).show();

        mSignupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String crnNo = mNameTxt.getText().toString();
        String userId = mUserIdText.getText().toString();
        String email = mEmailTxt.getText().toString();
        String contact = mContactTxt.getText().toString();
        String password = mPasswordText.getText().toString();
        String reEnterPassword = mReEnterPasswordText.getText().toString();

        if (crnNo.isEmpty() || crnNo.length() < 3) {
            mNameTxt.setError("at least 3 characters");
            valid = false;
        } else {
            mNameTxt.setError(null);
        }

        if (contact.isEmpty() || contact.length() < 10) {
            mContactTxt.setError("at least 10 characters");
            valid = false;
        } else {
            mContactTxt.setError(null);
        }

        if (email.isEmpty() || email.length() < 12) {
            mEmailTxt.setError("at least 12 characters");
            valid = false;
        } else {
            mNameTxt.setError(null);
        }

        if (userId.isEmpty()) {
            mUserIdText.setError("Enter Valid UserId");
            valid = false;
        } else {
            mUserIdText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mPasswordText.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mPasswordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            mReEnterPasswordText.setError("Password do not match");
            valid = false;
        } else {
            mReEnterPasswordText.setError(null);
        }

        return valid;
    }

}
