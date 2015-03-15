package ru.edu.reader.ui.widget;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;



import ru.edu.reader.R;

/**
 * Класс Content. Расишряет Item. Описывает контент для ListView С категориями.
 */
public class Content implements Item {

    private String key;
    private String value;

    /**
     * Конструктор класса
     * @param key ключ элемента
     * @param values значение элемента
     */
    public Content(String key, String values) {
        this.key = key;
        this.value = values;
    }

    @Override
    public int getViewType() {
        return ItemType.CONTENT.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.content_item,null);
        }
        TextView keyText = (TextView) convertView.findViewById(R.id.key_text);
        TextView valueText = (TextView) convertView.findViewById(R.id.value_text);

        if (key == null || key.trim().equals("")){
            keyText.setVisibility(View.GONE);
        }else {
            keyText.setText(key);
        }
        if (value == null || value.trim().equals("") ){
            valueText.setVisibility(View.INVISIBLE);
        }else {
            valueText.setText(value);
        }

        return convertView;
    }
}
