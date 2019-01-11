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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import java.text.ParseException;
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
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitData;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTripFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddTripFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTripFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TRANSPORTER_DATA = "transporter_data";
    private static final String VEHICLE_DATA = "vehicleData";

    public static final String FRAGMENT_TAG = "Add Trip";

    // TODO: Rename and change types of parameters
    private TransporterData transporterData;
    private VehicleData vehicleData;

    private OnFragmentInteractionListener mListener;

    public AddTripFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param transporterData Parameter 1.
     * @param vehicleData Parameter 2.
     * @return A new instance of fragment AddTripFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTripFragment newInstance(TransporterData transporterData, VehicleData vehicleData) {
        AddTripFragment fragment = new AddTripFragment();
        Bundle args = new Bundle();
        args.putParcelable(TRANSPORTER_DATA, transporterData);
        args.putParcelable(VEHICLE_DATA, vehicleData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transporterData = getArguments().getParcelable(TRANSPORTER_DATA);
            vehicleData = getArguments().getParcelable(VEHICLE_DATA);
        }
    }

    public static final int BROWSE_INVOICE = 101;
    public static final int BROWSE_ROAD_PERMIT = 102;

    LinearLayout materialContainer;
    LinearLayout confirmInvoice;
    LayoutInflater innerInflater;
    Spinner fromState;
    Spinner fromCity;
    EditText fromAddress;
    TextInputLayout inputLayoutFromAddress;
    Spinner toState;
    Spinner toCity;
    TextInputLayout inputLayoutToAddress;
    EditText toAddress;
    EditText customerName;
    EditText dispatchDate;
    EditText arrivalDate;
    EditText driverName;
    EditText driverMobile;

    EditText invoiceAmount;
    EditText invoiceNumber;
    EditText invoiceDate;
    EditText remark;
    ImageButton uploadInvoice;
    ImageView invoiceView;
    Button upload;
    Button addMaterial;

    Button addInvoice;
    LinearLayout addInvoiceLayout;
    Button addRoadPermit;
    LinearLayout addRoadPermitLayout;

    TripData tripData;

    Spinner materialType;
    Spinner invoiceId;
    EditText materialRemark;
    EditText materialAmount;

    Button submitTrip;

    LinearLayout confirmRoadPermit;
    EditText roadPermitRemark;
    Button uploadRoadPermit;
    ImageButton uploadRoadPermitImage;
    ImageView roadPermitView;

    DBAdapter db;
    Cursor stateListCursor;

    private static String TRIP_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat TRIP_DATE_FORMAT = new SimpleDateFormat(TRIP_DATE_FORMAT_STRING);
    ArrayList<InvoiceData> invoiceArray = new ArrayList<>();
    InvoiceData invoiceData;

    ArrayList<RoadPermitData> roadPermitArray = new ArrayList<>();
    RoadPermitData roadPermitData;

    ArrayList<MaterialTypeData> materialArray = new ArrayList<>();
    MaterialTypeData materialTypeData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        innerInflater = LayoutInflater.from(getActivity());
        materialContainer = (LinearLayout) view.findViewById(R.id.materialContainer);
        confirmInvoice = (LinearLayout) view.findViewById(R.id.confirmInvoice);

        inputLayoutFromAddress = (TextInputLayout) view.findViewById(R.id.inputLayoutFromAddress);
        inputLayoutToAddress = (TextInputLayout) view.findViewById(R.id.inputLayoutToAddress);

        fromState = (Spinner) view.findViewById(R.id.fromState);
        fromCity = (Spinner) view.findViewById(R.id.fromCity);
        fromAddress = (EditText) view.findViewById(R.id.fromAddress);
        toState = (Spinner) view.findViewById(R.id.toState);
        toCity = (Spinner) view.findViewById(R.id.toCity);
        toAddress = (EditText) view.findViewById(R.id.toAddress);
        customerName = (EditText) view.findViewById(R.id.customerName);
        dispatchDate = (EditText) view.findViewById(R.id.dispatchDate);
        arrivalDate = (EditText) view.findViewById(R.id.arrivalDate);
        driverName = (EditText) view.findViewById(R.id.driverName);
        driverMobile = (EditText) view.findViewById(R.id.driverMobile);

        invoiceAmount = (EditText) view.findViewById(R.id.invoiceAmount);
        invoiceNumber = (EditText) view.findViewById(R.id.invoiceNumber);
        invoiceDate = (EditText) view.findViewById(R.id.invoiceDate);
        remark = (EditText) view.findViewById(R.id.remark);
        uploadInvoice = (ImageButton) view.findViewById(R.id.uploadInvoice);
        invoiceView = (ImageView) view.findViewById(R.id.invoiceView);
        upload = (Button) view.findViewById(R.id.upload);
        addMaterial = (Button) view.findViewById(R.id.addMaterial);
        materialType = (Spinner) view.findViewById(R.id.materialType);
        invoiceId = (Spinner) view.findViewById(R.id.invoiceId);
        materialRemark = (EditText) view.findViewById(R.id.materialRemark);
        materialAmount = (EditText) view.findViewById(R.id.materialAmount);
        submitTrip = (Button) view.findViewById(R.id.submitTrip);

        confirmRoadPermit = (LinearLayout) view.findViewById(R.id.confirmRoadPermit);
        roadPermitRemark = (EditText) view.findViewById(R.id.roadPermitRemark);
        uploadRoadPermit = (Button) view.findViewById(R.id.uploadRoadPermit);
        uploadRoadPermitImage = (ImageButton) view.findViewById(R.id.uploadRoadPermitImage);
        roadPermitView = (ImageView) view.findViewById(R.id.roadPermitView);
        addInvoice = (Button)view.findViewById(R.id.addInvoice);
        addRoadPermit = (Button)view.findViewById(R.id.addRoadPermit);
        addInvoiceLayout = (LinearLayout) view.findViewById(R.id.addInvoiceLayout);
        addRoadPermitLayout = (LinearLayout) view.findViewById(R.id.addRoadPermitLayout);

        materialTypeData = new MaterialTypeData();
        tripData = new TripData();

        fromAddress.addTextChangedListener(new MyTextWatcher(fromAddress));
        toAddress.addTextChangedListener(new MyTextWatcher(toAddress));

        db = new DBAdapter(getActivity());
        db.open();

        return view;
    }


    private void setTransporterData(TransporterData transporterData){
        if (transporterData.getFromStateId() != null && transporterData.getFromStateId().trim().length() > 0) {
            if (stateListCursor.moveToFirst()) {
                for (int i = 0; i < stateListCursor.getCount(); i++) {
                    if (stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID)).equals(transporterData.getFromStateId())) {
                        fromState.setSelection(i, true);
                        break;
                    }
                    stateListCursor.moveToNext();
                }
            }
        }

        if (transporterData.getToStateId() != null && transporterData.getToStateId().trim().length() > 0) {
            if (stateListCursor.moveToFirst()) {
                for (int i = 0; i < stateListCursor.getCount(); i++) {
                    if (stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID)).equals(transporterData.getToStateId())) {
                        toState.setSelection(i, true);
                        break;
                    }
                    stateListCursor.moveToNext();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        invoiceData = new InvoiceData();
        roadPermitData = new RoadPermitData();
        String currentDate = TRIP_DATE_FORMAT.format(new Date());
        invoiceDate.setText(currentDate);


        stateListCursor = db.stateList();

        final ArrayList<String> stateNameArray = new ArrayList<>();
        if (stateListCursor.moveToFirst()) {
            do {
                String stateString = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                stateNameArray.add(stateString);
            } while (stateListCursor.moveToNext());
        }
        ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stateNameArray);

        fromState.setAdapter(state_adapter);
        fromState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                stateListCursor.moveToPosition(pos);
                String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                tripData.setFromStateId(stateId);
                tripData.setFromStateName(stateName);

                final Cursor cityListCursor = db.cityListByStateId(stateId);

                final ArrayList<String> cityNameArray = new ArrayList<>();
                if (cityListCursor.moveToFirst()) {
                    do {
                        String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        cityNameArray.add(cityString);
                    } while (cityListCursor.moveToNext());
                }

                ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, cityNameArray);

                fromCity.setAdapter(city_adapter);
                fromCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int pos, long id) {
                        cityListCursor.moveToPosition(pos);
                        String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                        String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        tripData.setFromCityId(cityId);
                        tripData.setFromCityName(cityName);
                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

                if(transporterData!=null) {
                    if (transporterData.getFromCityId() != null && transporterData.getFromCityId().trim().length() > 0) {
                        if (cityListCursor.moveToFirst()) {
                            for (int i = 0; i < cityListCursor.getCount(); i++) {
                                if (cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID)).equals(transporterData.getFromCityId())) {
                                    fromCity.setSelection(i);
                                    break;
                                }
                            }
                            cityListCursor.moveToFirst();
                        }
                    }
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        toState.setAdapter(state_adapter);
        toState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                stateListCursor.moveToPosition(pos);
                String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                tripData.setToStateId(stateId);
                tripData.setToStateName(stateName);

                final Cursor cityListCursor = db.cityListByStateId(stateId);

                final ArrayList<String> cityNameArray = new ArrayList<>();
                if (cityListCursor.moveToFirst()) {
                    do {
                        String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        cityNameArray.add(cityString);
                    } while (cityListCursor.moveToNext());
                }

                ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, cityNameArray);

                toCity.setAdapter(city_adapter);
                toCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        cityListCursor.moveToPosition(pos);
                        String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                        String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        tripData.setToCityId(cityId);
                        tripData.setToCityName(cityName);
                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

                if(transporterData!=null) {
                    if (transporterData.getToCityId() != null && transporterData.getToCityId().trim().length() > 0) {
                        if (cityListCursor.moveToFirst()) {
                            for (int i = 0; i < cityListCursor.getCount(); i++) {
                                if (cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID)).equals(transporterData.getToCityId())) {
                                    toCity.setSelection(i);
                                    break;
                                }
                            }
                            cityListCursor.moveToFirst();
                        }
                    }
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

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
                        String selectedDate = TRIP_DATE_FORMAT.format(date);
                        dispatchDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

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

        uploadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
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
                        String selectedDate = TRIP_DATE_FORMAT.format(date);
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
                    System.out.println("Material Amount :"+String.valueOf(materialTypeData.getAmount()));
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
                            totalAmount =  tempAmount + totalAmount;
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

        submitTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInvoiceMaterialJson();
                if (isValidTrip()){
                    String invoiceJsonData = getInvoiceMaterialJson();
                    String roadPermitTempIds = "";
                    for(RoadPermitData rdData : roadPermitArray){
                        roadPermitTempIds = roadPermitTempIds+rdData.getRoadPermitReturnId()+",";
                    }

                    //remove last comma from string
                    roadPermitTempIds = roadPermitTempIds.substring(0,roadPermitTempIds.length()-1);
                    addVehicleTrip(invoiceJsonData,roadPermitTempIds);
                }
            }
        });


        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addInvoiceLayout.setVisibility(View.VISIBLE);
                addInvoice.setVisibility(View.GONE);
            }
        });

        addRoadPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRoadPermitLayout.setVisibility(View.VISIBLE);
                addRoadPermit.setVisibility(View.GONE);
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

    ProgressDialog progressDialog;
    SharedPreferences prefs;

    private void addVehicleTrip(final String invoiceJsonData, final String roadPermitTempIds) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ADD_TRIP_API,
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

                map.put("vehicle_id", vehicleData.getVehicleId());
                map.put("from_country_id", tripData.getFromCountryId());
                map.put("to_country_id", tripData.getToCountryId());
                map.put("from_state_id", tripData.getFromStateId());
                map.put("to_state_id", tripData.getToStateId());
                map.put("from_city_id", tripData.getFromCityId());
                map.put("to_city_id", tripData.getToCityId());
                map.put("from_address", tripData.getFromAddress());
                map.put("to_address", tripData.getToAddress());
                map.put("customer_name", tripData.getCustomerName());
                map.put("order_request_id", tripData.getOrderRequestId());
                map.put("invoice_json_data", invoiceJsonData);
                map.put("road_permit_temporary_ids",roadPermitTempIds);
                map.put("quantity", tripData.getQuantity());
                map.put("dispatch_date", tripData.getDispatchDate()+" 00:00:00");
                map.put("arrival_date", tripData.getArrivalDate()+" 00:00:00");
                map.put("driver_name", tripData.getDriverName());
                map.put("driver_mobile_no", tripData.getDriverMobileNo());
                map.put("remarks","Dummy Remark");
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        progressDialog = ProgressDialog.show(getActivity(), "",
                "Please wait while adding trip .....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    boolean isValidTrip() {
        boolean isValid = true;

        if (!(tripData.getFromStateId() != null && tripData.getFromStateId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select source state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(tripData.getFromCityId() != null && tripData.getFromCityId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select source city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromAddress.getText() != null && fromAddress.getText().toString().trim().length() > 0) {
            tripData.setFromAddress(fromAddress.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter source address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!(tripData.getToStateId() != null && tripData.getToStateId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select destination state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(tripData.getToCityId() != null && tripData.getToCityId().trim().length() > 0)) {
            Toast.makeText(getActivity(), "Please select destination city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (toAddress.getText() != null && toAddress.getText().toString().trim().length() > 0) {
            tripData.setToAddress(toAddress.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter destination address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (customerName.getText() != null && customerName.getText().toString().trim().length() > 0) {
            tripData.setCustomerName(customerName.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter customer name", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (dispatchDate.getText() != null && dispatchDate.getText().toString().trim().length() > 0) {
            try {
                String dispatchDateString = dispatchDate.getText().toString();
                Date dispatchDateObj = TRIP_DATE_FORMAT.parse(dispatchDateString);
                tripData.setDispatchDate(dispatchDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Please enter date in " + TRIP_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getActivity(), "Please dispatch date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (arrivalDate.getText() != null && arrivalDate.getText().toString().trim().length() > 0) {
            try {
                Date arrivalDateObj = TRIP_DATE_FORMAT.parse(arrivalDate.getText().toString());
                String arrivalDateString  = arrivalDate.getText().toString();
                tripData.setArrivalDate(arrivalDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Please enter date in " + TRIP_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(getActivity(), "Please arrival date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (driverName.getText() != null && driverName.getText().toString().trim().length() > 0) {
            tripData.setDriverName(driverName.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter driver name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (driverMobile.getText() != null && driverMobile.getText().toString().trim().length() > 0) {
            tripData.setDriverMobileNo(driverMobile.getText().toString());
        } else {
            Toast.makeText(getActivity(), "Please enter driver contact no.", Toast.LENGTH_SHORT).show();
            return false;
        }


        for (InvoiceData iData : invoiceArray) {

            double totalAmount = 0.0;
            for (MaterialTypeData mData : materialArray) {
                if (mData.getInvoiceId().equals(iData.getInvoiceNumber())) {
                    double tempAmount = Double.parseDouble(mData.getAmount());
                    totalAmount = tempAmount + totalAmount;
                }
            }

            double invoiceAmount = 0.0;
            try {
                invoiceAmount = Double.parseDouble(iData.getInvoiceAmount());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!(invoiceAmount == totalAmount)) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("Invoice Amount is " + invoiceAmount + " and Material Amount is " + totalAmount)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return false;
            }

        }

        return isValid;
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
//{"result":"success","road_permit_temporary_id":1}
                                    String roadPermitTemporaryId = jsonObject.getString("road_permit_temporary_id");
                                    roadPermitData.setRoadPermitReturnId(roadPermitTemporaryId);
                                    String roadPermitIds = tripData.getRoadPermitTempIds();
                                    if (roadPermitIds != null) {
                                        tripData.setRoadPermitTempIds(roadPermitIds + roadPermitTemporaryId + ",");
                                    } else {
                                        tripData.setRoadPermitTempIds(roadPermitTemporaryId + ",");
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

                                    addRoadPermitLayout.setVisibility(View.GONE);
                                    addRoadPermit.setVisibility(View.VISIBLE);

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
                                    String tripDataInvoiceIds = tripData.getInvoiceIds();
                                    invoiceData.setInvoiceTempId(invoiceTemporaryId);
                                    if (tripDataInvoiceIds != null) {
                                        tripData.setInvoiceIds(tripDataInvoiceIds + invoiceTemporaryId + ",");
                                    } else {
                                        tripData.setInvoiceIds(invoiceTemporaryId + ",");
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

                                    addInvoiceLayout.setVisibility(View.GONE);
                                    addInvoice.setVisibility(View.VISIBLE);

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

    private boolean validateFromAddress() {
        if (fromAddress.getText().toString().trim().isEmpty()) {
            inputLayoutFromAddress.setError("Please Enter Source Address");
            requestFocus(fromAddress);
            return false;
        } else {
            inputLayoutFromAddress.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateToAddress() {
        if (toAddress.getText().toString().trim().isEmpty()) {
            inputLayoutToAddress.setError("Please Enter destination Address");
            requestFocus(toAddress);
            return false;
        } else {
            inputLayoutToAddress.setErrorEnabled(false);
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.fromAddress:
                    validateFromAddress();
                    break;
                case R.id.toAddress:
                    validateToAddress();
                    break;
            }
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
