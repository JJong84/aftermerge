package e.q.week1_2;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class ImageDialog extends Dialog {
    Button closeBtn;
    //Button deleteBtn;
    ImageView fullImgView;

    public ImageDialog(Context context, int[] deviceSize) {
        super(context);
        setContentView(R.layout.image_dialog);
        closeBtn = findViewById(R.id.close_button);
        //deleteBtn = findViewById(R.id.delete_button);
        fullImgView = findViewById(R.id.full_imageview);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();  //close dialog
            }
        });

        //set dialog w,h
        WindowManager.LayoutParams wm = this.getWindow().getAttributes();
        wm.copyFrom(this.getWindow().getAttributes());
        wm.width = (int) (deviceSize[0] * 0.95);
        wm.height = (int) (deviceSize[1] * 0.95);
    }

    public void performDelete(String path) {

    }



    public ImageView getFullImgView() {
        return fullImgView;
    }
}
