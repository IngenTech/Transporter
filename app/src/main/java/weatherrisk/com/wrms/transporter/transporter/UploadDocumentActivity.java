package weatherrisk.com.wrms.transporter.transporter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.dataobject.MaterialData;
import weatherrisk.com.wrms.transporter.utils.AppConstant;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

public class UploadDocumentActivity extends AppCompatActivity {

    public static final String ORDER_NO = "order_no";
    public static final String UPLOAD_TYPE = "upload_type";

    ImageView docImageView;
    EditText input_invoice_no;
    EditText input_invoice_amount;
    EditText input_invoice_date;
    EditText material_remark;
    EditText material_amount;
    Button btn_upload;
    RadioGroup docTypeRadioGroup;
    LinearLayout remark_layout;
    EditText input_remark;
    LinearLayout invoice_layout;
    LinearLayout addedMaterialLayout;
    Spinner materials;
    ImageButton addMaterial;

    String orderNo;
    String imageString;
    String uploadType = AppConstant.Constants.INVOICE_UPLOAD;
    String imagePath;
    ArrayList<MaterialData> materialArrayList = new ArrayList<>();
    LayoutInflater layoutInflater;

    MaterialData currentMaterial = new MaterialData();

    DBAdapter db;

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    EditText input_invoice_time;
    String mat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);


        docImageView = (ImageView) findViewById(R.id.docImageView);
        input_invoice_no = (EditText) findViewById(R.id.input_invoice_no);
        input_invoice_amount = (EditText) findViewById(R.id.input_invoice_amount);
        input_invoice_date = (EditText) findViewById(R.id.input_invoice_date);
        input_invoice_time = (EditText)findViewById(R.id.input_invoice_time);

        material_remark = (EditText) findViewById(R.id.materialRemark);
        material_amount = (EditText) findViewById(R.id.materialAmount);

        btn_upload = (Button) findViewById(R.id.btn_upload);
        docTypeRadioGroup = (RadioGroup) findViewById(R.id.docTypeRadioGroup);
        remark_layout = (LinearLayout) findViewById(R.id.remark_layout);
        input_remark = (EditText) findViewById(R.id.input_remark);
        invoice_layout = (LinearLayout) findViewById(R.id.invoice_layout);
        addedMaterialLayout = (LinearLayout) findViewById(R.id.addedMaterial);
        materials = (Spinner) findViewById(R.id.materials);
        addMaterial = (ImageButton) findViewById(R.id.addMaterial);
        layoutInflater = LayoutInflater.from(this);

        orderNo = getIntent().getStringExtra(ORDER_NO);

        db = new DBAdapter(this);
        db.open();

        input_invoice_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(UploadDocumentActivity.this);
                final View view = LayoutInflater.from(UploadDocumentActivity.this).inflate(R.layout.time_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface dialog, int arg1) {

                        TimePicker datePicker = (TimePicker) view.findViewById(R.id.timePicker1);

                        String selectedDate;
                        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                            String h = "00"+datePicker.getHour();
                            String m = "00"+datePicker.getMinute();
                            String s = "00"+datePicker.getMinute();
                            selectedDate = h.substring(Math.max(h.length() - 2, 0))+":"+m.substring(Math.max(m.length() - 2, 0))+":"+ s.substring(Math.max(s.length() - 2, 0));
                        } else {
                            String h = "00"+datePicker.getCurrentHour();
                            String m = "00"+datePicker.getCurrentMinute();
                            String s = "00"+datePicker.getCurrentMinute();
                            selectedDate = h.substring(Math.max(h.length() - 2, 0))+":"+m.substring(Math.max(m.length() - 2, 0))+":"+ s.substring(Math.max(s.length() - 2, 0));
                        }


                        input_invoice_time.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        addMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                materials.setSelection(0);

                if (materialArrayList.size()<3) {

                    if (isValidMaterial()) {
                        addMaterialInLayout(currentMaterial);
                    }
                }else {

                    Toast.makeText(getApplicationContext(),"You have already added 3 items.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        docTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.invoice_type) {
                    remark_layout.setVisibility(View.GONE);
                    invoice_layout.setVisibility(View.VISIBLE);
                    uploadType = AppConstant.Constants.INVOICE_UPLOAD;
                    imageString = null;
                    docImageView.setImageBitmap(null);
                    docImageView.setBackgroundResource(R.drawable.ic_camera_24dp);
                }
                if (i == R.id.roadpermit_type) {
                    remark_layout.setVisibility(View.VISIBLE);
                    invoice_layout.setVisibility(View.GONE);
                    uploadType = AppConstant.Constants.ROAD_PERMIT_UPLOAD;
                    imageString = null;
                    docImageView.setImageBitmap(null);
                    docImageView.setBackgroundResource(R.drawable.ic_camera_24dp);
                }
                if (i == R.id.declaration_type) {
                    remark_layout.setVisibility(View.VISIBLE);
                    invoice_layout.setVisibility(View.GONE);
                    uploadType = AppConstant.Constants.DECLARATION_UPLOAD;
                }
            }
        });


        input_invoice_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(UploadDocumentActivity.this);
                final View view = LayoutInflater.from(UploadDocumentActivity.this).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = DATE_FORMAT.format(date);
                        input_invoice_date.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });


        docImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent chooseImageIntent = ImagePicker.getPickImageIntent(UploadDocumentActivity.this, "");
                startActivityForResult(chooseImageIntent, REQUESTCODE_FOR_PROFILE_IMAGE);*/

                selectImage();
            }
        });

        final Cursor materialList = db.materialList();

        final ArrayList<String> materialNameArray = new ArrayList<>();

        materialNameArray.add("select material");

        if (materialList.moveToFirst()) {
            do {
                String materialString = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialNameArray.add(materialString);
            } while (materialList.moveToNext());
        }
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, materialNameArray);

        material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materials.setAdapter(material_adapter);
        materials.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                mat = null;

                if (pos>0) {
                    materialList.moveToPosition(pos-1);
                    String materialId = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_ID));
                    String materialName = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));

                    mat = materialName;

                    currentMaterial.setMaterialId(materialId);
                    currentMaterial.setMaterialName(materialName);
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(uploadType.equals(AppConstant.Constants.INVOICE_UPLOAD)) {
                    if (validate()) {
                        String invoiceNo = input_invoice_no.getText().toString();
                        String invoiceAmount = input_invoice_amount.getText().toString();
                        String invoiceDate = input_invoice_date.getText().toString();
                        String invoiceDateTime  = input_invoice_time.getText().toString();
                        String remark = input_remark.getText().toString();

                        if (invoiceNo == null || invoiceNo.length() < 2) {

                            input_invoice_no.setError("Enter valid Invoice No.");
                        } else if (invoiceAmount == null || invoiceAmount.length() < 2) {

                            input_invoice_amount.setError("Please Enter Valid Invoice Amount");
                        }else if (invoiceDate == null || invoiceDate.length() < 2) {

                            input_invoice_date.setError("Please Enter date");
                        }else if (invoiceDateTime == null || invoiceDateTime.length() < 2) {

                            input_invoice_time.setError("Please Enter Time");
                        }
                        else if (materialArrayList.size() < 1) {

                            Toast.makeText(getApplicationContext(), "Please enter materail", Toast.LENGTH_SHORT).show();
                        }else if (imageString==null){

                            Toast.makeText(getApplicationContext(), "Please Select Image", Toast.LENGTH_SHORT).show();

                        } else {

                            String materialString = "";
                            try {
                                JSONArray materialJsonArray = new JSONArray();
                                for (MaterialData materialData : materialArrayList) {
                                    JSONObject materialJsonObject = new JSONObject();
                                    materialJsonObject.put("material_type_id", materialData.getMaterialId());
                                    materialJsonObject.put("material_name", materialData.getMaterialRemark());
                                    materialJsonObject.put("amount", materialData.getMaterialAmount());

                                    materialJsonArray.put(materialJsonObject);

                                }
                                materialString = materialJsonArray.toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            System.out.println("materialString : " + materialString);

                            uploadInvoice(orderNo, invoiceNo, invoiceAmount, invoiceDate,invoiceDateTime, imageString, materialString, remark);

                        }
                    }
                }

                if(uploadType.equals(AppConstant.Constants.ROAD_PERMIT_UPLOAD)){

                    if (imageString==null){
                        Toast.makeText(getApplicationContext(), "Please Click Image", Toast.LENGTH_SHORT).show();

                    }else {

                        String remark = input_remark.getText().toString();
                        uploadRoadPermit(orderNo,imageString,remark);
                    }


                }

            }
        });

    }

    private boolean isValidMaterial() {
        boolean result = true;

        if (mat==null){
            Toast.makeText(UploadDocumentActivity.this, "Please select material", Toast.LENGTH_SHORT).show();
            return false;
        } else if (material_amount.getText().toString().trim().length() > 0) {
            currentMaterial.setMaterialAmount(material_amount.getText().toString());
        } else {
            Toast.makeText(UploadDocumentActivity.this, "Please enter material amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (material_remark.getText().toString().trim().length() > 0) {
            currentMaterial.setMaterialRemark(material_remark.getText().toString());
        }/*else{
            Toast.makeText(UploadDocumentActivity.this,"Please enter material remark",Toast.LENGTH_SHORT).show();
            return false;
        }*/

        return result;
    }

    private void addMaterialInLayout(final MaterialData materialData) {

        if (addedMaterialLayout.getChildCount() == 0) {
            // TITLE of the layout
            final View confirmedMaterialView = layoutInflater.inflate(R.layout.added_material_item, null, false);
            TextView materialName = (TextView) confirmedMaterialView.findViewById(R.id.materialName);
            materialName.setText("MATERIAL DETAIL");
            materialName.setTypeface(null, Typeface.BOLD);
            materialName.setGravity(Gravity.CENTER);

            TextView materialAmount = (TextView) confirmedMaterialView.findViewById(R.id.materialAmount);
            materialAmount.setVisibility(View.GONE);
            TextView materialRemark = (TextView) confirmedMaterialView.findViewById(R.id.materialRemark);
            materialRemark.setVisibility(View.GONE);
            ImageButton remove = (ImageButton) confirmedMaterialView.findViewById(R.id.cancleMaterial);
            remove.setVisibility(View.GONE);
            addedMaterialLayout.addView(confirmedMaterialView);

            final View titleMaterialView = layoutInflater.inflate(R.layout.added_material_item, null, false);
            ImageButton remove1 = (ImageButton) titleMaterialView.findViewById(R.id.cancleMaterial);
            remove1.setVisibility(View.INVISIBLE);
            addedMaterialLayout.addView(titleMaterialView);

        }

        final View confirmedMaterialView = layoutInflater.inflate(R.layout.added_material_item, null, false);
        TextView materialName = (TextView) confirmedMaterialView.findViewById(R.id.materialName);
        TextView materialAmount = (TextView) confirmedMaterialView.findViewById(R.id.materialAmount);
        TextView materialRemark = (TextView) confirmedMaterialView.findViewById(R.id.materialRemark);
        ImageButton remove = (ImageButton) confirmedMaterialView.findViewById(R.id.cancleMaterial);

        materialName.setText(currentMaterial.getMaterialName());
        materialAmount.setText(currentMaterial.getMaterialAmount());
        materialRemark.setText(currentMaterial.getMaterialRemark());

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewGroup) confirmedMaterialView.getParent()).removeView(confirmedMaterialView);
                Iterator<MaterialData> iter = materialArrayList.iterator();
                while (iter.hasNext()) {
                    if (iter.next().getMaterialId().equals(materialData.getMaterialId())) {
                        iter.remove();
                    }
                }
            }
        });

        materialArrayList.add(currentMaterial);

        String currentMaterialName = currentMaterial.getMaterialName();
        String currentMateriaId = currentMaterial.getMaterialId();


        currentMaterial = new MaterialData();
        currentMaterial.setMaterialName(currentMaterialName);
        currentMaterial.setMaterialId(currentMateriaId);
        material_amount.setText("");
        material_remark.setText("");

        addedMaterialLayout.addView(confirmedMaterialView);
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String response = "";
        if (data != null) {
            switch (requestCode) {
                case REQUESTCODE_FOR_PROFILE_IMAGE:
                    Bitmap bitmap = ImagePicker.getImageFromResult(UploadDocumentActivity.this, resultCode, data, "");
                    docImageView.setImageBitmap(bitmap);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }

    }*/

    public boolean validate() {
        boolean valid = true;

        String invoiceNo = input_invoice_no.getText().toString();
        String invoiceAmount = input_invoice_amount.getText().toString();
        String invoiceDate = input_invoice_date.getText().toString();
        String remark = input_invoice_date.getText().toString();

        if (uploadType.equals(AppConstant.Constants.INVOICE_UPLOAD)) {

            if (invoiceNo.trim().length() <= 0) {
                Toast.makeText(this, "Please enter invoice no", Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (invoiceAmount.trim().length() <= 0) {
                Toast.makeText(this, "Please enter invoice amount", Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (invoiceDate.trim().length() <= 0) {
                Toast.makeText(this, "Please enter invoice date", Toast.LENGTH_SHORT).show();
                valid = false;
            }

        }

        if (imageString == null || imageString.trim().length() == 0) {
            Toast.makeText(this, "Please select invoice image", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }



    ProgressDialog dialog;
    SharedPreferences prefs;

    private void uploadInvoice(final String orderNo,
                               final String invoiceNo,
                               final String invoiceAmount,
                               final String invoiceDate,
                               final String invoiceTime,
                               final String imageString,
                               final String materialString,
                               final String remark) {
        StringRequest uploadDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_TRIP_INVOICE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Upload Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Result")) {

                                String title = "INVOICE";
                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Uploaded Successfully";
                                    onUploadCompletion(title,message);

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    onUploadCompletion(title,message);
                                }
                            } else {
                                Toast.makeText(UploadDocumentActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(UploadDocumentActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(UploadDocumentActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = UploadDocumentActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("CreateId", accountId);
                map.put("OrderId", orderNo);
                map.put("InvoiceNumber", invoiceNo);
                map.put("InvoiceAmount", invoiceAmount);
                map.put("InvoiceDate", invoiceDate+" "+invoiceTime);
                map.put("Remark", remark);
                map.put("MaterialJsonData", materialString);
                map.put("ImageBase64", imageString);
                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        uploadDocRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(UploadDocumentActivity.this, "",
                "Uploading Invoice.....", true);
        AppController.getInstance().addToRequestQueue(uploadDocRequest);

    }

    private void uploadRoadPermit(final String orderNo, final String imageString, final String remark) {
        StringRequest uploadDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_ROAD_PERMIT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Upload Doc Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Result")) {
                                String title = "INVOICE";
                                /*{"result":"success","account_id":"103","road_permit_id":21,"message":"Road permit added successfully!"}*/
                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Uploaded Successfully";
                                    onUploadCompletion(title,message);

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    onUploadCompletion(title,message);
                                }
                            } else {
                                Toast.makeText(UploadDocumentActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(UploadDocumentActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(UploadDocumentActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = UploadDocumentActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("AccessToken", accessToken);
                map.put("UserId", accountId);
                map.put("OrderId", orderNo);
                map.put("Remark", remark);
                map.put("ImageBase64", imageString);
                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        uploadDocRequest.setRetryPolicy(new DefaultRetryPolicy(
                45000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        dialog = ProgressDialog.show(UploadDocumentActivity.this, "",
                "Uploading Invoice.....", true);
        AppController.getInstance().addToRequestQueue(uploadDocRequest);

    }

    private void onUploadCompletion(String title, String message){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(UploadDocumentActivity.this);
        builder.setTitle(title).
                setMessage(message).
                setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UploadDocumentActivity.this.finish();
                    }
                }).show();
    }




    // Vishal tripathi code from here

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
            case Utility.MY_PERMISSIONS_REQUEST_Camera:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }*/

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadDocumentActivity.this);
        builder.setTitle("Upload Document!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    boolean resultCam= Utility.checkPermissionCamera(UploadDocumentActivity.this);
                    if(resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    boolean resultCam= Utility.checkPermissionGallery(UploadDocumentActivity.this);
                    if(resultCam) {
                        galleryIntent();
                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
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

        docImageView.setImageBitmap(thumbnail);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.v("capturImage", String.valueOf(imageString.length()));
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        docImageView.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        Log.v("capturImage", String.valueOf(imageString.length()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                    || y < w.getTop() || y > w.getBottom()) ) {

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                }catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }



}
