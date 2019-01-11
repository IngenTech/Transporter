package weatherrisk.com.wrms.transporter.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
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
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.adapter.TripListAdapter;
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManageTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManageTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageTripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VEHICLE_LIST = "vehicleList";
    private static final String TRIP_LIST = "trip_list";

    public static final String FRAGMENT_TAG = "Manage Trip";

    private ArrayList<VehicleData> vehicleList;
    private ArrayList<TripData> tripDatas;


    private OnFragmentInteractionListener mListener;

    public ManageTripFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            /*ArrayList<VehicleData> vehicleList = new ArrayList<>();
            db.open();
            Cursor vehicleCursor = db.getVehicle();
            if (vehicleCursor.moveToFirst()) {
                do {

                    String rowId = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.ID));
                    String vehicleId = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_ID));
                    String vehicleNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_NO));
                    String imei = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.IMEI));
                    String modelNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.MODEL_NO));
                    String registrationNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.REGISTRATION_NO));
                    String insurenceNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.INSURANCE_NO));
                    String validityDate = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VALIDITY_DATE));
                    String pollutionNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.POLLUTION_NO));
                    String yearOfPurchase = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.YEAR_OF_PURCHASE));
                    String capacity = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CAPACITY));
                    String refrigerated = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.REFRIGERATED));
                    String closeDoor = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CLOSED_DORE));

                    vehicleList.add(new VehicleData(vehicleId,vehicleNo, imei,modelNo,registrationNo,insurenceNo,
                            validityDate,pollutionNo,yearOfPurchase,capacity,refrigerated,closeDoor,VehicleData.NOT_SELECTED));

                } while (vehicleCursor.moveToNext());
            }
            vehicleCursor.close();
            db.close();

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, SingleChoiceVehicleListFragment.newInstance(AddTripFragment.FRAGMENT_TAG, vehicleList), FRAGMENT_TAG);
//            ft.addToBackStack(FRAGMENT_TAG);
            ft.commit();*/

            String vehicleString = "";

            if(vehicleList!=null){
                for(VehicleData data: vehicleList){
                    vehicleString = vehicleString+data.getVehicleId()+",";
                }
            }
            //Remove last comma from string
            vehicleString = vehicleString.substring(0, vehicleString.length()-1);

            Fragment fragment = StartTripFragment.newInstance(vehicleList);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();

//            acceptedCustomerOrderRequest(vehicleString);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vehicleList Parameter 1.
     * @param tripDatas Parameter 2.
     * @return A new instance of fragment ManageTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageTripFragment newInstance(ArrayList<VehicleData> vehicleList, ArrayList<TripData> tripDatas) {
        ManageTripFragment fragment = new ManageTripFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        args.putParcelableArrayList(TRIP_LIST, tripDatas);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);
            tripDatas = getArguments().getParcelableArrayList(TRIP_LIST);
        }
    }


    ListView listView;
    TextView tagTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_manage_trip, container, false);

        listView = (ListView)view.findViewById(R.id.tripListView);
        tagTextView = (TextView)view.findViewById(R.id.TAG);
        tagTextView.setText("RUNNING TRIPS");

        return view;
    }



    TripListAdapter adapter;
    DBAdapter db;
    ArrayList<TripData> tripData = new ArrayList<>();
    SharedPreferences prefs;
    ProgressDialog dialog;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        String vehicleString = "";

        if(vehicleList!=null){
            for(VehicleData data: vehicleList){
                vehicleString = vehicleString+data.getVehicleId()+",";
            }
        }
        db = new DBAdapter(getActivity());
        db.open();

        //Remove last comma from string
        vehicleString = vehicleString.substring(0, vehicleString.length()-1);

        adapter = new TripListAdapter(getActivity(),tripDatas,false);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity())
                    .setActionBarTitle(getResources().getString(R.string.trip_manage));
        }
    }

    private void tripRequest(final String vehicleListString) {
        tripData = new ArrayList<>();
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.RUNNING_TRIP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String tripResponse) {
                        dialog.dismiss();
                        try {

                            System.out.println("Trip Response : " + tripResponse);
                            JSONObject jsonObject = new JSONObject(tripResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.getString("result").equals("success")) {

                                    String accountId = jsonObject.getString("account_id");
                                    JSONArray accountArray = jsonObject.getJSONArray("Trips");
                                    if (accountArray.length() > 0) {

                                        for (int i = 0; i < accountArray.length(); i++) {
                                            JSONObject jObject = accountArray.getJSONObject(i);
                                            TripData data = new TripData();
                                            data.setTripId(jObject.getString("trip_id"));
                                            data.setAccountId(accountId);
                                            data.setFromCountryId(jObject.getString("from_country_id"));
                                            data.setToCountryId(jObject.getString("to_country_id"));
                                            data.setVehicleId(jObject.getString("vehicle_id"),db);
                                            data.setFromStateId(jObject.getString("from_state_id"),db);
                                            data.setToStateId(jObject.getString("to_state_id"),db);
                                            data.setFromCityId(jObject.getString("from_city_id"),db);
                                            data.setToCityId(jObject.getString("to_city_id"),db);
                                            data.setFromAddress(jObject.getString("from_address"));
                                            data.setToAddress(jObject.getString("to_address"));
                                            data.setCustomerName(jObject.getString("customer_name"));
                                            data.setOrderRequestId(jObject.getString("order_request_id"));
                                            data.setMaterialTypeId(jObject.getString("material_type_id"));
                                            data.setQuantity(jObject.getString("quantity"));
                                            data.setInvoiceIds(jObject.getString("invoice_amount"));
                                            data.setDispatchDate(jObject.getString("dispatch_date"));
                                            data.setArrivalDate(jObject.getString("arrival_date"));
                                            data.setDriverName(jObject.getString("driver_name"));
                                            data.setDriverMobileNo(jObject.getString("driver_mobile_no"));
                                            tripData.add(data);
                                        }

                                        Fragment fragment = null;
                                        if(tripData!=null && tripData.size()>0){
                                            fragment = StartTripFragment.newInstance(vehicleList);
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_container, fragment);
                                            fragmentTransaction.commit();

                                        }else{
                                            Toast.makeText(getActivity(), "No Running Trip", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No Running Trip", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Request has been Refused", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
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
                map.put("vehicle_ids", vehicleListString);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }


                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Running Trip",
                "Fetching data...", true);
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }


    private void acceptedCustomerOrderRequest(final String vehicleListString) {
        tripData = new ArrayList<>();
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ACCEPTED_CUSTOMER_ORDER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String tripResponse) {
                        dialog.dismiss();
                        try {

                            System.out.println("ACCEPTED_CUSTOMER_ORDER Response : " + tripResponse);
                            JSONObject jsonObject = new JSONObject(tripResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.getString("result").equals("success")) {

                                    String accountId = jsonObject.getString("account_id");
                                    JSONArray accountArray = jsonObject.getJSONArray("Trips");
                                    if (accountArray.length() > 0) {

                                        for (int i = 0; i < accountArray.length(); i++) {
                                            JSONObject jObject = accountArray.getJSONObject(i);
                                            TripData data = new TripData();
                                            data.setTripId(jObject.getString("trip_id"));
                                            data.setAccountId(accountId);
                                            data.setFromCountryId(jObject.getString("from_country_id"));
                                            data.setToCountryId(jObject.getString("to_country_id"));
                                            data.setVehicleId(jObject.getString("vehicle_id"),db);
                                            data.setFromStateId(jObject.getString("from_state_id"),db);
                                            data.setToStateId(jObject.getString("to_state_id"),db);
                                            data.setFromCityId(jObject.getString("from_city_id"),db);
                                            data.setToCityId(jObject.getString("to_city_id"),db);
                                            data.setFromAddress(jObject.getString("from_address"));
                                            data.setToAddress(jObject.getString("to_address"));
                                            data.setCustomerName(jObject.getString("customer_name"));
                                            data.setOrderRequestId(jObject.getString("order_request_id"));
                                            data.setMaterialTypeId(jObject.getString("material_type_id"));
                                            data.setQuantity(jObject.getString("quantity"));
                                            data.setInvoiceIds(jObject.getString("invoice_amount"));
                                            data.setDispatchDate(jObject.getString("dispatch_date"));
                                            data.setArrivalDate(jObject.getString("arrival_date"));
                                            data.setDriverName(jObject.getString("driver_name"));
                                            data.setDriverMobileNo(jObject.getString("driver_mobile_no"));
                                            tripData.add(data);
                                        }

                                        Fragment fragment = null;
                                        if(tripData!=null && tripData.size()>0){
                                            fragment = StartTripFragment.newInstance(vehicleList);
                                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.frame_container, fragment);
                                            fragmentTransaction.commit();

                                        }else{
                                            Toast.makeText(getActivity(), "No Running Trip", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No Running Trip", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getActivity(), "Request has been Refused", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
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
                map.put("transporterId", accountId);
//                map.put("vehicle_ids", vehicleListString);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }


                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Running Trip",
                "Fetching data...", true);
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
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
        void onFragmentInteraction(Uri uri);
    }
}
