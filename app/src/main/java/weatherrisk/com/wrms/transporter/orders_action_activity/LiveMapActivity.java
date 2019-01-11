package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;

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
import weatherrisk.com.wrms.transporter.adapter.InfoWindowAdapter;
import weatherrisk.com.wrms.transporter.bean.LiveData;
import weatherrisk.com.wrms.transporter.dataobject.KeyValueData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 29-03-2017.
 */
public class LiveMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;
    AutoCompleteTextView searchVehicle;
    ArrayList<Marker> vehiclesMarker = new ArrayList<>();
    ArrayList<LiveData> vehiclesLiveArray = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String VEHICLE_LIST = "vehicleList";
    public static final String FRAGMENT_TAG = "Live";

    private String orderId;
    private String accessToken;
    private String userID;
    private String interval = "1";


    // TODO: Rename and change types of parameters


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_live_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        orderId = getIntent().getStringExtra("trip_id");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        int intervalInt = 10000;
        try {
            intervalInt = Integer.parseInt(interval) * 60;
        } catch (NumberFormatException e) {

            e.printStackTrace();
        }
        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        userID = prefs.getString(AppController.PREFERENCE_USER_ID, "");
        accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

        //customHandler.postDelayed(updateTimerThread, 0);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public LiveMapActivity() {
        // Required empty public constructor
    }


    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googlMap) {
        googleMap = googlMap;
        getLiveLocation();
    }

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            getLiveLocation();
            customHandler.postDelayed(this, 30000);
        }
    };


    SharedPreferences prefs;


    @Override
    public void onStart() {
        super.onStart();
        // customHandler.postDelayed(updateTimerThread, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        // customHandler.removeCallbacks(updateTimerThread);
    }

    private void getLiveLocation() {


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.LIVE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        if (this != null) {
                            try {
                                System.out.println("Live Location Response : " + loginResponse);
                                JSONObject jsonObject = new JSONObject(loginResponse);

                                if (jsonObject.has("Status") && jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                    if (jsonObject.get("Result").equals("Success")) {

                                        IconGenerator iconFactory = new IconGenerator(LiveMapActivity.this);

                                        LiveData data = new LiveData();

                                        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                        try {
                                            String haltTime = jsonObject.getString("LastHaltTime");
                                            String runningStatus = jsonObject.getString("VehicleRunningStatus");
                                            String vehicleName = jsonObject.getString("VehicleType");
                                            String imei = jsonObject.getString("DeviceIMEINo");

                                            String latit = jsonObject.getString("Latitude");
                                            String longi = jsonObject.getString("Longitude");

                                            String latitude = "";
                                            String longitude = "";

                                            if (latit != null && longi != null) {

                                                latitude = method(latit);
                                                longitude = method(longi);
                                            }

                                            String deviceDateTime = jsonObject.getString("LastHaltTime");

                                            String speed = jsonObject.getString("Speed");

//                                                    String runningStatus = "1";

                                            if (vehiclesLiveArray != null) {
                                                boolean isUpdated = false;
                                                for (LiveData liveData : vehiclesLiveArray) {
                                                    if (liveData.equals(vehicleName)) {
                                                        System.out.println("Existing vehicle updated");
                                                        liveData.setImei(imei);
                                                        liveData.setVehicleNo(vehicleName);
                                                        liveData.setDeviceDateTime(deviceDateTime);
                                                        liveData.setSpeed(speed);

                                                        liveData.setHaltTime(haltTime);
                                                        liveData.setRunningStatus(runningStatus);
                                                        liveData.addPoint(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                                                        liveData.setLat(latitude);
                                                        liveData.setLon(longitude);
                                                        data = liveData;
                                                        isUpdated = true;
                                                        break;
                                                    }
                                                }

                                                if (!isUpdated) {
                                                    ArrayList<LatLng> allPoints = new ArrayList<>();
                                                    allPoints.add(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
                                                    data = new LiveData(Double.parseDouble(latitude), Double.parseDouble(longitude), imei, vehicleName, deviceDateTime, speed, haltTime, runningStatus, "", "IO", allPoints);
                                                    vehiclesLiveArray.add(data);
//                                                            System.out.println("NewVehicleAdded");
                                                }
//                                                        System.out.println("VehicleListLength : "+vehiclesLiveArray.size());
                                            }

                                            builder.include(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));

                                            Marker perth = null;

                                            perth = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude))).title("Live").snippet("snippest").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        IconGenerator tc = new IconGenerator(LiveMapActivity.this);
                                        tc.setTextAppearance(R.style.ClusterIcon_TextAppearance1);


                                        try {
                                            if (vehiclesLiveArray.size() > 0) {

                                                int padding = 150;
                                                // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                                googleMap.moveCamera(cu);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_LONG).show();
                                            }
                                        } catch (Exception e) {
                                            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                                                @Override
                                                public void onMapLoaded() {
                                                    if (vehiclesLiveArray.size() > 0) {
                                                        int padding = 150;
                                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                                        googleMap.moveCamera(cu);
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Blank Response", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Not able parse response", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (this != null) {
                    volleyError.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not able to connect with server", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_USER_ID, "0"));
                accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
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

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                String vehicleNo = marker.getTitle();
                System.out.println("marker title vehicleNo : " + vehicleNo);
                if (vehicleNo != null && vehicleNo.trim().length() > 0) {
                    for (LiveData liveData : vehiclesLiveArray) {

                        System.out.println("marker_vehicleNo : " + vehicleNo);


                            System.out.println("marker_vehicle: " + vehicleNo);

                            ArrayList<KeyValueData> data = new ArrayList<>();
                            data.add(new KeyValueData("Vehicle No", liveData.getVehicleNo()));
                            data.add(new KeyValueData("Date Time", String.valueOf(liveData.getDeviceDateTime())));
                            data.add(new KeyValueData("Speed", String.valueOf(liveData.getSpeed())));
                            data.add(new KeyValueData("Day Max Speed", String.valueOf(liveData.getDayMaxSpeed())));
                            data.add(new KeyValueData("Day Max Speed Time", liveData.getDayMAxSpeedTime()));
                            data.add(new KeyValueData("Halt Time", liveData.getHaltTime()));
                            data.add(new KeyValueData("Address", liveData.getAddress()));

                            InfoWindowAdapter adapter = showDialog(Gravity.BOTTOM, true, data);
                            getStringFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, data, adapter);

                    }
                }

                return true;
            }
        });

    }


    private void DrawArrowHead(GoogleMap mMap, LiveData data) {

        Marker directionArrow = null;

        ArrayList<LatLng> points = data.getAllPoints();

        LatLng from = points.get(points.size() - 2);
        LatLng to = points.get(points.size() - 1);

        // obtain the bearing between the last two points
        double anngle = GetBearing(from, to);
        if (!(anngle > 1 && anngle < 360)) {
            return;
        }
        DecimalFormat df = new DecimalFormat("##");
        String angle = df.format(anngle);

        Drawable drawable = getResources().getDrawable(getResources()
                .getIdentifier("arrow" + angle, "drawable", this.getPackageName()));
        Bitmap image = ((BitmapDrawable) drawable).getBitmap();

        directionArrow = mMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(to)
                .icon(BitmapDescriptorFactory.fromBitmap(image)));

        Marker directionMarker = data.getDirectionMarker();
        if (directionMarker != null) {
            directionMarker.remove();
            System.out.println("Direction Marker Removed");
        }
        data.setDirectionMarker(directionArrow);

    }

    private double GetBearing(LatLng from, LatLng to) {

        // Compute the angle.
        double angle_t = Math.atan((to.latitude - from.latitude) / (to.longitude - from.longitude));
        double angle_deg = 360 * angle_t / (2 * Math.PI);

        if ((to.longitude - from.longitude) < 0) {
            angle_deg = 180 + angle_deg;
        } else if ((to.latitude - from.latitude) < 0) {
            angle_deg = 360 + angle_deg;
        }
        return angle_deg;
    }


    public void getStringFromLocation(final double lat, final double lng, final ArrayList<KeyValueData> data, final InfoWindowAdapter infoWindowAdapter) {

        String address = String
                .format(Locale.ENGLISH, "http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="
                        + Locale.getDefault().getCountry(), lat, lng);

        System.out.println("requestedAddress : " + address);

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.GET, address,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String addressResponse) {
                        System.out.println("addressResponse : " + addressResponse);
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
                            if (city != null) {
                                address = city + " " + address;
                            }

                            if (country != null) {
                                address = address + " " + country;
                            }

                            address.replaceAll("null", "");

                            data.remove(6);
                            data.add(new KeyValueData("Address", address));
                            infoWindowAdapter.notifyDataSetChanged();
                            System.out.println("GOT ADDRESS FROM GOOGLE API : " + address);

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.remove(6);
                            data.add(new KeyValueData("Address", "Could not retrieve address"));
                            infoWindowAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                data.remove(6);
                data.add(new KeyValueData("Address", "Could not retrieve address"));
                infoWindowAdapter.notifyDataSetChanged();
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    private InfoWindowAdapter showDialog(int gravity, boolean expanded, ArrayList<KeyValueData> data) {
        boolean isGrid;
        Holder holder = new ListHolder();


        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
                switch (view.getId()) {
                    case R.id.header_container:
//						Toast.makeText(LiveMap.this, "Header clicked", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.footer_close_button:
                        dialog.dismiss();
                        break;
                    case R.id.close:
                        dialog.dismiss();
                        break;
                }
//                dialog.dismiss();
            }
        };

        OnItemClickListener itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {

            }
        };

        OnDismissListener dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogPlus dialog) {

            }
        };

        OnCancelListener cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogPlus dialog) {
                dialog.dismiss();
            }
        };

        InfoWindowAdapter adapter = new InfoWindowAdapter(this, data);
        showCompleteDialog(holder, gravity, adapter, clickListener, itemClickListener, dismissListener, cancelListener,
                expanded);

        return adapter;
    }

    private void showCompleteDialog(Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    OnDismissListener dismissListener, OnCancelListener cancelListener,
                                    boolean expanded) {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(holder)
                .setHeader(R.layout.header)
                .setFooter(R.layout.footer)
                .setCancelable(true)
                .setGravity(gravity)
                .setAdapter(adapter)
                .setOnClickListener(clickListener)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.d("DialogPlus", "onItemClick() called with: " + "item = [" +
                                item + "], position = [" + position + "]");
                    }
                })
                .setOnDismissListener(dismissListener)
                .setExpanded(expanded)
//        .setContentWidth(800)
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnCancelListener(cancelListener)
                .setOverlayBackgroundResource(android.R.color.transparent)
//        .setContentBackgroundResource(R.drawable.corner_background)
                //                .setOutMostMargin(0, 100, 0, 0)
                .create();
        dialog.show();
    }


    private Marker addIcon(IconGenerator iconFactory, String text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        return googleMap.addMarker(markerOptions);
    }


    /*  @Override
      public void onResume() {
          super.onResume();
  //        mMapView.onResume();
      }

      @Override
      public void onPause() {
          super.onPause();
  //        mMapView.onPause();
      }
  */
    @Override
    public void onDestroy() {
        super.onDestroy();
      //  mMapView.onDestroy();
        customHandler.removeCallbacks(updateTimerThread);
    }

/*
    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mMapView.onLowMemory();
    }
*/


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String title);
    }

    public String method(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

}
