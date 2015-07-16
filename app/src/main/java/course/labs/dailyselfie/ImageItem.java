package course.labs.dailyselfie;

// ImageItem data structure to hold each item for list view

import android.graphics.Bitmap;
import java.io.File;

public class ImageItem {
    Bitmap img;
    String label;
    String path;

    public ImageItem(Bitmap bmp, String lbl, String filepath) {
        img = bmp;
        label= lbl;
        path = filepath;
    }

    public ImageItem(File imageFile) {
        img = ImageHelper.getImageThumbnail(imageFile.getAbsolutePath());
        label = imageFile.getName();
        label = label.substring(0, label.lastIndexOf(".")); // Strip ext from filename
        path = imageFile.getAbsolutePath();
    }
}
