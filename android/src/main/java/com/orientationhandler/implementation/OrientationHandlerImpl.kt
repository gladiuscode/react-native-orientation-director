package com.orientationhandler.implementation

import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl(context)

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
    val screenOrientation = mUtils.getActivityOrientationFromInterfaceOrientation(rawOrientation)
    context.currentActivity?.requestedOrientation = screenOrientation
  }

  companion object {
    const val NAME = "OrientationHandlerImpl"
  }
}
