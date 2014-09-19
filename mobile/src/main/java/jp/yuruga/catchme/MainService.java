package jp.yuruga.catchme;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import jp.yuruga.catchme.event.RoomEventDispatcher;
import jp.yuruga.catchme.event.RoomEventListenerInterface;

import static jp.yuruga.common.Constants.*;
import static jp.yuruga.common.Share.*;

public class MainService extends WearableListenerService
        implements RoomEventListenerInterface,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = MainService.class.getSimpleName();
    public static final String ACTION_START_GAME = "jp.yuruga.catcheme.action.START_GAME";
    public static final String ACTION_STOP_GAME = "jp.yuruga.catchme.action.STOP_GAME";


    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private long mLocationRequestInterval;
    private long mLocationRequestFastestInterval;
    private boolean isListeningLocation;


    public MainService() {
    }

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
            isListeningLocation = true;
            startListeningLocationUpdates();
        }else if(action.equals(ACTION_STOP_GAME))
        {
            stopListeningLocationUpdates();
        }
        return super.onStartCommand(intent, flags, startId);
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
                //TODO: manage data change
            }
        }
    }

    private void startListeningLocationUpdates()
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

    private void stopListeningLocationUpdates()
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
