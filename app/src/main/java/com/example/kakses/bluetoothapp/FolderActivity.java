package com.example.kakses.bluetoothapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lib.folderpicker.FolderPicker;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class FolderActivity extends AppCompatActivity {
    private String dirPath;
    private static final int FOLDERPICKER_CODE = 1;
    private boolean mReturningWithResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReturningWithResult = true;

        setContentView(R.layout.activity_folder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dirPath != null) {
                    Intent intent = new Intent(getApplicationContext(), SearchBtDevice.class);
                    Bundle b = new Bundle(); //pass the filepath
                    b.putString("path", dirPath);
                    intent.putExtras(b);
                    startActivity(intent);
                } else Toast.makeText(getApplicationContext(), "Select a directory!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void searchPairedDevices(View view) {
        Intent intent = new Intent(this, FolderPicker.class);
        startActivityForResult(intent, FOLDERPICKER_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FOLDERPICKER_CODE && resultCode == Activity.RESULT_OK) {
            dirPath = intent.getExtras().getString("data");
            File file = new File(dirPath);
            Toast.makeText(getApplicationContext(), "Selected: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            getFilesBySizes(file);

        }
    }

    private List<Long> getFilesBySizes(File parentDir) {
        ArrayList<Long> inFiles = new ArrayList<Long>();
        File[] files = parentDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                inFiles.addAll(getFilesBySizes(file));
            } else {
                inFiles.add(file.length());
//                Log.d("I", "Adding: " + file.getName() + ": " + file.length());
            }
        }
        return inFiles;
    }

}
