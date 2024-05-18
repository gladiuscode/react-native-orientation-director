package com.orientationhandler.implementation

import android.hardware.SensorManager
import android.view.OrientationEventListener
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class OrientationSensorListener(private val context: ReactApplicationContext) : OrientationEventListener(context, SensorManager.SENSOR_DELAY_UI) {
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

    val params = Arguments.createMap().apply {
      putInt("orientation", orientationValue)
    }

    sendEvent(context, Event.OrientationDidChange.name, params)
  }

  private fun sendEvent(reactContext: ReactContext, eventName: String, params: WritableMap?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

}
