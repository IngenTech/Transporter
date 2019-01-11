package weatherrisk.com.wrms.transporter.expenses;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.NoInternetConnectionAdapter;
import weatherrisk.com.wrms.transporter.adapter.ViewExpenseAdapter;
import weatherrisk.com.wrms.transporter.bean.ExpenseBean;
import weatherrisk.com.wrms.transporter.transporter.AddVehicalActivity;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Utility;

/**
 * Created by Admin on 09-05-2017.
 */
public class AddVehicleExpens  extends AppCompatActivity {

    ImageView addBillImage;
    Button uploadBTN;
    private int REQUEST_CAMERA_START = 0, SELECT_FILE_START = 1;
    String imageString;
    private String userChoosenTask;

    private Calendar calendar;
    private int year, month, day;
    static final int DATE_PICKER_ID = 1111;
    EditText input_date;
    EditText input_time;

    private Spinner vehicleSpinner,expenseType;
    DBAdapter db;

    ArrayList<String> vehicalTypeArray = new ArrayList<>();
    ArrayList<String> expenseTypeArray = new ArrayList<>();
    Cursor vehicleCursor;

    EditText amount,about,paidBy;
    String vehical_number,vehical_ID;
    String expense_type ;
    String expense_id;

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    String vh_s = null;
    String vh_t=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_expense);

        db = new DBAdapter(this);
        db.open();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        amount = (EditText)findViewById(R.id.expense_amount_edit);
        about = (EditText)findViewById(R.id.expense_detail);
        paidBy = (EditText)findViewById(R.id.expense_paid_by);

        input_date = (EditText) findViewById(R.id.input_date);
        input_time = (EditText)findViewById(R.id.input_time);

        vehicleSpinner = (Spinner)findViewById(R.id.driverSpinner);
        expenseType = (Spinner)findViewById(R.id.expenseTypeSpinner);

        vehicalTypeArray = new ArrayList<>();
        vehicalTypeArray.add("select vehicle");

        vehicleCursor = db.getVehicle();

        Log.v("lkslk", String.valueOf(vehicleCursor.getCount()));

        if (vehicleCursor.moveToFirst()) {
            do {
                String vehicleString = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_NO));
                vehicalTypeArray.add(vehicleString);
            } while (vehicleCursor.moveToNext());
        }
        ArrayAdapter<String> vehicle_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicalTypeArray);

        vehicle_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(vehicle_adapter);
        vehicleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                vh_s = null;
                if (pos>0) {
                    vehicleCursor.moveToPosition(pos-1);

                    vehical_number = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_NO));
                    vehical_ID = vehicleCursor.getString(vehicleCursor.getColumnIndex(DBAdapter.VEHICLE_ID));

                    vh_s = vehical_number;
                    Log.v("klsnmklcsm", vehical_ID + "," + vehical_number);
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        expenseTypeArray = new ArrayList<>();
        expenseTypeArray.add("select type");

        final Cursor expenseCursor = db.expenseTypeList();

        if (expenseCursor.moveToFirst()) {
            do {
                String expenseString = expenseCursor.getString(expenseCursor.getColumnIndex(DBAdapter.EXPENSE_TYPE));
                expenseTypeArray.add(expenseString);
            } while (expenseCursor.moveToNext());
        }
        ArrayAdapter<String> expense_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, expenseTypeArray);

        vehicle_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseType.setAdapter(expense_adapter);
        expenseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                vh_t = null;
                if (pos>0) {
                    expenseCursor.moveToPosition(pos-1);
                    expense_type = expenseCursor.getString(expenseCursor.getColumnIndex(DBAdapter.EXPENSE_TYPE));
                    expense_id = expenseCursor.getString(expenseCursor.getColumnIndex(DBAdapter.EXPENSE_TYPE_ID));
                    vh_t = expense_type;

                    Log.v("klsnmklcsm", expense_id + "," + expense_type);
                }

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });



        addBillImage = (ImageView) findViewById(R.id.expense_bill_image);
        uploadBTN = (Button)findViewById(R.id.submitDriverExpenses);

        uploadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addVehicleExpense();
            }
        });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        addBillImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        input_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(AddVehicleExpens.this);
                final View view = LayoutInflater.from(AddVehicleExpens.this).inflate(R.layout.date_picker, null);
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
                        input_date.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        input_time.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(AddVehicleExpens.this);
                final View view = LayoutInflater.from(AddVehicleExpens.this).inflate(R.layout.time_picker, null);
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


                        input_time.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

    }

    public void addVehicleExpense(){
        String amt = amount.getText().toString();
        String paid_By = paidBy.getText().toString();
        String paidDate = input_date.getText().toString();
        String paidTime = input_time.getText().toString();
        String detail = about.getText().toString();

        if (vh_s==null){
            Toast.makeText(getApplicationContext(),"Please select vehicle.",Toast.LENGTH_SHORT).show();
        }
        else if (amt==null || amt.length()<1){

            Toast.makeText(getApplicationContext(),"Please enter amount.",Toast.LENGTH_SHORT).show();
        }else if (vh_t==null){

            Toast.makeText(getApplicationContext(),"Please select expense type.",Toast.LENGTH_SHORT).show();
        }
        else if (detail==null || detail.length()<1){

            Toast.makeText(getApplicationContext(),"Please Enter in detail.",Toast.LENGTH_SHORT).show();
        }
        else if (paidDate==null || paidDate.length()<1){

            Toast.makeText(getApplicationContext(),"Please select paid date.",Toast.LENGTH_SHORT).show();
        }else if (paidTime==null || paidTime.length()<1){

            Toast.makeText(getApplicationContext(),"Please select paid Time.",Toast.LENGTH_SHORT).show();
        }
        else if (paid_By==null || paid_By.length()<1){

            Toast.makeText(getApplicationContext(),"Please select paid by.",Toast.LENGTH_SHORT).show();
        }else if (imageString==null || imageString.length()<1){

            Toast.makeText(getApplicationContext(),"Please select image",Toast.LENGTH_SHORT).show();
        }
        else {

            addVehicleExpenseMethod(amt,paid_By,paidDate+" "+paidTime,detail);

        }
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
                    boolean resultCam = Utility.checkPermissionCamera(AddVehicleExpens.this);
                    if (resultCam) {
                        cameraIntent();
                    }

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    boolean resultCam = Utility.checkPermissionGallery(AddVehicleExpens.this);
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

        addBillImage.setImageBitmap(thumbnail);

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

        addBillImage.setImageBitmap(bm);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

        try {
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        }catch (IllegalAccessError e) {
            e.printStackTrace();
        }
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

    ProgressDialog dialog;
    SharedPreferences prefs;

    private void addVehicleExpenseMethod(final String amount, final String paidBy, final String paidDate, final String details) {
        StringRequest viewDocRequest = new StringRequest(Request.Method.POST, MyUtility.URL.ADD_VEHICLE_EXPENSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String uploadDocResponse) {
                        dialog.dismiss();
                        try {
                            System.out.println("Add Vehicle Expense list Response : " + uploadDocResponse);
                            JSONObject jsonObject = new JSONObject(uploadDocResponse);

                            if (jsonObject.has("Status")&&(jsonObject.getString("Status").equalsIgnoreCase("1"))) {

                                if (jsonObject.get("Result").equals("Success")) {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be fetch";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();



                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "Can't be Uploaded";
                                    Toast.makeText(getApplicationContext(),message+"",Toast.LENGTH_SHORT).show();

                                }

                                finish();
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
                map.put("VehicleId", vehical_ID);
                map.put("ExpenseAmount", amount);
                map.put("ExpenseDetail", details);
                map.put("ExpenseTypeId", expense_id);
                map.put("BillDate", paidDate);
                map.put("PaidBy", paidBy);
                map.put("Remark","paid");
                map.put("CopyOfBill", imageString);

                for (Map.Entry<String, String> entry : map.entrySet()) {

                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        viewDocRequest.setRetryPolicy(new DefaultRetryPolicy(45 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        dialog = ProgressDialog.show(this, "","Adding Expense .....", true);
        AppController.getInstance().addToRequestQueue(viewDocRequest);

    }

}