package edu.rose_hulman.crowleaj.subscribed;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.gmail.GmailScopes;

public class SplashActivity extends AppCompatActivity {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String[] SCOPES = { GmailScopes.GMAIL_LABELS };


    GoogleAccountCredential mCredential;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}
