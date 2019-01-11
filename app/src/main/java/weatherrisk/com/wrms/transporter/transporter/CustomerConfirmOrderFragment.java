package weatherrisk.com.wrms.transporter.transporter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import weatherrisk.com.wrms.transporter.adapter.AcceptedOrderAdapter;
import weatherrisk.com.wrms.transporter.adapter.CustomerConfirmOrderAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.dataobject.CustomerConfirmOrder;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;
import weatherrisk.com.wrms.transporter.fragment.AddTripFragment;
import weatherrisk.com.wrms.transporter.fragment.SingleChoiceVehicleListFragment;
import weatherrisk.com.wrms.transporter.utils.MyUtility;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerConfirmOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerConfirmOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VEHICLE_LIST = "vehicleList";
    private static final String ORDERS_LIST = "orders_list";

    public static final String FRAGMENT_TAG = "Start Trip";

    ArrayList<VehicleData> vehicleList = new ArrayList<>();





    public CustomerConfirmOrderFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    public static CustomerConfirmOrderFragment newInstance(ArrayList<VehicleData> vehicleList) {
        CustomerConfirmOrderFragment fragment = new CustomerConfirmOrderFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        String vehicleString = "";

        if(vehicleList!=null){
            for(VehicleData data: vehicleList){
                vehicleString = vehicleString+data.getVehicleId()+",";
            }
        }

        db = new DBAdapter(getActivity());
        db.open();


    }


   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_trip, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add) {

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, SingleChoiceVehicleListFragment.newInstance(AddTripFragment.FRAGMENT_TAG, vehicleList), FRAGMENT_TAG);
            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/


    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity())
                    .setActionBarTitle(getResources().getString(R.string.customer_confirm_orders));
        }
    }

    DBAdapter db;
    ArrayList<CustomerConfirmOrder> customerConfirmOrders = new ArrayList<>();
    CustomerConfirmOrderAdapter adapter;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ListView listview;
    TextView noDataText;
    Button addTrip;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);
        }
        db = new DBAdapter(getActivity());
        db.open();
        loadConfirmOrderList();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_confirm_order, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        noDataText = (TextView)view.findViewById(R.id.no_confrm_data);

        addTrip = (Button)view.findViewById(R.id.add_trip_btn);

        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, SingleChoiceVehicleListFragment.newInstance(AddTripFragment.FRAGMENT_TAG, vehicleList), FRAGMENT_TAG);
                ft.commit();*/

                Intent intent= new Intent(getActivity(),VihicleChoiceActivity.class);
                intent.putParcelableArrayListExtra("VehicleList",vehicleList);
                startActivity(intent);
            }
        });

        return view;
    }


    private void loadConfirmOrderList() {
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.PENDING_ORDERS_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String confirmOrder) {
                        dialog.dismiss();
                        try {
                            System.out.println("Confirm Order Response : " + confirmOrder);
                            JSONObject jsonObject = new JSONObject(confirmOrder);

                            if  (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray cityArray = jsonObject.getJSONArray("transporterOrders");
                                    if (cityArray.length() > 0) {
                                        for (int i = 0; i < cityArray.length(); i++) {
                                            JSONObject jObject = cityArray.getJSONObject(i);
                                            CustomerConfirmOrder order = new CustomerConfirmOrder(jObject,db);
                                            customerConfirmOrders.add(order);
                                        }
                                  //      adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(getActivity(), "Order Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    String message = "Order Not Found";
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            }else if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("2"))) {
                                SharedPreferences preferences = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                               // db.resetDatabase();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else {
                               // Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }

                            if (customerConfirmOrders.size()>0){

                                listview.setVisibility(View.VISIBLE);
                                noDataText.setVisibility(View.GONE);

                                adapter = new CustomerConfirmOrderAdapter(getActivity(), customerConfirmOrders);
                                listview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }else {

                                listview.setVisibility(View.GONE);
                                noDataText.setVisibility(View.VISIBLE);
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
                String userID = prefs.getString(AppController.PREFERENCE_USER_ID,"");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN,"");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken",accessToken);
                map.put("UserId", userID);
                map.put("OrderStatus", AppController.CONFIRM_ORDER_TYPE);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Confirm Orders",
                "Please wait.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

}
