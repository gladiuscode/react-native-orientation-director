import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getInterfaceOrientation(): Promise<number>;
  getDeviceOrientation(): Promise<number>;
  lockTo(orientation: number): void;
  unlock(): void;
  isLocked(): boolean;
  resetSupportedInterfaceOrientations(): void;

  ////////////////////////////////////
  //
  // ANDROID ONLY
  //

  isAutoRotationEnabled(): boolean;
  enableOrientationSensors(): void;
  disableOrientationSensors(): void;

  //
  ////////////////////////////////////

  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('OrientationDirector');
