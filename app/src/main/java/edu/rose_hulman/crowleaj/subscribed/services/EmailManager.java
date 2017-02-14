package edu.rose_hulman.crowleaj.subscribed.services;

import android.util.Log;

import com.google.api.services.gmail.model.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.MainActivity;
import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDataTask;
import edu.rose_hulman.crowleaj.subscribed.tasks.EmailDeleteTask;
import edu.rose_hulman.crowleaj.subscribed.tasks.MakeRequestTask;

/**
 * Created by alex on 2/13/17.
 */

public class EmailManager implements MakeRequestTask.OnEmailsReceived, EmailLoader.OnLoaderUpdate {

    private MainActivity activity;
    private ArrayList<Subscription> mSubscriptions;
    private com.google.api.services.gmail.Gmail mService;
    private EmailLoader mLoader;

    public EmailManager(MainActivity activity, ArrayList<Subscription> subscriptions, SubscriptionCache cache) {
        this.activity = activity;
        mSubscriptions = subscriptions;
        mLoader = new EmailLoader(cache, this);
    }

    public void requestEmails(com.google.api.services.gmail.Gmail service) {
        mService = service;
        mLoader.readEmails();
    }


    public void fetchEmails() {
        int size = mSubscriptions.size();
        if (size == 0) {
            new MakeRequestTask(mService, activity, this).execute();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(mSubscriptions.get(0).getDate());
            cal.add(Calendar.DATE, -1);
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY/MM/dd");
            String date = formatter.format(cal.getTime());
            new MakeRequestTask(mService, activity, this, date).execute();
        }
    }

    @Override
    public void emailsReceived(List<Message> emails) {
        if (emails == null) {
            Log.d(Util.TAG_DEBUG, "NULLLL");

        } else {
           mLoader.setLoadCount(emails.size());
            outer : for (Message message : emails) {
                for (Subscription subscription : mSubscriptions) {
                    if (subscription.containsId(message.getId()))
                        continue outer;
                }
                new EmailDataTask(message, mLoader, mService).execute();
            }
        }
    }

    @Override
    public void onFilterUpdate(Subscription subscription) {
        activity.updateFilter(subscription);
    }

    protected ArrayList<Subscription> getSubscriptions() {
        return mSubscriptions;
    }

    public void onDeleteEmail(Email email, String account) {
        new EmailDeleteTask(email.id, account, mService).execute();
//        try {
//            Log.d("WORK", account);
//
//            mService.users().messages().trash(account, email.id).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
