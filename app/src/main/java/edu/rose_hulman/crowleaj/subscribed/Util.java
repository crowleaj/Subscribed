package edu.rose_hulman.crowleaj.subscribed;

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

import edu.rose_hulman.crowleaj.subscribed.models.Email;
import edu.rose_hulman.crowleaj.subscribed.models.Subscription;

/**
 * Created by alex on 2/11/17.
 */

public class Util {
    public static final String TAG_GOOGLE = "GOOG";
    public static final String TAG_DEBUG = "DEBUG";

    private static Gson gson = new GsonBuilder().setDateFormat("MM/dd/yyyy").setLenient().create();
    private static Type listType = new TypeToken<List<Email>>(){}.getType();

    public static void writeEmails(Context context, List<Subscription> subscriptions) {
        Type listType = new TypeToken<List<Email>>(){}.getType();
        try {
            FileOutputStream fos = context.openFileOutput("EMAILS", Context.MODE_PRIVATE);
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

    public static List<Email> readEmails(Context context) {
        try {
            InputStream is = context.openFileInput("EMAILS");
            Reader reader = new BufferedReader(new InputStreamReader(is));
            List<Email> mEmails = gson.fromJson(reader, listType);
            reader.close();
            return mEmails;
        } catch (Exception e) {
            return null;
        }
    }
}
