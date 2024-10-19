package com.orientationdirector.implementation

import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.BridgeReactContext
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
class OrientationDirectorModuleImplTest {
  private var context = BridgeReactContext(ApplicationProvider.getApplicationContext())
  private var mModule = OrientationDirectorModuleImpl(context)

  @Test
  @Config(
    qualifiers = "port"
  )
  fun assert_initial_orientation_matches_portrait() {
    val orientation = mModule.getInterfaceOrientation()

    assertEquals(
      "When user starts the app with the device in portrait, the initial interface should be portrait",
      Orientation.PORTRAIT,
      orientation
    )
  }

  @Test
  @Config(
    qualifiers = "land"
  )
  fun assert_initial_orientation_matches_landscape_left() {
    val orientation = mModule.getInterfaceOrientation()

    assertEquals(
      "When user starts the app with the device in landscape, the initial interface should be landscape left",
      Orientation.LANDSCAPE_LEFT,
      orientation
    )
  }

  @Test
  fun assert_initial_device_orientation_matches_unknown_at_startup() {
    val orientation = mModule.getDeviceOrientation()

    assertEquals(
      "When user starts the app, the initial device orientation should be unknown",
      Orientation.UNKNOWN,
      orientation
    )
  }

  @Test
  fun assert_initial_is_locked_matches_false_at_startup() {
    val isLocked = mModule.getIsLocked()

    assertEquals(
      "When user starts the app, interface orientation shouldn't be locked",
      false,
      isLocked
    )
  }
}
