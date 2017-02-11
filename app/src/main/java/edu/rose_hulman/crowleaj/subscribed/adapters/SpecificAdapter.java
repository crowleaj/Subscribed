package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * Created by barteeaj on 1/30/2017.
 */
public class SpecificAdapter extends RecyclerView.Adapter<SpecificAdapter.ViewHolder>  {
    private ArrayList<Email> emails;

    public SpecificAdapter(ArrayList<Email> m) {
        emails = m;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_subscriptions, parent, false);
        return new SpecificAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mSubject.setText(emails.get(position).getSubject());
        holder.mBody.setText(emails.get(position).getContent());
        holder.mDate.setText(emails.get(position).getFormattedDate());
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSubject;
        private TextView mBody;
        private TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            mSubject = (TextView) itemView.findViewById(R.id.subject_specific);
            mBody = (TextView) itemView.findViewById(R.id.body_specific);
            mDate = (TextView) itemView.findViewById(R.id.date_specific);
        }
    }
}
