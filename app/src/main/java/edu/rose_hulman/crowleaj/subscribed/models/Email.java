package edu.rose_hulman.crowleaj.subscribed.models;

import java.util.Date;

/**
 * Created by alex on 1/23/17.
 */

public class Email implements Comparable {

    Date date;
    public String subject;

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Object o) {
        return date.compareTo(((Email)o).getDate());
    }
}
