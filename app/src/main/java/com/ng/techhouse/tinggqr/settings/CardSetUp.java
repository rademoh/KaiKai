package com.ng.techhouse.tinggqr.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.ng.techhouse.tinggqr.AESHelper;
import com.ng.techhouse.tinggqr.CardAdapter;
import com.ng.techhouse.tinggqr.ElectricityAdapter;
import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.ResponseCode;
import com.ng.techhouse.tinggqr.TrippleDes;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.model.RechargePoJo;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.CardTypes;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

//import io.card.payment.CardIOActivity;
//import io.card.payment.CreditCard;

public class CardSetUp extends AppCompatActivity implements OnCardFormSubmitListener,
        CardEditText.OnCardTypeChangedListener {


    private static final int MY_SCAN_REQUEST_CODE = 400;
    protected CardForm mCardForm;
    private Button submitBtn;
    public static LinearLayout two;
    TextView etExtraData;
    ImageView ivCardImage;
    String responseCode;
    String callingActivity = null;
    String customerID,BillerShortName,PaymentItemCode, amount, productName;
    String phoneno, network;
    ResponseCode rc;
    private Context context = this;
    private ArrayList<CardPojo> cp;
    ListView list;
    private SupportedCardTypesView mSupportedCardTypesView;
    private static final CardType[] SUPPORTED_CARD_TYPES = { CardType.VISA, CardType.MASTERCARD, CardType.DISCOVER,
            CardType.AMEX, CardType.DINERS_CLUB, CardType.JCB, CardType.MAESTRO, CardType.UNIONPAY };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_set_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mSupportedCardTypesView = (SupportedCardTypesView) findViewById(R.id.supported_card_types);
        mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        mSupportedCardTypesView.setVisibility(View.GONE);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        etExtraData = (TextView) findViewById(R.id.extra_data);
        ivCardImage = (ImageView) findViewById(R.id.cardimage);

        rc = new ResponseCode();

        cp = new ArrayList<CardPojo>();

        two = (LinearLayout) findViewById(R.id.two);
        two.setVisibility(View.GONE);
        loadStoredCard();

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

        // Warning: this is for development purposes only and should never be done outside of this example app.
        // Failure to set FLAG_SECURE exposes your app to screenshots allowing other apps to steal card information.
        //getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCardForm.isValid()) {
                    saveCardDetails();
                } else {
                    mCardForm.validate();
                   /// Toast.makeText(CardSetUp.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("activity") != null) {
                if (intent.getStringExtra("activity").equals("paymentOption")) {
                    productName = intent.getStringExtra("PaymentItemName");
                    amount = intent.getStringExtra("PaymentAmount");
                    customerID = intent.getStringExtra("customerID");
                    PaymentItemCode = intent.getStringExtra("PaymentItemCode");
                    BillerShortName = intent.getStringExtra("BillerShortName");
                    callingActivity = intent.getStringExtra("activity");
                } else if (intent.getStringExtra("activity").equals("AirtimePaymentOption")) {
                    callingActivity = intent.getStringExtra("activity");
                    amount = intent.getStringExtra("amount");
                    phoneno = intent.getStringExtra("phoneno");
                    network = intent.getStringExtra("networkname");
                } else {

                }

            }
        }
    }

    public void loadStoredCard() {
        M.showLoadingDialog(CardSetUp.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("SourcePhone",M.getPhoneno(CardSetUp.this))
                .addBodyParameter("Type","9")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();
                        if(response.trim().equals("07")){
                            two.setVisibility(View.GONE);
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

                            if (ANError.getErrorCode() == 404) {
                                M.showToastL(CardSetUp.this,getString(R.string.requested_resource_notfound));
                            } else if (ANError.getErrorCode() == 500) {
                                M.showToastL(CardSetUp.this,getString(R.string.error_at_server));
                            }

                        } else {
                            M.showToastL(CardSetUp.this,ANError.getErrorDetail());
                        }

                    }
                });
    }
    private void displaySavedAccountView(String response) throws JSONException {
        JSONArray jsonarr = new JSONArray(response);
        if(!response.equals("[]")){
            two.setVisibility(View.VISIBLE);

            for(int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                CardPojo bean = new CardPojo(jsonobj.getString("Id"), jsonobj.getString("ExtraData"),jsonobj.getString("PhoneNumber"), jsonobj.getString("CardType"));

                cp.add(bean);

            }
            CardAdapter adapter=new CardAdapter(this, cp);
            list=(ListView)findViewById(R.id.card_list_view);
            list.setAdapter(adapter);
            list.setDivider(null);
        } else if (response.equals("[]")){

            M.showToastL(context, "No Saved Card Available");
        }
    }



    private void saveCardDetails(){

       M.showLoadingDialog(CardSetUp.this);

                 AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("CardPan",TrippleDes.encrypt(mCardForm.getCardNumber()))
                .addBodyParameter("CVV2",TrippleDes.encrypt(mCardForm.getCvv()))
                .addBodyParameter("ExpiryDate",TrippleDes.encrypt(mCardForm.getExpirationYear().substring(2)+mCardForm.getExpirationMonth()))
                .addBodyParameter("CardExtra",mCardForm.getCardNumber().substring(mCardForm.getCardNumber().length()-4))
                .addBodyParameter("SourcePhone",M.getPhoneno(CardSetUp.this))
                .addBodyParameter("Channel","2")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .addBodyParameter("Type","1")
                .addBodyParameter("CardType", String.valueOf(CardTypes.detect(mCardForm.getCardNumber())).toLowerCase())
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
                        } else {
                            M.showToastL(CardSetUp.this,ANError.getErrorDetail());
                        }

                    }

                });
    }


    private void handleResponse(String response) {
        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode.trim()) {
                case "00":

                    if (callingActivity == null || callingActivity.equals(null)){
                        clearForm(mCardForm);
                        cp.clear();
                        loadStoredCard();
                        M.showToastL(context,"Card Added Successfully " );
                        break;
                    } else if(callingActivity.equals("paymentOption")){
                        sendDataBackToPaymentOption();
                        break;
                    }else if (callingActivity.equals("AirtimePaymentOption")){
                        sendDataBackToAirtimePaymentOption();
                        break;
                    }
                    //break;
                case "05":
                    M.DialogBox(CardSetUp.this,rc.getResponseMessage(responseCode.trim()));
                    break;
                default:
                    M.DialogBox(CardSetUp.this, rc.getResponseMessage(responseCode.trim()));
            }
        } catch (Exception e) {

        }

    }

    private void sendDataBackToAirtimePaymentOption() {

        Intent intent=new Intent();
        intent.putExtra("amount", amount);
        intent.putExtra("phoneno", phoneno);
        intent.putExtra("networkname", network);
        setResult(250,intent);
        finish();
    }

    private void sendDataBackToPaymentOption() {
        Intent intent=new Intent();
        intent.putExtra("PaymentItemName", productName);
        intent.putExtra("PaymentAmount", amount);
        intent.putExtra("BillerShortName", BillerShortName);
        intent.putExtra("PaymentItemCode", PaymentItemCode);
        intent.putExtra("customerID", customerID);
        setResult(200,intent);
        finish();
    }


    @Override
    public void onCardTypeChanged(CardType cardType) {
        if (cardType == CardType.EMPTY) {
            mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
        } else {
            mSupportedCardTypesView.setSelected(cardType);
        }
    }

    @Override
    public void onCardFormSubmit() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.action_scancard:
                if(mCardForm.isCardScanningAvailable());{
                mCardForm.scanCard(CardSetUp.this);
                 }
               break;*/
            case android.R.id.home:
                doThis();
                break;
              // return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                M.showToastL(CardSetUp.this, resultDisplayStr);


                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }*/
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

    @Override
    public void onBackPressed() {
        doThis();
        // super.onBackPressed();
        }

    private void doThis() {

        if(callingActivity == null){
            super.onBackPressed();
            return;
        } else if(callingActivity.equals("paymentOption")) {
            Intent intent = new Intent();
            intent.putExtra("PaymentItemName", productName);
            intent.putExtra("PaymentAmount", amount);
            intent.putExtra("BillerShortName", BillerShortName);
            intent.putExtra("PaymentItemCode", PaymentItemCode);
            intent.putExtra("customerID", customerID);
            setResult(200, intent);
            finish();
        } else if(callingActivity.equals("AirtimePaymentOption")) {
            Intent intent = new Intent();
            intent.putExtra("amount", amount);
            intent.putExtra("phoneno", phoneno);
            intent.putExtra("networkname", network);
            setResult(250, intent);
            finish();
        }
    }


}


