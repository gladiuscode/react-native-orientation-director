import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface Spec extends TurboModule {
  getInterfaceOrientation(): Promise<number>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('OrientationHandler');
