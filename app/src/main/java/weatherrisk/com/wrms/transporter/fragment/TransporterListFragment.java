package weatherrisk.com.wrms.transporter.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.adapter.TransporterListAdapter;
import weatherrisk.com.wrms.transporter.dataobject.TransporterData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransporterListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransporterListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransporterListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TRANSPORTER_LIST = "transporter_list";

    public static final String FRAGMENT_TAG = "Transporter List";

    private ArrayList<TransporterData> transporterDatas;

    private OnFragmentInteractionListener mListener;

    public TransporterListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param transporterDatas Parameter 1.
     * @return A new instance of fragment TransporterListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransporterListFragment newInstance(ArrayList<TransporterData> transporterDatas) {
        TransporterListFragment fragment = new TransporterListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(TRANSPORTER_LIST, transporterDatas);
        fragment.setArguments(args);
        return fragment;
    }

    ListView listView;
    TransporterListAdapter adapter;
    DBAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transporterDatas = getArguments().getParcelableArrayList(TRANSPORTER_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_transporter_list, container, false);

        listView = (ListView)view.findViewById(R.id.transporterListView);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        adapter = new TransporterListAdapter((AppCompatActivity)getActivity(),transporterDatas);
        listView.setAdapter(adapter);

        /*listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TransporterData transporterData = transporterDatas.get(position);
                if(transporterData!=null) {
                    SharedPreferences prefs= getActivity().getSharedPreferences(AppController.ACCOUNT_PREFRENCE, getActivity().MODE_PRIVATE);
                    String accountId = (prefs.getString(AppController.PREFERENCE_ACCOUNT_ID, "0"));

                    String tripId = transporterData.getFirmId();
                    String vehicleId = transporterData.getContactName();
                    final FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//                    childFragment = AddOrderFragment.newInstance(transporterData, "");
                    ft.replace(R.id.frame_container, AddOrderFragment.newInstance(transporterData, ""), "Add Order By Customer");
                    ft.commit();
                }else{
                    System.out.println("transporter data is null ");
                }
            }
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        adapter.getChildFragment().getParentFragment().onActivityResult(requestCode, resultCode, data);
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
}
