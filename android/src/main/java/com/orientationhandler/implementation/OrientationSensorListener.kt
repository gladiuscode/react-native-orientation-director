package com.orientationhandler.implementation

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.facebook.react.bridge.ReactApplicationContext

class OrientationSensorListener(
  context: ReactApplicationContext,
  private val eventEmitter: OrientationEventManager
) : OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {

  override fun onOrientationChanged(orientation: Int) {
    var orientationValue: Int = 0;

    if (orientation == -1) {
      // UNKNOWN
      orientationValue = 0
    } else if (orientation > 355 || orientation < 5) {
      // PORTRAIT
      orientationValue = 1
    } else if (orientation in 86..94) {
      // LANDSCAPE-RIGHT
      orientationValue = 4
    } else if (orientation in 176..184) {
      // LANDSCAPE-LEFT
      orientationValue = 2
    } else if (orientation in 266..274) {
      // PORTRAIT-UPSIDE-DOWN
      orientationValue = 3
    }

    eventEmitter.sendDeviceOrientationDidChange(orientationValue)
  }
}
