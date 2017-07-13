package com.ng.techhouse.tinggqr.navactivity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.settings.SettingsAdapter;

public class ContactUs extends AppCompatActivity {

    ListView list;

    String[] listviewTitle = new String[]{ "Address", "Phone Number", "Email Address","Customer Care","IVR","Toll-Free","GLO USSDN","MTN USSDN","Etisalat USSDN"};
    String[] listviewContent = new String[]{ "21, 2nd_Floor, Fanis House, Adeniyi Jones Avenue Ikeja, Lagos Nigeria", "+234 (0)803 323 2059", "info@cellulant.com.ng","0700CELLULANT","01 4405799","0800EWALLET","*300#","*360#","*360#"};

    Integer[] listviewImage = {
            R.drawable.ic_home, R.drawable.ic_phone, R.drawable.ic_email, R.drawable.account,R.drawable.account,R.drawable.ic_phone,R.drawable.ic_phone,R.drawable.ic_phone,R.drawable.ic_phone
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ContactUsAdapter adapter=new ContactUsAdapter(this, listviewTitle,listviewContent, listviewImage);
        list=(ListView)findViewById(R.id.contact_list_view);
        list.setAdapter(adapter);


    }

}
