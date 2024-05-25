package com.orientationdirector.implementation

import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.util.Log
import com.facebook.react.bridge.ReactContext

class OrientationAutoRotationObserver(val context: ReactContext, handler: Handler?) : ContentObserver(handler) {
  private var lastAutoRotationStatus: Boolean = isAutoRotationEnabled()

  fun getLastAutoRotationStatus(): Boolean {
    return lastAutoRotationStatus
  }

  override fun onChange(selfChange: Boolean) {
    super.onChange(selfChange)
    Log.d(NAME, "onChange")
    val status = isAutoRotationEnabled()
    Log.d(NAME, "onChange - status: $status")
    lastAutoRotationStatus = status
  }

  fun enable() {
    Log.d(NAME, "enable")
    context.contentResolver.registerContentObserver(
      Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
      true,
      this,
    )
  }

  fun disable() {
    Log.d(NAME, "disable")
    context.contentResolver.unregisterContentObserver(this)
  }

  private fun isAutoRotationEnabled(): Boolean {
    Log.d(NAME, "isAutoRotationEnabled")
    return try {
      Settings.System.getInt(context.contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 1;
    } catch (ex: Settings.SettingNotFoundException) {
      false
    }
  }

  companion object {
    const val NAME = "AutoRotationObserver"
  }
}
