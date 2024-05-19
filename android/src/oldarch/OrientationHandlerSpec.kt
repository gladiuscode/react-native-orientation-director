package com.orientationhandler

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise

abstract class OrientationHandlerSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  abstract fun getInterfaceOrientation(promise: Promise)
  abstract fun getDeviceOrientation(promise: Promise)
  abstract fun lockTo(orientation: Int)
}
