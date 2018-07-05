package e.q.week1_2;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    public int FOR_GRIDVIEW = 0;
    public int FOR_LISTVIEW = 1;

    private ArrayList<String> images;
    private Activity context;
    private int purpose;
    private int[] deviceSize = new int[2];


    public ImageAdapter(Activity localContext, int purpose, int tabIndex) {
        context = localContext;
        this.purpose = purpose;
        images = getAllShownImagesPath(context, tabIndex);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        deviceSize[0] = dm.widthPixels;
        deviceSize[1] = dm.heightPixels;
    }

    public int getCount() {
        return images.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        ImageView picturesView;
        if (convertView == null) {
            picturesView = new ImageView(context);
            picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            picturesView = (ImageView) convertView;
        }

        Log.d("purpose", String.valueOf(purpose));

        if (purpose == FOR_GRIDVIEW) {
            Glide.with(context).load(images.get(position)).centerCrop()
                    .into(picturesView);
            picturesView
                    .setLayoutParams(new GridView.LayoutParams((int) (deviceSize[0] / 3 * 0.8), (int) (deviceSize[0] / 3 * 0.8)));
        } else {
            com.bumptech.glide.DrawableTypeRequest glideRequest = Glide.with(context).load(images.get(position));
            glideRequest.centerCrop().into(picturesView);
            int width = (int) (deviceSize[0] * 0.9);
            ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
            picturesView.setLayoutParams(params);
        }
        return picturesView;
    }


    private ArrayList<String> getAllShownImagesPath(Activity activity, int passedTabIndex) { //get all images paths
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        //Log.d("tabIndex", String.valueOf(passedTabIndex));

        if (passedTabIndex == 2) {
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        }
        else { //tabIndex == 3
            cursor = activity.getContentResolver().query(uri, projection, MediaStore.Images.Media.TITLE + " LIKE ?",
                    new String[]{"%tab3_sketches_title%"}, null);
        }

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);
            Log.d("filename", absolutePathOfImage);
            if (passedTabIndex == 2) {
                if (!absolutePathOfImage.contains("/Pictures/")) {
                    listOfAllImages.add(absolutePathOfImage);
                }
            }
            else {
                listOfAllImages.add(absolutePathOfImage);
            }

        }
        return listOfAllImages;
    }


    public void setImageView(ImageView imageView, int position) {
        Glide.with(context).load(images.get(position))
                .into(imageView);
        Log.d("dialog debug", "setImageView called");
    }

    public void deleteImg(int position) {
        File file = new File(images.get(position));
        file.delete();
    }
}