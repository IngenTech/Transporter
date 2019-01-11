package weatherrisk.com.wrms.transporter.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.ListHolder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DateTimePickerDialog;
import weatherrisk.com.wrms.transporter.adapter.WeatherInfoWindowAdapter;
import weatherrisk.com.wrms.transporter.utils.DirectionsJSONParser;
import weatherrisk.com.wrms.transporter.utils.PlaceDetailsJSONParser;
import weatherrisk.com.wrms.transporter.utils.PlaceJSONParser;
import weatherrisk.com.wrms.transporter.dataobject.WeatherData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WeatherForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends android.support.v4.app.Fragment
        implements OnMapReadyCallback,DateTimePickerDialog.DateTimeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String FRAGMENT_TAG = "Route Weather";

    private static final int START_LOCATION = 12;
    private static final int END_LOCATION = 13;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WeatherForecastFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WeatherForecastFragment newInstance(String param1, String param2) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
    AutoCompleteTextView sourceLocationSearch;
    AutoCompleteTextView destLocationSearch;
    /*int selectedSource = 0;
    int selectedDestination = 0;*/
    ArrayList<LatLng> markerPoints;
    EditText startDate;
    Button getWeatherForeCast;
    LatLng startPoint;
    LatLng endPoint;
    HashMap<String, ArrayList<WeatherData>> weatherData = new HashMap<>();
    String startDateString;
    String endDateString;
    LinearLayout viewLayout;

    ArrayList<LatLng> points = null;
    PolylineOptions lineOptions = null;

    double finalDistance = 0.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_weather_forecast, container, false);

        View v = inflater.inflate(R.layout.fragment_weather_forecast, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        sourceLocationSearch = (AutoCompleteTextView) v.findViewById(R.id.sourceLocation);
        destLocationSearch = (AutoCompleteTextView) v.findViewById(R.id.destLocationSearch);
        startDate = (EditText) v.findViewById(R.id.startDate);
        getWeatherForeCast = (Button) v.findViewById(R.id.getWeatherForeCast);
        viewLayout = (LinearLayout) v.findViewById(R.id.viewLayout);

        Date current = Calendar.getInstance().getTime();
        startDate.setText(AppController.WEATHER_API_DATE_TIME_FORMAT.format(current));

        return v;
    }

    private void showDateTimeDialog() {
        DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), true, this);
        pickerDialog.show();
    }

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int min, int sec, int am_pm) {

        String text = year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min);
        startDate.setText(text);
    }


    private Timer timer = new Timer();
    private final long DELAY = 1000;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Inside proceedButton onclick");
                showDateTimeDialog();
            }
        });

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
                String vehicleNo = marker.getSnippet();
                if (vehicleNo != null && vehicleNo.trim().length() > 0) {

                }

                return false;
            }
        });

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(21.0000, 78.0000)).zoom(8).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            }
        });


        sourceLocationSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(timer != null)
                    timer.cancel();*/
                if (s.length() > 2) {

                    getLocationViaGoogle(s.toString(), START_LOCATION);
                    /*if (selectedSource == 1) {
                        sourceLocationSearch.dismissDropDown();
                    } else {
                        sourceLocationSearch.showDropDown();
                    }*/
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(final Editable s) {
                //avoid triggering event when text is too short
                /*if (s.length() > 2) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            getLocationViaGoogle(s.toString(), START_LOCATION);
                        }

                    }, DELAY);
                }*/
            }
        });

        sourceLocationSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if (selectedSource == 1) {
                    sourceLocationSearch.dismissDropDown();
                } else {
                    sourceLocationSearch.showDropDown();
                }*/
                return false;
            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown list
        sourceLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

//                selectedSource = 1;
                sourceLocationSearch.dismissDropDown();
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                sourceLocationSearch.setText(hm.get("description"));

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                getPlacesDetailViaGoogle(url, START_LOCATION);
               /* if (selectedSource == 1) {
                    sourceLocationSearch.dismissDropDown();
                } else {
                    sourceLocationSearch.showDropDown();
                }*/

            }
        });

        destLocationSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    getLocationViaGoogle(s.toString(), END_LOCATION);
                    /*if (selectedDestination == 1) {
                        destLocationSearch.dismissDropDown();
                    } else {
                        destLocationSearch.showDropDown();
                    }*/
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (selectedDestination == 1) {
                    destLocationSearch.dismissDropDown();
                } else {
                    destLocationSearch.showDropDown();
                }*/
            }
        });

        destLocationSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /*if (selectedDestination == 1) {
                    destLocationSearch.dismissDropDown();
                } else {
                    destLocationSearch.showDropDown();
                }*/
                return false;
            }
        });

        // Setting an item click listener for the AutoCompleteTextView dropdown list
        destLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {

//                selectedDestination = 1;
                destLocationSearch.dismissDropDown();
                SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                destLocationSearch.setText(hm.get("description"));

                // Getting url to the Google Places details api
                String url = getPlaceDetailsUrl(hm.get("reference"));

                getPlacesDetailViaGoogle(url, END_LOCATION);
                /*if (selectedDestination == 1) {
                    destLocationSearch.dismissDropDown();
                } else {
                    destLocationSearch.showDropDown();
                }*/

            }
        });


        getWeatherForeCast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (finalDistance > 10.0) {
                    int chk_distance = 10;
                    if (finalDistance < 10.0) {
                        chk_distance = 2;
                    } else if (finalDistance > 50.0 && finalDistance <= 100.0) {
                        chk_distance = 10;
                    } else if (finalDistance > 100.0 && finalDistance <= 500.0) {
                        chk_distance = 20;
                    } else if (finalDistance > 500.0 && finalDistance <= 1000.0) {
                        chk_distance = 50;
                    } else if (finalDistance > 1000.0) {
                        chk_distance = 100;
                    }

                    System.out.println("CheckDistance : " + chk_distance + " Total Distance  :" + finalDistance);
                    if (startPoint != null) {
                        String mapKey = startPoint.latitude + "," + startPoint.longitude;
                        ArrayList<WeatherData> data = new ArrayList<>();
                        markers.add(mapKey);
                        weatherData.put(mapKey, data);
                    }
                    LatLng firstPoint = null;
                    double calculatedCheckDist = 0.0;
                    for (LatLng point : points) {
                        if (firstPoint != null) {
                            calculatedCheckDist = calculatedCheckDist + distFrom(firstPoint.latitude, firstPoint.longitude, point.latitude, point.longitude);
                            if (calculatedCheckDist >= chk_distance) {
                                String mapKey = point.latitude + "," + point.longitude;
                                ArrayList<WeatherData> data = new ArrayList<>();
                                markers.add(mapKey);
                                weatherData.put(mapKey, data);
                                calculatedCheckDist = 0.0;
                            }
                        }
                        firstPoint = point;
                    }
                    if (endPoint != null) {
                        String mapKey = endPoint.latitude + "," + endPoint.longitude;
                        ArrayList<WeatherData> data = new ArrayList<>();
                        markers.add(mapKey);
                        weatherData.put(mapKey, data);
                    }

                    // Drawing polyline in the Google Map for the i-th route


                            /*Iterator iterator = weatherData.entrySet().iterator();
                            while (iterator.hasNext()) {
                                Map.Entry pair = (Map.Entry)iterator.next();
                                String key = (String)pair.getKey();
                                ArrayList<WeatherData> value = (ArrayList)pair.getValue();
                            }*/
                    startDateString = startDate.getText().toString();
                    endDateString = "";
                    try {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(AppController.WEATHER_API_DATE_TIME_FORMAT.parse(startDateString));
                        calendar.add(Calendar.DATE, 2);
                        endDateString = AppController.WEATHER_API_DATE_TIME_FORMAT.format(calendar.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String key = markers.get(0);
                    String[] latLngString = key.split(",");
                    double latitude = Double.valueOf(latLngString[0]);
                    double longitude = Double.valueOf(latLngString[1]);
                    LatLng latlng = new LatLng(latitude, longitude);
                    getLocationWeatherForecast(endDateString, startDateString, latlng, key, weatherData.get(key));
                    viewLayout.setVisibility(View.GONE);
                    getWeatherDataCount++;


               /* if(startPoint==null || endPoint==null){
                    Toast.makeText(getActivity(),"Please Select start and end location",Toast.LENGTH_SHORT).show();
                    return;
                }
                String url = getDirectionsUrl(startPoint, endPoint);
                getDirectionViaGoogle(url);*/
                }
            }
        });
        // Initializing
        /*markerPoints = new ArrayList<LatLng>();
        if(googleMap!=null){

            // Enable MyLocation Button in the Map
            googleMap.setMyLocationEnabled(true);

            // Setting onclick event listener for the map
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    // Already two locations
                    if(markerPoints.size()>1){
                        markerPoints.clear();
                        googleMap.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    *//**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         *//*
                    if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    googleMap.addMarker(options);

                    // Checks, whether start and end locations are captured
                    if(markerPoints.size() >= 2){
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);

                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        getDirectionViaGoogle(url);
                    }
                }
            });
        }*/

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String key = marker.getSnippet();
                if (key != null && key.trim().length() > 0) {

                    ArrayList<WeatherData> weatherArray = weatherData.get(key);
                    if (weatherArray != null && weatherArray.size() > .0) {
                        WeatherInfoWindowAdapter infoWindowAdapter = showDialog(Gravity.BOTTOM, true, weatherArray);
                    }
                }

                return false;
            }
        });
    }

    private WeatherInfoWindowAdapter showDialog(int gravity, boolean expanded, ArrayList<WeatherData> data) {
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

        WeatherInfoWindowAdapter adapter = new WeatherInfoWindowAdapter(getActivity(), data);
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
                .setHeader(R.layout.weather_dialog_header)
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


    ArrayList<String> markers = new ArrayList<>();
    int getWeatherDataCount = 0;
    ProgressDialog progressDialog;

    private void getDirectionViaGoogle(final String url) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.cancel();
                        System.out.println("Google Direction Response : " + response);
                        JSONObject jObject;
                        List<List<HashMap<String, String>>> routes = null;

                        try {
                            jObject = new JSONObject(response);
                            DirectionsJSONParser parser = new DirectionsJSONParser();

                            // Starts parsing data
                            routes = parser.parse(jObject);
                            MarkerOptions markerOptions = new MarkerOptions();
                            finalDistance = 0.0;

                            // Traversing through all the routes
                            for (int i = 0; i < routes.size(); i++) {
                                points = new ArrayList<LatLng>();
                                lineOptions = new PolylineOptions();

                                // Fetching i-th route
                                List<HashMap<String, String>> path = routes.get(i);

                                LatLng previousPoint = null;
                                // Fetching all the points in i-th route
                                for (int j = 0; j < path.size(); j++) {
                                    HashMap<String, String> point = path.get(j);

                                    double lat = Double.parseDouble(point.get("lat"));
                                    double lng = Double.parseDouble(point.get("lng"));
                                    LatLng position = new LatLng(lat, lng);
                                    points.add(position);
                                    if (previousPoint != null) {
                                        finalDistance = finalDistance + distFrom(previousPoint.latitude, previousPoint.longitude, position.latitude, position.longitude);
                                    }
                                    previousPoint = position;
                                }

                                // Adding all the points in the route to LineOptions
                                lineOptions.addAll(points);
                                lineOptions.width(2);
                                lineOptions.color(Color.RED);
                            }

                            googleMap.addPolyline(lineOptions);
//                            double finalDistance = totleDistance/1000;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.cancel();
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        });

        progressDialog = ProgressDialog.show(getActivity(),"Fetching Data","Please wait",true);

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private void getLocationWeatherForecast(final String toDate, final String fromDate, final LatLng latLng, final String key, final ArrayList<WeatherData> weatherDataArrayList) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, "http://wrgfs.co.in/gfs/rest_weathers/getWeatherForecasting.json",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String weatherResponse) {
                        System.out.println("Weather Forecast Response : " + weatherResponse);
                        try {
                            JSONObject jsonObject = new JSONObject(weatherResponse);
                            if (jsonObject.has("resp")) {

                                WeatherData firstWeatherData = null;
                                JSONArray weatherArray = jsonObject.getJSONArray("resp");
                                if (weatherArray.length() > 0) {

                                    String dateString = "";

                                    for (int i = 0; i < weatherArray.length(); i++) {
                                        JSONObject wData = weatherArray.getJSONObject(i);

                                        WeatherData data = new WeatherData();
                                        if (wData.getString("Date").equals(dateString)) {

                                            data.setTime(wData.getString("Time"));
                                            data.setGeoCoord(wData.getString("GeoCoord"));
                                            data.setTemperature(wData.getString("Temperature"));
                                            data.setWindSpeed(wData.getString("WindSpeed"));
                                            data.setWindDirection(wData.getString("WindDirection"));
                                            data.setRainfall(wData.getString("Rainfall"));
                                            data.setHumidity(wData.getString("Humidity"));
                                            weatherDataArrayList.add(data);
                                        } else {
                                            data.setDate(wData.getString("Date"));
                                            weatherDataArrayList.add(data);

                                            data = new WeatherData();
                                            data.setTime("Time");
                                            data.setGeoCoord("GeoCoord");
                                            data.setTemperature("C" + (char) 0x00B0);
                                            data.setWindSpeed("WindSpeed");
                                            data.setWindDirection("WindDirection");
                                            data.setRainfall("Rainfall");
                                            data.setHumidity("Humidity");
                                            weatherDataArrayList.add(data);

                                            data = new WeatherData();
                                            data.setTime(wData.getString("Time"));
                                            data.setGeoCoord(wData.getString("GeoCoord"));
                                            data.setTemperature(wData.getString("Temperature"));
                                            data.setWindSpeed(wData.getString("WindSpeed"));
                                            data.setWindDirection(wData.getString("WindDirection"));
                                            data.setRainfall(wData.getString("Rainfall"));
                                            data.setHumidity(wData.getString("Humidity"));
                                            weatherDataArrayList.add(data);

                                            dateString = wData.getString("Date");
                                        }
                                        if (i == 0) {
                                            firstWeatherData = data;
                                        }

                                    }

                                    if (firstWeatherData != null) {
                                        MarkerOptions options = new MarkerOptions();
                                        options.position(latLng);
                                        options.snippet(key);

                                        BitmapDescriptor bd = null;
                                        double rain_status = 0.0;
                                        double tmp_status = 0.0;
                                        try {
                                            rain_status = Double.valueOf(firstWeatherData.getRainfall());
                                            tmp_status = Double.valueOf(firstWeatherData.getTemperature());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (rain_status > 0 && rain_status < 2.0) {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.drizzle);
                                        } else if (rain_status > 2.0) {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.rainy);
                                        } else if (tmp_status <= 15 && tmp_status != 0.0) {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.m_cloudy);
                                        } else if (tmp_status > 15 && tmp_status < 28) {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.sunny);
                                        } else if (tmp_status >= 28) {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.hazy);
                                        } else {
                                            bd = BitmapDescriptorFactory.fromResource(R.drawable.na);
                                        }

                                        options.icon(bd);
                                        googleMap.addMarker(options);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));
                                    }


                                } else {
                                    Toast.makeText(getActivity(), "Not Able to fetch weather data for this input", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Not Able to fetch weather data for this input", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        if (getWeatherDataCount < markers.size()) {
                            String key = markers.get(getWeatherDataCount);
                            String[] latLngString = key.split(",");
                            double latitude = Double.valueOf(latLngString[0]);
                            double longitude = Double.valueOf(latLngString[1]);
                            LatLng latlng = new LatLng(latitude, longitude);
                            getLocationWeatherForecast(endDateString, startDateString, latlng, key, weatherData.get(key));
                            getWeatherDataCount++;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
//                 'userId'='wrms','password'='wrms@007','toDate'= '2016/03/02 11:30','fromDate'='2016/03/01 15:30','geoCoord'='27.12,78.49'
                Map<String, String> map = new HashMap<>();
                map.put("userId", "wrms");
                map.put("password", "wrms@007");
                map.put("toDate", toDate);
                map.put("fromDate", fromDate);
                map.put("geoCoord", latLng.latitude + "," + latLng.longitude);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = (double) (earthRadius * c);

        return dist / 1000;
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private void getLocationViaGoogle(final String string, final int locationType) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, getPlaceUrl(string),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Google Location Response : " + response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                            // Getting the parsed data as a List construct
                            List<HashMap<String, String>> list = placeJsonParser.parse(jObject);


                            if (list != null) {
                                String[] from = new String[]{"description"};
                                int[] to = new int[]{android.R.id.text1};

                                // Creating a SimpleAdapter for the AutoCompleteTextView
                                SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), list, android.R.layout.simple_list_item_1, from, to);

                                // Setting the adapter

                                if (locationType == START_LOCATION) {
                                    sourceLocationSearch.setAdapter(adapter);
                                    /*if (selectedSource == 1) {
                                        sourceLocationSearch.dismissDropDown();
                                    } else {
                                        sourceLocationSearch.showDropDown();
                                    }*/
                                } else {
                                    destLocationSearch.setAdapter(adapter);
                                   /* if (selectedDestination == 1) {
                                        destLocationSearch.dismissDropDown();
                                    } else {
                                        destLocationSearch.showDropDown();
                                    }*/
                                }

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
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private void getPlacesDetailViaGoogle(final String url, final int locationType) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Google Location Detail Response : " + response);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
                            // Getting the parsed data as a List construct
                            List<HashMap<String, String>> list = placeDetailsJsonParser.parse(jObject);

                            if (list != null) {
                                HashMap<String, String> hm = list.get(0);
                                // Getting latitude from the parsed data
                                double latitude = Double.parseDouble(hm.get("lat"));
                                // Getting longitude from the parsed data
                                double longitude = Double.parseDouble(hm.get("lng"));

                                LatLng point = new LatLng(latitude, longitude);

                                // Creating MarkerOptions
                                MarkerOptions options = new MarkerOptions();

                                // Setting the position of the marker
                                options.position(point);

                                if (locationType == START_LOCATION) {
                                    startPoint = point;
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    System.out.println("Start Latitude : " + latitude + " longitude : " + longitude);
                                } else {
                                    endPoint = point;
                                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    System.out.println("End Latitude : " + latitude + " longitude : " + longitude);
                                }

                                if (startPoint != null && endPoint != null) {
                                    final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(startPoint);
                                    builder.include(endPoint);
                                    try {
                                        int padding = 20;
                                        // offset from edges of the map in pixels
                                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(builder.build(), padding);
                                        googleMap.moveCamera(cu);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String url = getDirectionsUrl(startPoint, endPoint);
                                    getDirectionViaGoogle(url);

                                }

                                googleMap.addMarker(options);

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
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private String getPlaceDetailsUrl(String ref) {

        // Obtain browser key from https://code.google.com/apis/console
        String key = "key=" + getResources().getString(R.string.browser_key);

        // reference of place
        String reference = "reference=" + ref;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = reference + "&" + sensor + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;

        return url;
    }

    private String getPlaceUrl(String ref) {

        // For storing data from web service
        String data = "";

        // Obtain browser key from https://code.google.com/apis/console

        String key = "key=" + getResources().getString(R.string.browser_key);
        String input = "";

        try {
            input = "input=" + URLEncoder.encode(ref, "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        // place type to be searched
        String types = "types=geocode";

        // place searched by country
        String country = "components=country:in";

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = input + "&" + types + "&" + sensor + "&" + country + "&" + key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;

        return url;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String uri);
    }
}
