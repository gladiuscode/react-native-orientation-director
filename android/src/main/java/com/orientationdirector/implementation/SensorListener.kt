package com.orientationdirector.implementation

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import kotlin.math.PI

class SensorListener(
  context: ReactApplicationContext,
) : SensorEventListener {
  private var mSensorManager: SensorManager

  private var mAccelerometerSensor: Sensor?
  private var mMagneticFieldSensor: Sensor?

  private var hasRequiredSensors: Boolean

  private val accelerometerReading = FloatArray(3)
  private val magnetometerReading = FloatArray(3)
  private val rotationMatrix = FloatArray(9)
  private val orientationAngles = FloatArray(3)

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

    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
      System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
      System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
    }

    updateOrientationAngles()

    val z = orientationAngles[0]
    val x = orientationAngles[1]
    val y = orientationAngles[2]

    val orientation = when {

      // Portrait (Rotation_0)
      x.equals(0f) && y.equals(-0f) && z.equals(0f) -> "Face Up"
      x.equals(0f) && y.equals(-PI.toFloat()) && z.equals(PI.toFloat()) -> "Face Down"

      // Landscape Right (Rotation_90)
      x.equals(-0f) && y.equals(-0f) && z.equals(PI.div(2).toFloat()) -> "Face Up"
      x.equals(-0f) && y.equals(-PI.toFloat()) && z.equals(PI.div(2).toFloat()) -> "Face Down"

      // Portrait Upside Down (Rotation_180)
      x.equals(0f) && y.equals(-0f) && z.equals(PI.toFloat()) -> "Face Up"
      x.equals(0f) && y.equals(-PI.toFloat()) && z.equals(-0f) -> "Face Down"

      // Landscape Left (Rotation_270)
      x.equals(-0f) && y.equals(-0f) && z.equals(-PI.div(2).toFloat()) -> "Face Up"
      x.equals(-0f) && y.equals(-PI.toFloat()) && z.equals(-PI.div(2).toFloat()) -> "Face Down"

      else -> "Unknown Orientation"
    }

    Log.d(TAG, orientation)
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

  private fun updateOrientationAngles() {
    SensorManager.getRotationMatrix(
      rotationMatrix,
      null,
      accelerometerReading,
      magnetometerReading
    )

    SensorManager.getOrientation(rotationMatrix, orientationAngles)
  }

  companion object {
    private const val TAG = "SensorListener"
  }
}
