package weatherrisk.com.wrms.transporter.transporter;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.LoginActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.TransporterMainActivity;
import weatherrisk.com.wrms.transporter.adapter.CustomerTripHistoryAdapter;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.dataobject.CustomerTripHistoryData;
import weatherrisk.com.wrms.transporter.utils.MyUtility;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CustomerTripHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CustomerTripHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerTripHistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CustomerTripHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomerTripHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomerTripHistoryFragment newInstance(String param1, String param2) {
        CustomerTripHistoryFragment fragment = new CustomerTripHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = new DBAdapter(getActivity());
        db.open();


    }

    public void onResume() {
        super.onResume();
        if (getActivity() instanceof TransporterMainActivity) {
            ((TransporterMainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.history));
        }
    }

    DBAdapter db;
    ArrayList<CustomerTripHistoryData> customerRunningTripDatas = new ArrayList<>();
    CustomerTripHistoryAdapter adapter;
    ProgressDialog dialog;
    SharedPreferences prefs;
    ListView listview;
    TextView date1, date2;
    Button searchHistory;
    RelativeLayout toDate, fromDate;
    private int mYear, mMonth, mDay;
    private int mYear1, mMonth1, mDay1;
    TextView noHistory;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//         inflater.inflate(R.layout.fragment_customer_trip_history, container, false);

        View view = inflater.inflate(R.layout.fragment_customer_trip_history, container, false);

        listview = (ListView) view.findViewById(R.id.listview);
        date1 = (TextView) view.findViewById(R.id.date1);
        date2 = (TextView) view.findViewById(R.id.date2);
        searchHistory = (Button) view.findViewById(R.id.search_history);
        toDate = (RelativeLayout) view.findViewById(R.id.toDate);
        fromDate = (RelativeLayout) view.findViewById(R.id.fromDate);
        noHistory = (TextView) view.findViewById(R.id.no_his_text);

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(year)) + "-" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "-" + mFormat.format(Double.valueOf(dayOfMonth));

                        date2.setText(Dates);


                    }
                }, mYear, mMonth, mDay);
                //    dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear1 = c.get(Calendar.YEAR);
                mMonth1 = c.get(Calendar.MONTH);
                mDay1 = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                        DecimalFormat mFormat = new DecimalFormat("00");
                        mFormat.format(Double.valueOf(year));
                        mFormat.setRoundingMode(RoundingMode.DOWN);
                        String Dates = mFormat.format(Double.valueOf(year)) + "-" + mFormat.format(Double.valueOf(monthOfYear + 1)) + "-" + mFormat.format(Double.valueOf(dayOfMonth));

                        date1.setText(Dates);


                    }
                }, mYear1, mMonth1, mDay1);
                //  dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                dpd.show();
            }

        });

        searchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String from_date = date1.getText().toString().trim();
                String to_date = date2.getText().toString().trim();

                if (from_date == null || from_date.length() < 7) {
                    Toast.makeText(getActivity(), "Please select from date", Toast.LENGTH_SHORT).show();
                } else if (to_date == null || to_date.length() < 7) {
                    Toast.makeText(getActivity(), "Please select to date", Toast.LENGTH_SHORT).show();
                } else {
                    historyTripList();
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        void onFragmentInteraction(Uri uri);
    }

    private void historyTripList() {

        StringRequest stringVarietyRequest = new StringRequest(Request.Method.POST, MyUtility.URL.HISTORY_TRIP_API,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String confirmOrder) {
                        dialog.dismiss();
                        try {
                            System.out.println("Confirm Order Response : " + confirmOrder);
                            JSONObject jsonObject = new JSONObject(confirmOrder);

                            if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("1"))) {
                                if (jsonObject.get("Result").equals("Success")) {

                                    JSONArray tripsArray = jsonObject.getJSONArray("Trips");
                                    if (tripsArray.length() > 0) {
                                        for (int i = 0; i < tripsArray.length(); i++) {
                                            JSONObject jObject = tripsArray.getJSONObject(i);
                                            CustomerTripHistoryData order = new CustomerTripHistoryData(jObject, db);
                                            customerRunningTripDatas.add(order);
                                        }
                                    }

                                } else {
                                    String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "No trip history";
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                }
                            } else if (jsonObject.has("Status") && (jsonObject.getString("Status").equalsIgnoreCase("2"))) {
                                String message = jsonObject.has("Message") ? jsonObject.getString("Message") : "No trip history";
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();


                                SharedPreferences preferences = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                              //  db.resetDatabase();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                getActivity().finish();


                            } else {
                                Toast.makeText(getActivity(), "Blank Response", Toast.LENGTH_LONG).show();
                            }

                            if (customerRunningTripDatas.size() > 0) {

                                listview.setVisibility(View.VISIBLE);
                                noHistory.setVisibility(View.GONE);

                                adapter = new CustomerTripHistoryAdapter(getActivity(), customerRunningTripDatas);
                                listview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {

                                listview.setVisibility(View.GONE);
                                noHistory.setVisibility(View.VISIBLE);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Not able parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                dialog.dismiss();
                Toast.makeText(getActivity(), "Not able to connect with server", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                if (prefs == null) {
                    prefs = getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                }
                String accountId = prefs.getString(AppController.PREFERENCE_USER_ID, "");
                String accessToken = prefs.getString(AppController.ACCESS_TOKEN, "");

                Map<String, String> map = new HashMap<>();
                map.put("UserId", accountId);
                map.put("AccessToken", accessToken);
                map.put("FromDate", date1.getText().toString().trim());
                map.put("ToDate", date2.getText().toString().trim());
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println(entry.getKey() + " : " + entry.getValue());
                }
                return map;
            }
        };
        dialog = ProgressDialog.show(getActivity(), "Trip History",
                "Please wait.....", true);
        AppController.getInstance().addToRequestQueue(stringVarietyRequest);

    }
}
