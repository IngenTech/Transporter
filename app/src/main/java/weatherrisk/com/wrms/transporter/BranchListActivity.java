package weatherrisk.com.wrms.transporter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.BranchListAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.ViewExpenseAdapter;
import weatherrisk.com.wrms.transporter.bean.BranchListBean;
import weatherrisk.com.wrms.transporter.bean.ExpenseBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 10-05-2017.
 */
public class BranchListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<BranchListBean> branch_list = new ArrayList<BranchListBean>();
    DBAdapter db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.branch_list_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(this);
        db.open();

        recyclerView = (RecyclerView)findViewById(R.id.brach_list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        getBranchList();


    }


    @Override
    protected void onRestart() {
        super.onRestart();
        getBranchList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_branch, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_branch) {
            Intent intent = new Intent(getApplicationContext(), AddBranchActivity.class);
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

    private void getBranchList() {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.BRANCH_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.cancel();
                        try {
                            System.out.println("branch list Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be fetch";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                    branch_list = new ArrayList<BranchListBean>();

                                    JSONArray jsonArray = jsonObject.getJSONArray("BranchList");

                                    for (int i =0 ; i<jsonArray.length();i++){
                                        JSONObject jObject = jsonArray.getJSONObject(i);
                                        BranchListBean bean = new BranchListBean();
                                        bean.setSerial(jObject.getString("serial"));
                                        bean.setFirm_name(jObject.getString("firm_name"));
                                        bean.setContact_name(jObject.getString("contact_name"));
                                        bean.setPersonal_contact_no(jObject.getString("personal_contact_no"));
                                        bean.setHome_contact_no(jObject.getString("home_contact_no"));
                                        bean.setOffice_contact_no(jObject.getString("office_contact_no"));
                                        bean.setTin_no(jObject.getString("tin_no"));
                                        bean.setCountry_id(jObject.getString("country_id"));
                                        bean.setAddress(jObject.getString("address"));
                                        bean.setEmail_id(jObject.getString("email_id"));
                                        String state_ID = jObject.getString("state_id");
                                        String city_ID = jObject.getString("city_id");
                                        Cursor stateName = db.stateById(state_ID);
                                        Cursor cityName = db.cityById(city_ID);

                                        if (stateName.moveToFirst()) {
                                            do {

                                                bean.setStateName(stateName.getString(stateName.getColumnIndex(DBAdapter.STATE_NAME)));

                                            } while (stateName.moveToNext());
                                        }

                                        if (cityName.moveToFirst()) {
                                            do {

                                                bean.setCityName(cityName.getString(cityName.getColumnIndex(DBAdapter.CITY_NAME)));

                                            } while (cityName.moveToNext());
                                        }

                                        branch_list.add(bean);

                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                            }


                            if (branch_list.size()>0) {
                                BranchListAdapter adapter = new BranchListAdapter(BranchListActivity.this,branch_list);
                                recyclerView.setAdapter(adapter);
                            }else {
                                NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
                                recyclerView.setAdapter(adapter_no);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();

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

        dialog = ProgressDialog.show(this, "","Fetching Branch List.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


}
