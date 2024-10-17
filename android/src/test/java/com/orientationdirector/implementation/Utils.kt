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
      "When pitch is half PI radians and roll is -0 radians, orientation should be portrait",
      Orientation.PORTRAIT,
      orientation
    )
  }

  @Test
  fun assert_device_orientation_is_landscape_right() {

    val orientationAngles = FloatArray(3)

    orientationAngles[1] = 0f
    orientationAngles[2] = (Math.PI / 2).toFloat()

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is 0 radians and roll is half PI radians, orientation should be landscape right",
      Orientation.LANDSCAPE_RIGHT,
      orientation
    )
  }

  @Test
  fun assert_device_orientation_is_portrait_upside_down() {

    val orientationAngles = FloatArray(3)

    orientationAngles[1] = (Math.PI / 2).toFloat()
    orientationAngles[2] = 0f

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is half PI radians and roll is 0 radians, orientation should be portrait upside down",
      Orientation.PORTRAIT_UPSIDE_DOWN, orientation
    )
  }

  @Test
  fun assert_device_orientation_is_landscape_left() {

    val orientationAngles = FloatArray(3)

    orientationAngles[1] = 0f
    orientationAngles[2] = -(Math.PI / 2).toFloat()

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is 0 radians and roll is negative half PI radians, orientation should be landscape left",
      Orientation.LANDSCAPE_LEFT,
      orientation
    )
  }

  @Test
  fun assert_device_orientation_is_face_down() {

    val orientationAngles = FloatArray(3)

    orientationAngles[1] = 0f
    orientationAngles[2] = -(Math.PI).toFloat()

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is 0 radians and roll is negative PI radians, orientation should be face down",
      Orientation.FACE_DOWN,
      orientation
    )
  }

  @Test
  fun assert_device_orientation_is_face_up() {

    val orientationAngles = FloatArray(3)

    orientationAngles[1] = 0f
    orientationAngles[2] = -0f

    val orientation = mUtils.convertToDeviceOrientationFrom(orientationAngles)

    assertEquals(
      "When pitch is 0 radians and roll is negative 0 radians, orientation should be face up",
      Orientation.FACE_UP,
      orientation
    )
  }

}
