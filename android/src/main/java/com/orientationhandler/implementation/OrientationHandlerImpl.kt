package com.orientationhandler.implementation

import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl(context)
  private var mEventEmitter = OrientationEventManager(context)
  private var mSensorListener = OrientationSensorListener(context, mEventEmitter)
  private var isLocked: Boolean = false

  init {
    mSensorListener.setCheckInterfaceOrientationCallback { deviceOrientation ->
      checkInterfaceOrientation(deviceOrientation)
    }

    if (mSensorListener.canDetectOrientation()) {
      mSensorListener.enable()
    } else {
      mSensorListener.disable()
    }
  }

  fun getInterfaceOrientation(): Int {
    if (context.currentActivity == null) {
      return InterfaceOrientation.UNKNOWN.ordinal
    }

    val activityOrientation = context.currentActivity!!.requestedOrientation
    if (
      mUtils.isActivityInPortraitOrientation(activityOrientation) ||
      mUtils.isActivityInLandscapeOrientation(activityOrientation)
    ) {
      return mUtils.getInterfaceOrientationFromActivityOrientation(activityOrientation).ordinal
    }

    return mUtils.getInterfaceOrientationFromDeviceOrientation().ordinal
  }

  fun lockTo(rawOrientation: Int) {
    val interfaceOrientation = mUtils.mapToInterfaceOrientation(rawOrientation)
    val screenOrientation =
      mUtils.getActivityOrientationFromInterfaceOrientation(interfaceOrientation)
    context.currentActivity?.requestedOrientation = screenOrientation
    mEventEmitter.sendInterfaceOrientationDidChange(interfaceOrientation.ordinal)
    isLocked = true
  }

  private fun checkInterfaceOrientation(deviceOrientation: Int) {
    if (isLocked) {
      return
    }

    val interfaceOrientation = getInterfaceOrientation()
    if (interfaceOrientation == deviceOrientation) {
      return
    }

    mEventEmitter.sendInterfaceOrientationDidChange(deviceOrientation)
  }

  companion object {
    const val NAME = "OrientationHandlerImpl"
  }
}
