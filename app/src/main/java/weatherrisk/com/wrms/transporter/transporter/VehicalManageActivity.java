package weatherrisk.com.wrms.transporter.transporter;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.ChangePasswordActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.VehicalManageAdapter;
import weatherrisk.com.wrms.transporter.bean.VehicalListBean;

/**
 * Created by Admin on 22-04-2017.
 */
public class VehicalManageActivity extends AppCompatActivity {

    RecyclerView listView;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<VehicalListBean> vehicalList = new ArrayList<VehicalListBean>();
    RelativeLayout addVehicalBTN;

    DBAdapter db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehical_manage_activity);

        db = new DBAdapter(this);
        db.open();

        listView = (RecyclerView)findViewById(R.id.manage_vehical_list);
        listView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        addVehicalBTN = (RelativeLayout)findViewById(R.id.addVehicalBtn);
        addVehicalBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in  = new Intent(getApplicationContext(),AddVehicalActivity.class);
                startActivity(in);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        vehicalList = new ArrayList<VehicalListBean>();

        final Cursor vehicleListCursor = db.getVehicle();

        if (vehicleListCursor.moveToFirst()) {
            do {
                VehicalListBean bean = new VehicalListBean();


                String id = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.VEHICLE_ID));
                bean.setVehicle_id(id);
                String imei = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.IMEI));

                String model = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.MODEL_NO));
                bean.setModel_no(model);
                String reg = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.REGISTRATION_NO));
                bean.setRegistration_no(reg);
                String insu = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.INSURANCE_NO));
                bean.setInsurance_no(insu);
                String valid = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.VALIDITY_DATE));
                bean.setInsurance_validity(valid);
                String pollution = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.POLLUTION_NO));
                bean.setPollution_no(pollution);
                String y_o_p = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.YEAR_OF_PURCHASE));
                bean.setYear_of_purchase(y_o_p);
                String capacity = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.CAPACITY));
                bean.setCapacity(capacity);
                String refrig = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.REFRIGERATED));
                bean.setRefrigerated_nonRefrigerated(refrig);
                String closedoor = vehicleListCursor.getString(vehicleListCursor.getColumnIndex(DBAdapter.CLOSED_DORE));
                bean.setClose_open(closedoor);




                vehicalList.add(bean);
            } while (vehicleListCursor.moveToNext());
        }



        if (vehicalList.size()>0) {
           VehicalManageAdapter invoiceAdapter = new VehicalManageAdapter(this,vehicalList);
            listView.setAdapter(invoiceAdapter);
        }else {
            NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
            listView.setAdapter(adapter_no);
        }
    }


    /*
     ProgressDialog dialog;
    SharedPreferences prefs;

    private void viewInviceList(final String orderNo) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, AppConstant.API.VIEW_INVICE_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Invoice Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                String title = "INVOICE";

                                invoiceList = new ArrayList<InvoiceBean>();


                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray js = jsonObject.getJSONArray("Invoices");
                                    for (int j = 0;j<js.length();j++){

                                        data_invoice = new InvoiceBean();

                                        JSONObject jsonObject1  = js.getJSONObject(j);

                                        data_invoice.setInvoiceID(jsonObject1.getString("InvoiceId"));
                                        data_invoice.setInvoiceNo(jsonObject1.getString("InvoiceNo"));
                                        data_invoice.setAmount(jsonObject1.getString("Amount"));
                                        data_invoice.setInvoiceDate(jsonObject1.getString("InvoiceDate"));

                                        JSONArray js1 = jsonObject1.getJSONArray("Materials");
                                        for (int j1 =0;j1< js1.length();j1++){

                                        }
                                        data_invoice.setNoOFMatterial(String.valueOf(js1.length()));

                                        invoiceList.add(data_invoice);
                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        if (invoiceList.size()>0) {
                            invoiceAdapter = new InvoiceAdapter(getActivity(),invoiceList);
                            recyclerView.setAdapter(invoiceAdapter);
                        }else {
                            NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
                            recyclerView.setAdapter(adapter_no);
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
                    prefs =getActivity().getSharedPreferences(AppConstant.Preference.APP_PREFERENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppConstant.Preference.USER_ID, "");
                String accessToken = prefs.getString(AppConstant.Preference.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("OrderNo", orderNo);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                return map;
            }
        };

        dialog = ProgressDialog.show(getActivity(), "",
                "Fetching Invoice.....", true);
        NimbuMirchiApplication.getInstance().addToRequestQueue(viewDocRequest);

    }

     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vehical_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_vehical) {

            Intent intent = new Intent(getApplicationContext(), AddVehicalActivity.class);
            startActivity(intent);

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
