package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.settings.CardSetUp;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.ng.techhouse.tinggqr.CardAdapterAirtime.selectedRadioButton;

public class AirtimePaymentOption extends AppCompatActivity {

    TextView tv_amount,tv_mobileno,tv_network,etNairaIcon;
    String amount,mobilenumber,networkname, amountT;
    private Context context = this;
    String MerchTxnRef ;
    String id,extraData,phoneNumber,cardType = null;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    AlertDialog customAlertDialog;
    ConnectionDetector connectionDetector;
    LinearLayout card_layout,addCard;
    ResponseCode rc;
    private ArrayList<CardPojo> cp;
    ListView list;
    static Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtime_payment_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_amount = (TextView) findViewById(R.id.amount);
        tv_mobileno = (TextView) findViewById(R.id.tv_mobile_no);
        tv_network = (TextView) findViewById(R.id.tv_networkname);
        addCard = (LinearLayout) findViewById(R.id.add_card);
        payBtn = (Button) findViewById(R.id.payBtn);
        etNairaIcon = (TextView) findViewById(R.id.nairaicon);
        etNairaIcon.setText("\u20A6");

        card_layout = (LinearLayout) findViewById(R.id.card_layout);
        card_layout.setVisibility(View.GONE);
        rc = new ResponseCode();
        connectionDetector = new ConnectionDetector(this);


        cp = new ArrayList<CardPojo>();

        Intent intent = getIntent();
        if (intent != null) {

            mobilenumber = intent.getStringExtra("phoneno");
            amount =      intent.getStringExtra("amount");
            networkname = intent.getStringExtra("networkname");
            tv_mobileno.setText(mobilenumber);
            tv_network.setText(networkname);
            tv_amount.setText(amount);
        }

        loadStoredCard();
        AndroidNetworking.initialize(getApplicationContext());


        payBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              paymentOptionLogic();
          }
      });

        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCardActivity();
            }
        });

    }
    private void addCardActivity() {

        Intent intent=new Intent(AirtimePaymentOption.this,CardSetUp.class);
        intent.putExtra("phoneno", mobilenumber);
        intent.putExtra("amount", amount);
        intent.putExtra("networkname", networkname);
        intent.putExtra("activity", "AirtimePaymentOption");
        startActivityForResult(intent, 250);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 250) {

            mobilenumber = data.getStringExtra("phoneno");
            amount =      data.getStringExtra("amount");
            networkname = data.getStringExtra("networkname");
            tv_mobileno.setText(mobilenumber);
            tv_network.setText(networkname);
            tv_amount.setText(amount);
            cp.clear();
            loadStoredCard();
        } else if (resultCode == RESULT_CANCELED) {

        }
    }
    private void paymentOptionLogic() {

        if(!CardAdapterAirtime.selectedRadioButton.isEmpty()) {
            for(CardPojo s: selectedRadioButton){
                cardType = s.getCardType();
                extraData = s.getExtraData();
                id =  s.getId();
                phoneNumber = s.getPhoneNumber();
            }
            if(cardType.equalsIgnoreCase("tingg")){
                showPaymentPin();
            } else{
                cardPaymentConfirmation();
            }
        } else {
            M.DialogBox(this,"Select One Payment Option");
        }
    }
    public void cardPaymentConfirmation() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(AirtimePaymentOption.this);
        alertDialogBuilder
                .setMessage("\u20A6"+ amount + " would be debited from your card ending with " + extraData)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        generateCardPayRef();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        Typeface face= Typeface.createFromAsset(getAssets(),"fonts/MavenPro-Regular.ttf");
        textView.setTypeface(face);
    }



    private void loadStoredCard() {
        M.showLoadingDialog(AirtimePaymentOption.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("SourcePhone",M.getPhoneno(AirtimePaymentOption.this))
                .addBodyParameter("Type","9")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){
                            card_layout.setVisibility(View.GONE);
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

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(AirtimePaymentOption.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void displaySavedAccountView(String response) throws JSONException {
        /////cp.clear();
        JSONArray jsonarr = new JSONArray(response);
        if(!response.equals("[]")){
            card_layout.setVisibility(View.VISIBLE);

            CardPojo cpbean = new CardPojo("080", M.getPhoneno(AirtimePaymentOption.this),M.getPhoneno(AirtimePaymentOption.this),"tingg");
            cp.add(cpbean);

            for(int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                CardPojo bean = new CardPojo(jsonobj.getString("Id"), jsonobj.getString("ExtraData"),jsonobj.getString("PhoneNumber"), jsonobj.getString("CardType"));

                cp.add(bean);
            }

            CardAdapterAirtime adapter=new CardAdapterAirtime(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        } else if (response.equals("[]")){
            card_layout.setVisibility(View.VISIBLE);
            CardPojo bean = new CardPojo("080", M.getPhoneno(AirtimePaymentOption.this),M.getPhoneno(AirtimePaymentOption.this),"tingg");
            cp.add(bean);
            CardAdapterAirtime adapter=new CardAdapterAirtime(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        }
    }

    public void showPaymentPin() {

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogLayout = inflater.inflate(R.layout.pin_layout, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(dialogLayout);
        final Button BtnPay = (Button) dialogLayout.findViewById(R.id.submitBtn);
        mOtpOneField = (EditText) dialogLayout.findViewById(R.id.otp_one);
        mOtpTwoField = (EditText) dialogLayout.findViewById(R.id.otp_two);
        mOtpThreeField = (EditText) dialogLayout.findViewById(R.id.otp_three);
        mOtpFourField = (EditText) dialogLayout.findViewById(R.id.otp_four);
        setFocusListener();
        setOnTextChangeListener();
        BtnPay.setText("Pay " + "\u20A6" + amount);

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (hasValidOTP()) {
                    if (connectionDetector.isConnectingToInternet()) {
                        buyAirtime();
                    } else {
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
                }else{
                    M.DialogBox(context,"Incomplete PIN");
                }
            }
        });

        customAlertDialog = builder.create();
        customAlertDialog.show();

    }

    private void setFocusListener() {
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override public void onFocusChange(View v, boolean hasFocus) {
                mCurrentlyFocusedEditText = (EditText) v;
                mCurrentlyFocusedEditText.setSelection(mCurrentlyFocusedEditText.getText().length());
            }
        };
        mOtpOneField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpTwoField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpThreeField.setOnFocusChangeListener(onFocusChangeListener);
        mOtpFourField.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setOnTextChangeListener() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText != mOtpFourField) {
                    mCurrentlyFocusedEditText.focusSearch(View.FOCUS_RIGHT).requestFocus();
                } else if (mCurrentlyFocusedEditText.getText().length() >= 1
                        && mCurrentlyFocusedEditText == mOtpFourField) {
                } else {
                    String currentValue = mCurrentlyFocusedEditText.getText().toString();
                    if (currentValue.length() <= 0 && mCurrentlyFocusedEditText.getSelectionStart() <= 0) {
                        mCurrentlyFocusedEditText.focusSearch(View.FOCUS_LEFT).requestFocus();
                    }
                }
            }
        };
        mOtpOneField.addTextChangedListener(textWatcher);
        mOtpTwoField.addTextChangedListener(textWatcher);
        mOtpThreeField.addTextChangedListener(textWatcher);
        mOtpFourField.addTextChangedListener(textWatcher);
    }
    private String getPIN(){
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(mOtpOneField.getText().toString());
        stringBuilder.append(mOtpTwoField.getText().toString());
        stringBuilder.append(mOtpThreeField.getText().toString());
        stringBuilder.append(mOtpFourField.getText().toString());
        return stringBuilder.toString();
    }

    public boolean hasValidOTP(){
        return getPIN().length()==4;
    }



    private void buyAirtime() {

        M.showLoadingDialog(AirtimePaymentOption.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getPurchase())
                .addBodyParameter("Pin",getPIN())
                .addBodyParameter("SourcePhone",M.getPhoneno(AirtimePaymentOption.this))
                .addBodyParameter("RechargeMobile",mobilenumber)
                .addBodyParameter("Amount",amount.replace(",",""))
                .addBodyParameter("Network",networkname)
                .addBodyParameter("Channel", "7")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleRespone(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(AirtimePaymentOption.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void handleRespone(String response) {

        if (response.trim().equals("SUCCESSFUL")){
            Intent i = new  Intent(getApplicationContext(),Confirmation.class);
            startActivity(i);
            finish();
        } else if(response.trim().equals("INVALID_PIN")){
            M.DialogBox(AirtimePaymentOption.this,rc.getResponseMessage("05"));
        }
        else if(response.trim().equals("INSUFFICIENT_FUND")){
            customAlertDialog.dismiss();
            M.DialogBox(AirtimePaymentOption.this,rc.getResponseMessage("24"));
        } else{
            customAlertDialog.dismiss();
            M.DialogBox(AirtimePaymentOption.this,response);
        }
    }
    public void generateCardPayRef(){
        HashMap pdata = new HashMap();
        pdata.put("SourcePhone",M.getPhoneno(AirtimePaymentOption.this));
        pdata.put("RechargeMobile", mobilenumber);
        pdata.put("Amount", amount.replace(",",""));
        pdata.put("Channel", "2");
        pdata.put("Network", networkname);
        pdata.put("PlatformId",M.getPlatformId(this));
       // pdata.put("PhoneToNotify",M.getPhoneno(AirtimePaymentOption.this));
       // pdata.put("Email",M.getEmail(AirtimePaymentOption.this));
        pdata.put("BillerShortName", "ART");
        ////////////////
        ArrayList list = new ArrayList();
        list.add(pdata);

        Gson gson = new GsonBuilder().create();
        String payerdata = gson.toJson(list);

        M.showLoadingDialog(AirtimePaymentOption.this);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                . writeTimeout(300, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"CardPayGenerateRef?")
                .addBodyParameter("Phone", M.getPhoneno(AirtimePaymentOption.this))
                .addBodyParameter("Amount",amount)
                .addBodyParameter("TransactionTypeId","1" )
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .addBodyParameter("Payload",payerdata)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        handleRespone1(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(AirtimePaymentOption.this,ANError.getErrorDetail());
                        }

                    }});
    }

    private void handleRespone1(String response) {
        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        try{
            switch (responseCode.trim()) {
                case "00":
                    MerchTxnRef  = responseArray[1];
                    paybillviaCard();
                    break;
                default:
                    M.DialogBox(AirtimePaymentOption.this,rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }
    private void paybillviaCard() {

        int amounT = Integer.parseInt(amount.replace(",",""));

        String amountInKobo = String.valueOf(amounT * 100);
        M.showLoadingDialog(AirtimePaymentOption.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getTinggPay()+new EndPoint().getGateWay())
                .addBodyParameter("vpc_MerchTxnRef",MerchTxnRef)
                .addBodyParameter("vpc_Amount",amountInKobo)
                .addBodyParameter("id",id)
                .addBodyParameter("phone", phoneNumber)
                .addBodyParameter("platformId",M.getPlatformId(this))
                .addBodyParameter("action","0")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        M.hideLoadingDialog();
                      //
                        try {
                            cardPaymentResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(AirtimePaymentOption.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void cardPaymentResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if(jsonObject.getString("txnResponseCode").equals("0") && jsonObject.getString("cellResponseCode").equals("00") ){
            Intent i = new  Intent(getApplicationContext(),Confirmation.class);
            startActivity(i);
            finish();
        }else {
            M.DialogBox(this,jsonObject.getString("message"));
        }
    }
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();

    }

}
