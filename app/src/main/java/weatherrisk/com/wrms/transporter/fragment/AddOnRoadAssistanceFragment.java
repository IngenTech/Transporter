package weatherrisk.com.wrms.transporter.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DateTimePickerDialog;
import weatherrisk.com.wrms.transporter.adapter.InfoWindowAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.KeyValueData;
import weatherrisk.com.wrms.transporter.dataobject.LastLocationData;
import weatherrisk.com.wrms.transporter.dataobject.OnRoadAssistanceData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * dynamicDataLayout simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddOnRoadAssistanceFragment.OnFragmentLastInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddOnRoadAssistanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOnRoadAssistanceFragment extends android.support.v4.app.Fragment
        implements OnMapReadyCallback, DateTimePickerDialog.DateTimeListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAMS1 = "onRoadAssistance";
    private static final String VEHICLE_LIST = "vehicleList";
    public static final String FRAGMENT_TAG = "On Road Assistance";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private ArrayList<VehicleData> vehicleList;


    private OnFragmentLastInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param params1     Parameter 1.
     * @param vehicleList Parameter 2.
     * @return dynamicDataLayout new instance of fragment AddOnRoadAssistanceFragment.
     */

    public static AddOnRoadAssistanceFragment newInstance(String params1, ArrayList<VehicleData> vehicleList) {
        AddOnRoadAssistanceFragment fragment = new AddOnRoadAssistanceFragment();
        Bundle args = new Bundle();
        args.putString(PARAMS1, params1);
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    public AddOnRoadAssistanceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (getArguments() != null) {
            /*if (getArguments().getParcelableArrayList(PARAMS1) != null) {
                vehiclesLastLocationArray = getArguments().getParcelableArrayList(PARAMS1);
            }*/
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);

        }


    }

    private void setUpMapIfNeeded() {
        if (googleMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            setRetainInstance(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googlMap) {
        this.googleMap = googlMap;
     //   setUpMapIfNeeded();


        if (vehicleList.size() > 0) {
            VehicleData data = vehicleList.get(0);
            getVehiclesLastLocation(data.getVehicleId(), data.getVehicleNo(), data.getImei());
        } else {
            System.out.println("no vehicle selected");
        }

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setRotateGesturesEnabled(true);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String vehicleNo = marker.getSnippet();
                if (vehicleNo != null && vehicleNo.trim().length() > 0) {
                    for (LastLocationData liveData : vehiclesLastLocationArray) {
                        if (liveData.getVehicleNo().toLowerCase().equals(vehicleNo.toLowerCase())) {

                            ArrayList<KeyValueData> data = new ArrayList<>();
                            data.add(new KeyValueData("Vehicle No", liveData.getVehicleNo()));
                            data.add(new KeyValueData("Date Time", String.valueOf(liveData.getDeviceDateTime())));
                            data.add(new KeyValueData("Speed", String.valueOf(liveData.getSpeed())));
                            data.add(new KeyValueData("Day Max Speed", String.valueOf(liveData.getDayMaxSpeed())));
                            data.add(new KeyValueData("Day Max Speed Time", liveData.getDayMAxSpeedTime()));
                            data.add(new KeyValueData("Halt Time", liveData.getHaltTime()));
                            data.add(new KeyValueData("Address", liveData.getAddress()));

                            InfoWindowAdapter infoWindowAdapter = showDialog(Gravity.BOTTOM, true, data);
                            getStringFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, data, infoWindowAdapter);
                        }
                    }
                }

                return false;
            }
        });

    }

    private GoogleMap googleMap;
    ArrayList<Marker> vehiclesMarker = new ArrayList<>();
    ArrayList<LastLocationData> vehiclesLastLocationArray = new ArrayList<>();
    ArrayList<VehicleData> searchArrayList = new ArrayList<>();

    EditText bookingDate;
    EditText contactNo;
    EditText landmark;
    EditText problem;
    Button addOnRoadAssistance;

    OnRoadAssistanceData assistanceData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_on_road_assistance, container, false);

        bookingDate = (EditText) v.findViewById(R.id.bookingDate);
        contactNo = (EditText) v.findViewById(R.id.contactNo);
        landmark = (EditText) v.findViewById(R.id.landmark);
        problem = (EditText) v.findViewById(R.id.problem);
        addOnRoadAssistance = (Button) v.findViewById(R.id.addOnRoadAssistance);

        bookingDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                showDateTimeDialog();
            }
        });

        if (vehicleList != null && vehicleList.size() > 0) {
            for (VehicleData vehicleData : vehicleList) {
                searchArrayList.add(vehicleData);
            }
        }

        if (assistanceData == null) {
            assistanceData = new OnRoadAssistanceData();
        }

        setUpMapIfNeeded();


        return v;
    }

    private void showDateTimeDialog() {
        DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), true, this);
        pickerDialog.show();
    }

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int min, int sec, int am_pm) {
        String text = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        bookingDate.setText(text);
    }

    private boolean isValid() {
        boolean isValid = true;

        if (bookingDate.getText() != null && bookingDate.getText().toString().trim().length() > 0) {
            assistanceData.setBookingDate(bookingDate.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please select date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (contactNo.getText() != null && contactNo.getText().toString().trim().length() > 0) {
            assistanceData.setContactNo(contactNo.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter contact no.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (landmark.getText() != null && landmark.getText().toString().trim().length() > 0) {
            assistanceData.setLandmark(landmark.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter landmark", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (problem.getText() != null && problem.getText().toString().trim().length() > 0) {
            assistanceData.setProblem(problem.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter problem", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(assistanceData.getLatitude()!=null) && (!assistanceData.getLatitude().isEmpty())) {
            Toast.makeText(getActivity(), "Last Location not found for the vehicle", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(assistanceData.getLongitude()!=null) && (!assistanceData.getLongitude().isEmpty())) {
            Toast.makeText(getActivity(), "Last Location not found for the vehicle", Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {




        addOnRoadAssistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()) {
                    if (vehicleList.size() > 0) {
                        VehicleData data = vehicleList.get(0);
                        onRoadAssistanceRequest(data.getVehicleId(), data.getVehicleNo(), data.getImei(), assistanceData);
                    } else {
                        System.out.println("no vehicle selected");
                    }

                }
            }
        });


        setHasOptionsMenu(true);
    }

    SharedPreferences prefs;

    ProgressDialog progressDialog;

    private void onRoadAssistanceRequest(final String vehicleId, final String vehicleName, final String imei,final OnRoadAssistanceData data) {

        final StringRequest stringAssistanceRequest = new StringRequest(Request.Method.POST, MyUtility.URL.BOOK_ON_ROAD_ASSISTANCE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String assistanceResponse) {
                        progressDialog.dismiss();
                        try {
                            System.out.println("Add On Road Assistance Response : " + assistanceResponse);
                            JSONObject jsonObject = new JSONObject(assistanceResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Request has been submitted successfully").
                                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    getActivity().getSupportFragmentManager().beginTransaction().remove(AddOnRoadAssistanceFragment.this).commit();
                                                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                    ft.replace(R.id.frame_container, OnRoadAssistanceFragment.newInstance("params1", "param2"), FRAGMENT_TAG);
//                                                    ft.addToBackStack(FRAGMENT_TAG);
                                                    ft.commit();
                                                }
                                            });
                                    builder.show();

                                } else {
                                    Toast.makeText(getActivity(), "Request Not Accepted", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                map.put("vehicle_id", vehicleId);
                map.put("date_of_booking", assistanceData.getBookingDate());
                map.put("contact_no", assistanceData.getContactNo());
                map.put("landmark", assistanceData.getLandmark());
                map.put("problem", assistanceData.getProblem());
                map.put("latitude", assistanceData.getLatitude());
                map.put("longitude", assistanceData.getLongitude());




                /*api_key,account_id,vehicle_id,date_of_booking,contact_no,landmark,problem,latitude,longitude*/
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringAssistanceRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        progressDialog = ProgressDialog.show(getActivity(), "",
                "Please Wait.....", true);
        AppController.getInstance().addToRequestQueue(stringAssistanceRequest);

    }

    private void getVehiclesLastLocation(final String vehicleId, final String vehicleName, final String imei) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.LAST_LOCATION_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        progressDialog.dismiss();
                        try {
                            System.out.println("LastLocation Response : " + loginResponse);
                            JSONObject jsonObjectMain = new JSONObject(loginResponse);

                            if (jsonObjectMain.has("result")) {
                                if (jsonObjectMain.get("result").equals("success")) {

                                    if (vehiclesMarker != null) {
                                        for (Marker marker : vehiclesMarker) {
                                            marker.remove();
                                        }
                                        vehiclesMarker.clear();
                                    } else {
                                        vehiclesMarker = new ArrayList<Marker>();
                                    }
                                    vehiclesLastLocationArray.clear();

                                    JSONArray vehicleArray = jsonObjectMain.getJSONArray("LastData");
                                    JSONObject jsonObject = null;
                                    if (vehicleArray.length() > 0) {
                                        jsonObject = vehicleArray.getJSONObject(0);
                                    }else{
                                        Toast.makeText(getActivity(),"Last Location not found for the vehicle",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String vehicleId = jsonObject.getString("vehicle_id");

                                    IconGenerator iconFactory = new IconGenerator(getActivity());
                                    final LatLngBounds.Builder builder = new LatLngBounds.Builder();

//                                        for (int i = 0; i < vehicleArray.length(); i++) {
                                    LastLocationData data = new LastLocationData();
                                    try {
                                        String deviceDateTime = jsonObject.getString("device_date");
                                        double lat = 0.0;
                                        double lng = 0.0;
                                        try {
                                            String latStr = jsonObject.getString("lat").replaceAll("[^\\d.]", "");
                                            String lngStr = jsonObject.getString("lng").replaceAll("[^\\d.]", "");
                                            lat = Double.parseDouble(latStr);
                                            lng = Double.parseDouble(lngStr);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        String speed = jsonObject.getString("speed");
                                        String dayMaxSpeed = jsonObject.getString("dayMaxSpeed");
                                        String dayMaxSpeedTime = jsonObject.getString("dayMaxSpeedTime");
                                        String haltTime = jsonObject.getString("lastHaltTime");
                                        String status = jsonObject.getString("status");
                                        String runningStatus = "0";
                                        double speedDouble = 0.0;
                                        double dayMaxSpeedDouble = 0.0;
                                        try {
                                            speedDouble = Double.parseDouble(speed);
                                            dayMaxSpeedDouble = Double.parseDouble(dayMaxSpeed);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        if(status.equals("Stopped")){
                                            runningStatus = "0";
                                        }else{
                                            runningStatus = "1";
                                        }

                                        data = new LastLocationData(lat, lng, imei, vehicleName, deviceDateTime, speedDouble, dayMaxSpeedDouble, dayMaxSpeedTime, haltTime, runningStatus, "", "IO");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    assistanceData.setLongitude(String.valueOf(data.getLon()));
                                    assistanceData.setLatitude(String.valueOf(data.getLat()));
                                    vehiclesLastLocationArray.add(data);

                                    if (data.getRunningStatus().equals("1")) {
                                        iconFactory.setStyle(IconGenerator.STYLE_GREEN);
                                    } else {
                                        iconFactory.setStyle(IconGenerator.STYLE_DEFAULT);
                                    }
                                    Marker marker = addIcon(iconFactory, data.getVehicleNo(), new LatLng(data.getLat(), data.getLon()));
                                    marker.setSnippet(data.getVehicleNo());
                                    vehiclesMarker.add(marker);
                                    builder.include(new LatLng(data.getLat(), data.getLon()));
//                                        }

                                    try {
                                        if (vehiclesLastLocationArray.size() > 0) {

                                            int padding = 20;
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
                                                if (vehiclesLastLocationArray.size() > 0) {
                                                    int padding = 20;
                                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                                    googleMap.moveCamera(cu);
                                                }
                                            }
                                        });

                                    }

                                    onSelectionOfTheFragment(FRAGMENT_TAG, vehiclesLastLocationArray);

                                } else {
                                    Toast.makeText(getActivity(), "No data for this vehicle", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                map.put("vehicle_ids", vehicleId);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        progressDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Last Location.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

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

        /*MarkerOptions carMarker = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromResource(R.drawable.car_marker)).
                position(position);
        return googleMap.addMarker(carMarker);*/

        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
                position(position).
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        return googleMap.addMarker(markerOptions);
    }


    /*@Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (vehiclesMarker != null) {
            for (Marker m : vehiclesMarker) {
                m.remove();
            }
        }
        vehiclesMarker.clear();
    }

    /*@Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }*/

    public void onSelectionOfTheFragment(String title, ArrayList<LastLocationData> vehiclesLastLocationArray) {
        if (mListener != null) {
            mListener.onLastLocationFragmentInteraction(title, vehiclesLastLocationArray);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentLastInteractionListener) activity;
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
    public interface OnFragmentLastInteractionListener {
        // TODO: Update argument type and name
        public void onLastLocationFragmentInteraction(String title, ArrayList<LastLocationData> vehiclesLastLocationArray);
    }

}
