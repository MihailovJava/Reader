package ru.edu.reader.ui.widget;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ru.edu.reader.R;

/**
 * Created by Nixy on 13.03.2015.
 */
public class Header implements Item {

    private String name;

    public Header(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return ItemType.HEADER.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.header_item,null);
        }
        TextView headerText = (TextView) convertView.findViewById(R.id.header_name);
        headerText.setText(name);
        return convertView;
    }
}
