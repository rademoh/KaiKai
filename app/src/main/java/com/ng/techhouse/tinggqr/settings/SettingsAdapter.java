package com.ng.techhouse.tinggqr.settings;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.techhouse.tinggqr.R;


/**
 * Created by rabiu on 25/01/2017.
 */

public class SettingsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] title;
    private final Integer[] imageId;
    public SettingsAdapter(Activity context, String[] title, Integer[] imageId) {
        super(context, R.layout.settings_row, title);
        this.context = context;
        this.title = title;
        this.imageId = imageId;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.settings_row, null, true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.settins_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.list_image);

        txtTitle.setText(title[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}
