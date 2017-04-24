package com.example.sloan.skyscraperbuilder;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.junkai.finalproject.HighestScore;
import com.example.junkai.finalproject.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static TextView scoreTextView;
    public static TextView gameOverTextView;
    public static Button replayButton,goScoreButton,saveScoreButton;
    public static LinearLayout gameOverContainer;

    public static LinearLayout enterNameContainer;
    public static Button submitButton;
    public static EditText enterNameEditText;

    private RelativeLayout mainContainer;
    public static int score;
    public boolean fileNotFound;
    public static ArrayList<String> scoreArrayList;
    public static final String TxtName = "score.txt";
    public static Context context;
    private static boolean saveScoreVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fontPath = "fonts/arcade_classic.TTF";
        Typeface arcadeTypeface = Typeface.createFromAsset(getAssets(), fontPath);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;

        gameOverContainer = (LinearLayout)findViewById(R.id.gameOverContainer);
        gameOverContainer.setVisibility(View.INVISIBLE);
        gameOverContainer.getLayoutParams().height = deviceHeight/2;
        gameOverContainer.getLayoutParams().width = deviceWidth/2;

        enterNameContainer = (LinearLayout)findViewById(R.id.enterNameContainer);
        enterNameContainer.setVisibility(View.INVISIBLE);
        enterNameContainer.getLayoutParams().width = deviceWidth/3;
        enterNameContainer.getLayoutParams().height = deviceHeight/3;
        enterNameEditText = (EditText)findViewById(R.id.enterNameEditText);
        submitButton = (Button)findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        scoreTextView = (TextView)findViewById(R.id.scoreTextView);
        score = 0;
        scoreTextView.setText("Score: " + score);
        scoreTextView.setTypeface(arcadeTypeface);

        gameOverTextView = (TextView)findViewById(R.id.gameOverText);
        gameOverTextView.setTypeface(arcadeTypeface);

        replayButton = (Button)findViewById(R.id.playAgainButton);
        replayButton.setTypeface(arcadeTypeface);
        replayButton.setOnClickListener(this);

        goScoreButton = (Button)findViewById(R.id.goScoreButton);
        goScoreButton.setVisibility(View.INVISIBLE);
        goScoreButton.setTypeface(arcadeTypeface);
        goScoreButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HighestScore.class);
                HighestScore.restartButtonName = "RESTART";
                startActivity(intent);
            }

        });

        //Try to create a new txt file for saving score if the txt file haven't been created yet
        //if the txt file exists, it won't do anything
        File scoreTXT = new File(TxtName);
        try {
            scoreTXT.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create an arraylist
        //initialize its value with all 0
        scoreArrayList = new ArrayList<String>();

        for(int i=0;i<5;i++){
            scoreArrayList.add("0");
        }
        Log.d("Array List Size", "onCreate: "+scoreArrayList.size());
        //set Context
        context = this.getApplicationContext();

        //implement saveScoreButton
        //save the score of game only if the users are willing to
        saveScoreButton = (Button)findViewById(R.id.SaveScoreButton);
        saveScoreButton.setVisibility(View.INVISIBLE);
        saveScoreButton.setTypeface(arcadeTypeface);
        saveScoreVisible = true;
        saveScoreButton.setOnClickListener(this);
        /**
        saveScoreButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                readFromFile(scoreArrayList,context);
                writeScoreToFile(scoreArrayList,score,context);
                saveScoreButton.setVisibility(v.INVISIBLE);
                saveScoreVisible=false;
            }
        });
    **/
    }

    public static void addToScore() {
        score++;
        scoreTextView.setText("Score: " + score);
    }

    public static void gameOver() {
        /**
         gameOverTextView.setText("Game Over!");
         replayButton.setVisibility(View.VISIBLE);
         goScoreButton.setVisibility(View.VISIBLE);

         //Error: will keep use this method when gameover
         //reason: keep drawing thread (keep refreshing activity)
         //When the user clicks the saveScore button, the button will disappear to avoid saving score multiple times
         **/
        replayButton.setVisibility(View.VISIBLE);
        gameOverContainer.setVisibility(View.VISIBLE);
        if(saveScoreVisible){
            saveScoreButton.setVisibility(View.VISIBLE);
        }else{
            saveScoreButton.setVisibility(View.INVISIBLE);
        }
        goScoreButton.setVisibility(View.VISIBLE);
        //write score to file if the score is high enough


    }

    /* Called by AnimationView restartGame() method when users click the button "restart"
    ** setup Visibility
    */
    public static void restartGame(){
        saveScoreVisible = true;
        gameOverContainer.setVisibility(View.INVISIBLE);
        goScoreButton.setVisibility(View.INVISIBLE);
        replayButton.setVisibility(View.INVISIBLE);
        saveScoreButton.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();
        if(viewID == replayButton.getId()) {
            score = 0;//added this
            scoreTextView.setText("Score: " + score);//added this
            gameOverTextView.setText("");
            replayButton.setVisibility(View.INVISIBLE);
            gameOverContainer.setVisibility(View.INVISIBLE);
            goScoreButton.setVisibility(View.INVISIBLE);
            replayButton.setVisibility(View.INVISIBLE);
            saveScoreButton.setVisibility(View.INVISIBLE);
            AnimationView.restartGame();
        } else if(viewID == saveScoreButton.getId()) {
            gameOverContainer.setClickable(false);
            enterNameContainer.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        } else if(viewID == submitButton.getId()) {
            enterNameContainer.setVisibility(View.INVISIBLE);
            gameOverContainer.setClickable(true);
            String saveScoreName = enterNameEditText.getText().toString();

            //TODO add in capability to write the persons name to the score file with that score
            //JUNKAI CODE
            readFromFile(scoreArrayList,context);
            writeScoreToFile(scoreArrayList,score,context,saveScoreName);
            saveScoreButton.setVisibility(v.INVISIBLE);
            saveScoreVisible=false;
        }
    }

    @Override
    public void onPause(){
        super.onPause();

    }

    @Override
    public void onDestroy(){
        super.onDestroy();

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
                    sArrayList.set(i,receivedString);
                    Log.d("fileContent", "readFromFile: "+sArrayList.get(i)+"\n");
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

    /**
     * write score information to a permanent txt file
     * @param sArrayList
     * @param score
     * @param context
     */
    public static void writeScoreToFile(ArrayList<String> sArrayList,int score, Context context,String userName){

        int[] temp = new int[sArrayList.size()];
        Scanner in = null;

        //get the score part from the txt file
        for(int i=0;i<sArrayList.size();i++){

            in = new Scanner(sArrayList.get(i)).useDelimiter("[^0-9]+");
            int integer = in.nextInt();
            temp[i]=integer;
            Log.d("array temp[]", "writeScoreToFile: "+temp[i]+"\n");
            in.close();

        }

        int tempVal=0;
        String tempString=null;
        if(score>temp[4]) {

            temp[4]=score;
            sArrayList.set(4,String.format("%-10s%d",userName,score));
            for(int j=0;j<sArrayList.size()-1;j++){
                for(int k=1;k<sArrayList.size()-j;k++){
                    if(temp[k-1]<temp[k]){
                        tempVal = temp[k-1];
                        temp[k-1]=temp[k];
                        temp[k]=tempVal;
                        tempString = sArrayList.get(k-1);
                        sArrayList.set(k-1,sArrayList.get(k));
                        sArrayList.set(k,tempString);
                    }
                }
            }


            //print arraylist
            for(int i=0;i<sArrayList.size();i++){
                Log.d("Arraylistcontent", "writeScoreToFile: "+sArrayList.get(i));
                //Log.d("sizeArrayList", "writeScoreToFile: "+sArrayList.size());
            }


            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("score.txt", Context.MODE_PRIVATE));
                for(int i=0;i<sArrayList.size();i++){
                    //use each line for one score
                    outputStreamWriter.write(sArrayList.get(i)+"\n");
                }
                outputStreamWriter.close();

            } catch (FileNotFoundException e) {
                Log.e("FileNotFoundException", "File write failed: " + e.toString());
            } catch (IOException e) {
                Log.e("IOException", "File write failed: " + e.toString());
            }
            Log.d("end of the log", "writeScoreToFile: "+".......");
        }
    }

}
