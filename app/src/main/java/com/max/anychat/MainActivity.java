package com.max.anychat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.os.Build;
import android.util.Pair;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {

    // Declare instance variables to retain state
    private String lastEnteredPhoneNumber = "";
    private String lastEnteredCountryCode = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout main = findViewById(R.id.main);
        Button chat = findViewById(R.id.chat);
        Button contact = findViewById(R.id.contact);
        EditText phone = findViewById(R.id.phone);
        EditText cc = findViewById(R.id.cc);
        TextView numtext = findViewById(R.id.nutext);

        // Lambda expressions for touch and click listeners
        main.setOnTouchListener((v, event) -> {
            hideKeyboard(v);
            return false;
        });

        main.setOnClickListener(this::hideKeyboard);

        chat.setOnClickListener(v -> handleChatButtonClick(phone, cc, numtext));

        contact.setOnClickListener(v -> openContactPage());

    }

    private void handleChatButtonClick(EditText phone, EditText cc, TextView numtext) {
        String enteredPhoneNumber = phone.getText().toString();
        String enteredCountryCode = cc.getText().toString();
        Pair<String, String> processedValues;
        if (lastEnteredPhoneNumber.length()>0 && enteredPhoneNumber.length()<1) {
            processedValues = processPhoneNumber(lastEnteredCountryCode, lastEnteredPhoneNumber, phone, cc);
        }
        else {
            processedValues = processPhoneNumber(enteredCountryCode, enteredPhoneNumber, phone, cc);
        }
        enteredCountryCode = processedValues.first;
        enteredPhoneNumber = processedValues.second;

        boolean isValidCountryCode = isValidCountryCode(enteredCountryCode);
        boolean isValidPhoneNumber = isValidPhoneNumber(enteredPhoneNumber);

        if (isValidPhoneNumber && isValidCountryCode) {
            String formattedPhoneNumber = String.format("%s - %s - %s", enteredCountryCode, enteredPhoneNumber.substring(0, 5), enteredPhoneNumber.substring(5, 10));
            numtext.setText(formattedPhoneNumber);
            phone.setText(enteredPhoneNumber);

            // Check if WhatsApp is installed
            requestNotificationPermission();
            if (isWhatsAppInstalled()) {
                // Use Intent to open WhatsApp
                openWhatsApp(enteredCountryCode, enteredPhoneNumber);
            } else {
                // WhatsApp not installed, open web version
                openWhatsAppWeb(enteredCountryCode, enteredPhoneNumber);
            }
            // Store the entered values for the next click
            lastEnteredCountryCode = enteredCountryCode;
            lastEnteredPhoneNumber = enteredPhoneNumber;
        } else {
            phone.setError("Invalid Mobile Number!");
            alert();
            phone.setText("");
            numtext.setText("");
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Check if the app has the notification permission
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null && !notificationManager.isNotificationPolicyAccessGranted()) {
                // If not, request the user to grant notification access
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }

    private Pair<String, String> processPhoneNumber(String enteredCountryCode, String phoneNumber, EditText phone, EditText cc) {
        // Remove spaces and non-numeric characters
        phoneNumber = phoneNumber.replaceAll("\\D", "");

        // Extract country code (first two digits) if applicable
        String countryCode = "";
        String mobileNumber = "";

        if (phoneNumber.length() >= 10) {
            mobileNumber = phoneNumber.substring(phoneNumber.length() - 10);
            if (phoneNumber.length() >= 12) {
                countryCode = phoneNumber.substring(0, 2);
            } else if (phoneNumber.length() == 11) {
                countryCode = phoneNumber.substring(0, 1);
            }
        }
        if(countryCode.length()<1)
            countryCode = enteredCountryCode;

        // Set the country code to the cc EditText
        cc.setText(countryCode);
        cc.setSelection(countryCode.length());

        // Set the modified phone number back to the EditText
        phone.setText(mobileNumber);
        phone.setSelection(mobileNumber.length());

        return new Pair<>(countryCode, mobileNumber);
    }

    private boolean isWhatsAppInstalled() {
        PackageManager packageManager = getPackageManager();
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openWhatsApp(String countryCode, String phoneNumber) {
        String whatsappLink = "https://wa.me/" + countryCode + phoneNumber;
        Uri whatsappUri = Uri.parse(whatsappLink);

        // Create a notification
        sendNotification("Starting WhatsApp chat with " + countryCode + "-" + phoneNumber + " in WhatsApp app");

        // Open WhatsApp using Intent
        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, whatsappUri);
        startActivity(whatsappIntent);
    }

    private void openWhatsAppWeb(String countryCode, String phoneNumber) {
        String whatsappWebLink = "https://web.whatsapp.com/send?phone=" + countryCode + phoneNumber;
        Uri whatsappWebUri = Uri.parse(whatsappWebLink);

        // Create a notification
        sendNotification("Starting WhatsApp chat with " + countryCode + "-" + phoneNumber + " in WhatsApp Web");

        // Open WhatsApp web version using Intent
        Intent whatsappWebIntent = new Intent(Intent.ACTION_VIEW, whatsappWebUri);
        startActivity(whatsappWebIntent);
    }

    // Create a Notification Channel
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Default";
            String description = "In this channel notifications are pushed for searched numbers in the app.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id_default", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Show Notification
    private void sendNotification(String contentText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            createNotificationChannel();
        }
        String contentTitle = "WhatsApp Chat Started";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id_default")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }



    private void openContactPage() {
        Uri uri = Uri.parse(getString(R.string.contact_url));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private boolean isValidCountryCode(String countryCode){
        return countryCode.length() > 0;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String numericPhoneNumber = phoneNumber.replaceAll("[^0-9]", "");
        int desiredLength = 10;
        return numericPhoneNumber.length() == desiredLength;
    }

    void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit??")
                .setMessage("Are you sure want to exit the app?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(MainActivity.this, "Exit successful!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> Toast.makeText(MainActivity.this, "Cancel successful!", Toast.LENGTH_SHORT).show())
                .create()
                .show();
    }

    public void alert() {
        new AlertDialog.Builder(this)
                .setTitle("Please enter a valid Mobile Number!!")
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {})
                .create()
                .show();
    }
}
