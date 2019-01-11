package weatherrisk.com.wrms.transporter.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import weatherrisk.com.wrms.transporter.AddTripActivity;
import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DateTimePickerDialog;
import weatherrisk.com.wrms.transporter.adapter.SingleSelectionVehicleAdapter;
import weatherrisk.com.wrms.transporter.dataobject.InvoiceData;
import weatherrisk.com.wrms.transporter.dataobject.MaterialTypeData;
import weatherrisk.com.wrms.transporter.dataobject.OrderData;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;
import weatherrisk.com.wrms.transporter.orders_action_activity.AddOnRoadAssistanceActivity;

/**
 * dynamicDataLayout simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleChoiceVehicleListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleChoiceVehicleListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleChoiceVehicleListFragment extends Fragment implements
        SearchView.OnQueryTextListener, DateTimePickerDialog.DateTimeListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REDIRECT_FRAGMENT = "redirectFragment";
    private static final String VEHICLE_LIST = "VehicleList";
    public static final String FRAGMENT_TAG = "";


    private String redirectFragment;
    private ArrayList<VehicleData> vehicleList;
    SingleSelectionVehicleAdapter adapter;

    private OnFragmentInteractionListener mListener;
    private EditText dateTimeView;

    private OrderData pendingOrderData;
    private ArrayList<InvoiceData> invoiceArray;
    private ArrayList<MaterialTypeData> materialArray;

    public void setPendingOrderData(OrderData pendingOrderData) {
        this.pendingOrderData = pendingOrderData;
    }
    public void setInvoiceArray(ArrayList<InvoiceData> invoiceArray) {
        this.invoiceArray = invoiceArray;
    }
    public void setMaterialArray(ArrayList<MaterialTypeData> materialArray) {
        this.materialArray = materialArray;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param redirectFragment Parameter 1.
     * @param vehicleList      Parameter 2.
     * @return dynamicDataLayout new instance of fragment SingleChoiceVehicleListFragment.
     */

    public static SingleChoiceVehicleListFragment newInstance(String redirectFragment, ArrayList<VehicleData> vehicleList) {
        SingleChoiceVehicleListFragment fragment = new SingleChoiceVehicleListFragment();
        Bundle args = new Bundle();
        args.putString(REDIRECT_FRAGMENT, redirectFragment);
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    public SingleChoiceVehicleListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            redirectFragment = getArguments().getString(REDIRECT_FRAGMENT);
            vehicleList = getArguments().getParcelableArrayList(VEHICLE_LIST);
            onSelectionOfTheFragment(FRAGMENT_TAG);
        }
    }

    ListView mListView;
    EditText endTime;
    EditText startTime;
    Button track;
    Button interval;

    String trackInterval = "1";
    ArrayList<VehicleData> searchArrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_choice_vehicle_list, container, false);
        mListView = (ListView) view.findViewById(R.id.singleChoiceList);

        startTime = (EditText) view.findViewById(R.id.startTime);
        endTime = (EditText) view.findViewById(R.id.endTime);
        track = (Button) view.findViewById(R.id.track);
        interval = (Button) view.findViewById(R.id.interval);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Date current = Calendar.getInstance().getTime();
        startTime.setText(AppController.S_DATE_TIME_FORMAT.format(current) + " 00:00:00");
        endTime.setText(AppController.DATE_TIME_FORMAT.format(current));

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Inside proceedButton onclick");
                showDateTimeDialog();
                dateTimeView = startTime;
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimeDialog();
                dateTimeView = endTime;
            }
        });

        interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] items = getActivity().getResources().getStringArray(R.array.track_report_interval);
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(items, 0, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                trackInterval = items[selectedPosition].replaceAll("\\D+", "");
//                                interval = Integer.valueOf(selectedInterval);

                                // Do something useful with the position of the selected radio button
                            }
                        })
                        .show();
            }
        });

        if (redirectFragment.equals(TrackMapFragment.FRAGMENT_TAG)) {
            interval.setVisibility(View.VISIBLE);
            startTime.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
        } else {
            interval.setVisibility(View.GONE);
            startTime.setVisibility(View.GONE);
            endTime.setVisibility(View.GONE);
        }

        track.setText(redirectFragment);

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelectedVehicle() != null) {
                    if (redirectFragment.equals(TrackMapFragment.FRAGMENT_TAG)) {
                        long duration = getDuration(startTime.getText().toString(), endTime.getText().toString());
                        int interval = Integer.parseInt(trackInterval);
                        System.out.println("Duration : " + duration);
                        if (duration > 10 && duration < 24) {
                            if (interval < 2) {
                                trackInterval = "2";
                            }
                        }
                        if (duration > 24 && duration < 48) {
                            if (interval < 5) {
                                Toast.makeText(getActivity(), "Interval can not be less then 5 min as Time Duration is more then 1 day", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (duration > 48 && duration < 92) {
                            if (interval < 10) {
                                Toast.makeText(getActivity(), "Interval can not be less then 10 min as Time Duration is more then 2 day", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        if (duration > 92) {
                            Toast.makeText(getActivity(), "Time Duration can not be more then 3 days ", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                    System.out.println("Sending intervall : "+trackInterval);
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, TrackMapFragment.newInstance(
                                startTime.getText().toString(),
                                endTime.getText().toString(),
                                trackInterval,
                                adapter.getSelectedVehicle()), FRAGMENT_TAG);
//                    ft.addToBackStack(FRAGMENT_TAG);
                        ft.commit();
                    }

                    if(redirectFragment.equals(AddOnRoadAssistanceFragment.FRAGMENT_TAG)){

                        ArrayList<VehicleData> data = new ArrayList<VehicleData>();
                        data.add(adapter.getSelectedVehicle());
                       /* final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, AddOnRoadAssistanceFragment.newInstance("params1", data), FRAGMENT_TAG);
//                        ft.addToBackStack(FRAGMENT_TAG);
                        ft.commit();*/

                        Intent in  = new Intent(getActivity(), AddOnRoadAssistanceActivity.class);
                        in.putParcelableArrayListExtra(VEHICLE_LIST,data);
                        startActivity(in);
                    }

                    if(redirectFragment.equals(AddTripFragment.FRAGMENT_TAG)){
                        VehicleData data = adapter.getSelectedVehicle();
                        Intent intent = new Intent(getActivity(), AddTripActivity.class);
                        intent.putExtra(AddTripActivity.VEHICLE_DATA,data);
                        getActivity().startActivity(intent);
                        /*final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, AddTripFragment.newInstance(null, data), FRAGMENT_TAG);
                        ft.commit();*/
                    }

                    if(redirectFragment.equals(StartTripFragment.FRAGMENT_TAG)){
                        VehicleData data = adapter.getSelectedVehicle();
                        final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, StartOrderTripFragment.newInstance(pendingOrderData,invoiceArray,materialArray, data), FRAGMENT_TAG);
                        ft.commit();
                    }

                }else {
                    Toast.makeText(getActivity(), "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                }

            }
        });

        setHasOptionsMenu(true);

    }

    private long getDuration(String endDate, String startDate) {
        try {
            Date date1 = AppController.DATE_TIME_FORMAT.parse(startDate);
            Date date2 = AppController.DATE_TIME_FORMAT.parse(endDate);

            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            return hours;
        } catch (Exception e) {

            return 0;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    System.out.println("SingleChoiceListFragment vehicle List : "+vehicleList.size());

        if (vehicleList != null && vehicleList.size() > 0) {
            if(searchArrayList.size()==0) {
                for (VehicleData vehicleData : vehicleList) {
                    searchArrayList.add(vehicleData);
                }
            }
        }

        System.out.println("SingleCoiceListFragment searchArrayList : "+vehicleList.size());

        adapter = new SingleSelectionVehicleAdapter(getActivity(), searchArrayList);
        mListView.setAdapter(adapter);

    }

    private void showDateTimeDialog() {
        DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), true, this);
        pickerDialog.show();
    }

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int min, int sec, int am_pm) {

        String text = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        /*if (am_pm != -1)
            text = text + (am_pm == Calendar.AM ? "AM" : "PM");*/
        dateTimeView.setText(text);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Vehicle No.");
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    public void onSelectionOfTheFragment(String title) {
        if (mListener != null) {
            mListener.onFragmentInteraction(title);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
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
        public void onFragmentInteraction(String title);
    }


}
