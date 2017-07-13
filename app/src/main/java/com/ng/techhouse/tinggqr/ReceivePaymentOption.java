package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ng.techhouse.tinggqr.barcode.BarcodeCaptureActivity;
import com.ng.techhouse.tinggqr.settings.SettingsAdapter;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class ReceivePaymentOption extends AppCompatActivity {

    ListView list;
    String decodedString;
    private Context context = this;
    private static final int BARCODE_READER_REQUEST_CODE = 9;
    private static final String LOG_TAG = SendPayment.class.getSimpleName();
    Integer [] IMAGEG = {R.drawable.ic_code_18, R.drawable.ic_card};
    String[] TITLE = {"Receive Payment By Code","Receive Payment By Tingg Card"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsAdapter adapter=new SettingsAdapter(this, TITLE, IMAGEG);
        list=(ListView)findViewById(R.id.receivepayment_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                 receivePaymentOption(position);
            }
        });

    }
    private void receivePaymentOption(int position) {

        switch (position) {
            case 0 :
                Intent intentP = new Intent(getApplicationContext(), ReceivePayment.class);
                startActivity(intentP);
                break;
            case 1:
                startScan();
                break;
            default:
                throw new IllegalArgumentException("Invalid Selection");
        }

    }
    private void startScan() {
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        intent.putExtra("activity","Receive");
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String barcodeValue = barcode.displayValue.trim();
                    if (barcodeValue.substring(0,4).equals("9988")){
                        Intent intentBundle = new Intent(ReceivePaymentOption.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("amount","receivePayment");
                        bundle.putString("phone", barcodeValue);
                        intentBundle.putExtras(bundle);
                        startActivity(intentBundle);
                    } else{
                        M.DialogBox(ReceivePaymentOption.this, "Alert", "Invalid Tingg Barcode");
                    }
                }else
                    M.DialogBox(context,getString(R.string.no_barcode_captured ));
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}
