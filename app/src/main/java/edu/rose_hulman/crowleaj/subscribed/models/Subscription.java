package edu.rose_hulman.crowleaj.subscribed.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alex on 1/23/17.
 */

public class Subscription {

    ArrayList<Email> mEmails = new ArrayList<>();
    String title;
    String date;
    public int clicks;
    boolean favorited;

    public Subscription(String subTitle) {
        title = subTitle;
    }

    public String getNewestSubject() {
        if (mEmails.size() > 0) {
            return mEmails.get(0).subject;
        }
        return "";
    }

    public void addEmail(Email email) {
        mEmails.add(email);
        Collections.sort(mEmails);
    }

    public String getTitle() {
        return title;
    }

    public int getSize() {
        return mEmails.size();
    }

    public String getDate() {
        return date;
    }

    public ArrayList<Email> getMatchingEmails(String query) {
        ArrayList<Email> emails = new ArrayList<>();
        for (Email email : mEmails) {
            if (email.content.contains(query))
                emails.add(email);
        }
        return emails;
    }

    public boolean isFavorited() {
        return favorited;
    }
}
