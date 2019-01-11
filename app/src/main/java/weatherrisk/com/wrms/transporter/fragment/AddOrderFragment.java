package weatherrisk.com.wrms.transporter.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Base64;
import weatherrisk.com.wrms.transporter.dataobject.CustomerOrderData;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitData;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TRANSPORTER_DATA = "transporter_data";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private TransporterData transporterData;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param transporterData Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddOrderFragment newInstance(TransporterData transporterData, String param2) {
        AddOrderFragment fragment = new AddOrderFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRANSPORTER_DATA, transporterData);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transporterData = getArguments().getParcelable(TRANSPORTER_DATA);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    EditText fromCity;
    EditText fromAddress;
    EditText toCity;
    EditText toAddress;
    EditText transporterName;
    EditText dispatchDate;
    EditText materialTypeName;
    EditText item;
    EditText doorStatusRefrigerated;
    Button addInvoice;
    LinearLayout confirmInvoice;
    LinearLayout addInvoiceLayout;
    TextView invoiceInstruction;
    EditText invoiceAmount;
    EditText invoiceNumber;
    EditText invoiceDate;
    EditText remark;
    ImageView invoiceView;
    ImageButton uploadInvoice;
    Button upload;
    LinearLayout confirmRoadPermit;
    LinearLayout roadPermitLayout;
    TextView roadPermitInstruction;
    EditText roadPermitRemark;
    Button uploadRoadPermit;
    ImageView roadPermitView;
    ImageButton uploadRoadPermitImage;
    LinearLayout materialContainer;
    Spinner materialType;
    Spinner invoiceId;
    EditText materialRemark;
    EditText materialAmount;
    Button addMaterial;
    Button submitOrder;

    ProgressDialog progressDialog;
    SharedPreferences prefs;

    DBAdapter db;
    Cursor stateListCursor;

    CustomerOrderData customerOrderData;

    public static final int BROWSE_INVOICE = 101;
    public static final int BROWSE_ROAD_PERMIT = 102;

    private static String ORDER_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat ORDER_DATE_FORMAT = new SimpleDateFormat(ORDER_DATE_FORMAT_STRING);
    ArrayList<InvoiceData> invoiceArray = new ArrayList<>();
    InvoiceData invoiceData;

    ArrayList<RoadPermitData> roadPermitArray = new ArrayList<>();
    RoadPermitData roadPermitData;

    ArrayList<MaterialTypeData> materialArray = new ArrayList<>();
    MaterialTypeData materialTypeData;

    LayoutInflater innerInflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_order, container, false);

        innerInflater = LayoutInflater.from(getActivity());

        fromCity = (EditText) view.findViewById(R.id.fromCity);
        fromAddress = (EditText) view.findViewById(R.id.fromAddress);
        toCity = (EditText) view.findViewById(R.id.toCity);
        toAddress = (EditText) view.findViewById(R.id.toAddress);
        transporterName = (EditText) view.findViewById(R.id.transporterName);
        dispatchDate = (EditText) view.findViewById(R.id.dispatchDate);
        materialTypeName = (EditText) view.findViewById(R.id.materialTypeName);
        item = (EditText) view.findViewById(R.id.item);
        doorStatusRefrigerated = (EditText) view.findViewById(R.id.doorStatusRefrigerated);
        addInvoice = (Button) view.findViewById(R.id.addInvoice);
        confirmInvoice = (LinearLayout) view.findViewById(R.id.confirmInvoice);
        addInvoiceLayout = (LinearLayout) view.findViewById(R.id.addInvoiceLayout);
        invoiceInstruction = (TextView) view.findViewById(R.id.invoiceInstruction);
        invoiceAmount = (EditText) view.findViewById(R.id.invoiceAmount);
        invoiceNumber = (EditText) view.findViewById(R.id.invoiceNumber);
        invoiceDate = (EditText) view.findViewById(R.id.invoiceDate);
        remark = (EditText) view.findViewById(R.id.remark);
        invoiceView = (ImageView) view.findViewById(R.id.invoiceView);
        uploadInvoice = (ImageButton) view.findViewById(R.id.uploadInvoice);
        upload = (Button) view.findViewById(R.id.upload);
        confirmRoadPermit = (LinearLayout) view.findViewById(R.id.confirmRoadPermit);
        roadPermitLayout = (LinearLayout) view.findViewById(R.id.roadPermitLayout);
        roadPermitInstruction = (TextView) view.findViewById(R.id.roadPermitInstruction);
        roadPermitRemark = (EditText) view.findViewById(R.id.roadPermitRemark);
        uploadRoadPermit = (Button) view.findViewById(R.id.uploadRoadPermit);
        roadPermitView = (ImageView) view.findViewById(R.id.roadPermitView);
        uploadRoadPermitImage = (ImageButton) view.findViewById(R.id.uploadRoadPermitImage);
        materialContainer = (LinearLayout) view.findViewById(R.id.materialContainer);
        materialType = (Spinner) view.findViewById(R.id.materialType);
        invoiceId = (Spinner) view.findViewById(R.id.invoiceId);
        materialRemark = (EditText) view.findViewById(R.id.materialRemark);
        materialAmount = (EditText) view.findViewById(R.id.materialAmount);
        addMaterial = (Button) view.findViewById(R.id.addMaterial);
        submitOrder = (Button) view.findViewById(R.id.submitOrder);

        materialTypeData = new MaterialTypeData();
        customerOrderData = new CustomerOrderData();
        db = new DBAdapter(getActivity());
        db.open();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        invoiceData = new InvoiceData();
        roadPermitData = new RoadPermitData();
        String currentDate = ORDER_DATE_FORMAT.format(new Date());
        invoiceDate.setText(currentDate);

        if (stateListCursor == null) {
            stateListCursor = db.stateList();
        }


        dispatchDate.setOnClickListener(new View.OnClickListener() {

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
                        String selectedDate = ORDER_DATE_FORMAT.format(date);
                        dispatchDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        uploadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
//                    getParentFragment().startActivityForResult(i, BROWSE_INVOICE);
                    startActivityForResult(i, BROWSE_INVOICE);
                } else {
                    Toast.makeText(getActivity(), "Do not found app for this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadRoadPermitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
//                    getParentFragment().startActivityForResult(i, BROWSE_ROAD_PERMIT);
                    startActivityForResult(i, BROWSE_ROAD_PERMIT);
                } else {
                    Toast.makeText(getActivity(), "Do not found app for this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        invoiceDate.setOnClickListener(new View.OnClickListener() {

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
                        String selectedDate = ORDER_DATE_FORMAT.format(date);
                        invoiceDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidInvoice()) {
                    upLoadInvoice();
                }
            }
        });

        uploadRoadPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidRoadPermit()) {
                    upLoadRoadPermit();
                }
            }
        });


        final Cursor materialList = db.materialList();

        final ArrayList<String> materialNameArray = new ArrayList<>();
        if (materialList.moveToFirst()) {
            do {
                String materialString = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialNameArray.add(materialString);
            } while (materialList.moveToNext());
        }
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, materialNameArray);

        materialType.setAdapter(material_adapter);
        materialType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                materialList.moveToPosition(pos);
                String materialId = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_ID));
                String materialName = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialTypeData.setMaterialTypeName(materialName);
                materialTypeData.setMaterialTypeId(materialId);
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        addMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidMaterial()) {

                    final String materialEntryId = String.valueOf(System.currentTimeMillis());
                    materialTypeData.setId(materialEntryId);

                    materialTypeData.setRemark(materialRemark.getText().toString());
                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(materialAmount.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    materialTypeData.setAmount(String.valueOf(amount));

                    materialArray.add(materialTypeData);

                    Toast.makeText(getActivity(), "Material Uploaded", Toast.LENGTH_SHORT).show();

//                    sno, materialTypeId, materialAmount, remove

                    final View confirmedMaterial = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                    TextView sn1 = (TextView) confirmedMaterial.findViewById(R.id.sno);
                    sn1.setText(materialTypeData.getInvoiceId());
                    TextView materialTypeId = (TextView) confirmedMaterial.findViewById(R.id.materialTypeId);
                    materialTypeId.setText(materialTypeData.getMaterialTypeName());
                    TextView materialAmount1 = (TextView) confirmedMaterial.findViewById(R.id.materialAmount1);
                    System.out.println("Material Amount :" + String.valueOf(materialTypeData.getAmount()));
                    materialAmount1.setText(String.valueOf(materialTypeData.getAmount()));

                    ImageButton remove = (ImageButton) confirmedMaterial.findViewById(R.id.remove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ViewGroup) confirmedMaterial.getParent()).removeView(confirmedMaterial);
                            for (MaterialTypeData mData : materialArray) {
                                if (mData.getId().equals(materialEntryId)) {
                                    materialArray.remove(mData);
                                    break;
                                }
                            }

                            for (MaterialTypeData mData : materialArray) {
                                System.out.println("Material " + mData.getId() + " , " + mData.getMaterialTypeId() + " , " + mData.getAmount());
                            }


                        }
                    });

                    materialContainer.addView(confirmedMaterial);

                    materialType.setSelection(0);
                    invoiceId.setSelection(0);
                    materialRemark.setText("");
                    materialAmount.setText("");

                    double totalAmount = 0.0;
                    for (MaterialTypeData mData : materialArray) {
                        if (mData.getInvoiceId().equals(materialTypeData.getInvoiceId())) {
                            double tempAmount = Double.parseDouble(mData.getAmount());
                            totalAmount = tempAmount + totalAmount;
                        }
                    }

                    for (InvoiceData iData : invoiceArray) {
                        if (iData.getInvoiceNumber().equals(materialTypeData.getInvoiceId())) {
                            double invoiceAmount = 0.0;
                            try {
                                invoiceAmount = Double.parseDouble(iData.getInvoiceAmount());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!(invoiceAmount == totalAmount)) {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage("Invoice Amount is " + invoiceAmount + " and Aaterial Amount is " + totalAmount)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                            }

                            break;
                        }
                    }

                    materialTypeData = new MaterialTypeData();
                }
            }
        });


        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInvoiceMaterialJson();
                /*if (isValidTrip()) {
                    String invoiceJsonData = getInvoiceMaterialJson();
                    String roadPermitTempIds = "";
                    for (RoadPermitData rdData : roadPermitArray) {
                        roadPermitTempIds = roadPermitTempIds + rdData.getRoadPermitReturnId() + ",";
                    }

                    //remove last comma from string
                    roadPermitTempIds = roadPermitTempIds.substring(0, roadPermitTempIds.length() - 1);
                    addVehicleTrip(invoiceJsonData, roadPermitTempIds);
                }*/
            }
        });

        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInvoiceLayout.setVisibility(View.VISIBLE);
                addInvoice.setVisibility(View.GONE);
            }
        });

        if(transporterData!=null){
            setTransporterData(transporterData);
        }

    }

    private String getInvoiceMaterialJson(){
        String jsonString = null;

        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            for (InvoiceData invoiceData : invoiceArray) {
                JSONObject jObject = new JSONObject();
                jObject.put("invoice_id", invoiceData.getInvoiceTempId());

                JSONArray materialJsonArray = new JSONArray();
                for(MaterialTypeData materialData : materialArray){
                    if(materialData.getInvoiceId().equals(invoiceData.getInvoiceNumber())) {
                        JSONObject materialJsonObject = new JSONObject();
                        materialJsonObject.put("material_type_id", materialData.getMaterialTypeId());
                        materialJsonObject.put("material_name", materialData.getMaterialTypeName());
                        materialJsonObject.put("amount", materialData.getAmount());
                        materialJsonArray.put(materialJsonObject);
                    }

                }
                jObject.put("materials",materialJsonArray);


                jsonArray.put(jObject);
            }
            jsonObject.put("invoices", jsonArray);
            jsonString = jsonObject.toString();
            System.out.println("jsonString : " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }


    private void setTransporterData(TransporterData transporterData) {
        fromCity.setText(transporterData.getFromCityName()+" , "+transporterData.getFromStateName());
        toCity.setText(transporterData.getToCityName()+" , "+transporterData.getToStateName());
        transporterName.setText(transporterData.getFirmName());
        if(transporterData.getMaterialId()!=null && transporterData.getMaterialId().trim().length()>0) {
            Cursor materialCursor = db.materialById(transporterData.getMaterialId());
            if(materialCursor.moveToFirst()){
                materialTypeName.setText(materialCursor.getString(materialCursor.getColumnIndex(DBAdapter.MATERIAL_NAME)));
            }
        }
        String doorStatusRefrigeratedString = transporterData.getDoorClosed()+" / "+transporterData.getRefrigerated();
        doorStatusRefrigerated.setText(doorStatusRefrigeratedString);
        fromCity.setEnabled(false);
        toCity.setEnabled(false);
        transporterName.setEnabled(false);
        materialTypeName.setEnabled(false);
        doorStatusRefrigerated.setEnabled(false);

    }


    boolean isValidInvoice() {
        boolean isValid = true;

        if (invoiceAmount.getText() != null && invoiceAmount.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceAmount(invoiceAmount.getText().toString().trim());
        } else {
            Toast.makeText(getActivity(), "Please enter invoice amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (invoiceNumber.getText() != null && invoiceNumber.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceNumber(invoiceNumber.getText().toString().trim());
        } else {
            Toast.makeText(getActivity(), "Please enter invoice number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (invoiceDate.getText() != null && invoiceDate.getText().toString().trim().length() > 0) {
            invoiceData.setDate(invoiceDate.getText().toString().trim());
        } else {
            Toast.makeText(getActivity(), "Please enter invoice date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (remark.getText() != null && remark.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceRemark(remark.getText().toString().trim());
        }

        if (!(invoiceData.getInvoicePath() != null && invoiceData.getInvoicePath().length() > 0)) {
            Toast.makeText(getActivity(), "Please select invoice image", Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    boolean isValidRoadPermit() {
        boolean isValid = true;

        if (roadPermitRemark.getText() != null && roadPermitRemark.getText().toString().trim().length() > 0) {
            roadPermitData.setRoadPermitRemark(roadPermitRemark.getText().toString().trim());
        } else {
            Toast.makeText(getActivity(), "Please enter road permit remark", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(roadPermitData.getRoadPermitImageString() != null && roadPermitData.getRoadPermitImageString().length() > 0)) {
            Toast.makeText(getActivity(), "Please select road permit image", Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    private void upLoadRoadPermit() {

        StringRequest stringRoadPermitRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_ROAD_PERMIT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String roadPermitResponse) {
                        try {
                            System.out.println("Road Permit Response : " + roadPermitResponse);
//                            Add Trip Response : {"result":"success","invoice_temporary_id":1}
                            JSONObject jsonObject = new JSONObject(roadPermitResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    Toast.makeText(getActivity(), "Road Permit Uploaded", Toast.LENGTH_SHORT).show();

                                    String roadPermitTemporaryId = jsonObject.getString("road_permit_temporary_id");
                                    roadPermitData.setRoadPermitReturnId(roadPermitTemporaryId);
                                    String roadPermitIds = customerOrderData.getRoadPermitTempIds();
                                    if (roadPermitIds != null) {
                                        customerOrderData.setRoadPermitTempIds(roadPermitIds + roadPermitTemporaryId + ",");
                                    } else {
                                        customerOrderData.setRoadPermitTempIds(roadPermitTemporaryId + ",");
                                    }

                                    if (roadPermitArray.size() == 0) {
                                        View confirmRoadPermitView = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn1 = (TextView) confirmRoadPermitView.findViewById(R.id.sno);
                                        sn1.setText("Road Permit Remark");
                                        TextView invoiceNo1 = (TextView) confirmRoadPermitView.findViewById(R.id.invoiceNo);
                                        invoiceNo1.setText("Temporary Id");
                                        TextView invoiceDate1 = (TextView) confirmRoadPermitView.findViewById(R.id.invoiceDate);
                                        invoiceDate1.setText("Invoice Date");
                                        invoiceDate1.setVisibility(View.GONE);
                                        TextView amount1 = (TextView) confirmRoadPermitView.findViewById(R.id.amount);
                                        amount1.setText("Amount");
                                        amount1.setVisibility(View.GONE);
                                        confirmRoadPermit.addView(confirmRoadPermitView);
                                    }

                                    View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                    TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                    sn1.setText(roadPermitData.getRoadPermitRemark());
                                    TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                    invoiceNo1.setText(roadPermitData.getRoadPermitReturnId());

                                    TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                    invoiceDate1.setVisibility(View.GONE);
                                    TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                    amount1.setVisibility(View.GONE);

                                    confirmRoadPermit.addView(confirmedInvoice1);


                                    roadPermitArray.add(roadPermitData);
                                    roadPermitData = new RoadPermitData();
                                    roadPermitRemark.setText("");
                                    roadPermitView.setImageDrawable(null);
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

                map.put("road_permit_image_base64", roadPermitData.getRoadPermitImageString());
                map.put("remark", roadPermitData.getRoadPermitRemark());

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringRoadPermitRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Track Data.....", true);
        AppController.getInstance().addToRequestQueue(stringRoadPermitRequest);

    }

    private void upLoadInvoice() {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_TRIP_INVOICE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {
                            System.out.println("Add Trip Response : " + trackResponse);
//                            Add Trip Response : {"result":"success","invoice_temporary_id":1}
                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    Toast.makeText(getActivity(), "Invoice Uploaded", Toast.LENGTH_SHORT).show();

                                    String invoiceTemporaryId = jsonObject.getString("invoice_temporary_id");
                                    String tripDataInvoiceIds = customerOrderData.getInvoiceIds();
                                    invoiceData.setInvoiceTempId(invoiceTemporaryId);
                                    if (tripDataInvoiceIds != null) {
                                        customerOrderData.setInvoiceIds(tripDataInvoiceIds + invoiceTemporaryId + ",");
                                    } else {
                                        customerOrderData.setInvoiceIds(invoiceTemporaryId + ",");
                                    }

                                    if (invoiceArray.size() == 0) {
                                        View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                        sn1.setText("SNo.");
                                        TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                        invoiceNo1.setText("Invoice No.");
                                        TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                        invoiceDate1.setText("Invoice Date");
                                        TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                        amount1.setText("Amount");
                                        confirmInvoice.addView(confirmedInvoice1);
                                    }

                                    View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                    TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                    sn1.setText(String.valueOf(invoiceArray.size() + 1));
                                    TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                    invoiceNo1.setText(invoiceData.getInvoiceNumber());
                                    TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                    invoiceDate1.setText(invoiceData.getDate());
                                    TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                    amount1.setText(invoiceData.getInvoiceAmount());
                                    confirmInvoice.addView(confirmedInvoice1);

                                    invoiceArray.add(invoiceData);
                                    invoiceData = new InvoiceData();
                                    invoiceAmount.setText("");
                                    invoiceNumber.setText("");
                                    invoiceDate.setText("");
                                    remark.setText("");
                                    invoiceView.setImageDrawable(null);
                                    InvoiceSpinnerAdapter invoiceSpinnerAdapter = new InvoiceSpinnerAdapter(invoiceArray);

                                    invoiceId.setAdapter(invoiceSpinnerAdapter);
                                    // onClickListener:
                                    invoiceId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> parent,
                                                                   View view, int pos, long id) {
                                            InvoiceData invoiceInnerdata = (InvoiceData) parent.getItemAtPosition(pos);
                                            materialTypeData.setInvoiceId(invoiceInnerdata.getInvoiceNumber());
                                            Toast.makeText(
                                                    getActivity(),
                                                    invoiceInnerdata.getInvoiceNumber() + " is of " + invoiceInnerdata.getInvoiceAmount() + " amount.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }

                                        public void onNothingSelected(AdapterView parent) {
                                            // Do nothing.
                                        }
                                    });


                                    addInvoice.setVisibility(View.VISIBLE);
                                    addInvoiceLayout.setVisibility(View.GONE);


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

                map.put("invoice_amount", invoiceData.getInvoiceAmount());
                map.put("invoice_number", invoiceData.getInvoiceNumber());
                map.put("invoice_date", invoiceData.getDate());
                map.put("invoice_image_base64", invoiceData.getInvoicePath());
                map.put("remark", invoiceData.getInvoiceRemark());

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(getActivity(), "",
                "Fetching Track Data.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String imageString = Base64.encodeBytes(ba);
            if (requestCode == BROWSE_INVOICE) {
                if (invoiceView != null) {
                    invoiceView.setImageBitmap(bitmap);
                }
                invoiceData.setInvoicePath(imageString);
            } else {
                if (roadPermitView != null) {
                    roadPermitView.setImageBitmap(bitmap);
                }
                roadPermitData.setRoadPermitImageString(imageString);
            }


        }
    }

    boolean isValidMaterial() {
        boolean isValid = true;

        if (!(materialTypeData.getMaterialTypeId() != null && materialTypeData.getMaterialTypeId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select material type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(materialTypeData.getInvoiceId() != null && materialTypeData.getInvoiceId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select invoice for material", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (materialAmount.getText() != null && materialAmount.getText().toString().trim().length() > 0) {
            double amount = 0.0;
            try {
                amount = Double.parseDouble(materialAmount.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(), "Please enter ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isValid;
    }


    /**
     * This is your own Adapter implementation which displays
     * the ArrayList of "Guy"-Objects.
     */
    private class InvoiceSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        /**
         * The internal data (the ArrayList with the Objects).
         */
        private final List<InvoiceData> data;

        public InvoiceSpinnerAdapter(List<InvoiceData> data) {
            this.data = data;
        }

        /**
         * Returns the Size of the ArrayList
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * Returns one Element of the ArrayList
         * at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Returns the View that is shown when a element was
         * selected.
         */
        @Override
        public View getView(int position, View recycle, ViewGroup parent) {
            TextView text;
            if (recycle != null) {
                // Re-use the recycled view here!
                text = (TextView) recycle;
            } else {
                // No recycled view, inflate the "original" from the platform:
                text = (TextView) getActivity().getLayoutInflater().inflate(
                        android.R.layout.simple_dropdown_item_1line, parent, false
                );
            }
            text.setTextColor(Color.BLACK);
            text.setText(data.get(position).getInvoiceNumber());
            return text;
        }


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
