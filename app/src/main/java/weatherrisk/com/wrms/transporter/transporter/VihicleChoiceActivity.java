package weatherrisk.com.wrms.transporter.transporter;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import weatherrisk.com.wrms.transporter.fragment.AddOnRoadAssistanceFragment;
import weatherrisk.com.wrms.transporter.fragment.AddTripFragment;
import weatherrisk.com.wrms.transporter.fragment.StartOrderTripFragment;
import weatherrisk.com.wrms.transporter.fragment.StartTripFragment;
import weatherrisk.com.wrms.transporter.fragment.TrackMapFragment;
import weatherrisk.com.wrms.transporter.orders_action_activity.AddOnRoadAssistanceActivity;

/**
 * Created by Admin on 14-06-2017.
 */
public class VihicleChoiceActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String VEHICLE_LIST = "VehicleList";
    public static final String FRAGMENT_TAG = "";

    private ArrayList<VehicleData> vehicleList;
    SingleSelectionVehicleAdapter adapter;
    ListView mListView;
    Button addTrip;

    String trackInterval = "1";
    ArrayList<VehicleData> searchArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singel_vehicle_selection);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        mListView = (ListView) findViewById(R.id.singleChoiceList);

        addTrip = (Button) findViewById(R.id.addTrip);

        vehicleList = getIntent().getExtras().getParcelableArrayList(VEHICLE_LIST);


        addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getSelectedVehicle() != null) {
                    VehicleData data = adapter.getSelectedVehicle();
                    Intent intent = new Intent(getApplicationContext(), AddTripActivity.class);
                    intent.putExtra(AddTripActivity.VEHICLE_DATA, data);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please Select Vehicle", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getCurrentFocus()!=null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        System.out.println("SingleChoiceListFragment vehicle List : " + vehicleList.size());

        if (vehicleList != null && vehicleList.size() > 0) {
            if (searchArrayList.size() == 0) {
                for (VehicleData vehicleData : vehicleList) {
                    searchArrayList.add(vehicleData);
                }
            }
        }

        System.out.println("SingleCoiceListFragment searchArrayList : " + vehicleList.size());

        adapter = new SingleSelectionVehicleAdapter(VihicleChoiceActivity.this, searchArrayList);
        mListView.setAdapter(adapter);

    }


    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Vehicle No.");
        searchView.setOnQueryTextListener(this);
    }*/


    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


}


