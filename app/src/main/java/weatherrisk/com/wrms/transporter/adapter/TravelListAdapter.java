package weatherrisk.com.wrms.transporter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;



import java.util.HashMap;
import java.util.List;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.TravelData;


/**
 * Created by WRMS on 03-03-2016.
 */
public class TravelListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<TravelData>> _listDataChild;

    public TravelListAdapter(Context context, List<String> listDataHeader,
                             HashMap<String, List<TravelData>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TravelData childObject = (TravelData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.travel_report_list_item, null);
        }

        TextView txtStartTime = (TextView) convertView
                .findViewById(R.id.startTime);
        TextView txtEndTime = (TextView) convertView
                .findViewById(R.id.endTime);
        TextView txtDistance = (TextView) convertView
                .findViewById(R.id.distance);
        TextView txtStartPlace = (TextView) convertView
                .findViewById(R.id.startPlace);
        TextView txtEndPlace = (TextView) convertView
                .findViewById(R.id.endPlace);
        TextView txtTravelTime = (TextView) convertView
                .findViewById(R.id.travelTime);

        /*if(childPosition%2==0){
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_1));
        }else{
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_2));
        }*/
        txtStartTime.setText(childObject.getStartDateTime());
        txtEndTime.setText(childObject.getEndDateTime());
        txtDistance.setText(childObject.getDistance()+" km.");
        txtStartPlace.setText(childObject.getStartPlace());
        txtEndPlace.setText(childObject.getEndPlace());
        txtTravelTime.setText(childObject.getTravelTime());
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expendable_list_group, null);
        }

        String[] vehicleDistanceArray = headerTitle.split("_");

        TextView txtVehicleName = (TextView) convertView
                .findViewById(R.id.vehicleName);
        txtVehicleName.setText(vehicleDistanceArray[0]);

        TextView txtTotalDistance = (TextView)convertView.findViewById(R.id.totalDistance);
        if(vehicleDistanceArray.length>1){
            txtTotalDistance.setText(vehicleDistanceArray[1]+" km.");
        }

       /* if(vehicleDistanceArray.length>1) {
            TextView txtTotalDistance = (TextView) convertView
                    .findViewById(R.id.totalDistance);
            txtTotalDistance.setText(vehicleDistanceArray[1]);
        }*/

        /*if(groupPosition%2==0){
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_1));
        }else{
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_2));
        }*/

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

