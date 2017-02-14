package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.api.services.gmail.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.fragments.SubscriptionsFragment;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;

/**
 * Created by alex on 1/23/17.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private final SubscriptionsFragment mFragment;
    private ArrayList<Subscription> mSubscriptions;
    private ArrayList<Subscription> filterSubs = new ArrayList<>();
    private Context mContext;
    private SubscriptionsFragment.Callback mCallback;
    public List<Email> matchingEmails = Collections.synchronizedList(new ArrayList<Email>());
    private int mThresh;

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mSubscription;
        private TextView mSubscriptionCount;
        private TextView mSubscriptionPreview;
        private TextView mSubscriptionDate;
        private CheckBox mfavoritebox;
        private View mSubView;

        public ViewHolder(View itemView) {
            super(itemView);
            mSubView = itemView;
            mSubscription = (TextView) itemView.findViewById(R.id.subscription_title);
            mSubscriptionCount = (TextView) itemView.findViewById(R.id.subscription_count);
            mSubscriptionPreview = (TextView) itemView.findViewById(R.id.subscription_preview);
            mSubscriptionDate = (TextView) itemView.findViewById(R.id.subscription_date);
            mfavoritebox = (CheckBox) itemView.findViewById(R.id.favorite_subscription);
        }

        @Override
        public void onClick(View v) {
            mSubscriptions.get(getAdapterPosition()).clicks++;
        }
    }

    public SubscriptionAdapter(SubscriptionsFragment fragment, SubscriptionsFragment.Callback callback, ArrayList<Subscription> subscriptions) {
        mContext = fragment.getContext();
        mSubscriptions = subscriptions;
        mCallback = callback;
        filterSubs.addAll(mSubscriptions);
        mFragment = fragment;
        mThresh = 0;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscriptions_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mSubscription.setText(mSubscriptions.get(position).getTitle());
        holder.mSubscriptionCount.setText(mSubscriptions.get(position).getSize() + "");
        holder.mSubscriptionPreview.setText(mSubscriptions.get(position).getNewestSubject());
        holder.mSubscriptionDate.setText(mSubscriptions.get(position).getDateString());
        holder.mSubView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.Callback(mSubscriptions.get(position).getEmails());
            }
        });
        holder.mfavoritebox.setChecked(mSubscriptions.get(position).isFavorited());
        holder.mfavoritebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubscriptions.get(position).setFavorited(!mSubscriptions.get(position).isFavorited());
                Collections.sort(mSubscriptions);
                notifyDataSetChanged();
            }
        });
//        if (mSubscriptions.get(position).getThresh()) {
//            holder.mSubView.setBackgroundColor(mContext.getResources().getColor(R.color.aboveThreshold));
//        } else {
//            holder.mSubView.setBackgroundColor(mContext.getResources().getColor(R.color.cardview_dark_background));
//        }
    }


    @Override
    public int getItemCount() {
        return (null != filterSubs ? filterSubs.size() : 0);
    }

    public ArrayList<Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    public void filter(final String newText) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                filterSubs.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(newText)) {

                    filterSubs.addAll(mSubscriptions);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Subscription item : mSubscriptions) {
                        //should find all emails that match the query
                        matchingEmails = item.getMatchingEmails(newText.toLowerCase());
                    }
                }
                // Set on UI Thread
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();
    }

    public void updateFilter(Subscription subscription) {
        if (subscription != null) {
            filterSubs.add(subscription);
            Collections.sort(filterSubs);
        }
        notifyDataSetChanged();
    }

    public void updateThreshold(int thresh) {
        Log.d("test", thresh+"");
        for (int i = 0; i < mSubscriptions.size(); i++) {
            if (mSubscriptions.get(i).getSize() >= thresh) {
                mSubscriptions.get(i).setAboveThresh(true);
            } else {
                mSubscriptions.get(i).setAboveThresh(false);
            }
        }
        notifyDataSetChanged();
    }
}
