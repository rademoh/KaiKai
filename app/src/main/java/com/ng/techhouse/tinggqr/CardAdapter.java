package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.model.RechargePoJo;
import com.ng.techhouse.tinggqr.settings.BankAccountSetUp;
import com.ng.techhouse.tinggqr.settings.CardSetUp;
import com.ng.techhouse.tinggqr.util.AppConstant;
import com.ng.techhouse.tinggqr.util.EndPoint;
import com.ng.techhouse.tinggqr.util.M;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;




/**
 * Created by rabiu on 25/01/2017.
 */

public class CardAdapter extends ArrayAdapter<CardPojo> {


    private final Activity context;
    private ArrayList<CardPojo> cp;
    String responseCode;
    public CardAdapter(Activity context, ArrayList<CardPojo> cp) {
        super(context, R.layout.card_details_row, cp);
        this.context = context;
        this.cp = cp;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.card_details_row, null, true);

        TextView extradata = (TextView) rowView.findViewById(R.id.extra_data);
        extradata.setText("**** **** "+ cp.get(position).getExtraData().toString());
        final String CardExtra = cp.get(position).getExtraData().toString();

        ImageView ivDelete = (ImageView) rowView.findViewById(R.id.deleteBtn);
        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConfirmDialogBox(CardExtra, position);
            }
        });

        ImageView ivCardImage = (ImageView) rowView.findViewById(R.id.cardimage);
        String cardType =  cp.get(position).getCardType().toString();
        if((cardType.equalsIgnoreCase("masterCard") || cardType.equalsIgnoreCase("mastercard"))){
            ivCardImage.setImageResource(R.drawable.master);
        } else if ((cardType.equalsIgnoreCase("visa")|| cardType.equalsIgnoreCase("visaCard"))){
            ivCardImage.setImageResource(R.drawable.visa);
        } else {
            ivCardImage.setImageResource(R.drawable.ic_card);
        }

        return rowView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return cp.size();
    }
    public void setCard(List<CardPojo> data) {
        cp.addAll(data);
        notifyDataSetChanged();
    }

    public void ConfirmDialogBox(final String CardExtra, final int position){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage("Delete Card Ending With " + CardExtra +" ?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        deleteAccount(CardExtra , position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void deleteAccount(final String CardExtra , final int position) {
        M.showLoadingDialog(context);
        AndroidNetworking.post(new EndPoint().getHttps()+new EndPoint().getLordaragorn()+new EndPoint().getSmartWalet()+new EndPoint().getSavedCardDetails())
                .addBodyParameter("SourcePhone",M.getPhoneno(context))
                .addBodyParameter("CardExtra",CardExtra)
                .addBodyParameter("PlatformId",M.getPlatformId(context))
                .addBodyParameter("Type","8")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        M.hideLoadingDialog();

                        try {
                            updateView(response, position);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError ANError) {

                        M.hideLoadingDialog();

                        if (ANError.getErrorCode() != 0) {
                        } else {
                            M.showToastL(context,ANError.getErrorDetail());
                        }
                    }});
    }

    private void updateView(String response, int position)throws JSONException {

        String[] responseArray = response.split("\\|");
        responseCode = responseArray[0];

        try{
            switch (responseCode.trim()) {
                case "00":
                    cp.remove(position);
                    notifyDataSetChanged();
                    if(cp.size() <  1){
                        CardSetUp.two.setVisibility(View.GONE);
                    }
                    M.showToastL(getContext().getApplicationContext(),"Card Successfully Deleted");
                    break;
            }
        } catch (Exception e) {

        }
    }
}
