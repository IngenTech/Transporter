package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.DetailActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.dataobject.ContentData;
import weatherrisk.com.wrms.transporter.dataobject.CustomerTripHistoryData;
import weatherrisk.com.wrms.transporter.fragment.ContentDataFragment;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 05-12-2016.
 */

public class CustomerTripHistoryAdapter extends BaseAdapter {

    public static final String TITLE = "Confirm Orders";

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<CustomerTripHistoryData> data;
    private static LayoutInflater inflater=null;
    CustomerTripHistoryData tempValues=null;
    int i=0;
    SharedPreferences prefs;

    /*************  CustomAdapter Constructor *****************/
    public CustomerTripHistoryAdapter(Activity context, ArrayList<CustomerTripHistoryData> data) {
        this.activity = context;
        this.data=data;

        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView titleTxt;
        public TextView rightTitleTxt;
        public TextView rightContent1Txt;
        public TextView rightContent2Txt;
        public TextView content3Txt;
        public TextView content1Txt;
        public TextView content2Txt;
        public Button action2Button;
        public Button viewDocument;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.customer_trip_history_card, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.titleTxt = (TextView) vi
                    .findViewById(R.id.titleTxt);
            holder.rightTitleTxt = (TextView) vi
                    .findViewById(R.id.rightTitleTxt);
            holder.rightContent1Txt = (TextView) vi
                    .findViewById(R.id.rightContent1Txt);
            holder.rightContent2Txt = (TextView) vi
                    .findViewById(R.id.rightContent2Txt);
            holder.content3Txt = (TextView) vi
                    .findViewById(R.id.content3Txt);
            holder.content1Txt = (TextView) vi
                    .findViewById(R.id.content1Txt);
            holder.content2Txt = (TextView) vi
                    .findViewById(R.id.content2Txt);
            holder.action2Button = (Button) vi.findViewById(R.id.action2Button);
            holder.viewDocument = (Button) vi.findViewById(R.id.actionviewButton);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.titleTxt.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( CustomerTripHistoryData ) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.titleTxt.setText(tempValues.getVehicleNo());
            holder.rightTitleTxt.setText(tempValues.getDocketNo());
            holder.rightContent1Txt.setText(tempValues.getOrderDate());
            holder.rightContent2Txt.setText(tempValues.getTransporterName());
            holder.content3Txt.setText(tempValues.getDispatchDate());
            holder.content1Txt.setText(tempValues.getFromCityName());
            holder.content2Txt.setText(tempValues.getToCityName());
            holder.action2Button.setOnClickListener(new OnItemClickListener(position));

            holder.viewDocument.setOnClickListener(new OnItemClickListener(position));

        }
        return vi;
    }

    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            final CustomerTripHistoryData customerConfirmOrder = data.get(mPosition);
            if(arg0.getId()==R.id.action2Button){
                ArrayList<ContentData> contentDatas = customerConfirmOrder.getContentData();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("content",(ArrayList<ContentData>) contentDatas);
                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }
            if (arg0.getId() == R.id.actionviewButton) {
                Intent viewDocumentIntent = new Intent(activity, ViewDocumentActivity.class);
                viewDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerConfirmOrder.getDocketNo());
                activity.startActivity(viewDocumentIntent);
            }

        }
    }
    ProgressDialog dialog;
    private void cancelOrder(final CustomerTripHistoryData customerConfirmOrder) {

        System.out.println("close docketNo get called"+customerConfirmOrder.getDocketNo());
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.CANCEL_ORDER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {

                                    data.remove(customerConfirmOrder);
                                    CustomerTripHistoryAdapter.this.notifyDataSetChanged();

                                } else {
                                    Toast.makeText(activity, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(activity, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(activity, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");


                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                map.put("docketNo", customerConfirmOrder.getDocketNo());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(activity, "",
                "Closing.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


}
