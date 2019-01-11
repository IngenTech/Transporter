package weatherrisk.com.wrms.transporter.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
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
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.KeyValueData;
import weatherrisk.com.wrms.transporter.dataobject.LiveData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * dynamicDataLayout simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Activities that contain this fragment must implement the
 * {@link LiveMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveMapFragment extends android.support.v4.app.Fragment
        implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String VEHICLE_LIST = "vehicleList";
    public static final String FRAGMENT_TAG = "Live";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ArrayList<VehicleData> vehicleList;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1      Parameter 1.
     * @param vehicleList Parameter 2.
     * @return dynamicDataLayout new instance of fragment LiveMapFragment.
     */

    public static LiveMapFragment newInstance(String param1, ArrayList<VehicleData> vehicleList) {
        LiveMapFragment fragment = new LiveMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    public LiveMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);
        }
        onSelectionOfTheFragment(FRAGMENT_TAG);
    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapView);
            mapFrag.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap = googleMap;
        setUpMapIfNeeded();
    }
    MapView mMapView;
    private GoogleMap googleMap;
    AutoCompleteTextView searchVehicle;
    ArrayList<Marker> vehiclesMarker = new ArrayList<>();
    ArrayList<LiveData> vehiclesLiveArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_live_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        /*searchVehicle = (AutoCompleteTextView) v.findViewById(R.id.vehicleSearch);
        if (vehicleList.size() > 0) {
            VehicleSearchAdapter adapter = new VehicleSearchAdapter(getActivity(), R.layout.auto_complete_textview, vehicleList);
            searchVehicle.setAdapter(adapter);
            searchVehicle.setThreshold(1);
            searchVehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String vehicleNo = searchVehicle.getText().toString();
                    if (vehiclesMarker.size() > 0) {
                        boolean isVehicleFound = false;
                        for (Marker mMarker : vehiclesMarker) {
                            if (mMarker.getSnippet().toLowerCase().contains(vehicleNo.toLowerCase())) {
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(mMarker.getPosition()).zoom(15).build();
                                googleMap.animateCamera(CameraUpdateFactory
                                        .newCameraPosition(cameraPosition));
                                isVehicleFound = true;
                                break;
                            }
                        }
                        if (!isVehicleFound) {
                            Toast.makeText(getActivity(), "Last Location data not exist for this vehicle", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "Last Location data not exist for this vehicle", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }*/

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

//        googleMap = mMapView.getMap();
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setRotateGesturesEnabled(true);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.hideInfoWindow();
                String vehicleNo = marker.getTitle();
                System.out.println("marker title vehicleNo : " + vehicleNo);
                if (vehicleNo != null && vehicleNo.trim().length() > 0) {
                    for (LiveData liveData : vehiclesLiveArray) {
                        if (liveData.equals(vehicleNo.toLowerCase())) {

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
                }

                return true;
            }
        });

        customHandler.postDelayed(updateTimerThread, 0);
        return v;

    }

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;


    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            String jsonString = createImeiJsonString();
            getVehiclesLastLocation(jsonString);
            customHandler.postDelayed(this, 30000);
        }
    };


    SharedPreferences prefs;

    private String createImeiJsonString() {
        String jsonString = "";
        /*if (prefs == null) {
            prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
        }
        String accointId = prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "");
//        VehicleList
        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for (VehicleData vehicleData : vehicleList) {
                JSONObject jObject = new JSONObject();
                jObject.put("Imei", vehicleData.getImei());
                jsonArray.put(jObject);
            }

            jsonObject.put("AccountId", accointId);
            jsonObject.put("VehicleImeiList", jsonArray);
            jsonString = jsonObject.toString();
            System.out.println("jsonString : " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        for (VehicleData vehicleData : vehicleList) {
            jsonString = jsonString + vehicleData.getVehicleId() + ",";
        }
        jsonString = jsonString.substring(0, jsonString.length() - 1);

        return jsonString;
    }

    @Override
    public void onStart() {
        super.onStart();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    @Override
    public void onStop() {
        super.onStop();
        customHandler.removeCallbacks(updateTimerThread);
    }

    private void getVehiclesLastLocation(final String jsonImeiList) {

        vehiclesMarker.clear();
        googleMap.clear();

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.LAST_LOCATION_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        if (getActivity() != null) {
                            try {
                                System.out.println("Live Location Response : " + loginResponse);
                                JSONObject jsonObject = new JSONObject(loginResponse);

                                if (jsonObject.has("result")) {
                                    if (jsonObject.get("result").equals("success")) {

                                        JSONArray vehicleArray = jsonObject.getJSONArray("LastData");
                                        if (vehicleArray.length() > 0) {


                                            /*if (vehiclesMarker != null) {
                                                for (Marker marker : vehiclesMarker) {
                                                    marker.remove();
                                                }
                                                vehiclesMarker = new ArrayList<Marker>();
                                            }*/
                                            final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            String[] listData = new String[vehicleArray.length()];
                                            int incremental = 0;
//                                        for (LiveData data : PublicContainer.liveData) {
                                            for (int vehicleCount = 0; vehicleCount < vehicleArray.length(); vehicleCount++) {
                                                LiveData data = new LiveData();
                                                try {
                                                    JSONObject liveLocationJSON = vehicleArray.getJSONObject(vehicleCount);
                                                    String imei = "";
                                                    String vehicleName = "";
                                                    if(vehicleList.size()>vehicleCount){
                                                        VehicleData vData = vehicleList.get(vehicleCount);
                                                        imei = vData.getImei();
                                                        vehicleName = vData.getVehicleNo();

                                                    }

                                                    String deviceDateTime = liveLocationJSON.getString("device_date");
                                                    double lat = 0.0;
                                                    double lng = 0.0;
                                                    try {
                                                        String latStr = liveLocationJSON.getString("lat").replaceAll("[^\\d.]", "");
                                                        String lngStr = liveLocationJSON.getString("lng").replaceAll("[^\\d.]", "");
                                                        lat = Double.parseDouble(latStr);
                                                        lng = Double.parseDouble(lngStr);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    String speed = liveLocationJSON.getString("speed");
                                                    String dayMaxSpeed = liveLocationJSON.getString("dayMaxSpeed");
                                                    String dayMaxSpeedTime = liveLocationJSON.getString("dayMaxSpeedTime");
                                                    String haltTime = liveLocationJSON.getString("lastHaltTime");
                                                    String runningStatus = liveLocationJSON.getString("status");
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
                                                                liveData.setDayMaxSpeed(dayMaxSpeed);
                                                                liveData.setDayMAxSpeedTime(dayMaxSpeedTime);
                                                                liveData.setHaltTime(haltTime);
                                                                liveData.setRunningStatus(runningStatus);
                                                                liveData.addPoint(new LatLng(lat, lng));
                                                                liveData.setLat(lat);
                                                                liveData.setLon(lng);
                                                                data = liveData;
                                                                isUpdated = true;
                                                                break;
                                                            }
                                                        }

                                                        if (!isUpdated) {
                                                            ArrayList<LatLng> allPoints = new ArrayList<>();
                                                            allPoints.add(new LatLng(lat, lng));
                                                            data = new LiveData(lat, lng, imei, vehicleName, deviceDateTime, speed, dayMaxSpeed, dayMaxSpeedTime, haltTime, runningStatus, "", "IO", allPoints);
                                                            vehiclesLiveArray.add(data);
//                                                            System.out.println("NewVehicleAdded");
                                                        }
//                                                        System.out.println("VehicleListLength : "+vehiclesLiveArray.size());
                                                    }

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    continue;
                                                }


                                                IconGenerator tc = new IconGenerator(getActivity());
                                                tc.setTextAppearance(R.style.ClusterIcon_TextAppearance1);

                                                if (data.getRunningStatus().contains("Stopped")) {
                                                    tc.setStyle(IconGenerator.STYLE_RED);
                                                    MarkerOptions markerOptions = new MarkerOptions().
                                                            position(new LatLng(data.getLat(), data.getLon())).
                                                            icon(BitmapDescriptorFactory.fromBitmap(tc.makeIcon(data.getVehicleNo()))).
                                                            snippet(String.valueOf(incremental)).
                                                            title(data.getVehicleNo());

                                                    for (int i = 0; i < vehiclesMarker.size(); i++) {
                                                        Marker marker = vehiclesMarker.get(i);
                                                        if (marker.getTitle().trim().equals(data.getVehicleNo())) {
                                                            marker.remove();
                                                            vehiclesMarker.remove(i);
                                                            break;
                                                        }

                                                    }

                                                    Marker perth = googleMap.addMarker(markerOptions);
                                                    vehiclesMarker.add(perth);
                                                    builder.include(perth.getPosition());
                                                } else {
                                                    tc.setStyle(IconGenerator.STYLE_GREEN);
                                                    MarkerOptions markerOptions = new MarkerOptions().
                                                            position(new LatLng(data.getLat(), data.getLon())).
                                                            icon(BitmapDescriptorFactory.fromBitmap(tc.makeIcon(data.getVehicleNo()))).
                                                            snippet(String.valueOf(incremental)).
                                                            title(data.getVehicleNo());

                                                    for (int i = 0; i < vehiclesMarker.size(); i++) {
                                                        Marker marker = vehiclesMarker.get(i);
                                                        if (marker.getTitle().trim().equals(data.getVehicleNo())) {
                                                            marker.remove();
                                                            vehiclesMarker.remove(i);
                                                            break;
                                                        }

                                                    }
                                                    Marker perth = googleMap.addMarker(markerOptions);
                                                    vehiclesMarker.add(perth);

                                                    ArrayList<LatLng> points = data.getAllPoints();
                                                    builder.include(perth.getPosition());
                                                    if (data.getAllPoints().size() > 1) {
                                                        DrawArrowHead(googleMap, data);

                                                    }
                                                }
                                            }

                                            try {
                                                if (vehiclesLiveArray.size() > 0) {

                                                    int padding = 150;
                                                    // offset from edges of the map in pixels
                                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                                    googleMap.moveCamera(cu);
                                                } else {
                                                    Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_LONG).show();
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
                                            Toast.makeText(getActivity(), "No Vehicle Found in this Account", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (getActivity() != null) {
                    volleyError.printStackTrace();
                    Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                Map<String, String> map = new HashMap<>();
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                map.put("vehicle_ids", jsonImeiList);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }


                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
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
                .getIdentifier("arrow" + angle, "drawable", getActivity().getPackageName()));
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

        InfoWindowAdapter adapter = new InfoWindowAdapter(getActivity(), data);
        showCompleteDialog(holder, gravity, adapter, clickListener, itemClickListener, dismissListener, cancelListener,
                expanded);

        return adapter;
    }

    private void showCompleteDialog(Holder holder, int gravity, BaseAdapter adapter,
                                    OnClickListener clickListener, OnItemClickListener itemClickListener,
                                    OnDismissListener dismissListener, OnCancelListener cancelListener,
                                    boolean expanded) {
        final DialogPlus dialog = DialogPlus.newDialog(getActivity())
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
        mMapView.onDestroy();
        customHandler.removeCallbacks(updateTimerThread);
    }

/*
    @Override
    public void onLowMemory() {
        super.onLowMemory();
//        mMapView.onLowMemory();
    }
*/


    public void onSelectionOfTheFragment(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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

}
