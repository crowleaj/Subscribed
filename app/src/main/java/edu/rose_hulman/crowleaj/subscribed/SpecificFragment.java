package edu.rose_hulman.crowleaj.subscribed;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rose_hulman.crowleaj.subscribed.adapters.SpecificAdapter;
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
    public static String mTitle;
    public static Toolbar mToolbar;
    public RecyclerView mRecyclerView;

    public SpecificFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     */
    public static SpecificFragment newInstance(ArrayList<Email> emails, Toolbar toolbar) {
        mTitle = emails.get(0).getSender();
        toolbar.setTitle(mTitle);
        mToolbar = toolbar;
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
//        TextView title = (TextView) view.findViewById(R.id.sub_title);
//        title.setText(mTitle);

        //Recycler View
        RecyclerView list = (RecyclerView) view.findViewById(R.id.recycler_specific);
        mRecyclerView = list;
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(manager);
        mAdapter = new SpecificAdapter(getArguments().<Email>getParcelableArrayList("key"), mListener);
        list.setAdapter(mAdapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int swipedPosition = viewHolder.getAdapterPosition();
                SpecificAdapter adapter = (SpecificAdapter) mRecyclerView.getAdapter();
                adapter.deleteEmail(swipedPosition);
            }
        };
        ItemTouchHelper TouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        TouchHelper.attachToRecyclerView(list);
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

    @Override
    public void onDestroy(){
        mToolbar.setTitle(getResources().getString(R.string.app_name));
        super.onDestroy();
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
        void onFragmentInteraction(Email email);
    }
}
