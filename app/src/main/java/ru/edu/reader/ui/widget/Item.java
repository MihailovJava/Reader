package ru.edu.reader.ui.widget;


import android.view.LayoutInflater;
import android.view.View;

public interface Item {
    public enum ItemType {HEADER,CONTENT};

    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}
