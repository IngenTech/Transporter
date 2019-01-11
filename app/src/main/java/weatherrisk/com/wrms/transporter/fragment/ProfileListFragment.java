package weatherrisk.com.wrms.transporter.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.EditProfileActivity;
import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.DBAdapter;
import weatherrisk.com.wrms.transporter.dataobject.ProfileData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileListFragment newInstance(String param1, String param2) {
        ProfileListFragment fragment = new ProfileListFragment();
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
    }

    ListView listView;
    ArrayList<String> firmListArray = new ArrayList<>();
    DBAdapter db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_profile_list, container, false);

        View rootView = inflater.inflate(R.layout.fragment_profile_list, container, false);

        db = new DBAdapter(getActivity());
        db.open();

        final Cursor firmListCursor = db.firmList();

        if(firmListArray.size()== 0) {
            if (firmListCursor.moveToFirst()) {
                do {

                    firmListArray.add(firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.FIRM_NAME)));

                } while (firmListCursor.moveToNext());
            }
        }

        listView = (ListView) rootView.findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_view_single_item, firmListArray);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(firmListCursor.moveToPosition(position)) {
                    ProfileData data = new ProfileData();
                    String firmId = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.FIRM_ID));
                    String firmName = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.FIRM_NAME));
                    String contactName = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.CONTACT_NAME));
                    String personContactNo = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.PERSON_CONTACT_NO));
                    String homeContactNo = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.HOME_CONTACT_NO));
                    String officeContactNo = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.OFFICE_CONTACT_NO));
                    String tinNo = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.TIN_NO));
                    String address = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.ADDRESS));
                    String email = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.EMAIL));
                    String stateId = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.STATE_ID));
                    String cityId = firmListCursor.getString(firmListCursor.getColumnIndex(DBAdapter.CITY_ID));
                //    data.setFirmId(firmId);
                  //  data.setFirmName(firmName);
                    data.setPersonContactNo(personContactNo);
                    data.setHomeContactNo(homeContactNo);
                    data.setContactName(contactName);
                    data.setOfficeContactNo(officeContactNo);
                    data.setTinNo(tinNo);
                    data.setAddress(address);
                    data.setEmail(email);
                    data.setStateId(stateId,db);
                    data.setCityId(cityId,db);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fT = fragmentManager.beginTransaction();
                  //  Fragment PersonalInfoFragment = EditProfileActivity(data);
                    fT.addToBackStack(null);
                   // fT.replace(R.id.frameLayoutInner, PersonalInfoFragment).commit();


                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;

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
