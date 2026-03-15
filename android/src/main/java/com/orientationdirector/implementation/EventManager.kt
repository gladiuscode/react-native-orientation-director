package com.orientationdirector.implementation

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

class EventManager(private val delegate: EventManagerDelegate) {
  fun sendDeviceOrientationDidChange(orientationValue: Int) {
    val params = Arguments.createMap().apply {
      putInt("orientation", orientationValue)
    }
    delegate.sendOnDeviceOrientationChanged(params)
  }

  fun sendInterfaceOrientationDidChange(orientationValue: Int) {
    val params = Arguments.createMap().apply {
      putInt("orientation", orientationValue)
    }
    delegate.sendOnInterfaceOrientationChanged(params)
  }

  fun sendLockDidChange(value: Boolean) {
    val params = Arguments.createMap().apply {
      putBoolean("locked", value)
    }
    delegate.sendOnLockChanged(params)
  }

  companion object {
    const val NAME = "OrientationEventManager"
  }

}
