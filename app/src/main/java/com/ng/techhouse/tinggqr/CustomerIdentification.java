package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.settings.BankAccountSetUp;
import com.ng.techhouse.tinggqr.settings.CardSetUp;
import com.ng.techhouse.tinggqr.settings.ChangePassword;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerIdentification extends AppCompatActivity {


    Button submitBtn;
    EditText etCustomerID,etAccountName,etAmount;
    TextInputLayout hint;
    ResponseCode rc;
    ConnectionDetector connectionDetector;
    private Context context = this;
    String amount,customerID,BillerId,PaymentItemName,BillerName,PaymentAmount,BillerShortName,PaymentItemCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_identification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        connectionDetector = new ConnectionDetector(this);

        Intent intent = getIntent();
        if (intent != null) {

            BillerId = intent.getStringExtra("BillerId");
            PaymentItemName = intent.getStringExtra("PaymentItemName");
            BillerName = intent.getStringExtra("BillerName");
            BillerShortName = intent.getStringExtra("BillerShortName");
            PaymentItemCode = intent.getStringExtra("PaymentItemCode");
            Integer amount = Integer.valueOf((intent.getStringExtra("PaymentAmount")));
            PaymentAmount = String.valueOf(amount / 100);

        }
        rc = new ResponseCode();

        etAccountName = (EditText) findViewById(R.id.input_account_name);
        hint = (TextInputLayout) findViewById(R.id.hint);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        etCustomerID = (EditText) findViewById(R.id.input_customerid);
        etAmount = (EditText) findViewById(R.id.input_amount);
        etAmount.setVisibility(View.GONE);
        etAmount.addTextChangedListener(watch);
        setHint(BillerId);
        Log.d("BillerId ", BillerId);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                proceed();
            }
        });

        etAccountName.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP) {
                    if (!etCustomerID.getText().toString().isEmpty()) {
                        if (connectionDetector.isConnectingToInternet()) {
                            getBillerEnquiryLogic(BillerId);
                        } else {
                            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                        }

                    } else {
                        etCustomerID.setError("");
                    }
                }
                return true;

            }
        });

    }

    private void getBillerEnquiryLogic(String billerId) {

        if(billerId.equals("403")){
            aedcNameEnquiry();
        } else if (billerId.equals("301")){
            ikejaElecNameEnquiry();
        }else if (billerId.equals("404")){
            jedcNameEnquiry();
        } else if (billerId.equals("992")){
            smileNameEnquiry();
        }else{
            getBillerEnquiry();
        }

    }

    private void smileNameEnquiry() {
        M.showLoadingDialog(CustomerIdentification.this);

        AndroidNetworking.post(new EndPoint().getHttp()+ new EndPoint().getIpDomain()+"AirtimeVendGateWay/ValidateAccount?")
                .addBodyParameter("accountNo",etCustomerID.getText().toString())
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        if(!response.trim().equals("07")){
                            etAccountName.setText(response);
                        }else {
                            M.DialogBox(CustomerIdentification.this,rc.getResponseMessage(response.trim()));
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {
                        M.hideLoadingDialog();
                        if (ANError.getErrorCode() != 0) {
                        } else {
                            M.showToastL(CustomerIdentification.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void jedcNameEnquiry() {
        M.showLoadingDialog(CustomerIdentification.this);
        AndroidNetworking.post(new EndPoint().getHttp()+ new EndPoint().getIpDomain()+"JEDC/CustInfo.do?")
                .addBodyParameter("meterNo",etCustomerID.getText().toString())
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
                            M.showToastL(CustomerIdentification.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void aedcNameEnquiry() {
        M.showLoadingDialog(CustomerIdentification.this);
        AndroidNetworking.post(new EndPoint().getHttp()+ new EndPoint().getIpDomain()+"AirtimeVendGateWay/CustInfo?")
                .addBodyParameter("MeterNo",etCustomerID.getText().toString())
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
                            M.showToastL(CustomerIdentification.this,ANError.getErrorDetail());
                        }

                    }
                });
    }
    private void ikejaElecNameEnquiry(){
        M.showLoadingDialog(CustomerIdentification.this);
        AndroidNetworking.post(new EndPoint().getHttp()+ new EndPoint().getIpDomain()+"AirtimeVendGateWay/NameEnquiry?")
                .addBodyParameter("MeterNo",etCustomerID.getText().toString())
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
                            M.showToastL(CustomerIdentification.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void setHint(String billerId) {

        if(billerId.equals("301")|| billerId.equals("403") || billerId.equals("404")){
            etAmount.setVisibility(View.VISIBLE);
            etCustomerID.setHint("Meter Number");
            hint.setHint("Meter Number");
        } else if (billerId.equals("459")|| billerId.equals("104") || billerId.equals("101")) {
            etCustomerID.setHint("SmartCard Number");
            hint.setHint("SmartCard Number");
        } else if (billerId.equals("110")|| billerId.equals("992") ) {
            etCustomerID.setHint("Customers Account ID");
            hint.setHint("Customers Account ID");
        }
    }

    private boolean checkBiller(){
        boolean valid = false;
        if(BillerId.equals("301")|| BillerId.equals("403") || BillerId.equals("404")){
          valid = true;
        }
        return valid;
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

        customerID = etCustomerID.getText().toString();
        amount =etAmount.getText().toString();


        if (customerID.isEmpty() ) {
            etCustomerID.setError("enter Customer ID");
            valid = false;

        } else if(checkBiller()){
            if(amount.isEmpty()){
                etAmount.setError("enter Amount");
                valid = false;
            }
        }
        return valid;
    }
    private void proceed() {

            if(validate()) {
                if(checkBiller()) {
                    PaymentAmount = amount;
                     }
                Intent intent = new Intent(getApplicationContext(), PaymentOption.class);
                intent.putExtra("BillerId",BillerId);
                intent.putExtra("PaymentItemName", PaymentItemName);
                intent.putExtra("BillerName",  BillerName);
                intent.putExtra("PaymentAmount", PaymentAmount);
                intent.putExtra("BillerShortName", BillerShortName);
                intent.putExtra("PaymentItemCode", PaymentItemCode);
                intent.putExtra("customerID", customerID);
                startActivity(intent);
            }
    }

    private void getBillerEnquiry() {

        M.showLoadingDialog(CustomerIdentification.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getBillerOps())
                .addBodyParameter("BillerId",BillerId)
                .addBodyParameter("CustomerId",etCustomerID.getText().toString())
                .addBodyParameter("Type","validate_customer")
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
                            M.showToastL(CustomerIdentification.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {
        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try{
            switch (responseCode.trim()) {

                case "00":
                    etAccountName.setText(responseArray[1]);
                    break;
                case "05":
                    M.DialogBox(CustomerIdentification.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                case "99":
                    M.DialogBox(CustomerIdentification.this,responseArray[1]);
                    break;
                default:
                    M.DialogBox(CustomerIdentification.this,rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }


}
