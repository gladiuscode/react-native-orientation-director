package com.orientationdirector

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.orientationdirector.implementation.OrientationDirectorImpl

class OrientationDirectorModule internal constructor(context: ReactApplicationContext) :
  OrientationDirectorSpec(context) {

  private var orientationDirectorImpl: OrientationDirectorImpl

  override fun getName(): String {
    return NAME
  }

  init {
    orientationDirectorImpl = OrientationDirectorImpl(context)
  }

  @ReactMethod()
  override fun getInterfaceOrientation(promise: Promise) {
    promise.resolve(orientationDirectorImpl.getInterfaceOrientation().ordinal)
  }

  @ReactMethod()
  override fun getDeviceOrientation(promise: Promise) {
    promise.resolve(orientationDirectorImpl.getDeviceOrientation().ordinal)
  }

  @ReactMethod()
  override fun lockTo(orientation: Double) {
    orientationDirectorImpl.lockTo(orientation.toInt())
  }

  @ReactMethod()
  override fun unlock() {
    orientationDirectorImpl.unlock()
  }

  @ReactMethod()
  override fun addListener(eventName: String) {}

  @ReactMethod()
  override fun removeListeners(count: Double) {}

  companion object {
    const val NAME = "OrientationDirector"
  }
}
