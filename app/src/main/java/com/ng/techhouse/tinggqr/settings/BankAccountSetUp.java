package com.ng.techhouse.tinggqr.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ng.techhouse.tinggqr.AccountAdapterPay;
import com.ng.techhouse.tinggqr.ConfirmBankAuth;
import com.ng.techhouse.tinggqr.FundTinggBankAccount;
import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.ResponseCode;
import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.model.BankAccountPojo;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.JsonUtil2;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ganfra.materialspinner.MaterialSpinner;

public class BankAccountSetUp extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    private MaterialSpinner s_banklist;
    EditText etAccountNo,etAccountName;
    TextView tvAccountNo;
    Button submitBtn;
    DBhelper controller;
    String bankCode,responseCode,bankName;
    ConnectionDetector connectionDetector;
    ResponseCode rc;
    ListView list;
    private ArrayList<BankAccountPojo> bankAccountPojo;
    LinearLayout one;
    ImageView deleteBtn;
    List<String> bankList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_set_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        s_banklist = (MaterialSpinner) findViewById(R.id.spinner_banklist);
        s_banklist.setOnItemSelectedListener(this);
        etAccountNo = (EditText) findViewById(R.id.input_account_no);
        etAccountName = (EditText) findViewById(R.id.input_account_name);
        bankList = new ArrayList<>();
      //  tvAccountNo= (TextView) findViewById(R.id.input_accountno);
        one = (LinearLayout) findViewById(R.id.card_layout);
       // deleteBtn = (ImageView) findViewById(R.id.deleteBtn);
        bankAccountPojo =  new ArrayList<>();
        submitBtn = (Button) findViewById(R.id.submitBtn);
        etAccountNo.addTextChangedListener(watch);
        controller = new DBhelper(this);
        rc = new ResponseCode();
        AndroidNetworking.initialize(getApplicationContext());

        //loadStoredAccount();
        fetchBank();
        populateBankName();
        one.setVisibility(View.GONE);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(validate()) {
                    SaveAccountDetails();
                }
            }
        });

        /*deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConfirmDialogBox();
            }
        });*/

    }
    public boolean validate() {
        boolean valid = true;
        if (etAccountNo.getText().toString().isEmpty() ) {
            etAccountNo.setError("");
            valid = false;

        } else if (etAccountName.getText().toString().isEmpty() ) {
            etAccountName.setError("");
            valid = false;
        }
        return valid;
    }

    private void deleteAccount() {

        M.showLoadingDialog(BankAccountSetUp.this);

        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("Type","7")
                .addBodyParameter("SourcePhone",M.getPhoneno(BankAccountSetUp.this))
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        try {
                            updateView(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(BankAccountSetUp.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void updateView(String response)throws JSONException {

        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode.trim()) {
                case "00":
                    one.setVisibility(View.GONE);
                    break;
                case "05":
                    M.DialogBox(BankAccountSetUp.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(BankAccountSetUp.this, rc.getResponseMessage(responseCode.trim()));
            }
        } catch (Exception e) {

        }
    }

    public void ConfirmDialogBox(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage("Delete Bank Account")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteAccount();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void populateBankName() {

       // List<String> bankList = controller.getAllBanks();

        ArrayAdapter<String> bankListAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, bankList){
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
            tv.setPadding(25,25,25,25);
            tv.setTextColor(getResources().getColor(R.color.hintcolor1));
            return tv;
        }};
        s_banklist.setAdapter(bankListAdapter);

        }

    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(s.length() == 10){

                if(connectionDetector.isConnectingToInternet()){
                    fetchAccountName();
                } else{
                    M.showToastL(BankAccountSetUp.this,getString(R.string.no_internet));
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void SaveAccountDetails(){
        M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?")
                .addBodyParameter("ProcessType","0")
                .addBodyParameter("Channel","2")
                .addBodyParameter("AccountNo",etAccountNo.getText().toString())
                .addBodyParameter("BankCode", bankCode)
                .addBodyParameter("Name", etAccountName.getText().toString())
                .addBodyParameter("Email", M.getEmail(BankAccountSetUp.this))
                .addBodyParameter("Phone",M.getPhoneno(BankAccountSetUp.this))
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        try {
                            handleResponse1(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        M.showToastL(BankAccountSetUp.this,ANError.getErrorDetail());

                    }
                });

    }
    private void SaveAccountDetails1(){
        M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .addBodyParameter("Type","5")
                .addBodyParameter("AccountNo", etAccountNo.getText().toString())
                .addBodyParameter("BankName", bankName)
                .addBodyParameter("BankCode", bankCode)
                .addBodyParameter("Name", etAccountName.getText().toString())
                .addBodyParameter("SourcePhone",M.getPhoneno(BankAccountSetUp.this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                      //  handleResponse1(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                            M.showToastL(BankAccountSetUp.this,ANError.getErrorDetail());

                    }
                });
    }

    private void handleResponse1(String response) throws JSONException {
        String statusCode,statusDesc,remitaRef, Param1,Param2="",Type;
        Log.d("RSR ", response);
        if(!response.isEmpty()) {
            JSONObject jsonObject = new JSONObject(response);
            statusCode = jsonObject.getString("StatusCode");
            statusDesc = jsonObject.getString("StatusDesc");
            if (statusCode.equals("00")) {

                if (jsonObject.has("Param2")) {
                    remitaRef = jsonObject.getString("RemitaRef");
                    Param1 = jsonObject.getString("Param1");
                    Param2 = jsonObject.getString("Param2");
                    Type = "1";

                } else {
                    remitaRef = jsonObject.getString("RemitaRef");
                    Param1 = jsonObject.getString("Param1");
                    Type = "0";
                }
                Intent intent = new Intent(this, ConfirmBankAuth.class);
                intent.putExtra("remitaRef", remitaRef);
                intent.putExtra("Param1", Param1);
                intent.putExtra("Param2", Param2);
                intent.putExtra("BankCode", bankCode);
                intent.putExtra("Type", Type);
                startActivity(intent);

            } else {
                M.DialogBox(BankAccountSetUp.this, statusDesc + " .....Please Try Again");
            }

        }

       /* String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode.trim()) {
                case "00":
                    s_banklist.setSelection(0);
                    etAccountNo.setText("");
                    etAccountName.setText("");
                    loadStoredAccount();
                    break;
                case "05":
                    M.DialogBox(BankAccountSetUp.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(BankAccountSetUp.this, rc.getResponseMessage(responseCode.trim()));
            }
        } catch (Exception e) {

        }*/
    }

    private void loadStoredAccount() {
        M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?")
                .addBodyParameter("PhoneNumber",M.getPhoneno(BankAccountSetUp.this))
                .addBodyParameter("ProcessType","3")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){
                            one.setVisibility(View.GONE);
                        }else {
                            try {
                                displaySavedAccountView(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                    }
                });
       /* M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("Type","6")
                .addBodyParameter("SourcePhone",M.getPhoneno(BankAccountSetUp.this))
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){
                            one.setVisibility(View.GONE);
                        }else {
                            try {
                                displaySavedAccountView(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                    }
                });*/
    }

    private void displaySavedAccountView(String response) throws JSONException {
        JSONArray jsonarr = new JSONArray(response);
        if (!response.equals("[]")) {
            one.setVisibility(View.VISIBLE);
            for (int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                BankAccountPojo bankaccountBean = new BankAccountPojo(jsonobj.getString("AccountNo"), jsonobj.getString("BankCode"));

                bankAccountPojo.add(bankaccountBean);
            }
            AccountAdapterPay adapter = new AccountAdapterPay(this, bankAccountPojo);
            list = (ListView) findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        }else {

            M.showToastS(this, "No Bank Account Linked yet");
        }
    }

    private void fetchAccountName() {

        M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttp()+new EndPoint().getIpSmartWallet()+new EndPoint().getSmartWalet()+"NameEnquiry?")
                .addBodyParameter("AccountNo", etAccountNo.getText().toString())
                .addBodyParameter("BankCode", bankCode)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleResponse(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                            if (ANError.getErrorCode() == 404) {
                                M.showToastL(BankAccountSetUp.this,getString(R.string.requested_resource_notfound));
                            } else if (ANError.getErrorCode() == 500) {
                                M.showToastL(BankAccountSetUp.this,getString(R.string.error_at_server));
                            }

                        } else {
                            M.showToastL(BankAccountSetUp.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {

        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode) {

                case "00":

                    etAccountName.setText(responseArray[1] );

                    break;
                case "05":
                    M.DialogBox(BankAccountSetUp.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(BankAccountSetUp.this, rc.getResponseMessage(responseCode.trim()));
            }
        } catch (Exception e) {

        }

    }
//2020291905

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MaterialSpinner spinner = (MaterialSpinner) parent;
        int ids = spinner.getId();
        switch (ids) {
            case R.id.spinner_banklist:
                bankName = parent.getItemAtPosition(position).toString();
                bankCode = controller.getBankCode(bankName.trim());
               // Log.d("BankName", bankName);
               // Log.d("BankCode ", bankCode);
                break;
        }


    }

    private void fetchBank() {

        M.showLoadingDialog(BankAccountSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"TinggMyAccount?ProcessType=4")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        try {
                            populateBankResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                            if (ANError.getErrorCode() == 404) {
                                M.showToastL(BankAccountSetUp.this,getString(R.string.requested_resource_notfound));
                            } else if (ANError.getErrorCode() == 500) {
                                M.showToastL(BankAccountSetUp.this,getString(R.string.error_at_server));
                            }

                        } else {
                            M.showToastL(BankAccountSetUp.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void populateBankResponse(String response) throws JSONException {
       JSONObject jsonObject = new JSONObject(response);
        Iterator<String> keys = jsonObject.keys();
        while(keys.hasNext() ){
            String key = (String)keys.next();
            bankList.add(key);
        }
        loadStoredAccount();
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}
