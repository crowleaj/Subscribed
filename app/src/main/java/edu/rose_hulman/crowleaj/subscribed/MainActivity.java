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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.adapters.SubscriptionAdapter;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.MakeRequestTask;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubscriptionsFragment.Callback, SpecificFragment.OnSpecificCallback, SplashFragment.AccountChooser,
        EasyPermissions.PermissionCallbacks {

    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private SubscriptionAdapter mAdapter;

    private GoogleServices mServices;

    private ArrayList<Subscription> mSubscriptions = new ArrayList<>();
    private SubscriptionsFragment mSubscriptionsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mServices = new GoogleServices(this);
        String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);
        if (accountName != null) {
            mServices.getCredential().setSelectedAccountName(accountName);
            mServices.getResultsFromApi();
            switchToSubsciptionsFragment();
        } else {
            Fragment frag = new SplashFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.drawer_layout, frag, "Splash");
            ft.commit();
        }
//        Fragment frag = new SubscriptionsFragment();
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.container, frag, "Fragment");
//        ft.commit();
//        chooseAccount();
//        getResultsFromApi();

    }

    public void switchToSubsciptionsFragment() {
        mSubscriptionsFrag = new SubscriptionsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, mSubscriptionsFrag, "Subscriptions");
        ft.commit();
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
                    mServices.getResultsFromApi();
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
                        mServices.getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    mServices.getResultsFromApi();
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
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                ImageFragment fragment = ImageFragment.newInstance(caption, url);
//                transaction.replace(R.id.fragment_stuff, fragment);
//                transaction.addToBackStack("Image");
//                transaction.commit();
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

    Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").setLenient().create();

    public void readEmails() {
        Type listType = new TypeToken<List<Email>>(){}.getType();
        try {
            InputStream is = getApplicationContext().openFileInput("EMAILS");
            Reader reader = new BufferedReader(new InputStreamReader(is));
            List<Email> mEmails = gson.fromJson(reader, listType);
            reader.close();
            mSubscriptionsFrag.mAdapter.populateSubscriptions(mServices.getService(), null);
//            if (mFragment.mAdapter.mEmails.size() > 0)
        } catch (Exception e) {
            new MakeRequestTask(mServices.getService(), mSubscriptionsFrag, mSubscriptionsFrag).execute();
            //e.printStackTrace();
        }
    }

    public void writeEmails() {
        Type listType = new TypeToken<List<Email>>(){}.getType();
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput("EMAILS", Context.MODE_PRIVATE);
            fos.write(("{").getBytes());
            for (int i = 0; i < mSubscriptions.size(); i++) {
                Subscription subscription = mSubscriptions.get(i);
                    for (Email email : subscription.getEmails()) {
                        fos.write((gson.toJson(email) + ",").getBytes());
                    }
            }
            fos.write(("}").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Subscription> getSubscriptions() {
        return mSubscriptions;
    }
}