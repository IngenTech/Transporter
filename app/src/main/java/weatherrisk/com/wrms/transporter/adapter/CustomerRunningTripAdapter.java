package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.DetailActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.dataobject.ContentData;
import weatherrisk.com.wrms.transporter.dataobject.CustomerRunningTripData;
import weatherrisk.com.wrms.transporter.fragment.ContentDataFragment;
import weatherrisk.com.wrms.transporter.orders_action_activity.AlertActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.DistanceReportActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.HaltReportActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.LiveMapActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.Navigation;
import weatherrisk.com.wrms.transporter.orders_action_activity.TrackMapActivity;
import weatherrisk.com.wrms.transporter.orders_action_activity.TravelReportActivity;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 05-12-2016.
 */

public class CustomerRunningTripAdapter extends BaseAdapter {

    public static final String TITLE = "Confirm Orders";

    /***********
     * Declare Used Variables
     *********/
    private Activity activity;
    private ArrayList<CustomerRunningTripData> data;
    private static LayoutInflater inflater = null;
    CustomerRunningTripData tempValues = null;
    int i = 0;
    SharedPreferences prefs;

    /*************
     * CustomAdapter Constructor
     *****************/
    public CustomerRunningTripAdapter(Activity context, ArrayList<CustomerRunningTripData> data) {
        this.activity = context;
        this.data = data;

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

        public TextView titleTxt;
        public TextView rightTitleTxt;
        public TextView rightContent1Txt;
        public TextView rightContent2Txt;
        public TextView content3Txt;
        public TextView content1Txt;
        public TextView content2Txt;
        public Button action1Button;
        public Button action2Button;
        public Button viewDocument;
        public LinearLayout row_item;
    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.customer_running_trip_card, null);

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
            holder.action1Button = (Button) vi
                    .findViewById(R.id.action1Button);
            holder.action2Button = (Button) vi
                    .findViewById(R.id.action2Button);

            holder.viewDocument = (Button) vi.findViewById(R.id.actionviewButton);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.titleTxt.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (CustomerRunningTripData) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.titleTxt.setText(tempValues.getVehicleNo());
            holder.rightTitleTxt.setText(tempValues.getDocketNo());
            holder.rightContent1Txt.setText("Order Date:  "+tempValues.getOrderDate());
            holder.rightContent2Txt.setText(tempValues.getTransporterName());
            holder.content3Txt.setText(tempValues.getDispatchDate());
            holder.content1Txt.setText(tempValues.getFromCityName());
            holder.content2Txt.setText(tempValues.getToCityName());
            holder.action1Button.setOnClickListener(new OnItemClickListener(position));
            holder.action2Button.setOnClickListener(new OnItemClickListener(position));
            holder.viewDocument.setOnClickListener(new OnItemClickListener(position));

            holder.row_item = (LinearLayout) vi.findViewById(R.id.running_row);
            holder.row_item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
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
                    title.setText("Your order no.  " + data.get(position).getDocketNo());

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

                    travel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(activity, TravelReportActivity.class);

                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    track.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(activity, TrackMapActivity.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    live.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(activity, LiveMapActivity.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    distance.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent in = new Intent(activity, DistanceReportActivity.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    halt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent in = new Intent(activity, HaltReportActivity.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    alert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(activity, AlertActivity.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);
                        }
                    });

                    navigation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent in = new Intent(activity, Navigation.class);
                            in.putExtra("trip_id", data.get(position).getTripId());
                            activity.startActivity(in);

                        }
                    });

                    return false;
                }
            });

        }
        return vi;
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            final CustomerRunningTripData customerRunningTripData = data.get(mPosition);
            if (arg0.getId() == R.id.action1Button) {
                Intent uploadDocumentIntent = new Intent(activity, UploadDocumentActivity.class);
                uploadDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerRunningTripData.getDocketNo());
                activity.startActivity(uploadDocumentIntent);
            }
            if (arg0.getId() == R.id.action2Button) {

                ArrayList<ContentData> contentDatas = customerRunningTripData.getContentData();

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("content", (ArrayList<ContentData>) contentDatas);

                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
            }

            if (arg0.getId() == R.id.actionviewButton) {
                Intent viewDocumentIntent = new Intent(activity, ViewDocumentActivity.class);
                viewDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerRunningTripData.getDocketNo());
                activity.startActivity(viewDocumentIntent);
            }
        }
    }

    ProgressDialog dialog;

    private void cancelOrder(final CustomerRunningTripData customerConfirmOrder) {

        System.out.println("close docketNo get called" + customerConfirmOrder.getDocketNo());
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
                                    CustomerRunningTripAdapter.this.notifyDataSetChanged();

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
                if (prefs == null) {
                    prefs = activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accssToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("UserId", accountId);
                map.put("AccessToken", accssToken);
                map.put("OrderId", customerConfirmOrder.getDocketNo());
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
