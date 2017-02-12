package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.model.Message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.SubscriptionsFragment;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDataTask;

/**
 * Created by alex on 1/23/17.
 */

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> implements EmailDataTask.OnEmailLoaded{

    private ArrayList<Subscription> mSubscriptions = new ArrayList<>();
    private ArrayList<Subscription> filterSubs = new ArrayList<>();
    private Context mContext;
    private SubscriptionsFragment.Callback mCallback;



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

        @Override
        public void onClick(View v) {
            mSubscriptions.get(getAdapterPosition()).clicks++;
        }
    }

    public SubscriptionAdapter(Fragment activity, SubscriptionsFragment.Callback callback) {
        mContext = activity.getContext();
        mCallback = callback;
        filterSubs.addAll(mSubscriptions);
       // populateSubscriptions(activity);
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
        holder.mSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.Callback(mSubscriptions.get(position).getEmails());
            }
        });
    }


    @Override
    public int getItemCount() {
        return (null != filterSubs ? filterSubs.size() : 0);
    }

    public ArrayList<Subscription> getmSubscriptions(){
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
                        if (item.getTitle().toLowerCase().contains(newText.toLowerCase())) {
                            // Adding Matched items
                            filterSubs.add(item);
                        }
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


    public void populateSubscriptions(com.google.api.services.gmail.Gmail service, List<Message> emails) {

//        Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").create();
//        Type listType = new TypeToken<List<Email>>(){}.getType();
//        InputStream is = mContext.getResources().openRawResource(R.raw.mock_emails);
//        Reader reader = new BufferedReader(new InputStreamReader(is));
//        List<Email> testEmails = gson.fromJson(reader, listType);
//        Collections.sort(emails);
////        try {
////            reader.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //Log.d("TAG", emails.get(0).getDateString().toString());
//        mSubscriptions.add(new Subscription(emails.get(0).getSender()));
//        mSubscriptions.get(0).addEmail(emails.get(0));
//        Log.d("TAG", mSubscriptions.get(0).getTitle());
//        boolean didContain;
//        for (int i = 1; i < emails.size(); i++) {
//            didContain = false;
//            for (int j = 0; j < mSubscriptions.size(); j++) {
//                if (mSubscriptions.get(j).getTitle().equals(emails.get(i).getSender())) {
//                    mSubscriptions.get(j).addEmail(emails.get(i));
//                    didContain = true;
//                }
//            }
//            if (!didContain) {
//                mSubscriptions.add(new Subscription(emails.get(i).getSender()));
//                mSubscriptions.get(mSubscriptions.size()-1).addEmail(emails.get(i));
//            }
//        }
//        filterSubs.addAll(mSubscriptions);
//        notifyDataSetChanged();
        com.google.api.services.gmail.Gmail mService;
        for (Message message : emails)
            new EmailDataTask(message, this, service).execute();
    }

    @Override
    public void emailLoaded(Email email) {
        boolean foundSubscription = false;
        for (Subscription subscription : mSubscriptions) {
           if(subscription.getTitle().equals(email.getSender())) {
               foundSubscription = true;
               subscription.addEmail(email);
           }
        }
        if (foundSubscription == false) {
            Subscription subscription = new Subscription(email.getSender());
            subscription.addEmail(email);
            mSubscriptions.add(subscription);
            filterSubs.add(subscription);
        }
        Collections.sort(mSubscriptions);
        Collections.sort(filterSubs);
        notifyDataSetChanged();
   }
}
