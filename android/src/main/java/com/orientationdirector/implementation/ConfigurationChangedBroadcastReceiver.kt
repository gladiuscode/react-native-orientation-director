package com.orientationdirector.implementation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.facebook.react.bridge.ReactApplicationContext

class ConfigurationChangedBroadcastReceiver internal constructor(private val context: ReactApplicationContext) :
  BroadcastReceiver() {

  private var onReceiveCallback: ((intent: Intent?) -> Unit)? = null

  override fun onReceive(context: Context?, intent: Intent?) {
    this.onReceiveCallback?.invoke(intent)
  }

  fun setOnReceiveCallback(callback: (intent: Intent?) -> Unit) {
    onReceiveCallback = callback
  }

  fun register() {
    val filter = IntentFilter("${context.packageName}.CONFIGURATION_CHANGED")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      context.registerReceiver(this, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
      context.registerReceiver(this, filter)
    }
  }

  fun unregister() {
    context.unregisterReceiver(this)
  }
}
