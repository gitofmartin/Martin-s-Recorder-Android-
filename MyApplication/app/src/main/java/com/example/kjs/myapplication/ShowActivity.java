package com.example.kjs.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class ShowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        final ArrayList<String> FilesInFolder = GetFiles("/sdcard/Android/data/com.example.kjs.myapplication/");

        if (FilesInFolder != null) {

            ListView lv;
            lv = (ListView) findViewById(R.id.list_file);
            lv.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, FilesInFolder));

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    MediaPlayer m = new MediaPlayer();

                    try {
                        m.setDataSource("/sdcard/Android/data/com.example.kjs.myapplication/" + FilesInFolder.get(position));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        m.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try{
                        m.start();
                        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer m) {
                            m.release();
                        }
                    });
                }
            });
        }
    }

    public ArrayList<String> GetFiles(String DirectoryPath) {
        ArrayList<String> MyFiles = new ArrayList<String>();

        File f = new File(DirectoryPath);

        f.mkdirs();
        File[] files = f.listFiles();

        if (files.length == 0)
            return null;
        else {
            for (int i=0; i<files.length; i++)
                MyFiles.add(files[i].getName());
        }
        return MyFiles;

    }

    public void backMain(View v){
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
