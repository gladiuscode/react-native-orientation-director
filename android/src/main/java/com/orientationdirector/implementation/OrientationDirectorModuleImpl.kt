package com.orientationdirector.implementation

import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import com.facebook.react.bridge.ReactApplicationContext

class OrientationDirectorModuleImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = Utils(context)
  private var mEventManager = EventManager(context)
  private var mOrientationSensorsEventListener = OrientationSensorsEventListener(context)
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
  private var didComputeInitialDeviceOrientation = false;

  init {
    mOrientationSensorsEventListener.setOnOrientationAnglesChangedCallback { orientation ->
      onOrientationAnglesChanged(orientation)
    }

    mAutoRotationObserver.enable()

    context.addLifecycleEventListener(mLifecycleListener)
    mLifecycleListener.setOnHostResumeCallback {
      if (!didComputeInitialDeviceOrientation) {
        mOrientationSensorsEventListener.enable()
      }
      mAutoRotationObserver.enable()
    }
    mLifecycleListener.setOnHostPauseCallback {
      if (initialized) {
        mOrientationSensorsEventListener.disable()
        mAutoRotationObserver.disable()
      }
    }
    mLifecycleListener.setOnHostDestroyCallback {
      mOrientationSensorsEventListener.disable()
      mAutoRotationObserver.disable()
    }

    initialSupportedInterfaceOrientations =
      context.currentActivity?.requestedOrientation ?: initialSupportedInterfaceOrientations
    lastInterfaceOrientation = initInterfaceOrientation()
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

  fun enableOrientationSensors() {
    mOrientationSensorsEventListener.enable()
  }

  fun disableOrientationSensor() {
    mOrientationSensorsEventListener.disable()
  }

  private fun initInterfaceOrientation(): Orientation {
    val rotation = mUtils.getInterfaceRotation()
    return mUtils.convertToOrientationFromScreenRotation(rotation)
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
      return
    }

    if (lastDeviceOrientation == deviceOrientation) {
      return
    }

    mEventManager.sendDeviceOrientationDidChange(deviceOrientation.ordinal)
    lastDeviceOrientation = deviceOrientation

    adaptInterfaceTo(deviceOrientation)

    if (!didComputeInitialDeviceOrientation) {
      didComputeInitialDeviceOrientation = true
      mOrientationSensorsEventListener.disable()
    }
  }

  private fun adaptInterfaceTo(deviceOrientation: Orientation) {
    if (!mAutoRotationObserver.getLastAutoRotationStatus()) {
      return
    }

    if (isLocked) {
      return
    }

    var newInterfaceOrientation = mUtils.convertToInterfaceOrientationFrom(deviceOrientation);

    /**
     * When the device orientation is either face up or face down,
     * we can't match it to an interface orientation, because
     * it could be either portrait or any landscape.
     * So we read it from the system itself.
     */
    if (newInterfaceOrientation == Orientation.UNKNOWN) {
      val rotation = mUtils.getInterfaceRotation()
      newInterfaceOrientation = mUtils.convertToOrientationFromScreenRotation(rotation)
    }

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
