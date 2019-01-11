package weatherrisk.com.wrms.transporter.fragment;

import android.app.Activity;
import android.content.DialogInterface;
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

import weatherrisk.com.wrms.transporter.AppController;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DateTimePickerDialog;
import weatherrisk.com.wrms.transporter.adapter.MultipleSelectionVehicleAdapter;
import weatherrisk.com.wrms.transporter.dataobject.VehicleData;

/**
 * dynamicDataLayout simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultipleChoiceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultipleChoiceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleChoiceListFragment extends Fragment  implements
        SearchView.OnQueryTextListener, DateTimePickerDialog.DateTimeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String REDIRECT_FRAGMENT = "redirectFragment";
    private static final String VEHICLE_LIST = "vehicleList";
    public static final String FRAGMENT_TAG = "";

    // TODO: Rename and change types of parameters
    private String redirectFragment;
    private ArrayList<VehicleData> vehicleList;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param redirectFragment Parameter 1.
     * @param vehicleList Parameter 2.
     * @return dynamicDataLayout new instance of fragment MultipleChoiceListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MultipleChoiceListFragment newInstance(String redirectFragment,  ArrayList<VehicleData> vehicleList) {
        MultipleChoiceListFragment fragment = new MultipleChoiceListFragment();
        Bundle args = new Bundle();
        args.putString(REDIRECT_FRAGMENT, redirectFragment);
        args.putParcelableArrayList(VEHICLE_LIST, vehicleList);
        fragment.setArguments(args);
        return fragment;
    }

    public MultipleChoiceListFragment() {
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
    Button proceedButton;
    Button intervalButton;

    String interval ="10";
    MultipleSelectionVehicleAdapter adapter;
    private EditText dateTimeView;
    ArrayList<VehicleData> searchArrayList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiple_choice_list, container, false);
        mListView = (ListView)view.findViewById(R.id.multipleChoiceList);

        startTime =(EditText)view.findViewById(R.id.startTime);
        endTime = (EditText)view.findViewById(R.id.endTime);
        proceedButton = (Button)view.findViewById(R.id.track);
        intervalButton = (Button)view.findViewById(R.id.interval);

        if(redirectFragment.equals(LiveMapFragment.FRAGMENT_TAG)){
            startTime.setVisibility(View.GONE);
            endTime.setVisibility(View.GONE);
            intervalButton.setVisibility(View.GONE);
        }else{
            startTime.setVisibility(View.VISIBLE);
            endTime.setVisibility(View.VISIBLE);
            intervalButton.setVisibility(View.VISIBLE);
        }

        Date current = Calendar.getInstance().getTime();
        startTime.setText(AppController.S_DATE_TIME_FORMAT.format(current)+" 00:00:00");
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

        //Initial Interval
        if(redirectFragment.equals(TrackMapFragment.FRAGMENT_TAG)){
            interval = "300";
        }
        if(redirectFragment.equals(TravelReportFragment.FRAGMENT_TAG)) {
            interval = "1800";
        }
        /*if(redirectFragment.equals(DistanceReportFragment.FRAGMENT_TAG)) {
            interval = "3600";
        }*/

        intervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String[] items = getActivity().getResources().getStringArray(R.array.track_report_interval);
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(items, 3, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                interval = items[selectedPosition].replaceAll("\\D+", "");
//                                interval = Integer.valueOf(selectedInterval);

                                // Do something useful withe the position of the selected radio button
                            }
                        })
                        .show();
            }
        });

        proceedButton.setText(redirectFragment);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(adapter.getSelectedVehicles().size()>0) {
                    if (redirectFragment.equals(LiveMapFragment.FRAGMENT_TAG)) {
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, LiveMapFragment.newInstance(
                                startTime.getText().toString(),
                                adapter.getSelectedVehicles()), FRAGMENT_TAG);
//                    ft.addToBackStack(LiveMapFragment.FRAGMENT_TAG);
                        ft.commit();

                    }

                    /*if (redirectFragment.equals(DistanceReportFragment.FRAGMENT_TAG)) {
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, DistanceReportFragment.newInstance(
                                startTime.getText().toString(),
                                endTime.getText().toString(),
                                interval,
                                adapter.getSelectedVehicles()), FRAGMENT_TAG);
//                    ft.addToBackStack(DistanceReportFragment.FRAGMENT_TAG);
                        ft.commit();
                    }*/

                    if (redirectFragment.equals(TravelReportFragment.FRAGMENT_TAG)) {
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, TravelReportFragment.newInstance(
                                startTime.getText().toString(),
                                endTime.getText().toString(),
                                interval,
                                adapter.getSelectedVehicles()), FRAGMENT_TAG);
//                    ft.addToBackStack(TravelReportFragment.FRAGMENT_TAG);
                        ft.commit();
                    }

                    /*if (redirectFragment.equals(HaltReportFragment.FRAGMENT_TAG)) {
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.frame_container, HaltReportFragment.newInstance(
                                startTime.getText().toString(),
                                endTime.getText().toString(),
                                interval,
                                adapter.getSelectedVehicles()), FRAGMENT_TAG);
//                    ft.addToBackStack(HaltReportFragment.FRAGMENT_TAG);
                        ft.commit();
                    }*/
                }else{
                    Toast.makeText(getActivity(), "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(vehicleList!=null && vehicleList.size()>0){
            if(searchArrayList.size()==0) {
                for (VehicleData vehicleData : vehicleList) {
                    searchArrayList.add(vehicleData);
                }
            }
        }

        adapter = new MultipleSelectionVehicleAdapter(getActivity(), searchArrayList);
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private View.OnClickListener intervalOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            /*if(redirectFragment.equals(TrackMapFragment.FRAGMENT_TAG)) {
                final String[] items = getActivity().getResources().getStringArray(R.array.track_report_interval);
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(items, 3, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                interval = items[selectedPosition].replaceAll("\\D+", "");
//                                interval = Integer.valueOf(selectedInterval);

                                // Do something useful withe the position of the selected radio button
                            }
                        })
                        .show();
            }

            if(redirectFragment.equals(DistanceReportFragment.FRAGMENT_TAG)) {
                final String[] items = getActivity().getResources().getStringArray(R.array.distance_report_interval);
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(items, 3, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                interval = items[selectedPosition].replaceAll("\\D+", "");
                                int value =  Integer.valueOf(interval);
                                value = value*3600;
                                interval = String.valueOf(value);

                                // Do something useful withe the position of the selected radio button
                            }
                        })
                        .show();
            }*/
            if(redirectFragment.equals(TravelReportFragment.FRAGMENT_TAG)) {
                final String[] items = getActivity().getResources().getStringArray(R.array.travel_halt_interval);
                new AlertDialog.Builder(getActivity())
                        .setSingleChoiceItems(items, 3, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                interval = items[selectedPosition].replaceAll("\\D+", "");
                                int value =  Integer.valueOf(interval);
                                if(value>=15){
                                    value = value * 3600;
                                }else {
                                    value = value * 60;
                                }
                                interval = String.valueOf(value);

                                // Do something useful withe the position of the selected radio button
                            }
                        })
                        .show();
            }

        }
    };

    private void showDateTimeDialog() {
        DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getActivity(), true, this);
        pickerDialog.show();
    }

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int min, int sec, int am_pm) {

        String text = year + "-" + String.format("%02d", month)+ "-" + String.format("%02d", day) + " " + String.format("%02d", hour) + ":" + String.format("%02d", min)+":"+String.format("%02d", sec);
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
