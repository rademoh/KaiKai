package com.ng.techhouse.tinggqr.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.R;
import com.ng.techhouse.tinggqr.ResponseCode;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

public class  ChangePassword extends AppCompatActivity {

    EditText etOldPassword,etNewPassword,etConfirmPassword;
    Button submitBtn;
    String oldPassword,newPassword,confirmPassword;
    ResponseCode rc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etOldPassword = (EditText) findViewById(R.id.input_oldpassword);
        etNewPassword = (EditText) findViewById(R.id.input_newpassword);
        etConfirmPassword = (EditText) findViewById(R.id.input_confirmpassword);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        rc = new ResponseCode();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()){
                    changePassword();
                }
            }
        });

    }

    private void changePassword() {

        M.showLoadingDialog(ChangePassword.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getChangePin())
                .addBodyParameter("Pin",oldPassword)
                .addBodyParameter("Phone", M.getPhoneno(ChangePassword.this))
                .addBodyParameter("Channel", "2")
                .addBodyParameter("OldPin", oldPassword)
                .addBodyParameter("NewPin", newPassword)
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .addBodyParameter("Action", "2")
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
                            if (ANError.getErrorCode() == 404) {
                                M.showToastL(ChangePassword.this,getString(R.string.requested_resource_notfound));
                            } else if (ANError.getErrorCode() == 500) {
                                M.showToastL(ChangePassword.this,getString(R.string.error_at_server));
                            }
                        } else {
                            M.showToastL(ChangePassword.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) {

        try{
            switch (response.trim()) {

                case "00":
                    etConfirmPassword.setText("");
                    etNewPassword.setText("");
                    etOldPassword.setText("");
                    M.DialogBox(ChangePassword.this,rc.getResponseMessage(response.trim()));
                    break;
                case "05":
                    M.DialogBox(ChangePassword.this,rc.getResponseMessage(response.trim()));
                    break;
                default:
                    M.DialogBox(ChangePassword.this,rc.getResponseMessage(response.trim()));
            }
        } catch (Exception e) {

        }

    }

    public boolean validate() {

        boolean valid = true;

        oldPassword = etOldPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();


        if (oldPassword.isEmpty() ) {
            etOldPassword.setError("enter Pin");
            valid = false;
        } else if (oldPassword.length() <=3) {
            etOldPassword.setError("Pin must be 4 digits");
            valid = false;
        }else if (newPassword.isEmpty()) {
            etNewPassword.setError("enter Pin");
            valid = false;
        }else if (newPassword.length() <=3 ) {
            etNewPassword.setError("Pin must be 4 digits");
            valid = false;
        }else if (confirmPassword.isEmpty()) {
            etConfirmPassword.setError("enter Pin");
            valid = false;
        }else if (!newPassword.equals(confirmPassword) ) {
            etConfirmPassword.setError("Password does not match");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }
}
