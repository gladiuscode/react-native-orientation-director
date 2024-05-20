package com.orientationhandler.implementation

import android.content.pm.ActivityInfo
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl()
  private var mEventEmitter = OrientationEventManager(context)
  private var mSensorListener = OrientationSensorListener(context)
  private var mLifecycleListener = OrientationLifecycleListener()
  private var isLocked: Boolean = false
  private var lastInterfaceOrientation = Orientation.UNKNOWN
  private var lastDeviceOrientation = Orientation.UNKNOWN
  private var initialized = false

  init {
    mSensorListener.setOnOrientationChangedCallback { orientation ->
      onOrientationChanged(orientation)
    }

    if (mSensorListener.canDetectOrientation()) {
      mSensorListener.enable()
    } else {
      mSensorListener.disable()
    }

    context.addLifecycleEventListener(mLifecycleListener)
    mLifecycleListener.setOnHostResumeCallback {
      if (mSensorListener.canDetectOrientation()) {
        mSensorListener.enable()
      }
    }
    mLifecycleListener.setOnHostPauseCallback {
      if (initialized) {
        mSensorListener.disable()
      }
    }
    mLifecycleListener.setOnHostDestroyCallback {
      mSensorListener.disable()
    }

    lastInterfaceOrientation = initInterfaceOrientation()
    lastDeviceOrientation = initDeviceOrientation()

    initialized = true
  }

  fun getInterfaceOrientation(): Orientation {
    return lastInterfaceOrientation
  }

  fun getDeviceOrientation(): Orientation {
    return lastDeviceOrientation
  }

  fun lockTo(jsOrientation: Int) {
    val interfaceOrientation = mUtils.getOrientationEnumFrom(jsOrientation)
    val screenOrientation =
      mUtils.getActivityOrientationFrom(interfaceOrientation)
    context.currentActivity?.requestedOrientation = screenOrientation
    mEventEmitter.sendInterfaceOrientationDidChange(interfaceOrientation.ordinal)
    lastInterfaceOrientation = interfaceOrientation
    isLocked = true
  }

  fun unlock() {
    context.currentActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    isLocked = false
    adaptInterfaceTo(getDeviceOrientation())
  }

  private fun initInterfaceOrientation(): Orientation {
    val activityOrientation = context.currentActivity!!.requestedOrientation
    if (
      mUtils.isActivityInPortraitOrientation(activityOrientation) ||
      mUtils.isActivityInLandscapeOrientation(activityOrientation)
    ) {
      return mUtils.getInterfaceOrientationFrom(activityOrientation)
    }

    val lastRotationDetected = mSensorListener.getLastRotationDetected()
      ?: return lastInterfaceOrientation

    return mUtils.getDeviceOrientationFrom(lastRotationDetected)
  }

  private fun initDeviceOrientation(): Orientation {
    val lastRotationDetected = mSensorListener.getLastRotationDetected()
      ?: return Orientation.UNKNOWN

    return mUtils.getDeviceOrientationFrom(lastRotationDetected)
  }

  private fun onOrientationChanged(rawDeviceOrientation: Int) {
    val deviceOrientation = mUtils.getDeviceOrientationFrom(rawDeviceOrientation)
    mEventEmitter.sendDeviceOrientationDidChange(deviceOrientation.ordinal)
    lastDeviceOrientation = deviceOrientation
    adaptInterfaceTo(deviceOrientation)
  }

  private fun adaptInterfaceTo(deviceOrientation: Orientation) {
    if (isLocked) {
      return
    }

    if (lastInterfaceOrientation == deviceOrientation) {
      return
    }

    lastInterfaceOrientation = deviceOrientation
    mEventEmitter.sendInterfaceOrientationDidChange(lastInterfaceOrientation.ordinal)
  }

  companion object {
    const val NAME = "OrientationHandlerImpl"
  }
}
