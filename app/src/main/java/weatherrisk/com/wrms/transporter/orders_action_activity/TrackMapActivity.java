package weatherrisk.com.wrms.transporter.orders_action_activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
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
import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import weatherrisk.com.wrms.transporter.bean.TrackData;
import weatherrisk.com.wrms.transporter.dataobject.KeyValueData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 31-03-2017.
 */
public class TrackMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    MapView mMapView;
    private GoogleMap googleMap;
    AutoCompleteTextView searchVehicle;
    ArrayList<Marker> vehiclesMarker = new ArrayList<>();

    Polyline line, currentLine;
    SharedPreferences prefs;
    ArrayList<TrackData> trackData = new ArrayList<>();
    private String interval = "1";
    private String order_id;
    private String accessToken;
    private String userID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_track_map);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        order_id = getIntent().getStringExtra("trip_id");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googlMap) {
        googleMap = googlMap;

        trackMethod(userID,order_id,accessToken);
    }


    ProgressDialog progressDialog;

    private void trackMethod(final String accountId, final String orderId, final String accessToken) {
        System.out.println("Track API Called: ");
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.TRACK_API_MAP,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {

                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("Status") && jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                if (jsonObject.get("Result").equals("Success")) {


                                    IconGenerator iconFactory = new IconGenerator(TrackMapActivity.this);
                                    JSONArray trackDataJSONArray = jsonObject.getJSONArray("Track");
                                    if (trackDataJSONArray.length() > 0) {
                                        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        for (int i = 0; i < trackDataJSONArray.length(); i++) {

                                            JSONObject trackDataJSON = trackDataJSONArray.getJSONObject(i);

                                            String vehicleName = trackDataJSON.getString("VehicleType");
                                            String vehicleImei = trackDataJSON.getString("DeviceIMEINo");

                                            double latitude = 0.0;
                                            double longitude = 0.0;
                                            try {
                                                String latStr = trackDataJSON.getString("Latitude").replaceAll("[^\\d.]", "");
                                                String lngStr = trackDataJSON.getString("Longitude").replaceAll("[^\\d.]", "");
                                                latitude = Double.parseDouble(latStr);
                                                longitude = Double.parseDouble(lngStr);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            String dateTime = trackDataJSON.getString("Longitude");
                                            String speedString = trackDataJSON.getString("Speed");
                                         //   String distance = trackDataJSON.getString("cumdist");

                                            String distance = "";


                                            String markerTitle = vehicleName + " (" + vehicleImei + ")";
                                            String snippet = String.valueOf(i);

                                            TrackData data = new TrackData(speedString, latitude, longitude, distance, dateTime, "Waiting");
                                            trackData.add(data);

                                            if (i >= 1) {
                                                LatLng from = new LatLng(trackData.get(i - 1).getLatitude(), trackData.get(i - 1).getLongitude());
                                                LatLng to = new LatLng(trackData.get(i).getLatitude(), trackData.get(i).getLongitude());
                                                LatLng midPoint = getMidPoint(from.latitude, from.longitude, to.latitude, to.longitude);

                                                line = googleMap.addPolyline(new PolylineOptions().add(from, to).width(4).color(Color.RED).geodesic(true));

                                                DrawArrowHead(googleMap, from, to, midPoint);


                                            }

                                            Marker perth = null;
                                            if (i == 0) {
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));
                                                iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                                                iconFactory.setTextAppearance(R.style.ClusterIcon_TextAppearance1);
                                                perth = addIcon(iconFactory, "S", new LatLng(latitude, longitude), snippet, markerTitle);

                                            } else if (i == (trackDataJSONArray.length() - 1)) {
                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));
                                                iconFactory.setStyle(IconGenerator.STYLE_RED);
                                                iconFactory.setTextAppearance(R.style.ClusterIcon_TextAppearance1);
                                                perth = addIcon(iconFactory, "E", new LatLng(latitude, longitude), snippet, markerTitle);
                                            } else {
                                                double speed = 0.0;
                                                try {
                                                    speed = Double.parseDouble(speedString);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }


                                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));

                                                if (speed >= 1 && speed <= 20) {
                                                    perth = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(latitude, longitude))
                                                            .title(markerTitle)
                                                            .snippet(snippet)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                } else if (speed > 20) {
                                                    perth = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(latitude, longitude))
                                                            .title(markerTitle)
                                                            .snippet(snippet)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                                } else if (speed < 1) {
                                                    perth = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(latitude, longitude))
                                                            .title(markerTitle)
                                                            .snippet(snippet)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                                } else {
                                                    perth = googleMap.addMarker(new MarkerOptions()
                                                            .position(new LatLng(latitude, longitude))
                                                            .title(markerTitle)
                                                            .snippet(snippet)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                                }


                                            }
                                            data.setMarker(perth);
                                            builder.include(new LatLng(latitude, longitude));
                                        }

                                        try {
                                            if (trackData.size() > 0) {

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
                                                    if (trackData.size() > 0) {
                                                        int padding = 150;
                                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                                        googleMap.moveCamera(cu);
                                                    }
                                                }
                                            });

                                        }

                                    } else {
                                        String msg = jsonObject.getString("Message");
                                        Toast.makeText(getApplicationContext(), ""+msg, Toast.LENGTH_LONG).show();                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else if (jsonObject.has("Status") && jsonObject.getString("Status").equalsIgnoreCase("2")) {

                                String msg = jsonObject.getString("Message");
                                Toast.makeText(getApplicationContext(), ""+msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
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

        progressDialog = ProgressDialog.show(this, "",
                "Fetching Track Data.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);



        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (marker.getSnippet() != null) {
                    if (marker.getSnippet().length() > 0) {
                        String markerPosition = marker.getSnippet();
                        int position = -1;
                        try {
                            position = Integer.valueOf(markerPosition);
                        } catch (NumberFormatException e) {
                            position = -1;
                        }

                        if (position != -1) {
                            if (trackData.size() > position) {
                                TrackData markerData = trackData.get(position);
                                if (markerData != null) {
                                    ArrayList<KeyValueData> data = new ArrayList<>();
                                    data.add(new KeyValueData("Vehicle No", "908098-08"));
                                    data.add(new KeyValueData("Speed", markerData.getSpeed()));
                                    data.add(new KeyValueData("Distance", markerData.getDistance()));
                                    data.add(new KeyValueData("Date Time", markerData.getDateTime()));

                                    data.add(new KeyValueData("Address", markerData.getAddress()));

                                    InfoWindowAdapter adapter = showDialog(Gravity.BOTTOM, true, data);
                                    getStringFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, data, adapter);
                                }
                            }
                        }


                    }
                }

                return false;
            }
        });


    }


    private Marker addIcon(IconGenerator iconFactory, String text, LatLng position, String snippet, String title) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                snippet(snippet).
                title(title).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        return googleMap.addMarker(markerOptions);
    }

    private void DrawArrowHead(GoogleMap mMap, LatLng from, LatLng to, LatLng midPoint) {
        // obtain the bearing between the last two points
        double anngle = GetBearing(from, to);
        if (!(anngle > 1 && anngle < 360)) {
            return;
        }
        DecimalFormat df = new DecimalFormat("##");
        String angle = df.format(anngle-45);

        System.out.println("Angle : " + "arrow" + angle);

        try {

        mMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(midPoint)
                .flat(true)
                .rotation(Float.parseFloat(angle))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer)));
    } catch (Exception e) {
        e.printStackTrace();
    }

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


    public LatLng getMidPoint(double lat1, double lon1, double lat2, double lon2) {

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new LatLng(Math.toDegrees(lat3), Math.toDegrees(lon3));
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

                            data.remove(4);
                            data.add(new KeyValueData("Address", address));
                            infoWindowAdapter.notifyDataSetChanged();

                            System.out.println("GOT ADDRESS FROM GOOGLE API : " + address);

                        } catch (Exception e) {
                            e.printStackTrace();
                            data.remove(4);
                            data.add(new KeyValueData("Address", "Could not retrieve address"));
                            infoWindowAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                data.remove(4);
                data.add(new KeyValueData("Address", "Could not retrieve address"));
                infoWindowAdapter.notifyDataSetChanged();
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    private InfoWindowAdapter showDialog(int gravity, boolean expanded, ArrayList<KeyValueData> data) {
        boolean isGrid;
        Holder holder = new ListHolder();
        ;

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


}
