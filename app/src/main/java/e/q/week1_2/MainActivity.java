package e.q.week1_2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] PERMISSIONS = {
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.VIBRATE};

        if (!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this,PERMISSIONS, 1);
            Handler mHandler = new Handler();
            Runnable mMyTask = new Runnable() {
                @Override
                public void run() {
                    doOncreate();
                }
            };
            mHandler.postDelayed(mMyTask, 0);
        }
        else {
            doOncreate();
        }

    }

    public void doOncreate() {
        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent(this, Tab1.class);
        spec = mTab.newTabSpec("Contact").setIndicator("Contact").setContent(intent);
        mTab.addTab(spec);

        intent = new Intent(this, Tab2.class);
        spec = mTab.newTabSpec("Gallery").setIndicator("Gallery").setContent(intent);
        mTab.addTab(spec);

        intent = new Intent(this, Tab3.class);
        spec = mTab.newTabSpec("tab3").setIndicator("Tab 3").setContent(intent);
        mTab.addTab(spec);
    }

    public static boolean hasPermissions(Context context, String... permissions){
        if (context != null && permissions != null) {
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
