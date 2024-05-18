package com.orientationhandler.implementation

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl(context)
  private var mEventEmitter = OrientationEventManager(context)
  private var mSensorListener = OrientationSensorListener(context, mEventEmitter)

  init {
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
    val screenOrientation = mUtils.getActivityOrientationFromInterfaceOrientation(interfaceOrientation)
    context.currentActivity?.requestedOrientation = screenOrientation
    mEventEmitter.sendInterfaceOrientationDidChange(interfaceOrientation.ordinal)
  }

  companion object {
    const val NAME = "OrientationHandlerImpl"
  }
}
