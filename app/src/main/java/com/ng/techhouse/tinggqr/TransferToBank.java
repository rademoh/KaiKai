package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.ganfra.materialspinner.MaterialSpinner;

public class TransferToBank extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    private MaterialSpinner s_banklist, account_type;
    EditText etAccountNo,etAccountName,etPhoneToNotify,etNaration,etToken,etAmount;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    ConnectionDetector connectionDetector;
    String bankCode,responseCode,bankName,EnquiryRefNo,Ben_LastName,Ben_otherName, AccountType,amount,AccountName,AccountNo,token;
    ImageView contactPicker;
    DBhelper controller;
    ResponseCode rc;
    Button getTokenBtn,submitBtn;
    AlertDialog customAlertDialog;
    private Context context = this;
    private static final int RESULT_PICK_CONTACT = 40000;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_to_bank);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        etAccountNo = (EditText) findViewById(R.id.input_account_no);
        etAccountName = (EditText) findViewById(R.id.input_account_name);
        etPhoneToNotify = (EditText) findViewById(R.id.input_mobile_notify);
      //  account_type = (MaterialSpinner) findViewById(R.id.account_type);
      //  etToken = (EditText) findViewById(R.id.input_token);
        etNaration = (EditText) findViewById(R.id.input_naration);
        etAmount = (EditText) findViewById(R.id.input_amount);
       // getTokenBtn = (Button) findViewById(R.id.getTokenBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        connectionDetector = new ConnectionDetector(this);
        s_banklist = (MaterialSpinner) findViewById(R.id.spinner_banklist);
        s_banklist.setOnItemSelectedListener(this);
        controller = new DBhelper(this);
        etAccountNo.addTextChangedListener(watch);
        etAmount.addTextChangedListener(watch1);
        rc = new ResponseCode();

        populateBankName();
       /// populateAccountType();

        contactPicker = (ImageView) findViewById(R.id.contact_picker);
        contactPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });
       /* getTokenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectionDetector.isConnectingToInternet()){
                    getToken();
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }

            }
        });*/
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                   showPaymentPin();
                }
            }
        });
        AndroidNetworking.initialize(TransferToBank.this);
        requestQueue = Volley.newRequestQueue(this);



    }

    public boolean validate() {

        boolean valid = true;

        AccountName = etAccountName.getText().toString();
        AccountNo = etAccountNo.getText().toString();
        bankName = s_banklist.getSelectedItem().toString();
        amount =   etAmount.getText().toString();

        if (bankName.equals("Select Recipient Bank")) {
            s_banklist.setError("Select Recipient Bank");
            valid = false;
        } else if(AccountNo.isEmpty() ) {
            etAccountNo.setError("enter Account No");
            valid = false;
        }else if (amount.isEmpty()) {
            etAmount.setError("enter Amount");
            valid = false;
        }
        return valid;
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
                etPhoneToNotify.setText(phoneNo.replace("+234", "0").trim().replace(" ", ""));
            } else {
                etPhoneToNotify.setText(phoneNo.trim().replace(" ", ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void populateBankName() {

        List<String> bankList = controller.getAllBanks();

        ArrayAdapter<String> bankListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, bankList){

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(
                        getResources().getColorStateList(R.color.hintcolor1)
                );
                return v;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = (TextView) view;
                tv.setPadding(20,20,20,20);
                tv.setTextColor(getResources().getColor(R.color.hintcolor1));
                return tv;
            }};


        s_banklist.setAdapter(bankListAdapter);

    }


    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length() == 10){

                if(connectionDetector.isConnectingToInternet()){
                    fetchAccountName();
                } else{
                    M.showToastL(TransferToBank.this,getString(R.string.no_internet));
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

    private void fetchAccountName() {

        M.showLoadingDialog(TransferToBank.this);
        AndroidNetworking.post(new EndPoint().getHttp()+new EndPoint().getIpSmartWallet()+new EndPoint().getSmartWalet()+"NameEnquiry?")
                .addBodyParameter("AccountNo", etAccountNo.getText().toString())
                .addBodyParameter("BankCode", bankCode)
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
                            M.showToastL(TransferToBank.this,ANError.getErrorDetail());
                        }

                    }
                });
    }
    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];


        try{
            switch (responseCode.trim()) {

                case "00":
                    etAccountName.setText(responseArray[1]);
                    EnquiryRefNo = responseArray[2];
                    Ben_LastName = responseArray[1].trim();
                    Ben_otherName = responseArray[1].trim();
                    /* if(responseArray[1].contains(",")){
                        String[] fullname =responseArray[1].split("\\,");
                        Ben_LastName = fullname[0].trim();
                        Ben_otherName = fullname[1].trim();
                       // M.DialogBox(context, Ben_LastName +" "+Ben_otherName);
                    }else {
                        String[] fullname =responseArray[1].split("\\s+");
                        Ben_LastName = fullname[0].trim();
                        Ben_otherName = fullname[1].trim();
                        //
                       // M.DialogBox(context, Ben_LastName +" "+Ben_otherName);
                    }*/
                    break;
                case "05":
                    M.showToastL(TransferToBank.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.showToastL(TransferToBank.this, rc.getResponseMessage(responseCode.trim()));
            }
        } catch (Exception e) {

        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MaterialSpinner spinner = (MaterialSpinner) parent;
        int ids = spinner.getId();
        switch (ids) {
            case R.id.spinner_banklist:
                bankName = parent.getItemAtPosition(position).toString();
                bankCode = controller.getBankCode(bankName);
                break;

        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void transferToBankVolley(){
        M.showLoadingDialog(TransferToBank.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSendToBank(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        requestQueue.stop();
                        M.hideLoadingDialog();
                        handlebankresponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestQueue.stop();
                        M.hideLoadingDialog();
                        if (error instanceof TimeoutError ) {
                            M.showToastL(context, "Time Out");
                        } else if (error instanceof NoConnectionError){
                            M.showToastL(context, "Connection Error");
                        }
                      //  Log.d("Error.Response", error.toString());
                    }
                })
                  {
            @Override
          public Map<String, String> getParams(){
            Map<String, String> params = new HashMap<>();
                params.put("Pin",getPIN());
                params.put("SourcePhone", M.getPhoneno(TransferToBank.this));
                params.put("Email", "");
                params.put("Ben_Phone",etPhoneToNotify.getText().toString());
                params.put("Ben_LastName",Ben_LastName);
                params.put("Ben_OtherName",Ben_otherName);
                params.put("Ben_Email", "");
                params.put("AccountNumber", etAccountNo.getText().toString());
                params.put("AccountType", "10");
                params.put("BankCode", bankCode);
                params.put("Channel", "5");
                params.put("Amount",amount.replace(",",""));
                params.put("Narration",etNaration.getText().toString());
                params.put("PlatformId",M.getPlatformId(TransferToBank.this));
                params.put("EnquiryRefNo", EnquiryRefNo);
                return params;
         }
        };
        //add request to queue
       ///// requestQueue.add(stringRequest);
       // requestQueue.stop();

    }

    private void transferToBank() {
        M.showLoadingDialog(TransferToBank.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSendToBank())
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("SourcePhone",M.getPhoneno(TransferToBank.this))
                .addBodyParameter("Email","")//
                .addBodyParameter("Ben_Phone",etPhoneToNotify.getText().toString())//
                .addBodyParameter("Ben_LastName",Ben_LastName)
                .addBodyParameter("Ben_OtherName",Ben_otherName)
                .addBodyParameter("Ben_Email","")//
                .addBodyParameter("AccountNumber",etAccountNo.getText().toString())
               // .addBodyParameter("AccountType",getAccountCode(AccountType))//savin=10,d=20
                .addBodyParameter("AccountType","10")
                .addBodyParameter("BankCode",bankCode)//
                .addBodyParameter("Channel", "5")
                .addBodyParameter("Amount",amount.replace(",",""))
                .addBodyParameter("Narration", etNaration.getText().toString())
                .addBodyParameter("PlatformId", M.getPlatformId(this))
                ///.addBodyParameter("Token", etToken.getText().toString())
                .addBodyParameter("EnquiryRefNo", EnquiryRefNo)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handlebankresponse(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(TransferToBank.this,ANError.getErrorDetail());
                        }


                    }});
    }

    private void handlebankresponse(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try {
            switch (responseCode.trim()) {
                case "00":
                    Intent i = new  Intent(getApplicationContext(),Confirmation.class);
                    startActivity(i);
                    break;
                default:
                    M.DialogBox(TransferToBank.this, rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }

    private String getAccountCode(String AccountType){
        String accountCode ="";
        if(AccountType.equals("Savings")){
            accountCode ="10";
        } else if(AccountType.equals("Current")){
            accountCode="20";
        }
        return  accountCode;
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
        BtnPay.setText("Transfer " + "\u20A6" + amount);

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectionDetector.isConnectingToInternet()){
                    transferToBank();
                    //transferToBankVolley();
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
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}
