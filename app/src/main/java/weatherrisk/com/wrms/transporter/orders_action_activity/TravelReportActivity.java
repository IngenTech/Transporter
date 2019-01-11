package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ExpandableListView;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.TravelListAdapter;
import weatherrisk.com.wrms.transporter.bean.TravelData;
import weatherrisk.com.wrms.transporter.utils.AppConstant;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 24-03-2017.
 */
public class TravelReportActivity extends AppCompatActivity {

    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String INTERVAL = "intervalButton";
    private static final String VEHICLE = "vehicle";
    public static final String FRAGMENT_TAG = "Travel";

    private String startTime;
    private String endTime;
    private String interval;

    DecimalFormat df = new DecimalFormat("#.##");
    ExpandableListView expendableListView;
    TravelListAdapter adapter;
    TextView durationTag;
    double totalDist = 0.0;
    List<String> header = new ArrayList<>();
    HashMap<String,List<TravelData>> listDataChild = new HashMap<>();
   // private ArrayList<VehicleData> vehicleDataList;
   private String order_id;
    private String accessToken;
    private String userID;
    SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.travel_report_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        order_id = getIntent().getStringExtra("trip_id");

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        userID = prefs.getString(AppController.PREFERENCE_USER_ID, "");
        accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

        expendableListView = (ExpandableListView)findViewById(R.id.travel_report_list);
        expendableListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        durationTag = (TextView)findViewById(R.id.title);
        durationTag.setText("Travel Report");



        getVehiclesTravelReport(userID,order_id,accessToken);

        adapter = new TravelListAdapter(this,header,listDataChild);
        expendableListView.setAdapter(adapter);
    }


    private void getVehiclesTravelReport(final String accountId, final String orderId, final String accessToken) {


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.TRAVEL_REPORT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String travelResponse) {
                        try {


                            JSONObject jsonObject = new JSONObject(travelResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray vehicleArray = jsonObject.getJSONArray("TravelReport");
                                    if (vehicleArray.length() > 0) {

                                        List<TravelData> listDataChildItem = new ArrayList<>();
                                        double totalDistance = 0.0;

                                        if (vehicleArray.length() > 0) {
                                            for (int j = 0; j < vehicleArray.length(); j++) {


                                                JSONObject distanceJsonObject = vehicleArray.getJSONObject(j);
                                                double distance = distanceJsonObject.getDouble("DistanceTravelled");
                                                String startDateTime = distanceJsonObject.getString("DateFrom");
                                                String endDateTime = distanceJsonObject.getString("DateTo");

                                           /* String startLat= distanceJsonObject.getString("LatStart");
                                                String startLong = distanceJsonObject.getString("LongStart");

                                                if (startLat!=null && startLong!=null) {
                                                     startLatitude = method(startLat);
                                                     startLongitude = method(startLong);
                                                }

                                                String endLat = distanceJsonObject.getString("LatEnd");
                                                String endLong = distanceJsonObject.getString("LongEnd");

                                                if (endLat!=null && endLong!=null) {

                                                     endLatitude = method(endLat);
                                                     endLongitude = method(endLong);
                                                }
*/

                                                String startLatitude = distanceJsonObject.getString("LatStart");
                                                String startLongitude = distanceJsonObject.getString("LongStart");

                                                String endLatitude = distanceJsonObject.getString("LatEnd");
                                                String endLongitude = distanceJsonObject.getString("LongEnd");

                                                String traveledTime = distanceJsonObject.getString("TravelTime");
                                                String maxSpeed = distanceJsonObject.getString("MaxSpeed");

                                                LatLng startLatLng = new LatLng(Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                                                LatLng endLatLng = new LatLng(Double.parseDouble(endLatitude), Double.parseDouble(endLongitude));

                                                TravelData TravelData = new TravelData(startDateTime, endDateTime, String.valueOf(distance), traveledTime, startLatLng, endLatLng);
                                                getAddressOfStartLatLng(TravelData);
                                                listDataChildItem.add(TravelData);

                                                totalDistance = totalDistance + distance;
                                            }
                                        }

                                        float aaa = (float) totalDistance;

                                        String headerString = "Travel Distance" +aaa;

                                        header.add(headerString);
                                        listDataChild.put(headerString, listDataChildItem);
                                        adapter.notifyDataSetChanged();



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
//                System.out.println("Get Params has been called");
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
    public void getAddressOfStartLatLng(final TravelData data) {

        String address = String.format(Locale.ENGLISH, AppConstant.API.ADDRESS_OF_LAT_LNG_API, data.getStartLatLng().latitude, data.getStartLatLng().longitude);

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


                            data.setStartPlace(address);
                            getAddressOfEndLatLng(data);

                            System.out.println("GOT ADDRESS FROM GOOGLE API : " + address);

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.setStartPlace("Could not retrieve address");
                            getAddressOfEndLatLng(data);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                data.setStartPlace("Could not retrieve address");
                getAddressOfEndLatLng(data);
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

    public void getAddressOfEndLatLng(final TravelData data) {

        String address = String.format(Locale.ENGLISH, AppConstant.API.ADDRESS_OF_LAT_LNG_API,  data.getEndLatLng().latitude, data.getEndLatLng().longitude);

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


                            data.setEndPlace(address);

                            System.out.println("GOT ADDRESS FROM GOOGLE API : "+address);

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.setEndPlace("Could not retrieve address");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                data.setEndPlace("Could not retrieve address");
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }


    public String method(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length()-1)=='x') {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

}
