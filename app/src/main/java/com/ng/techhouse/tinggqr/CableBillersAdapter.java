package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by rabiu on 31/01/2017.
 */

public class CableBillersAdapter extends ArrayAdapter<String> {


    private final Activity context;
    private final String[] title;

    public CableBillersAdapter(Activity context, String[] title) {
        super(context, R.layout.cablebillers_row, title);
        this.context = context;
        this.title = title;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.cablebillers_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        txtTitle.setText(title[position]);

        return rowView;
    }
}
