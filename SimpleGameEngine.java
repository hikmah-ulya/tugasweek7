package com.example.simplegameengine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SimpleGameEngine<Game> extends AppCompatActivity {


    GameView gameView;

    int Xmax;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView (this);
        setContentView(gameView);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width = size.x;
        height = size.y;
        Xmax = getResources().getDisplayMetrics().widthPixels;

    }

    class GameView extends SurfaceView implements Runnable {

    Thread gameThread = null;

    SurfaceHolder ourHolder;

    volatile boolean playing;

    Canvas canvas;
    Paint paint;

    long fps;

    private long timeThisFrame;

    Bitmap bitmapMario;

    boolean isMoving = false;

    float walkSpeedPerSecond = 150;

    float marioXPosition = 10;

    public GameView (Context context){

        super(context);
        ourHolder = getHolder();
        paint = new Paint();

        bitmapMario = BitmapFactory.decodeResource(this.getResources(),R.drawable.mario);
        playing = true;

    }
    @Override
    public void run (){
        while (playing) {

            long startFrameTime = System.currentTimeMillis();

            update();

            draw ();

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame > 0 ){
                fps = 1000 / timeThisFrame;
            }

        }
    }
    public  void update (){
        if (isMoving){
            if (marioXPosition > Xmax - 100 || marioXPosition < 0){
                walkSpeedPerSecond = - walkSpeedPerSecond;
            }
               marioXPosition = marioXPosition + (walkSpeedPerSecond/fps);
            }

        }

public void draw (){
        if (ourHolder.getSurface().isValid()){
            canvas = ourHolder.lockCanvas();

            canvas.drawColor(Color.argb(255,26,128,172));

            paint.setColor(Color.argb(255,249,129,0));

            paint.setTextSize(50);

            canvas.drawText("FPS : ", 20,40,paint) ;

            canvas.drawBitmap (bitmapMario,marioXPosition,300,paint);

            ourHolder.unlockCanvasAndPost(canvas);

        }
}
public void pause(){

        playing = false;
        try {
            gameThread.join();;
        } catch (InterruptedException e){
            Log.e ("Error:","joining thread");
        }
}
public void resume (){
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();;
}
@Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
    switch (motionEvent.getAction() & motionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:

            isMoving = true;

            break;

        case MotionEvent.ACTION_UP:

            isMoving = false;

            break;

    }
    return true;
}
}
@Override
protected void onPause(){
    super.onPause();

    gameView.pause();
    }
}
