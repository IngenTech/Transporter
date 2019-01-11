package weatherrisk.com.wrms.transporter.vehicalDocument;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.VehicalManageAdapter;
import weatherrisk.com.wrms.transporter.bean.VehicalListBean;
import weatherrisk.com.wrms.transporter.bean.VehicleDocumentBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 24-04-2017.
 */
public class ViewVehicalDocuments extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<VehicleDocumentBean> vehicleDocumentList = new ArrayList<VehicleDocumentBean>();
    DBAdapter db ;
    String  vehicleId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_vehical_document);

        db = new DBAdapter(this);
        db.open();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        vehicleId = getIntent().getStringExtra("vehicleId");

        recyclerView = (RecyclerView)findViewById(R.id.vehical_document_list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        viewVehicleDocuments();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void viewVehicleDocuments() {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VIEW_VEHICLE_DOCUMENTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("View Vehicle Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                                    JSONArray jsonArray = jsonObject.getJSONArray("DocumentList");

                                    vehicleDocumentList = new ArrayList<VehicleDocumentBean>();
                                    for (int i =0 ; i<jsonArray.length();i++){
                                        JSONObject jObject = jsonArray.getJSONObject(i);
                                        VehicleDocumentBean bean = new VehicleDocumentBean();
                                        bean.setDocumentId(jObject.getString("DocumentId"));
                                        bean.setDocumentTitleId(jObject.getString("DocumentTitleId"));

                                        String title_ID = jObject.getString("DocumentTitleId");
                                        Cursor titleCursor = db.documentTitleListById(title_ID);

                                        if (titleCursor.moveToFirst()) {
                                            do {

                                                bean.setTitle(titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.DOCUMENT_TITLE)));

                                            } while (titleCursor.moveToNext());
                                        }

                                        bean.setImage(jObject.getString("Image"));
                                        vehicleDocumentList.add(bean);

                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Fetch";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Fetch";
                                Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                            }


                            if (vehicleDocumentList.size()>0) {
                                ViewVehicalAdapter adapter = new ViewVehicalAdapter(ViewVehicalDocuments.this,vehicleDocumentList);
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
                map.put("VehicleId", vehicleId);


                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(this, "","Fetching vehicle Documents.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


}
