package course.labs.dailyselfie;

// ImageAdapter class to for our listview which holds each ImageItem
// It supports operations to return each row view, add, delete, load all, delete all

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.util.List;

public class ImageAdapter extends ArrayAdapter<ImageItem> {

        public ImageAdapter(Context context, int resource, List<ImageItem> values) {
            super(context, resource, values);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageItem imageItem = getItem(position);

            if( null == convertView ) {

                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_selfie, parent, false);
            }

            TextView textView = (TextView) convertView.findViewById(R.id.label);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
            textView.setText(imageItem.label);
            imageView.setImageBitmap(imageItem.img);

            return convertView;
        }

    public void loadImages(File storageDir) {
        if (storageDir.exists()) {
            for (File file : storageDir.listFiles()) {
                addImage( file);
            }
        }
    }

    public void addImage(File imageFile) {
        if (null != imageFile) {
            add(new ImageItem(imageFile));
        }
    }

    public void deleteImage(int iPos) {
        if( iPos < getCount()) {
            ImageItem item = getItem(iPos);
            File f = new File(item.path);
            f.delete();
            remove(item);
        }
    }

    public void deleteAllImages() {
        for( int i=getCount()-1; i >= 0; i--  ) {
            deleteImage(i);
        }
    }

}
