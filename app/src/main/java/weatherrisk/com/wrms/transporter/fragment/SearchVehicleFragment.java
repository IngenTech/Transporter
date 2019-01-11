package weatherrisk.com.wrms.transporter.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.utils.MyUtility;
import weatherrisk.com.wrms.transporter.dataobject.SearchTransporterData;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchVehicleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchVehicleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchVehicleFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String FRAGMENT_TAG = "Search Vehicle";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SearchVehicleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchVehicleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchVehicleFragment newInstance(String param1, String param2) {
        SearchVehicleFragment fragment = new SearchVehicleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void onResume() {
        super.onResume();
        if(getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity())
                    .setActionBarTitle(getResources().getString(R.string.customer_search_vehicle));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static String DATE_FORMAT_STRING = "yyyy-MM-dd";
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);

    Spinner fromStateSpinner;
    Spinner fromCitySpinner;
    Spinner toStateSpinner;
    Spinner toCitySpinner;
    EditText edtTxtDate;
    Spinner selectCategory;
    EditText edtTxtItem;
    EditText edtTxtCapacity;
    CheckBox checkBoxDoorClose;
    CheckBox checkBoxRefrigerated;
    EditText edtTxtNoOfVehicles;
    Button btnSearchTransporter;

    SearchTransporterData data;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ArrayList<TransporterData> transporterList = new ArrayList<>();

    DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_vehicle, container, false);

        fromStateSpinner = (Spinner) view.findViewById(R.id.fromStateSpinner);
        fromCitySpinner = (Spinner) view.findViewById(R.id.fromCitySpinner);
        toStateSpinner = (Spinner) view.findViewById(R.id.toStateSpinner);
        toCitySpinner = (Spinner) view.findViewById(R.id.toCitySpinner);
        edtTxtDate = (EditText) view.findViewById(R.id.edtTxtDate);
        selectCategory = (Spinner) view.findViewById(R.id.selectCategory);
        edtTxtItem = (EditText) view.findViewById(R.id.edtTxtItem);
        edtTxtCapacity = (EditText) view.findViewById(R.id.edtTxtCapacity);
        checkBoxDoorClose = (CheckBox) view.findViewById(R.id.checkBoxDoorClose);
        checkBoxRefrigerated = (CheckBox) view.findViewById(R.id.checkBoxRefrigerated);
        edtTxtNoOfVehicles = (EditText) view.findViewById(R.id.edtTxtNoOfVehicles);
        btnSearchTransporter = (Button) view.findViewById(R.id.btnSearchTransporter);

        if(data==null){
            data = new SearchTransporterData();
        }

        db = new DBAdapter(getActivity());
        db.open();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final Cursor stateListCursor = db.stateList();

        final ArrayList<String> stateNameArray = new ArrayList<>();
        if (stateListCursor.moveToFirst()) {
            do {
                String stateString = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                stateNameArray.add(stateString);
            } while (stateListCursor.moveToNext());
        }
        ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stateNameArray);

        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromStateSpinner.setAdapter(state_adapter);
        fromStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                stateListCursor.moveToPosition(pos);
                String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                data.setFromStateId(stateId);

                final Cursor cityListCursor = db.cityListByStateId(stateId);

                final ArrayList<String> cityNameArray = new ArrayList<>();
                if (cityListCursor.moveToFirst()) {
                    do {
                        String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        cityNameArray.add(cityString);
                    } while (cityListCursor.moveToNext());
                }

                ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, cityNameArray);
                city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                fromCitySpinner.setAdapter(city_adapter);
                fromCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int pos, long id) {
                        cityListCursor.moveToPosition(pos);
                        String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                        String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        data.setFromCityId(cityId);
                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        toStateSpinner.setAdapter(state_adapter);
        toStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                stateListCursor.moveToPosition(pos);
                String stateId = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_ID));
                String stateName = stateListCursor.getString(stateListCursor.getColumnIndex(DBAdapter.STATE_NAME));
                data.setToStateId(stateId);

                final Cursor cityListCursor = db.cityListByStateId(stateId);

                final ArrayList<String> cityNameArray = new ArrayList<>();
                if (cityListCursor.moveToFirst()) {
                    do {
                        String cityString = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        cityNameArray.add(cityString);
                    } while (cityListCursor.moveToNext());
                }

                ArrayAdapter<String> city_adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, cityNameArray);

                city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                toCitySpinner.setAdapter(city_adapter);
                toCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    /**
                     * Called when a new item was selected (in the Spinner)
                     */
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        cityListCursor.moveToPosition(pos);
                        String cityId = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_ID));
                        String cityName = cityListCursor.getString(cityListCursor.getColumnIndex(DBAdapter.CITY_NAME));
                        data.setToCityId(cityId);
                    }

                    public void onNothingSelected(AdapterView parent) {
                        // Do nothing.
                    }
                });

            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        edtTxtDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                final View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);
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
                        edtTxtDate.setText(selectedDate);
                    }
                });
                dialog = adb.create();
                dialog.show();
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
        ArrayAdapter<String> material_adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, materialNameArray);

        material_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectCategory.setAdapter(material_adapter);
        selectCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * Called when a new item was selected (in the Spinner)
             */
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                materialList.moveToPosition(pos);
                String materialId = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_ID));
                String materialName = materialList.getString(materialList.getColumnIndex(DBAdapter.MATERIAL_NAME));
                data.setMaterialId(materialId);
            }

            public void onNothingSelected(AdapterView parent) {
                // Do nothing.
            }
        });

        btnSearchTransporter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValid()){
                    searchTransporterRequest();
                }
            }
        });

    }

    private void searchTransporterRequest() {

        StringRequest stringLoginRequest = new StringRequest(Request.Method.POST, MyUtility.URL.SEARCH_TRANSPORTER_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String tripResponse) {
                        dialog.dismiss();
                        try {

                            System.out.println("Trip Response : " + tripResponse);
                            JSONObject jsonObject = new JSONObject(tripResponse);

                            if (jsonObject.has("result")) {
                                if (jsonObject.getString("result").equals("success")) {

//                                    String accountId = jsonObject.getString("account_id");
                                    JSONArray casesArray = jsonObject.getJSONArray("transportersList");
                                    if (casesArray.length() > 0) {

                                        for (int i = 0; i < casesArray.length(); i++) {
                                            JSONObject jObject = casesArray.getJSONObject(i);
                                            TransporterData transporterData = new TransporterData();
                                            transporterData.setFirmId(jObject.getString("TransporterId"));
                                            transporterData.setFirmName(jObject.getString("TransporterName"));
                                            transporterData.setContactName(jObject.getString("ContactName"));
                                            transporterData.setPersonContactNo(jObject.getString("MobileNo"));
                                            transporterData.setOfficeContactNo(jObject.getString("OfficeContactNo"));
                                            transporterData.setAddress(jObject.getString("Address"));
                                            transporterData.setFromCityId(jObject.getString("CityID"),db);
//                                            transporterData.setFromCityId(data.getFromCityId(),db);
                                            String doorClosed = jObject.getString("DoorClose");
                                            transporterData.setDoorClosed(doorClosed);
                                            transporterData.setRefrigerated(jObject.getString("Refrigerated"));
                                            transporterData.setMaterialId(jObject.getString("Category"));
                                            transporterData.setCapacity(jObject.getString("Capacity"));
                                            transporterData.setRate(jObject.getString("RatePerKm"));
                                            transporterData.setAvailableVehicle(jObject.getString("AvailableVehicles"));
                                            transporterData.setFromStateId(data.getFromStateId(),db);
                                            transporterData.setFromCityId(data.getFromCityId(),db);
                                            transporterData.setToStateId(data.getToStateId(),db);
                                            transporterData.setToCityId(data.getToCityId(),db);
                                            transporterList.add(transporterData);
                                        }

                                        System.out.println("transporter list size : "+transporterList.size());

                                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                        ft.replace(R.id.frame_container, TransporterListFragment.newInstance(transporterList), FRAGMENT_TAG);
                                        ft.commit();

                                    } else {
                                        Toast.makeText(getActivity(), "No Transporter Found in this Account", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    String message = "No transporter found.";
                                    if (jsonObject.has("message")) {
                                        message = jsonObject.getString("message");
                                    }
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                        dialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.cancel();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                String apiKey = getResources().getString(R.string.server_api_key);
                Map<String, String> map = new HashMap<>();
                map.put("api_key", apiKey);
                map.put("account_id", accountId);
                map.put("capacity",data.getCapacity());
//                map.put("shipment_date",data.getDate());
                map.put("from_city_id",data.getFromCityId());
                map.put("to_city_id",data.getToCityId());
                map.put("category_id",data.getMaterialId());
//                map.put("item",data.getItem());
                String doorClosed = "";
                if(data.isDoorClosed()){
                    doorClosed = "1";
                }else{
                    doorClosed = "0";
                }
                map.put("door_close",doorClosed);
                String refrigerated = "";
                if(data.isRefrigerated()){
                    refrigerated = "1";
                }else{
                    refrigerated = "0";
                }
                map.put("referigerated",refrigerated);
                map.put("no_of_vehicles",data.getNoOfVehicles());


                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }


                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Available Transporters",
                "Fetching data...", true);
        AppController.getInstance().addToRequestQueue(stringLoginRequest);
    }


    private boolean isValid(){
        boolean isValid = true;

        if(edtTxtDate.getText()!=null && edtTxtDate.getText().toString().trim().length()>0){
            data.setDate(edtTxtDate.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Please Select Date",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtTxtItem.getText()!=null && edtTxtItem.getText().toString().trim().length()>0){
            data.setItem(edtTxtItem.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Please Enter Item",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtTxtCapacity.getText()!=null && edtTxtCapacity.getText().toString().trim().length()>0){
            data.setCapacity(edtTxtCapacity.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Please Specify Capacity",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(edtTxtNoOfVehicles.getText()!=null && edtTxtNoOfVehicles.getText().toString().trim().length()>0){
            data.setNoOfVehicles(edtTxtNoOfVehicles.getText().toString());
        }else{
            Toast.makeText(getActivity(),"Please Enter no. of Vehicles",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(checkBoxDoorClose.isChecked()){
            data.setDoorClosed(true);
        }else{
            data.setDoorClosed(false);
        }

        if(checkBoxRefrigerated.isChecked()){
            data.setRefrigerated(true);
        }else{
            data.setRefrigerated(false);
        }

        if(!(data.getFromStateId()!=null && data.getFromStateId().trim().length()>0)){
            Toast.makeText(getActivity(),"Please Select Source State",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(data.getFromCityId()!=null && data.getFromCityId().trim().length()>0)){
            Toast.makeText(getActivity(),"Please Select Source City",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(data.getToStateId()!=null && data.getToStateId().trim().length()>0)){
            Toast.makeText(getActivity(),"Please Select Destination State",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(data.getToCityId()!=null && data.getToCityId().trim().length()>0)){
            Toast.makeText(getActivity(),"Please Select Destination City",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(data.getMaterialId()!=null && data.getMaterialId().trim().length()>0)){
            Toast.makeText(getActivity(),"Please Select Category ",Toast.LENGTH_SHORT).show();
            return false;
        }


        return isValid;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

   /* class SearchTransporterData {
        String fromStateId;
        String fromCityId;
        String toStateId;
        String toCityId;
        String date;
        String materialId;
        boolean doorClosed;
        boolean refrigerated;
        String item;
        String noOfVehicles;
        String capacity;

        public boolean isDoorClosed() {
            return doorClosed;
        }

        public void setDoorClosed(boolean doorClosed) {
            this.doorClosed = doorClosed;
        }

        public boolean isRefrigerated() {
            return refrigerated;
        }

        public void setRefrigerated(boolean refrigerated) {
            this.refrigerated = refrigerated;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public String getNoOfVehicles() {
            return noOfVehicles;
        }

        public void setNoOfVehicles(String noOfVehicles) {
            this.noOfVehicles = noOfVehicles;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getFromStateId() {
            return fromStateId;
        }

        public void setFromStateId(String fromStateId) {
            this.fromStateId = fromStateId;
        }

        public String getFromCityId() {
            return fromCityId;
        }

        public void setFromCityId(String fromCityId) {
            this.fromCityId = fromCityId;
        }

        public String getToStateId() {
            return toStateId;
        }

        public void setToStateId(String toStateId) {
            this.toStateId = toStateId;
        }

        public String getToCityId() {
            return toCityId;
        }

        public void setToCityId(String toCityId) {
            this.toCityId = toCityId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMaterialId() {
            return materialId;
        }

        public void setMaterialId(String materialId) {
            this.materialId = materialId;
        }
    }*/
}
