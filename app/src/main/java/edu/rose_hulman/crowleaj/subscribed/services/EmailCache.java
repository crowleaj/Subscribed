package edu.rose_hulman.crowleaj.subscribed.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;

/**
 * Created by alex on 2/13/17.
 */

public class EmailCache {
    private Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").setLenient().create();
    private Type listType = new TypeToken<List<Email>>(){}.getType();
    private Context mContext;

    public EmailCache(Context context) {
        mContext = context;
    }

    public void writeEmails(List<Subscription> subscriptions) {
        Type listType = new TypeToken<List<Email>>(){}.getType();
        try {
            FileOutputStream fos = mContext.openFileOutput("EMAILS", Context.MODE_PRIVATE);
            fos.write(("[").getBytes());
            for (int i = 0; i < subscriptions.size(); i++) {
                Subscription subscription = subscriptions.get(i);
                for (Email email : subscription.getEmails()) {
                    fos.write((gson.toJson(email) + ",").getBytes());
                }
            }
            fos.write(("]").getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(Util.TAG_DEBUG, e.getMessage());
        }
        Log.d(Util.TAG_DEBUG, "Emails written");
    }

    public List<Email> readEmails() {
        try {
            InputStream is = mContext.openFileInput("EMAILS");
            Reader reader = new BufferedReader(new InputStreamReader(is));
            List<Email> mEmails = gson.fromJson(reader, listType);
            reader.close();
            return mEmails;
        } catch (Exception e) {
            return null;
        }
    }
}
