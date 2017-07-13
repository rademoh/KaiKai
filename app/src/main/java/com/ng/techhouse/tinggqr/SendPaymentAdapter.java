package com.ng.techhouse.tinggqr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by rabiu on 26/01/2017.
 */

public class SendPaymentAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final Integer[] imageId;

    public SendPaymentAdapter(Activity context, String[] title, Integer[] imageId) {
        super(context, R.layout.vertical_item_view_test, title);
        this.context = context;
        this.title = title;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.vertical_item_view_test, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.recharge_title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.recharge_img);

        txtTitle.setText(title[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
