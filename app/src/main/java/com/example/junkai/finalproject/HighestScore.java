package com.example.junkai.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sloan.skyscraperbuilder.MainActivity;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class HighestScore extends AppCompatActivity{

    Toolbar toolbar;
    MediaPlayer mediaPlayer;
    TextView scoreTextView;
    ListView scoreListView;
    public static Button reStartButton,goMenuButton;
    public static final String TxtName = "score.txt";
    public static ArrayList<String> scoreArrayList;
    public static String restartButtonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highest_score);


        //set the background picture
        Drawable drBack = getResources().getDrawable(R.drawable.sky);
        Bitmap bitmapBack = ((BitmapDrawable) drBack).getBitmap();
        int heightOfScreen = Resources.getSystem().getDisplayMetrics().heightPixels;
        int widthOfScreen = Resources.getSystem().getDisplayMetrics().widthPixels;
        Drawable newDrBack = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmapBack,widthOfScreen,heightOfScreen,true));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setBackground(newDrBack);
        }


        reStartButton = (Button)findViewById(R.id.RestartButton);
        reStartButton.setText(restartButtonName);
        reStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }

        });

        goMenuButton = (Button)findViewById(R.id.goMenuButton);
        goMenuButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),MenuScreen.class);
                startActivity(intent);
            }

        });

        toolbar = (Toolbar)findViewById(R.id.scoreToolbar);
        setSupportActionBar(toolbar);

        // modify the size of drawable being used
        // and then set the icon on the toolbar(NavigationIcon)
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.audioicon);
        final Drawable d = new BitmapDrawable(getResources(),Bitmap.createScaledBitmap(bitmap,100,100,true));
        Bitmap mutedAudioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mutedaudioicon);
        final Drawable mutedAudioDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(mutedAudioBitmap, 100, 100, true));
        toolbar.setNavigationIcon(d);

        // do something when click the audio play button
        // basically stop or continue the music
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    toolbar.setNavigationIcon(mutedAudioDrawable);
                    mediaPlayer.pause();
                }else{
                    toolbar.setNavigationIcon(d);
                    mediaPlayer.start();
                }
            }
        });
        getSupportActionBar().setTitle("");

        //initialize Arraylist
        scoreArrayList = new ArrayList<String>();
        readFromFile(scoreArrayList,this.getApplicationContext());

        //set Text Color of TextView
        scoreTextView = (TextView)findViewById(R.id.ScoreTextView);
        scoreTextView.setTextColor(Color.BLACK);

        //setup scoreListView
        scoreListView = (ListView)findViewById(R.id.ScoreListView);

        //Define Array values to show in ListView
        String[] values = new String[]{"1","2","3","4","5"};

        //Define a new Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,scoreArrayList);

        //Assign Adapter to ListView
        scoreListView.setAdapter(adapter);

        scoreListView.setEnabled(false);


    }

    @Override
    public void onResume(){
        super.onResume();

        //create the backgroud music
        mediaPlayer = MediaPlayer.create(this,R.raw.intro);
        mediaPlayer.start();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this add items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_menuscreen,menu);
        return true;
    }

    /**
     * this method handle the option of menu of Toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here
        int id = item.getItemId();

        switch(id){
            case R.id.task_turnMusic:

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }else{
                    mediaPlayer.start();
                }

                break;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPause(){
        super.onPause();

        //stop and release music (free memory)
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

    }

    /**
     * read score from txt file and save them to the scoreArrayList
     * meanwhile, sort the scoreArrayList from largest to smallest
     * @param context
     */
    public static void readFromFile(ArrayList<String> sArrayList, Context context){

        int i=0;
        try{
            InputStream inputStream = context.openFileInput(TxtName);

            if(inputStream!=null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receivedString = "";

                while((receivedString = bufferedReader.readLine())!=null){
                    sArrayList.add(i,receivedString);
                    i++;
                }
                inputStream.close();
            }

        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }


    }
}
