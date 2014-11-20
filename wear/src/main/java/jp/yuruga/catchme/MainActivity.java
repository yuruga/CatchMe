package jp.yuruga.catchme;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    // センサはここを参照した
    // @see http://junkcode.aakaka.com/archives/626

    private TextView mTextView;
    private SensorManager mSensor;
    private SensorEventListener mSensorEventListener;

    // 加速度センサの値
    private float[] mAccelerometerValue = new float[3];
    // 磁気センサの値
    private float[] mMagneticFieldValue = new float[3];
    private boolean mValidMagneticField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                activeCatchButton();
            }
        });
        mSensor = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d(TAG, String.format("sensor type %d", sensorEvent.sensor.getType()));
                if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    mMagneticFieldValue = sensorEvent.values.clone();
                    mValidMagneticField = true;
                } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    mAccelerometerValue = sensorEvent.values.clone();
                }
                if (mValidMagneticField) {
                    float[] rotate = new float[16]; // 傾斜行列？
                    float[] inclination = new float[16];    // 回転行列

                    // うまいこと変換行列を作ってくれるらしい
                    SensorManager.getRotationMatrix(
                            rotate, inclination,
                            mAccelerometerValue,
                            mMagneticFieldValue);

                    // 方向を求める
                    float[] orientation = new float[3];
                    getOrientation(rotate, orientation);

                    // デグリー角に変換する
                    float degreeDir = (float)Math.toDegrees(orientation[0]);
                    if (mTextView != null) {
                        mTextView.setText(String.format("%.2f", degreeDir));
                        Log.d(TAG, String.format("degree %.2f", degreeDir));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        Log.d(TAG, "onCreate end");
    }
    private void activeCatchButton () {
        //findViewById(R.id.text).setVisibility(View.GONE);
        findViewById(R.id.catch_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.catch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    // ////////////////////////////////////////////////////////////
    // 画面が回転していることを考えた方角の取り出し
    public void getOrientation(float[] rotate, float[] out) {

        // ディスプレイの回転方向を求める(縦もちとか横持ちとか)
        Display disp = this.getWindowManager().getDefaultDisplay();
        // ↓コレを使うためにはAPIレベルを8にする必要がある
        int dispDir = disp.getRotation();

        // 画面回転してない場合はそのまま
        if (dispDir == Surface.ROTATION_0) {
            SensorManager.getOrientation(rotate, out);

            // 回転している
        } else {

            float[] outR = new float[16];

            // 90度回転
            if (dispDir == Surface.ROTATION_90) {
                SensorManager.remapCoordinateSystem(
                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
                // 180度回転
            } else if (dispDir == Surface.ROTATION_180) {
                float[] outR2 = new float[16];

                SensorManager.remapCoordinateSystem(
                        rotate, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR2);
                SensorManager.remapCoordinateSystem(
                        outR2, SensorManager.AXIS_Y,SensorManager.AXIS_MINUS_X, outR);
                // 270度回転
            } else if (dispDir == Surface.ROTATION_270) {
                SensorManager.remapCoordinateSystem(
                        outR, SensorManager.AXIS_MINUS_Y,SensorManager.AXIS_MINUS_X, outR);
            }
            SensorManager.getOrientation(outR, out);
        }
    }
    @Override
    protected void onResume () {
        super.onResume();
        mSensor.registerListener(mSensorEventListener, mSensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        mSensor.registerListener(mSensorEventListener, mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        Log.d(TAG, "onResume");
    }
    @Override
    protected void onPause () {
        super.onPause();
        mSensor.unregisterListener(mSensorEventListener);
    }
}
