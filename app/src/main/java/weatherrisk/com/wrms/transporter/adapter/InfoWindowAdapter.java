package weatherrisk.com.wrms.transporter.adapter;

/**
 * Created by WRMS on 12-02-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.KeyValueData;

public class InfoWindowAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    ArrayList<KeyValueData> data;

    public InfoWindowAdapter(Context context, ArrayList<KeyValueData> data) {
        layoutInflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {

            view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();

        KeyValueData keyValue = data.get(position);
        viewHolder.textView.setText(keyValue.getTitle()+" : "+keyValue.getValue());

        /*switch (position) {
            case 0:
                viewHolder.textView.setText("GooglePluse");
                viewHolder.imageView.setImageResource(R.drawable.ic_action_customer);
                break;
            case 1:
                viewHolder.textView.setText("GoogleMap");
                viewHolder.imageView.setImageResource(R.drawable.ic_action_map);
                break;
            default:
                viewHolder.textView.setText("GoogleMassenger");
                viewHolder.imageView.setImageResource(R.drawable.ic_action_report);
                break;
        }*/

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}

