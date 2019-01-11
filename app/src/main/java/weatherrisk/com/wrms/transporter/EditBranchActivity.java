package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.bean.BranchListBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 10-05-2017.
 */
public class EditBranchActivity extends AppCompatActivity {

    private EditText firmName, conatctName, homeNo, officeNo, personalNo, serviceTaxNo, address, emailId;
    private Spinner stateSpinner, citySpinner;
    ArrayAdapter<String> city_adapter;
    Cursor stateListCursor;
    Cursor cityListCursor;
    DBAdapter db;
    String stateId, stateName, cityId, cityName;
    private Button addBranchBTN;
    int pos;
    ArrayList<BranchListBean> branch_list = new ArrayList<BranchListBean>();
    String compareValueCity;
    String serialNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_branch_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
        branch_list = new ArrayList<BranchListBean>();

        String position = getIntent().getStringExtra("position");
        Log.v("popopop", "" + position);

        if (position != null && position.length() > 0) {
            pos = Integer.parseInt(position);
        }


        branch_list = getIntent().getExtras().getParcelableArrayList("branch_list");


        db = new DBAdapter(this);
        db.open();

        addBranchBTN = (Button) findViewById(R.id.submit_branch_btn);
        addBranchBTN.setText("SAVE");

        firmName = (EditText) findViewById(R.id.addbranch_firm_name);
        conatctName = (EditText) findViewById(R.id.addbranch_contact_name);
        homeNo = (EditText) findViewById(R.id.addbranch_home_no);
        officeNo = (EditText) findViewById(R.id.addbranch_office_no);
        personalNo = (EditText) findViewById(R.id.addbranch_personal_no);
        serviceTaxNo = (EditText) findViewById(R.id.addbranch_servicetax_no);
        address = (EditText) findViewById(R.id.addbranch_address);
        emailId = (EditText) findViewById(R.id.addbranch_email);

        if (branch_list != null) {
            firmName.setText(branch_list.get(pos).getFirm_name());
            conatctName.setText(branch_list.get(pos).getContact_name());
            homeNo.setText(branch_list.get(pos).getHome_contact_no());
            officeNo.setText(branch_list.get(pos).getOffice_contact_no());
            personalNo.setText(branch_list.get(pos).getPersonal_contact_no());
            serviceTaxNo.setText(branch_list.get(pos).getTin_no());
            address.setText(branch_list.get(pos).getAddress());
            emailId.setText(branch_list.get(pos).getEmail_id());

            cityId = branch_list.get(pos).getCity_id();
            stateId = branch_list.get(pos).getState_id();
            cityName = branch_list.get(pos).getCityName();
            stateName = branch_list.get(pos).getStateName();

            serialNo = branch_list.get(pos).getSerial();


        }

        stateSpinner = (Spinner) findViewById(R.id.addbranch_state_spinner);
        citySpinner = (Spinner) findViewById(R.id.addbranch_city_spinner);

        stateListCursor = db.stateList();
        final ArrayList<String> stateNameArray = new ArrayList<>();
        if (stateListCursor.moveToFirst()) {
            do {
                String stateString = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                stateNameArray.add(stateString);
            } while (stateListCursor.moveToNext());
        }
        ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, stateNameArray);

        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(state_adapter);


        String compareValue = stateName;
        if (!compareValue.equals(null) && compareValue.length() > 0) {
            int spinnerPosition = state_adapter.getPosition(compareValue);
            stateSpinner.setSelection(spinnerPosition);
        }


        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                stateListCursor.moveToPosition(pos);
                stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));


                cityListCursor = db.cityListByStateId(stateId);

                final ArrayList<String> cityNameArray = new ArrayList<>();
                if (cityListCursor.moveToFirst()) {
                    do {
                        String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        cityNameArray.add(cityString);
                    } while (cityListCursor.moveToNext());
                }

                city_adapter = new ArrayAdapter<String>(EditBranchActivity.this, android.R.layout.simple_spinner_item, cityNameArray);
                city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(city_adapter);

                compareValueCity = cityName;
                if (!compareValueCity.equals(null) && compareValueCity.length() > 0) {
                    int spinnerPosition = city_adapter.getPosition(compareValueCity);
                    citySpinner.setSelection(spinnerPosition);
                }


                citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */

                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        cityListCursor.moveToPosition(pos);
                        cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                        cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));

                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });


            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });


        addBranchBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = firmName.getText().toString().trim();
                String cName = conatctName.getText().toString().trim();
                String hNo = homeNo.getText().toString().trim();
                String oNo = officeNo.getText().toString().trim();
                String pNo = personalNo.getText().toString().trim();
                String sTax = serviceTaxNo.getText().toString().trim();
                String add = address.getText().toString().trim();
                String email = emailId.getText().toString().trim();

                if (fName == null || fName.length() < 2) {
                    firmName.setError("Please enter firm name");
                } else if (cName == null || cName.length() < 2) {
                    conatctName.setError("please enter contact no.");
                } else if (hNo == null || hNo.length() < 10) {
                    homeNo.setError("please enter valid home no.");
                } else if (oNo == null || oNo.length() < 10) {
                    officeNo.setError("please enter valid office no.");
                } else if (pNo == null || pNo.length() < 10) {
                    personalNo.setError("please enter valid personal no.");
                } else if (sTax == null || sTax.length() < 2) {
                    serviceTaxNo.setError("please enter service tax no.");
                } else if (add == null || add.length() < 5) {
                    address.setError("please enter address");
                } else if (email == null || email.length() < 2) {
                    emailId.setError("please enter valid email");
                } else {

                    firmName.setError(null);
                    conatctName.setError(null);
                    homeNo.setError(null);
                    officeNo.setError(null);
                    personalNo.setError(null);
                    serviceTaxNo.setError(null);
                    address.setError(null);
                    emailId.setError(null);

                    addBranchMethod();
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

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void addBranchMethod() {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.EDIT_BRANCH_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Add branch Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be added";
                                    Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();


                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be added";
                                    Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();

                                }

                                finish();

                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be added";
                                Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("StateId", stateId);
                map.put("CityId", cityId);
                map.put("OfficeContactNo", officeNo.getText().toString().trim());
                map.put("TinNo", serviceTaxNo.getText().toString().trim());
                map.put("Address", address.getText().toString().trim());
                map.put("FirmName", firmName.getText().toString().trim());
                map.put("CountryId", "1");
                map.put("HomeContactNo", homeNo.getText().toString().trim());
                map.put("EmailId", emailId.getText().toString().trim());
                map.put("PersonalContactNo", personalNo.getText().toString().trim());
                map.put("ContactName", conatctName.getText().toString().trim());
                map.put("Serial", serialNo);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        viewDocRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(this, "", "adding branch.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }
}