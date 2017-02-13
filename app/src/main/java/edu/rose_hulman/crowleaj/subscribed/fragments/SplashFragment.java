package edu.rose_hulman.crowleaj.subscribed.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.adapters.SubscriptionAdapter;

/**
 * Created by alex on 2/12/17.
 */

public class SplashFragment extends Fragment implements View.OnClickListener {

    private AccountChooser mReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_splash, container, false);
        Button button = (Button) view.findViewById(R.id.account_pick_button);
        button.setOnClickListener(this);
        //chooseAccount();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mReceiver = (AccountChooser) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    @Override
    public void onClick(View v) {
        mReceiver.chooseAccount();
    }

    public interface AccountChooser {
        void chooseAccount();
    }
}
