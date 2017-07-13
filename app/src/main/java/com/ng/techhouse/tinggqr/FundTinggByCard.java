package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.ng.techhouse.tinggqr.util.CardTypes;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import org.json.JSONException;

public class FundTinggByCard extends AppCompatActivity implements OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener {

    Button fundBtn;
    String amount,amountInKobo;
    ConnectionDetector connectionDetector;
    protected CardForm mCardForm;
    private SupportedCardTypesView mSupportedCardTypesView;
    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY };
    private String cardTypeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fund_tingg_by_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        mSupportedCardTypesView.setVisibility(View.GONE);


        Intent intent = getIntent();
        if (intent != null) {
            amount = intent.getStringExtra("amount");

        }


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


        fundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCardForm.isValid()) {
                    fundTinggNewCard();
                }
            }
        });
    }

    private void fundTinggNewCard() {
        Intent intent = new Intent(this, WebView.class);
        //intent.putExtra("amount",amountInKobo = findPercentage(amount));
        intent.putExtra("amount",amount.replace(",",""));
        intent.putExtra("cardNumber",mCardForm.getCardNumber());
        intent.putExtra("cvv",mCardForm.getCvv());
        intent.putExtra("expiry",mCardForm.getExpirationMonth()+"/"+mCardForm.getExpirationYear().substring(2));
        intent.putExtra("cardType",cardTypeName);
        startActivity(intent);
    }

    @Override
    public void onCardFormSubmit() {

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

        cardTypeName = cardType.toString().substring(0,1).toUpperCase() + cardType.toString().substring(1).toLowerCase();

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

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Funds();
                //onBackPressed();
                return true;
        }
        return(super.onOptionsItemSelected(item));
    }

    @Override
    public void onBackPressed() {
        Funds();
        super.onBackPressed();
    }

    private void Funds() {
        Intent intentP = new Intent(getApplicationContext(), FundTinggOption.class);
        intentP.putExtra("amount",amount);
        startActivity(intentP);
    }
}
