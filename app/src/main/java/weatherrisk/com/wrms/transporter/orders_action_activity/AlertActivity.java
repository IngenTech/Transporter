package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.HaltReportAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.bean.AlertBean;
import weatherrisk.com.wrms.transporter.bean.HaltData;
import weatherrisk.com.wrms.transporter.utils.AppConstant;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 26-04-2017.
 */
public class AlertActivity extends AppCompatActivity {

    RecyclerView listView;
    private String order_id;
    private String accessToken;
    private String userID;
    SharedPreferences prefs;
    DBAdapter db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(this);
        db.open();

        order_id = getIntent().getStringExtra("trip_id");

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        userID = prefs.getString(AppController.PREFERENCE_USER_ID, "");
        accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

        listView = (RecyclerView) findViewById(R.id.haltList);
        listView.setLayoutManager(new LinearLayoutManager(this));

        getAlertList(userID, order_id, accessToken);

    }


    ProgressDialog progressDialog;

    private void getAlertList(final String accountId, final String orderId, final String accessToken) {


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.REMINDER_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String travelResponse) {

                        Log.v("response",travelResponse.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(travelResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray reminderArray = jsonObject.getJSONArray("ReminderList");

                                    List<AlertBean> listDataChildItem = new ArrayList<AlertBean>();

                                    if (reminderArray.length() > 0) {
                                        for (int j = 0; j < reminderArray.length(); j++) {

                                            AlertBean bean = new AlertBean();
                                            JSONObject alertJO = reminderArray.getJSONObject(j);

                                            String alertId = alertJO.getString("ReminderId");
                                            String alertMsg = alertJO.getString("ReminderRemark");
                                            String vehicleId = alertJO.getString("VehicleId");
                                            String alertTypeId = alertJO.getString("ReminderTypeId");
                                            String reminderExpDate = alertJO.getString("ReminderExpiryDate");

                                            Cursor vehicleCursor = db.getVehicleById(vehicleId);
                                            if (vehicleCursor.moveToFirst()) {
                                                do {
                                                    bean.setVehicleName(vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_NO)));
                                                } while (vehicleCursor.moveToNext());
                                            }

                                            bean.setReminderTypeId(alertTypeId);
                                            bean.setReminderRemark(alertMsg);
                                            bean.setReminderId(alertId);
                                            bean.setReminderExpiryDate(reminderExpDate);
                                            listDataChildItem.add(bean);
                                        }

                                        AlertAdapter adapter = new AlertAdapter(AlertActivity.this, (ArrayList<AlertBean>) listDataChildItem);
                                        listView.setAdapter(adapter);

                                    } else {
                                        NoInternetConnectionAdapter adapterNo = new NoInternetConnectionAdapter("You don't have any alert.");
                                        listView.setAdapter(adapterNo);
                                    }


                                } else {

                                    String msg = jsonObject.getString("Message");

                                    Toast.makeText(getApplicationContext(), "" + msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.cancel();
                volleyError.printStackTrace();
                Toast.makeText(getApplicationContext(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                //  map.put("TripId", orderId);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        int x = 2;// retry count
        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(this, "", "Fetching Travel Report...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

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


}