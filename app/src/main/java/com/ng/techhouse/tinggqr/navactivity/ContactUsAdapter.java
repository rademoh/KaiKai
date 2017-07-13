package com.ng.techhouse.tinggqr.navactivity;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.R;

/**
 * Created by rabiu on 05/03/2017.
 */

public class ContactUsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final String[] content;
    private final Integer[] imageId;
    public ContactUsAdapter(Activity context, String[] title, String[] content, Integer[] imageId) {
        super(context, R.layout.contact_row, title);
        this.context = context;
        this.title = title;
        this.content = content;
        this.imageId = imageId;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.contact_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtContent = (TextView) rowView.findViewById(R.id.content);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);

        txtTitle.setText(title[position]);
        txtContent.setText(content[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
