package com.orientationdirector.implementation

import android.content.pm.ActivityInfo
import com.facebook.react.bridge.ReactApplicationContext

class OrientationDirectorImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationDirectorUtilsImpl()
  private var mEventEmitter = OrientationEventManager(context)
  private var mSensorListener = OrientationSensorListener(context)
  private var mLifecycleListener = OrientationLifecycleListener()
  private var lastInterfaceOrientation = Orientation.UNKNOWN
  private var lastDeviceOrientation = Orientation.UNKNOWN
  private var initialized = false

  var isLocked: Boolean = false

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
    isLocked = initIsLocked()

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
    updateIsLockedTo(true)
  }

  fun unlock() {
    context.currentActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    updateIsLockedTo(false)
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

  private fun initIsLocked(): Boolean {
    val activity = context.currentActivity ?: return false
    return listOf(
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
      ActivityInfo.SCREEN_ORIENTATION_LOCKED,
    ).contains(activity.requestedOrientation)
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

  private fun updateIsLockedTo(value: Boolean) {
    mEventEmitter.sendLockDidChange(value)
    isLocked = value
  }

  companion object {
    const val NAME = "OrientationDirectorImpl"
  }
}
