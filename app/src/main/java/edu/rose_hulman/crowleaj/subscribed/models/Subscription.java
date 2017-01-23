package edu.rose_hulman.crowleaj.subscribed.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by alex on 1/23/17.
 */

public class Subscription {

    ArrayList<Email> mEmails = new ArrayList<>();

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
}
