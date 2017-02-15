package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.fragments.BlackListFragment;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;

import java.util.ArrayList;
import java.util.List;


public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.ViewHolder> {

    private final BlackListFragment.OnBlackListCallback mListener;
    private ArrayList<Subscription> blackSubs;
    private ArrayList<Subscription> allSubs;


    public BlackListAdapter(BlackListFragment.OnBlackListCallback callback, ArrayList<Subscription> blackSubscriptions, ArrayList<Subscription> allSubscriptions) {
        mListener = callback;
        allSubs = allSubscriptions;
        blackSubs = blackSubscriptions;
        Log.d("TEST", blackSubs.get(blackSubs.size()-1).getTitle());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_blacklist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.subTitle.setText( mListener.onBlackListInteraction().get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return blackSubs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private TextView subTitle;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            subTitle = (TextView)view.findViewById(R.id.blacklist_subname);

        }

    }
}
