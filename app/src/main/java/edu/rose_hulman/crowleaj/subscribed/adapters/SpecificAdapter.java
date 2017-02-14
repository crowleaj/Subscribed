package edu.rose_hulman.crowleaj.subscribed.adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.rose_hulman.crowleaj.subscribed.R;
import edu.rose_hulman.crowleaj.subscribed.fragments.SpecificFragment;
import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * Created by barteeaj on 1/30/2017.
 */
public class SpecificAdapter extends RecyclerView.Adapter<SpecificAdapter.ViewHolder>  {
    private ArrayList<Email> emails;
    private SpecificFragment.OnSpecificCallback mCallback;
    private SpecificFragment.OnDeleteCallback mDeleteCallback;
    private Context mContext;
    private View mView;
    private RecyclerView mRecycler;

    public SpecificAdapter(ArrayList<Email> m, SpecificFragment.OnSpecificCallback callBack,
                           SpecificFragment.OnDeleteCallback deleteCallback, Context context, View view, RecyclerView list) {
        emails = m;
        mCallback = callBack;
        mDeleteCallback = deleteCallback;
        mContext = context;
        mView = view;
        mRecycler = list;
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
       // holder.mBody.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        //holder.mBody.setScrollbarFadingEnabled(true);
//        holder.mBody.setPadding(0,0,0,0);
//        holder.mBody.getSettings().setBuiltInZoomControls(true);
//        holder.mBody.getSettings().setUseWideViewPort(true);
//        holder.mBody.getSettings().setLoadWithOverviewMode(true);
//        holder.mBody.loadUrl("file:///android_asset/empty.html");
//        holder.mBody.loadData(emails.get(position).getContent(), "text/html", "UTF-8");
       // holder.mBody.setText(Html.fromHtml(emails.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT));
        //Html.fromHtml(emails.get(position).getContent(), Html.FROM_HTML_MODE_COMPACT)
        holder.mDate.setText(emails.get(position).getFormattedDate());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onFragmentInteraction(emails.get(position));
            }
        });
    }

    public void deleteEmail(final int position) {
        //Logic for deleting email goes here
        final boolean[] wasDeleted = {true};
        final Email temp = emails.get(position);
        emails.remove(position);
        notifyItemRemoved(position);
        Snackbar snack = Snackbar.make(mView, "Undo deletion?", Snackbar.LENGTH_LONG);
        snack.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emails.add(position, temp);
                notifyItemInserted(position);
                mRecycler.scrollToPosition(position);
                wasDeleted[0] = false;
            }
        });
        snack.addCallback(new Snackbar.Callback() {

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                if (wasDeleted[0]) {
                    mDeleteCallback.onDelete(temp);
                }
            }

            @Override
            public void onShown(Snackbar snackbar) {
                //Do nothing
            }
        });
        snack.show();
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mSubject;
        private WebView mBody;
        private TextView mDate;
        private View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mSubject = (TextView) itemView.findViewById(R.id.subject_specific);
            mBody = (WebView) itemView.findViewById(R.id.body_specific);
            mDate = (TextView) itemView.findViewById(R.id.date_specific);
        }
    }
}
