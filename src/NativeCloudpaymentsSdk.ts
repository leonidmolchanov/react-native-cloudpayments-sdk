import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type { ICloudpaymentsSdkSpec } from './types';
// Интерфейс для React Native Codegen (должен называться именно "Spec")
export interface Spec extends TurboModule, ICloudpaymentsSdkSpec {}

export default TurboModuleRegistry.getEnforcing<Spec>('CloudpaymentsSdk');
