package e.q.week1_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tab3 extends Activity {

    public int FOR_GRIDVIEW = 0;
    public int FOR_LISTVIEW = 1;

    private Button startBtn;
    private Button gravityStartBtn;
    private GridView sketches;
    private ImageAdapter gridAdapter;
    private ImageDialog imgDialog;

    private int[] deviceSize = new int[2];

    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab3);

        //get device size
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        deviceSize[0] = dm.widthPixels;
        deviceSize[1] = dm.heightPixels;

        startBtn = findViewById(R.id.paint_start_button);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Tab3.this, MoveActivity.class);
                startActivity(intent);
            }
        });
        gravityStartBtn = findViewById(R.id.gravity_start_button);
        gravityStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Tab3.this, GravityActivity.class);
                startActivity(intent);
            }
        });

        setViewsListeners();
    }

    public void setViewsListeners() {
        //set views
        sketches = (GridView) findViewById(R.id.sketches_gridview);
        gridAdapter = new ImageAdapter(this, FOR_GRIDVIEW, 3);
        sketches.setAdapter(gridAdapter);

        imgDialog = new ImageDialog(this, deviceSize);

        sketches.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                selectedPosition = position;
                setImgDialog(imgDialog, gridAdapter);
                imgDialog.show();
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        setViewsListeners();
    }


}