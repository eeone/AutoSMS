package com.ivan.autosms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivan on 8/20/16.
 */
public class Sender {

    private int nMessages = 0;
    private String licensePlate = "";

    public Sender () {
        nMessages = 0;
        licensePlate = "";
    }

    public Sender (int n, String lp) {
        nMessages = n;
        licensePlate = lp;
    }

    public int getNumMessages() {
        return nMessages;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getEndTime() {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        c.setTime(new Date());
        c.add(Calendar.HOUR_OF_DAY, nMessages);

        return new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(c.getTime());
    }

    public void send() {    };
}
