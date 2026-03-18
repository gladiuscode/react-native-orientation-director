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

  fun convertToActivityOrientationFrom(orientation: Orientation): Int {
    return when (orientation) {
      Orientation.LANDSCAPE_RIGHT -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      Orientation.PORTRAIT_UPSIDE_DOWN -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      Orientation.LANDSCAPE_LEFT -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      Orientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
  }

  fun convertToOrientationFromJsValue(jsValue: Int): Orientation {
    return when (jsValue) {
      2 -> Orientation.LANDSCAPE_RIGHT
      3 -> Orientation.PORTRAIT_UPSIDE_DOWN
      4 -> Orientation.LANDSCAPE_LEFT
      5 -> Orientation.LANDSCAPE
      else -> Orientation.PORTRAIT
    }
  }

  fun convertToOrientationFromScreenRotation(screenRotation: Int): Orientation {
    return when (screenRotation) {
      Surface.ROTATION_270 -> Orientation.LANDSCAPE_RIGHT
      Surface.ROTATION_90 -> Orientation.LANDSCAPE_LEFT
      Surface.ROTATION_180 -> Orientation.PORTRAIT_UPSIDE_DOWN
      else -> Orientation.PORTRAIT
    }
  }

}
