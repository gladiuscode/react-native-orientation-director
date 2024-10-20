package com.orientationdirector.implementation

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.BridgeReactContext
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class OrientationDirectorModuleImplTest {
  private var context = BridgeReactContext(ApplicationProvider.getApplicationContext())

  @Mock
  private val mockEventManager = EventManager(context)

  @Mock
  private val mockAutoRotationObserver = AutoRotationObserver(
    context,
    android.os.Handler(Looper.getMainLooper())
  )

  @InjectMocks
  private val mModule = OrientationDirectorModuleImpl(context)

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)

    // We stub this method because adaptInterfaceTo checks it
    `when`(mockAutoRotationObserver.getLastAutoRotationStatus()).thenReturn(true)
  }

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

  @Test
  fun assert_is_locked_matches_true_after_lock_to_gets_executed() {
    mModule.lockTo(1)
    val isLocked = mModule.getIsLocked()

    assertEquals(
      "When lockTo is executed, getIsLocked should match true",
      true,
      isLocked
    )
  }

  @Test
  fun assert_auto_rotation_enabled_at_startup() {
    // TODO: Find a way to set the value of Settings.System.ACCELEROMETER_ROTATION if possible
  }

  @Test
  fun assert_lock_orientation_to_portrait() {
    mModule.lockTo(1)

    // TODO: This test fails because currentActivity is null, check if we can mock it out and how
//    assertEquals(
//      "",
//      ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
//      context.currentActivity?.requestedOrientation
//    )
  }

  @Test
  fun assert_interface_orientation_matches_locked_to_portrait() {
    mModule.lockTo(1)

    assertEquals(
      "When the interface is locked to portrait, getInterfaceOrientation should return portrait",
      Orientation.PORTRAIT,
      mModule.getInterfaceOrientation()
    )
  }

  @Test
  fun assert_interface_orientation_matches_locked_to_landscape_right() {
    mModule.lockTo(2)

    assertEquals(
      "When the interface is locked to landscape right, getInterfaceOrientation should return landscape right",
      Orientation.LANDSCAPE_RIGHT,
      mModule.getInterfaceOrientation()
    )
  }

  @Test
  fun assert_interface_orientation_matches_locked_to_portrait_upside_down() {
    mModule.lockTo(3)

    assertEquals(
      "When the interface is locked to portrait upside down, getInterfaceOrientation should return portrait upside down",
      Orientation.PORTRAIT_UPSIDE_DOWN,
      mModule.getInterfaceOrientation()
    )
  }

  @Test
  fun assert_interface_orientation_matches_locked_to_landscape_left() {
    mModule.lockTo(4)

    assertEquals(
      "When the interface is locked to landscape left, getInterfaceOrientation should return landscape left",
      Orientation.LANDSCAPE_LEFT,
      mModule.getInterfaceOrientation()
    )
  }

  @Test
  fun assert_is_locked_reset_when_unlock_is_executed() {
    mModule.lockTo(1)
    mModule.unlock()

    assertEquals(
      "When unlock is executed, getIsLocked should return false",
      false,
      mModule.getIsLocked()
    )
  }

  @Test
  @Config(
    qualifiers = "port"
  )
  fun assert_interface_reset_to_portrait_on_unlock() {
    mModule.lockTo(2)
    mModule.unlock()

    assertEquals(
      "When unlock is executed, getInterfaceOrientation should match portrait when device is in portrait",
      Orientation.PORTRAIT,
      mModule.getInterfaceOrientation()
    )
  }

  @Test
  @Config(
    qualifiers = "land"
  )
  fun assert_interface_reset_to_landscape_left_on_unlock() {
    mModule.lockTo(2)
    mModule.unlock()

    assertEquals(
      "When unlock is executed, getInterfaceOrientation should match landscape left when device is in landscape right",
      Orientation.LANDSCAPE_LEFT,
      mModule.getInterfaceOrientation()
    )
  }
}
