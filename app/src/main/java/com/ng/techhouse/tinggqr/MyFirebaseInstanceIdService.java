package com.ng.techhouse.tinggqr;

import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.M;



/**
 * Created by rabiu on 21/02/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

 private  static  final String REG_TOKEN ="REG_TOKEN";
    private  static  final String TAG ="REG_TOKEN_SERVICE";
    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN, recent_token);
       // sendToServer();
    }

    private void sendToServer() {
        M.showLoadingDialog(MyFirebaseInstanceIdService.this);
        AndroidNetworking.post("")
                .addBodyParameter("Phone",M.getPhoneno(MyFirebaseInstanceIdService.this))
                .addBodyParameter("Channel", "2")
                .addBodyParameter("PlatformId", "001")
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
                            Toast.makeText(getApplicationContext(), ANError.getErrorDetail(), Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }

    private void handleResponse(String response) {
    }
}
