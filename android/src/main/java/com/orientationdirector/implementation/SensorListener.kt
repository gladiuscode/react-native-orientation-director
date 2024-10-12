package com.orientationdirector.implementation

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext

class SensorListener(
  context: ReactApplicationContext,
) : SensorEventListener {
  private var mSensorManager: SensorManager

  private var mAccelerometerSensor: Sensor?
  private var mMagneticFieldSensor: Sensor?

  private var hasRequiredSensors: Boolean

  private var lastRotationDetected: Int? = null
  private var onOrientationChangedCallback: ((orientation: Int) -> Unit)? = null

  init {
    mSensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager;

    mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    mMagneticFieldSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    hasRequiredSensors = mAccelerometerSensor != null && mMagneticFieldSensor != null
  }

  fun getLastRotationDetected(): Int? {
    return lastRotationDetected
  }

  fun setOnOrientationChangedCallback(callback: (orientation: Int) -> Unit) {
    onOrientationChangedCallback = callback
  }

//  override fun onOrientationChanged(orientation: Int) {
//    lastRotationDetected = orientation
//    onOrientationChangedCallback?.invoke(orientation)
//  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) {
      return
    }

//    Log.d(TAG, "onSensorChanged $event")
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // TODO("Not yet implemented")
  }

  fun enable() {
    Log.d(TAG, "enable - started")
    if (!hasRequiredSensors) {
      Log.d(TAG, "enable - device is missing required sensors")
      return
    }

    mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    mSensorManager.registerListener(this, mMagneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL)

    Log.d(TAG, "enable - done")
  }

  fun disable() {
    Log.d(TAG, "disable - started")
    mSensorManager.unregisterListener(this)
    Log.d(TAG, "disable - done")
  }

  companion object {
    private const val TAG = "SensorListener"
  }
}
