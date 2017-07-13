package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ng.techhouse.tinggqr.settings.SettingsAdapter;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

public class FundTinggOption extends AppCompatActivity {

    ListView list;
    Integer [] IMAGEG = {R.drawable.ic_account_balance, R.drawable.ic_card};
    String[] TITLE = {"Fund Tingg via Bank Account","Fund Tingg via Card"};
    String fundAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_tingg_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFundingAmount();

        SettingsAdapter adapter=new SettingsAdapter(this, TITLE, IMAGEG);
        list=(ListView)findViewById(R.id.fundTingg_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                fundTinggOption(position);

            }
        });
    }

    private void getFundingAmount() {
        Intent intent = getIntent();
        if (intent != null) {
            fundAmount = intent.getStringExtra("amount");
        }
    }

    private void fundTinggOption(int position) {

        switch (position) {
            case 0 :
                Intent intentP = new Intent(getApplicationContext(), FundTinggBankAccount .class);
                intentP.putExtra("amount",fundAmount);
                startActivity(intentP);
                break;
            case 1:
              //  M.DialogBox(this, "Unavailable for now");
                Intent intent = new Intent(getApplicationContext(), FundTinggByCard.class);
                intent.putExtra("amount",fundAmount);
                startActivity(intent);
                break;
            default:
                throw new IllegalArgumentException("Invalid Selection");
        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }


}
