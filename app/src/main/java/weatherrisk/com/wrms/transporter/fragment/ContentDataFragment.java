package weatherrisk.com.wrms.transporter.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import weatherrisk.com.wrms.transporter.R;
import weatherrisk.com.wrms.transporter.adapter.ContentDataAdapter;
import weatherrisk.com.wrms.transporter.adapter.CustomerConfirmOrderAdapter;
import weatherrisk.com.wrms.transporter.dataobject.ContentData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContentDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContentDataFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTENT_DATA_ARRAY = "content_data_array";
    private static final String ARG_PARAM2 = "param2";

    private ArrayList<ContentData> contentDataArrayList;
    private String mParam2;


    public ContentDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param contentDatas Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContentDataFragment.
     */

    public static ContentDataFragment newInstance(ArrayList<ContentData> contentDatas, String param2) {
        ContentDataFragment fragment = new ContentDataFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(CONTENT_DATA_ARRAY, contentDatas);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ContentDataAdapter adapter;
    ListView listview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentDataArrayList = getArguments().getParcelableArrayList(CONTENT_DATA_ARRAY);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content_data, container, false);;
        listview = (ListView)view.findViewById(R.id.listview);
        adapter = new ContentDataAdapter(getActivity(), contentDataArrayList);
        listview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

}
