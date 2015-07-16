package course.labs.dailyselfie;

// Daily Selfie App: Developed as a part of coursework for Android Mobile Development from cousera
// -Allows you to take and manage your selfies
// -Automatically sets up reminder every two minutes for you to take your selfie!

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.content.Intent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class SelfieActivity extends ListActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageAdapter mAdapter;
    private File mStorageDir;
    File mCurrentPhotoFile;
    private AlarmManager mAlarmManager;
    private PendingIntent mNotificationReceiverPendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup selfie list view
        mStorageDir = ImageHelper.createAlbumDir(getString(R.string.app_name));

        mAdapter = new ImageAdapter(this, R.layout.activity_selfie, new ArrayList<ImageItem>());
        mAdapter.loadImages(mStorageDir);
        mAdapter.setNotifyOnChange(false);
        setListAdapter(mAdapter);

        //Setup item context menu
        registerForContextMenu(getListView());

        // Setup alarm related services

        // Get the AlarmManager Service
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Create an PendingIntent that using an intent to broadcast to the AlarmNotificationReceiver
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                SelfieActivity.this, 0, new Intent(SelfieActivity.this, AlarmNotificationReceiver.class), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        createAlarm();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selfie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Handle each action bar case

        if (id == R.id.action_selfie) { // Take Selfie
            dispatchTakePictureIntent();
            return true;
        }
        else if( id == R.id.action_setalarm) { // Setup reminder to take selfie
            createAlarm();
            return true;
        }
        else if( id == R.id.action_cancelalarm) { // Cancel reminder to take selfie
            cancelAlarm();
            return true;
        }
        else if( id == R.id.action_deleteallselfies) { // Delete all selfies
            mAdapter.deleteAllImages();
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // Show the selected image in large view using default image viewer
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + ((ImageItem)getListAdapter().getItem(position)).path), "image/*");

        // Launch the Activity using the intent
        startActivity(intent);
    }

    // Item Context Menu
    /** This will be invoked when an item in the listview is long pressed */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_item_context, menu);
    }

    /** This will be invoked when a context menu item is selected */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();

        if( item.getItemId() == R.id.item_cntxt_delete ) {
            mAdapter.deleteImage(info.position);
            mAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
}

    // Alarms

    private void createAlarm() {
        final long ALARM_INTERVAL = 2 * 60 * 1000L;

        // Set repeating alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                ALARM_INTERVAL,
                mNotificationReceiverPendingIntent);
    }

    private void cancelAlarm() {
        // Cancel all alarms set with our pending intent
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);
        mNotificationReceiverPendingIntent.cancel();
    }

    // Camera Actions

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Setup to recieve captured selfie in a file
        File f = null;

        try {
            f = ImageHelper.createImageFile(mStorageDir);
            mCurrentPhotoFile = f;
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoFile = null;
        }

        // Launch camera
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Camera has captured the selfie, add the selfie to our list view
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (mCurrentPhotoFile != null) {
                mAdapter.addImage(mCurrentPhotoFile);
                mAdapter.notifyDataSetChanged();

                mCurrentPhotoFile = null;
            }
        }
    }


}

