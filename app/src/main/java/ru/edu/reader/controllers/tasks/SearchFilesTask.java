package ru.edu.reader.controllers.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.edu.reader.R;
import ru.edu.reader.controllers.adapters.OpenFileAdapter;

//TODO Расставить комменты
public class SearchFilesTask extends AsyncTask<String,Void,List<File>> {

    private Context context;
    private ProgressDialog dialog;

    public SearchFilesTask(Context context){
        this.context = context;
        dialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setTitle(context.getString(R.string.search_dialog_title));
        dialog.show();
    }

    @Override
    protected List<File> doInBackground(String... params) {
        List<File> result = new ArrayList<>();
        StringBuilder filter = new StringBuilder(".*").append(params[0])
                .append(params[2]).append("||").append(".*").append(params[0]).append(params[3]);
        search(filter.toString(),new File(params[1]),result);
        return result;
    }

    @Override
    protected void onPostExecute(List<File> files) {
        super.onPostExecute(files);
        if (files.size() > 0){
            OpenFileAdapter.getInstance(context).setFiles(files).notifyDataSetChanged();
            ((Activity) context).setTitle(context.getString(R.string.search_title));
        }else {
            Toast.makeText(context, R.string.nothing_found, Toast.LENGTH_SHORT).show();
        }
        if (dialog.isShowing())
            dialog.dismiss();
    }

    private void search(String query,File file,List<File> result){
        for (File innerFile : file.listFiles()){
            if(innerFile.isFile()){
                String name = innerFile.getName();
                if (name.matches(query)){
                    result.add(innerFile);
                }
            }else {
                try {
                    search(query,innerFile,result);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
