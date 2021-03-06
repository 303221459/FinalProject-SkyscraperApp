package com.example.sloan.skyscraperbuilder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.junkai.finalproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * AnimationView - offers a graphical view of a simple animated app
 *     with BallSprites that move around and bounce of the edges of the screen.
 *
 * Created by clintf on 3/29/17.
 * @author Clint Fuchs
 * @date 3/29/2017
 * @email clintf@coastal.edu
 * @course CSCI 343
 */
public class AnimationView extends View {



    private AnimationThread animationThread;

    private static List<Sprite> spriteList;

    private int canvasWidth;
    private int canvasHeight;//ADDED THIS
    private int leftBound;
    private int rightBound;

    Bitmap level = BitmapFactory.decodeResource(getResources(), R.drawable.level);
    Paint buildingPaint = new Paint();

    /**
     * Constructor that initializes this with two BallSprites and starts
     *     and animation thread that moves the BallSprites around the
     *     screen.
     *
     * @param context not null
     * @param attributeSet not null
     */
    public AnimationView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        initAnimationView();
    }

    private void initAnimationView(){

        this.spriteList = new ArrayList<Sprite>();
        //WHAT IS COMMENTED OUT YOU CAN GET RID OF
/**
 Sprite initialBuildingSprite = new BuildingSprite(750, 1000, 0, 0);
 initialBuildingSprite.setSize(BuildingSprite.DEFAULT_WIDTH, BuildingSprite.DEFAULT_HEIGHT);
 this.spriteList.add(initialBuildingSprite);
 Sprite buildingSprite = new BuildingSprite(500, 200, 15, 0);
 buildingSprite.setSize(BuildingSprite.DEFAULT_WIDTH, BuildingSprite.DEFAULT_HEIGHT);

 this.spriteList.add(buildingSprite);
 **/

        this.animationThread = new AnimationThread(this, AnimationThread.DEFAULT_FPS);
        this.animationThread.start();

    }


    /**
     * draws the BallSprites on this View and
     *    updates their position.
     *
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas){
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();//ADDED THIS
        leftBound = canvasWidth / 6;
        rightBound = canvasWidth - (leftBound);

        //ADDED THIS
        if(spriteList.size() == 0) {
            setupGame();
        }

        super.onDraw(canvas);

        Sprite sprite = spriteList.get(spriteList.size() - 1);

        //if the sprite is at the top of the stacked building it will stop the sprite from moving
        if (atBottom(sprite, canvas)) {
            sprite.setVelocity(0, 0);
        }

        //if the stacked building is too tall then it will remove the sprite at the bottom from the
        //arraylist and shift the remaining sprites down
        if (tooTall(canvas)) {
            spriteList.remove(0);
            shiftSpritesDown();
        }

        float xVel = sprite.getxVelocity();
        float yVel = sprite.getyVelocity();

        //if the last sprite in the arraylist is no longer moving then it will check if the game is
        //over if it isnt then it will create a new building level and add it to the arraylist
        if (yVel == 0 && xVel == 0) {
            //checks to see if the last level is out of bounds or if it is more than half off the
            //building level below that one
            if (outOfBounds(sprite) || halfOffPrevious(sprite)) {
                MainActivity.gameOver();
            } else {
                MainActivity.addToScore();
                Sprite newBuildingSprite = new BuildingSprite(canvas.getWidth() / 2, 200, 15, 0, level);
                newBuildingSprite.setSize(BuildingSprite.DEFAULT_WIDTH, BuildingSprite.DEFAULT_HEIGHT);
                spriteList.add(newBuildingSprite);
            }
        }

        drawAllSprites(canvas);

        updateAllSprites();
    }

    /**
     * draws all of Sprites in theList to this View.
     *
     * @param canvas should not be null
     */
    private void drawAllSprites(Canvas canvas){
        for(int index=0; index<this.spriteList.size(); index++){
            Sprite sprite = this.spriteList.get(index);

            sprite.draw(canvas);
        }
    }

    /**
     * updates the location of all of the Sprites' in theList
     */
    private void updateAllSprites(){
        for(int index=0; index<this.spriteList.size(); index++){
            Sprite sprite = this.spriteList.get(index);
            sprite.updateLocation(this.getWidth(),this.getHeight());
        }
    }

    /**
     * recocgnizes a user's touch or tapping of the screen and starts
     * to drop the building block straight down
     * @param motionEvent should not be null
     * @return the parent's onTouchEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){
        Sprite s = spriteList.get(spriteList.size()-1);
        s.setVelocity(0,50);
        return super.onTouchEvent(motionEvent);
    }

    //ADDED THIS
    private void setupGame() {
        BuildingSprite initialSprite = new BuildingSprite(canvasWidth/2, canvasHeight-BuildingSprite.DEFAULT_HEIGHT, 0, 0, level);
        initialSprite.setSize(BuildingSprite.DEFAULT_WIDTH, BuildingSprite.DEFAULT_HEIGHT);
        spriteList.add(initialSprite);
        BuildingSprite firstMovingSprite = new BuildingSprite(canvasWidth/2, 200, 15, 0, level);
        firstMovingSprite.setSize(BuildingSprite.DEFAULT_WIDTH, BuildingSprite.DEFAULT_HEIGHT);
        spriteList.add(firstMovingSprite);
    }

    /**
     * This is a boolean helper method which will determine if the building level the user tapped to
     * drop fell to the top of the previous building level yet, if it has it will return true otherwise
     * if it hasn't fallen far enough it will return false
     *
     * @param sprite The last sprite in the arraylist
     * @param canvas The screen which the animations are happening on
     * @return boolean of whether or not the building block has dropped to the top of the previous block
     */
    private boolean atBottom(Sprite sprite, Canvas canvas) {
        RectF currentLocation = sprite.getRect();
        if(currentLocation.bottom >= (canvas.getHeight()-((spriteList.size()-1)*BuildingSprite.DEFAULT_HEIGHT) - (BuildingSprite.DEFAULT_HEIGHT/18) )) {
            return true;
        }
        return false;
    }

    /**
     * This is a boolean helper method which will determine if the building has become too high that
     * way it prevents the building from going too high on the screen
     *
     * @param canvas The screen which the animations are happening on
     * @return boolean of whether or not the stacked building has gotten too high and the levels need
     * to be shifted down
     */
    private boolean tooTall(Canvas canvas) {
        int buildingHeight = (int)BuildingSprite.DEFAULT_HEIGHT * spriteList.size();
        //sets the max height the building can become to 75% of the height of the phone screen
        int maxHeight = (canvas.getHeight() - (canvas.getHeight()/4));
        if(buildingHeight >= maxHeight) {
            return true;
        }
        return false;
    }

    /**
     * Method that will iterate through all of the building levels except for the one moving at the
     * top and shifts all of them down by the height of a building sprite
     */
    private void shiftSpritesDown() {
        for(int i = 0; i < spriteList.size()-1; i++) {
            Sprite sprite = spriteList.get(i);
            RectF currentLocation = sprite.getRect();
            float spriteBottom = currentLocation.bottom;
            sprite.setLocation(currentLocation.left,spriteBottom);
        }
    }

    /**
     * This is a boolean helper method that will determine if the stacked building has went outside
     * of its bounds either too far to the left or too far to the right and will return true if it
     * has went too far or false if it is still in bounds
     *
     * @param sprite The last sprite in the arraylist
     * @return boolean of whether or not the stacked building has went too far to the left or too
     * far to the right
     */
    private boolean outOfBounds(Sprite sprite) {
        RectF spriteLocation = sprite.getRect();
        if((spriteLocation.left < leftBound) || (spriteLocation.right > rightBound)) {
            return true;
        }
        return false;
    }

    /**
     *boolean helper method that will check if the last building level is more than half off the second
     *to last building level either on the left side or the right side and will return true if it is
     *
     * @param currentSprite The last sprite in the arraylist
     * @return boolean of whether or not the last building level is more than half off the second
     * to last building level
     */
    private boolean halfOffPrevious(Sprite currentSprite) {
        if (spriteList.size() > 1) {
            RectF currentSpriteLocation = currentSprite.getRect();
            Sprite secondToLastSprite = spriteList.get(spriteList.size()-2);
            RectF secondToLastSpriteLocation = secondToLastSprite.getRect();

            boolean offLeftSide = currentSpriteLocation.left < (secondToLastSpriteLocation.left - (BuildingSprite.DEFAULT_WIDTH / 2));
            boolean offRightSide = currentSpriteLocation.right > (secondToLastSpriteLocation.right + (BuildingSprite.DEFAULT_WIDTH/2));

            if (offLeftSide || offRightSide) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Method that will execute if the user is at the game over screen and clicks the play again
     * button. This method will re-initialize the spriteList to an empty arrayList with no building
     * levels in it.
     */
    public static void restartGame() {
        spriteList = new ArrayList<Sprite>();
    }
}
