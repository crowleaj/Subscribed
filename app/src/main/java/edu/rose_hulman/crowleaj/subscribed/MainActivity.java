package edu.rose_hulman.crowleaj.subscribed;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.adapters.SubscriptionAdapter;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import edu.rose_hulman.crowleaj.subscribed.fragments.AboutFragment;
import edu.rose_hulman.crowleaj.subscribed.fragments.EmailFragment;
import edu.rose_hulman.crowleaj.subscribed.fragments.SpecificFragment;
import edu.rose_hulman.crowleaj.subscribed.fragments.SplashFragment;
import edu.rose_hulman.crowleaj.subscribed.fragments.SubscriptionsFragment;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.services.EmailManager;
import edu.rose_hulman.crowleaj.subscribed.services.GoogleServices;
import edu.rose_hulman.crowleaj.subscribed.services.SubscriptionCache;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SubscriptionsFragment.Callback, SpecificFragment.OnSpecificCallback, SplashFragment.AccountChooser,
        EasyPermissions.PermissionCallbacks, EmailFragment.OnFragmentInteractionListener, SpecificFragment.OnDeleteCallback {

    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final String PREF_ACCOUNT_NAME = "accountName";

    private GoogleServices mServices;

    private ArrayList<Subscription> mSubscriptions = new ArrayList<>();
    private SubscriptionsFragment mSubscriptionsFrag = null;

    private boolean splashShown = false;

    private EmailManager mManager;
    private SubscriptionCache mCache;
    private String mAccountName;
    private String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = new GoogleServices(this);
        mCache = new SubscriptionCache(this);
        mManager = new EmailManager(this, mSubscriptions, mCache);
        String accountName = getPreferences(Context.MODE_PRIVATE)
                .getString(PREF_ACCOUNT_NAME, null);

        if (accountName != null) {
            mAccountName = accountName;
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
        View header = navigationView.getHeaderView(0);
        TextView gmail = (TextView)header.findViewById(R.id.user_gmail);
        gmail.setText(mAccountName);
        TextView name = (TextView)header.findViewById(R.id.user_name);
        name.setText(name.getText());

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

    public void readEmails() {
        mManager.requestEmails(mServices.getService());
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
        Fragment switchTo = null;

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            switchTo = new SubscriptionsFragment();
        } else if (id == R.id.nav_gallery) {
            switchTo = new AboutFragment();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }


        if(switchTo!=null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container,switchTo);
            Log.d("NAv", "onNavigationItemSelected: hello");
            for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            ft.commit();

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
//        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//        intent.putExtra(SearchManager.QUERY, email.getSubject());
//        startActivity(intent);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        EmailFragment fragment = EmailFragment.newInstance(email);
        ft.replace(R.id.container, fragment);
        ft.addToBackStack("email");
        ft.commit();
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    //TODO: throw some error
                } else {
                    switchToSubsciptionsFragment();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    Log.d("Name", "onActivityResult: "+AccountManager.AUTHENTICATOR_ATTRIBUTES_NAME);
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

    public void updateFilter(Subscription subscription) {
        mSubscriptionsFrag.mAdapter.updateFilter(subscription);
    }

    public ArrayList<Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    @Override
    public void onEmailCallback() {

    }

    @Override
    public void onDelete(Email email) {
        Log.d("TEst", "TO DELETE ");
        mManager.onDeleteEmail(email, mAccountName);
    }
}