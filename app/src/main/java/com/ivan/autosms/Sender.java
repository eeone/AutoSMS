package com.ivan.autosms;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ivan on 8/20/16.
 */
public class Sender {
    private String licensePlate = "";
    private String phone = "";
    private String endTimeText = "";
    private int nMessages;

    // App elements
    private MainActivity mActivity;
    private EditText editLPlate;
    private EditText editPhone;
    public NumberPicker numberPickerStart;
    public NumberPicker numberPickerEnd;

    private enum NMsgRetVal {
        OK_TO_SEND,
        SENT_ALREADY,
        PAST_FIVE,
        WEEKEND
    }

    public Sender(MainActivity m) {
        mActivity = m;
        editLPlate  = (EditText)m.findViewById(R.id.editText3);
        editPhone = (EditText)m.findViewById(R.id.editText2);
        numberPickerStart = (NumberPicker)m.findViewById(R.id.numberPickerStart);
        numberPickerEnd = (NumberPicker)m.findViewById(R.id.numberPickerEnd);
    }

    private boolean checkInbox() { return false; }

    public NMsgRetVal setNumMessages() {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int day = c.get(Calendar.DAY_OF_WEEK);

        int startOfDay = numberPickerStart.getValue();
        int endOfDay = numberPickerEnd.getValue();

        if (checkInbox()) {
            return NMsgRetVal.SENT_ALREADY;
        }

        if (hour > endOfDay) {
            return NMsgRetVal.PAST_FIVE;
        }

        if (day == c.SATURDAY || day == c.SUNDAY) {
            return NMsgRetVal.WEEKEND;
        }

        c.setTime(new Date());
        if (hour < startOfDay) {
            nMessages = endOfDay - startOfDay;
            c.add(Calendar.HOUR_OF_DAY, nMessages + startOfDay - hour);
        }
        else {
            nMessages = endOfDay - hour;
            c.add(Calendar.HOUR_OF_DAY, nMessages);
        };
        endTimeText = new SimpleDateFormat("dd MMM yyyy HH:mm:ss").format(c.getTime());

        return NMsgRetVal.OK_TO_SEND;
    }

    public void sendClicked() {
        NMsgRetVal result = setNumMessages();

        if (result == NMsgRetVal.OK_TO_SEND) {
            openSendDialog();
        }
        else {
            openErrorDialog(result);
        }
    }

    private void openSendDialog() {
        phone = editPhone.getText().toString();
        licensePlate = editLPlate.getText().toString();

        mActivity.openSendDialog(nMessages, phone, licensePlate, endTimeText);
    }

    private void openErrorDialog(NMsgRetVal r) {
        String errorMessage = "";
        switch(r) {
            case PAST_FIVE:
                errorMessage = "Time is past the zone end time.";
                break;
            case SENT_ALREADY:
                errorMessage = "All messages are already sent.";
                break;
            case WEEKEND:
                errorMessage = "Parking is free on weekends.";
                break;
            default:
                break;
        }

        mActivity.openErrorDialog(errorMessage);
    }

    public void send() {
        for (int i = 0; i < nMessages; i++) {
            SmsManager.getDefault().sendTextMessage(phone, null, licensePlate, null, null);
        }
    };
}
