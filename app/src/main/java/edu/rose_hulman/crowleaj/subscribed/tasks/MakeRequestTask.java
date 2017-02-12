package edu.rose_hulman.crowleaj.subscribed.tasks;

/**
 * Created by alex on 1/30/17.
 */

import android.app.Activity;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.webtoken.JsonWebSignature;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.rose_hulman.crowleaj.subscribed.SubscriptionsFragment;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, List<Message>> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;
    private Fragment mActivity;
    private OnEmailsReceived mReceiver;

    public MakeRequestTask(com.google.api.services.gmail.Gmail service, Fragment activity,  OnEmailsReceived receiver) {
        mActivity = activity;
//        HttpTransport transport = AndroidHttp.newCompatibleTransport();
//        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//        mService = new com.google.api.services.gmail.Gmail.Builder(
//                transport, jsonFactory, credential)
//                .setApplicationName("Gmail API Android Quickstart")
//                .build();
        mService = service;
        mReceiver = receiver;
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
//        ListLabelsResponse listResponse =
//                mService.users().labels().list(user).execute();
        //"E, dd MM YYYY HH:mm:ss Z"



        ListMessagesResponse listResponse = mService.users().messages().list(user).setQ("unsubscribe").execute();
//
//
//
//            emails.add(email);
//        }

//        for (Label label : listResponse.getLabels()) {
//            labels.add(label.getName());
//        }
        return listResponse.getMessages();
    }


    @Override
    protected void onPreExecute() {
        //mOutputText.setText("");
        //mProgress.show();
    }

    @Override
    protected void onPostExecute(List<Message> emails) {
        //mProgress.hide();
//        if (output == null || output.size() == 0) {
//            //mOutputText.setText("No results returned.");
//        } else {
//            Log.d("ASDF", TextUtils.join("\n", output));
//            output.add(0, "Data retrieved using the Gmail API:");
//          //  mOutputText.setText(TextUtils.join("\n", output));
//        }
        mReceiver.emailsReceived(emails);
    }

    @Override
    protected void onCancelled() {


        //mProgress.hide();
        if (mLastError != null) {
            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                Log.d("ASDF", "Mission Critical error!");
//                showGooglePlayServicesAvailabilityErrorDialog(
//                        ((GooglePlayServicesAvailabilityIOException) mLastError)
//                                .getConnectionStatusCode());
            } else if (mLastError instanceof UserRecoverableAuthIOException) {
                mActivity.startActivityForResult(
                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                        SubscriptionsFragment.REQUEST_AUTHORIZATION);
            }
            else {
                Log.d("ERR", "The following error occurred:\n" + mLastError.getClass().getCanonicalName()
                        + mLastError.getMessage());
            }
        } else {
            Log.d("ASDF", "Mission Critical error!");
           // mOutputText.setText("Request cancelled.");
        }
    }

    public interface OnEmailsReceived {
        public void emailsReceived(List<Message> emails);
    }
}
