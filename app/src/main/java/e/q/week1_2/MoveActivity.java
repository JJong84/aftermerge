package e.q.week1_2;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoveActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mRotSensor;
    private Vibrator vibrator;
    private ImageView img_view;
    private android.support.design.widget.FloatingActionButton saveBtn;

    private int[] deviceSize = new int[2];
    private int[] drawStartPos = new int[2];
    private int[] imgPos = {0, 0};

    private boolean sensorOn = true;

    //for paper
    private ArrayList<Point> points;
    private LinearLayout paper;
    private PaperView paperView;
    private Paint paint;
    private Path path;
    int color = Color.BLACK;

    private String dirName = "/tab3_sketches";

    class PaperView extends View {
        public PaperView(Context context) {
            super(context);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        //get device size
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        deviceSize[0] = dm.widthPixels;
        deviceSize[1] = dm.heightPixels;

        //set sensors+vibrator & find views
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        img_view = (ImageView) findViewById(R.id.imageView);
        saveBtn = (android.support.design.widget.FloatingActionButton) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                captureSave();
            }
        });

        paper = (LinearLayout) findViewById(R.id.draw_linear);
        paint = new Paint();
        path = new Path();
        paperView = new PaperView(this);
        paper.addView(paperView);
        //paper.setDrawingCacheEnabled(false);

        drawStartPos[0] = deviceSize[0] / 2;
        drawStartPos[1] = deviceSize[1] / 2 - 30; //hard coding TT
        path.moveTo(drawStartPos[0], drawStartPos[1]);

        String dirPath = Environment.getExternalStorageDirectory().toString() + dirName;
        File path = new File(dirPath);
        if (! path.isDirectory()) {
            path.mkdirs();
        }
    }

    @SuppressLint("RestrictedApi")
    public void captureSave() {
        img_view.setVisibility(View.INVISIBLE);
        saveBtn.setVisibility(View.INVISIBLE);
        Bitmap captureView = Utils.takeScreenShot(this);
        savePicture(captureView, new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + ".jpg");
        Toast.makeText(getApplicationContext(), "Saved your sketch!", Toast.LENGTH_SHORT).show();
        img_view.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
    }

    private void savePicture(Bitmap bm, String imgName) {
        OutputStream fOut = null;
        String strDirectory = Environment.getExternalStorageDirectory().toString() + dirName;

        File f = new File(strDirectory, imgName);
        try {
            fOut = new FileOutputStream(f);

            //Compress image
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            //Update image to mediaStore
            MediaStore.Images.Media.insertImage(getContentResolver(),
                    f.getAbsolutePath(), "tab3_sketches_title" + imgName, "drawing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveWithAnimation(ImageView imgView, int xPos, int yPos, int duration, int willBuzz) {
        ObjectAnimator ani1 = ObjectAnimator.ofFloat(imgView, "translationX", xPos);
        ObjectAnimator ani2 = ObjectAnimator.ofFloat(imgView, "translationY", yPos);
        ani1.setInterpolator(new LinearInterpolator());
        ani2.setInterpolator(new LinearInterpolator());
        AnimatorSet viewAnimatorSet = new AnimatorSet();
        viewAnimatorSet.playTogether(ani1, ani2);
        viewAnimatorSet.setDuration(duration);
        viewAnimatorSet.start();

        final int buzz = willBuzz;

        Handler mHandler = new Handler();
        Runnable mMyTask = new Runnable() {
            @Override
            public void run() {
                paperView.invalidate();
                sensorOn = true;
                if (buzz == 1) vibrator.vibrate(50);
            }
        };
        mHandler.postDelayed(mMyTask, duration);
    }

    //methods to implement sensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.equals(mRotSensor) && sensorOn) {
            float[] rotationMatrix = new float[9], values = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, values);
            Log.d("orientation", twoArraytoString(new float[] {values[2], values[1]}));

            sensorOn = false;
            //paper.setDrawingCacheEnabled(false);
            float[] cos_sin = getDirectionFromOrientation(values[2], values[1]);
            int[] duration_buzz = setImgPos_getDurationBuzz(cos_sin, 10, 10);
            path.lineTo(imgPos[0] + drawStartPos[0], imgPos[1] + drawStartPos[1]);
            moveWithAnimation(img_view, imgPos[0], imgPos[1], duration_buzz[0], duration_buzz[1]);
        }
    }

    public int[] setImgPos_getDurationBuzz(float[] cos_sin, int distance, int basicDuration) {
        int ivWidth = img_view.getWidth();
        int ivHeight = img_view.getHeight();

        int nextX;
        int nextY;
        int realDuration;
        int willBuzz = 0;

        int xLowerBound = deviceSize[0] * (-1) / 2 + ivWidth / 2;
        int xUpperBound = deviceSize[0] / 2 - ivWidth / 2;
        int yLowerBound = deviceSize[1] * (-1) / 2 + ivHeight;
        int yUpperBound = deviceSize[1] / 2 - ivHeight;

        int tempNextX = (int) (imgPos[0] + distance * cos_sin[0]);
        int tempNextY = (int) (imgPos[1] + distance * cos_sin[1]);

        if (tempNextX < xLowerBound || tempNextX > xUpperBound || tempNextY < yLowerBound || tempNextY > yUpperBound) {
            willBuzz = 1;
        }

        if (tempNextX <= xLowerBound) nextX = xLowerBound;
        else if (tempNextX >= xUpperBound) nextX = xUpperBound;
        else nextX = tempNextX;

        if (tempNextY <= yLowerBound) nextY = yLowerBound;
        else if (tempNextY >= yUpperBound) nextY = yUpperBound;
        else nextY = tempNextY;

        int deltaX = nextX - imgPos[0];
        int deltaY = nextY - imgPos[1];

        imgPos[0] = nextX;
        imgPos[1] = nextY;
        Log.d("nextpos", String.valueOf(imgPos[0])+"  "+String.valueOf(imgPos[1]));
        realDuration = (int) (basicDuration * Math.sqrt(deltaX*deltaX + deltaY*deltaY) / distance);
        int[] retVal = {realDuration, willBuzz};
        return retVal;
    }

    public float[] getDirectionFromOrientation(float xRad, float yRad) {
        final float UPPER_BOUND = (float) Math.PI / 2;
        final float LOWER_BOUND = UPPER_BOUND * (-1);

        float[] cos_sin = {0, 0};
        if (xRad < LOWER_BOUND || xRad > UPPER_BOUND || yRad < LOWER_BOUND || yRad > UPPER_BOUND) {
            Log.d("weird direction", twoArraytoString(cos_sin));
            return cos_sin;
        }

        if (xRad == 0) {
            cos_sin[1] = 1;
            return cos_sin;
        }
        double small_rad = Math.atan(Math.abs(yRad/xRad));
        cos_sin[0] = (float) (getSignOf(xRad) * Math.cos(small_rad));
        cos_sin[1] = (float) (getSignOf(yRad) * Math.sin(small_rad) * (-1));
        Log.d("normal direction", twoArraytoString(cos_sin));
        return cos_sin;
    }

    public float getSignOf(float value) {
        if (value < 0) return -1;
        return 1;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }



    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX();
        float ty = event.getY();

        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                tx = event.getX();
                ty = event.getY();

                //       findViewById(R.id.character).setX(tx-45);
                //      findViewById(R.id.character).setY(ty-134);

                ObjectAnimator animX = ObjectAnimator.ofFloat(img_view, "x", tx-50);
                ObjectAnimator animY = ObjectAnimator.ofFloat(img_view, "y", ty-150);
                AnimatorSet animSetXY = new AnimatorSet();
                animSetXY.playTogether(animX, animY);
                animSetXY.start();

                break;
            default:
        }
        return true;
    } */

    public String twoArraytoString(float[] arr) {
        return (String.valueOf(arr[0])+"   "+String.valueOf(arr[1]));
    }

    public String twoArraytoString(int[] arr) {
        return (String.valueOf(arr[0])+"   "+String.valueOf(arr[1]));
    }

}
