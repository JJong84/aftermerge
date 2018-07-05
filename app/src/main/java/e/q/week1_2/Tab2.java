package e.q.week1_2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Tab2 extends Activity implements SensorEventListener{

    public int FOR_GRIDVIEW = 0;
    public int FOR_LISTVIEW = 1;

    private int[] deviceSize = new int[2];

    private SensorManager mSensorManager;
    private Sensor mRotSensor, mAccelSensor;

    private GridView gallery;
    private ListView listGallery;
    private ImageAdapter gridAdapter, listAdapter;
    private Button camBtn, fullBtn, shareBtn;
    private ImageDialog imgDialog;
    private Uri fileUri;

    private int selectedPosition = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);

        //get device size
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        deviceSize[0] = dm.widthPixels;
        deviceSize[1] = dm.heightPixels;

        setViewsListeners();

        //set sensors
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //set buttons
        camBtn = (Button) findViewById(R.id.camButton);
        shareBtn = (Button) findViewById(R.id.shareButton);


        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // 아래 정의한 capture한 사진의 저장 method를 실행 한 후
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
                // 먼저 선언한 intent에 해당 file 명의 값을 추가로 저장한다.
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                // start the image capture Intent
                // 해당 intent를 시작한다.
                startActivityForResult(intent, 1);
            }
        });

    }

    public void setViewsListeners() {
        //set views
        gallery = (GridView) findViewById(R.id.galleryGridView);
        gridAdapter = new ImageAdapter(this, FOR_GRIDVIEW, 2);
        gallery.setAdapter(gridAdapter);

        listGallery = (ListView) findViewById(R.id.galleryListView);

        int width = (int) (deviceSize[0] * 0.7);
        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(width, width * 4 / 3);
        listGallery.setLayoutParams(params);
        listAdapter = new ImageAdapter(this, FOR_LISTVIEW, 2);
        listGallery.setAdapter(listAdapter);

        gallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                selectedPosition = position;
                listGallery.setSelection(position);
                Log.d("show position", String.valueOf(position));
            }
        });

        imgDialog = new ImageDialog(this, deviceSize);

        fullBtn = (Button) findViewById(R.id.fullViewButton);
        fullBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setImgDialog(imgDialog, gridAdapter);
                imgDialog.show();
            }
        });
    }

    //methods to implement sensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.equals(mRotSensor)) {
            float[] rotationMatrix = new float[9], values = new float[3];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, values);

            double pitchDeg = values[1] *180 / Math.PI;
            Log.d("pitch degree", String.valueOf(pitchDeg));
            if (pitchDeg > 10) {
                Log.d("scroll by pitch", "go down");
                listGallery.smoothScrollBy(1000, 400);
            }
            else if (pitchDeg < -40) {
                Log.d("scroll by pitch", "go up");
                listGallery.smoothScrollBy(-1000, 400);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotSensor, SensorManager.SENSOR_DELAY_NORMAL);
        setViewsListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    //for dialog
    public void setImgDialog(ImageDialog imgDialog, ImageAdapter adapter) {
        Log.d("dialog debug", "showImagDialog called");
        ImageView fullImgView = imgDialog.getFullImgView();
        if (selectedPosition >= 0) {
            Log.d("dialog debug", String.valueOf(selectedPosition));
            adapter.setImageView(fullImgView, selectedPosition);
        }
    }

    //for camera

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        // 외부 저장소에 이 App을 통해 촬영된 사진만 저장할 directory 경로와 File을 연결
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){ // 해당 directory가 아직 생성되지 않았을 경우 mkdirs(). 즉 directory를 생성한다.
            if (! mediaStorageDir.mkdirs()){ // 만약 mkdirs()가 제대로 동작하지 않을 경우, 오류 Log를 출력한 뒤, 해당 method 종료
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        // File 명으로 file의 생성 시간을 활용하도록 DateFormat 기능을 활용
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile; // 생성된 File valuable을 반환
    }


}