package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import weatherrisk.com.wrms.transporter.adapter.HaltReportAdapter;
import weatherrisk.com.wrms.transporter.bean.HaltData;
import weatherrisk.com.wrms.transporter.utils.AppConstant;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 19-04-2017.
 */
public class HaltReportActivity extends AppCompatActivity  {

    RecyclerView listView;
    private String order_id;
    private String accessToken;
    private String userID;
    SharedPreferences prefs;
    TextView vehical_name;
    TextView imei_no;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.halt_report_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        order_id = getIntent().getStringExtra("trip_id");

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        userID = prefs.getString(AppController.PREFERENCE_USER_ID, "");
        accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

        vehical_name = (TextView)findViewById(R.id.halt_vehicalName);
        imei_no = (TextView)findViewById(R.id.halt_imei);

        listView = (RecyclerView) findViewById(R.id.haltList);
        listView.setLayoutManager(new LinearLayoutManager(this));

        getHaltReport(userID,order_id,accessToken);

    }


    private void getHaltReport(final String accountId, final String orderId, final String accessToken) {


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.HALT_REPORT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String travelResponse) {
                        try {


                            JSONObject jsonObject = new JSONObject(travelResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray haltArray = jsonObject.getJSONArray("HaltReport");
                                    if (haltArray.length() > 0) {

                                        List<HaltData> listDataChildItem = new ArrayList<HaltData>();

                                        if (haltArray.length() > 0) {
                                            for (int j = 0; j < haltArray.length(); j++) {

                                                JSONObject haltJsonObject = haltArray.getJSONObject(j);

                                                String imei = haltJsonObject.getString("DeviceIMEINo");
                                                String vehicalName = haltJsonObject.getString("VehicleName");

                                                imei_no.setText(imei);
                                                vehical_name.setText(vehicalName);

                                                String arrivalTime = haltJsonObject.getString("ArrivalTime");
                                                String departureTime = haltJsonObject.getString("DepartureTime");
                                                String haltDuration = haltJsonObject.getString("HaltDuration");
                                                String startLatitude = haltJsonObject.getString("Latitude");
                                                String startLongitude = haltJsonObject.getString("Longitude");
                                                LatLng startLatLng = new LatLng(Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                                                HaltData haltData = new HaltData("", "",arrivalTime, departureTime,haltDuration, startLatLng);
                                                getAddressOfStartLatLng(haltData);
                                                listDataChildItem.add(haltData);


                                            }
                                        }


                                        if (listDataChildItem.size()>0){
                                            HaltReportAdapter adapter = new HaltReportAdapter(HaltReportActivity.this, (ArrayList<HaltData>) listDataChildItem);
                                            listView.setAdapter(adapter);
                                        }

                                    } else {
                                        Toast.makeText(getApplicationContext(), "No Data found for the vehicle", Toast.LENGTH_LONG).show();
                                    }

                                } else {

                                    String msg = jsonObject.getString("Message");

                                    Toast.makeText(getApplicationContext(), ""+msg, Toast.LENGTH_LONG).show();
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
                map.put("TripId", orderId);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        int x=2;// retry count
        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(this, "", "Fetching Travel Report...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }
    ProgressDialog progressDialog;
    public void getAddressOfStartLatLng(final HaltData data) {

        String address = String.format(Locale.ENGLISH, AppConstant.API.ADDRESS_OF_LAT_LNG_API, data.getStartLatLong().latitude, data.getStartLatLong().longitude);

        System.out.println("requestedAddress : "+address);

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.GET, address,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String addressResponse) {
                        System.out.println("addressResponse : "+addressResponse);
                        try {
                            String address = "Could not retrieve address";
                            JSONObject jsonObject = new JSONObject();
                            jsonObject = new JSONObject(addressResponse.toString());

                            List<Address> addresses = new ArrayList<>();

                            if ("OK".equalsIgnoreCase(jsonObject.getString("status"))) {
                                JSONArray results = jsonObject.getJSONArray("results");
                                for (int i = 0; i < results.length(); i++) {
                                    JSONObject result = results.getJSONObject(i);
                                    String indiStr = result.getString("formatted_address");
                                    Address addr = new Address(Locale.getDefault());
                                    addr.setAddressLine(0, indiStr);
                                    addresses.add(addr);
                                }
                            }
                            address = addresses.get(0).getAddressLine(0);
                            String city = addresses.get(0).getAddressLine(1);
                            String country = addresses.get(0).getAddressLine(2);

                            if(city!=null){
                                address = city +" "+ address;
                            }

                            if(country!=null){
                                address = address +" "+ country;
                            }

                            address.replaceAll("null", "");


                            data.setPlace(address);


                            System.out.println("GOT ADDRESS FROM GOOGLE API : " + address);

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.setPlace("Could not retrieve address");

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                data.setPlace("Could not retrieve address");

            }
        });

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