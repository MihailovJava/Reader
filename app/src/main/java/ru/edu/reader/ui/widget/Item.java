package ru.edu.reader.ui.widget;


import android.view.LayoutInflater;
import android.view.View;

/**
 * Инткрфейс Item описывает методы получения View и ViewType для набора ItemType{HEADER,CONTENT}.
 * Необходимо для реализации ListView с разбиением по категориям.
 */
public interface Item {

    public enum ItemType {HEADER,CONTENT}; // набор типов элементов ListView {Заголовок, Контент}

    /**
     * Метод возврашающий тип элемента
     * @return тип элемента
     */
    public int getViewType();

    /**
     * Метод возвращающий View для элемента
     * @param inflater inflater
     * @param convertView View для заполнения
     * @return заполненый View
     */
    public View getView(LayoutInflater inflater, View convertView);
}
