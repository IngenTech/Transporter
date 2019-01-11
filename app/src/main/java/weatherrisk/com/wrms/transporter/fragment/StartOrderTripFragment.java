package weatherrisk.com.wrms.transporter.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartOrderTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartOrderTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartOrderTripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PENDING_ORDER_DATA = "pending_order_data";
    private static final String INVOICE_ARRAY = "invoice_array";
    private static final String MATERIAL_ARRAY = "material_array";
    private static final String VEHICLE_DATA = "vehicle_data";

    public static final String FRAGMENT_TAG = "StartOrderTripFragment";

    private OrderData pendingOrderData;
    private ArrayList<InvoiceData> invoiceArray;
    private ArrayList<MaterialTypeData> materialArray;
    private VehicleData vehicleData;

    private OnFragmentInteractionListener mListener;

    public StartOrderTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pendingOrderData Parameter 1.
     * @param invoiceArray Parameter 2.
     * @return A new instance of fragment StartOrderTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartOrderTripFragment newInstance(OrderData pendingOrderData,
                                                     ArrayList<InvoiceData> invoiceArray,
                                                     ArrayList<MaterialTypeData> materialArray,
                                                     VehicleData vehicleData) {
        StartOrderTripFragment fragment = new StartOrderTripFragment();
        Bundle args = new Bundle();
        args.putParcelable(PENDING_ORDER_DATA, pendingOrderData);
        args.putParcelableArrayList(INVOICE_ARRAY, invoiceArray);
        args.putParcelableArrayList(MATERIAL_ARRAY, materialArray);
        args.putParcelable(VEHICLE_DATA, vehicleData);
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
            vehicleData = getArguments().getParcelable(VEHICLE_DATA);
        }
    }
    TextView customerName;
    TextView fromLocation;
    TextView toLocation;
    TextView vehicleCapacity;
    TextView noOfVehicle;
    TextView doorStatus;
    TextView refrigeratorStatus;
    TextView dispatchDate;
    LinearLayout invoiceContainer;
    LayoutInflater innerInflater;

    EditText arrivalDate;
    EditText driverName;
    EditText driverMobile;
    EditText remarEdt;

    Button startTrip;


    SharedPreferences prefs;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_start_ordered_trip, container, false);

        View view = inflater.inflate(R.layout.fragment_start_ordered_trip, container, false);

        customerName = (TextView) view.findViewById(R.id.customerName);
        fromLocation = (TextView) view.findViewById(R.id.fromLocation);
        toLocation = (TextView) view.findViewById(R.id.toLocation);
        vehicleCapacity = (TextView) view.findViewById(R.id.vehicleCapacity);
        noOfVehicle = (TextView) view.findViewById(R.id.noOfVehicle);
        doorStatus = (TextView) view.findViewById(R.id.doorStatus);
        refrigeratorStatus = (TextView) view.findViewById(R.id.refrigeratorStatus);
        dispatchDate = (TextView)view.findViewById(R.id.dispatchDate);
        invoiceContainer = (LinearLayout) view.findViewById(R.id.invoiceContainer);
        startTrip = (Button) view.findViewById(R.id.startTrip);
        innerInflater = LayoutInflater.from(getActivity());

        arrivalDate = (EditText) view.findViewById(R.id.arrivalDate);
        driverName = (EditText) view.findViewById(R.id.driverName);
        driverMobile = (EditText)view.findViewById(R.id.driverMobile);
        remarEdt = (EditText)view.findViewById(R.id.remark);


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
            String dispatchDateString = "Dispatch Date : "+pendingOrderData.getOrderDate();
            dispatchDate.setText(dispatchDateString);

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
            }
        }

        arrivalDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = TRIP_DATE_FORMAT.format(date);
                        arrivalDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String docketNo = pendingOrderData.getOrderId();
                String dispatchDate = pendingOrderData.getOrderDate();
                String driverNameString = driverName.getText().toString();
                String driverMobileNoString = driverMobile.getText().toString();
                String remarkString = remarEdt.getText().toString();
                String arrivalDateString = arrivalDate.getText().toString();

                if(arrivalDateString==null || arrivalDateString.trim().length()<=0){
                    Toast.makeText(getActivity(),"Please Select Arrival Date",Toast.LENGTH_SHORT).show();
                    return;
                }

                orderToTripRequest(docketNo,dispatchDate,arrivalDateString,driverNameString,driverMobileNoString,remarkString);
            }
        });

    }

    private static String TRIP_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static SimpleDateFormat TRIP_DATE_FORMAT = new SimpleDateFormat(TRIP_DATE_FORMAT_STRING);
    private void orderToTripRequest(final String docketNo, final String dispatchData,final String arrivalDate,final String driverName,final String driverContactNo,final String remark) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ORDER_TO_TRIP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {
                            System.out.println("Add Trip Response : " + trackResponse);
                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    new AlertDialog.Builder(getActivity())
                                            .setMessage("Trip has been added successfully")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    getActivity().getFragmentManager().popBackStack();
                                                    onButtonPressed(FRAGMENT_TAG);
                                                }
                                            })
                                            .show();

                                } else {
                                    Toast.makeText(getActivity(), "Request has been denied by server", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                map.put("api_key", apiKey);
                map.put("account_id", accountId);

                map.put("DocketNo", docketNo);
                map.put("VehicleId", vehicleData.getVehicleId());
                map.put("DispatchDate", dispatchData);
                map.put("ArrivalDate", arrivalDate);
                map.put("Remarks", remark);
                map.put("DriverName", driverName);
                map.put("DriverMobileNo", driverContactNo);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        progressDialog = ProgressDialog.show(getActivity(), "Adding Trip",
                "Please wait .....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String title) {
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
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
}
