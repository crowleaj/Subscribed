package edu.rose_hulman.crowleaj.subscribed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SpecificFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpecificFragment extends android.support.v4.app.Fragment  {

    private OnSpecificCallback mListener;
    public SpecificAdapter mAdapter;

    public SpecificFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static SpecificFragment newInstance(ArrayList<Email> emails) {
        SpecificFragment fragment = new SpecificFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("key", emails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_specific, container, false);
        //Recycler View
        RecyclerView list = (RecyclerView) view.findViewById(R.id.recycler_specific);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(manager);
        mAdapter = new SpecificAdapter(getArguments().<Email>getParcelableArrayList("key"));
        list.setAdapter(mAdapter);
        setHasOptionsMenu(true);
        return view;
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSpecificCallback) {
            mListener = (OnSpecificCallback) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSpecificCallback {
        void onFragmentInteraction(Uri uri);
    }
}
