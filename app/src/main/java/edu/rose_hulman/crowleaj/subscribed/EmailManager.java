package edu.rose_hulman.crowleaj.subscribed;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDataTask;

/**
 * Created by alex on 2/13/17.
 */

public class EmailManager implements EmailDataTask.OnEmailLoaded{

    private int loaded = 0;
    private int toLoad;

    private ArrayList<Subscription> mSubscriptions;
    private MainActivity activity;

    public EmailManager(MainActivity activity, ArrayList<Subscription> subscriptions) {
        this.activity = activity;
        mSubscriptions = subscriptions;
    }

    @Override
    public void emailCanceled() {
        ++loaded;
        if (loaded == toLoad)
            Util.writeEmails(activity, mSubscriptions);
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
            activity.updateFilter(subscription);
        } else {
            activity.updateFilter(null);
        }
        Collections.sort(mSubscriptions);
        if (loaded == toLoad)
            Util.writeEmails(activity, mSubscriptions);
    }

    public void setLoadCount(int loadCount) {
        toLoad = loadCount;
    }
}
