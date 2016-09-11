package com.ivan.autosms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Sender s;
    private String sendDialogMessage = "This will send %d SMS for license plate %s. Your ticket will be valid until %s.";
    private Date validUntilDate;

    private void askForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void onButtonClick(View view) {
        s.sendClicked();
    }

    public void openSendDialog(int nMessages, String phone, String licensePlate, String endTimeText) {
        String messageText = String.format(sendDialogMessage, nMessages, licensePlate, endTimeText);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(messageText);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    s.send();
                }
            }
        );

        dialogBuilder.setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            }
        );

        AlertDialog sendDialog = dialogBuilder.create();
        sendDialog.show();
    }

    public void openErrorDialog(String errorMessage) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(errorMessage);
        dialogBuilder.setCancelable(true);

        dialogBuilder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );

        AlertDialog errorDialog = dialogBuilder.create();
        errorDialog.show();
    }

    public void scanSMS(View view) {
        String[] phoneNumber = new String[] { "9119" };

        Cursor c = getApplicationContext().getContentResolver().query(
                Uri.parse("content://sms/inbox"),
                new String[] { "address", "date", "body" }, "address=?", phoneNumber, "date desc");

        StringBuffer msgData = new StringBuffer();
        if (c.moveToFirst()) {
            String msgBody = c.getString(2);
            String msgDate = msgBody.substring(22, 40);

            Calendar cal = Calendar.getInstance(Locale.getDefault());
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(msgDate.substring(0, 2)));
            cal.set(Calendar.MINUTE, Integer.parseInt(msgDate.substring(3, 5)));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(msgDate.substring(9, 11)));
            cal.set(Calendar.MONTH, Integer.parseInt(msgDate.substring(12, 14)) - 1);
            cal.set(Calendar.YEAR, 2000 + Integer.parseInt(msgDate.substring(15, 17)));

            DateFormat df = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            Date validUntilDate = cal.getTime();

            TextView validUntilText = (TextView)findViewById(R.id.validUntilDate);
            validUntilText.setText(df.format(validUntilDate));
        } else {
        }

        c.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s = new Sender(this);
        s.numberPickerStart.setMinValue(0);
        s.numberPickerStart.setMaxValue(24);
        s.numberPickerStart.setValue(8);
        s.numberPickerEnd.setMinValue(0);
        s.numberPickerEnd.setMaxValue(24);
        s.numberPickerEnd.setValue(17);


        askForPermissions();
    }
}
