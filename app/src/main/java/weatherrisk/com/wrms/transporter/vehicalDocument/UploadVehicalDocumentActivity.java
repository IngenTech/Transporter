package weatherrisk.com.wrms.transporter.vehicalDocument;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

/**
 * Created by Admin on 24-04-2017.
 */
public class UploadVehicalDocumentActivity extends AppCompatActivity {

    Spinner vehicalTitle;
    ImageView addDocumentImage;
    Button uploadBTN;
    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;
    String vehicleId;
    ArrayList<String> titleList;
    ArrayList<String> titleIDList;
    DBAdapter db;
    Cursor titleCursor;
    String title_id;
    String ttt = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_vehical_document);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        imageString = null;

        db = new DBAdapter(this);
        db.open();

        vehicleId = getIntent().getStringExtra("vehicleId");

        vehicalTitle = (Spinner) findViewById(R.id.document_title_spinner);


         titleCursor = db.documentTitleList();
         titleList = new ArrayList<>();
        titleList.add("select type");
         titleIDList = new ArrayList<>();
        if (titleCursor.moveToFirst()) {
            do {
                String title = titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.DOCUMENT_TITLE));
                String title_id = titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.DOCUMENT_TITLE_ID));
                titleList.add(title);
                titleIDList.add(title_id);
            } while (titleCursor.moveToNext());
        }
        ArrayAdapter<String> title_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, titleList);

        title_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicalTitle.setAdapter(title_adapter);
        vehicalTitle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ttt = null;

                if (position>0) {

                    titleCursor.moveToPosition(position-1);
                    String title = titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.DOCUMENT_TITLE));
                    title_id = titleCursor.getString(titleCursor.getColumnIndex(DBAdapter.DOCUMENT_TITLE_ID));

                    ttt = title;

                    Log.v("title", title + "-" + title_id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        uploadBTN = (Button) findViewById(R.id.upload_Documnet_Btn);

        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ttt ==null){
                    Toast.makeText(getApplicationContext(),"Please select document type.",Toast.LENGTH_SHORT).show();
                } else if ( imageString == null || imageString.length()<10) {
                    Toast.makeText(getApplicationContext(),"Please click image.",Toast.LENGTH_SHORT).show();
                }else {

                    uploadVehicleDocument();
                }
            }
        });

        addDocumentImage = (ImageView) findViewById(R.id.add_vehical_document);

        addDocumentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    boolean resultCam = Utility.checkPermissionCamera(UploadVehicalDocumentActivity.this);
                    if (resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    boolean resultCam = Utility.checkPermissionGallery(UploadVehicalDocumentActivity.this);
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


    private void onCaptureImageResult(android.content.Intent data) {
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

        addDocumentImage.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(android.content.Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addDocumentImage.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        }catch (IllegalAccessError e) {
            e.printStackTrace();
        }
    }



    ProgressDialog dialog;
    SharedPreferences prefs;

    private void uploadVehicleDocument() {
        StringRequest uploadDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_VEHICLE_DOCUMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Upload Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {

                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Vehicle Document Uploaded successfully.";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                                    finish();

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();
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
                    prefs = getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("VehicleId", vehicleId);
                map.put("DocumentTitleId",title_id);
                map.put("Remark","NA");
                map.put("ImageBase64",imageString);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        dialog = ProgressDialog.show(this, "","Uploading vehicle Document.....", true);
        uploadDocRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(uploadDocRequest);

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }

}
