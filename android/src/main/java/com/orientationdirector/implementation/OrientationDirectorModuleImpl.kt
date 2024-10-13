package com.orientationdirector.implementation

import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReactApplicationContext

class OrientationDirectorModuleImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = Utils(context)
  private var mEventManager = EventManager(context)
  private var mSensorListener = SensorListener(context)
  private var mAutoRotationObserver = AutoRotationObserver(
    context, Handler(
      Looper.getMainLooper()
    )
  )
  private var mLifecycleListener = LifecycleListener()

  private var initialSupportedInterfaceOrientations = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  private var lastInterfaceOrientation = Orientation.UNKNOWN
  private var lastDeviceOrientation = Orientation.UNKNOWN
  private var initialized = false
  private var isLocked: Boolean = false

  init {
    mSensorListener.setOnOrientationAnglesChangedCallback { orientation ->
      onOrientationAnglesChanged(orientation)
    }

    mAutoRotationObserver.enable()

    context.addLifecycleEventListener(mLifecycleListener)
    mLifecycleListener.setOnHostResumeCallback {
      mSensorListener.enable()
      mAutoRotationObserver.enable()
    }
    mLifecycleListener.setOnHostPauseCallback {
      if (initialized) {
        mSensorListener.disable()
        mAutoRotationObserver.disable()
      }
    }
    mLifecycleListener.setOnHostDestroyCallback {
      mSensorListener.disable()
      mAutoRotationObserver.disable()
    }

    initialSupportedInterfaceOrientations =
      context.currentActivity?.requestedOrientation ?: initialSupportedInterfaceOrientations
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

  fun getIsLocked(): Boolean {
    return isLocked
  }

  fun getIsAutoRotationEnabled(): Boolean {
    return mAutoRotationObserver.getLastAutoRotationStatus()
  }

  fun lockTo(jsValue: Int) {
    val jsOrientation = mUtils.convertToOrientationFromJsValue(jsValue)
    val screenOrientation =
      mUtils.convertToActivityOrientationFrom(jsOrientation)
    context.currentActivity?.requestedOrientation = screenOrientation

    updateIsLockedTo(true)
    updateLastInterfaceOrientationTo(jsOrientation)
  }

  fun unlock() {
    context.currentActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    updateIsLockedTo(false)
    adaptInterfaceTo(lastDeviceOrientation)
  }

  fun resetSupportedInterfaceOrientations() {
    context.currentActivity?.requestedOrientation = initialSupportedInterfaceOrientations
    updateIsLockedTo(initIsLocked())
    updateLastInterfaceOrientationTo(initInterfaceOrientation())
  }

  private fun initInterfaceOrientation(): Orientation {
    val rotation = mUtils.getInterfaceRotation()
    return mUtils.convertToOrientationFromScreenRotation(rotation)
  }

  private fun initDeviceOrientation(): Orientation {
    // TODO: CHECK IF WE CAN INFER IT AT STARTUP TIME
    return Orientation.UNKNOWN
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

  private fun onOrientationAnglesChanged(orientationAngles: FloatArray) {
    val deviceOrientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)
    if (deviceOrientation == Orientation.UNKNOWN) {
      return;
    }

    if (lastDeviceOrientation == deviceOrientation) {
      return
    }

    mEventManager.sendDeviceOrientationDidChange(deviceOrientation.ordinal)
    lastDeviceOrientation = deviceOrientation

    adaptInterfaceTo(deviceOrientation)
  }

  private fun adaptInterfaceTo(deviceOrientation: Orientation) {
    if (!mAutoRotationObserver.getLastAutoRotationStatus()) {
      return
    }

    if (isLocked) {
      return
    }

    val newInterfaceOrientation = mUtils.convertToInterfaceOrientationFrom(deviceOrientation);
    if (newInterfaceOrientation == lastInterfaceOrientation) {
      return
    }

    updateLastInterfaceOrientationTo(newInterfaceOrientation)
  }

  private fun updateIsLockedTo(value: Boolean) {
    mEventManager.sendLockDidChange(value)
    isLocked = value
  }

  private fun updateLastInterfaceOrientationTo(value: Orientation) {
    lastInterfaceOrientation = value
    mEventManager.sendInterfaceOrientationDidChange(value.ordinal)
  }

  companion object {
    const val NAME = "OrientationDirector"
  }
}
