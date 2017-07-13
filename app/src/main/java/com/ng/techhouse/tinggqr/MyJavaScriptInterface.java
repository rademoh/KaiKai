package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by rabiu on 15/06/2017.
 */

public class MyJavaScriptInterface {


    private Context ctx;
    MyJavaScriptInterface(Context ctx) {
        this.ctx = ctx;
    }

    @JavascriptInterface
    public void showHTML(String html) {
        System.out.println("BODY "+html);
    }
}
