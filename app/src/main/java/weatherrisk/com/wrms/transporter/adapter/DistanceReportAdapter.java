package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.DistanceData;

/**
 * Created by Admin on 19-04-2017.
 */
public class DistanceReportAdapter  extends RecyclerView.Adapter<DistanceReportAdapter.ViewHolder>{

    public static final String TITLE = "Confirm Orders";

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<DistanceData> data;

    DistanceData tempValues=null;


    /*************  CustomAdapter Constructor *****************/
    public DistanceReportAdapter(Activity context, ArrayList<DistanceData> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data=data;


    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.distance_row, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        tempValues=null;
        tempValues = ( DistanceData ) data.get(position);


        holder.imei.setText(tempValues.getDeviceImei());
        holder.vehicalName.setText(tempValues.getVehicalName());
        holder.dateFrom.setText(tempValues.getDateFrom());
        holder.dateTo.setText(tempValues.getDateTo());
        holder.distance.setText(tempValues.getDistance());
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView imei ;
        public TextView vehicalName ;
        public TextView dateFrom;
        public TextView dateTo;
        public TextView distance;

        public ViewHolder(View vi) {
            super(vi);
            this.imei = (TextView) vi.findViewById(R.id.distance_imei);
            this.vehicalName = (TextView) vi.findViewById(R.id.distance_vehicalName);
            this.dateFrom = (TextView) vi.findViewById(R.id.distance_date_from);
            this.dateTo = (TextView) vi.findViewById(R.id.distance_date_to);
            this.distance = (TextView) vi.findViewById(R.id.distance_distance);

        }


    }

}