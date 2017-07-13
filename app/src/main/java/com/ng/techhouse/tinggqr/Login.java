package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.PinEntryLayout;
import com.ng.techhouse.tinggqr.util.Util;
import android.Manifest;
import android.widget.Toast;


public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText etPhoneno,etPassword;
    String phoneno,password;
    Button submitbtn;
    TextView signUpLink;
    ConnectionDetector connectionDetector;
    ResponseCode rc;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    Context mContext;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp < 360) {
            setContentView(R.layout.content_login3603);
        } else {
            setContentView(R.layout.activity_login);
        }

        etPhoneno = (EditText) findViewById(R.id.input_phoneno);
        etPassword = (EditText) findViewById(R.id.input_password);
        submitbtn = (Button) findViewById(R.id.btn_login);
        signUpLink = (TextView) findViewById(R.id.signUp_link);
        connectionDetector = new ConnectionDetector(this);
        submitbtn.setOnClickListener(this);
        signUpLink.setOnClickListener(this);
        rc = new ResponseCode();

        etPassword.setText("");

        checkSession();
        pushnotification();


       AndroidNetworking.initialize(getApplicationContext());

    }

    private void checkPermission(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

    }
    private void validateUserFromServer() {

        M.showLoadingDialog(Login.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"LoginMobile?")
       // AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getlogin())
                .addBodyParameter("Pin", password)
                .addBodyParameter("SourcePhone", phoneno)
                .addBodyParameter("PlatformId", "001")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleResponse(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {
                        } else {
                            M.showToastL(Login.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        System.out.println("Login response handle" + responseCode );

        try{
        switch (responseCode.trim()) {

            case "00":
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                M.setPhoneno(phoneno,Login.this);
                M.setPassword(password,Login.this);
                M.setFullName(responseArray[2].trim(),Login.this);
                M.setBalance(responseArray[1],Login.this);
                M.setEmail(responseArray[4],Login.this);
                M.setPlatformId(responseArray[12].substring(0,3),Login.this);
                startActivity(intent);
                break;
            case "05":
                M.DialogBox(Login.this,rc.getResponseMessage(responseCode.trim()));
                break;
            default:
                M.DialogBox(Login.this,rc.getResponseMessage(responseCode.trim()));
                break;
        }
        } catch (Exception e) {

            }

        }

    @Override
    protected void onStart() {
        etPassword.setText("");
        super.onStart();
    }

    public boolean validate() {
        boolean valid = true;
        phoneno = etPhoneno.getText().toString();
        password = etPassword.getText().toString();
        if (phoneno.isEmpty() ) {
            etPhoneno.setError("enter Phone number");
            valid = false;

        } else if (password.isEmpty() ) {
            etPassword.setError("enter Password");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_login:

               if(validate()) {
                   if(connectionDetector.isConnectingToInternet()){
                        validateUserFromServer();
                    } else{
                       M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
                }
                break;
            case R.id.signUp_link:
                Intent i_signup = new Intent(Login.this, RegisterUser.class);
                startActivity(i_signup);
                break;
            default:
                break;

        }
        

    }

    private void pushnotification(){
        String message ,title;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("message") != null) {
                title = getIntent().getExtras().getString("title");
                message = getIntent().getExtras().getString("message");
                //Log.d("message",getIntent().getExtras().getString("message") );
                M.DialogBox(Login.this,title, message);
            } else {
            }
        }
    }

    private void checkSession() {

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("timeout") != null) {
                M.DialogBox(Login.this,"Alert","Session timeout, please re-login");
            } else {
            }
        }
    }

    @Override
    public void onBackPressed() {
        exitAppDialogBox();
    }
    public void exitAppDialogBox() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Login.this);
        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/MavenPro-Regular.ttf");
        CustomTFSpan customTFSpan = new CustomTFSpan(face);
        SpannableString spannableString = new SpannableString("Quit");
        spannableString.setSpan(customTFSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialogBuilder
                .setTitle(spannableString)
                .setMessage("Are you sure you want to exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        System.exit(0);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setTypeface(face);

    }

    public static SpannableString typeface(Typeface typeface, CharSequence string) {
        SpannableString s = new SpannableString(string);
        s.setSpan(new TypefaceSpan(String.valueOf(typeface)), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
              ///  M.DialogBox(Login.this,Util.getContactName(Login.this,"08036003090"));
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

