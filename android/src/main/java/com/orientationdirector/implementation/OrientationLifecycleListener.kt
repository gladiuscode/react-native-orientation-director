package com.orientationdirector.implementation

import com.facebook.react.bridge.LifecycleEventListener

class OrientationLifecycleListener() : LifecycleEventListener {

  private var onHostResumeCallback: (() -> Unit)? = null
  private var onHostPauseCallback: (() -> Unit)? = null
  private var onHostDestroyCallback: (() -> Unit)? = null

  fun setOnHostResumeCallback(callback: () -> Unit) {
    this.onHostResumeCallback = callback
  }
  fun setOnHostPauseCallback(callback: () -> Unit) {
    this.onHostResumeCallback = callback
  }
  fun setOnHostDestroyCallback(callback: () -> Unit) {
    this.onHostDestroyCallback = callback
  }

  override fun onHostResume() {
    this.onHostResumeCallback?.invoke()
  }

  override fun onHostPause() {
    this.onHostPauseCallback?.invoke()
  }

  override fun onHostDestroy() {
    this.onHostDestroyCallback?.invoke()
  }

}
