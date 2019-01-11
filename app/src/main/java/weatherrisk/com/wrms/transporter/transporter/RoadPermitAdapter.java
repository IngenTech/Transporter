package weatherrisk.com.wrms.transporter.transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 04-04-2017.
 */
public class RoadPermitAdapter extends RecyclerView.Adapter<RoadPermitAdapter.ViewHolder> {
    private ArrayList<RoadPermitBean> mDataset = new ArrayList<RoadPermitBean>();

    public Context mContext;
    String imageString;
    int poss;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView remark;
        public TextView date;
        public ImageView rowImage;

        public ViewHolder(View v) {
            super(v);

            date = (TextView) v.findViewById(R.id.date);
            remark = (TextView) v.findViewById(R.id.remark);
            rowImage = (ImageView) v.findViewById(R.id.roadpermit_row_image);

        }
    }

    public RoadPermitAdapter(Context con, ArrayList<RoadPermitBean> myDataset) {
        mDataset = myDataset;

        mContext = con;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RoadPermitAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.road_permit_adapter, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.date.setText(mDataset.get(position).getCreateDate());
        holder.remark.setText(mDataset.get(position).getRemark());

        holder.rowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageString = null;
                String roadPermitID = mDataset.get(position).getPermitID();
                roadPermitDownload(roadPermitID, holder,position);


            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void roadPermitDownload(final String roadPermitID, final ViewHolder holder, final int pos) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DOWNLOAD_ROADTRIP_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Invoice Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {


                                if (jsonObject.get("Result").equals("Success")) {

                                    imageString = jsonObject.getString("ImageString");
                                    if (imageString != null && imageString.length() > 10) {
                                       /* byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                        holder.rowImage.setImageBitmap(decodedByte);*/

                                        poss = pos;

                                        Glide.with(mContext).load(imageString).into(holder.rowImage);
                                        Picasso.with(mContext).load(imageString).into(target);
                                    }


                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";

                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = mContext.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, mContext.MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("RoadPermitId", roadPermitID);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }

                return map;
            }
        };

        dialog = ProgressDialog.show(mContext, "",
                "Downloading image.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    String name = "road_permit_"+mDataset.get(poss).getPermitID()+ ".jpg";
                    File file = new File(
                            Environment.getExternalStorageDirectory().getPath()
                                    + "/"+name);

                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ostream);
                        ostream.close();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {}

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {}
    };
}