package com.orientationdirector.implementation

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.OrientationEventListener
import com.facebook.react.bridge.ReactApplicationContext
import kotlin.math.abs

class OrientationSensorsEventListener(
  context: ReactApplicationContext,
) : SensorEventListener, OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {
  private val rotationMatrix = FloatArray(9)
  private val orientationAngles = FloatArray(3)

  private var mSensorManager: SensorManager =
    context.getSystemService(SENSOR_SERVICE) as SensorManager
  private var mRotationSensor: Sensor? =
    mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
  private var hasRotationSensor: Boolean =
    mRotationSensor != null

  private var lastComputedDeviceOrientation = Orientation.UNKNOWN
  private var lastComputedFaceOrientation = Orientation.UNKNOWN

  private var onDeviceOrientationChangedCallback: ((deviceOrientation: Orientation) -> Unit)? = null
  fun setOnDeviceOrientationChangedCallback(callback: (deviceOrientation: Orientation) -> Unit) {
    onDeviceOrientationChangedCallback = callback
  }

  override fun enable() {
    super.enable()

    if (hasRotationSensor) {
      mSensorManager.registerListener(
        this,
        mRotationSensor,
        SensorManager.SENSOR_DELAY_NORMAL,
        SensorManager.SENSOR_DELAY_UI
      )
    }

    lastComputedDeviceOrientation = Orientation.UNKNOWN
    lastComputedFaceOrientation = Orientation.UNKNOWN
  }

  override fun disable() {
    super.disable()

    if (hasRotationSensor) {
      mSensorManager.unregisterListener(this)
    }

    lastComputedDeviceOrientation = Orientation.UNKNOWN
    lastComputedFaceOrientation = Orientation.UNKNOWN
  }

  override fun onOrientationChanged(angleDegrees: Int) {
    if (angleDegrees == ORIENTATION_UNKNOWN) {
      lastComputedDeviceOrientation = Orientation.UNKNOWN
      return
    }

    val currentDeviceOrientation = when (angleDegrees) {
      in LANDSCAPE_RIGHT_START .. LANDSCAPE_RIGHT_END -> Orientation.LANDSCAPE_RIGHT
      in PORTRAIT_UPSIDE_DOWN_START..PORTRAIT_UPSIDE_DOWN_END -> Orientation.PORTRAIT_UPSIDE_DOWN
      in LANDSCAPE_LEFT_START..LANDSCAPE_LEFT_END -> Orientation.LANDSCAPE_LEFT
      else -> Orientation.PORTRAIT
    }

    if (currentDeviceOrientation == lastComputedDeviceOrientation) return

    notifyDeviceOrientationChanged(currentDeviceOrientation)
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

    if (lastComputedDeviceOrientation != Orientation.UNKNOWN) {
      lastComputedFaceOrientation = Orientation.UNKNOWN
      return
    }

    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
    SensorManager.getOrientation(rotationMatrix, orientationAngles)

    val zUp = rotationMatrix[8]
    val currentFaceOrientation = when {
      zUp > FACE_UP_Z_THRESHOLD -> Orientation.FACE_UP
      zUp < -FACE_DOWN_Z_THRESHOLD -> Orientation.FACE_DOWN
      else -> null
    }

    if (currentFaceOrientation == null) return

    if (currentFaceOrientation == lastComputedFaceOrientation) return

    notifyFaceOrientationChanged(currentFaceOrientation)
  }

  private fun notifyDeviceOrientationChanged(deviceOrientation: Orientation) {
    onDeviceOrientationChangedCallback?.invoke(deviceOrientation)
    lastComputedDeviceOrientation = deviceOrientation
  }

  private fun notifyFaceOrientationChanged(faceOrientation: Orientation) {
    onDeviceOrientationChangedCallback?.invoke(faceOrientation)
    lastComputedFaceOrientation = faceOrientation
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

  companion object {
    private const val LANDSCAPE_RIGHT_START = 45
    private const val LANDSCAPE_RIGHT_END = 134
    private const val PORTRAIT_UPSIDE_DOWN_START = 135
    private const val PORTRAIT_UPSIDE_DOWN_END = 224
    private const val LANDSCAPE_LEFT_START = 225
    private const val LANDSCAPE_LEFT_END = 314
    private const val FACE_UP_Z_THRESHOLD   = 0.906f
    private const val FACE_DOWN_Z_THRESHOLD = 0.906f
  }
}
