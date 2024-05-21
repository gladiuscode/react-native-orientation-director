package com.orientationhandler

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.orientationhandler.implementation.OrientationHandlerImpl

class OrientationHandlerModule internal constructor(context: ReactApplicationContext) :
  OrientationHandlerSpec(context) {

  private var orientationHandlerImpl: OrientationHandlerImpl

  override fun getName(): String {
    return NAME
  }

  init {
    orientationHandlerImpl = OrientationHandlerImpl(context)
  }
  @ReactMethod()
  override fun getInterfaceOrientation(promise: Promise) {
    promise.resolve(orientationHandlerImpl.getInterfaceOrientation().ordinal)
  }

  @ReactMethod()
  override fun getDeviceOrientation(promise: Promise) {
    promise.resolve(orientationHandlerImpl.getDeviceOrientation().ordinal)
  }

  @ReactMethod()
  override fun lockTo(orientation: Double) {
    orientationHandlerImpl.lockTo(orientation.toInt())
  }

  @ReactMethod()
  override fun unlock() {
    orientationHandlerImpl.unlock()
  }

  @ReactMethod
  override fun addListener(eventName: String) {}

  @ReactMethod
  override fun removeListeners(count: Double) {}

  companion object {
    const val NAME = "OrientationHandler"
  }
}
