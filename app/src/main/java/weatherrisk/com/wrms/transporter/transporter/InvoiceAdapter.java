package weatherrisk.com.wrms.transporter.transporter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 21-03-2017.
 */
public class InvoiceAdapter  extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    private ArrayList<InvoiceBean> mDataset = new ArrayList<InvoiceBean>();

    public Context mContext;
    String imageString;
    int poss;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView date;
        public TextView totalMatterial;
        public TextView amount;
        public ImageView invoiceImage;


        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.titleTxt);
            date = (TextView) v.findViewById(R.id.date);
            amount = (TextView) v.findViewById(R.id.amount);
            totalMatterial = (TextView) v.findViewById(R.id.no_matterial);
            invoiceImage = (ImageView) v.findViewById(R.id.invoice_row_image);


        }
    }

    /*  public void add(int position, String item) {
          mDataset.add(position, item);
          notifyItemInserted(position);
      }

      public void remove(String item) {
          int position = mDataset.indexOf(item);
          mDataset.remove(position);
          notifyItemRemoved(position);
      }
  */
    // Provide a suitable constructor (depends on the kind of dataset)
    public InvoiceAdapter(Context con,ArrayList<InvoiceBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


       // holder.setIsRecyclable(false);

        holder.title.setText(mDataset.get(position).getInvoiceNo());
        holder.date.setText(mDataset.get(position).getInvoiceDate());
        holder.amount.setText(mDataset.get(position).getAmount());
        holder.totalMatterial.setText(mDataset.get(position).getNoOFMatterial());
        Glide.with(mContext).load(mDataset.get(position).getImage());

        holder.invoiceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageString = null;
                String invoiceID = mDataset.get(position).getInvoiceID();
                viewInviceImage(invoiceID,holder,position);



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

    private void viewInviceImage(final String invoiceID, final ViewHolder holder, final int pos) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DOWNLOAD_INVOICE_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Invoice Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {




                                if (jsonObject.get("Result").equals("Success")) {

                                    imageString = jsonObject.getString("ImageString");
                                    if (imageString!=null && imageString.length()>10) {

                                        poss =pos;

                                      //  new ImageDownloader(mContext,imageString,name).execute();


                                       /* byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                        holder.invoiceImage.setImageBitmap(decodedByte);
                                   //     notifyDataSetChanged();*/

                                        Glide.with(mContext).load(imageString).into(holder.invoiceImage);

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
                map.put("InvoiceId", invoiceID);

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

                    String name = "invoice_"+mDataset.get(poss).getInvoiceID()+ ".jpg";
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/"+name);

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


