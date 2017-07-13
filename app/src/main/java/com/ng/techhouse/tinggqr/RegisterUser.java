package com.ng.techhouse.tinggqr;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterUser extends AppCompatActivity {

    EditText etFull_name,etEmail,etPhoneno,etGender,etDob,etOccupation,
    etAddress,etSecretQuestion,etSecretAnswer;
    Button submitBtn;
    String fullname,email,phoneno,gender,dob,occupation,address,secretQuestion,secretAnswer;
    SimpleDateFormat dateFormatter;
    DatePickerDialog fromDatePickerDialog;
    ConnectionDetector connectionDetector;
    ResponseCode rc;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etFull_name = (EditText) findViewById(R.id.input_fullname);
        etEmail = (EditText) findViewById(R.id.input_email);
        etPhoneno = (EditText) findViewById(R.id.input_primaryno);
        etGender = (EditText) findViewById(R.id.input_gender);
        rc = new ResponseCode();
        etDob = (EditText) findViewById(R.id.input_dob);
        etOccupation = (EditText) findViewById(R.id.input_occupation);
        etAddress = (EditText) findViewById(R.id.input_address);
        etSecretQuestion = (EditText) findViewById(R.id.input_secret_question);
        etSecretAnswer = (EditText) findViewById(R.id.input_secret_answer);
        submitBtn = (Button) findViewById(R.id.btnSubmit);

        connectionDetector = new ConnectionDetector(this);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    if(connectionDetector.isConnectingToInternet()) {
                        registernewCustomer();
                    } else{
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
                }
            }
        });
        etGender.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    showDialogListView();
                }
                return true;}
        });
        etDob.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    dateDialogueBox();
                }
                return true;}
        });
        etSecretQuestion.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    showDialogSecurityQuestion();
                }
                return true;}
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

    }

    private String dobformatted(){
        String dobformatted;
        String[] dobArray = dob.split("\\-");
        dobformatted = dobArray[2]+"-"+dobArray[1]+"-"+dobArray[0];
        return dobformatted;
    }

    private void registernewCustomer() {

        M.showLoadingDialog(RegisterUser.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getRegCustomer())
                .addBodyParameter("Pin","5555")
                .addBodyParameter("FullName", fullname)
                .addBodyParameter("Email", email)
                .addBodyParameter("Address", address)
                .addBodyParameter("Phone",phoneno)
                .addBodyParameter("DateOfBirth", dobformatted())
                .addBodyParameter("Gender", gender)
                .addBodyParameter("Occupation",occupation)
                .addBodyParameter("SecQuestion", secretQuestion)
                .addBodyParameter("SecAnswer", secretAnswer)
                .addBodyParameter("PlatformId", "001")
                .addBodyParameter("Channel", "2")
                .addBodyParameter("Token", "3456877")
                .addBodyParameter("RefCode", "0373731817311")
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
                            M.showToastL(RegisterUser.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        System.out.println("Register  response handle" + responseCode );

        try{
            switch (responseCode.trim()) {

                case "00":
                    etFull_name.setText("");
                    etPhoneno.setText("");
                    etEmail.setText("");
                    etGender.setText("");
                    etOccupation.setText("");
                    etDob.setText("");
                    etAddress.setText("");
                    etSecretQuestion.setText("");
                    etSecretAnswer.setText("");
                   M.DialogBox(RegisterUser.this,"Your Pin would be sent to you shortly");
                    break;
                case "05":
                    M.DialogBox(RegisterUser.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(RegisterUser.this,rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }

    }

    public static boolean isValidEmail(String email)
    {
        String expression = "^[\\w\\.]+@([\\w]+\\.)+[A-Z]{2,7}$";
        CharSequence inputString = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.matches())
        {
            return true;
        }
        else{
            return false;
        }
    }
    public boolean validate() {

        boolean valid = true;

        fullname = etFull_name.getText().toString();
        email = etEmail.getText().toString();
        phoneno = etPhoneno.getText().toString();
        gender = etGender.getText().toString();
        dob = etDob.getText().toString();
        occupation = etOccupation.getText().toString();
        address = etAddress.getText().toString();
        secretQuestion = etSecretQuestion.getText().toString();
        secretAnswer = etSecretAnswer.getText().toString();


        if (fullname.isEmpty() ) {
            etFull_name.setError("enter Full Name");
            valid = false;
        } else if (!isValidEmail(email)) {
            etEmail.setError("enter valid Email address");
            valid = false;
        } else if (phoneno.isEmpty() ) {
            etPhoneno.setError("enter Phone number");
            valid = false;
        }else if (gender.isEmpty() ) {
            etGender.setError("Select Gender");
            valid = false;
        }else if (dob.isEmpty() ) {
            etDob.setError("Select Date Of Birth");
            valid = false;
        }else if (occupation.isEmpty() ) {
            etOccupation.setError("enter Occupation");
            valid = false;
        }else if (address.isEmpty() ) {
            etAddress.setError("enter Address");
            valid = false;
        }else if (secretQuestion.isEmpty() ) {
            etSecretQuestion.setError("Select Security Question");
            valid = false;
        }else if (secretAnswer.isEmpty() ) {
            etSecretAnswer.setError("provide answer for the S.Q");
            valid = false;
        }

        return valid;
    }

    private void dateDialogueBox() {
        Calendar newCalendar = Calendar.getInstance();

        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {


            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDob.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog.setTitle("Date Of Birth");
        fromDatePickerDialog.show();
    }
    public void showDialogListView(){
        final CharSequence[] items = {
                "Male", "Female"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Gender");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                etGender.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void showDialogSecurityQuestion(){
        final CharSequence[] items = {
                "Where were you born?", "Your nick name?","Your pet name?","Your mothers maiden name?","Your best pet?","Your favourite food?","Your favourite game?","Your best color?","Your fathers name"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Secret Question");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                etSecretQuestion.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
