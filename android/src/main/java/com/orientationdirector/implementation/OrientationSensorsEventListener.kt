package com.orientationdirector.implementation

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.facebook.react.bridge.ReactApplicationContext

class OrientationSensorsEventListener(
  context: ReactApplicationContext,
) : SensorEventListener {
  private var mSensorManager: SensorManager =
    context.getSystemService(SENSOR_SERVICE) as SensorManager

  private var mRotationSensor: Sensor? =
    mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
  private var mAccelerometerSensor: Sensor? =
    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
  private var mMagneticFieldSensor: Sensor? =
    mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

  private var hasRotationSensor: Boolean =
   mRotationSensor != null
  private var hasAccelerometerAndMagneticFieldSensors: Boolean =
    mAccelerometerSensor != null && mMagneticFieldSensor != null

  private val accelerometerReading = FloatArray(3)
  private val magnetometerReading = FloatArray(3)

  private var lastComputedOrientationAngles = FloatArray(3)
  private var onOrientationAnglesChangedCallback: ((orientationAngles: FloatArray) -> Unit)? = null

  fun setOnOrientationAnglesChangedCallback(callback: (orientation: FloatArray) -> Unit) {
    onOrientationAnglesChangedCallback = callback
  }

  override fun onSensorChanged(event: SensorEvent?) {
    if (event == null) {
      return
    }

    if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
      computeOrientationFromRotationSensor(event.values);
      return
    }

    computeOrientationFromOtherSensors(event)
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

  fun enable() {
    if (hasRotationSensor) {
      mSensorManager.registerListener(
        this,
        mRotationSensor,
        SensorManager.SENSOR_DELAY_NORMAL,
        SensorManager.SENSOR_DELAY_UI
      )
      return
    }

    if (hasAccelerometerAndMagneticFieldSensors) {
      mSensorManager.registerListener(
        this,
        mAccelerometerSensor,
        SensorManager.SENSOR_DELAY_NORMAL,
        SensorManager.SENSOR_DELAY_UI
      )
      mSensorManager.registerListener(
        this,
        mMagneticFieldSensor,
        SensorManager.SENSOR_DELAY_NORMAL,
        SensorManager.SENSOR_DELAY_UI
      )
      return
    }
  }

  fun disable() {
    mSensorManager.unregisterListener(this)
  }

  private fun computeOrientationFromRotationSensor(values: FloatArray) {
    val rotationMatrix = FloatArray(9)
    SensorManager.getRotationMatrixFromVector(rotationMatrix, values)

    val orientationAngles = FloatArray(3)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)

    notifyOrientationAnglesChanged(orientationAngles)
  }

  private fun computeOrientationFromOtherSensors(event: SensorEvent) {
    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
      System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
    }

    if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
      System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
    }

    val rotationMatrix = FloatArray(9)
    val didComputeMatrix = SensorManager.getRotationMatrix(
      rotationMatrix,
      null,
      accelerometerReading,
      magnetometerReading
    )
    if (!didComputeMatrix) {
      return
    }

    val orientationAngles = FloatArray(3)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)

    notifyOrientationAnglesChanged(orientationAngles)
  }

  private fun notifyOrientationAnglesChanged(orientationAngles: FloatArray) {
    if (lastComputedOrientationAngles.contentEquals(orientationAngles)) {
      return
    }

    onOrientationAnglesChangedCallback?.invoke(orientationAngles)
    lastComputedOrientationAngles = orientationAngles
  }
}
