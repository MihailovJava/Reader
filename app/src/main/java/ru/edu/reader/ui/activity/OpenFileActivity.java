package ru.edu.reader.ui.activity;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


import ru.edu.reader.R;
import ru.edu.reader.controllers.adapters.OpenFileAdapter;

public class OpenFileActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_file);
        ListView filesDirectory = (ListView) findViewById(R.id.files_list);
        OpenFileAdapter adapter = OpenFileAdapter.getInstance(this);
        filesDirectory.setAdapter(adapter
                .setFiles(new ArrayList<>(Arrays.asList(new File(adapter.getPath())))));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (OpenFileAdapter.getInstance(this).onBackPressed())
            super.onBackPressed();
    }
}
