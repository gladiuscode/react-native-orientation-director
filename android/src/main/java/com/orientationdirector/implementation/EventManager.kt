package com.orientationdirector.implementation

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

enum class Event {
  DeviceOrientationDidChange,
  InterfaceOrientationDidChange,
  LockDidChange,
}

class EventManager(private val context: ReactApplicationContext) {

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

  fun sendLockDidChange(value: Boolean) {
    val params = Arguments.createMap().apply {
      putBoolean("locked", value)
    }
    sendEvent(Event.LockDidChange, params)
  }

  private fun sendEvent(eventName: Event, params: WritableMap?) {
    context
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName.name, params)
  }

  companion object {
    const val NAME = "OrientationEventManager"
  }

}
