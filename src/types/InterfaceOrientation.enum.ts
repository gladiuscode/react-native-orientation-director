export enum InterfaceOrientation {
  unknown = 0,
  portrait = 1,

  /*
    Note: On IOS, the portraitUpsideDown is not supported by all iPhone devices
    that haven't got a home button.
   */
  portraitUpsideDown = 2,

  landscapeLeft = 3,
  landscapeRight = 4,
}

export default InterfaceOrientation;
