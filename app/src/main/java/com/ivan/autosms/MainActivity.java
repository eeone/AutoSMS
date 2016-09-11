package com.ivan.autosms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Sender s;

    private void askForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
        }
    }

    public void onSendButtonClick(View view) {
        s.sendClicked();
    }

    public void openSendDialog(int nMessages, String licensePlate, String endTimeText) {
        String sendDialogMessage = "This will send %d SMS for license plate %s. Your ticket will be valid until %s.";
        String messageText = String.format(Locale.getDefault(), sendDialogMessage, nMessages, licensePlate, endTimeText);
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

    public void onScanButtonClicked(View view) {
        s.scanSMS();
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
