package weatherrisk.com.wrms.transporter.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.adapter.PendingOrderAdapter;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String FRAGMENT_TAG = "Pending Orders";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PendingOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingOrderFragment newInstance(String param1, String param2) {
        PendingOrderFragment fragment = new PendingOrderFragment();
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

    ListView listView;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ArrayList<OrderData> pendingOrderDataArray = new ArrayList<>();
    HashMap<String, ArrayList<InvoiceData>> invoices = new HashMap<>();
    HashMap<String, ArrayList<MaterialTypeData>> materials = new HashMap<>();
    PendingOrderAdapter adapter;
    DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_order, container, false);

        listView = (ListView) view.findViewById(R.id.orderListView);

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        db = new DBAdapter(getActivity());
        db.open();

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrderData pendingOrderData = pendingOrderDataArray.get(position);
                if (pendingOrderData != null) {
                    ArrayList<InvoiceData> invoiceDatas = invoices.get(pendingOrderData.getOrderId());
                    ArrayList<MaterialTypeData> materialTypeDatas = new ArrayList<MaterialTypeData>();
                    if(invoiceDatas!=null) {
                        for (InvoiceData iData : invoiceDatas) {
                            ArrayList<MaterialTypeData> mDatas = materials.get(iData.getInvoiceTempId());
                            for (MaterialTypeData m : mDatas) {
                                materialTypeDatas.add(m);
                            }
                        }
                    }
                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.frame_container, OrderDetailFragment.newInstance(pendingOrderData, invoiceDatas, materialTypeDatas), FRAGMENT_TAG);
                    ft.commit();
                }
            }
        });


        pendingOrderRequest();
    }

    private void pendingOrderRequest() {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.PENDING_ORDERS_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String orderResponse) {
                        dialog.dismiss();
                        try {

                            System.out.println("Order Response : " + orderResponse);
                            JSONObject jsonObject = new JSONObject(orderResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.getString("result").equals("success")) {

//                                    String accountId = jsonObject.getString("account_id");
                                    JSONArray accountArray = jsonObject.getJSONArray("customerOrders");
                                    if (accountArray.length() > 0) {

                                        for (int i = 0; i < accountArray.length(); i++) {
                                            JSONObject jObject = accountArray.getJSONObject(i);
                                            OrderData data = new OrderData();
                                            data.setOrderId(jObject.getString("DocketNo"));
                                            data.setCustomerId(jObject.getString("CustomerID"));
                                            data.setCustomerName(jObject.getString("CustomerName"));
                                            data.setFromCityId(jObject.getString("FromCityID"), db);
                                            data.setToCityId(jObject.getString("ToCityID"), db);
                                            data.setFromAddress(jObject.getString("FromAddress"));
                                            data.setToAddress(jObject.getString("ToAddress"));
                                            data.setVehicleQuantity(jObject.getString("VehicleQuantity"));
//                                            data.setMaterialTypeId(jObject.getString("material_id"));
//                                            data.setItem(jObject.getString("item"));
                                            data.setCapacity(jObject.getString("Capacity"));
                                            data.setRate(jObject.getString("Rate"));
                                            data.setDoorStatus(jObject.getString("DoorStatus"));
                                            data.setRefrigerated(jObject.getString("Referigerated"));
//                                            data.setInvoiceAmount(jObject.getString("invoice_amount"));
                                            data.setOrderDate(jObject.getString("OrderDateTime"));
                                            data.setOrderStatus(jObject.getString("OrderStatus"));

                                            JSONArray invoicesArray = jObject.getJSONArray("Invoices");
                                            if (invoicesArray.length() > 0) {
                                                ArrayList<InvoiceData> orderInvoices = new ArrayList<>();
                                                for (int j = 0; j < invoicesArray.length(); j++) {
                                                    JSONObject invoiceJsonObject = invoicesArray.getJSONObject(j);
                                                    InvoiceData invoiceData = new InvoiceData();
                                                    invoiceData.setDate(invoiceJsonObject.getString("InvoiceDate"));
                                                    invoiceData.setInvoiceAmount(invoiceJsonObject.getString("Amount"));
                                                    invoiceData.setInvoiceTempId(invoiceJsonObject.getString("InvoiceId"));
                                                    invoiceData.setInvoiceNumber(invoiceJsonObject.getString("InvoiceNumber"));
                                                    invoiceData.setInvoicePath(invoiceJsonObject.getString("ImageUrl"));
                                                    invoiceData.setSno(jObject.getString("DocketNo"));

                                                    JSONArray materialArray = invoiceJsonObject.getJSONArray("Materials");
                                                    if (materialArray.length() > 0) {
                                                        ArrayList<MaterialTypeData> materialTypeArray = new ArrayList<>();
                                                        for (int k = 0; k < materialArray.length(); k++) {
                                                            JSONObject materialJsonObject = materialArray.getJSONObject(k);
                                                            MaterialTypeData materialTypeData = new MaterialTypeData();
                                                            materialTypeData.setMaterialTypeId(materialJsonObject.getString("MaterialTypeId"));
                                                            materialTypeData.setAmount(materialJsonObject.getString("MaterialAmount"));
                                                            materialTypeData.setMaterialTypeName(materialJsonObject.getString("MaterialName"));
                                                            materialTypeData.setInvoiceId(invoiceJsonObject.getString("InvoiceId"));
                                                            materialTypeData.setId("");
                                                            materialTypeData.setRemark("");
                                                            materialTypeArray.add(materialTypeData);
                                                        }
                                                        materials.put(invoiceData.getInvoiceTempId(), materialTypeArray);
                                                    }
                                                    orderInvoices.add(invoiceData);
                                                }
                                                invoices.put(data.getOrderId(), orderInvoices);
                                            }
                                            pendingOrderDataArray.add(data);
                                        }

                                        if(adapter == null){
                                            adapter = new PendingOrderAdapter(getActivity(), pendingOrderDataArray);
                                            listView.setAdapter(adapter);
                                        }else {
                                            adapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No Transporter Found in this Account", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    String message = "No Pending Orders";
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
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
