package com.example.sangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaplayer.stop();
        mediaplayer.release();
        updateSeek.interrupt();
    }

    TextView tv_songname;
    SeekBar sb_seekbar;
    ImageView iv_previous,iv_play,iv_next;
    ArrayList<File> songs;
    MediaPlayer mediaplayer;
    String textContent;
    int position;
    Thread updateSeek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        tv_songname=findViewById(R.id.tv_songname);
        sb_seekbar=findViewById(R.id.sb_seekbar);
        iv_previous=findViewById(R.id.iv_previous);
        iv_play=findViewById(R.id.iv_play);
        iv_next=findViewById(R.id.iv_next);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        tv_songname.setText(textContent);
        tv_songname.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaplayer = MediaPlayer.create(this,uri);
        mediaplayer.start();
        sb_seekbar.setMax(mediaplayer.getDuration());
        sb_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaplayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while(currentPosition<mediaplayer.getDuration()){
                        currentPosition = mediaplayer.getCurrentPosition();
                        sb_seekbar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            };
        updateSeek.start();
        iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((mediaplayer.isPlaying())){
                    iv_play.setImageResource(R.drawable.play);
                    mediaplayer.pause();
                }
                else{
                    iv_play.setImageResource(R.drawable.pause);
                    mediaplayer.start();
                }
            }
        });
        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (position!=0){
                    position=position-1;
                }
                else{
                    position=songs.size()-1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaplayer.start();
                iv_play.setImageResource(R.drawable.pause);
                sb_seekbar.setMax(mediaplayer.getDuration());
                textContent = songs.get(position).getName().toString();
                tv_songname.setText(textContent);
            }
        });
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaplayer.stop();
                mediaplayer.release();
                if (position!=songs.size()-1){
                    position=position+1;
                }
                else{
                    position=0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaplayer = MediaPlayer.create(getApplicationContext(),uri);
                mediaplayer.start();
                iv_play.setImageResource(R.drawable.pause);
                sb_seekbar.setMax(mediaplayer.getDuration());
                textContent = songs.get(position).getName().toString();
                tv_songname.setText(textContent);
            }
        });
        }
    }
