package weatherrisk.com.wrms.transporter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;

/**
 * Created by WRMS on 30-04-2016.
 */
public class InvoiceExpendableListAdapter  extends BaseExpandableListAdapter {

    private Context _context;
    private ArrayList<InvoiceData> invoiceArray;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<MaterialTypeData>> _listDataChild;

    public InvoiceExpendableListAdapter(Context context, List<String> listDataHeader,
                             HashMap<String, List<MaterialTypeData>> listChildData,ArrayList<InvoiceData> invoiceArray) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        System.out.println("listChildData : "+listChildData.size());
        this.invoiceArray = invoiceArray;
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

        final MaterialTypeData childObject = (MaterialTypeData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.material_report_list_item, null);
        }

        TextView txtMaterialName = (TextView) convertView
                .findViewById(R.id.materialTypeName);
        TextView txtMaterialTypeRemark = (TextView) convertView
                .findViewById(R.id.materialTypeRemark);
        TextView txtMaterialAmount = (TextView) convertView
                .findViewById(R.id.materialAmount);

        /*if(childPosition%2==0){
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_1));
        }else{
            convertView.setBackgroundColor(_context.getResources().getColor(R.color.row_alternet_color_2));
        }*/
        txtMaterialName.setText(childObject.getMaterialTypeName());
        txtMaterialTypeRemark.setText(childObject.getRemark());
        txtMaterialAmount.setText(childObject.getAmount());
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
            convertView = infalInflater.inflate(R.layout.expendable_invoice_group_item, null);
        }

        InvoiceData invoiceData = null;
        for(InvoiceData data : invoiceArray){
            if(headerTitle.equals(data.getInvoiceTempId())){
                invoiceData = data;
                break;
            }
        }

        TextView txtInvoiceNumber = (TextView) convertView
                .findViewById(R.id.invoiceNumber);
        TextView txtInvoiceAmount = (TextView) convertView
                .findViewById(R.id.invoiceAmount);
        TextView txtInvoiceDate = (TextView) convertView
                .findViewById(R.id.invoiceDate);
        ImageView txtInvoiceView = (ImageView) convertView
                .findViewById(R.id.invoiceView);

        if(invoiceData!=null){
            txtInvoiceNumber.setText(invoiceData.getInvoiceNumber());
            txtInvoiceAmount.setText(invoiceData.getInvoiceAmount());
            txtInvoiceDate.setText(invoiceData.getDate());
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

