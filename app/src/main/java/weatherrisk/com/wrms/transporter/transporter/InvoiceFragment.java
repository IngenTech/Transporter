package weatherrisk.com.wrms.transporter.transporter;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 21-03-2017.
 */
public class InvoiceFragment   extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    InvoiceAdapter invoiceAdapter;
   // ArrayList<String> myDataset = new ArrayList<String>();
   InvoiceBean data_invoice;

    ArrayList<InvoiceBean> invoiceList;


    public InvoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.invoice_fragment, container, false);

      /*  myDataset = new ArrayList<String>();
        myDataset.add("Lucknow");
        myDataset.add("Meerut");
        myDataset.add("Gorakhpur");
        myDataset.add("Noida");
        myDataset.add("Gurgaon");
*/
        invoiceList = new ArrayList<InvoiceBean>();

        recyclerView = (RecyclerView) view.findViewById(R.id.invoice_list);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        String orderNo = getActivity().getIntent().getStringExtra(UploadDocumentActivity.ORDER_NO);
        viewInviceList(orderNo);


        return view;
    }

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void viewInviceList(final String orderNo) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VIEW_INVICE_LIST,
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

                                        if (jsonObject1.has("ImageString")){
                                            data_invoice.setImage(jsonObject1.getString("ImageString"));
                                        }

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
                                // Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();
                            }
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
                    prefs =getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

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
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }

}