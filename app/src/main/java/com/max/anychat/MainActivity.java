package com.max.anychat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        final TextView text = (TextView)findViewById(R.id.about);


        //Hide keyboard when touched on the screen
        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(v);
                return false;
            }
        });

        //Chat button click function
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cc.getText().toString().length()!=2)
                {
                    cc.setError("Enter valid country code !!");
                }
                else if (phone.getText().toString().length()!=10)
                {
                    phone.setError("Enter valid Mobile Number !!");
                }
                else {
                    String link = "https://wa.me/" + cc.getText().toString() + phone.getText().toString();
                    //text.setText(link);
                    Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        //Contact Button click function
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://smaxiso.github.io"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

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
        alertDialogBuilder.setCancelable(false);
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
}
