package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DistanceReportAdapter;
import weatherrisk.com.wrms.transporter.bean.DistanceData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 19-04-2017.
 */
public class DistanceReportActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressDialog dialog;
    SharedPreferences prefs;
    String order_id;
    DistanceData distanceData;
    ArrayList<DistanceData> distArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        order_id = getIntent().getStringExtra("trip_id");

        recyclerView = (RecyclerView)findViewById(R.id.distanceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        distanceReport();
    }


    private void distanceReport() {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DISTANCE_REPORT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String tripResponse) {
                        dialog.cancel();
                        try {

                            System.out.println("Distance Report Response : " + tripResponse);
                            JSONObject jsonObject = new JSONObject(tripResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.getString("Result").equals("Success")) {

                                    JSONArray jsonArray = jsonObject.getJSONArray("DistanceReport");

                                    distArray = new ArrayList<DistanceData>();

                                    for (int d=0; d<jsonArray.length(); d++){

                                        distanceData = new DistanceData();
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(d);

                                        distanceData.setDeviceImei(jsonObject1.getString("DeviceIMEI"));
                                        distanceData.setDateFrom(jsonObject1.getString("DateFrom"));
                                        distanceData.setVehicalName(jsonObject1.getString("VehicleName"));
                                        distanceData.setDateTo(jsonObject1.getString("DateTo"));
                                        distanceData.setDistance(jsonObject1.getString("Distance"));

                                        distArray.add(distanceData);
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_LONG).show();

                                }
                            } else if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("2"))) {

                            }else {
                                Toast.makeText(getApplicationContext(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();

                        if (distArray.size()>0){

                            DistanceReportAdapter adapter = new DistanceReportAdapter(DistanceReportActivity.this,distArray);
                            recyclerView.setAdapter(adapter);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID,"");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN,"");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("TripId", order_id);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                return map;
            }
        };
        dialog = ProgressDialog.show(this, "", "Fetching distance report...", true);
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
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