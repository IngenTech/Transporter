package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.speech.tts.Voice;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.OnRoadAssistanceAdapter;
import weatherrisk.com.wrms.transporter.dataobject.CustomerPendingOrder;
import weatherrisk.com.wrms.transporter.dataobject.LastLocationData;
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;
import weatherrisk.com.wrms.transporter.expenses.DriverExpensesActivity;
import weatherrisk.com.wrms.transporter.expenses.VehicalExpensesActivity;
import weatherrisk.com.wrms.transporter.fragment.AddOnRoadAssistanceFragment;
import weatherrisk.com.wrms.transporter.fragment.AddOrderFragment;
import weatherrisk.com.wrms.transporter.fragment.AddTripFragment;
import weatherrisk.com.wrms.transporter.fragment.ManageTripFragment;
import weatherrisk.com.wrms.transporter.fragment.OnRoadAssistanceFragment;
import weatherrisk.com.wrms.transporter.fragment.SearchVehicleFragment;
import weatherrisk.com.wrms.transporter.fragment.SingleChoiceVehicleListFragment;
import weatherrisk.com.wrms.transporter.fragment.StartTripFragment;
import weatherrisk.com.wrms.transporter.fragment.TransporterListFragment;
import weatherrisk.com.wrms.transporter.orders_action_activity.AddOnRoadAssistanceActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.AlertActivity;
import weatherrisk.com.wrms.transporter.transporter.AddVehicalActivity;
import weatherrisk.com.wrms.transporter.transporter.CanceledOrder;
import weatherrisk.com.wrms.transporter.transporter.CustomerConfirmOrderFragment;
import weatherrisk.com.wrms.transporter.transporter.CustomerPendingOrderFragment;
import weatherrisk.com.wrms.transporter.transporter.CustomerRunningTripFragment;
import weatherrisk.com.wrms.transporter.transporter.CustomerTripHistoryFragment;
import weatherrisk.com.wrms.transporter.transporter.VehicalManageActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 21-04-2017.
 */
public class TransporterMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TransporterListFragment.OnFragmentInteractionListener,
        AddTripFragment.OnFragmentInteractionListener,
        AddOrderFragment.OnFragmentInteractionListener,
        OnRoadAssistanceFragment.OnFragmentInteractionListener,
        ManageTripFragment.OnFragmentInteractionListener,
        StartTripFragment.OnFragmentInteractionListener,
        SingleChoiceVehicleListFragment.OnFragmentInteractionListener,
        AddOnRoadAssistanceFragment.OnFragmentLastInteractionListener{

    Toolbar toolbar;
    // private ArrayList<TransporterData> transporterDatas;
    SharedPreferences prefs;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<VehicleData> vehicleList;

    ArrayList<TripData> tripList;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_customer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        tripList = new ArrayList<TripData>();

        db = new DBAdapter(this);
        db.open();


        vehicleList = new ArrayList<>();

        Cursor vehicleCursor = db.getVehicle();
        if (vehicleCursor.moveToFirst()) {
            do {

                String rowId = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.ID));
                String vehicleId = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_ID));
                String vehicleNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_NO));
                String imei = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.IMEI));
                String modelNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.MODEL_NO));
                String registrationNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.REGISTRATION_NO));
                String insuranceNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.INSURANCE_NO));
                String validityDate = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VALIDITY_DATE));
                String pollutionNo = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.POLLUTION_NO));
                String yearOfPurchase = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.YEAR_OF_PURCHASE));
                String capacity = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CAPACITY));
                String refrigerated = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.REFRIGERATED));
                String closeDoor = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.CLOSED_DORE));

                vehicleList.add(new VehicleData(vehicleId, vehicleNo, imei, modelNo, registrationNo, insuranceNo,
                        validityDate, pollutionNo, yearOfPurchase, capacity, refrigerated, closeDoor, VehicleData.NOT_SELECTED));

            } while (vehicleCursor.moveToNext());
        }
        vehicleCursor.close();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        //   transporterDatas = getIntent().getParcelableArrayListExtra(CustomerOrderActivity.TRANSPORTER_LIST);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = (View) LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }

                Intent in = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(in);
            }
        });

        System.out.println("(headerView!=null) : " + (headerView != null));
        if (headerView != null) {
            TextView accountName = (TextView) headerView.findViewById(R.id.accountName);
            TextView vehicleCount = (TextView) headerView.findViewById(R.id.vehicleCount);
            if (prefs == null) {
                prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
            }
            String accountNameString = prefs.getString(AppController.PREFERENCE_ACCOUNT_NAME, "");
            accountName.setText(accountNameString);
        }
        navigationView.addHeaderView(headerView);

        Fragment fragment = null;

        fragment = new CustomerConfirmOrderFragment().newInstance(vehicleList);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {


/*
            FragmentManager fm = this.getSupportFragmentManager();

            if (fm.getBackStackEntryCount()<1){
                exitMethod();
            }else {

                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    //  fm.popBackStack();

                    super.onBackPressed();
                }
            }*/



            Fragment frg = getSupportFragmentManager().findFragmentById(R.id.frame_container);
            if (frg instanceof CustomerConfirmOrderFragment) {    // do something with f
                //  HomeActivity.this.finish();
                exitMethod();
            } else {

                Fragment fragment = null;

                fragment = new CustomerConfirmOrderFragment().newInstance(vehicleList);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }

        }
    }

    private void logoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransporterMainActivity.this);
        builder.setTitle("LOGOUT").
                setMessage("Do You want to logout?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (prefs == null) {
                            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();
                        dialogInterface.cancel();
                        Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(in);
                        finish();
                    }
                }).
                setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void exitMethod() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TransporterMainActivity.this);
        builder.setTitle("EXIT").
                setMessage("Do You want to exit?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                }).
                setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_shipment_history:
                fragment = CustomerTripHistoryFragment.newInstance("", "");
                break;
            case R.id.nav_confirm_order:
                fragment = CustomerConfirmOrderFragment.newInstance(vehicleList);
                break;
            case R.id.nav_pending_orders:
                fragment = CustomerPendingOrderFragment.newInstance("", "");
                break;

            case R.id.nav_track_shipment:
                fragment = CustomerRunningTripFragment.newInstance(vehicleList, tripList);
                break;

            case R.id.nav_cancel_order:
                fragment = CanceledOrder.newInstance("", "");
                break;

            case R.id.nav_vehical_mng:
                Intent in = new Intent(getApplicationContext(), VehicalManageActivity.class);
                startActivity(in);
                break;

            case R.id.nav_onroad_assistance:

                fragment = OnRoadAssistanceFragment.newInstance("", "");
                break;

            case R.id.nav_driver_expense:
                Intent in1 = new Intent(getApplicationContext(), DriverExpensesActivity.class);
                startActivity(in1);
                break;

            case R.id.nav_vehical_expense:
                Intent in2 = new Intent(getApplicationContext(), VehicalExpensesActivity.class);
                startActivity(in2);
                break;

            case R.id.nav_alert_list:

                Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_logout:
                final ProgressDialog progressDialog = new ProgressDialog(TransporterMainActivity.this, R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();

                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }

                String user_id = (prefs.getString(AppController.PREFERENCE_USER_ID, "0"));

                String access_token = (prefs.getString(AppController.ACCESS_TOKEN, "0"));


                logoutRequest(user_id, access_token, progressDialog);
                break;

            default:

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            //fragmentTransaction.addToBackStack(null);
           // fragmentTransaction.addToBackStack(getString(R.string.customer_pending_orders));
            fragmentTransaction.commit();
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO for any intraction with fragment
    }

    private void logoutRequest(final String userId, final String accessToken, final ProgressDialog progressDialog) {
        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String loginResponse) {
                        progressDialog.dismiss();
                        Log.d("LogOut", "LogOut  Response : " + loginResponse);
                        try {

                            JSONObject jsonObject = new JSONObject(loginResponse);

                            String status = jsonObject.has("Status") ? jsonObject.getString("Status") : "";


                            if (status != null && status.equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equalsIgnoreCase("success")) {

                                    String succMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Blank Response";

                                    onLogoutSuccess(succMessage);

                                }
                            } else {
                                String failMessage = jsonObject.has("Message") ? jsonObject.getString("Message") : "Blank Response";
                                onLogoutFailed(failMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            onLogoutFailed("Not able parse response");
                        }
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                progressDialog.cancel();
                onLogoutFailed("Not able to connect with server");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();

                String physical_address = MyUtility.macAddress(TransporterMainActivity.this);
                map.put("UserId", userId);
                map.put("PhysicalAddress",physical_address);
                map.put("AccessToken", accessToken);

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }

    public void onLogoutSuccess(String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success");
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                SharedPreferences preferences = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                db.resetDatabase();

                dialogInterface.dismiss();
                Intent intent = new Intent(TransporterMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    public void onLogoutFailed(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Failed");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                SharedPreferences preferences = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                db.resetDatabase();

                dialogInterface.dismiss();
                Intent intent = new Intent(TransporterMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }


    @Override
    public void onFragmentInteraction(String title) {

    }

    @Override
    public void onLastLocationFragmentInteraction(String title, ArrayList<LastLocationData> vehiclesLastLocationArray) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sync) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sync Project");
            builder.setMessage("Do you want to sync your project?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();


                    syncMethod();

                }
            });
            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();


                }
            });
            builder.show();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void syncMethod(){

        loadVehicleList();

    }


    private void loadVehicleList() {
        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "", "Fetching State.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        System.out.println("load Vehicle called");

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICLE_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String vehicleResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehicle Response : " + vehicleResponse);
                            JSONObject jsonObject = new JSONObject(vehicleResponse);

                            if (jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                if (jsonObject.getString("Result").equals("Success")) {

                                    JSONArray vehicleArray = jsonObject.getJSONArray("VehicleList");
                                    if (prefs == null) {
                                        prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                                    }
                                    SharedPreferences.Editor editor = prefs.edit();

                                    editor.putString(AppController.PREFERENCE_VEHICLE_COUNT, String.valueOf(vehicleArray.length()));
                                    System.out.println("vehicleCount : " + String.valueOf(vehicleArray.length()));
                                    boolean isEdited = editor.commit();
                                    System.out.println("isEdited : " + isEdited);

                                    SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                    SqliteDB.beginTransaction();

                                    if (vehicleArray.length() > 0) {
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_VEHICLE);
                                        String insertVehicleQuery = "INSERT INTO " + DBAdapter.TABLE_VEHICLE + "(" + DBAdapter.ID + ","
                                                + DBAdapter.VEHICLE_ID + ","
                                                + DBAdapter.VEHICLE_NO + ","
                                                + DBAdapter.IMEI + ","
                                                + DBAdapter.MODEL_NO + ","
                                                + DBAdapter.REGISTRATION_NO + ","
                                                + DBAdapter.INSURANCE_NO + ","
                                                + DBAdapter.VALIDITY_DATE + ","
                                                + DBAdapter.POLLUTION_NO + ","
                                                + DBAdapter.YEAR_OF_PURCHASE + ","
                                                + DBAdapter.CAPACITY + ","
                                                + DBAdapter.VEHICLE_MODEL_ID + ","
                                                + DBAdapter.VEHICLE_DATE_RC + ","
                                                + DBAdapter.VEHICLE_DATE_POLLUTION + ","
                                                + DBAdapter.VEHICLE_ROAD_TAX + ","
                                                + DBAdapter.VEHICLE_DATE_ROAD_TAX + ","
                                                + DBAdapter.VEHICLE_PERMIT_TYPE + ","
                                                + DBAdapter.VEHICLE_PERMIT_REGION_ID + ","
                                                + DBAdapter.VEHICLE_DATE_PERMIT + ","
                                                + DBAdapter.VEHICLE_FITNESS + ","
                                                + DBAdapter.VEHICLE_DATE_FITNESS + ","
                                                + DBAdapter.VEHICLE_OTHER_DATE + ","
                                                + DBAdapter.VEHICLE_OTHER_NO + ","
                                                + DBAdapter.VEHICLE_CREATE_ID + ","
                                                + DBAdapter.VEHICLE_CREATE_DATE + ","
                                                + DBAdapter.VEHICLE_EDIT_ID + ","
                                                + DBAdapter.VEHICLE_STATUS + ","
                                                + DBAdapter.VEHICLE_REMARKS + ","
                                                + DBAdapter.VEHICLE_TYPE + ","
                                                + DBAdapter.VEHICLE_CATEGORY + ","
                                                + DBAdapter.VEHICLE_MAX_SPEED + ","
                                                + DBAdapter.REFRIGERATED + ","
                                                + DBAdapter.CLOSED_DORE + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )";

                                        SQLiteStatement insertVehicleStatement = SqliteDB.compileStatement(insertVehicleQuery);

                                        for (int i = 0; i < vehicleArray.length(); i++) {

                                            JSONObject jObject = vehicleArray.getJSONObject(i);

                                            insertVehicleStatement.bindString(1, "" + i);
                                            insertVehicleStatement.bindString(2, jObject.getString("vehicle_id"));
                                            insertVehicleStatement.bindString(3, jObject.getString("vehicle_no"));
                                            insertVehicleStatement.bindString(4, "IMEI");
                                            insertVehicleStatement.bindString(5, jObject.getString("model_no"));
                                            insertVehicleStatement.bindString(6, jObject.getString("registration_no"));
                                            insertVehicleStatement.bindString(7, jObject.getString("insurance_no"));
                                            insertVehicleStatement.bindString(8, jObject.getString("insurance_validity"));
                                            insertVehicleStatement.bindString(9, jObject.getString("pollution_no"));
                                            insertVehicleStatement.bindString(10, jObject.getString("year_of_purchase"));
                                            insertVehicleStatement.bindString(11, jObject.getString("capacity"));

                                            insertVehicleStatement.bindString(12, jObject.getString("model_id"));
                                            insertVehicleStatement.bindString(13, jObject.getString("date_rc"));
                                            insertVehicleStatement.bindString(14, jObject.getString("date_pollution"));
                                            insertVehicleStatement.bindString(15, jObject.getString("road_tax_no"));
                                            insertVehicleStatement.bindString(16, jObject.getString("date_road_tax"));
                                            insertVehicleStatement.bindString(17, jObject.getString("permit_type"));
                                            insertVehicleStatement.bindString(18, jObject.getString("permit_region_id"));
                                            insertVehicleStatement.bindString(19, jObject.getString("date_permit_type"));
                                            insertVehicleStatement.bindString(20, jObject.getString("fitness_no"));
                                            insertVehicleStatement.bindString(21, jObject.getString("date_fitness"));

                                            insertVehicleStatement.bindString(22, jObject.getString("date_others"));
                                            insertVehicleStatement.bindString(23, jObject.getString("others_no"));
                                            insertVehicleStatement.bindString(24, jObject.getString("create_id"));
                                            insertVehicleStatement.bindString(25, jObject.getString("create_date"));
                                            insertVehicleStatement.bindString(26, jObject.getString("edit_id"));
                                            insertVehicleStatement.bindString(27, jObject.getString("status"));
                                            insertVehicleStatement.bindString(28, jObject.getString("remarks"));
                                            insertVehicleStatement.bindString(29, jObject.getString("vehicle_type"));
                                            insertVehicleStatement.bindString(30, jObject.getString("category"));
                                            insertVehicleStatement.bindString(31, jObject.getString("max_speed"));

                                            insertVehicleStatement.bindString(32, jObject.getString("refrigerated_nonRefrigerated"));
                                            insertVehicleStatement.bindString(33, jObject.getString("close_open"));

                                            insertVehicleStatement.execute();
                                        }
                                    }
                                    SqliteDB.setTransactionSuccessful();
                                    SqliteDB.endTransaction();
                                    loadStateList();

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadStateList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    private void loadStateList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "",
                "Fetching State.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

      /*  Cursor stateCursor = db.stateList();
        if (!(stateCursor.getCount() > 0)) {*/

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.STATE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray stateArray = jsonObject.getJSONArray("States");
                                    if (stateArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_STATE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_STATE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.STATE_ID + "," +
                                                DBAdapter.STATE_NAME + "," +
                                                DBAdapter.ROAD_PERMIT + "," +
                                                DBAdapter.MIN_ROAD_PERMIT_AMOUNT + ") VALUES (?,?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < stateArray.length(); i++) {
                                            JSONObject jObject = stateArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("StateId"));
                                            stmt.bindString(3, jObject.getString("StateName"));
                                            stmt.bindString(4, "No");
                                            stmt.bindString(5, "0");
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "No State Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        loadCityList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadCityList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;

            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
        /*} else {
            loadCityList(accountId);
        }
        stateCursor.close();*/

    }

    private void loadCityList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "",
                "Fetching City.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
       /* Cursor cityCursor = db.cityList();
        if (!(cityCursor.getCount() > 0)) {
*/
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.CITY_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("City Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray cityArray = jsonObject.getJSONArray("Cities");
                                    if (cityArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_CITY);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_CITY + "(" + DBAdapter.ID + "," +
                                                DBAdapter.CITY_ID + "," +
                                                DBAdapter.CITY_NAME + "," +
                                                DBAdapter.STATE_ID + ") VALUES (?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < cityArray.length(); i++) {
                                            JSONObject jObject = cityArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("CityId"));
                                            stmt.bindString(3, jObject.getString("CityName"));
                                            stmt.bindString(4, jObject.getString("StateId"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "City Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        loadMaterialList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                loadMaterialList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        int x = 2;// retry count
        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
        /*} else {
            loadMaterialList(accountId);
        }*/
    }

    private void loadMaterialList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "", "Fetching Materials.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Material Called");
        if (prefs == null) {
            prefs = TransporterMainActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.MATERIAL_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Material Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("MaterialType");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_MATERIAL);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_MATERIAL + "(" + DBAdapter.ID + "," +
                                                DBAdapter.MATERIAL_ID + "," +
                                                DBAdapter.MATERIAL_NAME + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("MaterialTypeId"));
                                            stmt.bindString(3, jObject.getString("MaterialTypeName"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "Material Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                        }
                        //
                        documentTitleList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                // vehicalModelList();
                documentTitleList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    private void documentTitleList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "",
                "Fetching Document Titles.....", true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Title Called");
        if (prefs == null) {
            prefs = TransporterMainActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DOCUMENT_TITLE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehical Model Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("DocumentTitleList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_DOCUMENT_TITLE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_DOCUMENT_TITLE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.DOCUMENT_TITLE + "," +
                                                DBAdapter.DOCUMENT_TITLE_ID + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("DocumentTitle"));
                                            stmt.bindString(3, jObject.getString("DocumentTitleId"));
                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "Document Title Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        vehicalTypeList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                vehicalTypeList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void vehicalTypeList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "", "Fetching Vehical Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Vehical Type Called");
        if (prefs == null) {
            prefs = TransporterMainActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICAL_TYPE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Vehical Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("VehicleTypeList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_VEHICAL_TYPE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_VEHICAL_TYPE + "(" + DBAdapter.ID + "," +
                                                DBAdapter.VEHICAL_TYPE + ") VALUES (?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("VehicleType"));

                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "Vehical type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                            driverList();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                            driverList();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                driverList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void driverList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "",
                "Fetching Vehical Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load driver Type Called");
        if (prefs == null) {
            prefs = TransporterMainActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DRIVER_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Driver Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray materialArray = jsonObject.getJSONArray("DriverList");
                                    if (materialArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_DRIVER);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_DRIVER + "(" + DBAdapter.ID + "," +
                                                DBAdapter.DRIVER_ID + "," +
                                                DBAdapter.DRIVER_NAME + "," +
                                                DBAdapter.DRIVER_DL + "," +
                                                DBAdapter.DRIVER_EMAIL + "," +
                                                DBAdapter.DRIVER_AGE + "," +
                                                DBAdapter.DRIVER_PHONE + "," +
                                                DBAdapter.DRIVER_SEX + "," +
                                                DBAdapter.DRIVER_REMARK + ") VALUES (?,?,?,?,?,?,?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < materialArray.length(); i++) {
                                            JSONObject jObject = materialArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("driver_id"));
                                            stmt.bindString(3, jObject.getString("name"));
                                            stmt.bindString(4, jObject.getString("dl_number"));
                                            stmt.bindString(5, jObject.getString("email"));
                                            stmt.bindString(6, jObject.getString("age"));
                                            stmt.bindString(7, jObject.getString("phone_no"));
                                            stmt.bindString(8, jObject.getString("sex"));
                                            stmt.bindString(9, jObject.getString("remarks"));


                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "Vehical type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                            expenseTypeList();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();

                            expenseTypeList();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                expenseTypeList();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }


    private void expenseTypeList() {

        final ProgressDialog dialog = ProgressDialog.show(TransporterMainActivity.this, "",
                "Fetching Expense Type.....", true);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        System.out.println("Load Expense Type Called");
        if (prefs == null) {
            prefs = TransporterMainActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        final String accountType = (prefs.getString(AppController.PREFERENCE_ACCOUNT_TYPE, "0"));


        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.EXPENSE_TYPE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String materialResponse) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        try {
                            System.out.println("Expense Type Response : " + materialResponse);
                            JSONObject jsonObject = new JSONObject(materialResponse);

                            if (jsonObject.has("Result")) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray expenseArray = jsonObject.getJSONArray("ExpenseTypeList");
                                    if (expenseArray.length() > 0) {

                                        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
                                        SqliteDB.beginTransaction();
                                        db.db.execSQL("delete from " + DBAdapter.TABLE_EXPENSE_TYPE);
                                        String query = "INSERT INTO " + DBAdapter.TABLE_EXPENSE_TYPE + "(" + DBAdapter.ID + "," +  DBAdapter.EXPENSE_TYPE_ID + "," +  DBAdapter.EXPENSE_TYPE + ") VALUES (?,?,?)";

                                        SQLiteStatement stmt = SqliteDB.compileStatement(query);

                                        for (int i = 0; i < expenseArray.length(); i++) {
                                            JSONObject jObject = expenseArray.getJSONObject(i);
                                            stmt.bindString(1, "" + i);
                                            stmt.bindString(2, jObject.getString("ExpenseTypeId"));
                                            stmt.bindString(3, jObject.getString("ExpenseTypeName"));

                                            stmt.execute();
                                        }

                                        SqliteDB.setTransactionSuccessful();
                                        SqliteDB.endTransaction();
                                    } else {
                                        Toast.makeText(TransporterMainActivity.this, "Expense type Not Found", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(TransporterMainActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TransporterMainActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }

                            startMainActivity();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TransporterMainActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                            startMainActivity();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if (dialog != null) {
                    dialog.dismiss();
                }
                startMainActivity();
                Toast.makeText(TransporterMainActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
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
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };


        AppController.getInstance().addToRequestQueue(stringVarietyRequest);


    }

    public void startMainActivity(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Completed");
        builder.setMessage("Successfully sync your project.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();


            }
        });
        builder.show();
    }


}