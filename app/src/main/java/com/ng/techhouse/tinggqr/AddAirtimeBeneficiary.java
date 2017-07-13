package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.util.M;

import java.util.HashMap;

public class AddAirtimeBeneficiary extends AppCompatActivity {

    EditText etName,etPhone;
    String name,phoneno;
    DBhelper controller;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_airtime_beneficiary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        controller = new DBhelper(this);

        etName = (EditText) findViewById(R.id.input_name);
        etPhone = (EditText) findViewById(R.id.input_phoneno);
        submitBtn = (Button) findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()){
                    save();
                }
            }
        });

    }
    public boolean validate() {

        boolean valid = true;

        name = etName.getText().toString();
        phoneno = etPhone.getText().toString();

        if (phoneno.isEmpty() ) {
            etPhone.setError("enter Phone number");
            valid = false;
        } else if (name.isEmpty() ) {
            etName.setError("enter Name");
            valid = false;
        }
        return valid;
    }

public void save(){
    HashMap<String, String> queryValues;
    queryValues = new HashMap<String, String>();
    queryValues.put("name", name);
    queryValues.put("phoneno", phoneno);
    controller.saveBeneficiary(queryValues);
    controller.close();
    Intent i = new Intent(AddAirtimeBeneficiary.this,Airtime1.class);
    startActivity(i);
    M.showToastS(getApplicationContext(), "Beneficiary Saved Successfully");
}


}
