package weatherrisk.com.wrms.transporter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.CustomerOrderActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by WRMS on 28-04-2016.
 */
public class TransporterListAdapter  extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private AppCompatActivity activity;
    private ArrayList<TransporterData> data;
    private static LayoutInflater inflater=null;
    TransporterData tempValues=null;

    Fragment childFragment;

    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public TransporterListAdapter(AppCompatActivity context, ArrayList<TransporterData> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data=data;

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

        public TextView txtFirmName;
        public TextView txtOfficeContactNo;
        public TextView txtAddress;
        public TextView txtPrefrance;
        public TextView txtProductDetail;
        public TextView txtCapacity;
        public TextView txtRate;
        public Button butBookOrder;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.transporter_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtFirmName = (TextView) vi
                    .findViewById(R.id.vehicleName);
            holder.txtOfficeContactNo = (TextView) vi
                    .findViewById(R.id.startPlace);
            holder.txtAddress = (TextView) vi
                    .findViewById(R.id.startAddress);
            holder.txtPrefrance = (TextView) vi
                    .findViewById(R.id.endPlace);
            holder.txtProductDetail = (TextView) vi
                    .findViewById(R.id.endAddress);
            holder.txtCapacity = (TextView) vi
                    .findViewById(R.id.arrival);
            holder.txtRate = (TextView) vi
                    .findViewById(R.id.dispatch);
            holder.butBookOrder = (Button) vi
                    .findViewById(R.id.closeTrip);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.txtFirmName.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( TransporterData ) data.get(position);

            /************  Set Model values in Holder elements ***********/

            holder.txtFirmName.setText(tempValues.getFirmName()+" ("+tempValues.getContactName()+")");

            String contactNo = "Office Contact No. : "+tempValues.getOfficeContactNo()+" ( Personal : "+tempValues.getPersonContactNo()+" )";
            holder.txtOfficeContactNo.setText(contactNo);
            String address = "Address : "+tempValues.getAddress()+" , City : "+tempValues.getFromCityId();
            holder.txtAddress.setText(address);
            String preferences = "Door : ";
            if(tempValues.getDoorClosed().equalsIgnoreCase("yes")){
                preferences = preferences+"Closed"+"/ Refrigerated : ";
            }else{
                preferences = preferences+"Not Specified"+"/ Refrigerated : ";
            }

            if(tempValues.getRefrigerated().equalsIgnoreCase("yes")){
                preferences = preferences+"Yes";
            }else{
                preferences = preferences+"No";
            }
            holder.txtPrefrance.setText(preferences);
            String productDetail = "Product : "+tempValues.getMaterialId();
            holder.txtProductDetail.setText(productDetail);
            holder.txtCapacity.setText(tempValues.getCapacity()+" tones");
            holder.txtRate.setText(tempValues.getRate()+"/KM.");

            holder.butBookOrder.setText("BOOK");

            /******* Set Item Click Listner for LayoutInflater for each row ******/

            holder.butBookOrder.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    public Fragment getChildFragment(){
        return childFragment;
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

            TransporterData transporterData = data.get(mPosition);

            if(transporterData!=null) {
                /*SharedPreferences prefs= activity.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, activity.MODE_PRIVATE);
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String tripId = transporterData.getFirmId();
                String vehicleId = transporterData.getContactName();


                final FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                childFragment = AddOrderFragment.newInstance(transporterData, "");
                ft.replace(R.id.frame_container,childFragment , "Add Order By Customer");
                ft.commit();*/

                Intent intent = new Intent(activity, CustomerOrderActivity.class);
                intent.putParcelableArrayListExtra(CustomerOrderActivity.TRANSPORTER_LIST,data);
                intent.putExtra(CustomerOrderActivity.TRANSPORTER_DATA,transporterData);
                activity.startActivity(intent);

            }else{
                System.out.println("transporter data is null ");
            }
//            MainActivity sct = (MainActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

//            sct.onItemClick(mPosition);
        }
    }
    ProgressDialog dialog;
    private void closeTripRequest(final String accountId,final String vehicleId,final String remark,String tripId) {

        System.out.println("close trip get called"+tripId);
        StringRequest stringVarietyRequest = new StringRequest(Request.Method.PUT, MyUtility.URL.CLOSE_TRIP_API+tripId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String stateResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("State Response : " + stateResponse);
                            JSONObject jsonObject = new JSONObject(stateResponse);

                            if (jsonObject.has("Status")) {
                                if (jsonObject.get("Status").equals("success")) {

                                    JSONArray stateArray = jsonObject.getJSONArray("StateList");
                                    if (stateArray.length() > 0) {

                                    } else {
                                        Toast.makeText(activity, "No State Found", Toast.LENGTH_LONG).show();
                                    }

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
                map.put("vehicle_id", vehicleId);
                map.put("remark", remark);
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
