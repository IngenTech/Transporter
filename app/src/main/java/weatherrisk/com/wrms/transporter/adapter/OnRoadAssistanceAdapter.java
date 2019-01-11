package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.OnRoadAssistanceData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by WRMS on 08-04-2016.
 */
public class OnRoadAssistanceAdapter extends BaseAdapter {

    /***********
     * Declare Used Variables
     *********/
    private Activity activity;
    private ArrayList<OnRoadAssistanceData> data;
    private static LayoutInflater inflater = null;
    OnRoadAssistanceData tempValues = null;
    int i = 0;

    /*************
     * CustomAdapter Constructor
     *****************/
    public OnRoadAssistanceAdapter(Activity context, ArrayList<OnRoadAssistanceData> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data = data;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {

        if (data.size() <= 0)
            return 1;
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

        public TextView txtVehicleName;
        public TextView txtDateOfBooking;
        public TextView txtContactNo;
        public TextView txtLandmark;
        public TextView txtProblem;
        /*public EditText edttxtFeedBack;*/
        public Button butClose;

    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.on_road_assistance_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.txtVehicleName = (TextView) vi
                    .findViewById(R.id.vehicleName);
            holder.txtDateOfBooking = (TextView) vi
                    .findViewById(R.id.dateOfBooking);
            holder.txtContactNo = (TextView) vi
                    .findViewById(R.id.contactNo);
            holder.txtLandmark = (TextView) vi
                    .findViewById(R.id.landmark);
            holder.txtProblem = (TextView) vi
                    .findViewById(R.id.problem);
            /*holder.edttxtFeedBack = (EditText) vi
                    .findViewById(R.id.feedBack);            holder.edttxtFeedBack.setVisibility(View.VISIBLE);*/

            holder.butClose = (Button) vi
                    .findViewById(R.id.close);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() > 0) {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (OnRoadAssistanceData) data.get(position);

            /************  Set Model values in Holder elements ***********/

            DBAdapter db = new DBAdapter(activity);
            db.open();

            Cursor vehicleC = db.getVehicleById(tempValues.getVehicleId());
            String vehicleString=null;

            Log.v("vehicleCount", "" + vehicleC.getCount());

            if (vehicleC.moveToFirst()) {
                do {
                     vehicleString = vehicleC.getString(vehicleC.getColumnIndex(DBAdapter.VEHICLE_NO));

                } while (vehicleC.moveToNext());
            }

            holder.txtVehicleName.setText(vehicleString);
            holder.txtDateOfBooking.setText(tempValues.getBookingDate());
            holder.txtContactNo.setText(tempValues.getContactNo());
            holder.txtLandmark.setText(tempValues.getLandmark());
            holder.txtProblem.setText(tempValues.getProblem());
            //   holder.edttxtFeedBack.setText(tempValues.getVehicleName());

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            holder.butClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeMethod(position);
                }
            });
        }
        return vi;
    }



        public void closeMethod(int mPosition) {


                final OnRoadAssistanceData assistanceData = data.get(mPosition);


                View view1;
                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view1 = inflater.inflate(R.layout.on_road_assistance_alert_item, null);


                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                final EditText edittext = (EditText) view1.findViewById(R.id.feedBack);

                TextView textView = (TextView) view1.findViewById(R.id.detail);
                textView.setText("Booking Data : " + assistanceData.getBookingDate());

                TextView textView1 = (TextView) view1.findViewById(R.id.detail1);
                textView1.setText("Contact No. : " + assistanceData.getContactNo());

                TextView textView2 = (TextView) view1.findViewById(R.id.detail2);
                textView2.setText("Landmark : " + assistanceData.getLandmark());

                TextView textView3 = (TextView) view1.findViewById(R.id.detail3);
                textView3.setText("Problem : " + assistanceData.getProblem());

                alert.setTitle("CLOSE");
                alert.setView(view1);
                final int finalPosition = mPosition;
                alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String feedBackString = edittext.getText().toString();

                        if (feedBackString != null && !feedBackString.isEmpty()) {

                            SharedPreferences prefs = activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                            ;
                            String bookingId = assistanceData.getBookingId();
                            assistanceData.setFeedBack(feedBackString);

                            String user_id = (prefs.getString(AppController.PREFERENCE_USER_ID, "0"));
                            String access_token = (prefs.getString(AppController.ACCESS_TOKEN, "0"));

                            closeOnRoadAssistanceRequest(user_id,access_token, feedBackString, bookingId, finalPosition);

                        } else {
                            Toast.makeText(activity, "Please Enter Feedback", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        // what ever you want to do with No option.
                    }
                });

                alert.show();

        }


    private void closeOnRoadAssistanceRequest(final String accountId,final String access_token, final String feedBack, final String bookingId, final int position) {

        System.out.println("close trip get called" + bookingId);
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.CLOSE_ON_ROAD_ASSISTANCE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        try {


                            System.out.println("Close On road assistance Response : " + response);
                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.has("Status")) {
                                if (jsonObject.getString("Status").equalsIgnoreCase("1")) {
                                    if (jsonObject.has("Result")) {
                                        if (jsonObject.get("Result").equals("Success")) {
                                            data.remove(position);
                                            notifyDataSetChanged();
                                        }
                                    }else {
                                        String msg = jsonObject.getString("Message");
                                        Toast.makeText(activity, ""+msg, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                Toast.makeText(activity, "Blank Response", Toast.LENGTH_SHORT).show();
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
                map.put("AccessToken", access_token);
                map.put("UserId", accountId);
                map.put("onRoadAssistanceId", bookingId);
                map.put("Feedback", feedBack);
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(activity, "", "Closing Case.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }

    ProgressDialog dialog;


}
