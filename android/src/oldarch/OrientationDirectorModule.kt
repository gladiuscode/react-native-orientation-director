package com.orientationdirector

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactMethod
import com.orientationdirector.implementation.OrientationDirectorModuleImpl

class OrientationDirectorModule internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {

  private var implementation = OrientationDirectorModuleImpl(context)

  override fun getName() = OrientationDirectorModuleImpl.NAME

  @ReactMethod()
  fun getInterfaceOrientation(promise: Promise) {
    promise.resolve(implementation.getInterfaceOrientation().ordinal)
  }

  @ReactMethod()
  fun getDeviceOrientation(promise: Promise) {
    promise.resolve(implementation.getDeviceOrientation().ordinal)
  }

  @ReactMethod()
  fun lockTo(orientation: Double) {
    implementation.lockTo(orientation.toInt())
  }

  @ReactMethod()
  fun unlock() {
    implementation.unlock()
  }

  @ReactMethod()
  fun resetSupportedInterfaceOrientations() {
    implementation.resetSupportedInterfaceOrientations()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun isLocked(): Boolean {
    return implementation.getIsLocked()
  }

  @ReactMethod(isBlockingSynchronousMethod = true)
  fun isAutoRotationEnabled(): Boolean {
    return implementation.getIsAutoRotationEnabled()
  }

  @ReactMethod()
  fun addListener(eventName: String) {}

  @ReactMethod()
  fun removeListeners(count: Double) {}
}
