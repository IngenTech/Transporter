package weatherrisk.com.wrms.transporter.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import weatherrisk.com.wrms.transporter.bean.TravelData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.adapter.TravelListAdapter;

import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TravelReportFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TravelReportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TravelReportFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String INTERVAL = "intervalButton";
    private static final String VEHICLE = "vehicle";
    public static final String FRAGMENT_TAG = "Travel";

    private String startTime;
    private String endTime;
    private String interval;
    private ArrayList<VehicleData> vehicleDataList;

    private OnFragmentInteractionListener mListener;

    public TravelReportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param startTime Parameter 1.
     * @param endTime Parameter 2.
     * @return A new instance of fragment TravelReportFragment.
     */

    public static TravelReportFragment newInstance(String startTime, String endTime, String interval, ArrayList<VehicleData> vehicleData) {
        TravelReportFragment fragment = new TravelReportFragment();
        Bundle args = new Bundle();
        args.putString(START_TIME, startTime);
        args.putString(END_TIME, endTime);
        args.putString(INTERVAL, interval);
        args.putParcelableArrayList(VEHICLE, vehicleData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            startTime = getArguments().getString(START_TIME);
            endTime = getArguments().getString(END_TIME);
            vehicleDataList = getArguments().getParcelableArrayList(VEHICLE);
            interval = getArguments().getString(INTERVAL);

            System.out.println("Distance startTime  : " + startTime);
            System.out.println("Distance endTime  : " + endTime);
            System.out.println("Distance vehicleDataList size : " + vehicleDataList.size());
            System.out.println("Distance intervalButton  : " + interval);

            onSelectionOfTheFragment(FRAGMENT_TAG);
        }
    }

    DecimalFormat df = new DecimalFormat("#.##");
    ExpandableListView expendableListView;
    TravelListAdapter adapter;
    TextView durationTag;
    double totalDist = 0.0;
    List<String> header = new ArrayList<>();
    HashMap<String,List<TravelData>> listDataChild = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_travel_report, container, false);
        expendableListView = (ExpandableListView)view.findViewById(R.id.lvExp);
        expendableListView.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        durationTag = (TextView)view.findViewById(R.id.TAG);
        durationTag.setText(startTime+" TO "+endTime);

        String jsonParam = createImeiJsonString();
        getVehiclesTravelReport(jsonParam);
        adapter = new TravelListAdapter(getActivity(),header,listDataChild);
        expendableListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }


    SharedPreferences prefs;

    private String createImeiJsonString() {
        String jsonString = "";
        if (prefs == null) {
            prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
        }
        String accointId = prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "");
//        VehicleList
        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for (VehicleData vehicleData : vehicleDataList) {
                JSONObject jObject = new JSONObject();
                jObject.put("VehicleId", vehicleData.getVehicleId());
                jsonArray.put(jObject);
            }
            interval = "3600";
//            jsonObject.put("AccountId", accointId);
            jsonObject.put("StartDateTime", startTime);
            jsonObject.put("EndDateTime", endTime);
            jsonObject.put("UserInterval", interval);
            jsonObject.put("VehicleIdList", jsonArray);
            jsonString = jsonObject.toString();
            System.out.println("jsonString : " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }


    private void getVehiclesTravelReport(final String jsonImeiList) {


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.TRAVEL_REPORT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String travelResponse) {
                        try {

                            System.out.println("Travel Report Response : " + travelResponse);
                            JSONObject jsonObject = new JSONObject(travelResponse);

                            if (jsonObject.has("Status")) {
                                if (jsonObject.get("Status").equals("success")) {

                                    JSONArray vehicleArray = jsonObject.getJSONArray("VehicleList");
                                    if (vehicleArray.length() > 0) {

                                        for (int i = 0; i < vehicleArray.length(); i++) {

                                            JSONObject vehicleJonObject = vehicleArray.getJSONObject(i);
                                            String imei = vehicleJonObject.getString("VehicleImei");
//                                            String vehicleName = vehicleJonObject.getString("VehicleName");
                                            String vehicleName = "";
                                            if(vehicleDataList.size()>i){
                                                vehicleName = vehicleDataList.get(i).getVehicleNo();
                                            }

                                            JSONArray distanceArray = vehicleJonObject.getJSONArray("TravelArray");

                                            List<TravelData> listDataChildItem = new ArrayList<>();
                                            double totalDistance = 0.0;

                                            System.out.println("distanceArray.length() : " + distanceArray.length());

                                            if (distanceArray.length() > 0) {
                                                for (int j = 0; j < distanceArray.length(); j++) {
                                                    JSONObject distanceJsonObject = distanceArray.getJSONObject(j);
                                                    double distance = distanceJsonObject.getDouble("Distance");
                                                    String startDateTime = distanceJsonObject.getString("StartDateTime");
                                                    String endDateTime = distanceJsonObject.getString("EndDateTime");
                                                    String startLatitude = distanceJsonObject.getString("StartLatitude");
                                                    String startLongitude = distanceJsonObject.getString("StartLongitude");
                                                    String endLatitude = distanceJsonObject.getString("EndLatitude");
                                                    String endLongitude = distanceJsonObject.getString("EndLongitude");
                                                    String traveledTime = distanceJsonObject.getString("TraveledTime");
                                                    String maxSpeed = distanceJsonObject.getString("MaxSpeed");
                                                    String avgSpeed = distanceJsonObject.getString("AvgSpeed");

                                                    LatLng startLatLng = new LatLng(Double.parseDouble(startLatitude), Double.parseDouble(startLongitude));
                                                    LatLng endLatLng = new LatLng(Double.parseDouble(endLatitude), Double.parseDouble(endLongitude));

                                                    TravelData TravelData = new TravelData(startDateTime, endDateTime, String.valueOf(distance), traveledTime, startLatLng, endLatLng);
                                                    getAddressOfStartLatLng(TravelData);
                                                    listDataChildItem.add(TravelData);

                                                    totalDistance = totalDistance + distance;
                                                }
                                            }
                                            String headerString = vehicleName + "_" + totalDistance;

                                            header.add(headerString);
                                            listDataChild.put(headerString, listDataChildItem);
                                            adapter.notifyDataSetChanged();

                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No Data found for the vehicle", Toast.LENGTH_LONG).show();
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
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.cancel();
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
//                System.out.println("Get Params has been called");
                Map<String, String> map = new HashMap<>();
                map.put("InputDetail", jsonImeiList);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        progressDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Travel Report...", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }
    ProgressDialog progressDialog;
    public void getAddressOfStartLatLng(final TravelData data) {

        String address = String.format(Locale.ENGLISH,MyUtility.URL.ADDRESS_OF_LAT_LNG_API, data.getStartLatLng().latitude, data.getStartLatLng().longitude);

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


    public void getAddressOfEndLatLng(final TravelData data) {

        String address = String
                .format(Locale.ENGLISH, MyUtility.URL.ADDRESS_OF_LAT_LNG_API,  data.getEndLatLng().latitude, data.getEndLatLng().longitude);

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




    public void onSelectionOfTheFragment(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
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
        void onFragmentInteraction(String title);
    }
}
