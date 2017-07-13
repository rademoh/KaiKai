package com.ng.techhouse.tinggqr;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.helper.DBhelper;
import com.ng.techhouse.tinggqr.model.AirtimeBeneficiaryPojo;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rabiu on 04/04/2017.
 */

public class BeneficiaryAdapter extends BaseAdapter{


    private LayoutInflater inflater;
    private final Activity context;
    private List<AirtimeBeneficiaryPojo> abp = new ArrayList<AirtimeBeneficiaryPojo>();
    android.app.FragmentManager fragment;
    DBhelper controller;

    public BeneficiaryAdapter(Activity context, List<AirtimeBeneficiaryPojo> abp) {

        this.abp = abp;
        inflater =  LayoutInflater.from(context);
        this.context = context;
        fragment= context.getFragmentManager();
        controller = new DBhelper(context);

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

      /// final AirtimeBeneficiaryPojo abp = (AirtimeBeneficiaryPojo) getItem(position);

        if (view == null) {
            view = inflater.inflate(R.layout.beneficiary_row, null);
        }

        TextView tvname = (TextView) view.findViewById(R.id.name);
        TextView tvphoneno = (TextView) view.findViewById(R.id.phoneno);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.cover);
        ImageView deleteBtn = (ImageView) view.findViewById(R.id.deleteBtn);

        tvname.setText(abp.get(position).getName().toString());
        tvphoneno.setText(abp.get(position).getPhoneno().toString());
        //tvname.setText(abp.getName().toString());
       // tvphoneno.setText(abp.getPhoneno().toString());


        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context,Airtime1.class);
                i.putExtra("name",abp.get(position).getName().toString());
                i.putExtra("phoneno",abp.get(position).getPhoneno().toString());
                i.putExtra("beneficiary","beneficiary");
                context.startActivity(i);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                controller.delete(abp.get(position).getName().toString());
                abp.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;


    }

    public static void pushFragment(Fragment newFragment, Context context){

        FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container1, newFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public Object getItem(int position) {
        return abp.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        return abp.size();
    }
    public void setBeneficiary(List<AirtimeBeneficiaryPojo> data) {
        abp.addAll(data);
        notifyDataSetChanged();
    }
}
