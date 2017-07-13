package com.ng.techhouse.tinggqr;

import android.content.Intent;
//import android.icu.text.DecimalFormat;
//import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FundWallet extends AppCompatActivity {


    EditText etAmount;
    Button proceedBtn;
    TextView  tvbalance;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCustomerBalance();

        etAmount = (EditText) findViewById(R.id.input_amount);
        etAmount.addTextChangedListener(watch);
        proceedBtn = (Button) findViewById(R.id.proceedBtn);
        tvbalance = (TextView) findViewById(R.id.tv_balance_amount);



        proceedBtn.setText("Proceed To Add " + "\u20A6");

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()) {
                    Intent intent = new Intent(getApplicationContext(), FundTinggOption.class);
                    intent.putExtra("amount",amount);
                    startActivity(intent);

                }
            }
        });

    }
    public boolean validate() {

        boolean valid = true;

        amount = etAmount.getText().toString();

        if (amount.isEmpty() ) {
            etAmount.setError("enter Amount");
            valid = false;

        } else if (amount.startsWith("0") ) {
            etAmount.setError("invalid Amount");
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
           proceedBtn.setText("Proceed To Add " + "\u20A6" + etAmount.getText().toString() );
        }

        @Override
        public void afterTextChanged(Editable s) {

          //  DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.getDefault());
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
                proceedBtn.setText("Proceed To Add " + "\u20A6" + formattedString );
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            etAmount.addTextChangedListener(this);

        }
    };

    private void getCustomerBalance() {
         M.showLoadingDialog(FundWallet.this);
        AndroidNetworking.post(new EndPoint().getHttp()+new EndPoint().getIpDomain()+new EndPoint().getSmartWalet()+"BalanceEnquiry?")
                .addBodyParameter("SourcePhone", M.getPhoneno(FundWallet.this))
                .addBodyParameter("Channel", "7")
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
                            M.showToastL(FundWallet.this, ANError.getErrorDetail());
                        }

                    }
                });

    }

    private void handleResponse(String response) {
        tvbalance.setText(response);
        M.setBalance(response, FundWallet.this);
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
