package ru.edu.reader.ui.activity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

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
        if (adapter.getFiles() == null) {
            filesDirectory.setAdapter(adapter
                    .setFiles(new ArrayList<>(Arrays.asList(new File(adapter.getPath()).listFiles()))));
        } else {
            filesDirectory.setAdapter(adapter);
        }
        if (getIntent().getAction().equals(Intent.ACTION_MAIN))
            handleIntent(getIntent());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_open_file, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchView searchMenuItem = searchView;
                if (searchMenuItem != null) {
                    searchView.onActionViewCollapsed();
                    //searchMenuItem.collapseActionView();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (OpenFileAdapter.getInstance(this).onBackPressed())
            super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query != null && !query.equals(""))
                OpenFileAdapter.getInstance(this).find(query);
        }
    }

}
