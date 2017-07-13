package com.ng.techhouse.tinggqr;

//import android.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.ng.techhouse.tinggqr.util.Util;

import java.util.HashMap;
import java.util.Locale;

import fr.ganfra.materialspinner.MaterialSpinner;


import static android.app.Activity.RESULT_OK;

public class TabOne extends Fragment {

    private MaterialSpinner s_networklist;
    EditText etPhoneno,etAmount;
    Button submitBtn;
    ImageView contactPicker;
    String phoneNo, amount,networkname, name = "";
    ConnectionDetector connectionDetector;
    private Context context = getActivity();
    ResponseCode rc;
    private static final int RESULT_PICK_CONTACT = 60000;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    AlertDialog customAlertDialog;
    ArrayAdapter<String> Adapter;
    CheckBox saveCheckBox;
    DBhelper controller;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tabone, container, false);

        s_networklist = (MaterialSpinner) rootView.findViewById(R.id.spinner_networklist);
        etPhoneno = (EditText) rootView.findViewById(R.id.input_mobile_no);
        etAmount = (EditText) rootView.findViewById(R.id.input_amount);
        saveCheckBox = (CheckBox) rootView.findViewById(R.id.saveCheckBox);
        etAmount.addTextChangedListener(watch);
        etPhoneno.addTextChangedListener(watch1);
        submitBtn = (Button) rootView.findViewById(R.id.submitBtn);
        rc = new ResponseCode();
        connectionDetector = new ConnectionDetector(getActivity());
        controller = new DBhelper(getActivity());
        contactPicker = (ImageView) rootView.findViewById(R.id.contact_picker);
        contactPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        });

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if (intent.getStringExtra("beneficiary") != null) {
                phoneNo = intent.getStringExtra("phoneno");
            }
        }

        populateNetworkList();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    if(saveCheckBox.isChecked()){
                        save();
                        proceed();
                    } else{
                        proceed();
                    }
                    // showPaymentPin();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
       etPhoneno.setText(phoneNo);
        super.onStart();
    }

    private void proceed() {

        Intent intent = new Intent(getActivity(), AirtimePaymentOption.class);
        intent.putExtra("phoneno",etPhoneno.getText().toString());
        intent.putExtra("amount", etAmount.getText().toString());
        intent.putExtra("networkname", s_networklist.getSelectedItem().toString());
        startActivity(intent);
    }

    public boolean validate() {

        boolean valid = true;

        phoneNo = etPhoneno.getText().toString();
        amount = etAmount.getText().toString();
        networkname = s_networklist.getSelectedItem().toString();


        if (phoneNo.isEmpty() ) {
            etPhoneno.setError("enter Phone number");
            valid = false;
        } else if (amount.isEmpty() ) {
            etAmount.setError("enter Amount");
            valid = false;
        }else if (networkname.equalsIgnoreCase("Select Network")) {
            s_networklist.setError("Select Network Operator");
            valid = false;
        }
        return valid;
    }

    private void populateNetworkList() {

        String networkArray[] = {"Glo","MTN","Airtel","Etisalat"};

         Adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, networkArray){
             @NonNull
             @Override
             public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                 View v = super.getView(position, convertView, parent);
                 ((TextView) v).setTextColor(
                         getResources().getColorStateList(R.color.hintcolor1)
                 );
                 return v;
             }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setPadding(25, 25, 25, 25);
                tv.setTextColor(getResources().getColor(R.color.hintcolor1));
                return tv;
            }};
        s_networklist.setAdapter(Adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {

        }
    }


    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            //String phoneNo = null ;
          //  String name = null;
            Uri uri = data.getData();
            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int  phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int  nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);

            if("+234".equals(phoneNo.substring(0,4))){
                etPhoneno.setText(phoneNo.replace("+234", "0").trim().replace(" ", ""));
            } else if("234".equals(phoneNo.substring(0,3))){
                etPhoneno.setText(phoneNo.replace("234", "0").trim().replace(" ", ""));
            }
            else{
                etPhoneno.setText(phoneNo.trim().replace(" ", ""));
            }
            phoneNo = etPhoneno.getText().toString();

         //   M.showToastS(getActivity(), name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            etAmount.removeTextChangedListener(this);

            try {
                String originalString = s.toString();

                Long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                etAmount.setText(formattedString);
                etAmount.setSelection(etAmount.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
            etAmount.addTextChangedListener(this);
        }
    };
    TextWatcher watch1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() == 11){
            String networkname = Util.checkNetworkName(etPhoneno.getText().toString().substring(0,4));
                s_networklist.setSelection(Adapter.getPosition(networkname) + 1);

              name =  Util.getContactName(getActivity(),etPhoneno.getText().toString());
              //  M.showToastS(getActivity(), name);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    public void save(){
        HashMap<String, String> queryValues;
        queryValues = new HashMap<String, String>();
        queryValues.put("name", name);
        queryValues.put("phoneno", phoneNo);
        controller.saveBeneficiary(queryValues);
        controller.close();
       // M.showToastS(getActivity(), "Beneficiary Saved Successfully");
    }

}
