export interface DisplayChangedEvent {
  /**
   * Width of the new display, in points (iOS) / dp (Android),
   * in the orientation it is currently displayed.
   */
  width: number;
  /**
   * Height of the new display, in points (iOS) / dp (Android),
   * in the orientation it is currently displayed.
   */
  height: number;
}
