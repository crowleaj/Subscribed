package edu.rose_hulman.crowleaj.subscribed.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 1/23/17.
 */

public class Email implements Comparable, Parcelable {

    Date date;
    public String subject;
    public String sender;
    public String content;

    protected Email(Parcel in) {
        subject = in.readString();
        sender = in.readString();
        content = in.readString();
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
        return ((Email)o).getDate().compareTo(date);// date.compareTo(((Email)o).getDate());
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
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
