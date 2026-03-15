   package com.orientationdirector.implementation

   import com.facebook.react.bridge.WritableMap

   interface EventManagerDelegate {
       fun sendOnDeviceOrientationChanged(params: WritableMap)
       fun sendOnInterfaceOrientationChanged(params: WritableMap)
       fun sendOnLockChanged(params: WritableMap)
   }
