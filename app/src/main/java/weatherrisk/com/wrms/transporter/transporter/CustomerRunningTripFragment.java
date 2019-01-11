package weatherrisk.com.wrms.transporter.transporter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import weatherrisk.com.wrms.transporter.LoginActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.adapter.CustomerRunningTripAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.dataobject.CustomerConfirmOrder;
import weatherrisk.com.wrms.transporter.dataobject.CustomerRunningTripData;
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;
import weatherrisk.com.wrms.transporter.fragment.StartTripFragment;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerRunningTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerRunningTripFragment extends Fragment {


    private static final String VEHICLE_LIST = "vehicleList";
    private static final String TRIP_LIST = "trip_list";

    public static final String FRAGMENT_TAG = "Manage Trip";

    private ArrayList<VehicleData> vehicleList;
    private ArrayList<TripData> tripDatas;
    DBAdapter db;
    ArrayList<CustomerRunningTripData> customerRunningTripDatas = new ArrayList<>();
    CustomerRunningTripAdapter adapter;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ListView listview;
    TextView noData;

    public CustomerRunningTripFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    public static CustomerRunningTripFragment newInstance(ArrayList<VehicleData> vehicleList, ArrayList<TripData> tripDatas) {
        CustomerRunningTripFragment fragment = new CustomerRunningTripFragment();
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
        loadRunningTripList();
    }

    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity())
                    .setActionBarTitle(getResources().getString(R.string.running_trips));
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_running_trip, container, false);

        db  = new DBAdapter(getActivity());
        db.open();
        listview = (ListView) view.findViewById(R.id.listview);
        noData = (TextView)view.findViewById(R.id.no_data);
        return view;
    }

    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            String vehicleString = "";

            if(vehicleList!=null){
                for(VehicleData data: vehicleList){
                    vehicleString = vehicleString+data.getVehicleId()+",";
                }
            }
            //Remove last comma from string
            vehicleString = vehicleString.substring(0, vehicleString.length()-1);

            Fragment fragment = CustomerConfirmOrderFragment.newInstance(vehicleList);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/
    private void loadRunningTripList() {

        StringRequest stringRunningTripRequest = new StringRequest(Request.Method.POST, MyUtility.URL.RUNNING_TRIP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String confirmOrder) {
                        dialog.dismiss();
                        try {
                            System.out.println("Running Order Response : " + confirmOrder);
                            JSONObject jsonObject = new JSONObject(confirmOrder);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray cityArray = jsonObject.getJSONArray("Trips");
                                    if (cityArray.length() > 0) {
                                        for (int i = 0; i < cityArray.length(); i++) {
                                            JSONObject jObject = cityArray.getJSONObject(i);
                                            CustomerRunningTripData order = new CustomerRunningTripData(jObject, db);
                                            customerRunningTripDatas.add(order);
                                        }
                                    }

                                } else {
                                    String message = jsonObject.has("Message")? jsonObject.getString("Message") : "No running trip";
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            }else if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("2"))) {
                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "No trip history";
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();


                                SharedPreferences preferences = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                              //  db.resetDatabase();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();


                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }

                            if (customerRunningTripDatas.size()>0){

                                listview.setVisibility(View.VISIBLE);
                                noData.setVisibility(View.GONE);

                                adapter = new CustomerRunningTripAdapter(getActivity(), customerRunningTripDatas);
                                listview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else {

                                listview.setVisibility(View.GONE);
                                noData.setVisibility(View.VISIBLE);
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

                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");


                Map<String, String> map = new HashMap<>();
                map.put("AccessToken",accessToken);
                map.put("UserId", accountId);
               // map.put("OrderStatus", AppConstant.Constants.);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Running Trips",
                "Please wait.....", true);
        AppController.getInstance().addToRequestQueue(stringRunningTripRequest);

    }


}
