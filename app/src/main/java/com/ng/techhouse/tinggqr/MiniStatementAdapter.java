package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.model.StatementlistPoJo;
import com.ng.techhouse.tinggqr.util.M;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by rabiu on 20/02/2017.
 */

public class MiniStatementAdapter extends ArrayAdapter<StatementlistPoJo> {

    private final Activity context;
    private ArrayList<StatementlistPoJo> sp;

    public MiniStatementAdapter(Activity context, ArrayList<StatementlistPoJo> sp) {
        super(context, R.layout.ministatement_row, sp);
        this.context = context;
        this.sp = sp;


    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.ministatement_row, null, true);

        TextView tvaction,tvtransid, tvamount,tvdate;
        ImageView tvstatus;

        tvaction = (TextView) rowView.findViewById(R.id.action);
        tvaction.setText(sp.get(position).getAction().toString());

        tvstatus = (ImageView) rowView.findViewById(R.id.status);
        if(sp.get(position).getStatus().toString().equalsIgnoreCase("Successful")){
           // ivCardImage.setImageResource(R.drawable.master);
            tvstatus.setImageResource(R.drawable.ic_green_dot);
            /*tvstatus.setText(sp.get(position).getStatus().toString());
            tvstatus.setTextColor(Color.parseColor("#69db85"));*/
        } else{
            tvstatus.setImageResource(R.drawable.ic_red_dot);
            /*tvstatus.setText(sp.get(position).getStatus().toString());
            tvstatus.setTextColor(Color.parseColor("#E53935"));*/
        }
        tvtransid = (TextView) rowView.findViewById(R.id.transid);
        tvtransid.setText(sp.get(position).getTransactionId().toString());

        tvamount = (TextView) rowView.findViewById(R.id.amount);
        tvamount.setText(sp.get(position).getAmount().toString());

        tvdate = (TextView) rowView.findViewById(R.id.date);
        if(!sp.get(position).getDate().toString().equals(null)) {
            Timestamp ts = M.convertStringToTimestamp(sp.get(position).getDate().toString());
            tvdate.setText(M.timestampToMonthDay(ts).toUpperCase());
        } else {
            tvdate.setText(sp.get(position).getDate().toString());
        }

        return rowView;
    }
}
