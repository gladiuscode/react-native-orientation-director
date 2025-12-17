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
  private var mBroadcastReceiver = ConfigurationChangedBroadcastReceiver(context)

  private var initialSupportedInterfaceOrientations = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
  private var lastInterfaceOrientation = Orientation.UNKNOWN
  private var lastDeviceOrientation = Orientation.UNKNOWN
  private var initialized = false
  private var isLocked: Boolean = false
  private var areOrientationSensorsEnabled = false;
  private var didComputeInitialDeviceOrientation = false;

  init {
    mOrientationSensorsEventListener.setOnOrientationAnglesChangedCallback { orientation ->
      onOrientationAnglesChanged(orientation)
    }

    mAutoRotationObserver.enable()

    mBroadcastReceiver.setOnReceiveCallback {
      checkInterfaceOrientation(false)
    }

    // NOTE(1.init): This is needed to compute the initial device orientation
    mOrientationSensorsEventListener.enable()

    context.addLifecycleEventListener(mLifecycleListener)
    mLifecycleListener.setOnHostResumeCallback {
      if (areOrientationSensorsEnabled) {
        mOrientationSensorsEventListener.enable()
        mAutoRotationObserver.enable()
      }
      mBroadcastReceiver.register()
    }
    mLifecycleListener.setOnHostPauseCallback {
      if (areOrientationSensorsEnabled) {
        mOrientationSensorsEventListener.disable()
        mAutoRotationObserver.disable()
      }
      mBroadcastReceiver.unregister()
    }
    mLifecycleListener.setOnHostDestroyCallback {
      if (areOrientationSensorsEnabled) {
        mOrientationSensorsEventListener.disable()
        mAutoRotationObserver.disable()
      }
      mBroadcastReceiver.unregister()
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

    val orientationCanBeUpdatedDirectly = jsOrientation != Orientation.LANDSCAPE;
    if (orientationCanBeUpdatedDirectly) {
      updateLastInterfaceOrientationTo(jsOrientation)
      return
    }

    val lastInterfaceOrientationIsAlreadyInLandscape =
      lastInterfaceOrientation == Orientation.LANDSCAPE_RIGHT
        || lastInterfaceOrientation == Orientation.LANDSCAPE_LEFT
    if (lastInterfaceOrientationIsAlreadyInLandscape) {
      updateLastInterfaceOrientationTo(lastInterfaceOrientation)
      return;
    }

    val systemDefaultLandscapeOrientation = Orientation.LANDSCAPE_RIGHT
    updateLastInterfaceOrientationTo(systemDefaultLandscapeOrientation)
  }

  fun unlock() {
    context.currentActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

    updateIsLockedTo(false)
    checkInterfaceOrientation()
  }

  fun resetSupportedInterfaceOrientations() {
    context.currentActivity?.requestedOrientation = initialSupportedInterfaceOrientations
    updateIsLockedTo(initIsLocked())
    updateLastInterfaceOrientationTo(initInterfaceOrientation())
  }

  fun enableOrientationSensors() {
    areOrientationSensorsEnabled = true
    mOrientationSensorsEventListener.enable()
  }

  fun disableOrientationSensors() {
    areOrientationSensorsEnabled = false
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

    checkInterfaceOrientation()

    // NOTE(2.init): This is needed to disable sensors if they were needed just for the initial
    //  device computation.
    if (!didComputeInitialDeviceOrientation && !areOrientationSensorsEnabled) {
      didComputeInitialDeviceOrientation = true
      mOrientationSensorsEventListener.disable()
    }
  }

  private fun checkInterfaceOrientation(skipIfAutoRotationIsDisabled: Boolean = true) {
    if (skipIfAutoRotationIsDisabled && !mAutoRotationObserver.getLastAutoRotationStatus()) {
      return
    }

    if (isLocked) {
      return
    }

    if (lastDeviceOrientation != Orientation.LANDSCAPE_RIGHT && lastDeviceOrientation != Orientation.LANDSCAPE_LEFT) {
      val rotation = mUtils.getInterfaceRotation()
      val newInterfaceOrientation = mUtils.convertToOrientationFromScreenRotation(rotation)

      updateLastInterfaceOrientationTo(newInterfaceOrientation)
      return
    }

    /**
     * The reason we invert the interface orientation is to match iOS behavior with
     * UIInterfaceOrientation when device is in landscape mode
     */
    val interfaceOrientationBasedOnDeviceOne =
      if (lastDeviceOrientation == Orientation.LANDSCAPE_RIGHT) {
        Orientation.LANDSCAPE_LEFT
      } else {
        Orientation.LANDSCAPE_RIGHT
      }

    updateLastInterfaceOrientationTo(interfaceOrientationBasedOnDeviceOne)
  }

  private fun updateIsLockedTo(value: Boolean) {
    mEventManager.sendLockDidChange(value)
    isLocked = value
  }

  private fun updateLastInterfaceOrientationTo(value: Orientation) {
    if (value == lastInterfaceOrientation) {
      return
    }

    lastInterfaceOrientation = value
    mEventManager.sendInterfaceOrientationDidChange(value.ordinal)
  }

  companion object {
    const val NAME = "OrientationDirector"
  }
}
