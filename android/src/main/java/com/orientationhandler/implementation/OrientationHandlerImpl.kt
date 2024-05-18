package com.orientationhandler.implementation

import android.content.res.Configuration
import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerImpl internal constructor(private val context: ReactApplicationContext) {
  private var mUtils = OrientationHandlerUtilsImpl(context)

  fun getInterfaceOrientation(): Int {
    if (context.currentActivity == null) {
      return Configuration.ORIENTATION_UNDEFINED;
    }

    val activityOrientation = context.currentActivity!!.requestedOrientation
    if (
      !mUtils.isEitherPortraitOrReversePortrait(activityOrientation) &&
      !mUtils.isEitherLandscapeOrReverseLandscape(activityOrientation)
    ) {
      return mUtils.getDeviceOrientation()
    }

    return mUtils.getActivityOrientation(activityOrientation)
  }

  companion object {
    const val NAME = "OrientationHandlerImpl"
  }
}
