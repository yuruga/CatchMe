package jp.yuruga.catchme;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import jp.yuruga.catchme.event.RoomEventDispatcher;
import jp.yuruga.catchme.event.RoomEventListenerInterface;

public class MapsActivity extends FragmentActivity {

    private static final String TAG = MapsActivity.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public double latitude = 0;
    public double longitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        setUpRoomEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        //Google MapのMyLocationレイヤーを使用可能にする
        mMap.setMyLocationEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setTrafficEnabled(true);

        //MapEvents
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + latLng.latitude + "\n経度:" + latLng.longitude, Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
//            Toast.makeText(getApplicationContext(), "タップ位置\n緯度：" + location.getLatitude() + "\n経度:" + location.getLongitude(), Toast.LENGTH_LONG).show();
            }
        });

        //システムサービスのLOCATION_SERVICEからLocationManager objectを取得
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //retrieve providerへcriteria objectを生成
        Criteria criteria = new Criteria();

        //Best providerの名前を取得
        String provider = locationManager.getBestProvider(criteria, true);

        //現在位置を取得
        Location location = locationManager.getLastKnownLocation(provider);

        //現在位置の緯度を取得
        latitude = location.getLatitude();

        //現在位置の経度を取得
        longitude = location.getLongitude();

        //現在位置からLatLng objectを生成
        LatLng latLng = new LatLng(latitude, longitude);

        //Google Mapに現在地を表示
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Google Mapの Zoom値を指定
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }

    private void setUpRoomEvent() {

        Log.d(TAG, "@@@setUpRoomEvent@@@");

        RoomEventDispatcher.addEventListener(new RoomEventListenerInterface() {
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
        });

        RoomEventDispatcher.dispatchConnect(new Object());
        RoomEventDispatcher.dispatchReceiveMessage("test");
        RoomEventDispatcher.dispatchDisconnect();
    }
}
