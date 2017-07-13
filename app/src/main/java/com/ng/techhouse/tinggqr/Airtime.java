package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;

public class Airtime extends  AppCompatActivity {

    private MaterialSpinner s_networklist;
    EditText etPhoneno,etAmount;
    Button submitBtn;
    ImageView contactPicker;
    String phoneno, amount,networkname;
    ConnectionDetector connectionDetector;
    private Context context = this;
    ResponseCode rc;
    private static final int RESULT_PICK_CONTACT = 60000;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    AlertDialog customAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        s_networklist = (MaterialSpinner) findViewById(R.id.spinner_networklist);
        etPhoneno = (EditText) findViewById(R.id.input_mobile_no);
        etAmount = (EditText) findViewById(R.id.input_amount);
        etAmount.addTextChangedListener(watch);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        rc = new ResponseCode();
        connectionDetector = new ConnectionDetector(this);
        contactPicker = (ImageView) findViewById(R.id.contact_picker);
        contactPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });

        populateNetworkList();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    proceed();
                   // showPaymentPin();
                }
            }
        });
    }

    private void proceed() {

        Intent intent = new Intent(getApplicationContext(), AirtimePaymentOption.class);
        intent.putExtra("phoneno",etPhoneno.getText().toString());
        intent.putExtra("amount", etAmount.getText().toString());
        intent.putExtra("networkname", s_networklist.getSelectedItem().toString());
        startActivity(intent);
    }

    public boolean validate() {

        boolean valid = true;

        phoneno = etPhoneno.getText().toString();
        amount = etAmount.getText().toString();
        networkname = s_networklist.getSelectedItem().toString();


        if (phoneno.isEmpty() ) {
            etPhoneno.setError("enter Phone number");
            valid = false;
        } else if (amount.isEmpty() ) {
            etAmount.setError("enter Amount");
            valid = false;
        }else if (networkname.equalsIgnoreCase("Select Network")) {
            s_networklist.setError("Select Network Operator");
            valid = false;
        }
        return valid;
    }

    private void populateNetworkList() {

        String networkArray[] = {"Glo","MTN","Airtel","Etisalat"};

        ArrayAdapter<String> Adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, networkArray){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                 tv.setPadding(25, 25, 25, 25);
                return tv;
            }};
        s_networklist.setAdapter(Adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {

        }
    }

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            if("+234".equals(phoneNo.substring(0,4))){
                etPhoneno.setText(phoneNo.replace("+234", "0").trim().replace(" ", ""));
            }else{
                etPhoneno.setText(phoneNo.trim().replace(" ", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        BtnPay.setText("Pay " + "\u20A6" + amount);

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasValidOTP()) {
                    if (connectionDetector.isConnectingToInternet()) {
                        buyAirtime();
                    } else {
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
                }else{
                    M.DialogBox(context,"Incomplete PIN");
                }
            }
        });

        customAlertDialog = builder.create();
        customAlertDialog.show();

    }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            etAmount.removeTextChangedListener(this);

            try {
                String originalString = s.toString();

                Long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                etAmount.setText(formattedString);
                etAmount.setSelection(etAmount.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            etAmount.addTextChangedListener(this);
        }
    };

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

    public boolean hasValidOTP(){
        return getPIN().length()==4;
    }



    private void buyAirtime() {

        M.showLoadingDialog(Airtime.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getPurchase())
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("SourcePhone",M.getPhoneno(Airtime.this))
                .addBodyParameter("RechargeMobile",phoneno)
                .addBodyParameter("Amount",amount.replace(",",""))
                .addBodyParameter("Network",networkname)
                .addBodyParameter("Channel", "7")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleRespone(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(Airtime.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void handleRespone(String response) {

        if (response.trim().equals("SUCCESSFUL")){
            Intent i = new  Intent(getApplicationContext(),Confirmation.class);
            startActivity(i);
            finish();
        } else if(response.trim().equals("INVALID_PIN")){
            M.DialogBox(Airtime.this,rc.getResponseMessage("05"));
        }
        else if(response.trim().equals("INSUFFICIENT_FUND")){
            customAlertDialog.dismiss();
            M.DialogBox(Airtime.this,rc.getResponseMessage("24"));
        } else{
            customAlertDialog.dismiss();
            M.DialogBox(Airtime.this,response);
        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
