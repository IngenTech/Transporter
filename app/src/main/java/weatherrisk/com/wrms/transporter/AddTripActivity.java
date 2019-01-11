package weatherrisk.com.wrms.transporter;

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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Base64;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitData;
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

public class AddTripActivity extends AppCompatActivity {

    public static final String VEHICLE_DATA = "vehicleData";

    public static final String ACTIVITY_TAG = "AddTrip";
    private VehicleData vehicleData;

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
    EditText tripRemark;


    Button addMaterial;


    TripData tripData;

    Spinner materialType;

    EditText materialRemark;
    EditText materialAmount;

    Button submitTrip;



    DBAdapter db;
    Cursor stateListCursor;

    private static String TRIP_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat TRIP_DATE_FORMAT = new SimpleDateFormat(TRIP_DATE_FORMAT_STRING);


    ArrayList<MaterialTypeData> materialArray = new ArrayList<>();
    MaterialTypeData materialTypeData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        vehicleData = getIntent().getParcelableExtra(VEHICLE_DATA);

        innerInflater = LayoutInflater.from(this);
        materialContainer = (LinearLayout) findViewById(R.id.materialContainer);
        confirmInvoice = (LinearLayout) findViewById(R.id.confirmInvoice);
        System.out.println("On Create Material Container child count : " + materialContainer.getChildCount());
        System.out.println("onCreate Invoice Container child count : " + materialContainer.getChildCount());

        inputLayoutFromAddress = (TextInputLayout) findViewById(R.id.inputLayoutFromAddress);
        inputLayoutToAddress = (TextInputLayout) findViewById(R.id.inputLayoutToAddress);

        fromState = (Spinner) findViewById(R.id.fromState);
        fromCity = (Spinner) findViewById(R.id.fromCity);
        fromAddress = (EditText) findViewById(R.id.fromAddress);
        toState = (Spinner) findViewById(R.id.toState);
        toCity = (Spinner) findViewById(R.id.toCity);
        toAddress = (EditText) findViewById(R.id.toAddress);
        customerName = (EditText) findViewById(R.id.customerName);
        dispatchDate = (EditText) findViewById(R.id.dispatchDate);
        arrivalDate = (EditText) findViewById(R.id.arrivalDate);
        driverName = (EditText) findViewById(R.id.driverName);
        driverMobile = (EditText) findViewById(R.id.driverMobile);



        addMaterial = (Button) findViewById(R.id.addMaterial);
        materialType = (Spinner) findViewById(R.id.materialType);

        materialRemark = (EditText) findViewById(R.id.materialRemark);
        materialAmount = (EditText) findViewById(R.id.materialAmount);
        submitTrip = (Button) findViewById(R.id.submitTrip);
        tripRemark = (EditText) findViewById(R.id.tripRemark);


        materialTypeData = new MaterialTypeData();
        tripData = new TripData();

        db = new DBAdapter(this);
        db.open();

        String currentDate = TRIP_DATE_FORMAT.format(new Date());



        stateListCursor = db.stateList();

        final ArrayList<String> stateNameArray = new ArrayList<>();
        stateNameArray.add("select state");
        if (stateListCursor.moveToFirst()) {
            do {
                String stateString = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                stateNameArray.add(stateString);
            } while (stateListCursor.moveToNext());
        }
        ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(AddTripActivity.this,
                android.R.layout.simple_spinner_item, stateNameArray);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromState.setAdapter(state_adapter);
        fromState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos > 0) {



                    stateListCursor.moveToPosition(pos-1);
                    String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                    String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                    tripData.setFromStateId(stateId);
                    tripData.setFromStateName(stateName);

                    final Cursor cityListCursor = db.cityListByStateId(stateId);

                    final ArrayList<String> cityNameArray = new ArrayList<>();

                    cityNameArray.add("select city");

                    if (cityListCursor.moveToFirst()) {
                        do {
                            String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                            cityNameArray.add(cityString);
                        } while (cityListCursor.moveToNext());
                    }

                    ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(AddTripActivity.this, android.R.layout.simple_spinner_item, cityNameArray);
                    city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    fromCity.setAdapter(city_adapter);
                    fromCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * Called when a new item was selected (in the Spinner)
                         */
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            if (pos>0) {
                                cityListCursor.moveToPosition(pos-1);
                                String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                                String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                                tripData.setFromCityId(cityId);
                                tripData.setFromCityName(cityName);
                            }
                        }

                        public void onNothingSelected(AdapterView parent) {
                            // Do nothing.
                        }
                    });
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
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if (pos>0) {
                    stateListCursor.moveToPosition(pos - 1);
                    String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                    String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                    tripData.setToStateId(stateId);
                    tripData.setToStateName(stateName);

                    final Cursor cityListCursor = db.cityListByStateId(stateId);

                    final ArrayList<String> cityNameArray = new ArrayList<>();
                    cityNameArray.add("select city");
                    if (cityListCursor.moveToFirst()) {
                        do {
                            String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                            cityNameArray.add(cityString);
                        } while (cityListCursor.moveToNext());
                    }

                    ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(AddTripActivity.this,
                            android.R.layout.simple_spinner_item, cityNameArray);
                    city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    toCity.setAdapter(city_adapter);
                    toCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        /**
                         * Called when a new item was selected (in the Spinner)
                         */
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            if (pos>0) {
                                cityListCursor.moveToPosition(pos - 1);
                                String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                                String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                                tripData.setToCityId(cityId);
                                tripData.setToCityName(cityName);
                            }
                        }

                        public void onNothingSelected(AdapterView parent) {
                            // Do nothing.
                        }
                    });
                }
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        dispatchDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(AddTripActivity.this);
                final View view = LayoutInflater.from(AddTripActivity.this).inflate(R.layout.date_picker, null);
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
                final AlertDialog.Builder adb = new AlertDialog.Builder(AddTripActivity.this);
                final View view = LayoutInflater.from(AddTripActivity.this).inflate(R.layout.date_picker, null);
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
                        Date dispatchDateObj = new Date();
                        try {
                            dispatchDateObj = TRIP_DATE_FORMAT.parse(dispatchDate.getText().toString());
                            int comparedInt = date.compareTo(dispatchDateObj);
                            if (comparedInt <= 0) {
                                Toast.makeText(AddTripActivity.this, "Arrival can not be less then dispatch date", Toast.LENGTH_SHORT).show();
                            } else {
                                arrivalDate.setText(selectedDate);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            arrivalDate.setText(selectedDate);
                        }

                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });





        final Cursor materialList = db.materialList();

        final ArrayList<String> materialNameArray = new ArrayList<>();
        materialNameArray.add("select materail");
        if (materialList.moveToFirst()) {
            do {
                String materialString = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialNameArray.add(materialString);
            } while (materialList.moveToNext());
        }
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(AddTripActivity.this,
                android.R.layout.simple_spinner_item, materialNameArray);

        material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialType.setAdapter(material_adapter);
        materialType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if (pos>0) {

                    materialList.moveToPosition(pos-1);
                    String materialId = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_ID));
                    String materialName = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                    materialTypeData.setMaterialTypeName(materialName);
                    materialTypeData.setMaterialTypeId(materialId);
                }
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        addMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidMaterial()) {

                    System.out.println("Material Container child count : " + materialContainer.getChildCount());
                    if (materialArray.size() == 0) {
                        if (materialContainer.getChildCount() == 0) {
                            final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                            TextView innerProductName0 = (TextView) confirmedProduct0.findViewById(R.id.sno);
                            innerProductName0.setText("Added Materials");
                            innerProductName0.setTypeface(Typeface.DEFAULT_BOLD);
                            innerProductName0.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            TextView productUOM0 = (TextView) confirmedProduct0.findViewById(R.id.materialTypeId);
                            productUOM0.setVisibility(View.GONE);
                            TextView productQty0 = (TextView) confirmedProduct0.findViewById(R.id.materialAmount1);
                            productQty0.setVisibility(View.GONE);
                            ImageButton remove0 = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
                            remove0.setVisibility(View.GONE);

                            materialContainer.addView(confirmedProduct0);

                            final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                            TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.sno);
                            innerProductName.setText("Invoice");
                            innerProductName.setTypeface(Typeface.DEFAULT_BOLD);
                            TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.materialTypeId);
                            innerProductUOM.setText("Material");
                            innerProductUOM.setTypeface(Typeface.DEFAULT_BOLD);
                            TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.materialAmount1);
                            innerProductQty.setText("Amount");
                            innerProductQty.setTypeface(Typeface.DEFAULT_BOLD);
                            ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                            remove.setVisibility(View.INVISIBLE);

                            materialContainer.addView(confirmedProduct);
                        }
                    }


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

                    Toast.makeText(AddTripActivity.this, "Material Uploaded", Toast.LENGTH_SHORT).show();

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
                    String invoiceIsString = materialTypeData.getInvoiceId();

                    materialTypeData = new MaterialTypeData();
                    materialTypeData.setInvoiceId(invoiceIsString);

                    materialType.setSelection(0);
//                    invoiceId.setSelection(0);
                    materialRemark.setText("");
                    materialAmount.setText("");

                    double totalAmount = 0.0;
                    for (MaterialTypeData mData : materialArray) {
                        if (mData.getInvoiceId().equals(materialTypeData.getInvoiceId())) {
                            double tempAmount = Double.parseDouble(mData.getAmount());
                            totalAmount = tempAmount + totalAmount;
                        }
                    }


                }
            }
        });

        submitTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidTrip()) {

                    addVehicleTrip();
                }
            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    ProgressDialog progressDialog;
    SharedPreferences prefs;

    private void addVehicleTrip() {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ADD_TRIP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {
                            System.out.println("Add Trip Response : " + trackResponse);
                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    new AlertDialog.Builder(AddTripActivity.this)
                                            .setMessage("Trip has been added successfully")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    AddTripActivity.this.finish();
                                                    Intent intent = new Intent(AddTripActivity.this, TransporterMainActivity.class);
                                                    intent.putExtra(ACTIVITY_TAG, ACTIVITY_TAG);
                                                    startActivity(intent);

                                                    /*Intent intent = new Intent();
                                                    intent.putExtra(ACTIVITY_TAG,ACTIVITY_TAG);
                                                    setResult(RESULT_OK, intent);*/

                                                }
                                            })
                                            .show();

                                } else {
                                    Toast.makeText(AddTripActivity.this, "Request has been denied by server", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AddTripActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AddTripActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(AddTripActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = AddTripActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, AddTripActivity.this.MODE_PRIVATE);
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

                map.put("quantity", tripData.getQuantity());
                map.put("dispatch_date", tripData.getDispatchDate() + " 00:00:00");
                map.put("arrival_date", tripData.getArrivalDate() + " 00:00:00");
                map.put("driver_name", tripData.getDriverName());
                map.put("driver_mobile_no", tripData.getDriverMobileNo());
                map.put("remarks", tripData.getTripRemark());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        progressDialog = ProgressDialog.show(AddTripActivity.this, "Adding Trip",
                "Please wait.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    boolean isValidTrip() {
        boolean isValid = true;

        if (!(tripData.getFromStateId() != null && tripData.getFromStateId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select source state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(tripData.getFromCityId() != null && tripData.getFromCityId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select source city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromAddress.getText() != null && fromAddress.getText().toString().trim().length() > 0) {
            tripData.setFromAddress(fromAddress.getText().toString());
        } else {
            Toast.makeText(AddTripActivity.this, "Please enter source address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!(tripData.getToStateId() != null && tripData.getToStateId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select destination state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(tripData.getToCityId() != null && tripData.getToCityId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select destination city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (toAddress.getText() != null && toAddress.getText().toString().trim().length() > 0) {
            tripData.setToAddress(toAddress.getText().toString());
        } else {
            Toast.makeText(AddTripActivity.this, "Please enter destination address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (customerName.getText() != null && customerName.getText().toString().trim().length() > 0) {
            tripData.setCustomerName(customerName.getText().toString());
        } else {
            Toast.makeText(AddTripActivity.this, "Please enter customer name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (tripRemark.getText() != null && tripRemark.getText().toString().trim().length() > 0) {
            tripData.setTripRemark(tripRemark.getText().toString());
        } else {
            tripData.setTripRemark("");
//            Toast.makeText(AddTripActivity.this, "Trip remark is blank", Toast.LENGTH_SHORT).show();
        }

        if (dispatchDate.getText() != null && dispatchDate.getText().toString().trim().length() > 0) {
            try {
                String dispatchDateString = dispatchDate.getText().toString();
                Date dispatchDateObj = TRIP_DATE_FORMAT.parse(dispatchDateString);
                tripData.setDispatchDate(dispatchDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(AddTripActivity.this, "Please enter date in " + TRIP_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(AddTripActivity.this, "Please dispatch date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (arrivalDate.getText() != null && arrivalDate.getText().toString().trim().length() > 0) {
            try {
                Date arrivalDateObj = TRIP_DATE_FORMAT.parse(arrivalDate.getText().toString());
                String arrivalDateString = arrivalDate.getText().toString();
                tripData.setArrivalDate(arrivalDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(AddTripActivity.this, "Please enter date in " + TRIP_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(AddTripActivity.this, "Please arrival date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (driverName.getText() != null && driverName.getText().toString().trim().length() > 0) {
            tripData.setDriverName(driverName.getText().toString());
        } else {
            Toast.makeText(AddTripActivity.this, "Please enter driver name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (driverMobile.getText() != null && driverMobile.getText().toString().trim().length() > 0) {
            tripData.setDriverMobileNo(driverMobile.getText().toString());
        } else {
            Toast.makeText(AddTripActivity.this, "Please enter driver contact no.", Toast.LENGTH_SHORT).show();
            return false;
        }




        return isValid;
    }







    boolean isValidMaterial() {
        boolean isValid = true;

        if (!(materialTypeData.getMaterialTypeId() != null && materialTypeData.getMaterialTypeId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select material type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(materialTypeData.getInvoiceId() != null && materialTypeData.getInvoiceId().trim().length() > 0)) {
            Toast.makeText(AddTripActivity.this, "Please select invoice for material", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AddTripActivity.this, "Please enter ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isValid;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }



}
