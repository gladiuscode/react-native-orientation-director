import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getInterfaceOrientation(): Promise<number>;
  getDeviceOrientation(): Promise<number>;
  lockTo(orientation: number): void;
  unlock(): void;
  isLocked(): boolean;
  isAutoRotationEnabled(): boolean;
  resetSupportedInterfaceOrientations(): void;

  addListener: (eventType: string) => void;
  removeListeners: (count: number) => void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('OrientationDirector');
