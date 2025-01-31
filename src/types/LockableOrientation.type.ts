import { Orientation } from './Orientation.enum';

export type LockableOrientation =
  | Orientation.portrait
  | Orientation.portraitUpsideDown
  | Orientation.landscapeLeft
  | Orientation.landscapeRight
  | Orientation.landscape;
