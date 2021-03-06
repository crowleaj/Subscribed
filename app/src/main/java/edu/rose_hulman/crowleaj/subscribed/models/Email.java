package edu.rose_hulman.crowleaj.subscribed.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by alex on 1/23/17.
 */

public class Email implements Comparable, Parcelable {

    public String id;
    public Date date;
    public String subject;
    public String sender;
    public String content;
    public boolean matchesQuery;
    public boolean isFlagged;

    public Email(String id) {
        this.id = id;
    }
    protected Email(Parcel in) {
        id = in.readString();
        subject = in.readString();
        sender = in.readString();
        content = in.readString();
        matchesQuery = false;
        isFlagged = false;
    }

    public static final Creator<Email> CREATOR = new Creator<Email>() {
        @Override
        public Email createFromParcel(Parcel in) {
            return new Email(in);
        }

        @Override
        public Email[] newArray(int size) {
            return new Email[size];
        }
    };

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy");
        return formatter.format(date);
    }

    @Override
    public int compareTo(Object o) {
        return ((Email)o).getDate().compareTo(date);// date.compareTo(((Email)o).getDateString());
    }

    public String getSender() {
        return sender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subject);
        parcel.writeString(sender);
        parcel.writeString(content);
        parcel.writeByte((byte) (isFlagged ? 1 : 0));
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public boolean IsFlagged() {
        return isFlagged;
    }

    public void setFlag(boolean bool) {
        isFlagged = bool;
    }


    public boolean getMatchingEmails(String query) {
        if (content.contains(query))
            return true;
        else if(sender.toLowerCase().contains(query))
            return true;

        else if(subject.toLowerCase().contains(query))
            return true;
        return false;
    }
}
