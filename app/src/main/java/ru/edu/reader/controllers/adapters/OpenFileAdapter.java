package ru.edu.reader.controllers.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import ru.edu.reader.controllers.tasks.SearchFilesTask;
import ru.edu.reader.ui.activity.PreviewActivity;
import ru.edu.reader.ui.activity.ReadActivity;

/**
 * Адаптер для файловой системы андроид
 */
public class OpenFileAdapter extends BaseAdapter implements View.OnClickListener, Comparator<File> {

    private Context context;
    private List<File> files;   // Список файлов в директории
    private File parent;        // Корень директории
    private String path;        // Путь директории
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath(); // Исходная директория
    private static final String REG_EX_FB2 = ".*\\.fb2"; // Регулярное выражения для проверки нужного типа файла
    private static final String REG_EX_EPUB = ".*\\.epub"; // Регулярное выражения для проверки нужного типа файла
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
        if (!me.context.equals(context)) {
            me.context = context;
        }
        return me;
    }

    public List<File> getFiles() {
        return files;
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
            if (file.isFile()) {
                boolean a = file.getName().matches(REG_EX_EPUB);
                boolean b = file.getName().matches(REG_EX_FB2);
                if (!a && !b) {
                    this.files.remove(file);
                }
            }else {
                if (!file.isDirectory()){
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
        return files.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if (position > 0)
            return files.get(position-1);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0){
            convertView = convertView == null ? LayoutInflater.from(context).inflate(R.layout.open_file_item, null) : convertView;
            TextView fileNameText = (TextView) convertView.findViewById(R.id.file_name);
            TextView fileDirText = (TextView) convertView.findViewById(R.id.full_directory);
            fileDirText.setVisibility(View.GONE);
            fileNameText.setText(context.getString(R.string.back_text));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).onBackPressed();
                }
            });
            return convertView;
        }else {
            convertView = convertView == null ? LayoutInflater.from(context).inflate(R.layout.open_file_item, null) : convertView;
            TextView fileNameText = (TextView) convertView.findViewById(R.id.file_name);
            TextView fileDirText = (TextView) convertView.findViewById(R.id.full_directory);
            ImageView fileImage = (ImageView) convertView.findViewById(R.id.file_image);
            File file = (File) getItem(position);

            String fileName = file.getName();
            fileNameText.setText(fileName);

            if (file.isFile()) {
                fileImage.setImageResource(R.drawable.book_img);
                fileDirText.setText(file.getAbsolutePath());
                fileDirText.setVisibility(View.VISIBLE);
            } else {
                fileImage.setImageResource(R.drawable.folder_img);
                fileDirText.setVisibility(View.GONE);
            }
            convertView.setTag(R.integer.tag_file, file);
            convertView.setOnClickListener(this);

            return convertView;
        }
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
                File[] files = file.listFiles();
                me.setFiles(new ArrayList<>(Arrays.asList(files)))
                        .notifyDataSetChanged();
            }else
            if(file.isFile()){
                Intent intent = new Intent(context, PreviewActivity.class);
                intent.putExtra(context.getString(R.string.file_name),file.getAbsolutePath());
                context.startActivity(intent);
            }
            else {
                Toast.makeText(context,R.string.access_denied,Toast.LENGTH_SHORT).show();
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

    public void find(String query) {
        new SearchFilesTask(context).execute(query,ROOT_PATH,REG_EX_EPUB,REG_EX_FB2);
    }




}
