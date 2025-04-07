package com.orientationdirector.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.facebook.react.bridge.ReactApplicationContext

/**
 * This custom broadcast receiver is needed to properly update the interface orientation when
 * the user has disabled the automatic rotation.
 *
 * It listens for an explicit intent that the MainActivity can send in the onConfigurationChanged
 * method and calls a custom callback that is set in the main implementation init
 */
class ConfigurationChangedBroadcastReceiver internal constructor(private val context: ReactApplicationContext) :
  BroadcastReceiver() {

  private var isRegistered = false

  private var onReceiveCallback: ((intent: Intent?) -> Unit)? = null

  override fun onReceive(context: Context?, intent: Intent?) {
    this.onReceiveCallback?.invoke(intent)
  }

  fun setOnReceiveCallback(callback: (intent: Intent?) -> Unit) {
    onReceiveCallback = callback
  }

  /**
   * This method registers the receiver by checking the api we are currently running with.
   * With the latest changes in Android 14, we need to explicitly set the `Context.RECEIVER_NOT_EXPORTED`
   * flag.
   */
  fun register() {
    val filter = IntentFilter("${context.packageName}.$CUSTOM_INTENT_ACTION")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      context.registerReceiver(this, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
      context.registerReceiver(this, filter)
    }

    isRegistered = true
  }

  fun unregister() {
    if (!isRegistered) {
      return
    }

    try {
      context.unregisterReceiver(this)
    } catch(_: IllegalArgumentException) {
    } finally {
      isRegistered = false
    }
  }

  companion object {
    const val CUSTOM_INTENT_ACTION = "CONFIGURATION_CHANGED"
  }
}
