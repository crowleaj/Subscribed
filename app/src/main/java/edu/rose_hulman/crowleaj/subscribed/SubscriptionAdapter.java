package edu.rose_hulman.crowleaj.subscribed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;

/**
 * Created by alex on 1/23/17.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    private ArrayList<Subscription> mSubscriptions = new ArrayList<>();
    private Context mContext;

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSubscription;
        private TextView mSubscriptionCount;
        private TextView mSubscriptionPreview;
        private TextView mSubscriptionDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mSubscription = (TextView) itemView.findViewById(R.id.subscription_title);
            mSubscriptionCount = (TextView) itemView.findViewById(R.id.subscription_count);
            mSubscriptionPreview = (TextView) itemView.findViewById(R.id.subscription_preview);
            mSubscriptionDate = (TextView) itemView.findViewById(R.id.subscription_date);
        }
    }

    public SubscriptionAdapter(Context context) {
        mContext = context;
        populateSubscriptions();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscriptions_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSubscription.setText(mSubscriptions.get(position).getTitle());
        holder.mSubscriptionCount.setText(mSubscriptions.get(position).getSize() + "");
        holder.mSubscriptionPreview.setText(mSubscriptions.get(position).getNewestSubject());
        holder.mSubscriptionDate.setText(mSubscriptions.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return mSubscriptions.size();
    }

    public void populateSubscriptions() {
        Gson gson = new GsonBuilder().setDateFormat("M/d/yyyy").create();
        Type listType = new TypeToken<List<Email>>(){}.getType();
        InputStream is = mContext.getResources().openRawResource(R.raw.mock_emails);
        Reader reader = new BufferedReader(new InputStreamReader(is));
        List<Email> emails = gson.fromJson(reader, listType);
        Collections.sort(emails);
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("TAG", emails.get(0).getDate().toString());
        mSubscriptions.add(new Subscription(emails.get(0).getSender()));
        mSubscriptions.get(0).addEmail(emails.get(0));
        Log.d("TAG", mSubscriptions.get(0).getTitle());
        boolean didContain;
        for (int i = 1; i < emails.size(); i++) {
            didContain = false;
            for (int j = 0; j < mSubscriptions.size(); j++) {
                if (mSubscriptions.get(j).getTitle().equals(emails.get(i).getSender())) {
                    mSubscriptions.get(j).addEmail(emails.get(i));
                    didContain = true;
                }
            }
            if (!didContain) {
                mSubscriptions.add(new Subscription(emails.get(i).getSender()));
                mSubscriptions.get(mSubscriptions.size()-1).addEmail(emails.get(i));
            }
        }
    }
}
