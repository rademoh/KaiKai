package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.model.BankAccountPojo;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.settings.BankAccountSetUp;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ng.techhouse.tinggqr.AccountAdapterPay.selectedRadioButton;

public class FundTinggBankAccount extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<BankAccountPojo> bankAccountPojo;
    private Context context = this;
    String amount,accountNo, bankCode;
    ListView list;
    LinearLayout addBankNumber;
    ResponseCode rc;
    ConnectionDetector connectionDetector;
    TextView  etFundingAmount, etNairaIcon;
    Button payBtn;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_tingg_bank_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etFundingAmount = (TextView) findViewById(R.id.funding_amount);
        payBtn = (Button) findViewById(R.id.payBtn);
        addBankNumber = (LinearLayout) findViewById(R.id.add_card);
        etNairaIcon = (TextView) findViewById(R.id.nairaicon);
        rc = new ResponseCode();
        connectionDetector = new ConnectionDetector(this);
        etNairaIcon.setText("\u20A6");
        bankAccountPojo =  new ArrayList<BankAccountPojo>();
        Intent intent = getIntent();
        if (intent != null) {
            amount = intent.getStringExtra("amount");
            etFundingAmount.setText(amount);
        }

        payBtn.setOnClickListener(this);
        addBankNumber.setOnClickListener(this);

        retrieveLinkedAccounts();

    }

    private void retrieveLinkedAccounts() {

        M.showLoadingDialog(FundTinggBankAccount.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?")
                .addBodyParameter("PhoneNumber",M.getPhoneno(FundTinggBankAccount.this))
                .addBodyParameter("ProcessType","3")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){
                        }else {
                            try {
                               displaySavedAccountView(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(FundTinggBankAccount.this,ANError.getErrorDetail());
                        }

                    }
                });
    }


    private void displaySavedAccountView(String response) throws JSONException {
        JSONArray jsonarr = new JSONArray(response);
        if (!response.equals("[]")) {

            for (int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                BankAccountPojo bankaccountBean = new BankAccountPojo(jsonobj.getString("AccountNo"), jsonobj.getString("BankCode"));

                bankAccountPojo.add(bankaccountBean);
            }

            AccountAdapterPay adapter = new AccountAdapterPay(this, bankAccountPojo);
            list = (ListView) findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        }else {
            M.showToastL(this, "No Bank Account Linked yet");
           // M.DialogBoxExit(this, "No Bank Account Linked yet "+ '\n'+'\n'+ "Goto Settings->Add Bank Account", amount);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payBtn:
                paymentOptionLogic();
                break;
            case R.id.add_card:
                Intent intent = new Intent(this, BankAccountSetUp.class);
                intent.putExtra("activity","fundbybank");
                startActivity(intent);
                break;
        }
    }

    private void paymentOptionLogic() {
        if(!AccountAdapterPay.selectedRadioButton.isEmpty()) {
            for(BankAccountPojo s: selectedRadioButton){
                accountNo = s.getAccountNo();
                bankCode = s.getBankCode();

            }
            showPaymentPin();

        } else {
            M.DialogBox(this,"Select One Account Option");
        }

    }

    private void showPaymentPin() {
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
        BtnPay.setText("Fund Tingg " + "\u20A6" + amount);

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(connectionDetector.isConnectingToInternet()){
                    if(getPIN().length() < 4) {
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content), "Incomplete Pin");
                    }else{
                        fundTingg();
                        //37418
                    }
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }
            }
        });

        AlertDialog customAlertDialog = builder.create();
        customAlertDialog.show();
    }

    private void fundTingg() {

        M.showLoadingDialog(FundTinggBankAccount.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?")
                .addBodyParameter("PhoneNumber",M.getPhoneno(FundTinggBankAccount.this))
                .addBodyParameter("ProcessType","2")
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("AccountNo",accountNo)
                .addBodyParameter("Amount",amount.replace(",",""))
                .addBodyParameter("Narration","")
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
                            M.showToastL(FundTinggBankAccount.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {
        Log.d("ResponsePay", response);
        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try {
            switch (responseCode.trim()) {
                case M.SUCCESS:
                    Intent i = new  Intent(getApplicationContext(),Confirmation.class);
                    startActivity(i);
                    break;
                default:
                    M.DialogBox(FundTinggBankAccount.this, rc.getResponseMessage(responseCode.trim()));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Funds();
                //onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        Funds();
        super.onBackPressed();
    }

    private void Funds() {
        Intent intentP = new Intent(getApplicationContext(), FundTinggOption.class);
        intentP.putExtra("amount", etFundingAmount.getText().toString());
        startActivity(intentP);
    }

}
