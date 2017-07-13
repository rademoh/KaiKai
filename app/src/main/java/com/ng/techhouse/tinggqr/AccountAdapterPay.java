package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.model.BankAccountPojo;
import com.ng.techhouse.tinggqr.model.CardPojo;

import java.util.ArrayList;


/**
 * Created by rabiu on 25/01/2017.
 */

public class AccountAdapterPay extends ArrayAdapter<BankAccountPojo> {


    private final Activity context;
    private ArrayList<BankAccountPojo> bp;
    public static RadioButton selected=null;
    public static ArrayList<BankAccountPojo> selectedRadioButton;




    public AccountAdapterPay(Activity context, ArrayList<BankAccountPojo> bp) {
        super(context, R.layout.card_details_row_pay, bp);
        this.context = context;
        this.bp = bp;
        selectedRadioButton = new ArrayList<BankAccountPojo>();

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.card_details_row_pay, null, true);

        TextView extradata = (TextView) rowView.findViewById(R.id.extra_data);
        extradata.setText(bp.get(position).getAccountNo());
        extradata.setTextSize(16);

        ImageView ivCardImage = (ImageView) rowView.findViewById(R.id.cardimage);
        String cardType =  bp.get(position).getBankCode();
        if((cardType.equalsIgnoreCase("000015"))){
            ivCardImage.setImageResource(R.drawable.zenith);
        } else if (cardType.equalsIgnoreCase("000014")){
            ivCardImage.setImageResource(R.drawable.access);
        } else if (cardType.equalsIgnoreCase("000003")){
            ivCardImage.setImageResource(R.drawable.fcmb);
        } else if (cardType.equalsIgnoreCase("000011")){
            ivCardImage.setImageResource(R.drawable.unitybank);
        }else if (cardType.equalsIgnoreCase("000018")){
            ivCardImage.setImageResource(R.drawable.union);
        } else if (cardType.equalsIgnoreCase("000008")){
            ivCardImage.setImageResource(R.drawable.skyebank);
        }   else {
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

                BankAccountPojo bean = new BankAccountPojo(bp.get(position).getAccountNo(),bp.get(position).getBankCode());
                selectedRadioButton.add(bean);
                selected = rbcard;

            }
        });


        return rowView;

    }

}
