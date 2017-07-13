package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.model.Beanlist;
import com.ng.techhouse.tinggqr.util.M;

import java.util.ArrayList;

/**
 * Created by rabiu on 26/01/2017.
 */

public class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.MyViewHolder>{


    private ArrayList<Beanlist> bean;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public ImageView imgIcon;
        public LinearLayout perView;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = (TextView) view.findViewById(R.id.recharge_title);
            imgIcon = (ImageView) view.findViewById(R.id.recharge_img);
            perView = (LinearLayout) view.findViewById(R.id.item_view);


        }
    }


    public RechargeAdapter(Context context, ArrayList<Beanlist> bean) {
        this.context = context;
        this.bean = bean;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.horizontal_item_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tvTitle.setText(bean.get(position).getTitle());
        holder.imgIcon.setImageResource(bean.get(position).getImage());
        holder.perView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switchRechargeActivity(position);
            }
        });

    }

    private void switchRechargeActivity(int position) {

        switch (position) {
            case 0 :
                Intent intentE = new Intent(context, ElectricityBillers.class);
                context.startActivity(intentE);
                break;
            case 1:
                Intent intentC = new Intent(context, CableBillers.class);
                context.startActivity(intentC);
                break;
            case 2:
                Intent intentI= new Intent(context, InternetBillers.class);
                context.startActivity(intentI);
                break;
            case 3:
               // Intent intentA= new Intent(context, Airtime.class);
               // context.startActivity(intentA);
                Intent i = new Intent(context,Airtime1.class);
                context.startActivity(i);
                break;
            case 4:
               M.DialogBox(context,"Coming Soon");
                break;
            default:
                throw new IllegalArgumentException("Invalid Selection");
        }
    }

    @Override
    public int getItemCount() {
        return bean.size();
    }
}
