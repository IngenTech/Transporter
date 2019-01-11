package weatherrisk.com.wrms.transporter.vehicalDocument;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.VehicalListBean;
import weatherrisk.com.wrms.transporter.bean.VehicleDocumentBean;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 24-04-2017.
 */
public class ViewVehicalAdapter  extends RecyclerView.Adapter<ViewVehicalAdapter.ViewHolder> {
    private ArrayList<VehicleDocumentBean> mDataset = new ArrayList<VehicleDocumentBean>();

    public Context mContext;
    String imageString;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView docType;

        public ImageView docImage;



        public ViewHolder(View v) {
            super(v);
            docType = (TextView) v.findViewById(R.id.titleTxt);
            docImage = (ImageView) v.findViewById(R.id.vehicle_image);



        }
    }

      /*public void add(int position, String item) {
          mDataset.add(position, item);
          notifyItemInserted(position);
      }*/

    public void remove(int pos) {
        //   int position = mDataset.indexOf(item);
        mDataset.remove(pos);
        notifyItemRemoved(pos);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ViewVehicalAdapter(Context con,ArrayList<VehicleDocumentBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewVehicalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_vehical_doc_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.docType.setText(mDataset.get(position).getTitle());

        String imgUrl = mDataset.get(position).getImage();
        if (imgUrl!=null && imgUrl.length()>10) {

            Glide.with(mContext).load(imgUrl)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.docImage);
        }

        holder.docImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

                final Dialog dialog = new Dialog(mContext);

                dialog.setCanceledOnTouchOutside(true);
                Window window = dialog.getWindow();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0.5f;

                dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                // Include dialog.xml file
                dialog.setContentView(R.layout.screen_popup);
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                dialog.show();

                final ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel_popup);
                final ImageView img = (ImageView) dialog.findViewById(R.id.image_popup);

                String imgUrl = mDataset.get(position).getImage();
                if (imgUrl!=null && imgUrl.length()>10) {

                    Glide.with(mContext).load(imgUrl)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(img);
                }

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.cancel();
                    }
                });

            }
        });


        holder.docImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                exitMethod(position,mDataset.get(position).getDocumentId());

                return false;
            }
        });



    }

    private void exitMethod(final int pos, final String docID){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("EXIT").
                setMessage("Do you want to Delete this item?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        deleteDocument(docID);

                        remove(pos);
                    }
                }).
                setNegativeButton("NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    ProgressDialog dialog;
    SharedPreferences prefs;

    private void deleteDocument(final String docID) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.DELETE_DOCUMENT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Delete Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();
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
                map.put("DocumentId", docID);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(mContext, "","Deleting document.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


}