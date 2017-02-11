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
public class MakeRequestTask extends AsyncTask<Void, Void, List<Email>> {
    private com.google.api.services.gmail.Gmail mService = null;
    private Exception mLastError = null;
    private Fragment mActivity;
    private OnEmailsReceived mReceiver;

    public MakeRequestTask(GoogleAccountCredential credential, Fragment activity,  OnEmailsReceived receiver) {
        mActivity = activity;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Gmail API Android Quickstart")
                .build();
        mReceiver = receiver;
    }

    /**
     * Background task to call Gmail API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected List<Email> doInBackground(Void... params) {
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
    private List<Email> getDataFromApi() throws IOException {
        // Get the labels in the user's account.
        String user = "me";
        List<Email> emails = new ArrayList<Email>();
//        ListLabelsResponse listResponse =
//                mService.users().labels().list(user).execute();
        //"E, dd MM YYYY HH:mm:ss Z"
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        DateFormat df2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        DateFormat df3 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);


        ListMessagesResponse listResponse = mService.users().messages().list(user).setQ("unsubscribe").execute();
        for (Message message : listResponse.getMessages()) {
            Message m = mService.users().messages().get("me", message.getId()).execute();
            MessagePart part = m.getPayload();
//            if (part != null)
            Date date = null;
            String subject = null;
            String sender = null;
            String content = StringUtils.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));
            if (content == null) content = "";
           // Log.d("ASDF", content);
            for (MessagePartHeader header : part.getHeaders() ) {
                if (header.getName().equals("From")) {
                    sender = header.getValue();
                } else if (header.getName().equals("Subject")) {
                    subject = header.getValue();
                } else if (header.getName().equals("Date")) {
                    //date = header.getValue();

                    try {
                        date = df.parse(header.getValue());
                        //Log.d("ASDF",header.getValue());
                    } catch (Exception e) {
                        try {

                            date = df2.parse(header.getValue());
                        }
                        catch (Exception e1) {
                            try {

                                date = df3.parse(header.getValue());
                            }
                            catch (Exception e2) {
                                Log.e("ERR", e2.getMessage());
                            }
                        }
                    }
                    //Log.d("ASDF",header.getValue());
                }
//                else {
//                    Log.d("ASDF",header.getName());
//                }
            }
            Email email = new Email();
            email.date = date;
            email.subject = subject;
            email.sender = sender;
            email.content = content;
            emails.add(email);
        }

//        for (Label label : listResponse.getLabels()) {
//            labels.add(label.getName());
//        }
        return emails;
    }


    @Override
    protected void onPreExecute() {
        //mOutputText.setText("");
        //mProgress.show();
    }

    @Override
    protected void onPostExecute(List<Email> emails) {
        //mProgress.hide();
//        if (output == null || output.size() == 0) {
//            //mOutputText.setText("No results returned.");
//        } else {
//            Log.d("ASDF", TextUtils.join("\n", output));
//            output.add(0, "Data retrieved using the Gmail API:");
//          //  mOutputText.setText(TextUtils.join("\n", output));
//        }
        mReceiver.finished(emails);
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
        public void finished(List<Email> emails);
    }
}
