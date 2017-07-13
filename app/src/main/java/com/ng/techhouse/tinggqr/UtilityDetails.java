package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.util.Timeout;

public class UtilityDetails extends AppCompatActivity {

    TextView display;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_details);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        display = (TextView) findViewById(R.id.display);


        Intent intent = getIntent();
        if (intent !=null){
            int amount = Integer.parseInt(intent.getStringExtra("PaymentAmount"));
            double resultamount = amount/100;
            display.setText("1 "+ intent.getStringExtra("BillerId") + "  2  "+intent.getStringExtra("PaymentItemName") + "  3 "+ intent.getStringExtra("BillerName") + "  4 " + resultamount);

        }
         toolbar.setTitle(intent.getStringExtra("BillerName"));
          setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
