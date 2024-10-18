package com.orientationdirector.implementation

import android.content.pm.ActivityInfo
import android.os.Build
import android.view.Surface
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.BridgeReactContext
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class UtilsTest {
  private var context = BridgeReactContext(ApplicationProvider.getApplicationContext())
  private var mUtils = Utils(context)

  @Config(
    sdk = [Build.VERSION_CODES.N],
    qualifiers = "land"
  )
  @Test
  fun assert_interface_rotation_matches_current_landscape() {
    val rotation = mUtils.getInterfaceRotation()

    assertEquals(
      "When current interface orientation is landscape, rotation should be 1",
      Surface.ROTATION_90,
      rotation
    )
  }

  @Config(
    sdk = [Build.VERSION_CODES.R],
    qualifiers = "port"
  )
  @Test
  fun assert_interface_rotation_matches_current_portrait() {
    val rotation = mUtils.getInterfaceRotation()

    assertEquals(
      "When current interface orientation is portrait, rotation should be 0",
      Surface.ROTATION_0,
      rotation
    )
  }

  @Test
  fun assert_device_orientation_is_portrait() {
    val orientationAngles = FloatArray(3)
    orientationAngles[1] = -(Math.PI / 2).toFloat()
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

  @Test
  fun assert_activity_orientation_conversion_from_portrait() {
    val activityOrientation = mUtils.convertToActivityOrientationFrom(Orientation.PORTRAIT);

    assertEquals(
      "When orientation is portrait, activity orientation should be portrait",
      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
      activityOrientation
    )
  }

  @Test
  fun assert_activity_orientation_conversion_from_landscape_right() {
    val activityOrientation = mUtils.convertToActivityOrientationFrom(Orientation.LANDSCAPE_RIGHT);

    assertEquals(
      "When orientation is landscape right, activity orientation should be landscape",
      ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
      activityOrientation
    )
  }

  @Test
  fun assert_activity_orientation_conversion_from_portrait_upside_down() {
    val activityOrientation =
      mUtils.convertToActivityOrientationFrom(Orientation.PORTRAIT_UPSIDE_DOWN);

    assertEquals(
      "When orientation is portrait upside down, activity orientation should be reverse portrait",
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
      activityOrientation
    )
  }

  @Test
  fun assert_activity_orientation_conversion_from_landscape_left() {
    val activityOrientation = mUtils.convertToActivityOrientationFrom(Orientation.LANDSCAPE_LEFT);

    assertEquals(
      "When orientation is landscape left, activity orientation should be reverse landscape",
      ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
      activityOrientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_js_portrait() {
    val orientation = mUtils.convertToOrientationFromJsValue(1)

    assertEquals(
      "When js value is 1, orientation should be portrait",
      Orientation.PORTRAIT,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_js_landscape_right() {
    val orientation = mUtils.convertToOrientationFromJsValue(2)

    assertEquals(
      "When js value is 2, orientation should be landscape right",
      Orientation.LANDSCAPE_RIGHT,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_js_portrait_upside_down() {
    val orientation = mUtils.convertToOrientationFromJsValue(3)

    assertEquals(
      "When js value is 3, orientation should be portrait upside down",
      Orientation.PORTRAIT_UPSIDE_DOWN,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_js_landscape_left() {
    val orientation = mUtils.convertToOrientationFromJsValue(4)

    assertEquals(
      "When js value is 4, orientation should be landscape left",
      Orientation.LANDSCAPE_LEFT,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_screen_rotation_0() {
    val orientation = mUtils.convertToOrientationFromScreenRotation(Surface.ROTATION_0)

    assertEquals(
      "When screen rotation is 0, orientation should be portrait",
      Orientation.PORTRAIT,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_screen_rotation_90() {
    val orientation = mUtils.convertToOrientationFromScreenRotation(Surface.ROTATION_90)

    assertEquals(
      "When screen rotation is 90, orientation should be landscape left",
      Orientation.LANDSCAPE_LEFT,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_screen_rotation_180() {
    val orientation = mUtils.convertToOrientationFromScreenRotation(Surface.ROTATION_180)

    assertEquals(
      "When screen rotation is 180, orientation should be portrait upside down",
      Orientation.PORTRAIT_UPSIDE_DOWN,
      orientation
    )
  }

  @Test
  fun assert_orientation_conversion_from_screen_rotation_270() {
    val orientation = mUtils.convertToOrientationFromScreenRotation(Surface.ROTATION_270)

    assertEquals(
      "When screen rotation is 270, orientation should be landscape right",
      Orientation.LANDSCAPE_RIGHT,
      orientation
    )
  }

  @Test
  fun assert_interface_orientation_conversion_from_device_portrait() {
    val orientation = mUtils.convertToInterfaceOrientationFrom(Orientation.PORTRAIT)

    assertEquals(
      "When device orientation is portrait, interface orientation should be portrait",
      Orientation.PORTRAIT,
      orientation
    )
  }

  @Test
  fun assert_interface_orientation_conversion_from_device_landscape_right() {
    val orientation = mUtils.convertToInterfaceOrientationFrom(Orientation.LANDSCAPE_RIGHT)

    assertEquals(
      "When device orientation is landscape right, interface orientation should be landscape left",
      Orientation.LANDSCAPE_LEFT,
      orientation
    )
  }

  @Test
  fun assert_interface_orientation_conversion_from_device_portrait_upside_down() {
    val orientation = mUtils.convertToInterfaceOrientationFrom(Orientation.PORTRAIT_UPSIDE_DOWN)

    assertEquals(
      "When device orientation is portrait upside down, interface orientation should be portrait upside down",
      Orientation.PORTRAIT_UPSIDE_DOWN,
      orientation
    )
  }

  @Test
  fun assert_interface_orientation_conversion_from_device_landscape_left() {
    val orientation = mUtils.convertToInterfaceOrientationFrom(Orientation.LANDSCAPE_LEFT)

    assertEquals(
      "When device orientation is landscape left, interface orientation should be landscape right",
      Orientation.LANDSCAPE_RIGHT,
      orientation
    )
  }
}
