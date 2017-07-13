package com.ng.techhouse.tinggqr;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.WriterException;
import com.ng.techhouse.tinggqr.qrgenerator.QRGEncoders;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

//import me.sudar.zxingorient.ZxingOrient;
import androidmads.library.qrgenearator.QRGContents;


public class ReceivePayment extends  AppCompatActivity{

    private static final String TAG ="QRCode" ;
    ImageView imageView;
    EditText etAmount;
    public final static int QRcodeWidth = 400;
    Bitmap bitmap;
    Handler rabiusHandle = new Handler();
    TextView nairacode,encryptedString;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (ImageView) findViewById(R.id.imageView);
        etAmount = (EditText) findViewById(R.id.input_amount);
        encryptedString = (TextView) findViewById(R.id.encryptedString);
        nairacode = (TextView) findViewById(R.id.nairacode);

        nairacode.setText("\u20A6");

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);


        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                imageView = (ImageView) findViewById(R.id.imageView);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                Uri bmpUri = getLocalBitmapUri(bitmap);

                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
               // shareIntent.putExtra(Intent.EXTRA_TEXT, encryptedString.getText().toString());
                shareIntent.setType("image/*");

                // Launch sharing dialog for image
                 startActivity(Intent.createChooser(shareIntent, "Share Payment QR Code"));

            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, encryptedString.getText().toString());
                shareIntent.setType("text/plain");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Payment Code"));
            }
        });

        encryptedString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(encryptedString.getText());
                Snackbar.make(view, "Payment Code Copied", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
             updateBarcode();
             updateEncryption();
             etAmount.addTextChangedListener(watch);

    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private String getAmount(){
        String amount;
      //  amount = etAmount.getText().toString().replace(",","");
        amount = etAmount.getText().toString();
        if (amount.isEmpty()){
            amount = "ID";
        }
        return  amount;
    }

    private String paymentCode(){
        String output="C3LLUL4NT"+"|"+M.getPhoneno(ReceivePayment.this) + "|" + getAmount();
        return output;
    }

    private void updateEncryption(){
        try {
            String encryptedPaymentCode = AESCrypt.encrypt(AESHelper.seedValue,paymentCode());
            encryptedString.setText(encryptedPaymentCode);
        }catch (GeneralSecurityException e){

        }

    }

    private void updateBarcode() {
        String encryptedPaymentCode=null;
        try {
            encryptedPaymentCode = AESCrypt.encrypt(AESHelper.seedValue,paymentCode());
        }catch (GeneralSecurityException e){

        }
        QRGEncoders qrgEncoder = new QRGEncoders(encryptedPaymentCode, null, QRGContents.Type.TEXT, QRcodeWidth);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap(0xFF000000);
            rabiusHandle.post(new Runnable() {
                @Override
                public void run() {
                    // Setting Bitmap to ImageView
                    imageView.setImageBitmap(bitmap);
                }
            });


        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }

    }


    TextWatcher watch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            etAmount.removeTextChangedListener(this);

            try {
                String originalString = s.toString();

                Long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                etAmount.setText(formattedString);
                etAmount.setSelection(etAmount.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            etAmount.addTextChangedListener(this);

            Runnable rabiuRunnable = new Runnable() {
                @Override
                public void run() {

                    updateBarcode();
                }
            };
            new Thread(rabiuRunnable).start();

            updateEncryption();

          //  M.showToastS(context, paymentCode());
        }

        @Override
        public void afterTextChanged(Editable s) {

          /*  Runnable rabiuRunnable = new Runnable() {
                @Override
                public void run() {

                    updateBarcode();
                }
            };
            new Thread(rabiuRunnable).start();

            updateEncryption();
*/
            /*etAmount.removeTextChangedListener(this);

            try {
                String originalString = s.toString();

                Long longval;
                if (originalString.contains(",")) {
                    originalString = originalString.replaceAll(",", "");
                }
                longval = Long.parseLong(originalString);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longval);

                //setting text after format to EditText
                etAmount.setText(formattedString);
                etAmount.setSelection(etAmount.getText().length());
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            etAmount.addTextChangedListener(this);
*/
        }
    };

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}





