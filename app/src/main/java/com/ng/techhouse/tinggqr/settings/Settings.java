package com.ng.techhouse.tinggqr.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ng.techhouse.tinggqr.Login;
import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.UpdateProfile;
import com.ng.techhouse.tinggqr.util.Timeout;


public class Settings extends AppCompatActivity {

    ListView list;

    String[] listviewTitle = new String[]{ "Change Pin","Link Tingg Card", "Add Bank Account", "Add Card","Update Profile"};

    Integer[] listviewImage = {
            R.drawable.lock,R.drawable.ic_link, R.drawable.ic_account_balance, R.drawable.ic_card, R.drawable.account
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SettingsAdapter adapter=new SettingsAdapter(this, listviewTitle, listviewImage);
        list=(ListView)findViewById(R.id.settings_list_view);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
              //  String Slecteditem= listviewTitle[+position];
                switchSettingsActivity(position);
               // Toast.makeText(getApplicationContext(), Slecteditem +" " + position, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void switchSettingsActivity(int position) {

        switch (position) {
            case 0 :
                Intent intentP = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intentP);
                break;
            case 1:
                Intent intentL = new Intent(getApplicationContext(), LinkTinggCard.class);
                startActivity(intentL);
                break;
            case 2:
                Intent intentB = new Intent(getApplicationContext(), BankAccountSetUp.class);
                startActivity(intentB);
                break;
            case 3:
                Intent intentC = new Intent(getApplicationContext(), CardSetUp.class);
                startActivity(intentC);
                break;
            case 4:
                Intent intentA = new Intent(getApplicationContext(), UpdateProfile.class);
                startActivity(intentA);
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


