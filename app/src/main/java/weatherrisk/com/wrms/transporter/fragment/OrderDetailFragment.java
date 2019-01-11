package weatherrisk.com.wrms.transporter.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PENDING_ORDER_DATA = "pending_order_data";
    private static final String INVOICE_ARRAY = "invoice_array";
    private static final String MATERIAL_ARRAY = "material_array";


    private static final int ACCEPT_STATUS = 2;
    private static final int REJECT_STATUS = 3;

    // TODO: Rename and change types of parameters
    private OrderData pendingOrderData;
    private ArrayList<InvoiceData> invoiceArray;
    private ArrayList<MaterialTypeData> materialArray;

    private OnFragmentInteractionListener mListener;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pendingOrderData Parameter 1.
     * @param invoiceArray     Parameter 2.
     * @param materialArray    Parameter 3.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(OrderData pendingOrderData,
                                                  ArrayList<InvoiceData> invoiceArray,
                                                  ArrayList<MaterialTypeData> materialArray) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(PENDING_ORDER_DATA, pendingOrderData);
        args.putParcelableArrayList(INVOICE_ARRAY, invoiceArray);
        args.putParcelableArrayList(MATERIAL_ARRAY, materialArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pendingOrderData = getArguments().getParcelable(PENDING_ORDER_DATA);
            invoiceArray = getArguments().getParcelableArrayList(INVOICE_ARRAY);
            materialArray = getArguments().getParcelableArrayList(MATERIAL_ARRAY);
        }
    }

    TextView customerName;
    TextView fromLocation;
    TextView toLocation;
    TextView vehicleCapacity;
    TextView noOfVehicle;
    //    TextView productCotegory;
    TextView doorStatus;
    TextView refrigeratorStatus;
    /*ExpandableListView invoiceListView;
    InvoiceExpendableListAdapter invoiceAdapter;*/
    LinearLayout invoiceContainer;
    LayoutInflater innerInflater;

    Button accept;
    Button reject;

    SharedPreferences prefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        customerName = (TextView) view.findViewById(R.id.customerName);
        fromLocation = (TextView) view.findViewById(R.id.fromLocation);
        toLocation = (TextView) view.findViewById(R.id.toLocation);
        vehicleCapacity = (TextView) view.findViewById(R.id.vehicleCapacity);
        noOfVehicle = (TextView) view.findViewById(R.id.noOfVehicle);
//        productCotegory = (TextView) view.findViewById(R.id.productCotegory);
        doorStatus = (TextView) view.findViewById(R.id.doorStatus);
        refrigeratorStatus = (TextView) view.findViewById(R.id.refrigeratorStatus);
//        invoiceListView = (ExpandableListView)view.findViewById((R.id.invoiceListView));
        invoiceContainer = (LinearLayout) view.findViewById(R.id.invoiceContainer);
        accept = (Button) view.findViewById(R.id.accept);
        reject = (Button) view.findViewById(R.id.reject);
        innerInflater = LayoutInflater.from(getActivity());

        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        if (pendingOrderData != null) {
            customerName.setText(pendingOrderData.getCustomerName());
            String fromLocationString = "Source : " + pendingOrderData.getFromAddress() + "," + pendingOrderData.getFromCityName();
            fromLocation.setText(fromLocationString);
            String destinationLocationString = "Destination : " + pendingOrderData.getToAddress() + "," + pendingOrderData.getToCityName();
            toLocation.setText(destinationLocationString);
            String vehicleCapacityString = "Vehicle Capacity : " + pendingOrderData.getCapacity() + " tones";
            vehicleCapacity.setText(vehicleCapacityString);
            String noOfVehicleString = "Number Of Vehicle : " + pendingOrderData.getVehicleQuantity();
            noOfVehicle.setText(noOfVehicleString);
            /*String categoryString = "Category : "+pendingOrderData.getItem()+" , "+pendingOrderData.getMaterialTypeId();
            productCotegory.setText(categoryString);*/
            String doorStatusString = "Door Status : " + "Open";
            if (pendingOrderData.getDoorStatus().equals("1")) {
                doorStatusString = "Door Status : " + "Closed";
            }
            doorStatus.setText(doorStatusString);
            String refrigeratedString = "Refrigerated : " + "No";
            if (pendingOrderData.getRefrigerated().equals("1")) {
                refrigeratedString = "Refrigerated : " + "Yes";
            }
            refrigeratorStatus.setText(refrigeratedString);
        }

        final View invoicwViewH = innerInflater.inflate(R.layout.expendable_invoice_group_item, null, false);
        TextView txtInvoiceNumberH = (TextView) invoicwViewH
                .findViewById(R.id.invoiceNumber);
        TextView txtInvoiceAmountH = (TextView) invoicwViewH
                .findViewById(R.id.invoiceAmount);
        TextView txtInvoiceDateH = (TextView) invoicwViewH
                .findViewById(R.id.invoiceDate);
        ImageView txtInvoiceViewH = (ImageView) invoicwViewH
                .findViewById(R.id.invoiceView);

        txtInvoiceNumberH.setText("Invoice Number");
        txtInvoiceNumberH.setTypeface(null, Typeface.BOLD);
        txtInvoiceAmountH.setText("Amount");
        txtInvoiceAmountH.setTypeface(null, Typeface.BOLD);
        txtInvoiceDateH.setText("Date");
        txtInvoiceDateH.setTypeface(null, Typeface.BOLD);
        txtInvoiceViewH.setVisibility(View.GONE);


        invoiceContainer.addView(invoicwViewH);

        if (invoiceArray != null) {
            for (InvoiceData data : invoiceArray) {
//            _listDataHeader.add(data.getInvoiceTempId());

                final View invoicwView = innerInflater.inflate(R.layout.expendable_invoice_group_item, null, false);
                TextView txtInvoiceNumber = (TextView) invoicwView
                        .findViewById(R.id.invoiceNumber);
                TextView txtInvoiceAmount = (TextView) invoicwView
                        .findViewById(R.id.invoiceAmount);
                TextView txtInvoiceDate = (TextView) invoicwView
                        .findViewById(R.id.invoiceDate);
                ImageView txtInvoiceView = (ImageView) invoicwView
                        .findViewById(R.id.invoiceView);

                if (data != null) {
                    txtInvoiceNumber.setText(data.getInvoiceNumber());
                    txtInvoiceAmount.setText(data.getInvoiceAmount());
                    txtInvoiceDate.setText(data.getDate());
                }

                invoiceContainer.addView(invoicwView);

                View convertViewH = innerInflater.inflate(R.layout.material_report_list_item, null);
                TextView txtMaterialNameH = (TextView) convertViewH
                        .findViewById(R.id.materialTypeName);
                TextView txtMaterialTypeRemarkH = (TextView) convertViewH
                        .findViewById(R.id.materialTypeRemark);
                TextView txtMaterialAmountH = (TextView) convertViewH
                        .findViewById(R.id.materialAmount);
                txtMaterialNameH.setText("Category");
                txtMaterialNameH.setTypeface(null, Typeface.BOLD);
                txtMaterialTypeRemarkH.setText("Remark");
                txtMaterialTypeRemarkH.setTypeface(null, Typeface.BOLD);
                txtMaterialAmountH.setText("Amount");
                txtMaterialAmountH.setTypeface(null, Typeface.BOLD);
                invoiceContainer.addView(convertViewH);

                ArrayList<MaterialTypeData> mArray = new ArrayList<>();
                for (MaterialTypeData mData : materialArray) {
                    System.out.println("mData.getInvoiceId().equals(data.getInvoiceTempId()) : " + mData.getInvoiceId().equals(data.getInvoiceTempId()));
                    if (mData.getInvoiceId().equals(data.getInvoiceTempId())) {

                        View convertView = innerInflater.inflate(R.layout.material_report_list_item, null);
                        TextView txtMaterialName = (TextView) convertView
                                .findViewById(R.id.materialTypeName);
                        TextView txtMaterialTypeRemark = (TextView) convertView
                                .findViewById(R.id.materialTypeRemark);
                        TextView txtMaterialAmount = (TextView) convertView
                                .findViewById(R.id.materialAmount);
                        txtMaterialName.setText(mData.getMaterialTypeName());
                        txtMaterialTypeRemark.setText(mData.getRemark());
                        txtMaterialAmount.setText(mData.getAmount());
                        invoiceContainer.addView(convertView);
                        mArray.add(mData);
                    }
                }
//                _listDataChild.put(data.getInvoiceTempId(), mArray);
            }
        }
        /*invoiceAdapter = new InvoiceExpendableListAdapter(getActivity(),_listDataHeader,_listDataChild,invoiceArray);
        invoiceListView.setAdapter(invoiceAdapter);*/
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingOrderProcessRequest(String.valueOf(ACCEPT_STATUS), pendingOrderData.getOrderId());
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingOrderProcessRequest(String.valueOf(REJECT_STATUS), pendingOrderData.getOrderId());
            }
        });

    }


    ProgressDialog dialog;

    private void pendingOrderProcessRequest(final String status, final String orderId) {

        System.out.println("Pending order process request get called" + orderId);
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.PROCCESS_CUSTOME_ORDER_API + orderId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);
                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {

                                    String message = "accepted";
                                    if(status.equals(String.valueOf(ACCEPT_STATUS))) {
                                        message = "accepted";
                                    }else{
                                        message = "rejected";
                                    }

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Request has been "+message).
                                            setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                    getActivity().getSupportFragmentManager().beginTransaction().remove(OrderDetailFragment.this).commit();
                                                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                    ft.replace(R.id.frame_container, PendingOrderFragment.newInstance("params1", "param2"), "");
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
                dialog.dismiss();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getActivity().getResources().getString(R.string.server_api_key);
                Map<String, String> map = new HashMap<>();
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                map.put("status", status);
                map.put("DocketNo", orderId);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(getActivity(), "",
                "Closing Trip.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

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
