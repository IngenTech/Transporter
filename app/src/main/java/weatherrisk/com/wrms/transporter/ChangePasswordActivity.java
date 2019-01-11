package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

/**
 * Created by Admin on 23-03-2017.
 */
public class ChangePasswordActivity extends AppCompatActivity {


    Button changePassBtn;

    EditText oldPasswordText;
    EditText newPasswordText;
    EditText reEnterPassword;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        changePassBtn = (Button)findViewById(R.id.changePasswordBTN);
        oldPasswordText =(EditText)findViewById(R.id.forgot_old_password);
        newPasswordText = (EditText)findViewById(R.id.forgot_new_password);
        reEnterPassword = (EditText)findViewById(R.id.forgot_new_re_password);

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isValid()) {
                    String old_password = oldPasswordText.getText().toString();
                    String new_password = newPasswordText.getText().toString();

                    final ProgressDialog progressDialog = new ProgressDialog(ChangePasswordActivity.this, R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.show();


                    if (prefs == null) {
                        prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                    }
                    String user_id = (prefs.getString(AppController.PREFERENCE_USER_ID, "0"));
                    String physical_address = (prefs.getString(macAddress(), "0"));
                    String access_token = (prefs.getString(AppController.ACCESS_TOKEN, "0"));


                    changePassRequest(user_id,old_password,new_password,access_token,physical_address,progressDialog);
                }
            }
        });

    }

    private boolean isValid(){
        if(!(oldPasswordText.getText().toString()!=null && oldPasswordText.getText().toString().trim().length()>0)){
            Toast.makeText(this,"Please enter old password",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(newPasswordText.getText().toString()!=null && newPasswordText.getText().toString().trim().length()>0)){
            Toast.makeText(this,"Please enter new password",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(reEnterPassword.getText().toString()!=null && reEnterPassword.getText().toString().trim().length()>0)){
            Toast.makeText(this,"Please re-enter password",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPasswordText.getText().toString().equalsIgnoreCase(reEnterPassword.getText().toString())){
            Toast.makeText(this,"New password and re password must be same.",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void changePassRequest(final String userId,final String oldPassword,final String newPassword,final String accessToken,final String physicalAddress, final ProgressDialog progressDialog) {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.CHANGE_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        progressDialog.dismiss();
                        Log.d("Change", "Change pass Response : " + loginResponse);
                        try {

                            JSONObject jsonObject = new JSONObject(loginResponse);

                            String status = jsonObject.has("Status") ? jsonObject.getString("Status") : "";


                            if (status!=null && status.equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equalsIgnoreCase("success")) {

                                    String succMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Blank Response";

                                    String accessToken = jsonObject.has("AccessToken") ? jsonObject.getString("AccessToken") : "Blank Response";


                                    onChangeSuccess(succMessage,newPassword,accessToken);

                                }
                            } else {
                                String failMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Blank Response";
                                onChangeFailed(failMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onChangeFailed("Not able parse response");
                        }
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                progressDialog.cancel();
                onChangeFailed("Not able to connect with server");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                String md5_pass_old = MyUtility.md5(oldPassword);
                String md5_pass_new = MyUtility.md5(newPassword);
                map.put("PhysicalAddress", physicalAddress);
                map.put("UserId", userId);
                map.put("OldPassword", md5_pass_old);
                map.put("NewPassword", md5_pass_new);
                map.put("AccessToken", accessToken);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }

    public void onChangeSuccess(String message,String newPassword,String accessToken) {

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppController.PREFERENCE_USER_ID, newPassword);
        editor.putString(AppController.ACCESS_TOKEN, accessToken);

        editor.commit();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              /*  dialogInterface.dismiss();
                Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);*/
                finish();
            }
        });
        builder.show();
    }


    public void onChangeFailed(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Failed");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public String macAddress(){

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();

        return macAddress;
    }
}
