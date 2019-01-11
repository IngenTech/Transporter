package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.CustomerPendingOrder;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 03-12-2016.
 */

public class CustomerPendingOrderAdapter extends BaseAdapter implements View.OnClickListener {

    public static final String TITLE = "Confirm Orders";

    /***********
     * Declare Used Variables
     *********/
    private Activity activity;
    private ArrayList<CustomerPendingOrder> data;
    private static LayoutInflater inflater = null;
    CustomerPendingOrder tempValues = null;

    SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat requiredDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    int i = 0;

    SharedPreferences prefs;
    /*************
     * CustomAdapter Constructor
     *****************/
    public CustomerPendingOrderAdapter(Activity context, ArrayList<CustomerPendingOrder> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data = data;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {

        public TextView txtTitle;
        public TextView txtRightTitle;
        public TextView txtRightContent1;
        public TextView txtContent1;
        public TextView txtContent2;
        public TextView txtContent3;
        public Button action1;
        public Button viewDocument;
        public Button action2;

        public LinearLayout row_item;

    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

//        if (convertView == null) {

        /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
        if (convertView == null)
            vi = inflater.inflate(R.layout.pending_order_card, null);

        /****** View Holder Object to contain tabitem.xml file elements ******/

        holder = new ViewHolder();
        holder.txtTitle = (TextView) vi.findViewById(R.id.titleTxt);
        holder.txtRightTitle = (TextView) vi.findViewById(R.id.rightTitleTxt);
        holder.txtRightContent1 = (TextView) vi.findViewById(R.id.rightContect1Txt);
        holder.txtContent1 = (TextView) vi.findViewById(R.id.content1Txt);
        holder.txtContent2 = (TextView) vi.findViewById(R.id.content2Txt);
        holder.txtContent3 = (TextView) vi.findViewById(R.id.content3Txt);
        holder.txtContent3.setVisibility(View.VISIBLE);
        holder.action1 = (Button) vi.findViewById(R.id.action1Button);
        holder.viewDocument = (Button) vi.findViewById(R.id.actionviewButton);
        holder.action2 = (Button) vi.findViewById(R.id.action2Button);

        holder.row_item = (LinearLayout)vi.findViewById(R.id.pending_row);
        /*holder.row_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final Dialog dialog = new Dialog(activity,android.R.style.Theme_Translucent_NoTitleBar);
                // Include dialog.xml file
                dialog.setContentView(R.layout.order_list_popup);
                dialog.show();

                WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();

                //   WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                //   lp.copyFrom(dialog.getWindow().getAttributes());

                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.BOTTOM;
                lp.dimAmount = 0.3f;

                dialog.getWindow().setAttributes(lp);
                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);

                TextView title = (TextView) dialog.findViewById(R.id.title_popup);
                title.setText("Your order no.  "+data.get(position).getDocketNo());

                Button track = (Button) dialog.findViewById(R.id.track_popup);
                Button live = (Button) dialog.findViewById(R.id.live_popup);
                Button travel = (Button) dialog.findViewById(R.id.travel_popup);
                Button halt = (Button) dialog.findViewById(R.id.halt_popup);
                Button navigation = (Button) dialog.findViewById(R.id.navigation_popup);
                Button distance = (Button) dialog.findViewById(R.id.distance_popup);
                Button alert = (Button) dialog.findViewById(R.id.alert_popup);
                ImageView close = (ImageView) dialog.findViewById(R.id.close_popup);

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

              *//*  travel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(activity, TravelReportActivity.class);
                        activity.startActivity(in);
                    }
                });

                track.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent in = new Intent(activity, TrackMapActivity.class);
                        in.putExtra("travel_id",data.get(position).getDocketNo());
                        activity.startActivity(in);
                    }
                });*//*





                return false;
            }
        });*/


        /************  Set holder with LayoutInflater ************/
            /*vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();*/

        /*if(data.size()<=0){
            holder.txtTitle.setText("No Data");
        }else{*/
        /***** Get each Model object from Arraylist ********/
        if (data.size() > 0) {
            tempValues = null;
            tempValues = (CustomerPendingOrder) data.get(position);

            /************ Set Model values in Holder elements***********/

            holder.txtTitle.setText(tempValues.getDocketNo());
            holder.txtContent3.setText("Trip Date: "+tempValues.getTripDate());
            String startPlace = tempValues.getFromCityName();
            holder.txtContent1.setText(startPlace);
            String endPlace = tempValues.getToCityName();
            holder.txtContent2.setText(endPlace);
            String doorStatus = tempValues.getDoorStatus().contains("0") ? "Door Open" : "Door Close";
            String refrigeratedStatus = tempValues.getRefrigeratedStatus().contains("0") ? "Refrigerated Not" : "Refrigerated";
            holder.txtRightTitle.setText(tempValues.getCnt() + " / " + doorStatus + " / " + refrigeratedStatus);

            String orderDate = tempValues.getOrderDate();
            try {
                Date orderD = serverDateFormat.parse(tempValues.getOrderDate());
                orderDate = requiredDateFormat.format(orderD);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.txtRightContent1.setText(orderDate);

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            holder.viewDocument.setOnClickListener(new CustomerPendingOrderAdapter.OnItemClickListener(position));

            holder.action1.setOnClickListener(new CustomerPendingOrderAdapter.OnItemClickListener(position));
            holder.action2.setOnClickListener(new CustomerPendingOrderAdapter.OnItemClickListener(position));



        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            final CustomerPendingOrder customerPendingOrder = data.get(mPosition);
            if (customerPendingOrder != null) {

                if (arg0.getId() == R.id.action2Button) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("CANCEL");
                    alert.setMessage("Do you want to cancel the order?");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {cancelOrder(customerPendingOrder);
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                            // what ever you want to do with No option.
                        }
                    });
                    alert.show();

                }

                if (arg0.getId() == R.id.action1Button) {
                    Intent uploadDocumentIntent = new Intent(activity, UploadDocumentActivity.class);
                    uploadDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerPendingOrder.getDocketNo());
                    activity.startActivity(uploadDocumentIntent);
                }

                if (arg0.getId() == R.id.actionviewButton) {
                    Intent viewDocumentIntent = new Intent(activity, ViewDocumentActivity.class);
                    viewDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerPendingOrder.getDocketNo());
                    activity.startActivity(viewDocumentIntent);
                }

                if (arg0.getId() == R.id.pending_row){



                }

            } else {
                System.out.println("Trip data is null ");
            }
        }
    }

    ProgressDialog dialog;

    private void cancelOrder(final CustomerPendingOrder customerPendingOrder) {

        System.out.println("close docketNo get called" + customerPendingOrder.getDocketNo());
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.CANCEL_ORDER_API ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Status") &&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    data.remove(customerPendingOrder);
                                    CustomerPendingOrderAdapter.this.notifyDataSetChanged();

                                } else {
                                    String msg = jsonObject.getString("Message");
                                    Toast.makeText(activity, ""+msg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String msg = jsonObject.getString("Message");
                                Toast.makeText(activity, ""+msg, Toast.LENGTH_LONG).show();
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

                if (prefs == null) {
                    prefs = activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accssToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("UserId", accountId);
                map.put("AccessToken", accssToken);
                map.put("OrderId", customerPendingOrder.getDocketNo());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(activity, "",
                "Closing Trip.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


}
