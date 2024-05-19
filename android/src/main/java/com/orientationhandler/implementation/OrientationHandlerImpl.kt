package com.orientationhandler.implementation

import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl()
  private var mEventEmitter = OrientationEventManager(context)
  private var mSensorListener = OrientationSensorListener(context)
  private var isLocked: Boolean = false
  private var lastInterfaceOrientation = Orientation.UNKNOWN

  init {
    mSensorListener.setOnOrientationChangedCallback { orientation ->
      onOrientationChanged(orientation)
    }

    if (mSensorListener.canDetectOrientation()) {
      mSensorListener.enable()
    } else {
      mSensorListener.disable()
    }

    lastInterfaceOrientation = getInterfaceOrientation()
  }

  fun getInterfaceOrientation(): Orientation {
    if (isLocked) {
      return lastInterfaceOrientation
    }

    if (context.currentActivity == null) {
      return lastInterfaceOrientation
    }

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

  fun getDeviceOrientation(): Orientation {
    val lastRotationDetected = mSensorListener.getLastRotationDetected()
      ?: return Orientation.UNKNOWN

    return mUtils.getDeviceOrientationFrom(lastRotationDetected)
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

  private fun onOrientationChanged(rawDeviceOrientation: Int) {
    val deviceOrientation = mUtils.getDeviceOrientationFrom(rawDeviceOrientation)
    mEventEmitter.sendDeviceOrientationDidChange(deviceOrientation.ordinal)
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
