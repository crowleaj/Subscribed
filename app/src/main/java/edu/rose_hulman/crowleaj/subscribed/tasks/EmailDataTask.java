package edu.rose_hulman.crowleaj.subscribed.tasks;

import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.rose_hulman.crowleaj.subscribed.Util;
import edu.rose_hulman.crowleaj.subscribed.models.Email;

/**
 * Created by alex on 2/11/17.
 */

public class EmailDataTask extends AsyncTask<Void, Void, Email> {

    static DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    static DateFormat df2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    static DateFormat df3 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);


    private Message message;
    private OnEmailLoaded mCallback;
    private com.google.api.services.gmail.Gmail mService;

    public EmailDataTask(Message message, OnEmailLoaded callback, com.google.api.services.gmail.Gmail service) {
        this.message = message;
        mCallback = callback;
        mService = service;
    }
    @Override
    protected Email doInBackground(Void... params) {
        try {

        Message m = mService.users().messages().get("me", message.getId()).setFormat("FULL").execute();
        MessagePart part = m.getPayload();
//            if (part != null)
        Date date = null;
        String subject = null;
        String sender = null;
        String content = StringUtils.newStringUtf8(Base64.decodeBase64(part.getBody().getData()));

            if (content == null){
                int i = 0;
                String textPart = "";
                for(MessagePart mPart : part.getParts()){
                //    if (!mPart.getMimeType().equals("text/html"))
                 //       Log.d(Util.TAG_DEBUG, mPart.getMimeType());
                    if (mPart.getMimeType().equals("text/plain"))
                        textPart = StringUtils.newStringUtf8(Base64.decodeBase64(mPart.getBody().getData()));
                    else {
                        if (content == null)
                            content = StringUtils.newStringUtf8(Base64.decodeBase64(mPart.getBody().getData()));
                        else
                            content += StringUtils.newStringUtf8(Base64.decodeBase64(mPart.getBody().getData()));
                    }
                   // Log.d(Util.TAG_DEBUG, mPart.getMimeType() + " " + i++);
                   // Log.d(Util.TAG_DEBUG, StringUtils.newStringUtf8(Base64.decodeBase64(mPart.getBody().getData())));
                }
                if (content == null)
                    content = textPart;
            }
        for (MessagePartHeader header : part.getHeaders() ) {
            if (header.getName().equals("From")) {
                sender = header.getValue();
                int index = sender.indexOf('<');
                if (index != 0)
                    sender = sender.substring(0, index-1);
            } else if (header.getName().equals("Subject")) {
                subject = header.getValue();
            } else if (header.getName().equals("Date")) {
                //date = header.getValue();

                try {
                    date = df.parse(header.getValue());
                } catch (Exception e) {
                    try {

                        date = df2.parse(header.getValue());
                    }
                    catch (Exception e1) {
                        try {

                            date = df3.parse(header.getValue());
                        }
                        catch (Exception e2) {
                            Log.e(Util.TAG_DEBUG, e2.getMessage());
                        }
                    }
                }
            }
//                else {
//                    Log.d("ASDF",header.getName());
//                }
        }
        Email email = new Email(message.getId());
        email.date = date;
        email.subject = subject;
        email.sender = sender;
        email.content = content;
        return email;
        } catch (Exception e) {
            //mLastError = e;
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Email email) {
        super.onPostExecute(email);
        mCallback.emailLoaded(email);
    }

    public interface OnEmailLoaded {
        void emailLoaded(Email email);
        void emailCanceled();
    }
}

