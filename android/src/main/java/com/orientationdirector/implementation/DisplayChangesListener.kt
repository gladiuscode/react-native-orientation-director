package com.orientationdirector.implementation

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.facebook.react.bridge.ReactApplicationContext

/**
 * Notifies when the app moves to a display with a different physical size,
 * e.g. when a foldable device is folded or unfolded.
 *
 * DisplayManager notifies any display change, rotations included, so the
 * physical size is compared regardless of its orientation.
 */
class DisplayChangesListener internal constructor(private val context: ReactApplicationContext) :
  DisplayManager.DisplayListener {

  private val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
  private val handler = Handler(Looper.getMainLooper())
  private var isRegistered = false
  private var lastPhysicalSize: Pair<Int, Int>? = null

  private var onDisplayChangedCallback: ((widthDp: Double, heightDp: Double) -> Unit)? = null

  fun setOnDisplayChangedCallback(callback: (widthDp: Double, heightDp: Double) -> Unit) {
    onDisplayChangedCallback = callback
  }

  fun register() {
    if (isRegistered) {
      return
    }

    displayManager.registerDisplayListener(this, handler)
    isRegistered = true

    // The display might have changed while the listener was unregistered
    sync()
  }

  fun unregister() {
    if (!isRegistered) {
      return
    }

    displayManager.unregisterDisplayListener(this)
    isRegistered = false
  }

  override fun onDisplayAdded(displayId: Int) = Unit

  override fun onDisplayRemoved(displayId: Int) = Unit

  override fun onDisplayChanged(displayId: Int) {
    sync()
  }

  /**
   * Compares the current display with the last known one.
   * It is also needed when the activity moves between existing displays,
   * as DisplayManager doesn't notify it since no display has changed.
   */
  fun sync() {
    val size = computeDisplaySize() ?: return
    val physicalSize = Pair(minOf(size.x, size.y), maxOf(size.x, size.y))

    val previousPhysicalSize = lastPhysicalSize
    lastPhysicalSize = physicalSize

    // First sync, nothing changed yet
    if (previousPhysicalSize == null || previousPhysicalSize == physicalSize) {
      return
    }

    val density = computeDensity()
    onDisplayChangedCallback?.invoke(size.x / density, size.y / density)
  }

  /**
   * Returns the size of the display the activity is on, in pixels,
   * in the orientation it is currently displayed.
   */
  private fun computeDisplaySize(): Point? {
    val activity = context.currentActivity ?: return null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      val bounds = activity.windowManager.maximumWindowMetrics.bounds
      return Point(bounds.width(), bounds.height())
    }

    val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val size = Point()
    @Suppress("DEPRECATION")
    windowManager.defaultDisplay.getRealSize(size)
    return size
  }

  private fun computeDensity(): Double {
    val activity = context.currentActivity

    if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      return activity.windowManager.maximumWindowMetrics.density.toDouble()
    }

    return (activity?.resources ?: context.resources).displayMetrics.density.toDouble()
  }
}
