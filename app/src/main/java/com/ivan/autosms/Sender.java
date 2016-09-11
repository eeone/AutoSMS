package com.ivan.autosms;

import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Sender {
    private String licensePlate = "";
    private String phone = "";
    private String endTimeText = "";
    private int nMessages;
    private Date validUntilDate = null;

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

    public boolean checkAlreadySent() {
        if (validUntilDate == null) {
            return false;
        }
        else {
            Calendar c = Calendar.getInstance(Locale.getDefault());
            int today = c.get(Calendar.DAY_OF_MONTH);

            c.setTime(validUntilDate);
            int lastDay = c.get(Calendar.DAY_OF_MONTH);
            int lastHour = c.get(Calendar.HOUR_OF_DAY);

            return !(lastDay == today && lastHour >= numberPickerEnd.getValue());
        }
    }

    public NMsgRetVal setNumMessages() {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int day = c.get(Calendar.DAY_OF_WEEK);

        int startOfDay = numberPickerStart.getValue();
        int endOfDay = numberPickerEnd.getValue();

        if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
            return NMsgRetVal.WEEKEND;
        }

        if (!checkAlreadySent()) {
            return NMsgRetVal.SENT_ALREADY;
        }

        if (hour > endOfDay) {
            return NMsgRetVal.PAST_FIVE;
        }

        c.setTime(new Date());
        if (hour < startOfDay) {
            nMessages = endOfDay - startOfDay;
            c.add(Calendar.HOUR_OF_DAY, nMessages + startOfDay - hour);
        }
        else {
            nMessages = endOfDay - hour;
            c.add(Calendar.HOUR_OF_DAY, nMessages);
        }
        endTimeText = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(c.getTime());

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

        mActivity.openSendDialog(nMessages, licensePlate, endTimeText);
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
    }

    public void scanSMS() {
        phone = editPhone.getText().toString();
        String[] phoneNumber = new String[] { phone };

        Cursor c = mActivity.getApplicationContext().getContentResolver().query(
                Uri.parse("content://sms/inbox"),
                new String[] { "address", "date", "body" }, "address=?", phoneNumber, "date desc");

        if (c != null && c.moveToFirst()) {
            String msgBody = c.getString(2);
            while (!msgBody.startsWith("U Beogradu")) {
                c.moveToNext();
                msgBody = c.getString(2);
            }

            String msgDate = msgBody.substring(112, 135);

            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(msgDate.substring(0, 2)));
            cal.set(Calendar.MINUTE, Integer.parseInt(msgDate.substring(3, 5)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(msgDate.substring(14, 16)));
            cal.set(Calendar.MONTH, Integer.parseInt(msgDate.substring(17, 19)) - 1);
            cal.set(Calendar.YEAR, 2000 + Integer.parseInt(msgDate.substring(20, 22)));

            validUntilDate = cal.getTime();


            DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
            TextView validUntilText = (TextView) mActivity.findViewById(R.id.validUntilDate);
            validUntilText.setText(df.format(validUntilDate));

            c.close();
        }
    }
}
