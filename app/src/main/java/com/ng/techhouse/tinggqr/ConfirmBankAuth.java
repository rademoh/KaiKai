package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.settings.BankAccountSetUp;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfirmBankAuth extends AppCompatActivity {

    EditText etPin,etCardNo;
    String param1,param2,remitaRef,type, bankCode, cardNo="";
    LinearLayout cardEntryLayout;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_bank_auth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etPin = (EditText) findViewById(R.id.input_pin);
        etCardNo = (EditText) findViewById(R.id.input_cardno);
        cardEntryLayout = (LinearLayout) findViewById(R.id.cardno_layout);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        Intent intent = getIntent();
        if (intent != null) {
            remitaRef = intent.getStringExtra("remitaRef");
            param1 = intent.getStringExtra("Param1");
            param2 =  intent.getStringExtra("Param2");
            bankCode =  intent.getStringExtra("BankCode");
            type =  intent.getStringExtra("Type");
        }

        if(type.equals("0")){
          cardEntryLayout.setVisibility(View.GONE);
      }


      submitBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(validate()) {
                  authCard();
              }
          }
      });
    }
    public boolean validate() {
        boolean valid = true;
        if (etPin.getText().toString().isEmpty() ) {
            etPin.setError("Enter OTP Pin");
            valid = false;
        }
       if(cardEntryLayout.getVisibility() == View.VISIBLE) {

           cardNo = etCardNo.getText().toString();
           if (etCardNo.getText().toString().isEmpty() ) {
               etCardNo.setError("Enter card last four digits");
               valid = false;
           }
       }
        return valid;
    }
    private void authCard(){

        M.showLoadingDialog(ConfirmBankAuth.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?")
                .addBodyParameter("ProcessType","1")
                .addBodyParameter("Channel","2")
                .addBodyParameter("BankCode", bankCode)
                .addBodyParameter("TransRef", remitaRef)
                .addBodyParameter("OTP", etPin.getText().toString())
                .addBodyParameter("CardNo", cardNo)
                .addBodyParameter("PhoneNumber", M.getPhoneno(this))
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();


                        try {
                            handleResponse(response);
                            System.out.println("RES 1 "+ response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        M.showToastL(ConfirmBankAuth.this,ANError.getErrorDetail());

                    }
                });

    }

    private void handleResponse(String response) throws JSONException {
        String statusCode,statusDesc, message;
        Log.d("con", response);
        if(!response.isEmpty()){
            JSONObject jsonObject = new JSONObject(response);
            statusCode =   jsonObject.getString("StatusCode");
            statusDesc =   jsonObject.getString("StatusDesc");

            if(statusCode.equals("00")){
                etPin.setText("");
                etCardNo.setText("");

                message =    jsonObject.getString("Message");
                M.showToastL(this, message + " Successful");
                Intent intent = new Intent(this, BankAccountSetUp.class);
                startActivity(intent);
              ///  M.DialogBox(this, message);

            } else {
                M.DialogBox(ConfirmBankAuth.this, statusDesc);
            }
        }

    }

}
