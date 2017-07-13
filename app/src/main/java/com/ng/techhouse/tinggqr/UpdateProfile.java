
package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateProfile extends AppCompatActivity {

    EditText etAddress,etEmail,etOccupation, etGender;
    private String TAG ="Update Profile";
    String responseCode,name,address,occupation,gender,email;
    TextView tvName;
    Button submitBtn;
    private Context context = this;
    ConnectionDetector connectionDetector;
    ResponseCode rc;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    AlertDialog customAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        tvName = (TextView) findViewById(R.id.input_name);
        etAddress = (EditText) findViewById(R.id.input_address);
        etEmail = (EditText) findViewById(R.id.input_email);
        etOccupation = (EditText) findViewById(R.id.input_occupation);
        etGender = (EditText) findViewById(R.id.input_gender);
        connectionDetector = new ConnectionDetector(this);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        rc = new ResponseCode();

        if(connectionDetector.isConnectingToInternet()){
            getProfileDetails();
        } else{
            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
        }


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){
                  // showPaymentPin();
                    if(connectionDetector.isConnectingToInternet()){
                        updateProfile();
                    } else{
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
                }
            }
        });
        etGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showGenderDialog();
            }
        });


    }

    private void showGenderDialog() {
        final CharSequence[] items = {"Male", "Female"};
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

    private boolean validate() {
        boolean valid = true;

        email = etEmail.getText().toString();
        gender = etGender.getText().toString();
        occupation = etOccupation.getText().toString();
        address = etAddress.getText().toString();

        if (!isValidEmail(email) ) {
            etEmail.setError("enter valid Email address");
            valid = false;

        } else if (occupation.isEmpty() ) {
            etOccupation.setError("enter Occupation");
            valid = false;
        }else if (address.isEmpty() ) {
            etAddress.setError("enter Address");
            valid = false;
        }
        return valid;

    }
    public void showPaymentPin() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.pin_layout, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        final Button BtnPay = (Button) dialogLayout.findViewById(R.id.submitBtn);
        mOtpOneField = (EditText) dialogLayout.findViewById(R.id.otp_one);
        mOtpTwoField = (EditText) dialogLayout.findViewById(R.id.otp_two);
        mOtpThreeField = (EditText) dialogLayout.findViewById(R.id.otp_three);
        mOtpFourField = (EditText) dialogLayout.findViewById(R.id.otp_four);
        setFocusListener();
        setOnTextChangeListener();
        BtnPay.setText("Update Profile");

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectionDetector.isConnectingToInternet()){
                    updateProfile();
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }

            }
        });

        customAlertDialog = builder.create();
        customAlertDialog.show();
    }
    private void setFocusListener() {
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                mCurrentlyFocusedEditText = (EditText) v;
                mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length());
            }
        };
        mOtpOneField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpTwoField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpThreeField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpFourField.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setOnTextChangeListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText != mOtpFourField) {
                    mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus();
                } else if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText == mOtpFourField) {
                } else {
                    String currentValue = mCurrentlyFocusedEditText.getText().toString();
                    if (currentValue.length() <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus();
                    }
                }
            }
        };
        mOtpOneField.addTextChangedListener(textWatcher);
        mOtpTwoField.addTextChangedListener(textWatcher);
        mOtpThreeField.addTextChangedListener(textWatcher);
        mOtpFourField.addTextChangedListener(textWatcher);
    }
    private String getPIN(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(mOtpOneField.getText().toString());
        stringBuilder.append(mOtpTwoField.getText().toString());
        stringBuilder.append(mOtpThreeField.getText().toString());
        stringBuilder.append(mOtpFourField.getText().toString());
        return stringBuilder.toString();
    }


    private void updateProfile(){
        M.showLoadingDialog(UpdateProfile.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getProfileNoCheck())
                .addBodyParameter("Phone",M.getPhoneno(UpdateProfile.this))
                .addBodyParameter("FullName", tvName.getText().toString())
                .addBodyParameter("NewEmail", email)
                .addBodyParameter("NewAddress", address)
                .addBodyParameter("NewOccupation", occupation)
                .addBodyParameter("NewGender", gender)
                .addBodyParameter("Channel", "2")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleResponse1(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {


                        } else {

                            Log.d(TAG, "onError errorDetail : " + ANError.getErrorDetail());
                            Toast.makeText(getApplicationContext(), ANError.getErrorDetail(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private void handleResponse1(String response) {
        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        System.out.println("Update Profile response handle" + responseCode );

        try{
            switch (responseCode.trim()) {

                case "00":
                      getProfileDetails();
                    M.DialogBox(UpdateProfile.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                case "05":
                    M.DialogBox(UpdateProfile.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(UpdateProfile.this,rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }

    }

    private void getProfileDetails() {

        M.showLoadingDialog(UpdateProfile.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"NameEnquiry?")
                .addBodyParameter("Phone",M.getPhoneno(UpdateProfile.this))
                .addBodyParameter("Channel", "2")
                .addBodyParameter("PlatformId", M.getPlatformId(this))
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

                            Toast.makeText(getApplicationContext(), ANError.getErrorDetail(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
    private void handleResponse(String response) {
        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode) {
                case "00":
                    tvName.setText(M.formatFullName(responseArray[1]));
                    etAddress.setText(responseArray[4]);
                    etEmail.setText(responseArray[3]);
                    etOccupation.setText(responseArray[5]);
                    etGender.setText(responseArray[6]);
                    break;
                default:

            }
        } catch (Exception e) {

        }

    }
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }


}

