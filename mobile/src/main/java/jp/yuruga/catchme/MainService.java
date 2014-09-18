package jp.yuruga.catchme;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import jp.yuruga.catchme.event.RoomEventDispatcher;
import jp.yuruga.catchme.event.RoomEventListenerInterface;

public class MainService extends WearableListenerService implements RoomEventListenerInterface
{

    private static final String TAG = MainService.class.getSimpleName();
    public static final String ACTION_START_GAME = "jp.yuruga.catcheme.action.START_GAME";


    private GoogleApiClient mGoogleApiClient;


    public MainService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        //Listen Room Events
        RoomEventDispatcher.addEventListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_START_GAME))
        {
            //TODO start game

        }else
        {

        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
    }


    //room events listener

    @Override
    public void onConnect(Object obj) {
        Log.d(TAG, "@@onConnect");
    }

    @Override
    public void onDisconnect() {
        Log.d(TAG, "@@onDisconnect");
    }

    @Override
    public void onChangeRoomProp(Object obj) {
        Log.d(TAG, "@@onChangeRoomProp");
    }

    @Override
    public void onChangeUserProp(Object[] arr) {
        Log.d(TAG, "@@onChangeUserProp");
    }

    @Override
    public void onReceiveMessage(String message) {
        Log.d(TAG, "@@onReceiveMessage");
    }
}
