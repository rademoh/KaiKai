package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

public class PeerToPeer extends AppCompatActivity {

    EditText etPhoneno, etAmount, etAccountName;
    ConnectionDetector connectionDetector;
    Button submitBtn;
    String amount,phoneno;
    ImageView contactPicker;
    private Context context = this;
    ResponseCode rc;
    AlertDialog customAlertDialog;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    private static final int RESULT_PICK_CONTACT = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_to_peer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etPhoneno = (EditText) findViewById(R.id.input_mobile_no);
        etAmount = (EditText) findViewById(R.id.input_amount);
        etAccountName = (EditText) findViewById(R.id.input_account_name);
        etPhoneno.addTextChangedListener(watch);
        etAmount.addTextChangedListener(watch1);
        rc = new ResponseCode();
        submitBtn = (Button) findViewById(R.id.submitBtn);
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
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vaidate()){
                    showPaymentPin();
                }
            }
        });

    }

    private boolean vaidate() {
        boolean valid = true;

        phoneno = etPhoneno.getText().toString();
        amount = etAmount.getText().toString();

        if (phoneno.isEmpty() ) {
            etPhoneno.setError("enter Recipient Mobile number");
            valid = false;
        } else if (amount.isEmpty() ) {
            etAmount.setError("enter Amount");
            valid = false;
        }
        return valid;
    }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length() == 11){

                if(connectionDetector.isConnectingToInternet()){
                    nameEnquiry();
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    TextWatcher watch1 = new TextWatcher() {
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
            String phoneNo = null;
            String name = null;
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            if ("+234".equals(phoneNo.substring(0, 4))) {
                etPhoneno.setText(phoneNo.replace("+234", "0").trim().replace(" ", ""));
            } else {
                etPhoneno.setText(phoneNo.trim().replace(" ", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nameEnquiry() {
        M.showLoadingDialog(PeerToPeer.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getNameEnquiry())
                .addBodyParameter("Phone", etPhoneno.getText().toString())
                .addBodyParameter("Channel", "2")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
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
                            M.showToastL(PeerToPeer.this, ANError.getErrorDetail());
                        }

                    }
                });

    }

    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try {
            switch (responseCode.trim()) {

                case "00":
                    etAccountName.setText(responseArray[1]);
                    break;
                default:
                    M.DialogBox(PeerToPeer.this, rc.getResponseMessage(responseCode.trim()));
                    etAccountName.setText("");
                    break;
            }
        } catch (Exception e) {

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
                if(connectionDetector.isConnectingToInternet()){
                    transferToPeer();
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }
            }
        });

        customAlertDialog = builder.create();
        customAlertDialog.show();

    }

    private void transferToPeer() {
        M.showLoadingDialog(PeerToPeer.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSendMoney())
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("SourcePhone",M.getPhoneno(PeerToPeer.this))
                .addBodyParameter("ReceiverPhone",phoneno)
                .addBodyParameter("Amount",amount.replace(",",""))
                .addBodyParameter("Channel", "2")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .addBodyParameter("Token", "576353")
                .addBodyParameter("RefCode", "0373731817311")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleRespone1(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {


                        } else {
                            M.showToastL(PeerToPeer.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void handleRespone1(String response) {
        Log.d("peer to peer", response);

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try {
            switch (responseCode.trim()) {
                case "00":
                    Intent i = new  Intent(getApplicationContext(),Confirmation.class);
                    startActivity(i);
                    finish();
                    break;
                default:
                    M.DialogBox(PeerToPeer.this, rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
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
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}