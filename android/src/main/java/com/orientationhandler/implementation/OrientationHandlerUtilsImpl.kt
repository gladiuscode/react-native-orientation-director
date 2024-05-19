package com.orientationhandler.implementation

import android.content.pm.ActivityInfo

class OrientationHandlerUtilsImpl() {

  fun getDeviceOrientationFrom(rotation: Int): Orientation {
    var orientation = Orientation.UNKNOWN

    if (rotation == -1) {
      orientation = Orientation.UNKNOWN
    } else if (rotation > 355 || rotation < 5) {
      orientation = Orientation.PORTRAIT
    } else if (rotation in 86..94) {
      orientation = Orientation.LANDSCAPE_RIGHT
    } else if (rotation in 176..184) {
      orientation = Orientation.PORTRAIT_UPSIDE_DOWN
    } else if (rotation in 266..274) {
      orientation = Orientation.LANDSCAPE_LEFT
    }

    return orientation
  }

  fun getInterfaceOrientationFrom(activityInfo: Int): Orientation {
    return when (activityInfo) {
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> Orientation.LANDSCAPE_RIGHT
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> Orientation.PORTRAIT_UPSIDE_DOWN
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> Orientation.LANDSCAPE_LEFT
      else -> Orientation.PORTRAIT
    };
  }

  fun isActivityInLandscapeOrientation(orientation: Int): Boolean {
    return listOf(
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
    ).contains(orientation);
  }

  fun isActivityInPortraitOrientation(orientation: Int): Boolean {
    return listOf(
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
    ).contains(orientation);
  }

  fun getActivityOrientationFrom(interfaceOrientation: Orientation): Int {
    return when (interfaceOrientation) {
      Orientation.LANDSCAPE_RIGHT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      Orientation.PORTRAIT_UPSIDE_DOWN -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      Orientation.LANDSCAPE_LEFT -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
  }

  fun getOrientationEnumFrom(jsOrientation: Int): Orientation {
    return when (jsOrientation) {
      2 -> Orientation.LANDSCAPE_RIGHT
      3 -> Orientation.PORTRAIT_UPSIDE_DOWN
      4 -> Orientation.LANDSCAPE_LEFT
      else -> Orientation.PORTRAIT
    }
  }
}
