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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class Tab2 extends Activity implements SensorEventListener{

    public int FOR_GRIDVIEW = 0;
    public int FOR_LISTVIEW = 1;

    private int[] deviceSize = new int[2];

    private SensorManager mSensorManager;
    private Sensor mRotSensor, mAccelSensor;
    private ImageButton fullBtn;

    private GridView gallery;
    private ListView listGallery;
    private ImageAdapter gridAdapter, listAdapter;
    //private Button camBtn, fullBtn, shareBtn;
    private ImageDialog imgDialog;
    private Uri fileUri;

    private int selectedPosition = 0;

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

    }

    public void setViewsListeners() {
        //set views
        gallery = (GridView) findViewById(R.id.galleryGridView);
        gridAdapter = new ImageAdapter(this, FOR_GRIDVIEW, 2);
        gallery.setAdapter(gridAdapter);

        listGallery = (ListView) findViewById(R.id.galleryListView);

        int width = (int) (deviceSize[0] * 0.9);
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
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

        fullBtn = (ImageButton) findViewById(R.id.full_view_button);
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
            else if (pitchDeg < -50) {
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
}