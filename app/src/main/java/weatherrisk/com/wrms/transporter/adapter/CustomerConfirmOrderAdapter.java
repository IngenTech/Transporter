package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import weatherrisk.com.wrms.transporter.dataobject.CustomerConfirmOrder;
import weatherrisk.com.wrms.transporter.fragment.ContentDataFragment;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 02-12-2016.
 */

public class CustomerConfirmOrderAdapter extends BaseAdapter implements View.OnClickListener {

    public static final String TITLE = "Confirm Orders";

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<CustomerConfirmOrder> data;
    private static LayoutInflater inflater = null;
    CustomerConfirmOrder tempValues = null;
    int i = 0;

    /*************  CustomAdapter Constructor *****************/
    public CustomerConfirmOrderAdapter(Activity context, ArrayList<CustomerConfirmOrder> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data = data;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
    public static class ViewHolder {

        public TextView txtTitle;
        public TextView txtRightTitle;
        public TextView txtRightContent1;
        public TextView txtContent0;
        public TextView txtContent1;
        public TextView txtContent2;
        public TextView txtContent3;
        public Button action1;
        public Button action2;
        public TextView action3;
        public Button viewDocument;
        public LinearLayout detail_BTN;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.customer_confirm_order_card, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtTitle = (TextView) vi.findViewById(R.id.titleTxt);
            holder.txtRightTitle = (TextView) vi
                    .findViewById(R.id.rightTitleTxt);
            holder.txtRightContent1 = (TextView) vi
                    .findViewById(R.id.rightContect1Txt);
            holder.txtContent1 = (TextView) vi
                    .findViewById(R.id.content1Txt);
            holder.txtContent0 = (TextView) vi
                    .findViewById(R.id.content0Txt);
            holder.txtContent2 = (TextView) vi
                    .findViewById(R.id.content2Txt);
            holder.txtContent3 = (TextView) vi
                    .findViewById(R.id.content3Txt);
            holder.action1 = (Button) vi
                    .findViewById(R.id.action1Button);
            holder.action2 = (Button) vi
                    .findViewById(R.id.action2Button);
            holder.action3 = (TextView) vi.findViewById(R.id.action3Button);

            holder.viewDocument = (Button) vi
                    .findViewById(R.id.actionviewButton);
            holder.detail_BTN = (LinearLayout)vi.findViewById(R.id.detail_layout);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.txtTitle.setText("No Data");

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (CustomerConfirmOrder) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtTitle.setText(tempValues.getTransporterName());
            String startPlace = tempValues.getFromCityName();
            holder.txtContent1.setText(startPlace);
            holder.txtContent0.setText("Trip Date: "+tempValues.getTripDate());
            String endPlace = tempValues.getToCityName();
            holder.txtContent2.setText(endPlace);
            holder.txtRightTitle.setText(tempValues.getDocketNo());
            holder.txtRightContent1.setText(tempValues.getOrderDate());

            /******** Set Item Click Listner for LayoutInflater for each row *******/
            holder.action1.setOnClickListener(new CustomerConfirmOrderAdapter.OnItemClickListener(position));
            holder.action2.setOnClickListener(new CustomerConfirmOrderAdapter.OnItemClickListener(position));
        //    holder.action3.setOnClickListener(new CustomerConfirmOrderAdapter.OnItemClickListener(position));

            holder.viewDocument.setOnClickListener(new CustomerConfirmOrderAdapter.OnItemClickListener(position));

            holder.detail_BTN.setOnClickListener(new CustomerConfirmOrderAdapter.OnItemClickListener(position));

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

            final CustomerConfirmOrder customerConfirmOrder = data.get(mPosition);

            if (customerConfirmOrder != null) {

                if (arg0.getId() == R.id.action2Button) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                    alert.setTitle("Detail");
                    alert.setMessage("Do you want to start your trip?");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Toast.makeText(activity,"API not integrated",Toast.LENGTH_SHORT).show();
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
                    uploadDocumentIntent.putExtra(UploadDocumentActivity.ORDER_NO, customerConfirmOrder.getDocketNo());
                    activity.startActivity(uploadDocumentIntent);
                }

                if (arg0.getId() == R.id.detail_layout) {
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

            } else {
                System.out.println("Trip data is null ");
            }
        }
    }


    private void showChangeLangDialog(final CustomerConfirmOrder customerConfirmOrder) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_dialoug_layout, null);
        dialogBuilder.setView(dialogView);

        final EditText alert_dialoug_edt = (EditText) dialogView.findViewById(R.id.alert_dialoug_edt);
        alert_dialoug_edt.setHint("REMARK");
        dialogBuilder.setTitle("Cancel");
        dialogBuilder.setMessage("Do you want to cancel the order?");
        dialogBuilder.setPositiveButton("YES", null);
        dialogBuilder.setNegativeButton("NO", null);
        final AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        if (alert_dialoug_edt.getText() != null && alert_dialoug_edt.getText().toString().length() > 0) {
                            alertDialog.dismiss();
                            cancelOrder(customerConfirmOrder);
                        } else {
                            Toast.makeText(activity, "Please enter remark", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        alertDialog.show();
    }


    ProgressDialog dialog;

    private void cancelOrder(final CustomerConfirmOrder customerConfirmOrder) {

        System.out.println("close docketNo get called" + customerConfirmOrder.getDocketNo());
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.CANCEL_ORDER_API,
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
                                    CustomerConfirmOrderAdapter.this.notifyDataSetChanged();

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

//                map.put("status", customerConfirmOrder.getDocketNo());
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

    SharedPreferences prefs;

}