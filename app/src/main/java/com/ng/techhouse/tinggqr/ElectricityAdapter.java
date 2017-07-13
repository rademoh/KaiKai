package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.model.RechargePoJo;

import java.util.ArrayList;


/**
 * Created by rabiu on 25/01/2017.
 */

public class  ElectricityAdapter extends ArrayAdapter<RechargePoJo> {

    private final Activity context;
   // private final String[] title;
    private ArrayList<RechargePoJo> rp;

    public ElectricityAdapter(Activity context, ArrayList<RechargePoJo> rp) {
        super(context, R.layout.recharge_row, rp);
        this.context = context;
        this.rp = rp;


    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.recharge_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.recharge_name);


        txtTitle.setText(rp.get(position).getPaymentItemName().toString());
        return rowView;
    }
}
