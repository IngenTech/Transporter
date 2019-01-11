package weatherrisk.com.wrms.transporter.transporter;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
import weatherrisk.com.wrms.transporter.adapter.CustomerPendingOrderAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.dataobject.CustomerPendingOrder;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerPendingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerPendingOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public CustomerPendingOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerPendingOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerPendingOrderFragment newInstance(String param1, String param2) {
        CustomerPendingOrderFragment fragment = new CustomerPendingOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void onResume() {
        super.onResume();
        if(getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity())
                    .setActionBarTitle(getResources().getString(R.string.customer_pending_order));
        }
    }

    DBAdapter db;
    ArrayList<CustomerPendingOrder> customerConfirmOrders = new ArrayList<>();
    CustomerPendingOrderAdapter adapter;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ListView listview;
    TextView noPendingData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        db = new DBAdapter(getActivity());
        db.open();

        loadPendingOrderList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_pending_order, container, false);;
        listview = (ListView)view.findViewById(R.id.listview);
        noPendingData = (TextView)view.findViewById(R.id.no_pending_data);

        return view;
    }

    private void loadPendingOrderList() {


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
                                            CustomerPendingOrder order = new CustomerPendingOrder(jObject,db);
                                            customerConfirmOrders.add(order);
                                        }
                                        noPendingData.setVisibility(View.GONE);
                                        adapter = new CustomerPendingOrderAdapter(getActivity(),customerConfirmOrders);
                                        listview.setAdapter(adapter);
                                    } else {

                                        Toast.makeText(getActivity(), "Order Not Found", Toast.LENGTH_LONG).show();
                                        noPendingData.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    String message = "Order Not Found";
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                                    noPendingData.setVisibility(View.VISIBLE);
                                }
                            }else if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("2"))) {
                                SharedPreferences preferences = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                             //   db.cle();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();
                            }
                            else {
                             //   Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                                noPendingData.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                            noPendingData.setVisibility(View.VISIBLE);
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
                map.put("OrderStatus", AppController.PENDING_ORDER_TYPE);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Pending Orders",
                "Please wait.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


        if (customerConfirmOrders.size()<1){


        }

    }


}
