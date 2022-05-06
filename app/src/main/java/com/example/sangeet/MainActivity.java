package com.example.sangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_list=findViewById(R.id.lv_list);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText(MainActivity.this, "Runtime permission", Toast.LENGTH_SHORT).show();
                        ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        String[] items = new String[mySongs.size()];
                        for (int i = 0; i < mySongs.size(); i++) {
                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, items);
                        lv_list.setAdapter(adapter);
                        lv_list.setOnItemClickListener((adapterView, view, i, l) -> {
                            Intent I = new Intent(MainActivity.this,PlaySong.class);
                            String currentSong = lv_list.getItemAtPosition(i).toString();
                            I.putExtra("songList", mySongs);
                            I.putExtra("currentSong", currentSong);
                            I.putExtra("position", i);
                            startActivity(I);
                        });
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }
    public ArrayList<File> fetchSongs(File file) {
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if (songs != null) {
            for (File myFile : songs) {
                if (!myFile.isHidden() && myFile.isDirectory())
                    arrayList.addAll(fetchSongs(myFile));
                else {
                    if (myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith("."))
                        arrayList.add(myFile);
                }
            }
        }
        else{
            Toast.makeText(MainActivity.this,"Files not found",Toast.LENGTH_SHORT).show();
        }
        return arrayList;
    }

}