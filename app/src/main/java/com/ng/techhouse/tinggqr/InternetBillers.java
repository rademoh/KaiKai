package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ng.techhouse.tinggqr.util.Timeout;

public class InternetBillers extends AppCompatActivity {

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_billers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String[] listviewTitle = new String[]{ "ipNX Subscription Payments", "Smile"};

        InternetBillersAdapter adapter = new InternetBillersAdapter(this, listviewTitle);
        list=(ListView)findViewById(R.id.internetbillers_list_view);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(getApplicationContext(), InternetBouquet.class);
                intent.putExtra("BillerName",list.getItemAtPosition(position).toString());
                startActivity(intent);

            }
        });


    }
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
