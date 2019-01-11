package weatherrisk.com.wrms.transporter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;


/**
 * Created by WRMS on 22-02-2016.
 */
public class MultipleSelectionVehicleAdapter extends BaseAdapter {
    // Declare Variables
    Context mContext;
    LayoutInflater inflater;
    private List<VehicleData> vehicleList = null;
    private ArrayList<VehicleData> arraylist;

    private ArrayList<VehicleData> selectedVehicles = new ArrayList<>();

    public MultipleSelectionVehicleAdapter(Context context,
                                           List<VehicleData> vehicalList) {
        mContext = context;
        this.vehicleList = vehicalList;
        for(VehicleData vehicleData : this.vehicleList){
            vehicleData.setSelected(VehicleData.NOT_SELECTED);
        }
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<VehicleData>();
        this.arraylist.addAll(vehicalList);
    }

    public class ViewHolder {
        TextView txtVehicalNumber;
        TextView txtIMEI;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return vehicleList.size();
    }

    @Override
    public VehicleData getItem(int position) {
        return vehicleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.multiple_choice_list_item, null);
            // Locate the TextViews in listview_item.xml
            holder.txtIMEI = (TextView) convertView.findViewById(R.id.imei);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setText(vehicleList.get(position).getVehicleNo());
        holder.txtIMEI.setText(vehicleList.get(position).getImei());
        if (vehicleList.get(position).getSelected().equals(VehicleData.SELECTED)) {
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    selectedVehicles.add(vehicleList.get(position));
                    vehicleList.get(position).setSelected(VehicleData.SELECTED);
                }else {
                    vehicleList.get(position).setSelected(VehicleData.NOT_SELECTED);
                    if (selectedVehicles.contains(vehicleList.get(position))) {
                        int indexOf = selectedVehicles.indexOf(vehicleList.get(position));
                        selectedVehicles.remove(indexOf);
                    }
                }

            }
        });
        /*if (vehicleList.get(position).getActiveStatus().contains("1")) {
            holder.checkBox.setTextColor(Color.parseColor("#028C00"));
            holder.txtIMEI.setTextColor(Color.parseColor("#028C00"));
        } else {
            holder.checkBox.setTextColor(Color.BLACK);
            holder.txtIMEI.setTextColor(Color.BLACK);
        }*/


        return convertView;
    }

    public ArrayList<VehicleData> getSelectedVehicles() {
        return selectedVehicles;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        vehicleList.clear();
        if (charText.length() == 0) {
            vehicleList.addAll(arraylist);
        } else {
            for (VehicleData vi : arraylist) {
                if (vi.getVehicleNo().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    vehicleList.add(vi);
                }
            }
        }
        notifyDataSetChanged();
    }

}
