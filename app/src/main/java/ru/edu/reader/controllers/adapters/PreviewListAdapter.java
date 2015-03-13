package ru.edu.reader.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.edu.reader.ui.widget.Item;


public class PreviewListAdapter extends ArrayAdapter<Item> {

    List<Item> values;
    Context context;

    public PreviewListAdapter(Context context,List<Item> values){
        super(context,0,values);
        this.values = values;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public int getViewTypeCount() {
        return Item.ItemType.values().length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(LayoutInflater.from(context),convertView);
    }
}
