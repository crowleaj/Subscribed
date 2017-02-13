package edu.rose_hulman.crowleaj.subscribed.services;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDataTask;

/**
 * Created by alex on 2/13/17.
 */

public class EmailLoader implements EmailDataTask.OnEmailLoaded {

    private int loaded = 0;
    private int toLoad;
    private ArrayList<Subscription> mSubscriptions;
    private OnLoaderUpdate mManager;
    private SubscriptionCache mCache;

    public EmailLoader(SubscriptionCache cache, EmailManager manager) {
        mManager = manager;
        mSubscriptions = manager.getSubscriptions();
        mCache = cache;
    }

    private boolean read = false;
    public void readEmails() {
        if (read == false) {
            read = true;
            List<Subscription> subscriptions = mCache.readSubscriptions();
            if (subscriptions != null) {
                for (Subscription subscription : subscriptions) {
                    mSubscriptions.add(subscription);
                    mManager.onFilterUpdate(subscription);
                }
            }
//            if (mEmails != null) {
//                for (Email email : mEmails)
//                    emailLoaded(email);
//            }
        }
        mManager.fetchEmails();
    }

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
            mManager.onFilterUpdate(subscription);
        } else {
            mManager.onFilterUpdate(null);
        }
        Collections.sort(mSubscriptions);
        if (loaded == toLoad)
            mCache.writeSubscriptions(mSubscriptions);
    }

    @Override
    public void emailCanceled() {
        ++loaded;
        if (loaded == toLoad)
            mCache.writeSubscriptions(mSubscriptions);
    }

    public void setLoadCount(int loadCount) {
        toLoad = loadCount;
    }

    public void persistSubscriptions()  {
        mCache.writeSubscriptions(mSubscriptions);
    }

    public interface OnLoaderUpdate {
        void onFilterUpdate(Subscription subscription);
        void fetchEmails();
    }
}
