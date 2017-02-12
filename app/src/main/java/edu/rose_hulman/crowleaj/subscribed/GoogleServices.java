package edu.rose_hulman.crowleaj.subscribed;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;

import edu.rose_hulman.crowleaj.subscribed.tasks.MakeRequestTask;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by alex on 2/11/17.
 */

public class GoogleServices {
    private final SubscriptionsFragment mFragment;
    private final Activity mActivity;

    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY };

    private static final String PREF_ACCOUNT_NAME = "accountName";

    GoogleAccountCredential mCredential;

    private com.google.api.services.gmail.Gmail mService = null;

    public GoogleServices(SubscriptionsFragment fragment) {
        mFragment = fragment;
        mActivity = fragment.getActivity();
        //Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                fragment.getContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    public void setAccountName(String name) {
        mCredential.setSelectedAccountName(name);
    }

    public GoogleAccountCredential getCredential() {
        return mCredential;
    }

    public com.google.api.services.gmail.Gmail getService() {
        return mService;
    }
    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void getResultsFromApi() {
        if (! isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            mFragment.chooseAccount();
        } else if (! isDeviceOnline()) {
            // mOutputText.setText("No network connection available.");
        } else {
            Log.d(Util.TAG_GOOGLE, mCredential.getSelectedAccountName());
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
            mFragment.mAdapter.readEmails();
            if (mFragment.mAdapter.mEmails.size() > 0)
                mFragment.mAdapter.populateSubscriptions(mService, null);
            else
                new MakeRequestTask(mService, mFragment, mFragment).execute();
        }
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mActivity);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            Log.d(Util.TAG_GOOGLE, "" + connectionStatusCode);
            // showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
