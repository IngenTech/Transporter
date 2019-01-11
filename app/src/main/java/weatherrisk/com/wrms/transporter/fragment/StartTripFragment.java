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
import weatherrisk.com.wrms.transporter.adapter.AcceptedOrderAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.transporter.CustomerConfirmOrderFragment;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartTripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VEHICLE_LIST = "vehicleList";
    private static final String ORDERS_LIST = "orders_list";

    public static final String FRAGMENT_TAG = "Start Trip";

    private OnFragmentInteractionListener mListener;

    public StartTripFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.new_trip, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {

            final FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, SingleChoiceVehicleListFragment.newInstance(AddTripFragment.FRAGMENT_TAG, vehicleList), FRAGMENT_TAG);
//            ft.addToBackStack(FRAGMENT_TAG);
            ft.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param vehicleList Parameter 1.
     * @return A new instance of fragment StartTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartTripFragment newInstance(ArrayList<VehicleData> vehicleList){
    StartTripFragment fragment = new StartTripFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);
        }
    }

    ArrayList<VehicleData> vehicleList = new ArrayList<>();
    ListView listView;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ArrayList<OrderData> pendingOrderDataArray = new ArrayList<>();
    HashMap<String, ArrayList<InvoiceData>> invoices = new HashMap<>();
    HashMap<String, ArrayList<MaterialTypeData>> materials = new HashMap<>();
    AcceptedOrderAdapter adapter;
    DBAdapter db;
    TextView tagTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_manage_trip, container, false);

        listView = (ListView)view.findViewById(R.id.tripListView);
        tagTextView = (TextView)view.findViewById(R.id.TAG);
        tagTextView.setText("ORDERS");
        return view;
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

        /*Cursor vehicleCursor = db.getVehicle();
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
        vehicleCursor.close();*/


        //Remove last comma from string
        vehicleString = vehicleString.substring(0, vehicleString.length()-1);



        /*listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                OrderData pendingOrderData = pendingOrderDataArray.get(position);
                if(pendingOrderData!=null){
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
                    SingleChoiceVehicleListFragment fragment = SingleChoiceVehicleListFragment.newInstance(FRAGMENT_TAG,vehicleList);
                    fragment.setInvoiceArray(invoiceDatas);
                    fragment.setMaterialArray(materialTypeDatas);
                    fragment.setPendingOrderData(pendingOrderData);
                    ft.replace(R.id.frame_container,fragment , FRAGMENT_TAG);
                    ft.commit();
                }
            }
        });
*/

        acceptedOrderRequest();
    }


    @Override
    public void onStop() {
        super.onStop();
        db.close();
    }

    private void acceptedOrderRequest() {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ACCEPTED_CUSTOMER_ORDER_API,
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
//                                            data.setCustomerName(jObject.getString("CustomerName"));
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
//                                                    invoiceData.setDate(invoiceJsonObject.getString("InvoiceDate"));
                                                    invoiceData.setInvoiceAmount(invoiceJsonObject.getString("Amount"));
                                                    invoiceData.setInvoiceTempId(invoiceJsonObject.getString("InvoiceId"));
//                                                    invoiceData.setInvoiceNumber(invoiceJsonObject.getString("InvoiceNumber"));
                                                    invoiceData.setInvoicePath(invoiceJsonObject.getString("ImageUrl"));
                                                    invoiceData.setSno(jObject.getString("DocketNo"));

                                                    JSONArray materialArray = invoiceJsonObject.getJSONArray("Materials");
                                                    if (materialArray.length() > 0) {
                                                        ArrayList<MaterialTypeData> materialTypeArray = new ArrayList<>();
                                                        for (int k = 0; k < materialArray.length(); k++) {
                                                            JSONObject materialJsonObject = materialArray.getJSONObject(k);
                                                            MaterialTypeData materialTypeData = new MaterialTypeData();
                                                            materialTypeData.setMaterialTypeId(materialJsonObject.getString("MaterialTypeId"));
                                                            materialTypeData.setAmount(materialJsonObject.getString("MaterialName"));
                                                            materialTypeData.setMaterialTypeName(materialJsonObject.getString("MaterialAmount"));
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
                                        if(adapter==null) {
                                            adapter = new AcceptedOrderAdapter(getActivity(), pendingOrderDataArray,vehicleList,invoices,materials);
                                            listView.setAdapter(adapter);
                                        }else {
                                            adapter.notifyDataSetChanged();
                                        }

                                    } else {
                                        Toast.makeText(getActivity(), "No Transporter Found in this Account", Toast.LENGTH_LONG).show();
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
        dialog = ProgressDialog.show(getActivity(), "Start Trip",
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
