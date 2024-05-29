package com.orientationdirector.implementation

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.view.Surface
import android.view.WindowManager
import com.facebook.react.bridge.ReactContext

class Utils(private val context: ReactContext) {

  fun getInterfaceRotation(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      context.currentActivity?.display?.rotation ?: Surface.ROTATION_0
    } else {
      val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      @Suppress("DEPRECATION")
      windowManager.defaultDisplay.rotation
    }
  }

  fun convertToDeviceOrientationFrom(deviceRotation: Int): Orientation {
    return if (deviceRotation == -1) {
      Orientation.UNKNOWN
    } else if (deviceRotation > 355 || deviceRotation < 5) {
      Orientation.PORTRAIT
    } else if (deviceRotation in 86..94) {
      Orientation.LANDSCAPE_RIGHT
    } else if (deviceRotation in 176..184) {
      Orientation.PORTRAIT_UPSIDE_DOWN
    } else if (deviceRotation in 266..274) {
      Orientation.LANDSCAPE_LEFT
    } else {
      return Orientation.UNKNOWN
    }
  }

  fun convertToActivityOrientationFrom(orientation: Orientation): Int {
    return when (orientation) {
      Orientation.LANDSCAPE_RIGHT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      Orientation.PORTRAIT_UPSIDE_DOWN -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      Orientation.LANDSCAPE_LEFT -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
  }

  fun convertToOrientationFromJsValue(jsValue: Int): Orientation {
    return when (jsValue) {
      2 -> Orientation.LANDSCAPE_RIGHT
      3 -> Orientation.PORTRAIT_UPSIDE_DOWN
      4 -> Orientation.LANDSCAPE_LEFT
      else -> Orientation.PORTRAIT
    }
  }

  fun convertToOrientationFromScreenRotation(screenRotation: Int): Orientation {
    return when(screenRotation) {
      Surface.ROTATION_270 -> Orientation.LANDSCAPE_RIGHT
      Surface.ROTATION_90 -> Orientation.LANDSCAPE_LEFT
      Surface.ROTATION_180 -> Orientation.PORTRAIT_UPSIDE_DOWN
      else -> Orientation.PORTRAIT
    }
  }

  fun convertToInterfaceOrientationFrom(deviceOrientation: Orientation): Orientation {
    return when(deviceOrientation) {
      Orientation.PORTRAIT -> Orientation.PORTRAIT
      Orientation.LANDSCAPE_RIGHT -> Orientation.LANDSCAPE_LEFT
      Orientation.PORTRAIT_UPSIDE_DOWN -> Orientation.PORTRAIT_UPSIDE_DOWN
      Orientation.LANDSCAPE_LEFT -> Orientation.LANDSCAPE_RIGHT
      else -> Orientation.UNKNOWN
    }
  }
}
