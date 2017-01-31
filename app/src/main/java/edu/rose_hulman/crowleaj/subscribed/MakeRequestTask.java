package edu.rose_hulman.crowleaj.subscribed;

/**
 * Created by alex on 1/30/17.
 */

import android.app.Activity;
import android.os.AsyncTask;
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
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Gmail API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;
    private Activity mActivity;
    MakeRequestTask(GoogleAccountCredential credential, Activity activity) {
        mActivity = activity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Gmail API Android Quickstart")
                .build();
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<String> doInBackground(Void... params) {
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
    private List<String> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<String> labels = new ArrayList<String>();
//        ListLabelsResponse listResponse =
//                mService.users().labels().list(user).execute();
        ListMessagesResponse listResponse = mService.users().messages().list(user).setQ("unsubscribe").execute();
        for (Message message : listResponse.getMessages()) {
            Message m = mService.users().messages().get("me", message.getId()).execute();
            MessagePart part = m.getPayload();
//            if (part != null)
            for (MessagePartHeader header : part.getHeaders() ) {
                if (header.getName().equals("From")) {
                    labels.add(header.getValue());
                } //else {
//                    Log.d("ASDF",header.getName());
//                }
            }
        }
//        for (Label label : listResponse.getLabels()) {
//            labels.add(label.getName());
//        }
        return labels;
    }


    @Override
    protected void onPreExecute() {
        //mOutputText.setText("");
        //mProgress.show();
    }

    @Override
    protected void onPostExecute(List<String> output) {
        //mProgress.hide();
        if (output == null || output.size() == 0) {
            //mOutputText.setText("No results returned.");
        } else {
            Log.d("ASDF", TextUtils.join("\n", output));
            output.add(0, "Data retrieved using the Gmail API:");
          //  mOutputText.setText(TextUtils.join("\n", output));
        }
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
                        MainActivity.REQUEST_AUTHORIZATION);
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
}
