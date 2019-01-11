package weatherrisk.com.wrms.transporter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.EditBranchActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.BranchListBean;
import weatherrisk.com.wrms.transporter.bean.PhotoBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 11-05-2017.
 */
public class PhotoAdapter  extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private ArrayList<PhotoBean> mDataset = new ArrayList<PhotoBean>();

    public Context mContext;
    String imageString;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoVehicle;




        public ViewHolder(View v) {
            super(v);
            photoVehicle = (ImageView) v.findViewById(R.id.photo_vehicle);
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
    public PhotoAdapter(Context con,ArrayList<PhotoBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);

        Bitmap bm = StringToBitMap(mDataset.get(position).getPhoto());

        holder.photoVehicle.setImageBitmap(bm);

    }

    private void exitMethod(final int pos, final String branchID){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("EXIT").
                setMessage("Do you want to Delete this item?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        deleteBranch(branchID);

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

    private void deleteBranch(final String branchID) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.BRANCH_DELETE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Delete Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Deleted";
                                    Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Deleted";
                                    Toast.makeText(mContext,message+"",Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Deleted";
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
                map.put("Serial", branchID);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(mContext, "","Deleting Branch.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }

}