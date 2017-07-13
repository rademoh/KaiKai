package com.ng.techhouse.tinggqr;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.settings.CardSetUp;
import com.ng.techhouse.tinggqr.util.AppConstant;
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

import static com.ng.techhouse.tinggqr.CardAdapterPay.selectedRadioButton;


public class PaymentOption extends AppCompatActivity implements View.OnClickListener {

    TextView etProductname, etProductamount, etNairaIcon;
    static Button payBtn;
    private Context context = this;
    ConnectionDetector connectionDetector;
    String resultamount,MerchTxnRef ;
    String id,extraData,phoneNumber,cardType = null;
    LinearLayout card_layout, addCard;
    ResponseCode rc;
    private ArrayList<CardPojo> cp;
    ListView list;
    protected EditText mOtpOneField, mOtpTwoField, mOtpThreeField, mOtpFourField,mCurrentlyFocusedEditText;
    String customerID,BillerShortName,PaymentItemCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etProductname = (TextView) findViewById(R.id.product_name);
        etProductamount = (TextView) findViewById(R.id.product_amount);
        addCard = (LinearLayout) findViewById(R.id.add_card);
        etNairaIcon = (TextView) findViewById(R.id.nairaicon);
        etNairaIcon.setText("\u20A6");
        card_layout = (LinearLayout) findViewById(R.id.card_layout);
        card_layout.setVisibility(View.GONE);
        rc = new ResponseCode();
        connectionDetector = new ConnectionDetector(this);
        payBtn = (Button) findViewById(R.id.payBtn);

        cp = new ArrayList<CardPojo>();

        payBtn.setOnClickListener(this);
        addCard.setOnClickListener(this);


        Intent intent = getIntent();
        if (intent != null) {

            etProductname.setText(intent.getStringExtra("PaymentItemName"));
            etProductamount.setText(intent.getStringExtra("PaymentAmount"));
            resultamount = intent.getStringExtra("PaymentAmount");
            customerID = intent.getStringExtra("customerID");
            PaymentItemCode = intent.getStringExtra("PaymentItemCode");
            BillerShortName =intent.getStringExtra("BillerShortName");
        }

       loadStoredCard();
        AndroidNetworking.initialize(getApplicationContext());

    }

private void paybillviaTingg(){

    M.showLoadingDialog(PaymentOption.this);
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            . writeTimeout(240, TimeUnit.SECONDS)
            .build();
    AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"PayBill?")
            .addBodyParameter("Pin",getPIN())
            .addBodyParameter("SourcePhone",M.getPhoneno(PaymentOption.this))
            .addBodyParameter("customerId",customerID )
            .addBodyParameter("BillerShortName", BillerShortName)
            .addBodyParameter("PaymentItem", PaymentItemCode)
            .addBodyParameter("Amount", String.valueOf(resultamount).replace(",",""))
            .addBodyParameter("Channel", "2")
            .addBodyParameter("PlatformId",M.getPlatformId(this))
            .addBodyParameter("Token", "576353")
            .addBodyParameter("RefCode", "0373731817311")
            .setPriority(Priority.HIGH)
            .setOkHttpClient(okHttpClient)
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
                        M.showToastL(PaymentOption.this,ANError.getErrorDetail());
                    }

                }});
}

    private void handleRespone(String response) {

        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];

        try {
            switch (responseCode.trim()) {
                case M.SUCCESS:
                    Intent i = new  Intent(getApplicationContext(),Confirmation.class);
                    startActivity(i);
                    break;
                default:
                    M.DialogBox(PaymentOption.this, rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payBtn:
                paymentOptionLogic();
                break;
            case R.id.add_card:
                addCardActivity();
                break;
            default:
                break;

        }
    }

    private void addCardActivity() {

        Intent intent=new Intent(PaymentOption.this,CardSetUp.class);
        intent.putExtra("PaymentItemName", etProductname.getText().toString());
        intent.putExtra("PaymentAmount", resultamount);
        intent.putExtra("BillerShortName", BillerShortName);
        intent.putExtra("PaymentItemCode", PaymentItemCode);
        intent.putExtra("customerID", customerID);
        intent.putExtra("activity", "paymentOption");
        startActivityForResult(intent, 200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 200) {

                etProductname.setText(data.getStringExtra("PaymentItemName"));
                resultamount = data.getStringExtra("PaymentAmount");
                etProductamount.setText(resultamount);
                customerID = data.getStringExtra("customerID");
                PaymentItemCode = data.getStringExtra("PaymentItemCode");
                BillerShortName = data.getStringExtra("BillerShortName");
                cp.clear();

                loadStoredCard();
            } else if (resultCode == RESULT_CANCELED) {

            }
    }

    private void paymentOptionLogic() {
        if(!CardAdapterPay.selectedRadioButton.isEmpty()) {
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
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(PaymentOption.this);
        alertDialogBuilder
                .setMessage("\u20A6"+ resultamount + " would be debited from your card ending with " + extraData)
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
        BtnPay.setText("Pay " + "\u20A6" + resultamount);

        BtnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    if(connectionDetector.isConnectingToInternet()){
                        paybillviaTingg();
                    } else{
                        M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                    }
            }
        });

        AlertDialog customAlertDialog = builder.create();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

/*
    private String generatePayLoad(){
        String payerdata = null;

        HashMap pdata = new HashMap();
        pdata.put("SourcePhone", M.getPhoneno(PaymentOption.this));
        pdata.put("Amount", resultamount);
        pdata.put("Channel", "1");
        pdata.put("PaymentItem",PaymentItemCode);
        pdata.put("PhoneToNotify", "");
        pdata.put("BillerShortName", BillerShortName);
        pdata.put("customerId", customerID);
        pdata.put("Email","");

        ArrayList list = new ArrayList();
        list.add(pdata);
        Gson gson = new GsonBuilder().create();
       return  payerdata = gson.toJson(list);
    }*/
public void generateCardPayRef(){

    HashMap pdata = new HashMap();
    pdata.put("SourcePhone", M.getPhoneno(PaymentOption.this));
    pdata.put("Amount", resultamount.replace(",",""));
    pdata.put("Channel", "2");
    pdata.put("TransactionTypeId", "5");
    pdata.put("PaymentItem",PaymentItemCode);
    pdata.put("PhoneToNotify",M.getPhoneno(PaymentOption.this));
    pdata.put("BillerShortName", BillerShortName);
    pdata.put("customerId", customerID);
    pdata.put("Email",M.getEmail(PaymentOption.this));

    ArrayList list = new ArrayList();
    list.add(pdata);

    Gson gson = new GsonBuilder().create();
    String payerdata = gson.toJson(list);

    M.showLoadingDialog(PaymentOption.this);
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            . writeTimeout(240, TimeUnit.SECONDS)
            .build();
    AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"CardPayGenerateRef?")
            .addBodyParameter("Phone", M.getPhoneno(PaymentOption.this))
            .addBodyParameter("Amount",resultamount)
            .addBodyParameter("TransactionTypeId","5" )
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
                        M.showToastL(PaymentOption.this,ANError.getErrorDetail());
                    }

                }});
}

    private void handleRespone1(String response) {
        String[] responseArray = response.split("\\|");
        String responseCode = responseArray[0];
        try{
            switch (responseCode.trim()) {
                case M.SUCCESS:
                    MerchTxnRef  = responseArray[1];
                    paybillviaCard();
                    break;
                default:
                    M.DialogBox(PaymentOption.this,rc.getResponseMessage(responseCode.trim()));
                    break;
            }
        } catch (Exception e) {

        }
    }

    private void paybillviaCard() {

        int amount = Integer.parseInt(resultamount.replace(",",""));
       String amountInKobo = String.valueOf(amount * 100);
        M.showLoadingDialog(PaymentOption.this);
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
                            M.showToastL(PaymentOption.this,ANError.getErrorDetail());
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

    private void loadStoredCard() {
        M.showLoadingDialog(PaymentOption.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("SourcePhone",M.getPhoneno(PaymentOption.this))
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
                            M.showToastL(PaymentOption.this,ANError.getErrorDetail());
                        }

                    }
                });
    }
    private void displaySavedAccountView(String response) throws JSONException {
        JSONArray jsonarr = new JSONArray(response);
        if(!response.equals("[]")){
            card_layout.setVisibility(View.VISIBLE);

            CardPojo cpbean = new CardPojo("080", M.getPhoneno(PaymentOption.this),M.getPhoneno(PaymentOption.this),"tingg");
            cp.add(cpbean);

            for(int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                CardPojo bean = new CardPojo(jsonobj.getString("Id"), jsonobj.getString("ExtraData"),jsonobj.getString("PhoneNumber"), jsonobj.getString("CardType"));

                cp.add(bean);
            }

            CardAdapterPay adapter=new CardAdapterPay(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        } else if (response.equals("[]")){
            card_layout.setVisibility(View.VISIBLE);
            CardPojo bean = new CardPojo("080", M.getPhoneno(PaymentOption.this),M.getPhoneno(PaymentOption.this),"tingg");
            cp.add(bean);
            CardAdapterPay adapter=new CardAdapterPay(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);

        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }


}


