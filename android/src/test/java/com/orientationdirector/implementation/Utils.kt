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

  // This is just a random test, I'm
  // still trying to understand how
  // to properly write them.
  @Test
  fun assert_orientation_is_portrait() {

    val orientationAngles = FloatArray(3)
    orientationAngles[0] = 30f
    orientationAngles[0] = 30f
    orientationAngles[0] = 30f

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(orientation, Orientation.LANDSCAPE_RIGHT)
  }

}
