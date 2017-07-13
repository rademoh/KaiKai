package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import static com.ng.techhouse.tinggqr.CardAdapterPay1.selectedRadioButton;

public class FundWalletOption extends AppCompatActivity implements OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener {
    private ArrayList<CardPojo> cp;
    ListView list;
    private Context context = this;
    ConnectionDetector connectionDetector;
    TextView tvFundAmount;
    String amount;
    static LinearLayout cardLayout;
    Button fundBtn;
    String id,phoneNumber,cardType = null;
    String extraData;
    String amountInKobo;
    protected CardForm mCardForm;
    private SupportedCardTypesView mSupportedCardTypesView;
    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_wallet_option);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        mSupportedCardTypesView.setVisibility(View.GONE);
        cardLayout = (LinearLayout) findViewById(R.id.card_layout);
        cardLayout.setVisibility(View.GONE);

        fundBtn = (Button) findViewById(R.id.proceedBtn);

        mCardForm = (CardForm) findViewById(R.id.card_form);
        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel(getString(R.string.purchase))
                .setup(this);
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);

        tvFundAmount = (TextView) findViewById(R.id.fund_amount);
        cp = new ArrayList<CardPojo>();

        Intent intent = getIntent();
        if (intent != null) {
            tvFundAmount.setText(intent.getStringExtra("amount"));
            String formattedamount = intent.getStringExtra("amount");
            amount = formattedamount.replace(",","");
        }

        loadStoredCard();

        fundBtn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              fundingLogic();
          }
      });
        AndroidNetworking.initialize(getApplicationContext());


    }

    private void fundingLogic() {
       if(!selectedRadioButton.isEmpty()) {

           for (CardPojo s : selectedRadioButton) {
               cardType = s.getCardType();
               extraData = s.getExtraData();
               id = s.getId();
               phoneNumber = s.getPhoneNumber();
           }

           if (extraData.equalsIgnoreCase("New Card Number")) {
               if (mCardForm.isValid()) {
                   fundTinggNewCard();
               } else {
                   mCardForm.validate();
               }
           } else {
               fundWithSavedCard();
           }
       } else {
           M.DialogBox(this,"Select One Funding Option");
       }
    }

    private String findPercentage(String amount){
        double perc_value = 1.5;
        double numb_value = Double.parseDouble(amount);
        double rslt_value;

        rslt_value= ((perc_value * numb_value)/100);
        rslt_value = rslt_value + numb_value;
        rslt_value = rslt_value * 100;
        int x = (int)rslt_value;

        return String.valueOf(x);
    }

private void fundWithSavedCard(){
    amountInKobo = findPercentage(amount);
    M.showLoadingDialog(FundWalletOption.this);
    AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getTinggPay()+new EndPoint().getGateWay())
            .addBodyParameter("id",id)
            .addBodyParameter("amount",amount)
            .addBodyParameter("vpc_Amount",amountInKobo)
            .addBodyParameter("phone",M.getPhoneno(FundWalletOption.this))
            .addBodyParameter("action","0")
            .addBodyParameter("type","0")
            .addBodyParameter("platformId",M.getPlatformId(this))
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
                        M.showToastL(FundWalletOption.this,ANError.getErrorDetail());
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
    private void fundTinggNewCard() {
            amountInKobo = findPercentage(amount);
        M.showLoadingDialog(FundWalletOption.this);
       AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getTinggPay()+new EndPoint().getGateWay())
                .addBodyParameter("phone",M.getPhoneno(FundWalletOption.this))
                .addBodyParameter("amount",amount)
                .addBodyParameter("vpc_CardNum", TrippleDes.encrypt(mCardForm.getCardNumber()))
                .addBodyParameter("vpc_CardSecurityCode",TrippleDes.encrypt(mCardForm.getCvv()))
                .addBodyParameter("vpc_CardExp",TrippleDes.encrypt(mCardForm.getExpirationMonth()+"/"+mCardForm.getExpirationYear().substring(2)))
                .addBodyParameter("vpc_Amount",amountInKobo)
                .addBodyParameter("type","0")
                .addBodyParameter("platformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        try {
                            handleResponse(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(FundWalletOption.this,ANError.getErrorDetail());
                        }

                    }
                });

    }

    private void handleResponse(String response) throws JSONException {

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
        M.showLoadingDialog(FundWalletOption.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("SourcePhone",M.getPhoneno(FundWalletOption.this))
                .addBodyParameter("Type","9")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()

                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){

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
                            M.showToastL(FundWalletOption.this,ANError.getErrorDetail());
                        }

                    }
                });
    }
    private void displaySavedAccountView(String response) throws JSONException {
        JSONArray jsonarr = new JSONArray(response);
        if(!response.equals("[]")){

            CardPojo beancp = new CardPojo("080", "New Card Number",M.getPhoneno(FundWalletOption.this),"new");
            cp.add(beancp);

            for(int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                CardPojo bean = new CardPojo(jsonobj.getString("Id"), jsonobj.getString("ExtraData"),jsonobj.getString("PhoneNumber"), jsonobj.getString("CardType"));

                cp.add(bean);
            }

            CardAdapterPay1 adapter=new CardAdapterPay1(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);


        } else if (response.equals("[]")){

            CardPojo bean = new CardPojo("080", "New Card Number",M.getPhoneno(FundWalletOption.this),"new");
            cp.add(bean);
            CardAdapterPay1 adapter=new CardAdapterPay1(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);


        }
    }


    @Override
    public void onCardFormSubmit() {

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

    }

    public static void clearForm(ViewGroup group)
    {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                ((EditText)view).setText("");
            }

            if(view instanceof ViewGroup && (((ViewGroup)view).getChildCount() > 0))
                clearForm((ViewGroup)view);
        }
    }
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}
