package edu.rose_hulman.crowleaj.subscribed;

import android.Manifest;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.api.services.gmail.model.Message;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.adapters.SubscriptionAdapter;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDataTask;
import edu.rose_hulman.crowleaj.subscribed.tasks.MakeRequestTask;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubscriptionsFragment.Callback, SpecificFragment.OnSpecificCallback, SplashFragment.AccountChooser,
        EasyPermissions.PermissionCallbacks, EmailDataTask.OnEmailLoaded, MakeRequestTask.OnEmailsReceived, EmailFragment.OnFragmentInteractionListener {

    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private SubscriptionAdapter mAdapter;

    private GoogleServices mServices;

    private ArrayList<Subscription> mSubscriptions = new ArrayList<>();
    private SubscriptionsFragment mSubscriptionsFrag = null;

    private boolean splashShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new GoogleServices(this);
        String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if (accountName != null) {
            reinflateLayout();
            mServices.getCredential().setSelectedAccountName(accountName);
            switchToSubsciptionsFragment();
        } else {
            splashShown = true;
            setContentView(R.layout.activity_main);
            Fragment frag = new SplashFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_layout, frag, "Splash");
            ft.commit();
        }
    }

    public void reinflateLayout() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void switchToSubsciptionsFragment() {
        if (mSubscriptionsFrag == null) {
            if (splashShown == true) {
                splashShown = false;
                reinflateLayout();
            }
            mSubscriptionsFrag = new SubscriptionsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, mSubscriptionsFrag, "Subscriptions");
            ft.commit();
        }
        //mServices.getResultsFromApi();

    }

    public GoogleServices getServices() {
        return mServices;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void Callback(ArrayList<Email> emails) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SpecificFragment fragment = SpecificFragment.newInstance(emails, (Toolbar) findViewById(R.id.toolbar));
        ft.replace(R.id.container, fragment);
        ft.addToBackStack("detail");
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Email email) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        EmailFragment fragment = EmailFragment.newInstance(email);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack("email");
        ft.commit();
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     *     activity result.
     * @param data Intent (containing result data) returned by incoming
     *     activity result.
     */
    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
//                    mOutputText.setText(
//                            "This app requires Google Play Services. Please install " +
//                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    switchToSubsciptionsFragment();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =

                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mServices.setAccountName(accountName);
                        switchToSubsciptionsFragment();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    switchToSubsciptionsFragment();
                }
                break;
        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    //@AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    public void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mServices.getCredential().newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        chooseAccount();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private int loaded = 0;
    private int toLoad;

    @Override
    public void emailLoaded(Email email) {
       if (email == null)
           return;
        ++loaded;
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
            mSubscriptionsFrag.mAdapter.updateFilter(subscription);
        } else {
            mSubscriptionsFrag.mAdapter.updateFilter(null);
        }
        Collections.sort(mSubscriptions);
        if (loaded == toLoad)
            Util.writeEmails(this, mSubscriptions);
    }

    @Override
    public void emailCanceled() {
        ++loaded;
        if (loaded == toLoad)
            Util.writeEmails(this, mSubscriptions);
    }

    private boolean read = false;
    public void readEmails() {
            if (read == false) {
                read = true;
                List<Email> mEmails = Util.readEmails(this);
                if (mEmails != null) {
                    for (Email email : mEmails)
                        emailLoaded(email);
                }
            }
        requestEmails();
    }

    public void requestEmails() {
        int size = mSubscriptions.size();
        if (size == 0) {
            new MakeRequestTask(mServices.getService(), mSubscriptionsFrag, this).execute();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mSubscriptions.get(0).getDate());
            cal.add(Calendar.DATE, -1);
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY/MM/dd");
            String date = formatter.format(cal.getTime());
            new MakeRequestTask(mServices.getService(), mSubscriptionsFrag, this, date).execute();
        }
    }

    public ArrayList<Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    @Override
    public void emailsReceived(List<Message> emails) {
        //mAdapter.populateSubscriptions(mServices.getService(), emails);
        if (emails == null) {
            Log.d(Util.TAG_DEBUG, "NULLLL");

        } else {
            toLoad = emails.size();
            outer : for (Message message : emails) {
                for (Subscription subscription : mSubscriptions) {
                    if (subscription.containsId(message.getId()))
                        continue outer;
                }
                new EmailDataTask(message, this, mServices.getService()).execute();
            }
        }
    }

    @Override
    public void onEmailCallback() {

    }
}