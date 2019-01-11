package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import weatherrisk.com.wrms.transporter.dataobject.TripData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by WRMS on 03-03-2016.
 */
public class TripListAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<TripData> data;
    private static LayoutInflater inflater=null;
    boolean isStart = false;
    TripData tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public TripListAdapter(Activity context, ArrayList<TripData> data,boolean isStart) {

        /********** Take passed values **********/
        this.activity = context;
        this.data=data;
        this.isStart = isStart;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
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

        public TextView txtVehicleName ;
        public TextView txtStartPlace ;
//        public TextView txtStartAddress ;
        public TextView txtEndPlace;
//        public TextView txtEndAddress ;
        public TextView txtArrival;
        public TextView txtDispatch;
        public Button butCloseTrip;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.trip_list_card, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtVehicleName = (TextView) vi
                    .findViewById(R.id.vehicleName);
            holder.txtStartPlace = (TextView) vi
                    .findViewById(R.id.startPlace);
            /*holder.txtStartAddress = (TextView) vi
                    .findViewById(R.id.startAddress);*/
            holder.txtEndPlace = (TextView) vi
                    .findViewById(R.id.endPlace);
            /*holder.txtEndAddress = (TextView) vi
                    .findViewById(R.id.endAddress);*/
            holder.txtArrival = (TextView) vi
                    .findViewById(R.id.arrival);
            holder.txtDispatch = (TextView) vi
                    .findViewById(R.id.dispatch);
            holder.butCloseTrip = (Button) vi
                    .findViewById(R.id.closeTrip);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.txtVehicleName.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( TripData ) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtVehicleName.setText(tempValues.getVehicleName());
            
            String startPlace = "From : "+tempValues.getFromCityName()+" , "+tempValues.getFromStateName()+" ("+tempValues.getFromAddress()+")";
            holder.txtStartPlace.setText(startPlace);
//            holder.txtStartAddress.setText(tempValues.getFromAddress());
            String endPlace = "To : "+tempValues.getToCityName()+" , "+tempValues.getToStateName()+" ("+tempValues.getToAddress()+")";
            holder.txtEndPlace.setText(endPlace);
//            holder.txtEndAddress.setText(tempValues.getToAddress());
            holder.txtArrival.setText(tempValues.getArrivalDate());
            holder.txtDispatch.setText(tempValues.getDispatchDate());

            /******** Set Item Click Listner for LayoutInflater for each row *******/

        if(isStart){
            holder.butCloseTrip.setText("START TRIP");
        }else{
            holder.butCloseTrip.setText("CLOSE TRIP");
        }
            holder.butCloseTrip.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            final TripData tripData = data.get(mPosition);

            if(tripData!=null) {

                /*String detailString  = "Vehicle No. : "+tripData.getVehicleName()+"\n"+
                        "Start Place : "+tripData.getFromAddress()+","+tripData.getFromCityName()+","+tripData.getFromStateName()+"\n"+
                        "End Place : "+tripData.getToAddress()+","+tripData.getToCityName()+","+tripData.getToStateName()+"\n"+
                        "Arrival : "+tripData.getArrivalDate();*/

                View view1;
                LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view1 = inflater.inflate(R.layout.on_road_assistance_alert_item, null);


                AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                final EditText edittext = (EditText)view1.findViewById(R.id.feedBack) ;
                edittext.setHint("Remark");

                TextView textView = (TextView)view1.findViewById(R.id.detail) ;
                textView.setText("Vehicle No. : "+tripData.getVehicleName());

                String startPlaceString = "Start Place : "+tripData.getFromAddress()+","+tripData.getFromCityName()+","+tripData.getFromStateName();
                TextView textView1 = (TextView)view1.findViewById(R.id.detail1) ;
                textView1.setText(startPlaceString);

                String endPlaceString = "End Place : "+tripData.getToAddress()+","+tripData.getToCityName()+","+tripData.getToStateName();
                TextView textView2 = (TextView)view1.findViewById(R.id.detail2) ;
                textView2.setText(endPlaceString);

                TextView textView3 = (TextView)view1.findViewById(R.id.detail3) ;
                textView3.setText("Arrival : "+tripData.getArrivalDate());

                alert.setView(view1);
                alert.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //What ever you want to do with the value
                        Editable YouEditTextValue = edittext.getText();
                        //OR
                        String feedBackString = edittext.getText().toString();

                        if(feedBackString!=null && !feedBackString.isEmpty()){

                            SharedPreferences prefs= activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                            String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                            String tripId = tripData.getTripId();
                            String vehicleId = tripData.getVehicleId();
                            closeTripRequest(accountId,vehicleId,feedBackString,tripId,tripData);

                        }else{
                            Toast.makeText(activity,"Please Enter Remark",Toast.LENGTH_SHORT).show();
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





            }else{
                System.out.println("Trip data is null ");
            }
//            MainActivity sct = (MainActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

//            sct.onItemClick(mPosition);
        }
    }
    ProgressDialog dialog;
    private void closeTripRequest(final String accountId,final String vehicleId,final String remark,final String tripId,final TripData tripData) {

        System.out.println("close trip get called"+tripId);
            StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.CLOSE_TRIP_API+tripId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String stateResponse) {
                            dialog.dismiss();
                            try {
                                System.out.println("State Response : " + stateResponse);
                                JSONObject jsonObject = new JSONObject(stateResponse);

                                if (jsonObject.has("result")) {
                                    if (jsonObject.get("result").equals("success")) {

                                        data.remove(tripData);
                                        TripListAdapter.this.notifyDataSetChanged();

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
                    String apiKey = activity.getResources().getString(R.string.server_api_key);
                    Map<String, String> map = new HashMap<>();
                    map.put("api_key", apiKey);
                    map.put("account_id", accountId);
//                    map.put("vehicle_id", vehicleId);
                    map.put("remark", remark);
//                    map.put("trip_id", tripId);
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