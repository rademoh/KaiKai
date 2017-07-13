package com.ng.techhouse.tinggqr.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ng.techhouse.tinggqr.CustomTFSpan;
import com.ng.techhouse.tinggqr.FundTinggOption;
import com.ng.techhouse.tinggqr.Login;
import com.ng.techhouse.tinggqr.R;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dmax.dialog.SpotsDialog;


/**
 * Created by rabiu on 16/01/2017.
 */

public class M {
    static ProgressDialog pDialog;
    static AlertDialog dialog;
    private static Context context;
    private static SharedPreferences mSharedPreferences;
    private static  CountDownTimer countDownTimer;
    private  static  Handler handler;
    private  static  Runnable r;
    public static final String SUCCESS = "00";

    public static void DialogBox(Context mContext, String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        Typeface face= Typeface.createFromAsset(mContext.getAssets(),"fonts/MavenPro-Regular.ttf");
        alertDialogBuilder
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setTypeface(face);
    }
    public static void DialogBoxExit(final Context mContext, String text, final String amount) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        Typeface face= Typeface.createFromAsset(mContext.getAssets(),"fonts/MavenPro-Regular.ttf");
        alertDialogBuilder
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent intent = new Intent(mContext, FundTinggOption.class);
                        intent.putExtra("amount", amount);
                        mContext.startActivity(intent);

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setTypeface(face);
    }

    public static void DialogBox(Context mContext, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        Typeface face= Typeface.createFromAsset(mContext.getAssets(),"fonts/MavenPro-Regular.ttf");
        CustomTFSpan customTFSpan = new CustomTFSpan(face);
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(customTFSpan, 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        alertDialogBuilder.setTitle(spannableString);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setTypeface(face);
    }

    public static void showLoadingDialog(Context mContext) {
        dialog = new SpotsDialog(mContext,R.style.Custom);
        dialog.setCancelable(false);
        dialog.show();
    }
    public static void hideLoadingDialog() {
       if(dialog.isShowing()){
           dialog.dismiss();
       }
    }



   /* public static void showLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoadingDialog() {
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }
*/
    public static boolean setBalance(String balance, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("balance", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("balance", balance);
        return editor.commit();
    }

    public static String getBalance(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("balance", 0);
        return mSharedPreferences.getString("balance", null);
    }

    public static boolean setPhoneno(String phoneno, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("phoneno", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("phoneno", phoneno);
        return editor.commit();
    }

    public static boolean setEmail(String email, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("email", email);
        return editor.commit();
    }

    public static String getEmail(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("email", 0);
        return mSharedPreferences.getString("email", null);
    }


    public static String getPhoneno(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("phoneno", 0);
        return mSharedPreferences.getString("phoneno", null);
    }

    public static boolean setPassword(String password, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("password", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("password", password);
        return editor.commit();
    }

    public static String getPassword(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("password", 0);
        return mSharedPreferences.getString("password", null);
    }

    public static boolean setFullName(String fullname, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("fullname", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("fullname", fullname);
        return editor.commit();
    }

    public static String getFullName(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("fullname", 0);
        return mSharedPreferences.getString("fullname", null);
    }

    public static boolean setPlatformId(String platformId, Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("platformId", 0);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("platformId", platformId);
        return editor.commit();
    }

    public static String getPlatformId(Context mContext) {
        mSharedPreferences = mContext.getSharedPreferences("platformId", 0);
        return mSharedPreferences.getString("platformId", null);
    }


    public static void showToastL(Context mContext, String message) {

        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();

    }

    public static void showToastS(Context mContext, String Message) {
        Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(Context context, View view) {
        Snackbar sb = Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.error));
        sb.show();
    }
    public static void showSnackBar(Context context, View view,String msg) {
        Snackbar sb = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        sb.getView().setBackgroundColor(ContextCompat.getColor(context, R.color.error));
        sb.show();
    }

    public static void logOut() {
       // Context context = null;
        Intent i = new Intent(context, Login.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("timeout", "timeout");
        context.startActivity(i);
    }



    /*public static void TimeOut(final Context context) {
    new CountDownTimer(300000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
              //  M.logOut(context);
            }
        }.start();

    }*/

    public static void TimeOut(final Context context) {
        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
              //  M.logOut(context);
            }
        }.start();
    }

    public static void EndTimer(){
        countDownTimer.cancel();
    }

    public static void SessionEnd(final Context context){
    handler = new Handler();
    r = new Runnable() {

        @Override
        public void run() {
        /// M.logOut(context);
        }
    };
    startHandler();
}
    public static void stopHandler() {
        handler.removeCallbacks(r);
    }

    public static  void startHandler() {
        handler.postDelayed(r, 60000);
    }
    private static final SimpleDateFormat monthDayformatter = new SimpleDateFormat("dd MMM");

    public static String timestampToMonthDay(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return monthDayformatter.format((java.util.Date) timestamp);
        }
    }

    public static String formatFullName(String fullname){
        String output= null;
        String[] responseArray = fullname.split("\\ ");
        StringBuilder sb =new StringBuilder();
        for(int i = 0; i < responseArray.length; i++){
            sb.append(responseArray[i].substring(0,1).toUpperCase() + responseArray[i].substring(1).toLowerCase() +" ");
        }
        output = sb.toString();
        return  output;
    }


    public static Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date = (Date) formatter.parse(str_date);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }



}