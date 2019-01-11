package weatherrisk.com.wrms.transporter.expenses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.ViewExpenseAdapter;
import weatherrisk.com.wrms.transporter.bean.ExpenseBean;
import weatherrisk.com.wrms.transporter.transporter.AddDriverExpenseActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

/**
 * Created by Admin on 24-04-2017.
 */
public class VehicalExpensesActivity extends AppCompatActivity {

    private RecyclerView.LayoutManager mLayoutManager;
    RecyclerView listView;

    ArrayList<ExpenseBean> expenseList = new ArrayList<ExpenseBean>();
    DBAdapter db;
    TextView totalAmount;
    int ttlamnt = 0;
    TextView date1, date2;
    Button searchHistory;
    RelativeLayout toDate, fromDate;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    String currentDate,afterDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.driver_expenses_activity);

        db = new DBAdapter(this);
        db.open();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        listView = (RecyclerView)findViewById(R.id.driver_expense_list);
        totalAmount = (TextView)findViewById(R.id.total_amount);

        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        date1 = (TextView) findViewById(R.id.date1);
        date2 = (TextView) findViewById(R.id.date2);
        searchHistory = (Button) findViewById(R.id.search_history);
        toDate = (RelativeLayout) findViewById(R.id.toDate);
        fromDate = (RelativeLayout) findViewById(R.id.fromDate);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = df.format(c.getTime());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, +20);
        Date aaaafftt = cal.getTime();
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        afterDate = df1.format(aaaafftt);

        Log.v("date","before"+currentDate+"after"+afterDate);

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(VehicalExpensesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(year)) + "-" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "-" + mFormat.format(Double.valueOf(dayOfMonth));

                        date2.setText(Dates);


                    }
                }, mYear, mMonth, mDay);
                //    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear1 = c.get(Calendar.YEAR);
                mMonth1 = c.get(Calendar.MONTH);
                mDay1 = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(VehicalExpensesActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(year)) + "-" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "-" + mFormat.format(Double.valueOf(dayOfMonth));

                        date1.setText(Dates);


                    }
                }, mYear1, mMonth1, mDay1);
                //  dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });

        searchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String from_date = date1.getText().toString().trim();
                String to_date = date2.getText().toString().trim();

                if (from_date == null || from_date.length() < 7) {
                    Toast.makeText(getApplicationContext(), "Please select from date", Toast.LENGTH_SHORT).show();
                } else if (to_date == null || to_date.length() < 7) {
                    Toast.makeText(getApplicationContext(), "Please select to date", Toast.LENGTH_SHORT).show();
                } else {
                    driverExpenseList();
                }
            }
        });

        driverExpenseList();


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_expense, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_expense) {

            Intent intent = new Intent(getApplicationContext(), AddVehicleExpens.class);
            startActivity(intent);

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void driverExpenseList() {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VEHICLE_EXPENSE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("View Expense list Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                ttlamnt = 0;

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be fetch";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                    expenseList = new ArrayList<ExpenseBean>();

                                    JSONArray jsonArray = jsonObject.getJSONArray("VehicleExpenseList");

                                    for (int i =0 ; i<jsonArray.length();i++){
                                        JSONObject jObject = jsonArray.getJSONObject(i);
                                        ExpenseBean bean = new ExpenseBean();
                                        bean.setId(jObject.getString("vehicle_id"));
                                        bean.setExpense_amount(jObject.getString("expense_amount"));
                                        bean.setExpense_detail(jObject.getString("expense_detail"));
                                        bean.setExpenseTypeID(jObject.getString("expense_type_id"));
                                        bean.setBill_image(jObject.getString("scan_bill"));
                                        bean.setBillDate(jObject.getString("bill_date"));
                                        bean.setPaidBy(jObject.getString("paid_by"));
                                        bean.setRemarks(jObject.getString("remarks"));

                                        String title_ID = jObject.getString("expense_type_id");
                                        Cursor titleCursor = db.expenseTypeByID(title_ID);

                                        if (titleCursor.moveToFirst()) {
                                            do {

                                                bean.setTitle(titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.EXPENSE_TYPE)));

                                            } while (titleCursor.moveToNext());
                                        }
                                        expenseList.add(bean);


                                        String ammm = bean.getExpense_amount().trim();
                                        if (ammm!=null){

                                            ttlamnt = ttlamnt + Integer.parseInt(ammm);
                                        }

                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                            }


                            if (expenseList.size()>0) {

                                totalAmount.setVisibility(View.VISIBLE);

                                totalAmount.setText("Total Expense amount = "+ttlamnt);

                                ExpensesAdapter adapter = new ExpensesAdapter(VehicalExpensesActivity.this,expenseList);
                                listView.setAdapter(adapter);
                            }else {
                                NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
                                listView.setAdapter(adapter_no);
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

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(this, "","Fetching Expense List.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }



}
