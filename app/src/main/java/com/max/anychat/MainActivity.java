package com.max.anychat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout main = (ConstraintLayout)findViewById(R.id.main);
        Button chat = (Button)findViewById(R.id.chat);
        Button contact = (Button)findViewById(R.id.contact);
        final EditText phone = (EditText)findViewById(R.id.phone);
        final EditText cc = (EditText)findViewById(R.id.cc);
        cc.setEnabled(false);   //currently can't edit country code, India is default.
        final TextView numtext = (TextView)findViewById(R.id.nutext);
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";


        //Hide keyboard when touched on the screen
        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        //Chat button click function
        chat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String  temp = phone.getText().toString();
                temp = temp.substring(temp.length()-10);
                if(cc.getText().toString().length()==0)
                {
                    cc.setError("Enter valid country code !!");
                }
                else if (temp.length()!=10)
                {
                    phone.setError("Mobile no. length should be 10");
                    numtext.setText("");
                }
                else
                {
                    boolean flag = check(temp);   //check phone no.'s correct format
                    if (flag)
                    {
                        //numtext.setText("+91 - " + phone.getText().toString().substring(0,5) + " - " + phone.getText().toString().substring(5,10));
                        numtext.setText("+91 - " + temp.substring(0,5) + " - " + temp.substring(5,10));
                        phone.setText(temp);
                        String link ="https://wa.me/91" + temp;
                        Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                    else
                    {
                        //Toast.makeText(MainActivity.this, "Invalid Mobile Number !", Toast.LENGTH_SHORT).show();
                        alert();
                        phone.setText("");
                        numtext.setText("");
                    }
                }
            }
        });

        //Contact Button click function
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://smaxiso.github.io/contact.html"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

    //function to check if valid number is entered or not
    public boolean check(String s)
    {
        //s = s.substring(s.length() - 10);
        if (s.charAt(0)=='9' || s.charAt(0)=='8' || s.charAt(0)=='7' || s.charAt(0)=='6')
        {
            return true;
        }
        else
            return false;
    }

    //function to hide keyboard
    void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //exit warning on BackPress
    public void onBackPressed(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm Exit??");
        alertDialogBuilder.setMessage("Are you sure want to exit the app?");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Exit successfull !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Cancel successfull !", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.show();
    }

    //show alert message prompting to enter the correct mobile number format
    public void alert()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please enter valid Mobile Number !!");
        builder.setMessage("Mobile telephone numbering in India start with 9, 8, 7 or 6 which is based on GSM, WCDMA and LTE technologies.");
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {}
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
