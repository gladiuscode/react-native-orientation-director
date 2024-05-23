package com.orientationdirector

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise

abstract class OrientationDirectorSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun getInterfaceOrientation(promise: Promise)
  abstract fun getDeviceOrientation(promise: Promise)
  abstract fun lockTo(orientation: Double)
  abstract fun unlock()

  abstract fun isLocked(): Boolean
  abstract fun addListener(eventName: String)
  abstract fun removeListeners(count: Double)
}
