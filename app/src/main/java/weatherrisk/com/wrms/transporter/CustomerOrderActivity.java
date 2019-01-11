package weatherrisk.com.wrms.transporter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.utils.Base64;
import weatherrisk.com.wrms.transporter.dataobject.CustomerOrderData;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.RoadPermitData;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;

public class CustomerOrderActivity extends AppCompatActivity {

    public static final String TRANSPORTER_LIST = "transporter_list";
    public static final String TRANSPORTER_DATA = "transporter_data";

    EditText fromCity;
    EditText fromAddress;
    EditText toCity;
    EditText toAddress;
    EditText transporterName;
    EditText dispatchDate;
    EditText doorStatusRefrigerated;
    Button addInvoice;
    LinearLayout confirmInvoice;
    LinearLayout addInvoiceLayout;
    TextView invoiceInstruction;
    EditText invoiceAmount;
    EditText invoiceNumber;
    EditText invoiceDate;
    EditText remark;
    ImageView invoiceView;
    ImageButton uploadInvoice;
    Button upload;
    LinearLayout confirmRoadPermit;
    LinearLayout roadPermitLayout;
    TextView roadPermitInstruction;
    EditText roadPermitRemark;
    Button addRoadPermit;
    Button uploadRoadPermit;
    ImageView roadPermitView;
    ImageButton uploadRoadPermitImage;
    LinearLayout materialContainer;
    Spinner materialType;
    Spinner invoiceId;
    EditText materialRemark;
    EditText materialAmount;
    EditText orderRemark;
    Button addMaterial;
    Button submitOrder;

    ProgressDialog progressDialog;
    SharedPreferences prefs;

    DBAdapter db;
    Cursor stateListCursor;

    CustomerOrderData customerOrderData;

    public static final int BROWSE_INVOICE = 101;
    public static final int BROWSE_ROAD_PERMIT = 102;

    private static String ORDER_DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat ORDER_DATE_FORMAT = new SimpleDateFormat(ORDER_DATE_FORMAT_STRING);
    ArrayList<InvoiceData> invoiceArray = new ArrayList<>();
    InvoiceData invoiceData;

    ArrayList<RoadPermitData> roadPermitArray = new ArrayList<>();
    RoadPermitData roadPermitData;

    ArrayList<MaterialTypeData> materialArray = new ArrayList<>();
    MaterialTypeData materialTypeData;

    LayoutInflater innerInflater;
    private TransporterData transporterData;
    private ArrayList<TransporterData> transporterDatas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);
//        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();

        innerInflater = LayoutInflater.from(this);

        fromCity = (EditText) findViewById(R.id.fromCity);
        fromAddress = (EditText) findViewById(R.id.fromAddress);
        toCity = (EditText) findViewById(R.id.toCity);
        toAddress = (EditText) findViewById(R.id.toAddress);
        transporterName = (EditText) findViewById(R.id.transporterName);
        dispatchDate = (EditText) findViewById(R.id.dispatchDate);
        /*materialTypeName = (EditText) findViewById(R.id.materialTypeName);
        item = (EditText) findViewById(R.id.item);*/
        doorStatusRefrigerated = (EditText) findViewById(R.id.doorStatusRefrigerated);
        addInvoice = (Button) findViewById(R.id.addInvoice);
        confirmInvoice = (LinearLayout) findViewById(R.id.confirmInvoice);
        addInvoiceLayout = (LinearLayout) findViewById(R.id.addInvoiceLayout);
        invoiceInstruction = (TextView) findViewById(R.id.invoiceInstruction);
        invoiceAmount = (EditText) findViewById(R.id.invoiceAmount);
        invoiceNumber = (EditText) findViewById(R.id.invoiceNumber);
        invoiceDate = (EditText) findViewById(R.id.invoiceDate);
        remark = (EditText) findViewById(R.id.remark);
        invoiceView = (ImageView) findViewById(R.id.invoiceView);
        uploadInvoice = (ImageButton) findViewById(R.id.uploadInvoice);
        upload = (Button) findViewById(R.id.upload);
        confirmRoadPermit = (LinearLayout) findViewById(R.id.confirmRoadPermit);
        addRoadPermit = (Button) findViewById(R.id.addRoadPermit);
        roadPermitLayout = (LinearLayout) findViewById(R.id.roadPermitLayout);
        roadPermitInstruction = (TextView) findViewById(R.id.roadPermitInstruction);
        roadPermitRemark = (EditText) findViewById(R.id.roadPermitRemark);
        uploadRoadPermit = (Button) findViewById(R.id.uploadRoadPermit);
        roadPermitView = (ImageView) findViewById(R.id.roadPermitView);
        uploadRoadPermitImage = (ImageButton) findViewById(R.id.uploadRoadPermitImage);
        materialContainer = (LinearLayout) findViewById(R.id.materialContainer);
        materialType = (Spinner) findViewById(R.id.materialType);
        invoiceId = (Spinner) findViewById(R.id.invoiceId);
        materialRemark = (EditText) findViewById(R.id.materialRemark);
        materialAmount = (EditText) findViewById(R.id.materialAmount);
        addMaterial = (Button) findViewById(R.id.addMaterial);
        submitOrder = (Button) findViewById(R.id.submitOrder);
        orderRemark = (EditText)findViewById(R.id.orderRemark);

        transporterData = getIntent().getParcelableExtra(TRANSPORTER_DATA);
        transporterDatas = getIntent().getParcelableArrayListExtra(TRANSPORTER_LIST);

        materialTypeData = new MaterialTypeData();
        customerOrderData = new CustomerOrderData();
        db = new DBAdapter(this);
        db.open();


        invoiceData = new InvoiceData();
        roadPermitData = new RoadPermitData();
        String currentDate = ORDER_DATE_FORMAT.format(new Date());
        invoiceDate.setText(currentDate);

        if (stateListCursor == null) {
            stateListCursor = db.stateList();
        }


        dispatchDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(CustomerOrderActivity.this);
                final View view = LayoutInflater.from(CustomerOrderActivity.this).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = ORDER_DATE_FORMAT.format(date);
                        dispatchDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        uploadInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(CustomerOrderActivity.this.getPackageManager()) != null) {
//                    getParentFragment().startActivityForResult(i, BROWSE_INVOICE);
                    startActivityForResult(i, BROWSE_INVOICE);
                } else {
                    Toast.makeText(CustomerOrderActivity.this, "Do not found app for this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadRoadPermitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(CustomerOrderActivity.this.getPackageManager()) != null) {
//                    getParentFragment().startActivityForResult(i, BROWSE_ROAD_PERMIT);
                    startActivityForResult(i, BROWSE_ROAD_PERMIT);
                } else {
                    Toast.makeText(CustomerOrderActivity.this, "Do not found app for this action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        invoiceDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(CustomerOrderActivity.this);
                final View view = LayoutInflater.from(CustomerOrderActivity.this).inflate(R.layout.date_picker, null);
                adb.setView(view);
                final Dialog dialog;
                adb.setPositiveButton("Add", new android.content.DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                        DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
                        java.util.Date date = null;
                        Calendar cal = GregorianCalendar.getInstance();
                        cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                        date = cal.getTime();
                        String selectedDate = ORDER_DATE_FORMAT.format(date);
                        invoiceDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isValidInvoice()) {
                    upLoadInvoice();
                }
            }
        });

        addInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (invoiceArray.size() <= 5) {
                    addInvoiceLayout.setVisibility(View.VISIBLE);
                    addInvoice.setVisibility(View.GONE);
                } else {
                    Toast.makeText(CustomerOrderActivity.this, "5 invoices has been uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploadRoadPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidRoadPermit()) {
                    upLoadRoadPermit();
                }
            }
        });

        addRoadPermit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (roadPermitArray.size() <= 2) {
                    roadPermitLayout.setVisibility(View.VISIBLE);
                    addRoadPermit.setVisibility(View.GONE);
                } else {
                    Toast.makeText(CustomerOrderActivity.this, "2 Road permit has been uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Cursor materialList = db.materialList();

        final ArrayList<String> materialNameArray = new ArrayList<>();
        if (materialList.moveToFirst()) {
            do {
                String materialString = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialNameArray.add(materialString);
            } while (materialList.moveToNext());
        }
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(CustomerOrderActivity.this,
                android.R.layout.simple_spinner_item, materialNameArray);

        material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        materialType.setAdapter(material_adapter);
        materialType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                materialList.moveToPosition(pos);
                String materialId = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_ID));
                String materialName = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                materialTypeData.setMaterialTypeName(materialName);
                materialTypeData.setMaterialTypeId(materialId);
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        addMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidMaterial()) {

                    if (materialArray.size() == 0) {

                        final View confirmedProduct0 = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                        TextView innerProductName0 = (TextView) confirmedProduct0.findViewById(R.id.sno);
                        innerProductName0.setText("Added Materials");
                        innerProductName0.setTypeface(Typeface.DEFAULT_BOLD);
                        innerProductName0.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        TextView productUOM0 = (TextView) confirmedProduct0.findViewById(R.id.materialTypeId);
                        productUOM0.setVisibility(View.GONE);
                        TextView productQty0 = (TextView) confirmedProduct0.findViewById(R.id.materialAmount1);
                        productQty0.setVisibility(View.GONE);
                        ImageButton remove0 = (ImageButton) confirmedProduct0.findViewById(R.id.remove);
                        remove0.setVisibility(View.GONE);

                        materialContainer.addView(confirmedProduct0);

                        final View confirmedProduct = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                        TextView innerProductName = (TextView) confirmedProduct.findViewById(R.id.sno);
                        innerProductName.setText("Invoice");
                        innerProductName.setTypeface(Typeface.DEFAULT_BOLD);
                        TextView innerProductUOM = (TextView) confirmedProduct.findViewById(R.id.materialTypeId);
                        innerProductUOM.setText("Material");
                        innerProductUOM.setTypeface(Typeface.DEFAULT_BOLD);
                        TextView innerProductQty = (TextView) confirmedProduct.findViewById(R.id.materialAmount1);
                        innerProductQty.setText("Amount");
                        innerProductQty.setTypeface(Typeface.DEFAULT_BOLD);
                        ImageButton remove = (ImageButton) confirmedProduct.findViewById(R.id.remove);
                        remove.setVisibility(View.INVISIBLE);

                        materialContainer.addView(confirmedProduct);
                    }

                    final String materialEntryId = String.valueOf(System.currentTimeMillis());
                    materialTypeData.setId(materialEntryId);

                    materialTypeData.setRemark(materialRemark.getText().toString());
                    double amount = 0.0;
                    try {
                        amount = Double.parseDouble(materialAmount.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    materialTypeData.setAmount(String.valueOf(amount));

                    materialArray.add(materialTypeData);

                    Toast.makeText(CustomerOrderActivity.this, "Material Uploaded", Toast.LENGTH_SHORT).show();

//                    sno, materialTypeId, materialAmount, remove

                    final View confirmedMaterial = innerInflater.inflate(R.layout.confirmed_material_type, null, false);
                    TextView sn1 = (TextView) confirmedMaterial.findViewById(R.id.sno);
                    sn1.setText(materialTypeData.getInvoiceId());
                    TextView materialTypeId = (TextView) confirmedMaterial.findViewById(R.id.materialTypeId);
                    materialTypeId.setText(materialTypeData.getMaterialTypeName());
                    TextView materialAmount1 = (TextView) confirmedMaterial.findViewById(R.id.materialAmount1);
                    System.out.println("Material Amount :" + String.valueOf(materialTypeData.getAmount()));
                    materialAmount1.setText(String.valueOf(materialTypeData.getAmount()));

                    ImageButton remove = (ImageButton) confirmedMaterial.findViewById(R.id.remove);
                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ViewGroup) confirmedMaterial.getParent()).removeView(confirmedMaterial);
                            for (MaterialTypeData mData : materialArray) {
                                if (mData.getId().equals(materialEntryId)) {
                                    materialArray.remove(mData);
                                    break;
                                }
                            }

                            for (MaterialTypeData mData : materialArray) {
                                System.out.println("Material " + mData.getId() + " , " + mData.getMaterialTypeId() + " , " + mData.getAmount());
                            }


                        }
                    });

                    materialContainer.addView(confirmedMaterial);

                    String invoiceIsString = materialTypeData.getInvoiceId();

                    materialTypeData = new MaterialTypeData();
                    materialTypeData.setInvoiceId(invoiceIsString);

                    materialType.setSelection(0);
//                    invoiceId.setSelection(0);
                    materialRemark.setText("");
                    materialAmount.setText("");

                    double totalAmount = 0.0;
                    for (MaterialTypeData mData : materialArray) {
                        if (mData.getInvoiceId().equals(materialTypeData.getInvoiceId())) {
                            double tempAmount = Double.parseDouble(mData.getAmount());
                            totalAmount = tempAmount + totalAmount;
                        }
                    }

                    for (InvoiceData iData : invoiceArray) {
                        if (iData.getInvoiceNumber().equals(materialTypeData.getInvoiceId())) {
                            double invoiceAmount = 0.0;
                            try {
                                invoiceAmount = Double.parseDouble(iData.getInvoiceAmount());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (!(invoiceAmount == totalAmount)) {
                                new AlertDialog.Builder(CustomerOrderActivity.this)
                                        .setMessage("Invoice Amount is " + invoiceAmount + " and Aaterial Amount is " + totalAmount)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();
                            }

                            break;
                        }
                    }

//                    materialTypeData = new MaterialTypeData();
                }
            }
        });


        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInvoiceMaterialJson();
                if (isValidOrder()) {
                    String invoiceJsonData = getInvoiceMaterialJson();
                    String roadPermitTempIds = "";
                    for (RoadPermitData rdData : roadPermitArray) {
                        roadPermitTempIds = roadPermitTempIds + rdData.getRoadPermitReturnId() + ",";
                    }

                    //remove last comma from string
                    if (roadPermitTempIds.length() > 2) {
                        roadPermitTempIds = roadPermitTempIds.substring(0, roadPermitTempIds.length() - 1);
                    }
                    addCustomerOrder(invoiceJsonData, roadPermitTempIds);
                }
            }
        });


        if (transporterData != null) {

            setTransporterData(transporterData);
        }

    }

    private void addCustomerOrder(final String invoiceJsonData, final String roadPermitTempIds) {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.
                ADD_CUSTOMER_ORDER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {
                            System.out.println("Add Trip Response : " + trackResponse);
                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    new AlertDialog.Builder(CustomerOrderActivity.this)
                                            .setMessage("Trip has been added successfully")
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    /*Intent intent = new Intent(AddTripActivity.this, MainActivity.class);
                                                    intent.putExtra(ACTIVITY_TAG,ACTIVITY_TAG);
                                                    startActivity(intent);*/
                                                    CustomerOrderActivity.this.finish();
                                                }
                                            })
                                            .show();

                                } else {
                                    Toast.makeText(CustomerOrderActivity.this, "Request has been denied by server", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(CustomerOrderActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CustomerOrderActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(CustomerOrderActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = CustomerOrderActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, CustomerOrderActivity.this.MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                map.put("CustomerID", accountId);
                map.put("TransporterFirmID", transporterData.getFirmId());
                map.put("FromCityID", customerOrderData.getFromCityId());
                map.put("ToCityID", customerOrderData.getToCityId());
                map.put("FromAddress", customerOrderData.getFromAddress());
                map.put("ToAddress", customerOrderData.getToAddress());
                map.put("AvailableVehicles", transporterData.getAvailableVehicle());
                map.put("Capacity", transporterData.getCapacity());
                map.put("invoice_json_data", invoiceJsonData);
                map.put("road_permit_temporary_ids", roadPermitTempIds);
                map.put("Rate", transporterData.getRate());
                String doorClosed = "0";
                if (transporterData.getDoorClosed().equalsIgnoreCase("door_closed")) {
                    doorClosed = "1";
                }
                map.put("DoorStatus", doorClosed);
                String refrigeratedString = "0";
                if (transporterData.getRefrigerated().equalsIgnoreCase("refrigerated")) {
                    refrigeratedString = "1";
                }
                map.put("Referigerated", refrigeratedString);
                map.put("TripDate", customerOrderData.getDispatchDate());
                map.put("Remark", customerOrderData.getOrderRemark());

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        progressDialog = ProgressDialog.show(CustomerOrderActivity.this, "Order Upload",
                "Please wait .....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }


    boolean isValidOrder() {
        boolean isValid = true;

        if (!(customerOrderData.getFromStateId() != null && customerOrderData.getFromStateId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select source state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(customerOrderData.getFromCityId() != null && customerOrderData.getFromCityId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select source city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromAddress.getText() != null && fromAddress.getText().toString().trim().length() > 0) {
            customerOrderData.setFromAddress(fromAddress.getText().toString());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter source address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!(customerOrderData.getToStateId() != null && customerOrderData.getToStateId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select destination state", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(customerOrderData.getToCityId() != null && customerOrderData.getToCityId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select destination city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (toAddress.getText() != null && toAddress.getText().toString().trim().length() > 0) {
            customerOrderData.setToAddress(toAddress.getText().toString());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter destination address", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (orderRemark.getText() != null && orderRemark.getText().toString().trim().length() > 0) {
            customerOrderData.setOrderRemark(orderRemark.getText().toString());
        } else {
            customerOrderData.setOrderRemark("");
            Toast.makeText(CustomerOrderActivity.this, "Order remark is blank", Toast.LENGTH_SHORT).show();
        }


        if (dispatchDate.getText() != null && dispatchDate.getText().toString().trim().length() > 0) {
            try {
                String dispatchDateString = dispatchDate.getText().toString();
                Date dispatchDateObj = ORDER_DATE_FORMAT.parse(dispatchDateString);
                customerOrderData.setDispatchDate(dispatchDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(CustomerOrderActivity.this, "Please enter date in " + ORDER_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please select dispatch date", Toast.LENGTH_SHORT).show();
            return false;
        }

        /*if (arrivalDate.getText() != null && arrivalDate.getText().toString().trim().length() > 0) {
            try {
                Date arrivalDateObj = TRIP_DATE_FORMAT.parse(arrivalDate.getText().toString());
                String arrivalDateString  = arrivalDate.getText().toString();
                tripData.setArrivalDate(arrivalDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(CustomerOrderActivity.this, "Please enter date in " + TRIP_DATE_FORMAT_STRING + " format", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please arrival date", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        if (toAddress.getText() != null && toAddress.getText().toString().trim().length() > 0) {
            customerOrderData.setToAddress(toAddress.getText().toString());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter driver name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fromAddress.getText() != null && fromAddress.getText().toString().trim().length() > 0) {
            customerOrderData.setFromAddress(fromAddress.getText().toString());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter from address", Toast.LENGTH_SHORT).show();
            return false;
        }


        for (InvoiceData iData : invoiceArray) {

            double totalAmount = 0.0;
            for (MaterialTypeData mData : materialArray) {
                if (mData.getInvoiceId().equals(iData.getInvoiceNumber())) {
                    double tempAmount = Double.parseDouble(mData.getAmount());
                    totalAmount = tempAmount + totalAmount;
                }
            }

            double invoiceAmount = 0.0;
            try {
                invoiceAmount = Double.parseDouble(iData.getInvoiceAmount());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!(invoiceAmount == totalAmount)) {
                new AlertDialog.Builder(CustomerOrderActivity.this)
                        .setMessage("Invoice Amount is " + invoiceAmount + " and Material Amount is " + totalAmount)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return false;
            }

        }

        return isValid;
    }


    private String getInvoiceMaterialJson() {
        String jsonString = null;

        try {
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();
            if(invoiceArray.size()==0){
                return "";
            }
            for (InvoiceData invoiceData : invoiceArray) {
                JSONObject jObject = new JSONObject();
                jObject.put("invoice_id", invoiceData.getInvoiceTempId());

                JSONArray materialJsonArray = new JSONArray();
                for (MaterialTypeData materialData : materialArray) {
                    if (materialData.getInvoiceId().equals(invoiceData.getInvoiceNumber())) {
                        JSONObject materialJsonObject = new JSONObject();
                        materialJsonObject.put("material_type_id", materialData.getMaterialTypeId());
                        materialJsonObject.put("material_name", materialData.getMaterialTypeName());
                        materialJsonObject.put("amount", materialData.getAmount());
                        materialJsonArray.put(materialJsonObject);
                    }

                }
                jObject.put("materials", materialJsonArray);


                jsonArray.put(jObject);
            }
            jsonObject.put("invoices", jsonArray);
            jsonString = jsonObject.toString();
            System.out.println("jsonString : " + jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonString;
    }


    private void setTransporterData(TransporterData transporterData) {
        fromCity.setText(transporterData.getFromCityName() + " , " + transporterData.getFromStateName());
        customerOrderData.setFromCityId(transporterData.getFromCityId());
        customerOrderData.setFromStateId(transporterData.getFromStateId());
        toCity.setText(transporterData.getToCityName() + " , " + transporterData.getToStateName());
        customerOrderData.setToCityId(transporterData.getToCityId());
        customerOrderData.setToStateId(transporterData.getToStateId());
        transporterName.setText(transporterData.getFirmName());
        customerOrderData.setMaterialTypeId(transporterData.getMaterialId());
        /*Date dispatchDate = new Date();
        try{
            dispatchDate = ORDER_DATE_FORMAT.parse(transporterData.ge)
        }catch (DataFormatException dfe){

        }*/
        /*if(transporterData.getMaterialId()!=null && transporterData.getMaterialId().trim().length()>0) {
            Cursor materialCursor = db.materialById(transporterData.getMaterialId());
            if(materialCursor.moveToFirst()){
                materialTypeName.setText(materialCursor.getString(materialCursor.getColumnIndex(DBAdapter.MATERIAL_NAME)));
            }
        }*/
        String doorStatusRefrigeratedString = transporterData.getDoorClosed() + " / " + transporterData.getRefrigerated();
        if (transporterData.getDoorClosed().equals("1")) {
            doorStatusRefrigeratedString = "CLOSE / ";
        } else {
            doorStatusRefrigeratedString = "OPEN / ";
        }

        if (transporterData.getRefrigerated().equals("1")) {
            doorStatusRefrigeratedString = doorStatusRefrigeratedString + "REFRIGERATED";
        } else {
            doorStatusRefrigeratedString = doorStatusRefrigeratedString + "NON-REFRIGERATED";
        }

        doorStatusRefrigerated.setText(doorStatusRefrigeratedString);
        fromCity.setEnabled(false);
        toCity.setEnabled(false);
        transporterName.setEnabled(false);
        doorStatusRefrigerated.setEnabled(false);

    }


    boolean isValidInvoice() {
        boolean isValid = true;

        if (invoiceAmount.getText() != null && invoiceAmount.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceAmount(invoiceAmount.getText().toString().trim());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter invoice amount", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (invoiceNumber.getText() != null && invoiceNumber.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceNumber(invoiceNumber.getText().toString().trim());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter invoice number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (invoiceDate.getText() != null && invoiceDate.getText().toString().trim().length() > 0) {
            invoiceData.setDate(invoiceDate.getText().toString().trim());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter invoice date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (remark.getText() != null && remark.getText().toString().trim().length() > 0) {
            invoiceData.setInvoiceRemark(remark.getText().toString().trim());
        }

        if (!(invoiceData.getInvoicePath() != null && invoiceData.getInvoicePath().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select invoice image", Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    boolean isValidRoadPermit() {
        boolean isValid = true;

        if (roadPermitRemark.getText() != null && roadPermitRemark.getText().toString().trim().length() > 0) {
            roadPermitData.setRoadPermitRemark(roadPermitRemark.getText().toString().trim());
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter road permit remark", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(roadPermitData.getRoadPermitImageString() != null && roadPermitData.getRoadPermitImageString().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select road permit image", Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }

    private void upLoadRoadPermit() {

        StringRequest stringRoadPermitRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_ROAD_PERMIT_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String roadPermitResponse) {
                        try {
                            System.out.println("Road Permit Response : " + roadPermitResponse);
//                            Add Trip Response : {"result":"success","invoice_temporary_id":1}
                            JSONObject jsonObject = new JSONObject(roadPermitResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    Toast.makeText(CustomerOrderActivity.this, "Road Permit Uploaded", Toast.LENGTH_SHORT).show();

                                    String roadPermitTemporaryId = jsonObject.getString("road_permit_temporary_id");
                                    roadPermitData.setRoadPermitReturnId(roadPermitTemporaryId);
                                    String roadPermitIds = customerOrderData.getRoadPermitTempIds();
                                    if (roadPermitIds != null) {
                                        customerOrderData.setRoadPermitTempIds(roadPermitIds + roadPermitTemporaryId + ",");
                                    } else {
                                        customerOrderData.setRoadPermitTempIds(roadPermitTemporaryId + ",");
                                    }

                                    if (roadPermitArray.size() == 0) {
                                        View confirmedInvoice0 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn0 = (TextView) confirmedInvoice0.findViewById(R.id.sno);
                                        sn0.setText("Uploaded Road Permits");
                                        sn0.setTypeface(Typeface.DEFAULT_BOLD);
                                        sn0.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        TextView invoiceNo0 = (TextView) confirmedInvoice0.findViewById(R.id.invoiceNo);
                                        invoiceNo0.setVisibility(View.GONE);
                                        TextView invoiceDate0 = (TextView) confirmedInvoice0.findViewById(R.id.invoiceDate);
                                        invoiceDate0.setVisibility(View.GONE);
                                        TextView amount0 = (TextView) confirmedInvoice0.findViewById(R.id.amount);
                                        amount0.setVisibility(View.GONE);
                                        confirmRoadPermit.addView(confirmedInvoice0);


                                        View confirmRoadPermitView = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn1 = (TextView) confirmRoadPermitView.findViewById(R.id.sno);
                                        sn1.setText("Road Permit Remark");
                                        TextView invoiceNo1 = (TextView) confirmRoadPermitView.findViewById(R.id.invoiceNo);
                                        invoiceNo1.setText("Temporary Id");
                                        TextView invoiceDate1 = (TextView) confirmRoadPermitView.findViewById(R.id.invoiceDate);
                                        invoiceDate1.setVisibility(View.GONE);
                                        TextView amount1 = (TextView) confirmRoadPermitView.findViewById(R.id.amount);
                                        amount1.setVisibility(View.GONE);
                                        confirmRoadPermit.addView(confirmRoadPermitView);
                                    }

                                    View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                    TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                    sn1.setText(roadPermitData.getRoadPermitRemark());
                                    TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                    invoiceNo1.setText(roadPermitData.getRoadPermitReturnId());

                                    TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                    invoiceDate1.setVisibility(View.GONE);
                                    TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                    amount1.setVisibility(View.GONE);

                                    confirmRoadPermit.addView(confirmedInvoice1);


                                    roadPermitArray.add(roadPermitData);
                                    roadPermitData = new RoadPermitData();
                                    roadPermitRemark.setText("");
                                    roadPermitView.setImageDrawable(null);

                                    roadPermitLayout.setVisibility(View.GONE);
                                    addRoadPermit.setVisibility(View.VISIBLE);
                                    int restRoadPermit = (2 - roadPermitArray.size());
                                    roadPermitInstruction.setText("You can upload " + restRoadPermit + " more road permit");

                                } else {
                                    Toast.makeText(CustomerOrderActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(CustomerOrderActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CustomerOrderActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(CustomerOrderActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }

        )

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = CustomerOrderActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, CustomerOrderActivity.this.MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                map.put("api_key", apiKey);
                map.put("account_id", accountId);

                map.put("road_permit_image_base64", roadPermitData.getRoadPermitImageString());
                map.put("remark", roadPermitData.getRoadPermitRemark());

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringRoadPermitRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(CustomerOrderActivity.this, "",
                "Fetching Track Data.....", true);
        AppController.getInstance().

                addToRequestQueue(stringRoadPermitRequest);

    }

    private void upLoadInvoice() {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.UPLOAD_TRIP_INVOICE_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String trackResponse) {
                        try {
                            System.out.println("Add Trip Response : " + trackResponse);
//                            Add Trip Response : {"result":"success","invoice_temporary_id":1}
                            JSONObject jsonObject = new JSONObject(trackResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.get("result").equals("success")) {
                                    Toast.makeText(CustomerOrderActivity.this, "Invoice Uploaded", Toast.LENGTH_SHORT).show();

                                    String invoiceTemporaryId = jsonObject.getString("invoice_temporary_id");
                                    String tripDataInvoiceIds = customerOrderData.getInvoiceIds();
                                    invoiceData.setInvoiceTempId(invoiceTemporaryId);
                                    if (tripDataInvoiceIds != null) {
                                        customerOrderData.setInvoiceIds(tripDataInvoiceIds + invoiceTemporaryId + ",");
                                    } else {
                                        customerOrderData.setInvoiceIds(invoiceTemporaryId + ",");
                                    }

                                    if (invoiceArray.size() == 0) {

                                        View confirmedInvoice0 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn0 = (TextView) confirmedInvoice0.findViewById(R.id.sno);
                                        sn0.setText("Uploaded Invoices");
                                        sn0.setTypeface(Typeface.DEFAULT_BOLD);
                                        sn0.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                        TextView invoiceNo0 = (TextView) confirmedInvoice0.findViewById(R.id.invoiceNo);
                                        invoiceNo0.setVisibility(View.GONE);
                                        TextView invoiceDate0 = (TextView) confirmedInvoice0.findViewById(R.id.invoiceDate);
                                        invoiceDate0.setVisibility(View.GONE);
                                        TextView amount0 = (TextView) confirmedInvoice0.findViewById(R.id.amount);
                                        amount0.setVisibility(View.GONE);
                                        confirmInvoice.addView(confirmedInvoice0);


                                        View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                        TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                        sn1.setText("SNo.");
                                        sn1.setTypeface(Typeface.DEFAULT_BOLD);
                                        TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                        invoiceNo1.setText("Invoice No.");
                                        invoiceNo1.setTypeface(Typeface.DEFAULT_BOLD);
                                        TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                        invoiceDate1.setText("Invoice Date");
                                        invoiceDate1.setTypeface(Typeface.DEFAULT_BOLD);
                                        TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                        amount1.setText("Amount");
                                        amount1.setTypeface(Typeface.DEFAULT_BOLD);
                                        confirmInvoice.addView(confirmedInvoice1);
                                    }

                                    View confirmedInvoice1 = innerInflater.inflate(R.layout.confirmed_invoice_item, null, false);
                                    TextView sn1 = (TextView) confirmedInvoice1.findViewById(R.id.sno);
                                    sn1.setText(String.valueOf(invoiceArray.size() + 1));
                                    TextView invoiceNo1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceNo);
                                    invoiceNo1.setText(invoiceData.getInvoiceNumber());
                                    TextView invoiceDate1 = (TextView) confirmedInvoice1.findViewById(R.id.invoiceDate);
                                    invoiceDate1.setText(invoiceData.getDate());
                                    TextView amount1 = (TextView) confirmedInvoice1.findViewById(R.id.amount);
                                    amount1.setText(invoiceData.getInvoiceAmount());
                                    confirmInvoice.addView(confirmedInvoice1);

                                    invoiceArray.add(invoiceData);
                                    invoiceData = new InvoiceData();
                                    invoiceAmount.setText("");
                                    invoiceNumber.setText("");
                                    invoiceDate.setText("");
                                    remark.setText("");
                                    invoiceView.setImageDrawable(null);
                                    InvoiceSpinnerAdapter invoiceSpinnerAdapter = new InvoiceSpinnerAdapter(invoiceArray);

                                    invoiceId.setAdapter(invoiceSpinnerAdapter);
                                    // onClickListener:
                                    invoiceId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        public void onItemSelected(AdapterView<?> parent,
                                                                   View view, int pos, long id) {
                                            InvoiceData invoiceInnerdata = (InvoiceData) parent.getItemAtPosition(pos);
                                            materialTypeData.setInvoiceId(invoiceInnerdata.getInvoiceNumber());
                                            Toast.makeText(
                                                    CustomerOrderActivity.this,
                                                    invoiceInnerdata.getInvoiceNumber() + " is of " + invoiceInnerdata.getInvoiceAmount() + " amount.",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }

                                        public void onNothingSelected(AdapterView parent) {
                                            // Do nothing.
                                        }
                                    });


                                    addInvoice.setVisibility(View.VISIBLE);
                                    addInvoiceLayout.setVisibility(View.GONE);
                                    int restInvoice = 5 - invoiceArray.size();
                                    invoiceInstruction.setText("You can upload " + restInvoice + " more invoices");


                                } else {
                                    Toast.makeText(CustomerOrderActivity.this, "Invalid Account Detail", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(CustomerOrderActivity.this, "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(CustomerOrderActivity.this, "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                volleyError.printStackTrace();
                Toast.makeText(CustomerOrderActivity.this, "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if (prefs == null) {
                    prefs = CustomerOrderActivity.this.getSharedPreferences(AppController.ACCOUNT_PREFRENCE, CustomerOrderActivity.this.MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                map.put("api_key", apiKey);
                map.put("account_id", accountId);

                map.put("invoice_amount", invoiceData.getInvoiceAmount());
                map.put("invoice_number", invoiceData.getInvoiceNumber());
                map.put("invoice_date", invoiceData.getDate());
                map.put("invoice_image_base64", invoiceData.getInvoicePath());
                map.put("remark", invoiceData.getInvoiceRemark());

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };

        stringVarietyRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        progressDialog = ProgressDialog.show(CustomerOrderActivity.this, "",
                "Fetching Track Data.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = CustomerOrderActivity.this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String imageString = Base64.encodeBytes(ba);
            if (requestCode == BROWSE_INVOICE) {
                if (invoiceView != null) {
                    invoiceView.setImageBitmap(bitmap);
                }
                invoiceData.setInvoicePath(imageString);
            } else {
                if (roadPermitView != null) {
                    roadPermitView.setImageBitmap(bitmap);
                }
                roadPermitData.setRoadPermitImageString(imageString);
            }


        }
    }

    boolean isValidMaterial() {
        boolean isValid = true;

        if (!(materialTypeData.getMaterialTypeId() != null && materialTypeData.getMaterialTypeId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select material type", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(materialTypeData.getInvoiceId() != null && materialTypeData.getInvoiceId().trim().length() > 0)) {
            Toast.makeText(CustomerOrderActivity.this, "Please select invoice for material", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (materialAmount.getText() != null && materialAmount.getText().toString().trim().length() > 0) {
            double amount = 0.0;
            try {
                amount = Double.parseDouble(materialAmount.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(CustomerOrderActivity.this, "Please enter ", Toast.LENGTH_SHORT).show();
            return false;
        }
        return isValid;
    }


    /**
     * This is your own Adapter implementation which displays
     * the ArrayList of "Guy"-Objects.
     */
    private class InvoiceSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        /**
         * The internal data (the ArrayList with the Objects).
         */
        private final List<InvoiceData> data;

        public InvoiceSpinnerAdapter(List<InvoiceData> data) {
            this.data = data;
        }

        /**
         * Returns the Size of the ArrayList
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * Returns one Element of the ArrayList
         * at the specified position.
         */
        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Returns the View that is shown when a element was
         * selected.
         */
        @Override
        public View getView(int position, View recycle, ViewGroup parent) {
            TextView text;
            if (recycle != null) {
                // Re-use the recycled view here!
                text = (TextView) recycle;
            } else {
                // No recycled view, inflate the "original" from the platform:
//                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                text = (TextView) CustomerOrderActivity.this.getLayoutInflater().inflate(
                        android.R.layout.simple_spinner_dropdown_item, parent, false
                );
            }
            text.setTextColor(Color.BLACK);
            text.setText(data.get(position).getInvoiceNumber());
            return text;
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CustomerOrderActivity.this, TransporterMainActivity.class);
        intent.putParcelableArrayListExtra(TRANSPORTER_LIST, transporterDatas);
        startActivity(intent);
    }
}
