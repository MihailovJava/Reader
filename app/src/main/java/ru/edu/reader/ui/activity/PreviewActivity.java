package ru.edu.reader.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.edu.reader.R;
import ru.edu.reader.controllers.adapters.PreviewListAdapter;
import ru.edu.reader.ui.widget.Content;
import ru.edu.reader.ui.widget.Header;
import ru.edu.reader.ui.widget.Item;
import ru.edu.reader.util.EBookMetaData;

/**
 * Активность, описывающая мета-данные открываемой книги. В ней пользователь узнает краткую информацию
 * о книге и принимает решение о чтение.
 */
public class PreviewActivity extends ActionBarActivity {

    private String filePath;    // путь до файла

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Intent intent = getIntent();
        filePath = intent.getStringExtra(getString(R.string.file_name));
        EBookMetaData metaData = new EBookMetaData(new File(filePath));
        List<Item> items = new ArrayList<>();
        items.add(new Header(getString(R.string.about)));
        String authorFirstName = metaData.getFirstName();
        String authorLastName = metaData.getLastName();
        String genre = metaData.getGenre();
        String title = metaData.getBookTitle();
        if (authorLastName != null){
            authorFirstName += " " + authorLastName;
        }
        addIfNotNull(authorFirstName,getString(R.string.author),items);
        addIfNotNull(genre,getString(R.string.genre),items);
        addIfNotNull(title,getString(R.string.title),items);

        String annotation = metaData.getAnnotation();
        if (annotation != null){
            items.add(new Header(getString(R.string.annotation)));
            items.add(new Content("",annotation));
        }

        String bookID = metaData.getBookID();
        String programUsed = metaData.getProgramUsed();
        items.add(new Header(getString(R.string.info)));
        addIfNotNull(programUsed,getString(R.string.program_used),items);
        addIfNotNull(filePath,getString(R.string.file_path),items);
        addIfNotNull(bookID,getString(R.string.book_id),items);

        ListView preview = (ListView) findViewById(R.id.preview_list);
        PreviewListAdapter adapter = new PreviewListAdapter(this,items);
        preview.setAdapter(adapter);
    }

    private void addIfNotNull(String value,String key,List<Item> items){
        if (value != null){
            items.add(new Content(key,value));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_read) {
            Intent intent = new Intent(this,ReadActivity.class);
            intent.putExtra(getString(R.string.file_name),filePath);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
