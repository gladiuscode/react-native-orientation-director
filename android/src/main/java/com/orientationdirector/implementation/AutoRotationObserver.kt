package com.orientationdirector.implementation

import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import com.facebook.react.bridge.ReactContext

class AutoRotationObserver(private val context: ReactContext, handler: Handler?) : ContentObserver(handler) {
  private var lastAutoRotationStatus: Boolean = isAutoRotationEnabled()

  fun getLastAutoRotationStatus(): Boolean {
    return lastAutoRotationStatus
  }

  override fun onChange(selfChange: Boolean) {
    super.onChange(selfChange)
    val status = isAutoRotationEnabled()
    lastAutoRotationStatus = status
  }

  fun enable() {
    context.contentResolver.registerContentObserver(
      Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION),
      true,
      this,
    )
  }

  fun disable() {
    context.contentResolver.unregisterContentObserver(this)
  }

  private fun isAutoRotationEnabled(): Boolean {
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
