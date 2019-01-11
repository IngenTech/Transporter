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
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 21-03-2017.
 */
public class RoadPermitFragment   extends Fragment {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    RoadPermitAdapter roadAdapter;
    RoadPermitBean data_road;
    ArrayList<RoadPermitBean> roadList;

    // ArrayList<String> myDataset = new ArrayList<String>();


    public RoadPermitFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roadpermit_layout, container, false);

        roadList = new ArrayList<RoadPermitBean>();

        recyclerView = (RecyclerView) view.findViewById(R.id.road_list);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        String orderNO = getActivity().getIntent().getStringExtra(UploadDocumentActivity.ORDER_NO);
        viewRoadList(orderNO);

        return view;
    }

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void viewRoadList(final String orderNo) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.VIEW_ROADPERMIT_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Road permit Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                roadList = new ArrayList<RoadPermitBean>();


                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray jsonArray = jsonObject.getJSONArray("RoadPermits");
                                    for (int j =0 ;j<jsonArray.length();j++){
                                        data_road = new RoadPermitBean();
                                        JSONObject jsonObject1 = jsonArray.getJSONObject(j);

                                        data_road.setPermitID(jsonObject1.getString("PermitId"));
                                        data_road.setCreateDate(jsonObject1.getString("CreateDate"));
                                        data_road.setRemark(jsonObject1.getString("Remark"));
                                        if (jsonObject1.has("ImageString")) {
                                            data_road.setImage(jsonObject1.getString("ImageString"));
                                        }

                                        roadList.add(data_road);
                                    }


                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                              //  Toast.makeText(getActivity(), ""+message, Toast.LENGTH_LONG).show();
                                                 }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }

                        if (roadList.size()>0) {
                            roadAdapter = new RoadPermitAdapter(getActivity(),roadList);
                            recyclerView.setAdapter(roadAdapter);
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
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("OrderNo", orderNo);

                return map;
            }
        };

        dialog = ProgressDialog.show(getActivity(), "", "Fetching Road Permits.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


}