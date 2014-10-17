package jp.yuruga.catchme;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import jp.yuruga.catchme.event.RoomEventDispatcher;
import jp.yuruga.catchme.event.RoomEventListenerInterface;

import static jp.yuruga.common.Constants.*;
import static jp.yuruga.common.Share.*;

public class GameListenerService extends WearableListenerService
        implements //RoomEventListenerInterface,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener//,
        //LocationListener
{

    private static final String TAG = GameListenerService.class.getSimpleName();



    private GoogleApiClient mGoogleApiClient;
    /*private LocationRequest mLocationRequest;
    private long mLocationRequestInterval;
    private long mLocationRequestFastestInterval;
    private boolean isListeningLocation;*/

    //private MainService mMainService;
    //private boolean mIsBound;

    /*private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {

            // サービスとの接続確立時に呼び出される
            Toast.makeText(GameListenerService.this, "Activity:onServiceConnected",
                    Toast.LENGTH_SHORT).show();

            // サービスにはIBinder経由で#getService()してダイレクトにアクセス可能
            mMainService = ((MainService.MainServiceLocalBinder)service).getService();

            //必要であればmBoundServiceを使ってバインドしたサービスへの制御を行う
        }

        public void onServiceDisconnected(ComponentName className) {
            // サービスとの切断(異常系処理)
            // プロセスのクラッシュなど意図しないサービスの切断が発生した場合に呼ばれる。
            mMainService = null;
            Toast.makeText(GameListenerService.this, "onServiceDisconnected",
                    Toast.LENGTH_SHORT).show();
        }
    };*/

    public GameListenerService() {
    }

    @Override
    public void onCreate() {

        /*mLocationRequestInterval = (long)(getResources().getInteger(R.integer.location_update_interval_s)*1000);
        mLocationRequestFastestInterval = (long)(getResources().getInteger(R.integer.location_update_fastest_interval_s)*1000);*/

        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                //.addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        //Listen Room Events
        //RoomEventDispatcher.addEventListener(this);
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION_START_GAME))
        {
            //TODO init game
            if(mIsBound)
            {
                mMainService.startListeningLocationUpdates();
            }else
            {
                bindMainServiceWithAction(MainService.ACTION_START_WATCHING_LOCATION);
            }
            //isListeningLocation = true;
            //startListeningLocationUpdates();
        }else if(action.equals(ACTION_STOP_GAME))
        {
            if(mIsBound)
            {
                mMainService.stopListeningLocationUpdates();
            }else
            {

            }
            //stopListeningLocationUpdates();
        }else if(action.equals(ACTION_TEST_0))//test action 0
        {
            if(mIsBound)
            {
                mMainService.testAction0();
            }else
            {
                bindMainServiceWithAction(MainService.ACTION_TEST_ACTION);
            }
            //testAction0();
        }
        return super.onStartCommand(intent, flags, startId);
    }*/

    private void testAction0() {
        PutDataMapRequest dataMap = PutDataMapRequest.create(PATH_GAME_DATA);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        dataMap.getDataMap().putString(KEY_META, "{hoge:0,hage:9}");
        PutDataRequest request = dataMap.asPutDataRequest();
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(mGoogleApiClient, request);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {

                String path = event.getDataItem().getUri().getPath();
                log("aaaaaaaaaaa"+path);
                //TODO: manage data change

                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                String a  = dataMapItem.getDataMap().getString(KEY_META);


                log("cccc"+a);
            }
        }
    }

   /* private void startListeningLocationUpdates()
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

    }*/

    /*private void stopListeningLocationUpdates()
    {
        if(mGoogleApiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            isListeningLocation = false;
        }
    }*/



    //room events listener
/*

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

*/
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

    /*@Override
    public void onLocationChanged(Location location) {
        log("location:" + location.toString());
    }*/


    @Override
    public void onConnected(Bundle bundle) {
        log("%%google api client connected%%");
        /*if(isListeningLocation)
        {
            startListeningLocationUpdates();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    /*private void bindMainServiceWithAction(String action) {
        Intent bindServiceIntent = new Intent(GameListenerService.this,MainService.class);
        bindServiceIntent.setAction(action);
        bindService(bindServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void unnbindMainService() {
        if (mIsBound) {
            // コネクションの解除
            unbindService(mConnection);
            mIsBound = false;
        }
    }*/
}
