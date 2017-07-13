package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.model.StatementlistPoJo;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MiniStatement extends  AppCompatActivity{

    private static final String TAG ="MiniStatement" ;
    private ArrayList<StatementlistPoJo> sp;
    ListView list;
    ConnectionDetector connectionDetector;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_statement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        connectionDetector = new ConnectionDetector(this);
        sp = new ArrayList<StatementlistPoJo>();

        AndroidNetworking.initialize(getApplicationContext());

        if(connectionDetector.isConnectingToInternet()){
            getMiniStatement();
        } else{
            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
        }

    }

    private void getMiniStatement(){
        M.showLoadingDialog(MiniStatement.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+"MiniStatement1?")
                .addBodyParameter("SourcePhone",M.getPhoneno(MiniStatement.this))
                .addBodyParameter("Channel", "5")
                .addBodyParameter("PlatformId",M.getPlatformId(this))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        try {
                            handleResponse(response);
                            Log.d("Statement Response" , response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                            M.showToastL(MiniStatement.this,ANError.getErrorDetail());
                        }

                    }
                });
    }


    private void handleResponse(String response) throws JSONException  {

        if (response.equalsIgnoreCase("[]")){
            M.showToastS(this, "You haven't perform any transaction yet ");
        } else {

            JSONArray jsonarr = new JSONArray(response);

            for (int i = 0; i < jsonarr.length(); i++) {

                JSONObject jsonobj = jsonarr.getJSONObject(i);

                if (!jsonobj.getString("Action").equalsIgnoreCase("MiniStatement")) {

                    StatementlistPoJo bean = new StatementlistPoJo(jsonobj.getString("Status"), jsonobj.getString("Action"), jsonobj.getString("Amount"), jsonobj.getString("Date"), jsonobj.getString("TransactionId"));

                    sp.add(bean);

                }
                 MiniStatementAdapter adapter=new MiniStatementAdapter(this, sp);
                 list=(ListView)findViewById(R.id.ministatement_list_view);
                 list.setAdapter(adapter);
            }

            // MiniStatementAdapter adapter=new MiniStatementAdapter(this, sp);
            // list=(ListView)findViewById(R.id.ministatement_list_view);
            // list.setAdapter(adapter);
        }
    }

    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

}
