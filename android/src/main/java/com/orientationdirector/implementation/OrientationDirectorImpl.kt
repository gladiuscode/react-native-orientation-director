package com.orientationdirector.implementation

import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.view.OrientationEventListener.ORIENTATION_UNKNOWN
import com.facebook.react.bridge.ReactApplicationContext

class OrientationDirectorImpl internal constructor(private val context: ReactApplicationContext) {
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
    mSensorListener.setOnOrientationChangedCallback { orientation ->
      onOrientationChanged(orientation)
    }

    if (mSensorListener.canDetectOrientation()) {
      mSensorListener.enable()
    } else {
      mSensorListener.disable()
    }

    mAutoRotationObserver.enable()

    context.addLifecycleEventListener(mLifecycleListener)
    mLifecycleListener.setOnHostResumeCallback {
      if (mSensorListener.canDetectOrientation()) {
        mSensorListener.enable()
      }

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
    val lastRotationDetected = mSensorListener.getLastRotationDetected()
      ?: return Orientation.UNKNOWN

    return mUtils.convertToDeviceOrientationFrom(lastRotationDetected)
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
    val deviceOrientation = mUtils.convertToDeviceOrientationFrom(rawDeviceOrientation)

    if (rawDeviceOrientation != ORIENTATION_UNKNOWN && deviceOrientation == Orientation.UNKNOWN) {
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
