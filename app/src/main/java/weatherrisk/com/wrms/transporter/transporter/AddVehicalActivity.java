package weatherrisk.com.wrms.transporter.transporter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

/**
 * Created by Admin on 22-04-2017.
 */
public class AddVehicalActivity extends AppCompatActivity {


    EditText vehicalName;
    EditText vehicalNumber;
    EditText registrationValidity;

    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;
    private Spinner vehicalType;
    private Spinner vehicalCategory;
    private EditText maxSpeed;
    private Button addVehicalBTN;
    private Button addMore;
    private LinearLayout moreLayout;
    String openstatus = "0";

    private Spinner modelSpinner, permitSpinner, regionSpinner;
    private EditText purchageText, insurance, pollution, roadtax, fitness, other, capacity, ins_validity;
    private EditText poll_validity, road_validity, permit_validity, fit_validity, other_validity;
    CheckBox refrigerated, closedoor;

    ArrayList<String> vehicalTypeArray = new ArrayList<>();
    ArrayList<String> categoryArray = new ArrayList<>();

    ArrayList<String> modelArray = new ArrayList<>();

    ArrayList<String> permitArray = new ArrayList<>();
    ArrayList<String> regionArray = new ArrayList<>();
    private Calendar calendar;
    private int year, month, day;

    static final int DATE_PICKER_REG = 7;
    static final int DATE_PICKER_INS = 1;
    static final int DATE_PICKER_POLL = 2;
    static final int DATE_PICKER_PER = 3;
    static final int DATE_PICKER_ROAD = 4;
    static final int DATE_PICKER_FIT = 5;
    static final int DATE_PICKER_OTHER = 6;
    DBAdapter db;
    String vehical_type;
    String model_name;
    String refrig, close;
    String v_h = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehical_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(this);
        db.open();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        registrationValidity = (EditText) findViewById(R.id.add_vehical_validity);

        vehicalName = (EditText) findViewById(R.id.add_vehical_name);
        vehicalNumber = (EditText) findViewById(R.id.add_vehical_regi_no);
        vehicalType = (Spinner) findViewById(R.id.vehical_type_spinner);
        vehicalCategory = (Spinner) findViewById(R.id.vehical_category);
        maxSpeed = (EditText) findViewById(R.id.add_vehical_max_speed);
        addVehicalBTN = (Button) findViewById(R.id.addVehical_BTN);

        addMore = (Button) findViewById(R.id.add_more_BTN);
        moreLayout = (LinearLayout) findViewById(R.id.addmore_layout);

        modelSpinner = (Spinner) findViewById(R.id.add_vehical_model_no);
        permitSpinner = (Spinner) findViewById(R.id.add_vehical_permit_type);
        regionSpinner = (Spinner) findViewById(R.id.add_vehical_permit_region);

        categoryArray.add("select category");
        permitArray.add("Permit Type");
        regionArray.add("Permit Region");


        purchageText = (EditText) findViewById(R.id.add_vehical_purchage_year);

        insurance = (EditText) findViewById(R.id.add_vehical_insurance);
        pollution = (EditText) findViewById(R.id.add_vehical_pollution_no);
        roadtax = (EditText) findViewById(R.id.add_vehical_road_tax_no);
        fitness = (EditText) findViewById(R.id.add_vehical_fitness_receipt_no);
        other = (EditText) findViewById(R.id.add_vehical_other);
        capacity = (EditText) findViewById(R.id.add_vehical_capacity);

        registrationValidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_REG);
            }
        });

        ins_validity = (EditText) findViewById(R.id.add_insurance_validity);
        ins_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DATE_PICKER_INS);
            }
        });
        poll_validity = (EditText) findViewById(R.id.add_pollution_validity);
        poll_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_POLL);
            }
        });
        road_validity = (EditText) findViewById(R.id.add_roadtax_validity);
        road_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ROAD);
            }
        });
        permit_validity = (EditText) findViewById(R.id.add_permit_validity);
        permit_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_PER);
            }
        });
        fit_validity = (EditText) findViewById(R.id.add_fitness_validity);
        fit_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_FIT);
            }
        });

        other_validity = (EditText) findViewById(R.id.add_others_validity);
        other_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_OTHER);
            }
        });

        refrigerated = (CheckBox) findViewById(R.id.addvehical_checkBoxRefrigerated);
        closedoor = (CheckBox) findViewById(R.id.addvehical_checkBoxDoorClose);

        if (refrigerated.isChecked()) {

            refrig = "1";
        } else {
            refrig = "0";
        }
        if (closedoor.isChecked()) {
            close = "1";
        } else {
            close = "0";
        }

        final Cursor vehicalTypeCursor = db.vehicalTypeList();

        vehicalTypeArray = new ArrayList<>();
        vehicalTypeArray.add("select vehicle");

        if (vehicalTypeCursor.moveToFirst()) {
            do {
                String vehicleString = vehicalTypeCursor.getString(vehicalTypeCursor.getColumnIndex(DBAdapter.VEHICAL_TYPE));
                vehicalTypeArray.add(vehicleString);
            } while (vehicalTypeCursor.moveToNext());
        }
        ArrayAdapter<String> vehicle_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicalTypeArray);

        vehicle_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicalType.setAdapter(vehicle_adapter);
        vehicalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                v_h = null;

                if (pos > 0) {

                    vehicalTypeCursor.moveToPosition(pos - 1);
                    vehical_type = vehicalTypeCursor.getString(vehicalTypeCursor.getColumnIndex(DBAdapter.VEHICAL_TYPE));

                    v_h = vehical_type;

                   // vehicalModelList(vehical_type);
                }


            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });


        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryArray);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicalCategory.setAdapter(categoryAdapter);

        vehicalCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });


        ArrayAdapter<String> permitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, permitArray);
        permitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        permitSpinner.setAdapter(permitAdapter);


        permitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, regionArray);
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(regionAdapter);


        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {


            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        addMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openstatus.equalsIgnoreCase("0")) {
                    moreLayout.setVisibility(View.VISIBLE);


                    openstatus = "1";
                } else {

                    moreLayout.setVisibility(View.GONE);
                    openstatus = "0";
                }
            }
        });

        vehicalType.setPrompt("Vehical Type");
        vehicalCategory.setPrompt("Category");


        addVehicalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String na = vehicalName.getText().toString();
                String nu = vehicalNumber.getText().toString();
                String regN = registrationValidity.getText().toString();
                String max = maxSpeed.getText().toString();

                if (na == null || na.length() < 2) {

                    vehicalName.setError("Please enter vehical name");

                } else if (nu == null || nu.length() < 2) {
                    vehicalNumber.setError("Please enter registration name");
                } else if (regN == null || regN.length() < 2) {
                    registrationValidity.setError("Please select registration validity.");
                }else if (max == null || max.length() < 1) {
                    maxSpeed.setError("Please select registration validity.");
                }
                else if (v_h==null) {

                    Toast.makeText(getApplicationContext(),"Please select vehicle Type",Toast.LENGTH_SHORT).show();

                } else {
                    vehicalName.setError(null);
                    vehicalNumber.setError(null);
                    registrationValidity.setError(null);
                    maxSpeed.setError(null);

                    if (refrigerated.isChecked()) {

                        refrig = "1";
                    } else {
                        refrig = "0";
                    }
                    if (closedoor.isChecked()) {
                        close = "1";
                    } else {
                        close = "0";
                    }

                    addVehicleMethod();
                }
            }
        });

    }


    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case DATE_PICKER_INS:

                DatePickerDialog da = new DatePickerDialog(this, ins_dateListener,
                        year, month, day);
                Calendar c = Calendar.getInstance();

                c.add(Calendar.DATE, 1);
                Date newDate = c.getTime();
                da.getDatePicker().setMinDate(newDate.getTime() - 1000);
                return da;

            // return new DatePickerDialog(this, ins_dateListener, year, month, day);
            case DATE_PICKER_POLL:

                DatePickerDialog da2 = new DatePickerDialog(this, pol_dateListener,
                        year, month, day);
                Calendar c2 = Calendar.getInstance();

                c2.add(Calendar.DATE, 1);
                Date newDate2 = c2.getTime();
                da2.getDatePicker().setMinDate(newDate2.getTime() - 1000);
                return da2;

            case DATE_PICKER_PER:
                DatePickerDialog da3 = new DatePickerDialog(this, permit_dateListener,
                        year, month, day);
                Calendar c3 = Calendar.getInstance();

                c3.add(Calendar.DATE, 1);
                Date newDate3 = c3.getTime();
                da3.getDatePicker().setMinDate(newDate3.getTime() - 1000);
                return da3;

            case DATE_PICKER_ROAD:

                DatePickerDialog da4 = new DatePickerDialog(this, road_dateListener,
                        year, month, day);
                Calendar c4 = Calendar.getInstance();

                c4.add(Calendar.DATE, 1);
                Date newDate4 = c4.getTime();
                da4.getDatePicker().setMinDate(newDate4.getTime() - 1000);
                return da4;


            case DATE_PICKER_FIT:

                DatePickerDialog da5 = new DatePickerDialog(this, fit_dateListener,
                        year, month, day);
                Calendar c5 = Calendar.getInstance();

                c5.add(Calendar.DATE, 1);
                Date newDate5 = c5.getTime();
                da5.getDatePicker().setMinDate(newDate5.getTime() - 1000);
                return da5;

            case DATE_PICKER_OTHER:

                DatePickerDialog da6 = new DatePickerDialog(this, other_dateListener,
                        year, month, day);
                Calendar c6 = Calendar.getInstance();

                c6.add(Calendar.DATE, 1);
                Date newDate6 = c6.getTime();
                da6.getDatePicker().setMinDate(newDate6.getTime() - 1000);
                return da6;

            case DATE_PICKER_REG:

                DatePickerDialog da1 = new DatePickerDialog(this, regis_dateListener,
                        year, month, day);
                Calendar c1 = Calendar.getInstance();

                c1.add(Calendar.DATE, 1);
                Date newDate1 = c1.getTime();
                da1.getDatePicker().setMinDate(newDate1.getTime() - 1000);
                return da1;

            //  return new DatePickerDialog(this, regis_dateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener regis_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    showDateReg(arg1, arg2, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener ins_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day

            showDateI(arg1, arg2, arg3);
        }
    };


    private DatePickerDialog.OnDateSetListener pol_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDatePol(arg1, arg2, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener road_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDateRoad(arg1, arg2, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener permit_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDatePermit(arg1, arg2, arg3);
                }
            };


    private DatePickerDialog.OnDateSetListener fit_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day

                    showDateFit(arg1, arg2, arg3);
                }
            };


    private DatePickerDialog.OnDateSetListener other_dateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDateOther(arg1, arg2, arg3);
                }
            };


    private void showDateReg(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        registrationValidity.setText(cdate);
    }


    private void showDateOther(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        other_validity.setText(cdate);
    }

    private void showDateFit(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        fit_validity.setText(cdate);
    }

    private void showDatePermit(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        permit_validity.setText(cdate);
    }


    private void showDateRoad(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        road_validity.setText(cdate);
    }


    private void showDateI(int year, int month, int day) {

        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);

        ins_validity.setText(cdate);
    }

    private void showDatePol(int year, int month, int day) {
        Date date = new Date(year - 1900, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String cdate = formatter.format(date);
        poll_validity.setText(cdate);
    }


    ProgressDialog dialog;
    SharedPreferences prefs;

    private void addVehicleMethod() {
        StringRequest addVehicle = new StringRequest(Request.Method.POST, MyUtility.URL.ADD_VEHICLE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {
                            // System.out.println("Add vehical: " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {


                                if (jsonObject.get("Result").equals("Success")) {


                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Added";
                                    Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Added";
                                    Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Added";
                                Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.v("slklksl", volleyError.getMessage().toString() + "");
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String user_id = (prefs.getString(AppController.PREFERENCE_USER_ID, "0"));
                String access_token = (prefs.getString(AppController.ACCESS_TOKEN, "0"));

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", access_token);
                map.put("UserId", user_id);
                map.put("VehicleName", "" + vehicalName.getText().toString());
                map.put("RegistrationNumber", "" + vehicalNumber.getText().toString());
                map.put("RegistrationValidity", "" + registrationValidity.getText().toString());

                map.put("MaxSpeed", "" + maxSpeed.getText().toString());
                map.put("VehicleType", "" + vehical_type);
                map.put("VehicleCategory", "1");

                map.put("ModelNumber", "" + model_name);
                map.put("PurchaseYear", "" + purchageText.getText().toString());
                map.put("InsuranceNumber", "" + insurance.getText().toString());
                map.put("InsuranceValidity", "" + ins_validity.getText().toString());
                map.put("PollutionNumber", "" + pollution.getText().toString());
                map.put("PollutionValidity", "" + poll_validity.getText().toString());
                map.put("RoadTaxNumber", "" + roadtax.getText().toString());
                map.put("RoadTaxValidity", "" + road_validity.getText().toString());
                map.put("PermitType", "");
                map.put("PermitRegion", "");
                map.put("PermitValidity", "" + permit_validity.getText().toString());
                map.put("FitnessRecieptNumber", "" + fitness.getText().toString());
                map.put("Others", "" + other.getText().toString());
                map.put("OthersValidity", "" + other_validity.getText().toString());
                map.put("Capacity", "" + capacity.getText().toString());
                map.put("Refrigrated", "" + refrig);
                map.put("DoorClose", "" + close);
                map.put("SimNumber", "");
                map.put("MobileNumber", "");
                map.put("VehicleTag", "");
                map.put("VehicleNumber", "");


                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                return map;
            }
        };

        dialog = ProgressDialog.show(this, "", "Adding Vehicle.....", true);
        addVehicle.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(addVehicle);

    }


    ProgressDialog dialog1;

    private void vehicalModelList(final String type) {


        if (prefs == null) {
            prefs = this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICAL_MODEL_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        dialog1.dismiss();
                        try {
                            System.out.println("Vehical Model Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("VehicleModelList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_VEHICAL_MODEL);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_VEHICAL_MODEL + "(" + DBAdapter.ID + "," +
                                                DBAdapter.VEHICAL_TYPE + "," +
                                                DBAdapter.MODEL_NAME + "," +
                                                DBAdapter.MODEL_NUMBER + "," +
                                                DBAdapter.MODEL_YEAR + ") VALUES (?,?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("VehicleType"));
                                            stmt.bindString(3, jObject.getString("ModelName"));
                                            stmt.bindString(4, jObject.getString("ModelNumber"));
                                            stmt.bindString(5, jObject.getString("ModelYear"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();

                                        // Set Model Number in model Spinner


                                        modelArray = new ArrayList<>();

                                        final Cursor modelCursor = db.vehicalModelList();
                                        if (modelCursor.moveToFirst()) {
                                            do {
                                                String modelString = modelCursor.getString(modelCursor.getColumnIndex(DBAdapter.MODEL_NUMBER));
                                                modelArray.add(modelString);
                                            } while (modelCursor.moveToNext());
                                        }

                                        ArrayAdapter<String> modelAdapter = new ArrayAdapter<String>(AddVehicalActivity.this, android.R.layout.simple_spinner_item, modelArray);
                                        modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        modelSpinner.setAdapter(modelAdapter);


                                        modelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                                modelCursor.moveToPosition(pos);
                                                model_name = modelCursor.getString(modelCursor.getColumnIndex(DBAdapter.MODEL_NUMBER));

                                            }

                                            public void onNothingSelected(AdapterView parent) {
                                                // Do nothing.
                                            }
                                        });

                                    } else {
                                        Toast.makeText(AddVehicalActivity.this, "Vehical Model Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(AddVehicalActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AddVehicalActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(AddVehicalActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog1.dismiss();

                Toast.makeText(AddVehicalActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                map.put("VehicleType", type);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog1 = ProgressDialog.show(AddVehicalActivity.this, "", "Fetching Vehical Model.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


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
