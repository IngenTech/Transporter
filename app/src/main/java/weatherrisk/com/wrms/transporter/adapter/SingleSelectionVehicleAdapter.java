package weatherrisk.com.wrms.transporter.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * Created by WRMS on 20-02-2016.
 */
public class SingleSelectionVehicleAdapter extends BaseAdapter implements Checkable {

    private List<VehicleData> vehicalList = null;
    private ArrayList<VehicleData> arraylist;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;

    public Activity context;
    public LayoutInflater inflater;

    private VehicleData selectedVehicle = null;

    public SingleSelectionVehicleAdapter(Activity context, List<VehicleData> vehicalList) {
        super();

        this.context = context;
        this.vehicalList = vehicalList;
        this.arraylist = new ArrayList<VehicleData>();
        this.arraylist.addAll(vehicalList);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public static class ViewHolder {
        RadioButton txtViewTitle;
        TextView txtViewDescription;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return vehicalList.size();
    }

    @Override
    public VehicleData getItem(int position) {
        return vehicalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final int finalPosition = position;
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.single_choice_vehicle_row, null);

            holder.txtViewTitle = (RadioButton) convertView.findViewById(R.id.vehicleRadioButton);
            holder.txtViewDescription = (TextView) convertView.findViewById(R.id.vehicleImei);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.txtViewTitle.setText(vehicalList.get(position).getVehicleNo());
        holder.txtViewDescription.setText(vehicalList.get(position).getImei());

        holder.txtViewTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((finalPosition != mSelectedPosition) && (mSelectedRB != null)) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = finalPosition;
                mSelectedRB = (RadioButton) v;

                selectedVehicle = vehicalList.get(finalPosition);

            }
        });


        if (mSelectedPosition != position) {
            holder.txtViewTitle.setChecked(false);
        } else {
            holder.txtViewTitle.setChecked(true);
            if (mSelectedRB != null && holder.txtViewTitle != mSelectedRB) {
                mSelectedRB = holder.txtViewTitle;
            }
        }
        /*if (vehicalList.get(finalPosition).getActiveStatus().contains("1")) {
            holder.txtViewTitle.setTextColor(Color.parseColor("#028C00"));
            holder.txtViewDescription.setTextColor(Color.parseColor("#028C00"));
        } else {
            holder.txtViewTitle.setTextColor(Color.BLACK);
            holder.txtViewDescription.setTextColor(Color.BLACK);
        }*/
        return convertView;
    }


    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        vehicalList.clear();
        if (charText.length() == 0) {
            vehicalList.addAll(arraylist);
        } else {
            for (VehicleData vi : arraylist) {
                if (vi.getVehicleNo().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    vehicalList.add(vi);
                }
            }
        }
        notifyDataSetChanged();
    }

    public VehicleData getSelectedVehicle() {
        return selectedVehicle;
    }

    @Override
    public boolean isChecked() {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public void setChecked(boolean checked) {
        // TODO Auto-generated method stub

    }


    @Override
    public void toggle() {
        // TODO Auto-generated method stub

    }
}
