package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;
import weatherrisk.com.wrms.transporter.fragment.SingleChoiceVehicleListFragment;
import weatherrisk.com.wrms.transporter.fragment.StartTripFragment;

/**
 * Created by WRMS on 25-05-2016.
 */
public class AcceptedOrderAdapter  extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<OrderData> data;
    private static LayoutInflater inflater=null;
    OrderData tempValues=null;
    int i=0;


    ArrayList<VehicleData> vehicleList = new ArrayList<>();
    HashMap<String, ArrayList<InvoiceData>> invoices = new HashMap<>();
    HashMap<String, ArrayList<MaterialTypeData>> materials = new HashMap<>();


    /*************  CustomAdapter Constructor *****************/
    public AcceptedOrderAdapter(Activity context, ArrayList<OrderData> data,ArrayList<VehicleData> vehicleList,HashMap<String, ArrayList<InvoiceData>> invoices ,HashMap<String, ArrayList<MaterialTypeData>> materials ) {

        /********** Take passed values **********/
        this.activity = context;
        this.data=data;
        this.vehicleList = vehicleList;
        this.invoices = invoices;
        this.materials = materials;

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
        public TextView txtEndPlace;
        public TextView txtDoorStatus;
        public TextView txtRefrigerated;
        public TextView txtNoOfVehicles;
        public Button butStartTrip;
//        public Button butReject;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.accepted_order_list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.txtVehicleName = (TextView) vi
                    .findViewById(R.id.vehicleName);
            holder.txtStartPlace = (TextView) vi
                    .findViewById(R.id.startPlace);
            holder.txtEndPlace = (TextView) vi
                    .findViewById(R.id.endPlace);
            holder.txtDoorStatus = (TextView) vi
                    .findViewById(R.id.doorStatus);
            holder.txtRefrigerated = (TextView) vi
                    .findViewById(R.id.refrigerated);
            holder.txtNoOfVehicles = (TextView) vi
                    .findViewById(R.id.noOfVehicles);
            holder.butStartTrip = (Button) vi
                    .findViewById(R.id.startTrip);
/*
            holder.butReject = (Button) vi
                    .findViewById(R.id.reject);
*/

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
            tempValues = (OrderData) data.get(position);

            /************  Set Model values in Holder elements ***********/
            holder.txtVehicleName.setText(tempValues.getCustomerName());

            String startPlace = "Start Place : "+tempValues.getFromAddress()+" , "+tempValues.getFromCityName();
            holder.txtStartPlace.setText(startPlace);
            String endPlace = "End Place : "+tempValues.getToAddress()+" , "+tempValues.getToCityName();
            holder.txtEndPlace.setText(endPlace);
//            holder.txtDoorStatus.setText(tempValues.getDoorStatus());

            String doorStatusString = "Open";
            if(tempValues.getDoorStatus().equals("1")) {
                doorStatusString = "Closed";
            }
            holder.txtDoorStatus.setText(doorStatusString);
            String refrigeratedString = "No";
            if(tempValues.getRefrigerated().equals("1")){
                refrigeratedString ="Yes";
            }
            holder.txtRefrigerated.setText(refrigeratedString);
            holder.txtNoOfVehicles.setText(tempValues.getVehicleQuantity());

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            holder.butStartTrip.setOnClickListener(new OnItemClickListener(position));
//            holder.butReject.setOnClickListener(new OnItemClickListener(position));

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

            OrderData pendingOrderData = data.get(mPosition);
            if(pendingOrderData!=null){
                ArrayList<InvoiceData> invoiceDatas = invoices.get(pendingOrderData.getOrderId());
                ArrayList<MaterialTypeData> materialTypeDatas = new ArrayList<MaterialTypeData>();
                if(invoiceDatas!=null) {
                    for (InvoiceData iData : invoiceDatas) {
                        ArrayList<MaterialTypeData> mDatas = materials.get(iData.getInvoiceTempId());
                        for (MaterialTypeData m : mDatas) {
                            materialTypeDatas.add(m);
                        }
                    }
                }
                TransporterMainActivity mActivity = (TransporterMainActivity)activity;
                final FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                SingleChoiceVehicleListFragment fragment = SingleChoiceVehicleListFragment.newInstance(StartTripFragment.FRAGMENT_TAG,vehicleList);
                fragment.setInvoiceArray(invoiceDatas);
                fragment.setMaterialArray(materialTypeDatas);
                fragment.setPendingOrderData(pendingOrderData);
                ft.replace(R.id.frame_container,fragment , StartTripFragment.FRAGMENT_TAG);
                ft.commit();
            }
        }
    }


}

