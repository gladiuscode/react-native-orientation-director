package com.orientationhandler.implementation

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.facebook.react.bridge.ReactApplicationContext

class OrientationSensorListener(
  context: ReactApplicationContext,
) : OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {

  private var lastRotationDetected: Int? = null
  fun getLastRotationDetected(): Int? {
    return lastRotationDetected
  }

  private var onOrientationChangedCallback: ((orientation: Int) -> Unit)? = null
  fun setOnOrientationChangedCallback(callback: (orientation: Int) -> Unit) {
    onOrientationChangedCallback = callback
  }

  override fun onOrientationChanged(orientation: Int) {
    lastRotationDetected = orientation
    onOrientationChangedCallback?.invoke(orientation)
  }
}
