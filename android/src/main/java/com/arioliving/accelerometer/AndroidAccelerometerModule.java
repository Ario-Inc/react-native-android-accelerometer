package com.arioliving.accelerometer;

import com.facebook.react.uimanager.*;
import com.facebook.react.bridge.*;
import com.facebook.systrace.Systrace;
import com.facebook.systrace.SystraceMessage;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AndroidAccelerometerModule extends ReactContextBaseJavaModule implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 9;
    private ReactApplicationContext mReactContext;
    private float mThreshold = 0;

    //Constructor
    public AndroidAccelerometerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mReactContext = reactContext;

        mSensorManager = (SensorManager) reactContext.getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public String getName() {
        return "AndroidAccelerometerModule";
    }

    @ReactMethod
    public void setThreshold(double threshold) {
        mThreshold = (float) threshold;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // get the change of the x,y,z values of the accelerometer
        float x = Math.abs(lastX - event.values[0]);
        float y = Math.abs(lastY - event.values[1]);
        float z = Math.abs(lastZ - event.values[2]);
        // if the change is below 2, it is just plain noise
        if (x > mThreshold || y > mThreshold || z > mThreshold) {
            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];

            WritableMap data = Arguments.createMap();//new WriteableMap();
            data.putDouble("x", event.values[0]);
            data.putDouble("y", event.values[1]);
            data.putDouble("z", event.values[2]);

            if (mThreshold != 0) {
                mReactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("accelerometerUpdate",
                        data);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
