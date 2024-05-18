package com.orientationhandler.implementation

import android.content.pm.ActivityInfo
import android.os.Build
import android.view.Surface
import com.facebook.react.bridge.ReactApplicationContext

class OrientationHandlerUtilsImpl(private val context: ReactApplicationContext)  {
  private val UNKNOWN_ROTATION = -1;

  fun getDeviceOrientation(): Int {
    val rotation = getDeviceRotation()

    return when (rotation) {
      Surface.ROTATION_0 -> 1
      // TODO: Check in real device, since in emulator it doesn't work
      Surface.ROTATION_180 -> 2
      Surface.ROTATION_90 -> 3
      Surface.ROTATION_270 -> 4
      else -> UNKNOWN_ROTATION
    }
  }

  fun getActivityOrientation(activityOrientation: Int): Int {
    return when (activityOrientation) {
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> 4
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> 3
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> 2
      else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    };
  }

  fun isEitherLandscapeOrReverseLandscape(orientation: Int): Boolean {
    return listOf(
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
    ).contains(orientation);
  }

  fun isEitherPortraitOrReversePortrait(orientation: Int): Boolean {
    return listOf(
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
    ).contains(orientation);
  }

  private fun getDeviceRotation(): Int? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      return context.display?.rotation
    }

    @Suppress("DEPRECATION")
    return context.currentActivity?.windowManager?.defaultDisplay?.rotation
  }

}
