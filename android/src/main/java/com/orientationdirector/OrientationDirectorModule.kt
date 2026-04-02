package com.orientationdirector

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.orientationdirector.implementation.EventManagerDelegate
import com.orientationdirector.implementation.OrientationDirectorModuleImpl

class OrientationDirectorModule(reactContext: ReactApplicationContext) :
  NativeOrientationDirectorSpec(reactContext), EventManagerDelegate {

  override fun getName() = NAME

  private var implementation = OrientationDirectorModuleImpl(reactContext, this)

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

  override fun sendOnDeviceOrientationChanged(params: WritableMap) {
    try {
      emitOnDeviceOrientationChanged(params)
    } catch(_: Exception) {
      // No listener instance yet
    }
  }

  override fun sendOnInterfaceOrientationChanged(params: WritableMap) {
    try {
      emitOnInterfaceOrientationChanged(params)
    } catch(_: Exception) {
      // No listener instance yet
    }
  }

  override fun sendOnLockChanged(params: WritableMap) {
    try {
      emitOnLockChanged(params)
    } catch(_: Exception) {
      // No listener instance yet
    }
  }

  companion object {
    const val NAME = OrientationDirectorModuleImpl.NAME
  }

}
