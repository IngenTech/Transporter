package weatherrisk.com.wrms.transporter.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.WeatherData;

/**
 * Created by WRMS on 21-03-2016.
 */
public class WeatherInfoWindowAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    ArrayList<WeatherData> data;

    public WeatherInfoWindowAdapter(Context context, ArrayList<WeatherData> data) {
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

            view = layoutInflater.inflate(R.layout.weather_data_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.timeTextView = (TextView) view.findViewById(R.id.time);
            viewHolder.tempTextView = (TextView) view.findViewById(R.id.temp);
            viewHolder.windSpeedTextView = (TextView) view.findViewById(R.id.windSpeed);
            viewHolder.rainfallTextView = (TextView) view.findViewById(R.id.rainfall);
            viewHolder.humidityTextView = (TextView) view.findViewById(R.id.humidity);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();

        WeatherData weatherValue = data.get(position);
        if(weatherValue.getDate()!=null && weatherValue.getDate().trim().length()>0) {

            viewHolder.timeTextView.setText(weatherValue.getDate());
            viewHolder.timeTextView.setTextColor(Color.BLACK);
            viewHolder.timeTextView.setPadding(0, 0, 0, 10);
            viewHolder.timeTextView.setTypeface(null, Typeface.BOLD);

            viewHolder.tempTextView.setVisibility(View.GONE);
            viewHolder.windSpeedTextView.setVisibility(View.GONE);
            viewHolder.rainfallTextView.setVisibility(View.GONE);
            viewHolder.humidityTextView.setVisibility(View.GONE);

        }else{
            if(weatherValue.getTime().equals("Time")){
                viewHolder.timeTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.tempTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.windSpeedTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.rainfallTextView.setTypeface(null, Typeface.BOLD);
                viewHolder.humidityTextView.setTypeface(null, Typeface.BOLD);
            }
            viewHolder.timeTextView.setText(weatherValue.getTime());
            viewHolder.tempTextView.setText(weatherValue.getTemperature());
            viewHolder.windSpeedTextView.setText(weatherValue.getWindSpeed());
            viewHolder.rainfallTextView.setText(weatherValue.getRainfall());
            viewHolder.humidityTextView.setText(weatherValue.getHumidity());
        }

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
        TextView timeTextView;
        TextView tempTextView;
        TextView windSpeedTextView;
        TextView rainfallTextView;
        TextView humidityTextView;
    }
}

