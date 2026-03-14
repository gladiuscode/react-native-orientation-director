import { type CodegenTypes, type TurboModule } from 'react-native';
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

  readonly onDeviceOrientationChanged: CodegenTypes.EventEmitter<{
    orientation: number;
  }>;
  readonly onInterfaceOrientationChanged: CodegenTypes.EventEmitter<{
    orientation: number;
  }>;
  readonly onLockChanged: CodegenTypes.EventEmitter<{ locked: boolean }>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('OrientationDirector');
