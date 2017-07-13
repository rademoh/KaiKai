package com.ng.techhouse.tinggqr.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by rabiu on 27/02/2017.
 */

public class Util {

    static String[] MTNList = { "0703","0706","0803","0806","0810","0813","0814","0816","0903"};
    static String[] ZAINlist = {"0701","0708","0802","0808","0812","0902"};
    static String[] ETISALATlist = {"0809","0817","0818","0909","0908"};
    static String[] GLOlist = {"0705","0805","0807","0811","0815","0905"};

   ///Glo","MTN","Airtel","Etisalat"

    public static String checkNetworkName(String prefix){
        String networkname = null;
        for(String  list: MTNList){
            if(prefix.equals(list)){
                networkname = "MTN";
            }
            for(String  list1: ZAINlist){
                if(prefix.equals(list1)){
                    networkname = "Airtel";
                }
                for(String  list2: ETISALATlist){
                    if(prefix.equals(list2)){
                        networkname = "Etisalat";
                    }
                    for(String  list3: GLOlist){
                        if(prefix.equals(list3)){
                            networkname = "Glo";
                        }
                    }
                }
            }
        }
        return networkname;
    }


    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }



}
