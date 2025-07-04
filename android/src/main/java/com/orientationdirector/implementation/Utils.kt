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

  fun convertToDeviceOrientationFrom(orientationAngles: FloatArray): Orientation {
    val (_, pitchRadians, rollRadians) = orientationAngles;

    val pitchDegrees = Math.toDegrees(pitchRadians.toDouble()).toFloat()
    val rollDegrees = Math.toDegrees(rollRadians.toDouble()).toFloat()

    // This is needed to account for inaccuracy due to subtle movements such as tilting
    val pitchToleranceDefault = 5f
    val rollTolerance = 0f
    val toleranceForFaceUpOrDown = 5f;

    //////////////////////////////////////
    // These limits are set based on SensorManager.getOrientation reference
    // https://developer.android.com/develop/sensors-and-location/sensors/sensors_position#sensors-pos-orient
    //
    val portraitLimit = -90f
    val landscapeRightLimit = 180f
    val landscapeLeftLimit = -180f
    //
    //////////////////////////////////////

    val isPitchInLandscapeModeRange =
      checkIfValueIsBetweenTolerance(pitchDegrees, pitchToleranceDefault)
    val isPitchCloseToFaceUpOrDown =
      checkIfValueIsBetweenTolerance(pitchDegrees, toleranceForFaceUpOrDown)

    return when {
      checkIfRollIsCloseToFaceUp(rollDegrees) && isPitchCloseToFaceUpOrDown -> Orientation.FACE_UP
      checkIfRollIsCloseToFaceDown(rollDegrees) && isPitchCloseToFaceUpOrDown -> Orientation.FACE_DOWN
      rollDegrees in rollTolerance..landscapeRightLimit - rollTolerance && isPitchInLandscapeModeRange -> Orientation.LANDSCAPE_RIGHT
      rollDegrees in landscapeLeftLimit + rollTolerance..-rollTolerance && isPitchInLandscapeModeRange -> Orientation.LANDSCAPE_LEFT
      pitchDegrees in portraitLimit..pitchToleranceDefault -> Orientation.PORTRAIT
      else -> Orientation.PORTRAIT_UPSIDE_DOWN
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

  fun convertToInterfaceOrientationFrom(deviceOrientation: Orientation): Orientation {
    return when (deviceOrientation) {
      Orientation.PORTRAIT -> Orientation.PORTRAIT
      Orientation.LANDSCAPE_RIGHT -> Orientation.LANDSCAPE_LEFT
      Orientation.PORTRAIT_UPSIDE_DOWN -> Orientation.PORTRAIT_UPSIDE_DOWN
      Orientation.LANDSCAPE_LEFT -> Orientation.LANDSCAPE_RIGHT
      else -> Orientation.UNKNOWN
    }
  }

  fun getRequestedOrientation(): Int {
    if (context.currentActivity?.requestedOrientation == null) {
      return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    return context.currentActivity!!.requestedOrientation;
  }

  private fun checkIfValueIsBetweenTolerance(value: Float, tolerance: Float): Boolean {
    return value > -tolerance && value < tolerance
  }

  private fun checkIfRollIsCloseToFaceDown(value: Float): Boolean {
    val landscapeLimit = 180f
    val faceDownLimit = 170f

    return value in faceDownLimit..landscapeLimit  ||
      value in -landscapeLimit..-faceDownLimit;
  }

  private fun checkIfRollIsCloseToFaceUp(value: Float): Boolean {
    val landscapeLimit = 0f
    val faceUpLimit = 10f

    return value in landscapeLimit..faceUpLimit ||
      value in -faceUpLimit..-landscapeLimit
  }
}
