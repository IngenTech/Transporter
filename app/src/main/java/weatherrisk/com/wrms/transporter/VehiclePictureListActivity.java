package weatherrisk.com.wrms.transporter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.BranchListAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.PhotoAdapter;
import weatherrisk.com.wrms.transporter.bean.BranchListBean;
import weatherrisk.com.wrms.transporter.bean.PhotoBean;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

/**
 * Created by Admin on 11-05-2017.
 */
public class VehiclePictureListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<BranchListBean> branch_list = new ArrayList<BranchListBean>();
    DBAdapter db;

    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;
    ImageView imageView;

    ArrayList<PhotoBean> listPhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_list_activity);


        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        String user_Id = prefs.getString(AppController.PREFERENCE_USER_ID, "");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        db = new DBAdapter(this);
        db.open();

        recyclerView = (RecyclerView) findViewById(R.id.pic_list);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        Cursor titleCursor = db.photoListByStateId(user_Id);

        try {
            // read data from the cursor in here


            Log.v("wkdaks", titleCursor.getCount() + "");
            listPhoto = new ArrayList<PhotoBean>();

            if (titleCursor.getCount() > 0) {
                if (titleCursor.moveToFirst()) {
                    do {

                        PhotoBean bean = new PhotoBean();

                        bean.setPhotoId(titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.PHOTO_ID)));
                        bean.setPhoto(titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.PHOTO)));


                        listPhoto.add(bean);

                    } while (titleCursor.moveToNext());
                }

            }

        } finally {
            titleCursor.close();
        }

        if (listPhoto.size() > 0) {
            PhotoAdapter adapter = new PhotoAdapter(VehiclePictureListActivity.this, listPhoto);
            recyclerView.setAdapter(adapter);
        } else {
            NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
            recyclerView.setAdapter(adapter_no);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addpics_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_pics) {
            initiatePopupWindow();

            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    ProgressDialog dialog;
    SharedPreferences prefs;

    private void getBranchList() {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.BRANCH_LIST_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.cancel();
                        try {
                            System.out.println("branch list Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be fetch";
                                    Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();

                                    branch_list = new ArrayList<BranchListBean>();

                                    JSONArray jsonArray = jsonObject.getJSONArray("BranchList");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jObject = jsonArray.getJSONObject(i);
                                        BranchListBean bean = new BranchListBean();
                                        bean.setSerial(jObject.getString("serial"));
                                        bean.setFirm_name(jObject.getString("firm_name"));
                                        bean.setContact_name(jObject.getString("contact_name"));
                                        bean.setPersonal_contact_no(jObject.getString("personal_contact_no"));
                                        bean.setHome_contact_no(jObject.getString("home_contact_no"));
                                        bean.setOffice_contact_no(jObject.getString("office_contact_no"));
                                        bean.setTin_no(jObject.getString("tin_no"));
                                        bean.setCountry_id(jObject.getString("country_id"));
                                        bean.setAddress(jObject.getString("address"));
                                        bean.setEmail_id(jObject.getString("email_id"));
                                        String state_ID = jObject.getString("state_id");
                                        String city_ID = jObject.getString("city_id");
                                        Cursor stateName = db.stateById(state_ID);
                                        Cursor cityName = db.cityById(city_ID);

                                        if (stateName.moveToFirst()) {
                                            do {

                                                bean.setStateName(stateName.getString(stateName.getColumnIndex(DBAdapter.STATE_NAME)));

                                            } while (stateName.moveToNext());
                                        }

                                        if (cityName.moveToFirst()) {
                                            do {

                                                bean.setCityName(cityName.getString(cityName.getColumnIndex(DBAdapter.CITY_NAME)));

                                            } while (cityName.moveToNext());
                                        }

                                        branch_list.add(bean);

                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();

                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(getApplicationContext(), message + "", Toast.LENGTH_SHORT).show();
                            }


                            if (branch_list.size() > 0) {
                                BranchListAdapter adapter = new BranchListAdapter(VehiclePictureListActivity.this, branch_list);
                                recyclerView.setAdapter(adapter);
                            } else {
                                NoInternetConnectionAdapter adapter_no = new NoInternetConnectionAdapter("No Data Found.");
                                recyclerView.setAdapter(adapter_no);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        viewDocRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(this, "", "Fetching Branch List.....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }


    private void initiatePopupWindow() {


        //final Dialog dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar);
        final Dialog dialog = new Dialog(this);

        Window window = dialog.getWindow();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        // wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);


        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_addphoto);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView = (ImageView) dialog.findViewById(R.id.image_view);
        Button addPhoto = (Button) dialog.findViewById(R.id.add_photo);
        Button uploadPhoto = (Button) dialog.findViewById(R.id.upload_photo);
        ImageView close = (ImageView) dialog.findViewById(R.id.close_btn);


        dialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageString = null;
                dialog.cancel();
            }
        });

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageString = null;
                selectImage();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageString == null || imageString.length() < 10) {
                    Toast.makeText(getApplicationContext(), "please add photo first", Toast.LENGTH_SHORT).show();
                } else {

                    Log.v("imhahj", imageString + "");
                    localPhotoList();
                }

                dialog.cancel();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(VehiclePictureListActivity.this);
                    if (resultCam) {

                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    boolean resultCam = Utility.checkPermissionGallery(VehiclePictureListActivity.this);
                    if (resultCam) {

                        galleryIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE_START);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA_START);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE_START) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA_START) {
                onCaptureImageResult(data);
            }
        }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (IllegalAccessError e) {
            e.printStackTrace();
        }
    }

    public void localPhotoList() {

        if (prefs == null) {
            prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
        }
        String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
        String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

        SQLiteDatabase SqliteDB = db.getSQLiteDatabase();
        SqliteDB.beginTransaction();
        // db.db.execSQL("delete from " + DBAdapter.TABLE_PHOTO);
        String query = "INSERT INTO " + DBAdapter.TABLE_PHOTO + "(" + DBAdapter.PHOTO_ID + "," + DBAdapter.USER_ID + "," + DBAdapter.PHOTO + ") VALUES (?,?,?)";

        SQLiteStatement stmt = SqliteDB.compileStatement(query);

        stmt.bindString(1, "1");
        stmt.bindString(2, accountId);
        stmt.bindString(3, imageString);

        stmt.execute();


        SqliteDB.setTransactionSuccessful();
        SqliteDB.endTransaction();

        imageString = null;

    }


}