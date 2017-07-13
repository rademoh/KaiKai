package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.model.CardPojo;
import com.ng.techhouse.tinggqr.util.M;

import java.util.ArrayList;


/**
 * Created by rabiu on 25/01/2017.
 */

public class CardAdapterPay extends ArrayAdapter<CardPojo> {


    private final Activity context;
    private ArrayList<CardPojo> cp;
    public static RadioButton selected=null;
    public static ArrayList<CardPojo> selectedRadioButton;


    public CardAdapterPay(Activity context, ArrayList<CardPojo> cp) {
        super(context, R.layout.card_details_row_pay, cp);
        this.context = context;
        this.cp = cp;
        selectedRadioButton = new ArrayList<CardPojo>();

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.card_details_row_pay, null, true);

        TextView extradata = (TextView) rowView.findViewById(R.id.extra_data);
        if( cp.get(position).getExtraData().toString().length() > 4){
            extradata.setText(cp.get(position).getExtraData().toString());
        } else{
            extradata.setText("**** **** "+ cp.get(position).getExtraData().toString());
        }
        ImageView ivCardImage = (ImageView) rowView.findViewById(R.id.cardimage);
        String cardType =  cp.get(position).getCardType().toString();
        if((cardType.equalsIgnoreCase("masterCard") || cardType.equalsIgnoreCase("mastercard"))){
            ivCardImage.setImageResource(R.drawable.master);
        } else if ((cardType.equalsIgnoreCase("visa")|| cardType.equalsIgnoreCase("visaCard"))){
            ivCardImage.setImageResource(R.drawable.visa);
        }else if ((cardType.equalsIgnoreCase("tingg"))){
            ivCardImage.setImageResource(R.drawable.tingg_logo_s);
        }else {
            ivCardImage.setImageResource(R.drawable.ic_card);
        }

        final RadioButton rbcard = (RadioButton) rowView.findViewById(R.id.rb_card);

        rbcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected != null)
                {
                    selected.setChecked(false);
                }

                rbcard.setChecked(true);

                CardPojo bean = new CardPojo(cp.get(position).getId().toString(),cp.get(position).getExtraData().toString(),cp.get(position).getPhoneNumber().toString(),cp.get(position).getCardType().toString());
                selectedRadioButton.add(bean);
                selected = rbcard;
                if(cp.get(position).getId().toString().equals("080")){
                    PaymentOption.payBtn.setText("Proceed");
                }else{
                    PaymentOption.payBtn.setText("Proceed");
                }

            }
        });



        return rowView;

    }

}
