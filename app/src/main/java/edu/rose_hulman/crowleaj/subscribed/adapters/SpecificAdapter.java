package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.SpecificFragment;
import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * Created by barteeaj on 1/30/2017.
 */
public class SpecificAdapter extends RecyclerView.Adapter<SpecificAdapter.ViewHolder>  {
    private ArrayList<Email> emails;
    private SpecificFragment.OnSpecificCallback mCallback;

    public SpecificAdapter(ArrayList<Email> m, SpecificFragment.OnSpecificCallback callBack) {
        emails = m;
        mCallback = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_subscriptions, parent, false);
        return new SpecificAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(Util.TAG_DEBUG,"Content: " + emails.get(position).getContent());
        holder.mSubject.setText(emails.get(position).getSubject());
        holder.mBody.setText(Html.fromHtml(emails.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
        //Html.fromHtml(emails.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT)
        holder.mDate.setText(emails.get(position).getFormattedDate());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFragmentInteraction(emails.get(position));
            }
        });
    }

    public void deleteEmail(int position) {
        //Logic for deleting email goes here
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSubject;
        private TextView mBody;
        private TextView mDate;
        private View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mSubject = (TextView) itemView.findViewById(R.id.subject_specific);
            mBody = (TextView) itemView.findViewById(R.id.body_specific);
            mDate = (TextView) itemView.findViewById(R.id.date_specific);
        }
    }
}
