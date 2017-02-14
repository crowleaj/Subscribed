package edu.rose_hulman.crowleaj.subscribed.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.IOException;

/**
 * Created by barteeaj on 2/13/2017.
 */

public class EmailDeleteTask extends AsyncTask {

    Gmail mService;
    String mID;
    String mAccount;

    public EmailDeleteTask(String id, String account, com.google.api.services.gmail.Gmail service) {
        mService = service;
        mID = id;
        mAccount = account;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {
            mService.users().messages().trash(mAccount, mID).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
