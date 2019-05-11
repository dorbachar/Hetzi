package com.example.hetzi_beta.BusinessApp.ShopSettings;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hetzi_beta.R;

public class ShopSettingsAdapter extends ArrayAdapter {
    private Activity context;
    private String[] titles;
    private String[] details;
    private int list_item_resId;

    @Override
    public int getCount() {
        return titles.length;
    }

    public ShopSettingsAdapter(Activity context, String[] titles, String[] details, int item) {
        super(context, item);


        this.context = context;
        this.titles = titles;
        this.details = details;
        this.list_item_resId = item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(list_item_resId, parent, false);

        TextView title_TextView = rowView.findViewById(R.id.setting_title);
        title_TextView.setText(titles[position]);

        TextView details_TextView   = rowView.findViewById(R.id.setting_details);
        if(details != null) details_TextView.setText(details[position]);
        else details_TextView.setText("");

        return rowView;
    }

}
