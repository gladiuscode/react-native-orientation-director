package com.orientationhandler

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.orientationhandler.implementation.OrientationHandlerImpl

class OrientationHandlerModule internal constructor(context: ReactApplicationContext) :
  OrientationHandlerSpec(context) {

  private var orientationHandlerImpl: OrientationHandlerImpl

  override fun getName(): String {
    return NAME
  }

  init {
    orientationHandlerImpl = OrientationHandlerImpl(context)
  }

  @ReactMethod()
  override fun getInterfaceOrientation(promise: Promise) {
    promise.resolve(orientationHandlerImpl.getInterfaceOrientation())
  }

  companion object {
    const val NAME = "OrientationHandler"
  }
}
