package jp.yuruga.catchme;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jp.yuruga.catchme.event.RoomEventDispatcher;
import jp.yuruga.catchme.event.RoomEventListenerInterface;

import static jp.yuruga.common.Constants.KEY_META;
import static jp.yuruga.common.Constants.PATH_GAME_DATA;
import static jp.yuruga.common.Share.log;

public class MainService extends Service implements RoomEventListenerInterface,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{

    private static final String TAG = GameListenerService.class.getSimpleName();
    public static final String ACTION_START_WATCHING_LOCATION = "jp.yuruga.catcheme.action.START_WATCHING_LOCATION";
    public static final String ACTION_TEST_ACTION = "jp.yuruga.catcheme.action.ACTION_TEST_0";
    public static final String ACTION_START_GAME = "jp.yuruga.catcheme.action.START_GAME";
    public static final String ACTION_STOP_GAME = "jp.yuruga.catchme.action.STOP_GAME";

    //test action
    //public static final String ACTION_TEST_0 = "jp.yuruga.catchme.action.TEST_0";


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long mLocationRequestInterval;
    private long mLocationRequestFastestInterval;
    private boolean isListeningLocation;

    /*public class MainServiceLocalBinder extends Binder {
        MainService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MainService.this;
        }
    }*/

    //Binderの生成
    //private final IBinder mBinder = new MainServiceLocalBinder();

    /*@Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(this, "MyService#onBind"+ ": " + intent, Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        if(ACTION_START_WATCHING_LOCATION.equals(action))
        {
            startListeningLocationUpdates();
        }else if(ACTION_TEST_ACTION.equals(action))
        {
            testAction0();
        }
        log("onBind" + ": " + intent + "with action"+action);

        return mBinder;
    }*/

    /*@Override
    public void onRebind(Intent intent){
        Toast.makeText(this, "MyService#onRebind"+ ": " + intent, Toast.LENGTH_SHORT).show();
        log("onRebind" + ": " + intent);
    }

    @Override
    public boolean onUnbind(Intent intent){
        Toast.makeText(this, "MyService#onUnbind"+ ": " + intent, Toast.LENGTH_SHORT).show();
        log("onUnbind" + ": " + intent);

        //onUnbindをreturn trueでoverrideすると次回バインド時にonRebildが呼ばれる
        return true;
    }

    public MainService() {
    }*/



    @Override
    public void onCreate() {

        mLocationRequestInterval = (long)(getResources().getInteger(R.integer.location_update_interval_s)*1000);
        mLocationRequestFastestInterval = (long)(getResources().getInteger(R.integer.location_update_fastest_interval_s)*1000);

        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
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
            //TODO init game
            startListeningLocationUpdates();
            isListeningLocation = true;
        }else if(action.equals(ACTION_STOP_GAME))
        {
            stopListeningLocationUpdates();
        }else if(ACTION_TEST_ACTION.equals(action)) {
            testAction0();
        }

        return Service.START_STICKY_COMPATIBILITY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void testAction0() {
        PutDataMapRequest dataMap = PutDataMapRequest.create(PATH_GAME_DATA);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        dataMap.getDataMap().putString(KEY_META, "{hoge:0,hage:9}");
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request);
    }


    public void startListeningLocationUpdates()
    {
        log("%%StartListening%%");
        if(mGoogleApiClient.isConnected())
        {
            mLocationRequest = LocationRequest.create();
            // Use high accuracy
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(mLocationRequestInterval);
            mLocationRequest.setFastestInterval(mLocationRequestFastestInterval);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            log("%%StartListening2222%%");
        }

    }

    public void stopListeningLocationUpdates()
    {
        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            isListeningLocation = false;
        }
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


    private void sendMessage(String path, byte[] data) {
        Collection<String> nodes = getNodes();
        for (String node : nodes) {
            Wearable.MessageApi.sendMessage(
                    mGoogleApiClient, node, path, data).setResultCallback(
                    new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (!sendMessageResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Failed to send message with status code: "
                                        + sendMessageResult.getStatus().getStatusCode());
                            }
                        }
                    }
            );
        }
    }

    private Collection<String> getNodes() {
        HashSet<String> results= new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }
        return results;
    }

    @Override
    public void onLocationChanged(Location location) {
        log("location:" + location.toString());
    }


    @Override
    public void onConnected(Bundle bundle) {
        log("%%google api client connected%%");
        if(isListeningLocation)
        {
            startListeningLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
