package com.orientationdirector.implementation

import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.BridgeReactContext
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UtilsTest {

  private var context = BridgeReactContext(ApplicationProvider.getApplicationContext())

  private var mUtils = Utils(context)

  @Test
  fun assert_device_orientation_is_portrait() {
    val orientationAngles = FloatArray(3)

    // pitch radians
    orientationAngles[1] = -(Math.PI / 2).toFloat()
    // roll radians
    orientationAngles[2] = -0f

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is half PI radians and roll is -0 radians, orientation should be PORTRAIT",
      Orientation.PORTRAIT,
      orientation
    )
  }

}
