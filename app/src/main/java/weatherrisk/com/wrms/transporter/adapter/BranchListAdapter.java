package weatherrisk.com.wrms.transporter.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.EditBranchActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.bean.BranchListBean;
import weatherrisk.com.wrms.transporter.bean.ExpenseBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;

/**
 * Created by Admin on 10-05-2017.
 */
public class BranchListAdapter extends RecyclerView.Adapter<BranchListAdapter.ViewHolder> {
    private ArrayList<BranchListBean> mDataset = new ArrayList<BranchListBean>();

    public Context mContext;
    String imageString;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView firmName,contactName,contactNo,address,tinNo,editBTN,deleteBTN;




        public ViewHolder(View v) {
            super(v);
            firmName = (TextView) v.findViewById(R.id.branchlist_firm_name);
            contactName = (TextView) v.findViewById(R.id.branchlist_contact_name);
            contactNo = (TextView) v.findViewById(R.id.branchlist_contact_no);
            address = (TextView) v.findViewById(R.id.branchlist_address);
            tinNo = (TextView) v.findViewById(R.id.branchlist_tin_no);

            editBTN = (TextView) v.findViewById(R.id.branchlist_edit_btn);
            deleteBTN = (TextView) v.findViewById(R.id.branchlist_delete_btn);





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
    public BranchListAdapter(Context con,ArrayList<BranchListBean> myDataset) {
        mDataset = myDataset;
        mContext = con;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public BranchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.branch_list_row, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        // holder.setIsRecyclable(false);


        holder.firmName.setText(mDataset.get(position).getFirm_name());
        holder.contactName.setText(mDataset.get(position).getContact_name());
        holder.contactNo.setText(mDataset.get(position).getPersonal_contact_no());
        holder.tinNo.setText(mDataset.get(position).getTin_no());
        holder.address.setText(mDataset.get(position).getAddress());


        holder.deleteBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitMethod(position,mDataset.get(position).getSerial());
            }
        });

        holder.editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(mContext, EditBranchActivity.class);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("branch_list", mDataset);
                in.putExtras(bundle);
                in.putExtra("position",String.valueOf(position));
                mContext.startActivity(in);
            }
        });

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


}