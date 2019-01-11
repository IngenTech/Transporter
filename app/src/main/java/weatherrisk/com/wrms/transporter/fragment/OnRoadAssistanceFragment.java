package weatherrisk.com.wrms.transporter.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
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
import weatherrisk.com.wrms.transporter.utils.AppConstant;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.adapter.OnRoadAssistanceAdapter;
import weatherrisk.com.wrms.transporter.dataobject.OnRoadAssistanceData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnRoadAssistanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OnRoadAssistanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnRoadAssistanceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String FRAGMENT_TAG = "On Road Assistance";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ListView listView;
    ArrayList<OnRoadAssistanceData> onRoadAssistanceData = new ArrayList<>();
    OnRoadAssistanceAdapter adapter;
    DBAdapter db;

    private OnFragmentInteractionListener mListener;

    public OnRoadAssistanceFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_on_road_assistance, menu);

        *//*final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Vehicle No.");
        searchView.setOnQueryTextListener(this);*//*
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            ArrayList<VehicleData> vehicleList = new ArrayList<>();
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
                    String insuranceNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.INSURANCE_NO));
                    String validityDate = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VALIDITY_DATE));
                    String pollutionNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.POLLUTION_NO));
                    String yearOfPurchase = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.YEAR_OF_PURCHASE));
                    String capacity = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CAPACITY));
                    String refrigerated = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.REFRIGERATED));
                    String closeDoor = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CLOSED_DORE));

                    vehicleList.add(new VehicleData(vehicleId,vehicleNo, imei,modelNo,registrationNo,insuranceNo,
                            validityDate,pollutionNo,yearOfPurchase,capacity,refrigerated,closeDoor,VehicleData.NOT_SELECTED));

                } while (vehicleCursor.moveToNext());
            }
            vehicleCursor.close();
            db.close();

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, SingleChoiceVehicleListFragment.newInstance(AddOnRoadAssistanceFragment.FRAGMENT_TAG, vehicleList), FRAGMENT_TAG);
//            ft.addToBackStack(FRAGMENT_TAG);
            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnRoadAssistanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OnRoadAssistanceFragment newInstance(String param1, String param2) {
        OnRoadAssistanceFragment fragment = new OnRoadAssistanceFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_on_road_assistance2, container, false);
        listView = (ListView)view.findViewById(R.id.listView);

        db = new DBAdapter(getActivity());

        onRoadAssistanceRequest();



        return view;
    }




    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.road_assistance));
        }
    }

    private void closeOnRoadAssistanceRequest(final String accountId, final String feedBack, final String bookingId, final int position) {

        System.out.println("close trip get called" + bookingId);
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.CLOSE_ON_ROAD_ASSISTANCE_API + bookingId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {

                            //{"result":"success","case_id":"10","account_id":"2082"}

                            System.out.println("Close On road assistance Response : " + response);
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    onRoadAssistanceData.remove(position);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(getActivity(), "Request has been denied", Toast.LENGTH_LONG).show();
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
                dialog.dismiss();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getActivity().getResources().getString(R.string.server_api_key);
                Map<String, String> map = new HashMap<>();
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                map.put("case_id", bookingId);
                map.put("feedback", feedBack);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(getActivity(), "",
                "Closing Case.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private void onRoadAssistanceRequest() {

        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ON_ROAD_ASSISTANCE_REQUEST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String tripResponse) {
                        dialog.dismiss();
                        try {

                            System.out.println("Trip Response : " + tripResponse);
                            JSONObject jsonObject = new JSONObject(tripResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.getString("Result").equals("Success")) {

                                    JSONArray casesArray = jsonObject.getJSONArray("OnRoadAssistanceList");
                                    if (casesArray.length() > 0) {

                                        for (int i = 0; i < casesArray.length(); i++) {
                                            JSONObject jObject = casesArray.getJSONObject(i);
                                            OnRoadAssistanceData data = new OnRoadAssistanceData();

                                            data.setBookingId(jObject.getString("serial"));
                                            String vehicleId = jObject.getString("vehicle_id");
                                            data.setVehicleId(vehicleId);

                                            data.setBookingDate(jObject.getString("date_of_booking"));
                                            data.setContactNo(jObject.getString("contact_no"));
                                            data.setLandmark(jObject.getString("landmark"));
                                            data.setProblem(jObject.getString("problem"));
                                          /*  data.setLatitude(jObject.getString("latitude"));
                                            data.setLongitude(jObject.getString("longitude"));*/
                                            onRoadAssistanceData.add(data);
                                        }

                                        adapter = new OnRoadAssistanceAdapter(getActivity(),onRoadAssistanceData);
                                        listView.setAdapter(adapter);

                                    } else {
                                        Toast.makeText(getActivity(), "Road Assistance not found", Toast.LENGTH_LONG).show();
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

                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }


                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "On Road Assistance",
                "Fetching data...", true);
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }


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
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
