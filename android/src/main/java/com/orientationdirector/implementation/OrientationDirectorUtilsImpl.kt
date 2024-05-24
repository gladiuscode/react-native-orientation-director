package com.orientationdirector.implementation

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import com.facebook.react.bridge.ReactContext

class OrientationDirectorUtilsImpl(val context: ReactContext) {

  fun getInterfaceRotation(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      context.currentActivity?.display?.rotation ?: Surface.ROTATION_0
    } else {
      val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      @Suppress("DEPRECATION")
      windowManager.defaultDisplay.rotation
    }
  }

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

  fun getOrientationFromRotation(rotation: Int): Orientation {
    return when(rotation) {
      Surface.ROTATION_270 -> Orientation.LANDSCAPE_RIGHT
      Surface.ROTATION_90 -> Orientation.LANDSCAPE_LEFT
      Surface.ROTATION_180 -> Orientation.PORTRAIT_UPSIDE_DOWN
      else -> Orientation.PORTRAIT
    }
  }
}
