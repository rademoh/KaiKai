package com.ng.techhouse.tinggqr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {
    TextView merchant_name,nairacode;
    Button btnPay;
    TextView amount;
    EditText inputted_amount;
    private static final String TAG = "Payment";
    String phoneno, text,amountStr, SourcePhoneNo;
    ConnectionDetector connectionDetector;
    private Context context = this;
    ResponseCode rc;
    AlertDialog customAlertDialog;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);

        AndroidNetworking.initialize(getApplicationContext());

        merchant_name = (TextView) findViewById(R.id.merchantname);
        nairacode = (TextView) findViewById(R.id.nairacode);
        amount = (TextView) findViewById(R.id.amount);
        inputted_amount = (EditText) findViewById(R.id.inputted_amount);
        btnPay = (Button) findViewById(R.id.btnPay);
        rc = new ResponseCode();

        inputted_amount.addTextChangedListener(watch);

        Bundle bundle = getIntent().getExtras();
        phoneno = bundle.getString("phone");
        amountStr = bundle.getString("amount");


        if (amountStr.equals("noamount")){
            amount.setVisibility(View.GONE);
            nameEnquiry();
        } else if(amountStr.equals("tinggcard")){
            amount.setVisibility(View.GONE);
            cardNameEnquiry();
        }else if(amountStr.equals("receivePayment")){
            amount.setVisibility(View.GONE);
            cardNameEnquiry();
        } else {
          inputted_amount.setVisibility(View.GONE);
            nameEnquiry();
        }





btnPay.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        if(inputted_amount.getVisibility() == View.VISIBLE) {
            if(validate()){showPaymentPin();}
        } else {showPaymentPin();}
    }
});
        SourcePhoneNo = M.getPhoneno(this);

    }

    private void cardNameEnquiry() {
        if(connectionDetector.isConnectingToInternet()){
            getCustomerNameByCard(phoneno);
        } else{
            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
        }
    }
    private void getCustomerNameByCard(String phoneno) {
        M.showLoadingDialog(PaymentActivity.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"PanEnquiry?")
                .addBodyParameter("CardNo", phoneno)
                .addBodyParameter("Channel", "2")
                .addBodyParameter("Mode", "1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleResponseCard(response);
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

    private void handleResponseCard(String response) {
        String[] results = response.split("\\|");
      if (response.equalsIgnoreCase("32")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            M.showToastL(this,"Unauthorized Card");
        } else if(results[1].trim().equals("null") && results[2].trim().equals("null") ){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            M.showToastL(this,"Card not linked to any account yet");
        } else if (amountStr.equals("receivePayment")){
              merchant_name.setText(formatFullName(M.getFullName(this)));
              phoneno = M.getPhoneno(this);
              nairacode.setText("\u20A6");
              SourcePhoneNo = results[1];
              amount.setText(amountStr);
           } else {
            merchant_name.setText(formatFullName(results[2]));
            phoneno = results[1];
            nairacode.setText("\u20A6");
            amount.setText(amountStr);
        }
    }

    private void nameEnquiry(){
        if(connectionDetector.isConnectingToInternet()){
            getCustomerName(phoneno);
        } else{
            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
        }
    }

    public boolean validate() {
        String amount;
        boolean valid = true;
        amount = inputted_amount.getText().toString();
        if (amount.isEmpty() ) {
            inputted_amount.setError("enter Amount");
            valid = false;
        }
        return valid;
    }

    private void transferToPeer() {

        String amounT;

        if(inputted_amount.getVisibility() == View.VISIBLE) {
            amounT = inputted_amount.getText().toString().replace(",","");
        } else {
            amounT = amountStr.replace(",","");
        }
        if(amountStr.equals("receivePayment")){
            phoneno = M.getPhoneno(this);
            //SourcePhoneNo =
        }
        M.showLoadingDialog(PaymentActivity.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSendMoney())
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("SourcePhone", SourcePhoneNo)
                .addBodyParameter("ReceiverPhone",phoneno )
                .addBodyParameter("Amount", amounT)
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

                        ResponseMsg(response);
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

    private void ResponseMsg(String response) {

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
                    M.DialogBox(PaymentActivity.this, rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void getCustomerName(String phoneno) {

        M.showLoadingDialog(PaymentActivity.this);

            AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getNameEnquiry())
                    .addBodyParameter("Phone", phoneno)
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
                                Toast.makeText(getApplicationContext(), ANError.getErrorDetail(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
        }

    public String formatFullName(String fullname){
        String output= null;
        String[] responseArray = fullname.split("\\s+");
        StringBuilder sb =new StringBuilder();
        for(int i = 0; i < responseArray.length; i++){
            sb.append(responseArray[i].substring(0,1).toUpperCase() + responseArray[i].substring(1).toLowerCase() +" ");
        }
        output = sb.toString();
        return  output;
    }

    private void handleResponse(String response) {

        String[] results = response.split("\\|");

        merchant_name.setText(formatFullName(results[1]));
        nairacode.setText("\u20A6");
        amount.setText(amountStr);
       // M.showToastL(PaymentActivity.this, amountStr);

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
        if(inputted_amount.getVisibility() == View.VISIBLE) {
            BtnPay.setText("Transfer  " + "\u20A6" + inputted_amount.getText().toString());
        } else {
            BtnPay.setText("Transfer  " + "\u20A6" + amountStr);
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
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

            inputted_amount.removeTextChangedListener(this);

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
                inputted_amount.setText(formattedString);
                inputted_amount.setSelection(inputted_amount.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            inputted_amount.addTextChangedListener(this);

        }
    };


    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}


