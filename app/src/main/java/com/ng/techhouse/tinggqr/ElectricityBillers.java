package com.ng.techhouse.tinggqr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.model.RechargePoJo;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.ConnectionDetector;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;
import com.ng.techhouse.tinggqr.util.Timeout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static android.provider.Telephony.Mms.Part.FILENAME;

public class ElectricityBillers extends AppCompatActivity {

    private static final String TAG ="EB" ;
    private ArrayList<RechargePoJo> rp;
    ListView list;
    ConnectionDetector connectionDetector;
    private  Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_billers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        connectionDetector = new ConnectionDetector(this);
        rp = new ArrayList<RechargePoJo>();

        AndroidNetworking.initialize(getApplicationContext());

        if(connectionDetector.isConnectingToInternet()){
            getElectricityBillers();
        } else{
            M.showSnackBar(getApplicationContext(), findViewById(android.R.id.content));
        }


    }

    private void getElectricityBillers(){
        M.showLoadingDialog(ElectricityBillers.this);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getDataOps()+"Phone=2348060&PlatformId=001&Type=1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        try {
                            handleResponse(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {
                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {

                        } else {
                             M.showToastL(ElectricityBillers.this,ANError.getErrorDetail());
                        }

                    }
                });
    }

    private void handleResponse(String response) throws JSONException  {

        JSONArray  jsonarr = new JSONArray(response);

        for(int i = 0; i < jsonarr.length(); i++){

            JSONObject jsonobj = jsonarr.getJSONObject(i);

            RechargePoJo bean = new RechargePoJo(jsonobj.getString("BillerId"),jsonobj.getString("PaymentItemName"),jsonobj.getString("BillerName"),jsonobj.getString("PaymentAmount"),jsonobj.getString("BillerShortName"),jsonobj.getString("PaymentItemCode"));

            rp.add(bean);
        }

        ElectricityAdapter adapter=new ElectricityAdapter(this, rp);
        list=(ListView)findViewById(R.id.recharge_list_view);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), CustomerIdentification.class);
                intent.putExtra("BillerId",rp.get(position).getBillerId());
                intent.putExtra("PaymentItemName",rp.get(position).getPaymentItemName());
                intent.putExtra("BillerName",rp.get(position).getBillerName());
                intent.putExtra("PaymentAmount",rp.get(position).getPaymentAmount());
                intent.putExtra("BillerShortName",rp.get(position).getBillerShortName());
                intent.putExtra("PaymentItemCode",rp.get(position).getPaymentItemCode());
                startActivity(intent);
            }
        });
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }
    @Override
    public void onUserInteraction() {

        super.onUserInteraction();

        Timeout.reset();
    }

    /*public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getApplicationContext().getAssets().open("billers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }*/

}
