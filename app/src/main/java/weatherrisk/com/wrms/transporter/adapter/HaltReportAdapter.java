package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.HaltData;

/**
 * Created by Admin on 19-04-2017.
 */
public class HaltReportAdapter  extends RecyclerView.Adapter<HaltReportAdapter.ViewHolder>{



    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList<HaltData> data;

    HaltData tempValues=null;


    /*************  CustomAdapter Constructor *****************/
    public HaltReportAdapter(Activity context, ArrayList<HaltData> data) {

        /********** Take passed values **********/
        this.activity = context;
        this.data=data;


    }

    public Object getItem(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.halt_row, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        tempValues=null;
        tempValues = ( HaltData ) data.get(position);


        holder.arrivalTime.setText(tempValues.getArrivalTime());
        holder.departureTime.setText(tempValues.getDepartureTime());
        holder.haltDuration.setText(tempValues.getHaltDuration());
        holder.address.setText(tempValues.getPlace());
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

        public TextView arrivalTime;
        public TextView departureTime;
        public TextView haltDuration;
        public TextView address;

        public ViewHolder(View vi) {
            super(vi);

            this.arrivalTime = (TextView) vi.findViewById(R.id.halt_arrival_time);
            this.departureTime = (TextView) vi.findViewById(R.id.halt_departure_time);
            this.haltDuration = (TextView) vi.findViewById(R.id.halt_duration);
            this.address = (TextView) vi.findViewById(R.id.halt_address);

        }


    }

}