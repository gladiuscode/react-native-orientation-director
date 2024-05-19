package com.orientationhandler.implementation

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

enum class Event {
  DeviceOrientationDidChange,
  InterfaceOrientationDidChange,
}

class OrientationEventManager(private val context: ReactApplicationContext) {

  fun sendDeviceOrientationDidChange(orientationValue: Int) {
    val params = Arguments.createMap().apply {
      putInt("orientation", orientationValue)
    }
    sendEvent(Event.DeviceOrientationDidChange, params)
  }

  fun sendInterfaceOrientationDidChange(orientationValue: Int) {
    val params = Arguments.createMap().apply {
      putInt("orientation", orientationValue)
    }
    sendEvent(Event.InterfaceOrientationDidChange, params)
  }

  private fun sendEvent(eventName: Event, params: WritableMap?) {
    Log.d(NAME, "sendEvent - $eventName")
    Log.d(NAME, "sendEvent - $params")
    context
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName.name, params)
  }

  companion object {
    val NAME = "OrientationEventManager"
  }

}
