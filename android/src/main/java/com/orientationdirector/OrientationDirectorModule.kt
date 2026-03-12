package com.orientationdirector

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.orientationdirector.implementation.OrientationDirectorModuleImpl

class OrientationDirectorModule(reactContext: ReactApplicationContext) :
  NativeOrientationDirectorSpec(reactContext) {

  override fun getName() = NAME

  private var implementation = OrientationDirectorModuleImpl(reactContext)

  override fun getInterfaceOrientation(promise: Promise) {
    promise.resolve(implementation.getInterfaceOrientation().ordinal)
  }

  override fun getDeviceOrientation(promise: Promise) {
    promise.resolve(implementation.getDeviceOrientation().ordinal)
  }

  override fun lockTo(orientation: Double) {
    implementation.lockTo(orientation.toInt())
  }

  override fun unlock() {
    implementation.unlock()
  }

  override fun resetSupportedInterfaceOrientations() {
    implementation.resetSupportedInterfaceOrientations()
  }

  override fun isLocked(): Boolean {
    return implementation.getIsLocked()
  }

  override fun isAutoRotationEnabled(): Boolean {
    return implementation.getIsAutoRotationEnabled()
  }

  override fun enableOrientationSensors() {
    return implementation.enableOrientationSensors()
  }

  override fun disableOrientationSensors() {
    return implementation.disableOrientationSensors()
  }

  override fun addListener(eventName: String) {}

  override fun removeListeners(count: Double) {}

  companion object {
    const val NAME = OrientationDirectorModuleImpl.NAME
  }

}
