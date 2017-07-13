package com.ng.techhouse.tinggqr.settings;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
//import com.google.zxing.integration.android.IntentIntegrator;

import android.Manifest;


import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.ResponseCode;
import com.ng.techhouse.tinggqr.SendPayment;
import com.ng.techhouse.tinggqr.barcode.BarcodeCaptureActivity;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;


public class LinkTinggCard extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 202;
    private static final int BARCODE_READER_REQUEST_CODE = 302 ;
    private static final String LOG_TAG = SendPayment.class.getSimpleName();
    EditText etCardNo;
    Button submitBtn;
    String cardNo;
    ResponseCode rc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_tingg_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etCardNo = (EditText) findViewById(R.id.input_cardno);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        rc = new ResponseCode();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String inputcardNo = etCardNo.getText().toString();
                if (validate(inputcardNo)){
                    linkCard();
                }
            }
        });


    }

    private void linkCard() {
        M.showLoadingDialog(LinkTinggCard.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"NBSPanLink?SourcePhone="+M.getPhoneno(LinkTinggCard.this)+"&PlatformId="+M.getPlatformId(this)+"&CardNo="+etCardNo.getText().toString()+"&Channel=2")
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
                            M.showToastL(LinkTinggCard.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        System.out.println("Link Card Response " + response );

        try{
            switch (responseCode.trim()) {

                case "00":
                    etCardNo.setText("");
                    M.DialogBox(LinkTinggCard.this,rc.getResponseMessage(response.trim()));
                    break;
                case "50":
                    etCardNo.setText("");
                    M.DialogBox(LinkTinggCard.this,rc.getResponseMessage(response.trim()));
                    break;
                case "05":
                    M.DialogBox(LinkTinggCard.this,rc.getResponseMessage(response.trim()));
                    break;
                default:
                    M.DialogBox(LinkTinggCard.this,rc.getResponseMessage(response.trim()));
            }
        } catch (Exception e) {

        }

    }


    public boolean validate(String cardNo) {

        boolean valid = true;
     //   cardNo = etCardNo.getText().toString();
        if (cardNo.isEmpty() ) {
            etCardNo.setError("enter Card Number");
            valid = false;
        }  else if (!cardNo.substring(0,4).equals("9988")) {
            etCardNo.setError("Invalid Tingg Card Format");
            valid = false;
        }else if (cardNo.length() <=15) {
            etCardNo.setError("Card Number must be 16 digits");
            valid = false;
        }
        return valid;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_scancard:
                Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                intent.putExtra("activity","Link");
                startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
               break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String barcodeValue = barcode.displayValue.trim();
                    if (validate(barcodeValue)) {
                        etCardNo.setText(barcodeValue);
                    }
                }else
                    M.DialogBox(this,getString(R.string.no_barcode_captured ));
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }




}
