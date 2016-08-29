package com.ivan.autosms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Sender s;
    private EditText mEdit;
    private EditText editPhone;

    private void openDialog(Sender s) {
        String messageText = String.format(
                "This will send %d SMS for license plate %s. Your ticket will be valid until %s.",
                s.getNumMessages(),
                s.getLicensePlate(),
                s.getEndTime());
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(messageText);
        dialogBuilder.setCancelable(true);

        final int n = s.getNumMessages();
        final String ss = s.getLicensePlate();
        dialogBuilder.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                for (int i = 0; i < n; i++)
                SmsManager.getDefault().sendTextMessage(editPhone.getText().toString(), null, ss, null, null);
            }
        });

        dialogBuilder.setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog sendDialog = dialogBuilder.create();
        sendDialog.show();
    }

    private void askForPermissions() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

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
    }

    public void onButtonClick(View view) {
        Calendar c = Calendar.getInstance(Locale.getDefault());
        int nMessages = 17 - c.get(Calendar.HOUR_OF_DAY);

        if (nMessages > 0) {
            s = new Sender(nMessages, mEdit.getText().toString());
            openDialog(s);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEdit   = (EditText)findViewById(R.id.editText3);
        editPhone = (EditText)findViewById(R.id.editText2);

        askForPermissions();
    }
}
