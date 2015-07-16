package course.labs.dailyselfie;

// Image Helper class offers utility methods to create image files, scale bitmaps and get thumbnails

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final int THUMB_SIZE = 50;


    // Standard storage location for digital camera files
    private static final String CAMERA_DIR = "/dcim/";

    private static File getAlbumStorageDir(String albumName) {
        return new File(
                Environment.getExternalStorageDirectory()
                        + CAMERA_DIR
                        + albumName
        );
    }


    public static File createAlbumDir(String albumName) {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(albumName);

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(albumName, "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public static File createImageFile(File storageDir) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = storageDir.getAbsolutePath() + "/" + JPEG_FILE_PREFIX + timeStamp + JPEG_FILE_SUFFIX;
        File f = new File(imageFileName);
        f.createNewFile();
        return f;
    }


    public static Bitmap getTargetBitmap(String sourceImageFilePath, int targetW, int targetH) {

		/* There isn't enough memory to open up more than a couple full size camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceImageFilePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        return BitmapFactory.decodeFile(sourceImageFilePath, bmOptions);
    }

    public static Bitmap getImageThumbnail(String photoPath) {
        // Return a small size bitmap
        return getTargetBitmap(photoPath, THUMB_SIZE, THUMB_SIZE);
     }

}
