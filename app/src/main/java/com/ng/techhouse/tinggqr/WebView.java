package com.ng.techhouse.tinggqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;

import java.util.Date;

public class WebView extends AppCompatActivity {

    android.webkit.WebView browser;
    String amount,cardNumber,cvv,expiry,cardTypeName;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentExtras();

        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        browser = (android.webkit.WebView) findViewById(R.id.webview);
        browser.setWebViewClient(new MyWebViewClient());
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       // browser.addJavascriptInterface(new MyJavaScriptInterface(this), "HtmlViewer");
        browser.addJavascriptInterface(new Object() {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl) {
                Intent i = new  Intent(getApplicationContext(),MainActivity.class);
                i.putExtra("successful","successful");
                startActivity(i);
                finish();
            }
        }, "ok");
        browser.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(android.webkit.WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                if(newProgress == 100){
                    // Hide the progressbar
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {

            Log.d("MIGS URL",""+intent.getStringExtra("amount")+"&vpc_Card="+intent.getStringExtra("cardType")+"&vpc_CardNum="+intent.getStringExtra("cardNumber")+"&vpc_CardExp="+intent.getStringExtra("expiry")+"&vpc_CardSecurityCode="+intent.getStringExtra("cvv")+"&phone="+M.getPhoneno(this)+"&platform="+M.getPlatformId(this)+"&billers=fw");
            browser.loadUrl(new EndPoint().getMigsUrl()+""+intent.getStringExtra("amount")+"&vpc_Card="+intent.getStringExtra("cardType")+"&vpc_CardNum="+intent.getStringExtra("cardNumber")+"&vpc_CardExp="+intent.getStringExtra("expiry")+"&vpc_CardSecurityCode="+intent.getStringExtra("cvv")+"&phone="+M.getPhoneno(this)+"&platform="+M.getPlatformId(this)+"&billers=fw");
        }
    }

    private void getIntentExtras() {
        Intent intent = getIntent();
        if (intent != null) {
            amount = intent.getStringExtra("amount");
            cardNumber = intent.getStringExtra("cardNumber");
            cvv = intent.getStringExtra("cvv");
            expiry = intent.getStringExtra("expiry");
            cardTypeName = intent.getStringExtra("cardType");
        }
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView webView, String url) {
            webView.loadUrl(url);
            return true;
           // return  super.shouldOverrideUrlLoading(webView,url);
        }

        @Override
        public void onPageStarted(android.webkit.WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            mProgressBar.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPageFinished(android.webkit.WebView view, String url) {

           browser.loadUrl("javascript:HtmlViewer.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
          // browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(android.webkit.WebView view, WebResourceRequest request, WebResourceError error) {

            Log.d("error response",error.getDescription().toString() + error.getErrorCode() );
        }

        @Override
        public void onReceivedSslError(android.webkit.WebView view, SslErrorHandler handler, SslError error) {
            Log.d("ssl error response",error.toString() +" "+ error.getPrimaryError() );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.browser.canGoBack()) {
            this.browser.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Funds();
                onBackPressed();
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
        Intent intentP = new Intent(getApplicationContext(), FundTinggByCard.class);
        intentP.putExtra("amount",amount);
        startActivity(intentP);
    }

}
