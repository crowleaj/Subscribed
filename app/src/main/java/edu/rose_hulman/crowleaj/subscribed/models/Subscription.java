package edu.rose_hulman.crowleaj.subscribed.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.lang.*;

/**
 * Created by alex on 1/23/17.
 */

public class Subscription implements Comparable, Parcelable {

    ArrayList<Email> mEmails = new ArrayList<>();
    String title;
    Date date;
    public int clicks;
    Boolean favorited;
    boolean aboveThresh;
    boolean blackList;

    public Subscription(String subTitle) {
        title = subTitle;
    }

    protected Subscription(Parcel in) {
        mEmails = in.createTypedArrayList(Email.CREATOR);
        title = in.readString();
        clicks = in.readInt();
        favorited = false;
        aboveThresh = false;
        blackList = false;
    }

    public static final Creator<Subscription> CREATOR = new Creator<Subscription>() {
        @Override
        public Subscription createFromParcel(Parcel in) {
            return new Subscription(in);
        }

        @Override
        public Subscription[] newArray(int size) {
            return new Subscription[size];
        }
    };

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

    public String getDateString() {
        return mEmails.get(0).getFormattedDate();
    }

    public Date getDate() {
        return mEmails.get(0).getDate();
    }

    public ArrayList<Email> getMatchingEmails(String query) {
        ArrayList<Email> emails = new ArrayList<>();
        for (Email email : mEmails) {
            if (email.content.contains(query)){
                email.matchesQuery = true;
                emails.add(email);}
            else if(email.sender.toLowerCase().contains(query)){
                email.matchesQuery = true;
                emails.add(email);
            }
            else if(email.subject.toLowerCase().contains(query)){
                email.matchesQuery = true;
                emails.add(email);
            }
        }
        return emails;
    }

    public ArrayList<Email> getEmails() {
        return mEmails;
    }

    public Boolean isFavorited() {
        return favorited;
    }
    public Boolean isBlackListed() {return blackList;}
    public Boolean setBlackListed(boolean bool) {return blackList = bool;}
    public boolean setFavorited(boolean bool) {
        return favorited = bool;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mEmails);
        parcel.writeString(title);
        parcel.writeInt(clicks);
        parcel.writeByte((byte) (favorited ? 1 : 0));
        parcel.writeByte((byte) (aboveThresh ? 1 : 0));
    }

    @Override
    public int compareTo(Object o) {
        if (((Subscription)o).isFavorited().compareTo(isFavorited()) != 0) {
            return ((Subscription)o).isFavorited().compareTo(isFavorited());
        }
        return (((Subscription)o).getDate().compareTo(getDate()));// date.compareTo(((Email)o).getDateString());
    }

    public boolean containsId(String id) {
        for (Email email : mEmails) {
            if (email.id.equals(id))
                return true;
        }
        return false;
    }

    public void setAboveThresh(boolean bool) {
        aboveThresh = bool;
    }

    public boolean getThresh() {
        return aboveThresh;
    }
}
