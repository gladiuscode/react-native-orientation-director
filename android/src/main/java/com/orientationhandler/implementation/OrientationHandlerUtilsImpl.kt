package com.orientationhandler.implementation

import android.content.pm.ActivityInfo
import android.os.Build
import android.view.Surface
import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerUtilsImpl(private val context: ReactApplicationContext) {
  fun getInterfaceOrientationFromDeviceOrientation(): InterfaceOrientation {
    val rotation = getDeviceRotation()

    return when (rotation) {
      Surface.ROTATION_0 -> InterfaceOrientation.PORTRAIT
      // TODO: Check in real device, since in emulator it doesn't work
      Surface.ROTATION_180 -> InterfaceOrientation.PORTRAIT_UPSIDE_DOWN
      Surface.ROTATION_90 -> InterfaceOrientation.LANDSCAPE_RIGHT
      Surface.ROTATION_270 -> InterfaceOrientation.LANDSCAPE_LEFT
      else -> InterfaceOrientation.UNKNOWN
    }
  }

  fun getInterfaceOrientationFromActivityOrientation(activityInfo: Int): InterfaceOrientation {
    return when (activityInfo) {
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> InterfaceOrientation.LANDSCAPE_LEFT
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> InterfaceOrientation.LANDSCAPE_RIGHT
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> InterfaceOrientation.PORTRAIT_UPSIDE_DOWN
      else -> InterfaceOrientation.PORTRAIT
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

  fun getActivityOrientationFromInterfaceOrientation(interfaceOrientationValue: Int): Int {
    return when (interfaceOrientationValue) {
      InterfaceOrientation.PORTRAIT_UPSIDE_DOWN.ordinal -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      InterfaceOrientation.LANDSCAPE_LEFT.ordinal -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      InterfaceOrientation.LANDSCAPE_RIGHT.ordinal -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
  }

  private fun getDeviceRotation(): Int? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return context.display?.rotation
    }

    @Suppress("DEPRECATION")
    return context.currentActivity?.windowManager?.defaultDisplay?.rotation
  }

}
