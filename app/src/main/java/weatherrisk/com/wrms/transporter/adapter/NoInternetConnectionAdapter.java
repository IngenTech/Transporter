package weatherrisk.com.wrms.transporter.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import weatherrisk.com.wrms.transporter.R;


/**
 * Created by Ajay on 30/06/2016.
 */
public class NoInternetConnectionAdapter extends RecyclerView.Adapter<NoInternetConnectionAdapter.ViewHolder> {

    private String mMessage;

    public NoInternetConnectionAdapter(){}

    public NoInternetConnectionAdapter(String message){
        mMessage = message;
    }

    @Override
    public NoInternetConnectionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_internet_connection, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        if(mMessage != null){
            viewHolder.mMessageView.setText(mMessage);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoInternetConnectionAdapter.ViewHolder holder, int position) {}

    @Override
    public int getItemCount() {
        return 1;//must return one otherwise none item is shown
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mMessageView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mMessageView = (TextView) view.findViewById(R.id.no_internet_text);
        }
    }
}