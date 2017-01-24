package edu.rose_hulman.crowleaj.subscribed.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 1/23/17.
 */

public class Email implements Comparable {

    Date date;
    public String subject;
    public String sender;
    public String content;

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
}
