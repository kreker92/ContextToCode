import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.media.MediaPlayer;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;
import android.content.pm.UserInfo;

public class Example {
    /***************************************
     *
     * Global Variables
     *
     ***************************************/
    ContentResolver			contentResolver = null;
    Cursor					songCursor = null;
    Cursor					albumCursor = null;
    boolean					SHUFFLE = false;
    MediaPlayer				mediaPlayer = null;
    boolean					waitingOnCall = false;
    NotificationManager 	notificationManager = null;
    Notification			notification = null;
    RemoteViews 			contentView = null;
    long					playlist = constants.PLAYLIST_ALL;
    int						recentPeriod = constants.RECENT_PERIOD_DEFAULT_IN_DAYS;
    boolean					isPaused = true;
    Context                 context;
    Drawable                icon;
    String                  name;


    /***************************************
     *
     * Service Interface Stub declaration
     *
     ****************************************/
    private final PlayerServiceInterface.Stub mBinder =
            new PlayerServiceInterface.Stub()
            {
                public void	play(int albumCursorPosition, int songCursorPosition){

                    try{
                        albumCursor.moveToPosition(albumCursorPosition);
                        /*initializeSongCursor(albumCursor.getString(albumCursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));*/
//                        songCursor.moveToPosition(songCursorPosition);
//                        String songPath = songCursor.getString(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    } catch (CursorIndexOutOfBoundsException e){
                        e.printStackTrace();
                        return;
                    }


                    Log.i("SVC","playing "+albumCursorPosition+" "+songCursorPosition);

                    UserInfo info = um.getUserInfo(sipper.userId);
                    if (info != null) {
                        icon = Utils.getUserIcon(context, um, info);
                        name = info != null ? info.name : null;
                        if (name == null) {
                            name = Integer.toString(info.id);
                        }
                        name = context.getResources().getString(R.string.running_process_item_user_label, name);
                    } else {
                        icon = null;
                        name = context.getResources().getString(R.string.running_process_item_removed_user_label);
                    }

                    /*
                     * show notification
                     */
                    if(mediaPlayer.isPlaying()){
                        /*
                         * Show notification
                         */
                        if(notificationManager != null){
                            //TODO: takes this initialization into a sharedpreferences listener

                            contentView.setTextViewText(R.id.trackNameNotification, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                            contentView.setTextViewText(R.id.albumNameNotification, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                            contentView.setTextViewText(R.id.artistNameNotification, songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                            notification.contentView = contentView;
                            Log.i("SVC", "Notification persistence is "+ prefs.getBoolean(PREFS_SHOW_ICON, false));

                            if(prefs.getBoolean(PREFS_SHOW_ICON, false) == true){
                                notification.flags |= Notification.FLAG_ONGOING_EVENT;
                            } else {
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                            }
                            notificationManager.notify(SONG_NOTIFICATION_ID, notification);
                            Log.i("NEWSONG", "Notification presented");
                        }
                    }
                }
            };
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        /*String nick = (TextView) view.findViewById(R.id.nick);*/
        File file = getItem(position);
        if (view != null) {
            /*view.setText(file.getName());*/
        }
        return view;
    }
}


