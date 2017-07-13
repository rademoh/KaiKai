package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ng.techhouse.tinggqr.barcode.BarcodeCaptureActivity;
import com.ng.techhouse.tinggqr.settings.SettingsAdapter;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

//import in.raveesh.customtype.TextView;

public class SendPayment extends  AppCompatActivity{

    ListView list;
    String decodedString;
    private Barcode barcodeResult;
    private Context context = this;
    private static final int BARCODE_READER_REQUEST_CODE = 2;
    private static final String LOG_TAG = SendPayment.class.getSimpleName();


     Integer [] IMAGEG = {R.drawable.ic_account_wallet, R.drawable.ic_account_balance, R.drawable.ic_qrcode_scan, R.drawable.ic_code_18};
     String[] TITLE = {"Tingg Transfer","Bank Transfer","Pay by QR Code ","Pay by Code"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsAdapter adapter=new SettingsAdapter(this, TITLE, IMAGEG);
        list=(ListView)findViewById(R.id.sendpayment_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                sendPaymentOption(position);
            }
        });

    }

    private void sendPaymentOption(int position) {

        switch (position) {
            case 0 :
                Intent intentP = new Intent(getApplicationContext(), PeerToPeer.class);
                startActivity(intentP);
                break;
            case 1:
                Intent intentT = new Intent(getApplicationContext(), TransferToBank.class);
                startActivity(intentT);
                break;
            case 2:
                startScan();
                break;
            case 3:
                Intent intentM= new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intentM);
                break;
            default:
                throw new IllegalArgumentException("Invalid Selection");
        }

    }

    private void startScan() {
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        intent.putExtra("activity","Send");
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
                        Intent intentBundle = new Intent(SendPayment.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("amount","tinggcard");
                        bundle.putString("phone", barcodeValue);
                        intentBundle.putExtras(bundle);
                        startActivity(intentBundle);
                    } else{
                        try {
                            decodedString = AESCrypt.decrypt(AESHelper.seedValue, barcode.displayValue.trim());
                        } catch (GeneralSecurityException e) {
                            M.DialogBox(SendPayment.this, "Alert", "Invalid Barcode");
                        }
                        try{
                            if (getString(R.string.identifier).equals(decodedString.substring(0, 9))) {
                                Intent intentBundle = new Intent(SendPayment.this, PaymentActivity.class);
                                Bundle bundle = new Bundle();
                                String[] results = decodedString.split("\\|");
                                bundle.putString("phone", results[1]);
                                if (results[2].equals("ID")) {
                                    bundle.putString("amount", "noamount");
                                } else {
                                    bundle.putString("amount", results[2]);
                                }
                                intentBundle.putExtras(bundle);
                                startActivity(intentBundle);
                            }else {
                                M.DialogBox(SendPayment.this, "Alert", "Invalid Tingg Barcode");
                            }
                        }catch (NullPointerException e) {
                            M.DialogBox(SendPayment.this, "Alert", "Invalid Tingg Barcode");
                        }
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
