package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;

public class Main2Activity extends AppCompatActivity {

    EditText encryptedStringToDecode;
    Button verifyBtn;
    String url,decryptedData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        encryptedStringToDecode = (EditText) findViewById(R.id.decode);
        verifyBtn = (Button) findViewById(R.id.verifyBtn);

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    try {
                        decryptedData = AESCrypt.decrypt(AESHelper.seedValue, encryptedStringToDecode.getText().toString());
                    }catch (GeneralSecurityException e){
                        M.DialogBox(Main2Activity.this,"Alert","Invalid Payment Code");
                        //System.out.println("encryi error " + e.toString());
                        //handle error - could be due to incorrect password or tampered encryptedMsg
                    }
                    if(getString(R.string.identifier).equals(decryptedData.substring(0,9))) {
                        Intent intentBundle = new Intent(Main2Activity.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        String[] results = decryptedData.split("\\|");
                        bundle.putString("phone",results[1]);
                        if (results[2].equals("ID")) {
                            bundle.putString("amount", "noamount");
                        } else {
                            bundle.putString("amount", results[2]);

                        }
                        intentBundle.putExtras(bundle);
                        startActivity(intentBundle);

                         }
                      else{
                        M.DialogBox(Main2Activity.this,"Alert","Invalid Payment Code");
                    }}
                }
        });

    }



    public boolean validate() {
        boolean valid = true;
        url = encryptedStringToDecode.getText().toString();
        if (url.isEmpty() ) {
            encryptedStringToDecode.setError("enter Payment Code");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }


}
