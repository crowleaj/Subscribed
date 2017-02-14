package edu.rose_hulman.crowleaj.subscribed.tasks;

/**
 * Created by alex on 1/30/17.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.MainActivity;
import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, List<Message>> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;
    private Activity mActivity;
    private OnEmailsReceived mReceiver;
    private String mDate = null;
    public MakeRequestTask(com.google.api.services.gmail.Gmail service, Activity activity, OnEmailsReceived receiver) {
        mService = service;
        mReceiver = receiver;
        mActivity = activity;
    }
    public MakeRequestTask(com.google.api.services.gmail.Gmail service, Activity activity,  OnEmailsReceived receiver, String date) {
        this(service, activity, receiver);
        mDate = date;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<Message> doInBackground(Void... params) {
        try {
            return getDataFromApi();
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }

    /**
     * Fetch a list of Gmail labels attached to the specified account.
     * @return List of Strings labels.
     * @throws IOException
     */
    private List<Message> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<Email> emails = new ArrayList<Email>();
        //"E, dd MM YYYY HH:mm:ss Z"
        String query = "unsubscribe";
        if (mDate != null)
            query = "unsubscribe after:" + mDate;
        Log.d(Util.TAG_DEBUG, query);
        ListMessagesResponse listResponse = mService.users().messages().list(user).setQ(query).execute();
        return listResponse.getMessages();
    }

    @Override
    protected void onPostExecute(List<Message> emails) {
        if (emails == null)
            emails = new ArrayList<>();
        mReceiver.emailsReceived(emails);
    }

    @Override
    protected void onCancelled() {


        //mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                Log.d(Util.TAG_DEBUG, "Mission Critical error!");
//                showGooglePlayServicesAvailabilityErrorDialog(
//                        ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                mActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        MainActivity.REQUEST_AUTHORIZATION);
            }
            else {
                Log.d(Util.TAG_DEBUG, "The following error occurred:\n" + mLastError.getClass().getCanonicalName()
                        + mLastError.getMessage());
            }
        } else {
            Log.d(Util.TAG_DEBUG, "Mission Critical error!");
           // mOutputText.setText("Request cancelled.");
        }
    }

    public interface OnEmailsReceived {
        public void emailsReceived(List<Message> emails);
    }
}
