package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.ng.techhouse.tinggqr.barcode.BarcodeCaptureActivity;
import com.ng.techhouse.tinggqr.model.Beanlist;
import com.ng.techhouse.tinggqr.navactivity.AboutUs;
import com.ng.techhouse.tinggqr.navactivity.ContactUs;
import com.ng.techhouse.tinggqr.navactivity.Help;
import com.ng.techhouse.tinggqr.settings.Settings;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.ArrayList;

import android.Manifest;
import android.widget.Toast;




public class MainActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener {

    String decodedString, versionName;
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    LinearLayout request_moneyBtn, pay_barcodeBtn, pay_codeBtn, profileBtn, sendPaymentBtn,lministatement, fundwallet;
    TextView tvname, tvbalance,tvPhone , tvVersionName;

    private RecyclerView rv;
    private RechargeAdapter baseAdapter;
    ConnectionDetector connectionDetector;
    private ArrayList<Beanlist> Bean;
    private Context context = this;
    private static final String TAG = "Dashboard";
    Toolbar toolbar;


    private int[] IMAGEG = {R.drawable.ic_lightbulb, R.drawable.ic_live_tv, R.drawable.web, R.drawable.ic_airtime, R.drawable.ic_open};
    private String[] RECHARGETITLE = {"Electricity", "Cable", "Internet", "Airtime", "Others"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp < 360) {
            setContentView(R.layout.activity_main1);
        } else {
            setContentView(R.layout.activity_main);
        }
        connectionDetector = new ConnectionDetector(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.tingglogo110x36);
        toolbar.setNavigationIcon(null);

        setupDrawer();


        tvname = (TextView) findViewById(R.id.tv_name);
        tvbalance = (TextView) findViewById(R.id.tv_balance_amount);

        tvname.setText(formatFullName(M.getFullName(MainActivity.this)));
       tvbalance.setText(M.getBalance(MainActivity.this));


        request_moneyBtn = (LinearLayout) findViewById(R.id.l_request_money);
        pay_codeBtn = (LinearLayout) findViewById(R.id.l_pay_code);
        pay_barcodeBtn = (LinearLayout) findViewById(R.id.l_pay_barcode);
        profileBtn = (LinearLayout) findViewById(R.id.l_profile);
        sendPaymentBtn = (LinearLayout) findViewById(R.id.l_send_money);
        lministatement = (LinearLayout) findViewById(R.id.l_ministatement);
        fundwallet = (LinearLayout) findViewById(R.id.fund_wallet);

        request_moneyBtn.setOnClickListener(this);
        pay_barcodeBtn.setOnClickListener(this);
        pay_codeBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);
        sendPaymentBtn.setOnClickListener(this);
        lministatement.setOnClickListener(this);
        fundwallet.setOnClickListener(this);


        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(mLayoutManager);


        Bean = new ArrayList<Beanlist>();

        for (int i = 0; i < RECHARGETITLE.length; i++) {
            Beanlist bean = new Beanlist(IMAGEG[i], RECHARGETITLE[i]);
            Bean.add(bean);
        }

        baseAdapter = new RechargeAdapter(MainActivity.this, Bean) {
        };
        rv.setAdapter(baseAdapter);

         Timeout.init(context);

        checkSuccess();
        pushnotification();

    }


    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvPhone = (TextView) header.findViewById(R.id.tvPhone);
        versionName = BuildConfig.VERSION_NAME;
        tvVersionName = (TextView) header.findViewById(R.id.tvVersionName);
        tvVersionName.setText("v"+versionName);
        tvPhone.setText(M.getPhoneno(MainActivity.this));

      // NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }


    }

    private void checkSuccess() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getStringExtra("successful") != null) {
                if(connectionDetector.isConnectingToInternet()){
                    getCustomerBalance();
                } else{
                    M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
                }
            } else {
            }
        }
    }

    private boolean checkGPVC() throws PackageManager.NameNotFoundException {
        boolean valid = false;
        int v = getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode;
        if (v >= 9080000){
          valid = true;
        }
      return valid;
    }

    private void getCustomerBalance() {
       // M.showLoadingDialog(MainActivity.this);
        AndroidNetworking.post(new EndPoint().getHttp()+new EndPoint().getIpDomain()+new EndPoint().getSmartWalet()+"BalanceEnquiry?")
                .addBodyParameter("SourcePhone", M.getPhoneno(MainActivity.this))
                .addBodyParameter("Channel", "7")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                      //  M.hideLoadingDialog();

                        handleResponse(response);
                    }

                    @Override
                    public void onError(ANError ANError) {

                      //  M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(MainActivity.this, ANError.getErrorDetail());
                        }

                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                    drawer.closeDrawer(Gravity.RIGHT);
                } else {
                    drawer.openDrawer(Gravity.RIGHT);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void gotoSettings() {
        Intent intentSettings = new Intent(getApplicationContext(), Settings.class);
        startActivity(intentSettings);
    }

    private void handleResponse(String response) {
        tvbalance.setText(response);
        M.setBalance(response, MainActivity.this);
    }

    public  void scanTinggQR(){
        Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        intent.putExtra("activity","Main");
        startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    String barcodeValue = barcode.displayValue.trim();
                    if (barcodeValue.substring(0,4).equals("9988")){
                        Intent intentBundle = new Intent(MainActivity.this, PaymentActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("amount","tinggcard");
                        bundle.putString("phone", barcodeValue);
                        intentBundle.putExtras(bundle);
                        startActivity(intentBundle);
                    } else{
                    try {
                        decodedString = AESCrypt.decrypt(AESHelper.seedValue, barcode.displayValue.trim());
                    } catch (GeneralSecurityException e) {
                         M.DialogBox(MainActivity.this, "Alert", "Invalid Barcode");
                    }
                    try{
                        if (getString(R.string.identifier).equals(decodedString.substring(0, 9))) {
                            Intent intentBundle = new Intent(MainActivity.this, PaymentActivity.class);
                            Bundle bundle = new Bundle();
                            String[] results = decodedString.split("\\|");
                            bundle.putString("phone", results[1]);
                            if (results[2].equals("ID")) {
                                bundle.putString("amount", "noamount");
                            } else {
                                bundle.putString("amount", results[2]);
                            }
                            intentBundle.putExtras(bundle);
                            startActivity(intentBundle);
                        }else {
                            M.DialogBox(MainActivity.this, "Alert", "Invalid Tingg Barcode");
                        }
                    }catch (NullPointerException e) {
                        M.DialogBox(MainActivity.this, "Alert", "Invalid Tingg Barcode");
                    }
                    }
                }else
                    M.DialogBox(context,getString(R.string.no_barcode_captured ));
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.l_request_money:
                Intent intentReceivePayment = new Intent(getApplicationContext(), ReceivePaymentOption.class);
                startActivity(intentReceivePayment);
                break;
            case R.id.l_pay_barcode:
                try {
                    if(checkGPVC()){
                        scanTinggQR();
                    }else {
                        scanTinggQR();
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.l_profile:
                Intent ints = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(ints);
                break;
            case R.id.l_pay_code:
                Intent intse = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intse);
                break;
            case R.id.l_send_money:
                Intent intsend = new Intent(getApplicationContext(), SendPayment.class);
                startActivity(intsend);
                break;
           case R.id.l_ministatement:
                Intent intm = new Intent(getApplicationContext(), MiniStatement.class);
                startActivity(intm);
                break;
            case R.id.fund_wallet:
                Intent intf = new Intent(getApplicationContext(), FundWallet.class);
                startActivity(intf);
                break;
            default:
                break;

        }

    }

    @Override
    public void onBackPressed() {
        exitAppDialogBox();
    }

    public void exitAppDialogBox() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage("Press Yes to exit")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intl = new Intent(getApplicationContext(), Login.class);
                        intl.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.finish();
                        startActivity(intl);
                        Timeout.stop();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
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

    private void pushnotification(){
        String message,title;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("message") != null) {
                message = getIntent().getExtras().getString("message");
                title = getIntent().getExtras().getString("title");
                M.DialogBox(MainActivity.this,title, message);
            } else {
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

   @Override
    protected void onStart() {
        super.onStart();
        Timeout.reset();

    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();
        Timeout.reset();
    }
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MavenPro-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {



        int id = item.getItemId();


        if (id == R.id.nav_settings) {
            gotoSettings();
        } else if (id == R.id.nav_help) {
            Intent intent = new Intent(context, Help.class);
            startActivity(intent);
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(context, AboutUs.class);
            startActivity(intent);
        } else if (id == R.id.nav_contact) {
            Intent intent = new Intent(context, ContactUs.class);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
              exitAppDialogBox();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }


    public String formatFullName(String fullname){
        String output= null;
        String[] responseArray = fullname.split("\\s+");
        StringBuilder sb =new StringBuilder();
        for(int i = 0; i < responseArray.length; i++){
            sb.append(responseArray[i].substring(0,1).toUpperCase() + responseArray[i].substring(1).toLowerCase() +" ");
        }
        output = sb.toString();
        return  output;
    }



}