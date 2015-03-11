package ru.edu.reader.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ru.edu.reader.R;

/**
 * Адаптер для файловой системы андроид
 */
public class OpenFileAdapter extends BaseAdapter implements View.OnClickListener, Comparator<File> {

    private Context context;
    private List<File> files;   // Список файлов в директории
    private File parent;        // Корень директории
    private String path;        // Путь директории
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); // Исходная директория
    private static final String REG_EX = ".*\\.fb2||.*\\.epub"; // Регулярное выражения для проверки нужного типа файла

    private static OpenFileAdapter me; // Реализация паттерна одиночки

    /**
     * Закрытый конструктор для реализации одиночки
     * @param context контекст
     */
    private OpenFileAdapter(Context context){
        this.context = context;
        path = ROOT_PATH;
    }

    /**
     * Статический метод получения экземпляра собственного лкасса. Оеализация паттерна одиночка
     * @param context контекст
     * @return одиночка
     */
    public static OpenFileAdapter getInstance(Context context){
        if (me == null){
            me = new OpenFileAdapter(context);
        }
        me.context = context;
        return me;
    }

    public String getPath() {
        return path;
    }

    /**
     * Установка новой директории, а также изменении заголовка активности
     * @param files файлы директории
     * @return одиночка
     */
    public OpenFileAdapter setFiles(List<File> files) {
        File file =  filterAndSort(files);
        if (file != null){
            parent = file.getParentFile();
            if (parent != null)
                ((Activity) context).setTitle(parent.getAbsolutePath());
        } else {
            if (parent != null)
                ((Activity) context).setTitle(parent.getAbsolutePath());
        }
        return me;
    }

    /**
     * Сортировка и фильтрация текущей директории
     * @param files файлы директории
     * @return Отфильтрованная директория
     */
    private File filterAndSort(List<File> files) {
        this.files = new ArrayList<>(files);
        Collections.sort(this.files,this);
        File file = null;
        for (File fromFiles : files) {
            file = fromFiles;
            if (file.isFile())
                if (!file.getName().matches(REG_EX)) {
                    this.files.remove(file);
                } else {
                    if (!file.canExecute()) {
                        this.files.remove(file);
                    }
                }
        }
        return file;
    }

    /*
       Далее идут перекрытые методы родительского класса BaseAdapter
     */

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = convertView == null ? LayoutInflater.from(context).inflate(R.layout.open_file_item,parent) : convertView;
        TextView fileText = (TextView) convertView.findViewById(R.id.file_name);
        ImageView fileImage = (ImageView) convertView.findViewById(R.id.file_image);
        File file = (File) getItem(position);

        String fileName = file.getName();
        fileText.setText(fileName);

        if(file.isFile()){
            fileImage.setImageResource(R.drawable.book_img);
        }else {
            fileImage.setImageResource(R.drawable.folder_img);
        }
        convertView.setTag(R.integer.tag_file,file);
        convertView.setOnClickListener(this);
        return convertView;
    }

    /*
        Реализация метода интерфейса View.OnClickListener для перехода в следующую директорию
        или открытия файла
     */
    @Override
    public void onClick(View v) {
        try {
            File file = (File) v.getTag(R.integer.tag_file);
            if (file.isDirectory()){
                parent = file;
                path = file.getPath();
                me.setFiles(new ArrayList<>(Arrays.asList(file.listFiles())))
                        .notifyDataSetChanged();
            }else {
                //TODO Добавить переход на активити чтения
            }
        }catch (NullPointerException e){
            Toast.makeText(context,R.string.access_denied,Toast.LENGTH_SHORT).show();
        }

    }

    /*
        Метод реализующий возврат в корневую директорию
     */
    public boolean onBackPressed() {
        File grandParent = parent.getParentFile();
        if (grandParent != null){
            me.setFiles(new ArrayList<>(Arrays.asList(grandParent.listFiles())))
                    .notifyDataSetChanged();
        }
        return grandParent == null;
    }

    /*
       Реализация метода интерфейса Comparator<File> для сортировки
     */
    @Override
    public int compare(File lhs, File rhs) {
        if (lhs.isDirectory() && rhs.isFile())
            return -1;
        else if (lhs.isFile() && rhs.isDirectory())
            return 1;
        else
            return lhs.getPath().compareTo(rhs.getPath());
    }
}
