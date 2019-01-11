package weatherrisk.com.wrms.transporter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.VehicalListBean;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceBean;
import weatherrisk.com.wrms.transporter.transporter.UploadDocumentActivity;
import weatherrisk.com.wrms.transporter.transporter.ViewDocumentActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.vehicalDocument.UploadVehicalDocumentActivity;
import weatherrisk.com.wrms.transporter.vehicalDocument.ViewVehicalDocuments;

/**
 * Created by Admin on 22-04-2017.
 */
public class VehicalManageAdapter  extends RecyclerView.Adapter<VehicalManageAdapter.ViewHolder> {
    private ArrayList<VehicalListBean> mDataset = new ArrayList<VehicalListBean>();

    public Context mContext;
    String imageString;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView vehicalName;
        public TextView regist;
        public TextView insurance,roadTax,permit,pollution,fitness;

        private LinearLayout vehicalItem;
        private Button viewDocumentBtn;
        private Button uploadDocumentBtn;


        public ViewHolder(View v) {
            super(v);
            vehicalName = (TextView) v.findViewById(R.id.titleTxt);
            regist = (TextView) v.findViewById(R.id.regisTxt);
            insurance = (TextView) v.findViewById(R.id.insurance_no);

            roadTax = (TextView) v.findViewById(R.id.road_tax);
            pollution = (TextView) v.findViewById(R.id.pollution);
            permit = (TextView) v.findViewById(R.id.permit);
            fitness = (TextView)v.findViewById(R.id.fitness);


            vehicalItem = (LinearLayout)v.findViewById(R.id.vehical_manage_item);
            viewDocumentBtn = (Button)v.findViewById(R.id.actionviewButton);
            uploadDocumentBtn =(Button)v.findViewById(R.id.actionUploadButton);



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
    public VehicalManageAdapter(Context con,ArrayList<VehicalListBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public VehicalManageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehical_manage_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);

        holder.vehicalName.setText(mDataset.get(position).getModel_no());
        holder.regist.setText("("+mDataset.get(position).getRegistration_no()+")");
        holder.insurance.setText(mDataset.get(position).getInsurance_no());
        holder.roadTax.setText(mDataset.get(position).getRoad_tax_no());
        holder.permit.setText(mDataset.get(position).getPermit_type());
        holder.pollution.setText(mDataset.get(position).getPollution_no());
        holder.fitness.setText(mDataset.get(position).getFitness_no());



        holder.vehicalItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

               exitMethod(position);
                return false;
            }
        });


        holder.viewDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewVehicalDocuments.class);
                intent.putExtra("vehicleId",mDataset.get(position).getVehicle_id());
                mContext.startActivity(intent);
            }
        });

        holder.uploadDocumentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in  = new Intent(mContext, UploadVehicalDocumentActivity.class);
                in.putExtra("vehicleId",mDataset.get(position).getVehicle_id());
                mContext.startActivity(in);
            }
        });

    }





    private void exitMethod(final int pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("EXIT").
                setMessage("Do you want to Delete this item?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                        deleteVehicle(mDataset.get(pos).getVehicle_id());
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

    private void deleteVehicle(final String vehicleID) {
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
                map.put("VehicleId", vehicleID);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(mContext, "","Deleting Vehicle.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


}