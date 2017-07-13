package com.ng.techhouse.tinggqr;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
//import com.google.zxing.WriterException;
//import com.ng.techhouse.tinggqr.qrgenerator.QRGEncoders;
import com.google.zxing.WriterException;
import com.ng.techhouse.tinggqr.qrgenerator.QRGEncoders;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;
import com.scottyab.aescrypt.AESCrypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

import androidmads.library.qrgenearator.QRGContents;

public class MyProfile extends AppCompatActivity{

    TextView tvCustomerCode;
    ImageView user_barcode,barcodeView;
    Bitmap bitmap;
    String encryptedID;
    public final static int QRcodeWidth = 400;
    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2;
    private Context context = this;
    private static final String TAG ="MyProfile" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_barcode = (ImageView) findViewById(R.id.user_barcode);
        tvCustomerCode = (TextView) findViewById(R.id.input_customer_code);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);


        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu first item clicked
                barcodeView = (ImageView) findViewById(R.id.user_barcode);
                Bitmap bitmap = ((BitmapDrawable)barcodeView.getDrawable()).getBitmap();
                Uri bmpUri = getLocalBitmapUri(bitmap);

                // Construct a ShareIntent with link to image
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                // shareIntent.putExtra(Intent.EXTRA_TEXT, encryptedString.getText().toString());
                shareIntent.setType("image/*");

                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Payment ID QR Code"));


            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO something when floating action menu second item clicked
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, encryptedID);
                shareIntent.setType("text/plain");
                // Launch sharing dialog for image
                startActivity(Intent.createChooser(shareIntent, "Share Payment ID Code"));

            }
        });


        displayCustomerID();

        tvCustomerCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(tvCustomerCode.getText());
                Snackbar.make(view, "Tingg ID Code Copied", Snackbar.LENGTH_LONG).setAction("Action", null).show();

            }
        });

    }

    private void displayCustomerID() {

        String unique_id = "C3LLUL4NT" + "|" + M.getPhoneno(MyProfile.this) + "|" + "ID";
        try {
            encryptedID = AESCrypt.encrypt(AESHelper.seedValue, unique_id);
        }catch (GeneralSecurityException e){

        }
        QRGEncoders qrgEncoders = new QRGEncoders(encryptedID,null,QRGContents.Type.TEXT,QRcodeWidth);
      //  QRGEncoders qrgEncoder = new QRGEncoders(encryptedID, null, QRGContents.Type.TEXT, QRcodeWidth);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoders.encodeAsBitmap(0xFF000000);
            // Setting Bitmap to ImageView
            user_barcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.v(TAG, e.toString());
        }
        tvCustomerCode.setText(encryptedID);
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
}
